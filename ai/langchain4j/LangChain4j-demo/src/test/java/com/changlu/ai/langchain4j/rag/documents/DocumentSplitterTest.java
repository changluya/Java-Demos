package com.changlu.ai.langchain4j.rag.documents;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.onnx.HuggingFaceTokenizer;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DocumentSplitterTest {

    /**
     * 加载文档并存入向量数据库
     */
    @Test
    public void testReadDocumentAndStore() {
        //使用FileSystemDocumentLoader读取指定目录下的知识库文档
        //并使用默认的文档解析器对文档进行解析(TextDocumentParser)
        Document document = FileSystemDocumentLoader.loadDocument("/Users/edy/changlu_workspace/mymd/demo-exer/ai/LangChain4j-demo/src/main/resources/documents/人工智能.md");
        //为了简单起见，我们暂时使用基于内存的向量存储
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        //ingest
        //1、分割文档：默认使用递归分割器，将文档分割为多个文本片段，每个片段包含不超过 300个token，并且有 30个token的重叠部分保证连贯性
        //DocumentByParagraphSplitter(DocumentByLineSplitter(DocumentBySentenceSplitter(DocumentByWordSplitter)))
        //2、文本向量化：使用一个LangChain4j内置的轻量化向量模型对每个文本片段进行向量化
        //3、将原始文本和向量存储到向量数据库中(InMemoryEmbeddingStore)
        EmbeddingStoreIngestor.ingest(document, embeddingStore);
        //查看向量数据库内容
        System.out.println(embeddingStore);
    }

    /**
     * 文档分割
     */
    @Test
    public void testDocumentSplitter() {
        //使用FileSystemDocumentLoader读取指定目录下的知识库文档
        //并使用默认的文档解析器对文档进行解析(TextDocumentParser)
        Document document = FileSystemDocumentLoader.loadDocument("/Users/edy/changlu_workspace/mymd/demo-exer/ai/LangChain4j-demo/src/main/resources/documents/人工智能.md");
        //为了简单起见，我们暂时使用基于内存的向量存储
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        //自定义文档分割器
        //按段落分割文档：每个片段包含不超过 300个token，并且有 30个token的重叠部分保证连贯性
        //注意：当段落长度总和小于设定的最大长度时，就不会有重叠的必要。
        DocumentByParagraphSplitter documentSplitter = new DocumentByParagraphSplitter(
                300,
                30,
                //token分词器：按token计算
                new HuggingFaceTokenizer());
        //按字符计算
        //DocumentByParagraphSplitter documentSplitter = new DocumentByParagraphSplitter(300, 30);
        EmbeddingStoreIngestor
                .builder()
                .embeddingStore(embeddingStore)
                .documentSplitter(documentSplitter)
                .build()
                .ingest(document);
    }
}
