package com.changlu.springboot.milvus.util;

import com.alibaba.fastjson.JSON;
import com.changlu.springboot.milvus.vo.TikaVo;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TikaUtil {

    /**
     * 解析文件（各类格式）为文本
     * @param file MultipartFile
     * @return String
     */
    public static String extractTextString(MultipartFile file) {
        try {
            // 创建解析器--在不确定文档类型时候可以选择使用AutoDetectParser可以自动检测一个最合适的解析器
            Parser parser = new AutoDetectParser();
            // 用于捕获文档提取的文本内容。-1 参数表示使用无限缓冲区,解析到的内容通过此hander获取
            BodyContentHandler bodyContentHandler = new BodyContentHandler(-1);
            // 元数据对象，它在解析器中传递元数据属性---可以获取文档属性
            Metadata metadata = new Metadata();
            // 带有上下文相关信息的ParseContext实例，用于自定义解析过程。
            ParseContext parseContext = new ParseContext();
            parser.parse(file.getInputStream(), bodyContentHandler, metadata, parseContext);
            // 获取文本
            return bodyContentHandler.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 文件内容提取
     *
     * @param file 上传的文件
     * @return 文件内容
     */
    public static TikaVo extractText(MultipartFile file) {
        try {
            // 创建解析器--在不确定文档类型时候可以选择使用AutoDetectParser可以自动检测一个最合适的解析器
            Parser parser = new AutoDetectParser();
            // 用于捕获文档提取的文本内容。-1 参数表示使用无限缓冲区,解析到的内容通过此hander获取
            BodyContentHandler bodyContentHandler = new BodyContentHandler(-1);
            // 元数据对象，它在解析器中传递元数据属性---可以获取文档属性
            Metadata metadata = new Metadata();
            // 带有上下文相关信息的ParseContext实例，用于自定义解析过程。
            ParseContext parseContext = new ParseContext();
            parser.parse(file.getInputStream(), bodyContentHandler, metadata, parseContext);
            // 获取文本
            String text = bodyContentHandler.toString();
            // 元数据信息
            String[] names = metadata.names();
            // 将元数据转换成JSON字符串
            Map<String, String> map = new HashMap<>();
            for (String name : names) {
                map.put(name, metadata.get(name));
            }
            return splitParagraphs(text);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 使用langchain4j的分段工具
     *
     * @param content 文本内容
     */
    private static TikaVo splitParagraphs(String content) {
        DocumentSplitter splitter = DocumentSplitters.recursive(Langchain4jUtil.TARGET_LENGTH, Langchain4jUtil.LENGTH_TOLERANCE, new OpenAiTokenizer());
        List<TextSegment> split = splitter.split(Document.document(content));
        return new TikaVo()
                .setText(
                        split.stream()
                                .map(TextSegment::text)
                                .toList()
                ).setMetadata(
                        split.stream()
                                .map(textSegment -> JSON.toJSONString(textSegment.metadata()))
                                .toList()
                );
    }

}
