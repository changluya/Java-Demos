package com.changlu;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineNode;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PDFSplitter {

    public static void main(String[] args) {
        String inputFilePath = "/Users/edy/changlu_workspace/mymd/demo-exer/common-utils/src/main/resources/系统架构设计师教程-官方指定用书（第2版）（3级目录+OCR）.pdf"; // 输入的PDF文件路径
        String outputDirectory = "/Users/edy/changlu_workspace/mymd/demo-exer/common-utils/src/main/resources/output"; // 输出目录
        int[][] pageRanges = {
                {1, 417},    // 第一部分
                {418, 728},  // 第二部分
        };

        try {
            splitPDFWithFullOutline(inputFilePath, outputDirectory, pageRanges);
            System.out.println("PDF拆分完成，完整目录结构已保留！");
        } catch (IOException e) {
            System.err.println("拆分PDF时出错: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void splitPDFWithFullOutline(String inputFilePath,
                                               String outputDirectory,
                                               int[][] pageRanges) throws IOException {
        // 加载原始PDF文档（使用内存优化）
        PDDocument sourceDoc = PDDocument.load(new File(inputFilePath));

        // 获取原始目录结构
        PDDocumentCatalog catalog = sourceDoc.getDocumentCatalog();
        PDDocumentOutline originalOutline = catalog.getDocumentOutline();

        // 创建输出目录
        File outputDir = new File(outputDirectory);
        if (!outputDir.exists()) outputDir.mkdirs();

        int totalPages = sourceDoc.getNumberOfPages();
        System.out.println("原始PDF总页数: " + totalPages);

        // 遍历每个页数范围进行拆分
        for (int i = 0; i < pageRanges.length; i++) {
            int startPage = pageRanges[i][0];
            int endPage = pageRanges[i][1];

            // 验证页数范围是否有效
            if (startPage < 1 || endPage > totalPages || startPage > endPage) {
                System.err.println("跳过无效范围: " + startPage + "-" + endPage);
                continue;
            }

            // 创建拆分器并设置页数范围
            Splitter splitter = new Splitter();
            splitter.setStartPage(startPage);
            splitter.setEndPage(endPage);

            // 执行拆分
            List<PDDocument> splitDocuments = splitter.split(sourceDoc);
            PDDocument part = splitDocuments.get(0);

            // 完整复制原始目录结构
            if (originalOutline != null) {
//                PDDocumentOutline newOutline = cloneOutline(originalOutline);
                part.getDocumentCatalog().setDocumentOutline(originalOutline);
            }

            // 保存拆分后的文档
            String outputFileName = String.format("%s/part_%d_%d-%d.pdf",
                    outputDirectory, i + 1, startPage, endPage);
            part.save(outputFileName);
            part.close();

            System.out.println("已创建: " + outputFileName + " (页数: " + (endPage - startPage + 1) + ")");
        }

        sourceDoc.close();
    }
}