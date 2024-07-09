/**
 * @description TODO
 * @author changlu
 * @date 2024/07/07 12:18
 * @version 1.0
 */
package com.changlu.hadoop;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.file.src.FileSource;
import org.apache.flink.connector.file.src.reader.TextLineInputFormat;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.hadoop.security.UserGroupInformation;

/**
 * @description  Flink安全认证
 * @author changlu
 * @date 2024-07-07 12:18
 */
public class FlinkOperateAuthHDSF {
    public static void main(String[] args) throws Exception {
        //进行kerberos认证
        System.setProperty("java.security.krb5.conf", "E:\\自学历程\\Gitee仓库\\demo-exer\\bigdata\\kerberos\\kerberosAuth\\kerberosAuth\\src\\main\\resources\\krb5.conf");
        String principal = "zhangsan@EXAMPLE.COM";
        String keytabPath = "E:\\自学历程\\Gitee仓库\\demo-exer\\bigdata\\kerberos\\kerberosAuth\\kerberosAuth\\src\\main\\resources\\zhangsan.keytab";
        UserGroupInformation.loginUserFromKeytab(principal, keytabPath);

        //flink流式处理
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        FileSource<String> fileSource = FileSource.forRecordStreamFormat(
                new TextLineInputFormat(),
                new Path("hdfs://mycluster/wc.txt")
        ).build();
        DataStreamSource<String> dataStream = env.fromSource(fileSource, WatermarkStrategy.noWatermarks(), "file-source");
        dataStream.print();

        env.execute();
    }
}
