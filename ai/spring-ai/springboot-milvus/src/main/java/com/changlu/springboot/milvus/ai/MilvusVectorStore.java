package com.changlu.springboot.milvus.ai;

import com.alibaba.fastjson.JSON;
import com.changlu.springboot.milvus.service.MilvusService;
import com.drew.lang.annotations.NotNull;
import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class MilvusVectorStore implements VectorStore {

    @Autowired
    private MilvusService milvusService;

    @Autowired
    private OllamaEmbeddingModel embeddingModel;
//    @Autowired
//    private DashScopeEmbeddingModel embeddingModel;

    @Override
    public void add(List<Document> documents) {
        if (!documents.isEmpty()) {
            for (Document document : documents) {
                milvusService.insert(embeddingModel.embed(document), document.getText(), JSON.toJSONString(document.getMetadata()), null);
            }
        }
    }

    @Override
    public void delete(List<String> idList) {
        if (!idList.isEmpty()) {
            // idList转换为id数组
            String[] ids = idList.toArray(new String[0]);
            milvusService.delete(ids);
        }
    }

    @Override
    public void delete(Filter.Expression filterExpression) {
        milvusService.delete(filterExpression.toString());
    }


    @Override
    public List<Document> similaritySearch(@NotNull SearchRequest request) {
        return milvusService.search(request);
    }
}