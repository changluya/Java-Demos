package com.changlu.springboottest.controller;

import com.changlu.springboottest.pojo.TestUser;
import com.changlu.springboottest.pojo.User;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName TestController
 * @Author ChangLu
 * @Date 3/30/2022 3:34 PM
 * @Description TODO
 */
@RestController
public class TestController {

    @CrossOrigin
    @RequestMapping("/test")
    public Map<String, Object> test() {
        Map<String, Object> res = new HashMap();
        res.put("code", 200);
        res.put("data", new User("changlu", 19));
        return res;
    }

    @RequestMapping("/test108926")
    @ResponseBody
    public Map<String, Object> test108926(HttpServletRequest request) throws IOException {
        // 接收请求数据
        BufferedReader reader = request.getReader();
        char[] buf = new char[512];
        int len = 0;
        StringBuffer contentBuffer = new StringBuffer();
        while ((len = reader.read(buf)) != -1) {
            contentBuffer.append(buf, 0, len);
        }
        String content = contentBuffer.toString();
        System.out.println(content);
        Map<String, Object> res = new HashMap();
        res.put("code", 200);
        res.put("data", "success!");
        return res;
    }

}
