package com.changlu;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;

public class SimpleHdfsOperator {

    public static void main(String[] args) throws IOException {
        // 创建一个配置对象
        Configuration configuration = new Configuration();
        // 指定hdfs的地址
        configuration.set("fs.defaultFS", "hdfs://server:8020");
        // 获取操作hdfs的对象
        FileSystem fileSystem = FileSystem.get(configuration);
        // 上传文件
        // 获取本地文件的输入流
        FileInputStream fis = new FileInputStream("/Users/edy/workspace/1.txt");
        // 获取hdfs文件的输出流
        FSDataOutputStream fos = fileSystem.create(new Path("/user.txt"));
        // 上传文件，将输入流拷贝到输出流中 (将本地文件上传到hdfs)
        IOUtils.copyBytes(fis, fos, 1024, true);
    }

}
