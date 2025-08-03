package com.changlu.ai.rag.example.milvus.config;

import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.DataType;
import io.milvus.param.ConnectParam;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import io.milvus.param.collection.CreateCollectionParam;
import io.milvus.param.collection.DropCollectionParam;
import io.milvus.param.collection.FieldType;
import io.milvus.param.collection.HasCollectionParam;
import io.milvus.param.index.CreateIndexParam;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.TokenCountBatchingStrategy;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.milvus.MilvusSearchRequest;
import org.springframework.ai.vectorstore.milvus.MilvusVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ManualVectorStoreConfig {


    // 1. 手动创建 Ollama 嵌入模型 (bge-m3)
//    @Bean
//    public EmbeddingModel ollamaEmbeddingModel() {
//        OllamaApi ollamaApi = OllamaApi.builder().baseUrl("http://localhost:11434").build();
//        OllamaEmbeddingModel ollamaEmbeddingModel = OllamaEmbeddingModel.builder()
//                .ollamaApi(ollamaApi)
//                .defaultOptions(OllamaOptions.builder().model("bge-m3:latest").build())
//                .build();
//        return ollamaEmbeddingModel;
//    }

    // 2. 手动创建 Milvus 向量存储
    @Bean(name = "diymilvusVectorStore")
    public MilvusVectorStore milvusVectorStore(EmbeddingModel embeddingModel) throws InterruptedException {
        // Milvus 连接参数
        MilvusServiceClient milvusClient = new MilvusServiceClient(
            ConnectParam.newBuilder()
                .withHost("localhost")
                .withPort(19530)
                .withAuthorization("root", "123456") // 用户名密码
                .withDatabaseName("default")          // 数据库名
                .build()
        );

        // 集合参数
        String collectionName = "manual_demo";
        int embeddingDimension = 1024; // bge-m3 的维度

        // 如果集合已存在则删除
        HasCollectionParam hasCollectionParam = HasCollectionParam.newBuilder().withCollectionName(collectionName).build();
        if (milvusClient.hasCollection(hasCollectionParam).getData()) {
            DropCollectionParam dropCollectionParam = DropCollectionParam.newBuilder().withCollectionName(collectionName).build();
            milvusClient.dropCollection(dropCollectionParam);

            // 等待删除完成
            Thread.sleep(1000);
        }

        // 创建新集合
        milvusClient.createCollection(
            CreateCollectionParam.newBuilder()
                .withCollectionName(collectionName)
//                .addFieldType(FieldType.newBuilder()
//                    .withName("id")
//                    .withDataType(DataType.Int64)
//                    .withPrimaryKey(true)
//                    .withAutoID(true)
//                    .build())
                .addFieldType(FieldType.newBuilder()
                    .withName("content")
                    .withDataType(DataType.VarChar)
                    .withMaxLength(512)
                    .build())
                .addFieldType(FieldType.newBuilder()
                    .withName("embedding")
                    .withDataType(DataType.FloatVector)
                    .withDimension(embeddingDimension)
                    .build())
                .build()
        );

        // 创建索引
        milvusClient.createIndex(
            CreateIndexParam.newBuilder()
                .withCollectionName(collectionName)
                .withFieldName("embedding")
                .withIndexType(IndexType.IVF_FLAT)
                .withMetricType(MetricType.COSINE)
                .build()
        );

        // 构造一个MilvusVectorStore
        return MilvusVectorStore.builder(milvusClient, embeddingModel)
                .collectionName(collectionName)
                .contentFieldName("content")
                .embeddingDimension(embeddingDimension)
                .batchingStrategy(new TokenCountBatchingStrategy())
                .initializeSchema(true)
                .build();
    }

    // 3. 执行上传和查询
    @Bean
    public CommandLineRunner demoRunner(@Qualifier("diymilvusVectorStore") MilvusVectorStore vectorStore) {
        return args -> {
            // 上传文档
            //		List<Document> documents = List.of(

//		);
            List<Document> documents = List.of(
                new Document("SpringAIAlibaba 1.0.0.2 是阿里巴巴开源的 Spring AI 集成框架"),
                new Document("Ollama 的 bge-m3 模型支持 1024 维向量生成"),
                new Document("Milvus 2.3.x 兼容 Spring AI 1.0.0 的向量存储接口"),
                new Document("1. 使用SpringAIAlibaba创建一个Spring Boot项目，并添加spring-ai-alibaba-starter-dashscope依赖。"),
                new Document("2. 在SpringAIAlibaba项目的pom.xml中添加Spring Milestone和Snapshot存储库。"),
                new Document("3. 通过SpringAIAlibaba申请阿里云通义API Key，在application.yml中进行配置。"),
                new Document("4. 使用SpringAIAlibaba的ChatClient和Prompt功能实现对话模型。"),
                new Document("5. 通过SpringAIAlibaba的Spring Boot与Spring Cloud Alibaba AI对接，实现基本聊天功能。"),
                new Document("6. SpringAIAlibaba支持文本生成、翻译、摘要等生成式AI功能。"),
                new Document("7. SpringAIAlibaba支持文本数据的语义搜索和AI绘画功能。"),
                new Document("8. 使用SpringAIAlibaba的TongYiChatModel和TongYiImagesModel实现聊天和图片服务。"),
                new Document("9. 在SpringAIAlibaba的REST控制器中提供对外的API接口。"),
                new Document("10. 通过SpringAIAlibaba的简单API调用实现AI模型的集成。"),
                new Document("11. 使用SpringAIAlibaba的Prompt模板管理控制AI模型的输出。"),
                new Document("12. 结合SpringAIAlibaba的检索和生成技术（RAG）提高生成内容的质量。"),
                new Document("13. 使用SpringAIAlibaba实现文本生成图像和图像识别功能。"),
                new Document("14. 准备SpringAIAlibaba需要的Java 17及以上的开发环境。"),
                new Document("15. 使用IDEA进行SpringAIAlibaba的Java开发和HBuilder X进行前端开发。"),
                new Document("16. 在SpringAIAlibaba的Spring Boot项目中集成多种AI模型和向量数据库。"),
                new Document("17. SpringAIAlibaba支持自然语言处理、计算机视觉、语音处理和数据分析与预测功能。"),
                new Document("18. 通过SpringAIAlibaba的配置中心和注册中心实现动态扩展。")
            );
            vectorStore.add(documents);
            System.out.println("✅ 文档上传完成");

            // 查询相似文档
//            SearchRequest request = MilvusSearchRequest.builder().query("SpringAIAlibaba 版本").similarityThreshold(0.7).topK(1).build();
            SearchRequest request = MilvusSearchRequest.builder().query("SpringAIAlibaba需要java的开发环境版本是多少？").similarityThreshold(0.7).topK(3).build();

            List<Document> results = vectorStore.similaritySearch(request);
            System.out.println("\n🔍 相似性搜索结果:");
            results.forEach(doc -> 
                System.out.println(" - " + doc.getFormattedContent())
            );
        };
    }
}