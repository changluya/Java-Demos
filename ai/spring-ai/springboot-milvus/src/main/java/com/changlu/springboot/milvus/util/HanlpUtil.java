package com.changlu.springboot.milvus.util;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;

import java.util.ArrayList;
import java.util.List;

/**
 * @description  HanLP分词库工具类
 * @author changlu
 * @date 2025/6/7 16:23
 */
public class HanlpUtil {

    // 目标段落长度（汉字字符数）
    private static final int TARGET_LENGTH = 500;
    // 允许的段落长度浮动范围（±20字）
    private static final int LENGTH_TOLERANCE = 150;

    /**
     * 使用HanLP进行句子分割
     *
     * @param text 输入的大文本
     * @return 段落列表，每个段落至少包含minLength个字符
     */
    public static List<String> splitParagraphsHanLP(String text) {
        List<String> paragraphs = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return paragraphs;
        }

        // 1. 使用 HanLP 分词并分句
        List<String> sentences = splitSentences(text);

        // 2. 动态合并句子到段落
        paragraphs = mergeSentencesIntoParagraphs(sentences);

        return paragraphs;
    }

    // 使用 HanLP 分词实现分句
    private static List<String> splitSentences(String text) {
        List<String> sentences = new ArrayList<>();
        StringBuilder currentSentence = new StringBuilder();
        List<Term> terms = HanLP.segment(text);

        for (Term term : terms) {
            currentSentence.append(term.word);
            // 使用正则表达式匹配句子结束标点（支持中英文标点）
            if (term.word.matches("[。！？；.!?;]+")) {
                sentences.add(currentSentence.toString());
                currentSentence.setLength(0);
            }
        }

        // 添加最后一个句子（如果没有标点结尾）
        if (!currentSentence.isEmpty()) {
            sentences.add(currentSentence.toString());
        }

        return sentences;
    }

    // 动态合并句子到段落
    private static List<String> mergeSentencesIntoParagraphs(List<String> sentences) {
        List<String> paragraphs = new ArrayList<>();
        StringBuilder currentParagraph = new StringBuilder();
        int currentLength = 0;

        for (String sentence : sentences) {
            int sentenceLength = countChineseChars(sentence);

            // 处理超长句子（强制分割）
            if (sentenceLength > TARGET_LENGTH + LENGTH_TOLERANCE) {
                if (currentLength > 0) {
                    paragraphs.add(currentParagraph.toString());
                    currentParagraph.setLength(0);
                    currentLength = 0;
                }
                // 按标点二次分割超长句
                List<String> subSentences = splitLongSentence(sentence);
                paragraphs.addAll(subSentences);
                continue;
            }

            // 合并到当前段落的条件
            if (currentLength + sentenceLength <= TARGET_LENGTH + LENGTH_TOLERANCE) {
                currentParagraph.append(sentence);
                currentLength += sentenceLength;
            } else {
                // 当前段落达到长度，保存并重置
                paragraphs.add(currentParagraph.toString());
                currentParagraph.setLength(0);
                currentParagraph.append(sentence);
                currentLength = sentenceLength;
            }
        }

        // 添加最后一个段落
        if (currentLength > 0) {
            paragraphs.add(currentParagraph.toString());
        }

        return paragraphs;
    }

    // 处理超长句子：按逗号、分号等二次分割
    private static List<String> splitLongSentence(String sentence) {
        List<String> validParts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int currentLength = 0;

        // 按标点分割句子
        String[] parts = sentence.split("[,;；，]");
        for (String part : parts) {
            int partLength = countChineseChars(part);
            if (currentLength + partLength > TARGET_LENGTH + LENGTH_TOLERANCE) {
                // 当前部分过长，保存并重置
                validParts.add(current.toString());
                current.setLength(0);
                currentLength = 0;
            }
            // 补回分割符号
            current.append(part).append("，");
            currentLength += partLength;
        }

        // 添加最后一个部分
        if (!current.isEmpty()) {
            validParts.add(current.toString());
        }

        return validParts;
    }

    // 统计中文字符数（忽略标点、英文）
    private static int countChineseChars(String text) {
        return (int) text.chars()
                .filter(c -> Character.UnicodeScript.of(c) == Character.UnicodeScript.HAN)
                .count();
    }

}
