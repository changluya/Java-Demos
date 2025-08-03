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
import java.security.PrivilegedExceptionAction;

/**
 * @description  支持hdfs的上传、下载、删除文件操作
 * @author changlu
 * @date 2025/5/3 01:17
 */
public class HdfsOperator2 {

    public static void main(String[] args) throws IOException, InterruptedException {
        // 创建一个配置对象
        Configuration configuration = new Configuration();
        // 指定hdfs的地址
        configuration.set("fs.defaultFS", "hdfs://server:8020");
        // 方式二：指明用户获取到ugi【模拟其他用户执行操作时（如代理用户）】
        UserGroupInformation ugi = UserGroupInformation.createRemoteUser("root");
        ugi.doAs(new PrivilegedExceptionAction<Void>() {
            public Void run() throws IOException {
                FileSystem fs = FileSystem.get(configuration);
                // 上传文件 check权限
                put(fs);

                // 下载文件
                // hdfs指定路径下载文件到本地
//                download(fs);

                // 删除文件  check权限
//                delete(fs);
                return null;
            }
        });
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
