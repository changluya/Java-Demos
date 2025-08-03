package com.changlu.springboot.milvus.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.changlu.springboot.milvus.constant.MilvusArchiveConstant;
import com.changlu.springboot.milvus.service.MilvusService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.milvus.v2.client.MilvusClientV2;
import io.milvus.v2.common.DataType;
import io.milvus.v2.common.IndexParam;
import io.milvus.v2.service.collection.request.*;
import io.milvus.v2.service.index.request.CreateIndexReq;
import io.milvus.v2.service.vector.request.DeleteReq;
import io.milvus.v2.service.vector.request.InsertReq;
import io.milvus.v2.service.vector.request.SearchReq;
import io.milvus.v2.service.vector.request.data.FloatVec;
import io.milvus.v2.service.vector.response.InsertResp;
import io.milvus.v2.service.vector.response.SearchResp;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentMetadata;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @description  MilvusServiceImpl 向量业务实现类
 * @author changlu
 * @date 2025/6/7 14:45
 */
@Service
public class MilvusServiceImpl implements MilvusService {

    @Autowired
    private MilvusClientV2 milvusClientV2;

    @Autowired
    private OllamaEmbeddingModel embeddingModel;
//    private DashScopeEmbeddingModel embeddingModel;
 
    /**
     * 检查集合是否存在
     */
    @Override
    public Boolean hasCollection() {
        Boolean b = milvusClientV2.hasCollection(HasCollectionReq.builder().collectionName(MilvusArchiveConstant.COLLECTION_NAME).build());
        if (!b) {
            this.createCollection();
        }
        return b;
    }

    /**
     * 插入数据
     *  vectorParam：向量特征
     *  text：文本内容
     *  metadata：原始文本元数据
     */
    @Override
    public InsertResp insert(float[] vectorParam, String text, String metadata, String fileName) {
        // 校验集合是否存在
        this.hasCollection();
        JsonObject jsonObject = new JsonObject();
        // 数组转换成JsonElement
        jsonObject.add(MilvusArchiveConstant.Field.FEATURE, new Gson().toJsonTree(vectorParam));
        jsonObject.add(MilvusArchiveConstant.Field.TEXT, new Gson().toJsonTree(text));
        jsonObject.add(MilvusArchiveConstant.Field.METADATA, new Gson().toJsonTree(metadata));
        jsonObject.add(MilvusArchiveConstant.Field.FILE_NAME, new Gson().toJsonTree(fileName));
        InsertReq insertReq = InsertReq.builder()
                // 集合名称
                .collectionName(MilvusArchiveConstant.COLLECTION_NAME)
                .data(Collections.singletonList(jsonObject))
                .build();

        return milvusClientV2.insert(insertReq);
    }
 
    /**
     * 创建集合
     */
    public void createCollection() {
        // 创建字段
        CreateCollectionReq.CollectionSchema schema = milvusClientV2.createSchema()
                // 创建主键字段
                .addField(AddFieldReq.builder()
                        // 字段名
                        .fieldName(MilvusArchiveConstant.Field.ID)
                        // 字段描述
                        .description("主键ID")
                        // 字段类型
                        .dataType(DataType.Int64)
                        // 是否为主键
                        .isPrimaryKey(true)
                        // 设置主键自增
                        .autoID(true)
                        .build())
                .addField(AddFieldReq.builder()
                        // 字段名
                        .fieldName(MilvusArchiveConstant.Field.FILE_NAME)
                        // 字段描述
                        .description("文件名")
                        // 字段类型
                        .dataType(DataType.VarChar)
                        // 设置字段为可空
                        .isNullable(true)
                        .build())
                // 创建特征向量字段
                .addField(AddFieldReq.builder()
                        // 字段名
                        .fieldName(MilvusArchiveConstant.Field.FEATURE)
                        // 字段描述
                        .description("特征向量")
                        // 字段类型
                        .dataType(DataType.FloatVector)
                        // 设置向量维度
                        .dimension(MilvusArchiveConstant.FEATURE_DIM)
                        .build())
                .addField(AddFieldReq.builder()
                        // 字段名
                        .fieldName(MilvusArchiveConstant.Field.TEXT)
                        // 字段描述
                        .description("文本")
                        // 字段类型
                        .dataType(DataType.VarChar)
                        // 设置字段为可空
                        .isNullable(true)
                        .build())
                .addField(AddFieldReq.builder()
                        // 字段名
                        .fieldName(MilvusArchiveConstant.Field.METADATA)
                        // 字段描述
                        .description("元数据")
                        // 字段类型
                        .dataType(DataType.VarChar)
                        // 设置字段为可空
                        .isNullable(true)
                        .build());
        // 创建集合
        CreateCollectionReq collectionReq = CreateCollectionReq.builder()
                // 集合名称
                .collectionName(MilvusArchiveConstant.COLLECTION_NAME)
                // 集合描述
                .description("自定义知识库")
                // 集合字段
                .collectionSchema(schema)
                // 分片数量
                .numShards(MilvusArchiveConstant.SHARDS_NUM)
                .build();
        milvusClientV2.createCollection(collectionReq);
 
        // 创建索引，针对向量来实现索引
        IndexParam indexParam = IndexParam.builder()
                // 索引字段名
                .fieldName(MilvusArchiveConstant.Field.FEATURE)
                // 索引类型
                .indexType(IndexParam.IndexType.IVF_FLAT)
                // 索引距离度量
                .metricType(IndexParam.MetricType.COSINE)
                .build();
        CreateIndexReq createIndexReq = CreateIndexReq.builder()
                .collectionName(MilvusArchiveConstant.COLLECTION_NAME)
                .indexParams(Collections.singletonList(indexParam))
                .build();
 
        milvusClientV2.createIndex(createIndexReq);
    }

    /**
     * 搜索数据
     *
     * @param vectorParam 向量参数
     */
    @Override
    public SearchResp search(float[] vectorParam, Integer searchCount) {
        this.loadCollection();
        FloatVec floatVec = new FloatVec(vectorParam);
        SearchReq searchReq = SearchReq.builder()
                // 集合名称
                .collectionName(MilvusArchiveConstant.COLLECTION_NAME)
                // 搜索距离度量
                .metricType(IndexParam.MetricType.COSINE)
                // 搜索向量
                .data(Collections.singletonList(floatVec))
                // 搜索字段
                .annsField(MilvusArchiveConstant.Field.FEATURE)
                // 返回字段
                .outputFields(Arrays.asList(MilvusArchiveConstant.Field.ID, MilvusArchiveConstant.Field.TEXT, MilvusArchiveConstant.Field.METADATA, MilvusArchiveConstant.Field.FILE_NAME))
                // 搜索数量
                .topK(searchCount)
                .build();
        return milvusClientV2.search(searchReq);
    }

    @Override
    public List<Document> search(SearchRequest request) {
        String query = request.getQuery();
        float[] embed = embeddingModel.embed(query);
        List<List<SearchResp.SearchResult>> searchResults = this.search(embed, 3).getSearchResults();
        if (!searchResults.isEmpty()) {
            return searchResults.get(0).stream()
                    .filter(searchResult -> searchResult.getScore() >= request.getSimilarityThreshold()) // 过滤获取到指定阈值之上的文本片段
                    .map(searchResult -> {
                String docId = searchResult.getId().toString();
                String content = searchResult.getEntity().get(MilvusArchiveConstant.Field.TEXT).toString();
                JSONObject metadata = JSON.parseObject(searchResult.getEntity().get(MilvusArchiveConstant.Field.METADATA).toString());
                metadata.put(DocumentMetadata.DISTANCE.value(), 1.0F - searchResult.getScore());
                return Document.builder()
                        .id(docId)
                        .text(content)
                        .metadata(metadata == null ? null : metadata.getInnerMap())
                        .score((double) searchResult.getScore())
                        .build();
            }).toList();
        }
        return List.of();
    }

    @Override
    public void delete(String... ids) {
        milvusClientV2.delete(DeleteReq.builder().collectionName(MilvusArchiveConstant.COLLECTION_NAME).ids(Arrays.asList(ids)).build());
    }

    @Override
    public void delete(String filter) {
        milvusClientV2.delete(DeleteReq.builder().collectionName(MilvusArchiveConstant.COLLECTION_NAME).filter(filter).build());
    }

    /**
     * 将集合加载到内存中以提高搜索性能
     */
    private void loadCollection() {
        // 首先检查集合是否存在
        if (this.hasCollection()) {
            try {
                milvusClientV2.loadCollection(
                        LoadCollectionReq.builder()
                                .collectionName(MilvusArchiveConstant.COLLECTION_NAME)
                                .build()
                );
            } catch (Exception e) {
                // 处理加载失败的情况
                throw new RuntimeException("加载集合到内存失败: " + e.getMessage(), e);
            }
        } else {
            throw new RuntimeException("集合不存在: " + MilvusArchiveConstant.COLLECTION_NAME);
        }
    }

    /**
     * 批量插入数据
     *
     * @param vectorParam 向量参数
     * @param text        文本
     * @param metadata    元数据
     * @param fileName    文件名
     */
    @Override
    public InsertResp batchInsert(List<float[]> vectorParam, List<String> text, List<String> metadata, List<String> fileName) {
        if (vectorParam.size() == text.size() && vectorParam.size() == metadata.size() && vectorParam.size() == fileName.size()) {
            List<JsonObject> jsonObjects = new ArrayList<>();
            for (int i = 0; i < vectorParam.size(); i++) {
                JsonObject jsonObject = new JsonObject();
                // 数组转换成JsonElement
                jsonObject.add(MilvusArchiveConstant.Field.FEATURE, new Gson().toJsonTree(vectorParam.get(i)));
                jsonObject.add(MilvusArchiveConstant.Field.TEXT, new Gson().toJsonTree(text.get(i)));
                jsonObject.add(MilvusArchiveConstant.Field.METADATA, new Gson().toJsonTree(metadata.get(i)));
                jsonObject.add(MilvusArchiveConstant.Field.FILE_NAME, new Gson().toJsonTree(fileName.get(i)));
                jsonObjects.add(jsonObject);
            }
            InsertReq insertReq = InsertReq.builder()
                    // 集合名称
                    .collectionName(MilvusArchiveConstant.COLLECTION_NAME)
                    .data(jsonObjects)
                    .build();
            return milvusClientV2.insert(insertReq);
        }
        return null;
    }

    /**
     * 从内存中释放集合
     */
    public void releaseCollection() {
        milvusClientV2.releaseCollection(
                ReleaseCollectionReq.builder()
                        .collectionName(MilvusArchiveConstant.COLLECTION_NAME)
                        .build()
        );
    }

}