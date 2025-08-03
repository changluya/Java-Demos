package com.changlu.springboot.milvus.util;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.openai.OpenAiTokenizer;

import java.util.List;


public class Langchain4jUtil {

    // 目标段落长度（汉字字符数）
    public static final int TARGET_LENGTH = 1000;
    // 允许的段落长度浮动范围（±20字）
    public static final int LENGTH_TOLERANCE = 150;

    /**
     * 使用langchain4j的分段工具
     *
     * @param content 输入的大文本
     * @return 段落列表，每个段落至少包含minLength个字符
     */
    public static List<String> splitParagraphsLangChain(String content) {
        DocumentSplitter splitter = DocumentSplitters.recursive(TARGET_LENGTH, LENGTH_TOLERANCE, new OpenAiTokenizer());
        List<TextSegment> split = splitter.split(Document.document(content));
        return split.stream().map(TextSegment::text).toList();
    }

}
