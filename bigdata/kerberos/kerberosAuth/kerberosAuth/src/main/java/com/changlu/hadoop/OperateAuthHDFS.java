/**
 * @description TODO
 * @author changlu
 * @date 2024/07/05 00:47
 * @version 1.0
 */
package com.changlu.hadoop;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.security.PrivilegedExceptionAction;

/**
 * @description  操作kerberos认证的HDFS
 * @author changlu
 * @date 2024-07-05 0:49
 */
public class OperateAuthHDFS {

    private static FileSystem fs;

    public static void main(String[] args) throws Exception {
        final Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://mycluster");

        //设置kerberos认证
        System.setProperty("java.security.krb5.conf", "E:\\自学历程\\Gitee仓库\\demo-exer\\bigdata\\kerberos\\kerberosAuth\\kerberosAuth\\src\\main\\resources\\krb5.conf");
        UserGroupInformation.loginUserFromKeytab("zhangsan", "E:\\自学历程\\Gitee仓库\\demo-exer\\bigdata\\kerberos\\kerberosAuth\\kerberosAuth\\src\\main\\resources\\zhangsan.keytab");
        UserGroupInformation ugi = UserGroupInformation.getLoginUser();
        fs = ugi.doAs(new PrivilegedExceptionAction<FileSystem>() {
            @Override
            public FileSystem run() throws Exception {
                return FileSystem.get(conf);
            }
        });

        //查看HDFS路径文件
        listHDFSPathDir("/");

        //创建目录
        System.out.println("=======================");
        mkdirOnHDFS("/kerberos_test");
        System.out.println("=======================\n");

        //向HDFS中写入数据
        System.out.println("=======================");
        writeFileToHDFS("E:\\自学历程\\Gitee仓库\\demo-exer\\bigdata\\kerberos\\kerberosAuth\\kerberosAuth\\data\\test.txt", "/kerberos_test/test.txt");
        System.out.println("=======================\n");

        //读取HDFS中数据
        System.out.println("=======================");
        readFileFromHDFS("/kerberos_test/test.txt");
        System.out.println("=======================\n");

        //删除HDFS中目录或文件
        System.out.println("=======================");
        deleteFileOrDirFromHDFS("/kerberos_test");
        System.out.println("=======================\n");

        fs.close();
    }

    private static void deleteFileOrDirFromHDFS(String hdfsFileOrDirPath)throws Exception {
        //判断文件是否存在HDFS
        Path path = new Path(hdfsFileOrDirPath);
        if (!fs.exists(path)) {
            System.out.println("HDFS目录或者文件不存在");
            return;
        }
        //第二个参数表示是否递归删除
        boolean result = fs.delete(path, true);
        if (result) {
            System.out.println("删除目录：" + path + " 成功！");
        }else {
            System.out.println("删除目录：" + path + " 失败！");
        }
    }

    private static void readFileFromHDFS(String hdfsFilePath)throws Exception {
        //读取HDFS文件
        Path path = new Path(hdfsFilePath);
        FSDataInputStream in = fs.open(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String newLine = "";
        while ((newLine = br.readLine()) != null) {
            System.out.println(newLine);
        }

        //关闭流对象
        br.close();
        in.close();
    }

    private static void writeFileToHDFS(String localFilePath, String hdfsFilePath) throws Exception{
        //判断HDFS文件是否存在，存在则删除
        Path hdfsPath = new Path(hdfsFilePath);
        if (fs.exists(hdfsPath)) {
            fs.delete(hdfsPath, true);
        }

        //创建HDFS文件路径
        Path path = new Path(hdfsFilePath);
        FSDataOutputStream out = fs.create(path);

        //读取本地文件写入HDFS路径中
        FileReader fr = new FileReader(localFilePath);
        BufferedReader br = new BufferedReader(fr);
        String newLine = "";
        while ((newLine = br.readLine()) != null) {
            out.write(newLine.getBytes());
            out.write("\n".getBytes());
        }

        //关闭流对象
        out.close();
        br.close();
        fr.close();
        System.out.println("本地文件 ./data/test.txt 写入了HDFS中的" + path.toString() + "文件中");
    }

    private static void mkdirOnHDFS(String dirPath)throws Exception {
        Path path = new Path(dirPath);
        //判断目录是否存在
        if (fs.exists(path)) {
            System.out.println("目录" + dirPath + "已经存在！");
            return;
        }
        //创建HDFS目录
        boolean result = fs.mkdirs(path);
        if (result) {
            System.out.println("创建目录：" + dirPath + " 成功！");
        }else {
            System.out.println("创建目录：" + dirPath + " 失败！");
        }
    }

    private static void listHDFSPathDir(String hdfsPath) throws Exception {
        FileStatus[] fileStatuses = fs.listStatus(new Path(hdfsPath));
        for (FileStatus fileStatus : fileStatuses) {
            System.out.println(fileStatus.getPath());
        }
    }

}
