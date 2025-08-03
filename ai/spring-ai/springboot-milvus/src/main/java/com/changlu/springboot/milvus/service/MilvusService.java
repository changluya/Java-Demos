package com.changlu.springboot.milvus.service;

import io.milvus.v2.service.vector.response.InsertResp;
import io.milvus.v2.service.vector.response.SearchResp;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;

import java.util.List;

/**
 * @author changlu
 * @des Milvus服务接口
 * @date 2025/2/25 下午3:09
 */
public interface MilvusService {
    /**
     * 检查集合是否存在,不存在则创建集合
     */
    Boolean hasCollection();

    /**
     * 插入数据
     *
     * @param vectorParam 向量参数
     * @param text        文本
     * @param metadata    元数据
     * @param fileName    文件名
     */
    InsertResp insert(float[] vectorParam, String text, String metadata, String fileName);

    /**
     * 批量插入数据
     *
     * @param vectorParam 向量参数
     * @param text        文本
     * @param metadata    元数据
     * @param fileName    文件名
     */
    InsertResp batchInsert(List<float[]> vectorParam, List<String> text, List<String> metadata, List<String> fileName);

    /**
     * 搜索数据
     *
     * @param vectorParam 向量参数
     */
    SearchResp search(float[] vectorParam, Integer searchCount);

    /**
     * 搜索数据
     *
     * @param request 搜索请求
     */
    List<Document> search(SearchRequest request);

    /**
     * 删除数据
     *
     * @param ids id
     */
    void delete(String... ids);

    void delete(String filter);

}