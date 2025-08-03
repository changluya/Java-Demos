package com.changlu.springboot.milvus.controller;

import com.alibaba.fastjson.JSON;
import com.changlu.springboot.milvus.service.MilvusService;
import com.changlu.springboot.milvus.util.HanlpUtil;
import com.changlu.springboot.milvus.util.Langchain4jUtil;
import com.changlu.springboot.milvus.util.TikaUtil;
import com.changlu.springboot.milvus.vo.TikaVo;
import io.milvus.v2.service.vector.response.InsertResp;
import io.milvus.v2.service.vector.response.SearchResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * @description  向量数据库实践
 * @author changlu
 * @date 2025/6/6 22:05
 */
@RestController
@RequestMapping("/api/vector")
@Tag(name = "向量数据库实践", description = "向量数据库实践")
public class MilvusController {

    @Autowired
    private OllamaEmbeddingModel embeddingModel;
//    private DashScopeEmbeddingModel embeddingModel;

    @Autowired
    private MilvusService milvusService;
 
 
    @Operation(summary = "添加自定义rag数据")
    @GetMapping("/addCustomRagData")
    public ResponseEntity<InsertResp> addCustomRagData(@RequestParam String text) {
        Assert.notNull(text, "text不能为空");
        // 借助向量模型来完成文本的向量化
        float[] embed = embeddingModel.embed(text);
        // 插入数据
        InsertResp insert = milvusService.insert(embed, text, JSON.toJSONString(new HashMap<>().put("custom", text)), "custom");
        return ResponseEntity.ok(insert);
    }

    @Operation(summary = "搜索")
    @GetMapping("/search")
    public ResponseEntity<SearchResp> search(@RequestParam String text, @RequestParam Integer searchCount) {
        Assert.notNull(text, "text不能为空");
        float[] embed = embeddingModel.embed(text);
        // 搜索数据
        return ResponseEntity.ok(milvusService.search(embed, searchCount));
    }


    @Operation(summary = "解析文件内容（支持上传各类文件）")
    @PostMapping("/extractFileString")
    public ResponseEntity<String> extractFileString(MultipartFile file) {
        return ResponseEntity.ok(TikaUtil.extractTextString(file));
    }

    @Operation(summary = "解析文件内容-HanLP分片")
    @PostMapping("/splitParagraphsHanLP")
    public ResponseEntity<List<String>> splitParagraphsHanLP(MultipartFile file) {
        return ResponseEntity.ok(HanlpUtil.splitParagraphsHanLP(TikaUtil.extractTextString(file)));
    }

    @Operation(summary = "解析文件内容-LangChina分片")
    @PostMapping("/splitParagraphsLangChain")
    public ResponseEntity<List<String>> splitParagraphsLangChain(MultipartFile file) {
        return ResponseEntity.ok(Langchain4jUtil.splitParagraphsLangChain(TikaUtil.extractTextString(file)));
    }

    @Operation(summary = "上传知识库")
    @PostMapping("/uploadFile")
    public ResponseEntity<InsertResp> uploadFile(MultipartFile file) {
        // 获取文件内容
        TikaVo tikaVo = TikaUtil.extractText(file);
        if (tikaVo != null && Objects.nonNull(tikaVo.getText())) {
            List<String> textList = tikaVo.getText();
            List<String> metadataList = tikaVo.getMetadata();

            // 存储计算得到的向量结果 & 文件名
            List<float[]> embedList = new ArrayList<>();
            List<String> fileNameList = new ArrayList<>();
            for (String s : tikaVo.getText()) {
                embedList.add(embeddingModel.embed(s));
                fileNameList.add(file.getOriginalFilename());
            }
            return ResponseEntity.ok(milvusService.batchInsert(embedList, textList, metadataList, fileNameList));
        }
        return ResponseEntity.ok(null);
    }
    
}