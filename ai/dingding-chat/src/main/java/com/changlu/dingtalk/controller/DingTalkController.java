package com.changlu.dingtalk.controller;

import com.alibaba.fastjson.JSON;
import com.changlu.dingtalk.model.DingTalkRequest;
import com.changlu.dingtalk.model.DingTalkResponse;
import com.changlu.dingtalk.service.DingTalkService;
import com.changlu.dingtalk.util.DingCallbackCrypto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/callback")
public class DingTalkController {

    @Value("${dingtalk.aes_key}")
    private String aesKey;

    @Value("${dingtalk.token}")
    private String token;

    @Value("${dingtalk.corp_id}")
    private String corpId;

    @Autowired
    private DingTalkService dingTalkService;

    // 钉钉回调验证接口
    @GetMapping
    public Map<String, String> callbackVerification(
            @RequestParam("msg_signature") String signature,
            @RequestParam("timestamp") String timestamp,
            @RequestParam("nonce") String nonce,
            @RequestParam("echostr") String echostr) {

        try {
            log.info("收到验证请求: signature={}, timestamp={}, nonce={}, echostr={}",
                    signature, timestamp, nonce, echostr);

            DingCallbackCrypto crypto = new DingCallbackCrypto(token, aesKey, corpId);
            String plainText = crypto.getDecryptMsg(signature, timestamp, nonce, echostr);

            log.info("验证成功，返回明文: {}", plainText);
            HashMap<String, String> res = new HashMap<>();
            res.put("echostr", plainText);
            return res;

        } catch (Exception e) {
            log.error("回调验证失败 - token:{}, aesKey:{}, corpId:{}", token, aesKey, corpId, e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "回调验证失败: " + e.getMessage());
        }
    }

    // 钉钉事件回调接口
    @PostMapping
    public Map<String, String> handleCallback(
            @RequestParam("msg_signature") String signature,
            @RequestParam("timestamp") String timestamp,
            @RequestParam("nonce") String nonce,
            @RequestBody Map<String, String> encryptedBody) {

        try {
            log.info("收到回调请求: signature={}, timestamp={}, nonce={}, body={}",
                    signature, timestamp, nonce, encryptedBody);

            DingCallbackCrypto crypto = new DingCallbackCrypto(token, aesKey, corpId);
            String encryptMsg = encryptedBody.get("encrypt");

            if (encryptMsg == null || encryptMsg.isEmpty()) {
                throw new IllegalArgumentException("加密消息体为空");
            }

            // 1. 解密消息
            String plainText = crypto.getDecryptMsg(signature, timestamp, nonce, encryptMsg);
            log.info("解密后的消息: {}", plainText);

            // 2. 处理测试回调事件
            HashMap<String, String> res = new HashMap<>();
            if (plainText.contains("\"EventType\":\"check_url\"")) {
                log.info("处理测试回调事件");
                String randomStr = DingCallbackCrypto.Utils.getRandomStr(16);
                String encryptResponse = crypto.encrypt(randomStr, "success");

                res.put("msg_signature", crypto.getSignature(token, timestamp, nonce, encryptResponse));
                res.put("encrypt", encryptResponse);
                res.put("timeStamp", timestamp);
                res.put("nonce", nonce);
                return res;
            }

            // 3. 处理普通消息
            DingTalkRequest request = JSON.parseObject(plainText, DingTalkRequest.class);
            String content = request.getText().getContent()
                    .replaceAll("@机器人名字", "").trim();
            log.info("处理用户消息: {}", content);

            // 4. 调用AI服务
            String responseContent = dingTalkService.processMessage(content, request.getSenderId());

            // 5. 构造并加密响应
            DingTalkResponse response = new DingTalkResponse(
                    "text",
                    new DingTalkResponse.Text(responseContent),
                    new DingTalkResponse.At(new String[]{request.getSenderId()})
            );

            String randomStr = DingCallbackCrypto.Utils.getRandomStr(16);
            String encryptedResponse = crypto.encrypt(randomStr, JSON.toJSONString(response));

            // 6. 返回加密响应
            res.put("msg_signature", crypto.getSignature(token, timestamp, nonce, encryptedResponse));
            res.put("encrypt", encryptedResponse);
            res.put("timeStamp", timestamp);
            res.put("nonce", nonce);
            return res;

        } catch (Exception e) {
            log.error("处理回调异常 - token:{}, aesKey:{}, corpId:{}", token, aesKey, corpId, e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "处理回调异常: " + e.getMessage()
            );
        }
    }
}