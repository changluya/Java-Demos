package com.changlu;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @description  支持hdfs的上传、下载、删除文件操作
 * @author changlu
 * @date 2025/5/3 01:17
 */
public class HdfsOperator {

    public static void main(String[] args) throws IOException {
        // 方式一：在代码中设置系统属性（优先级高于环境变量）【底层生效点：UserGroupInformation 会读取此属性。】
        System.setProperty("HADOOP_USER_NAME", "root");
        // 创建一个配置对象
        Configuration configuration = new Configuration();
        // 指定hdfs的地址
        configuration.set("fs.defaultFS", "hdfs://server:8020");
        // 获取操作hdfs的对象
        FileSystem fileSystem = FileSystem.get(configuration);
        // 上传文件 check权限
        put(fileSystem);
        // 下载文件
        // hdfs指定路径下载文件到本地
//        download(fileSystem);
        
        // 删除文件  check权限
//        delete(fileSystem);
    }

    private static void delete(FileSystem fileSystem) throws IOException {
        boolean flag = fileSystem.delete(new Path("/README.txt"), true);
        if (flag) {
            System.out.println("删除成功！");
        }else {
            System.out.println("删除失败！");
        }
    }

    /**
     * 下载文件
     * @param fileSystem
     * @throws IOException
     */
    private static void download(FileSystem fileSystem) throws IOException {
        FSDataInputStream fis = fileSystem.open(new Path("/README.txt"));
        FileOutputStream fos = new FileOutputStream("/Users/edy/changlu_workspace/mymd/demo-exer/bigdata/hadoop/hadoop3-demo/src/main/resources/download/README.txt");
        IOUtils.copyBytes(fis, fos, 1024, true);
    }

    /**
     * 上传文件
     * @param fileSystem
     * @throws IOException
     */
    private static void put(FileSystem fileSystem) throws IOException {
        // 上传文件
        // 获取本地文件的输入流
        FileInputStream fis = new FileInputStream("/Users/edy/workspace/1.txt");
        // 获取hdfs文件的输出流
        FSDataOutputStream fos = fileSystem.create(new Path("/user.txt"));
        // 上传文件，将输入流拷贝到输出流中 (将本地文件上传到hdfs)
        IOUtils.copyBytes(fis, fos, 1024, true);
    }

}
