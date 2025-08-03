package com.changlu.ai.langchain4j.rag.documents;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DocumentLoaderTest {

    @Test
    public void testReadDocument() {
        //  使用FileSystemDocumentLoader读取指定目录下的知识库文档
        //  并使用默认的文档解析器TextDocumentParser对文档进行解析
        Document document = FileSystemDocumentLoader.loadDocument("/Users/edy/changlu_workspace/mymd/demo-exer/ai/LangChain4j-demo/src/main/resources/documents/测试.txt");
        System.out.println(document.text());
    }

}
