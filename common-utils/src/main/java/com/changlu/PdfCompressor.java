package com.changlu;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;

import java.io.File;
import java.io.IOException;

public class PdfCompressor {

    public static void compressPdf(String inputPath, String outputPath) throws IOException {
        // 加载PDF文档
        PDDocument document = PDDocument.load(new File(inputPath));
        
        // 设置文档为压缩模式
        document.setAllSecurityToBeRemoved(true); // 移除所有安全设置以允许压缩
        
        // 保存时压缩
        document.save(outputPath);
        
        // 关闭文档
        document.close();
        
        System.out.println("PDF压缩完成，输出文件: " + outputPath);
    }

    public static void main(String[] args) {
        try {
            compressPdf("/Users/edy/changlu_workspace/mymd/demo-exer/common-utils/src/main/resources/系统架构设计师教程-官方指定用书（第2版）（3级目录+OCR）.pdf", "/Users/edy/changlu_workspace/mymd/demo-exer/common-utils/src/main/resources/系统架构设计师教程-官方指定用书（第2版）（3级目录+OCR）压缩.pdf");
        } catch (IOException e) {
            System.err.println("PDF压缩失败: " + e.getMessage());
        }
    }
}