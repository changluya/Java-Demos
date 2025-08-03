package com.changlu.ai.langchain4j.rag.documents;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DocumentParserTest {

    /**
     * 解析PDF
     */
    @Test
    public void testParsePDF() {
        Document document = FileSystemDocumentLoader.loadDocument(
                "/Users/edy/changlu_workspace/mymd/demo-exer/ai/LangChain4j-demo/src/main/resources/documents/尚硅谷-Java+大模型应用-硅谷小智（医疗版）.pdf",
                new ApachePdfBoxDocumentParser()
        );
        System.out.println(document);
    }

}
