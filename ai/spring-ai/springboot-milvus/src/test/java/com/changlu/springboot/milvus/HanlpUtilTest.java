package com.changlu.springboot.milvus;

import com.changlu.springboot.milvus.util.HanlpUtil;
import com.changlu.springboot.milvus.util.Langchain4jUtil;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import org.junit.jupiter.api.Test;

import java.util.List;

public class HanlpUtilTest {

    private String articleContent = "# 排查生命周期问题清理未生效\n" +
            "\n" +
            "- [前言](#%E5%89%8D%E8%A8%80)\n" +
            "- [生命周期支持能力](#%E7%94%9F%E5%91%BD%E5%91%A8%E6%9C%9F%E6%94%AF%E6%8C%81%E8%83%BD%E5%8A%9B)\n" +
            "  * [快速查询sql支持data_type_code](#%E5%BF%AB%E9%80%9F%E6%9F%A5%E8%AF%A2sql%E6%94%AF%E6%8C%81data_type_code)\n" +
            "  * [基于之前sql上去转换code、apptype为name](#%E5%9F%BA%E4%BA%8E%E4%B9%8B%E5%89%8Dsql%E4%B8%8A%E5%8E%BB%E8%BD%AC%E6%8D%A2codeapptype%E4%B8%BAname)\n" +
            "  * [后续增量sql补充](#%E5%90%8E%E7%BB%AD%E5%A2%9E%E9%87%8Fsql%E8%A1%A5%E5%85%85)\n" +
            "- [技术支持排查思路（首次排查）](#%E6%8A%80%E6%9C%AF%E6%94%AF%E6%8C%81%E6%8E%92%E6%9F%A5%E6%80%9D%E8%B7%AF%E9%A6%96%E6%AC%A1%E6%8E%92%E6%9F%A5)\n" +
            "- [生命周期接口触发](#%E7%94%9F%E5%91%BD%E5%91%A8%E6%9C%9F%E6%8E%A5%E5%8F%A3%E8%A7%A6%E5%8F%91)\n" +
            "- [业务逻辑](#%E4%B8%9A%E5%8A%A1%E9%80%BB%E8%BE%91)\n" +
            "  * [hive表删除分区表结构的逻辑](#hive%E8%A1%A8%E5%88%A0%E9%99%A4%E5%88%86%E5%8C%BA%E8%A1%A8%E7%BB%93%E6%9E%84%E7%9A%84%E9%80%BB%E8%BE%91)\n" +
            "  * [starrocks清理分区逻辑](#starrocks%E6%B8%85%E7%90%86%E5%88%86%E5%8C%BA%E9%80%BB%E8%BE%91)\n" +
            "- [客户异常场景](#%E5%AE%A2%E6%88%B7%E5%BC%82%E5%B8%B8%E5%9C%BA%E6%99%AF)\n" +
            "  * [问题1：出现网络异常Broken pipe](#%E9%97%AE%E9%A2%981%E5%87%BA%E7%8E%B0%E7%BD%91%E7%BB%9C%E5%BC%82%E5%B8%B8broken-pipe)\n" +
            "  * [问题2：生命周期时间为7天，但是界面上显示还有7天外的分区](#%E9%97%AE%E9%A2%982%E7%94%9F%E5%91%BD%E5%91%A8%E6%9C%9F%E6%97%B6%E9%97%B4%E4%B8%BA7%E5%A4%A9%E4%BD%86%E6%98%AF%E7%95%8C%E9%9D%A2%E4%B8%8A%E6%98%BE%E7%A4%BA%E8%BF%98%E6%9C%897%E5%A4%A9%E5%A4%96%E7%9A%84%E5%88%86%E5%8C%BA)\n" +
            "- [各类问题](#%E5%90%84%E7%B1%BB%E9%97%AE%E9%A2%98)\n" +
            "  * [1）表中分区的清除时间的代码逻辑](#1%E8%A1%A8%E4%B8%AD%E5%88%86%E5%8C%BA%E7%9A%84%E6%B8%85%E9%99%A4%E6%97%B6%E9%97%B4%E7%9A%84%E4%BB%A3%E7%A0%81%E9%80%BB%E8%BE%91)\n" +
            "  * [2）客户从5.2升级到6.0后出现分区表为空时删除表结构的情况](#2%E5%AE%A2%E6%88%B7%E4%BB%8E52%E5%8D%87%E7%BA%A7%E5%88%B060%E5%90%8E%E5%87%BA%E7%8E%B0%E5%88%86%E5%8C%BA%E8%A1%A8%E4%B8%BA%E7%A9%BA%E6%97%B6%E5%88%A0%E9%99%A4%E8%A1%A8%E7%BB%93%E6%9E%84%E7%9A%84%E6%83%85%E5%86%B5)\n" +
            "\n" +
            "---\n" +
            "\n" +
            "# 前言\n" +
            "界面图：寻找到某一张表里的即有分区记录\n" +
            "\n" +
            "参考排查文档：[https://dtstack.yuque.com/rd-center/sm6war/vris4e5hsphz1rxp](https://dtstack.yuque.com/rd-center/sm6war/vris4e5hsphz1rxp)\n" +
            "\n" +
            "TableLifecycleJob表生命周期任务（源码）：[https://e2tfu6fqrn.feishu.cn/docx/U4lldkbbSoLwEmxcYPmcotDSnAc](https://e2tfu6fqrn.feishu.cn/docx/U4lldkbbSoLwEmxcYPmcotDSnAc)\n" +
            "\n" +
            "**相关修复bug：**\n" +
            "\n" +
            "+ 2023-12-08【离线开发，5.2.91】表的生命周期异常，过期数据未清理**（大智排查-提供sql解决）**：[http://zenpms.dtstack.cn/zentao/bug-view-104495.html【mysql锁原因】](http://zenpms.dtstack.cn/zentao/bug-view-104495.html【mysql锁原因】)\n" +
            "+ 2023-12-08【标签模块，5.3.74】hbase表到了生命周期没被删除**（九尾解决-NPE异常导致主线程夯住）**：[http://zenpms.dtstack.cn/zentao/bug-view-98485.html](http://zenpms.dtstack.cn/zentao/bug-view-98485.html)\n" +
            "+ 2023-12-25 【数据资产，hotfix_5.2.x_99887】设置的表的生命周期和实际的对不上**（九尾解决-文件不存在问题）**：[http://zenpms.dtstack.cn/zentao/bug-view-99887.html](http://zenpms.dtstack.cn/zentao/bug-view-99887.html) 【包含其中的验证过程】\n" +
            "+ 2024-04-09【数据地图，v5.2.60】部分hive 分区表的生命周期没生效（me fix）：[http://zenpms.dtstack.cn/zentao/bug-view-106301.html【详细排查流程文件不存在问题，参考#98485】](http://zenpms.dtstack.cn/zentao/bug-view-106301.html【详细排查流程文件不存在问题，参考#98485】)\n" +
            "+ 2024-04-23【离线开发，v5.3.5_lzlj】hive表的生命周期未生效（me fix）：[http://zenpms.dtstack.cn/zentao/bug-view-107328.html【详细排查流程文件不存在问题，参考#99887】](http://zenpms.dtstack.cn/zentao/bug-view-107328.html【详细排查流程文件不存在问题，参考#99887】)\n" +
            "\n" +
            "**设计如此的禅道：**\n" +
            "\n" +
            "+ 2024-09-06：一张生命周期为3天且里面没有数据的表莫名被删除了：[http://zenpms.dtstack.cn/zentao/bug-view-117730.html](http://zenpms.dtstack.cn/zentao/bug-view-117730.html)\n" +
            "\n" +
            "\n" +
            "\n" +
            "代码：\n" +
            "\n" +
            "doProcessPartitionTable：删除表里的分区\n" +
            "\n" +
            "doProcessTable：删除表\n" +
            "\n" +
            "\n" +
            "\n" +
            "---\n" +
            "\n" +
            "# 生命周期支持能力\n" +
            "## 快速查询sql支持data_type_code\n" +
            "执行sql如下：\n" +
            "\n" +
            "```sql\n" +
            "select life_process, app_type, GROUP_CONCAT(data_type_code SEPARATOR ',') from lc_table_process\n" +
            "GROUP BY life_process, app_type\n" +
            "```\n" +
            "\n" +
            "根据子产品类型、数据源类型来划分的：\n" +
            "\n" +
            "![1743073942901-3259a90a-4452-4861-aba0-997a098fced8.png](./img/rUXMH6ZMv9NmVrcp/1743073942901-3259a90a-4452-4861-aba0-997a098fced8-663695.png)\n" +
            "\n" +
            "\n" +
            "\n" +
            "对于data_type_code枚举定义：\n" +
            "\n" +
            "```java\n" +
            "public enum DataSourceTypeEnum {\n" +
            "\n" +
            "    /**\n" +
            "     * RDBMS\n" +
            "     */\n" +
            "    MySQL(1, \"MySQL\", \"5.x、8.x\"),\n" +
            "    // 86\n" +
            "    MySQL_SHARDING(DataSourceType.MYSQL_SHARDING.getVal(), \"MySQL\", \"sharding-proxy\"),\n" +
            "    MySQL8(1001, \"MySQL8\", null),\n" +
            "    MySQLPXC(98, \"MySQL PXC\", null),\n" +
            "    TDSQL(95, \"TDSQL\", null),\n" +
            "    Polardb_For_MySQL(28, \"PolarDB for MySQL8\", null),\n" +
            "    Oracle(2, \"Oracle\", null),\n" +
            "    SQLServer(3, \"SQL Server\", null),\n" +
            "    SQLSERVER_2017_LATER(32, \"SQL Server JDBC\", null),\n" +
            "    PostgreSQL(4, \"PostgreSQL\", null),\n" +
            "    ADB_PostgreSQL(54, \"AnalyticDB PostgreSQL\", null),\n" +
            "    DB2(19, \"DB2\", null),\n" +
            "    DMDB_FOR_MySQL(35, \"DMDB\", \"For MySQL\"),\n" +
            "    RDBMS(5, \"RDBMS\", null),\n" +
            "    KINGBASE8(40, \"KingbaseES8\", \"8.3(8.2)\"),\n" +
            "    KINGBASE8_R6(121, \"KingbaseES8\", \"8.6\"),\n" +
            "    HIVE1X(27, \"Hive\", \"1.x\"),\n" +
            "    HIVE2X(7, \"Hive\", \"2.x\"),\n" +
            "    HIVE3X_APACHE(50, \"Hive\", \"3.x(Apache)\"),\n" +
            "    HIVE3X_CDP(65, \"Hive\", \"3.x(CDP)\"),\n" +
            "    // 108\n" +
            "    HIVE3X_HUAWEI(DataSourceType.HUAWEI_HIVE3.getVal(),\"Hive\",\"3Huawei\"),\n" +
            "    SparkThrift2_1(45, \"SparkThrift\", null),\n" +
            "    MAXCOMPUTE(10, \"Maxcompute\", null),\n" +
            "    GREENPLUM6(36, \"Greenplum\", null),\n" +
            "    SEABOX(122, \"Seabox\", null),\n" +
            "    HOLOGRES(123, \"Hologres\", \"1.3.67\"),\n" +
            "    LIBRA(21, \"GaussDB\", null),\n" +
            "    GBase_8a(22, \"GBase_8a\", null),\n" +
            "    HDFS(6, \"HDFS\", \"2.x\"),\n" +
            "    HDFS_TBDS(60, \"HDFS\", \"TBDS\"),\n" +
            "    HDFS3(63, \"HDFS\", \"3.x\"),\n" +
            "    HDFS3_CDP(1003, \"HDFS\", \"CDP\"),\n" +
            "    HUAWEI_HDFS(78, \"HDFS\", \"Huawei\"),\n" +
            "    FTP(9, \"FTP\", null),\n" +
            "    IMPALA(29, \"Impala\", null),\n" +
            "    ClickHouse(25, \"ClickHouse\", null),\n" +
            "    TiDB(31, \"TiDB\", null),\n" +
            "    CarbonData(20, \"CarbonData\", null),\n" +
            "    Kudu(24,\"Kudu\", null),\n" +
            "    Kylin(58, \"Kylin URL\", \"3.x\"),\n" +
            "    HBASE(8, \"HBase\", \"1.x\"),\n" +
            "    HBASE2(39, \"HBase\", \"2.x\"),\n" +
            "    HBASE_TBDS(61,\"HBase\",\"TBDS\"),\n" +
            "    Phoenix4(30, \"Phoenix\", \"4.x\"),\n" +
            "    Phoenix5(38, \"Phoenix\", \"5.x\"),\n" +
            "    ES(11, \"Elasticsearch\", \"5.x\"),\n" +
            "    ES6(33, \"Elasticsearch\", \"6.x\"),\n" +
            "    ES7(46, \"Elasticsearch\", \"7.x\"),\n" +
            "    MONGODB(13, \"MongoDB\", null),\n" +
            "    REDIS(12, \"Redis\", \"9.0\"),\n" +
            "    KEYBYTE9(125, \"Keybyte\", \"9.0\"),\n" +
            "    S3(41, \"S3\", null),\n" +
            "    KAFKA_TBDS(62,\"Kafka\",\"TBDS\"),\n" +
            "    KAFKA(26, \"Kafka\", \"1.x\"),\n" +
            "    KAFKA_2X(37, \"Kafka\", \"2.x\"),\n" +
            "    KAFKA_3X(120,\"Kafka\",\"3.x\"),\n" +
            "    KAFKA_09(18, \"Kafka\", \"0.9\"),\n" +
            "    KAFKA_10(17, \"Kafka\", \"0.10\"),\n" +
            "    KAFKA_11(14, \"Kafka\", \"0.11\"),\n" +
            "    EMQ(34, \"EMQ\", null),\n" +
            "    WEB_SOCKET(42, \"WebSocket\", null),\n" +
            "    VERTICA(43, \"Vertica\", \"7.x\"),\n" +
            "    VERTICA_11(69, \"Vertica\", \"11.x\"),\n" +
            "    STARROCKS(91, \"StarRocks\", \"2.x\"),\n" +
            "    STARROCKS_3_X(118, \"StarRocks\", \"3.x\"),\n" +
            "    SOCKET(44, \"Socket\", null),\n" +
            "    ADS(15, \"AnalyticDB MySQL\", \"3.x\"),\n" +
            "    //112\n" +
            "    ADS_MYSQL_2_X(DataSourceType.ADS_MYSQL_2_X.getVal(), \"AnalyticDB MySQL\", \"2.x\"),\n" +
            "    Presto(48, \"Presto\", null),\n" +
            "    TBDS_PRESTO(109,\"Presto\",\"TBDS\"),\n" +
            "    SOLR(53,\"Solr\",\"7.x\"),\n" +
            "    INFLUXDB(55,\"InfluxDB\",\"1.x\"),\n" +
            "    INCEPTOR(52, \"Inceptor\", null),\n" +
            "    AWS_S3(51, \"AWS S3\", null),\n" +
            "    OPENTSDB(56,\"OpenTSDB\",\"2.x\"),\n" +
            "    Doris_JDBC(57,\"Doris\",\"0.14.x(jdbc)\"),\n" +
            "    Kylin_Jdbc(23, \"Kylin JDBC\", \"3.x\"),\n" +
            "    // 49\n" +
            "    OceanBase(DataSourceType.OceanBase.getVal(),\"OceanBase\",\"3.2.x(Mysql)\"),\n" +
            "    // 117\n" +
            "    OceanBase_For_Oracle(DataSourceType.OceanBase_For_Oracle.getVal(), \"OceanBase\",\"3.2.x(Oracle)\"),\n" +
            "    RESTFUL(47,\"Restful\",null),\n" +
            "    TRINO(59,\"Trino\",null),\n" +
            "    DORIS_HTTP(64,\"Doris\",\"0.14.x(http)\"),\n" +
            "    DORIS_2_X(119, \"Doris\", \"2.x\"),\n" +
            "    ICEBERG(66,\"Iceberg\",null),\n" +
            "    DMDB_FOR_ORACLE(67, \"DMDB\", \"For Oracle\"),\n" +
            "    HIVE_METASTORE(68, \"Hive metastore\",\"2.x\"),\n" +
            "    HIVE_METASTORE_3X(124, \"Hive metastore\",\"3.x\"),\n" +
            "    DRDS(72,\"DRDS\",null),\n" +
            "    UPDRDB(73,\"UPDRDB\",null),\n" +
            "    UPRedis(74,\"UPRedis\",null),\n" +
            "    CSP_S3(75,\"CSP S3\",null),\n" +
            "    SAP_HANA1x(76,\"SAP HANA\",\"1.x\"),\n" +
            "    SAP_HANA2x(77,\"SAP HANA\",\"2.x\"),\n" +
            "    KAFKA_HUAWEICLOUD(70,\"Kafka\",\"HuaweiCloud\"),\n" +
            "    HBASE_HUAWEICLOUD(71,\"HBase\",\"HuaweiCloud\"),\n" +
            "    HBASE_REST(99,\"HBase\",\"RESTFUL\"),\n" +
            "    Confluent5(79, \"Confluent\", \"5.x\"),\n" +
            "    // 枚举值与dataSourceX保持一致\n" +
            "    TDENGINE(87,  \"TDengine\", \"2.4.0\"),\n" +
            "    // 116\n" +
            "    TDENGINE_31X(DataSourceType.TDENGINE_3X.getVal(),  \"TDengine\", \"3.1.0.x\"),\n" +
            "\n" +
            "    RANGER(89,\"ranger\",null),\n" +
            "    LDAP(90,\"ldap\",null),\n" +
            "    ROCKET_MQ(93, \"RocketMQ\",\"4.x\"),\n" +
            "    RABBIT_MQ(92, \"RabbitMQ\",\"3.7\"),\n" +
            "    TBDS_HIVE(94,\"Hive\",\"2.x-TBDS\"),\n" +
            "    GREAT_DB(96,\"GreatDB\",null),\n" +
            "    SYBASE(97,\"Sybase\",\"\"),\n" +
            "    ARGODB(100,\"ArgoDB\",\"\"),\n" +
            "    VASTBASE(101,\"Vastbase\",\"\"),\n" +
            "    ES_HUAWEI(102, \"Elasticsearch\", \"HuaweiCloud\"),\n" +
            "    // 103\n" +
            "    HYPER_BASE(DataSourceType.HYPER_BASE.getVal(),DataSourceType.HYPER_BASE.getName(),\"7.x\"),\n" +
            "    // 126\n" +
            "    HYPER_BASE_9X(DataSourceType.HYPER_BASE_9X.getVal(),DataSourceType.HYPER_BASE.getName(),\"9.0\"),\n" +
            "    // 104\n" +
            "    HASH_DATA(DataSourceType.HASH_DATA.getVal(),\"HashData\",\"\" ),\n" +
            "    // 105\n" +
            "    CKAFKA1X(DataSourceType.CKAFKA_101.getVal(),\"Kafka\",\"CKafka 1.x\"),\n" +
            "    // 106\n" +
            "    CKAFKA2X(DataSourceType.CKAFKA_281.getVal(), \"Kafka\",\"CKafka 2.x\"),\n" +
            "    // 107\n" +
            "    CMQ(DataSourceType.CMQ.getVal(),\"RabbitMQ\",\"CMQ\"),\n" +
            "    // 113\n" +
            "    OUSHUDB(DataSourceType.OUSHUDB.getVal(), \"OushuDB\",\"5.1.1.x\"),\n" +
            "    // 114\n" +
            "    GITLAB(DataSourceType.GITLAB.getVal(), \"GitLab\",\"\"),\n" +
            "    ;\n" +
            "\n" +
            "}\n" +
            "```\n" +
            "\n" +
            "\n" +
            "\n" +
            "app_type如下：\n" +
            "\n" +
            "```sql\n" +
            "public enum AppTypeEnum {\n" +
            "\n" +
            "\n" +
            "\n" +
            "    /**\n" +
            "     * 离线计算\n" +
            "     */\n" +
            "    RDOS(1, \"rdos\", \"离线开发\", \"rdos\",100),\n" +
            "    /**\n" +
            "     * 数据质量\n" +
            "     */\n" +
            "    DQ(2, \"valid\", \"数据质量\", \"dataQuality\",50),\n" +
            "    /**\n" +
            "     * 数据api\n" +
            "     */\n" +
            "    API(3, \"api\", \"数据服务\", \"dataApi\",85),\n" +
            "    /**\n" +
            "     * 标签引擎\n" +
            "     */\n" +
            "    TAG(4, \"tag\", \"客户数据洞察\", \"tagEngine\",80),\n" +
            "    /**\n" +
            "     *\n" +
            "     */\n" +
            "    MAP(5, \"map\", \"数据地图\", \"\",65),\n" +
            "    /**\n" +
            "     * 控制台\n" +
            "     */\n" +
            "    CONSOLE(6, \"console\", \"控制台\", \"console\",65),\n" +
            "    /**\n" +
            "     * 实时\n" +
            "     */\n" +
            "    STREAM(7, \"stream\", \"实时开发\", \"stream\",95),\n" +
            "    /**\n" +
            "     * 数据科学(算法)\n" +
            "     */\n" +
            "    DATASCIENCE(8, \"ai\", \"算法开发\", \"science\",60),\n" +
            "    /**\n" +
            "     * 数据资产\n" +
            "     */\n" +
            "    DATAASSETS(9, \"assets\", \"数据资产\", \"dataAssets\",90),\n" +
            "    /**\n" +
            "     * 指标平台\n" +
            "     */\n" +
            "    INDEX(10, \"easyIndex\", \"指标管理分析\", \"easyIndex\",75),\n" +
            "\n" +
            "    /**\n" +
            "     * 数据集成\n" +
            "     */\n" +
            "    DATASYNC(12,\"dataSync\",\"数据集成\",\"dataSync\",55),\n" +
            "\n" +
            "    /**\n" +
            "     * 数据湖\n" +
            "     */\n" +
            "    DATALAKE(15,\"dataLake\",\"数据湖\",\"dataLake\",70),\n" +
            "\n" +
            "    /**\n" +
            "     * 业务中心\n" +
            "     */\n" +
            "    PUBLIC_SERVICE(16,\"publicService\",\"公共服务\",\"publicService\",54),\n" +
            "\n" +
            "    ;\n" +
            "}\n" +
            "```\n" +
            "\n" +
            "\n" +
            "\n" +
            "## 基于之前sql上去转换code、apptype为name\n" +
            "```sql\n" +
            "SELECT \n" +
            "    life_process, \n" +
            "    CASE app_type\n" +
            "        WHEN 1 THEN '离线开发'\n" +
            "        WHEN 2 THEN '数据质量'\n" +
            "        WHEN 3 THEN '数据服务'\n" +
            "        WHEN 4 THEN '客户数据洞察'\n" +
            "        WHEN 5 THEN '数据地图'\n" +
            "        WHEN 6 THEN '控制台'\n" +
            "        WHEN 7 THEN '实时开发'\n" +
            "        WHEN 8 THEN '算法开发'\n" +
            "        WHEN 9 THEN '数据资产'\n" +
            "        WHEN 10 THEN '指标管理分析'\n" +
            "        WHEN 12 THEN '数据集成'\n" +
            "        WHEN 15 THEN '数据湖'\n" +
            "        WHEN 16 THEN '公共服务'\n" +
            "        ELSE CONCAT('未知类型(', app_type, ')')\n" +
            "    END AS app_type_name,\n" +
            "    GROUP_CONCAT(\n" +
            "        CASE data_type_code\n" +
            "            WHEN 1 THEN 'MySQL'\n" +
            "            WHEN 2 THEN 'Oracle'\n" +
            "            WHEN 3 THEN 'SQLServer'\n" +
            "            WHEN 4 THEN 'PostgreSQL'\n" +
            "            WHEN 5 THEN 'RDBMS'\n" +
            "            WHEN 6 THEN 'HDFS'\n" +
            "            WHEN 7 THEN 'HIVE2X'\n" +
            "            WHEN 8 THEN 'HBASE'\n" +
            "            WHEN 9 THEN 'FTP'\n" +
            "            WHEN 10 THEN 'MAXCOMPUTE'\n" +
            "            WHEN 11 THEN 'ES'\n" +
            "            WHEN 12 THEN 'REDIS'\n" +
            "            WHEN 13 THEN 'MONGODB'\n" +
            "            WHEN 14 THEN 'KAFKA_11'\n" +
            "            WHEN 15 THEN 'ADS'\n" +
            "            WHEN 17 THEN 'KAFKA_10'\n" +
            "            WHEN 18 THEN 'KAFKA_09'\n" +
            "            WHEN 19 THEN 'DB2'\n" +
            "            WHEN 20 THEN 'CarbonData'\n" +
            "            WHEN 21 THEN 'LIBRA'\n" +
            "            WHEN 22 THEN 'GBase_8a'\n" +
            "            WHEN 23 THEN 'Kylin_Jdbc'\n" +
            "            WHEN 24 THEN 'Kudu'\n" +
            "            WHEN 25 THEN 'ClickHouse'\n" +
            "            WHEN 26 THEN 'KAFKA'\n" +
            "            WHEN 27 THEN 'HIVE1X'\n" +
            "            WHEN 28 THEN 'Polardb_For_MySQL'\n" +
            "            WHEN 29 THEN 'Impala'\n" +
            "            WHEN 30 THEN 'Phoenix4'\n" +
            "            WHEN 31 THEN 'TiDB'\n" +
            "            WHEN 32 THEN 'SQLSERVER_2017_LATER'\n" +
            "            WHEN 33 THEN 'ES6'\n" +
            "            WHEN 34 THEN 'EMQ'\n" +
            "            WHEN 35 THEN 'DMDB_FOR_MySQL'\n" +
            "            WHEN 36 THEN 'GREENPLUM6'\n" +
            "            WHEN 37 THEN 'KAFKA_2X'\n" +
            "            WHEN 38 THEN 'Phoenix5'\n" +
            "            WHEN 39 THEN 'HBASE2'\n" +
            "            WHEN 40 THEN 'KINGBASE8'\n" +
            "            WHEN 41 THEN 'S3'\n" +
            "            WHEN 42 THEN 'WEB_SOCKET'\n" +
            "            WHEN 43 THEN 'VERTICA'\n" +
            "            WHEN 44 THEN 'SOCKET'\n" +
            "            WHEN 45 THEN 'SparkThrift2_1'\n" +
            "            WHEN 46 THEN 'ES7'\n" +
            "            WHEN 47 THEN 'RESTFUL'\n" +
            "            WHEN 48 THEN 'Presto'\n" +
            "            WHEN 49 THEN 'OceanBase'\n" +
            "            WHEN 50 THEN 'HIVE3X_APACHE'\n" +
            "            WHEN 51 THEN 'AWS_S3'\n" +
            "            WHEN 52 THEN 'INCEPTOR'\n" +
            "            WHEN 53 THEN 'SOLR'\n" +
            "            WHEN 54 THEN 'ADB_PostgreSQL'\n" +
            "            WHEN 55 THEN 'INFLUXDB'\n" +
            "            WHEN 56 THEN 'OPENTSDB'\n" +
            "            WHEN 57 THEN 'Doris_JDBC'\n" +
            "            WHEN 58 THEN 'Kylin'\n" +
            "            WHEN 59 THEN 'TRINO'\n" +
            "            WHEN 60 THEN 'HDFS_TBDS'\n" +
            "            WHEN 61 THEN 'HBASE_TBDS'\n" +
            "            WHEN 62 THEN 'KAFKA_TBDS'\n" +
            "            WHEN 63 THEN 'HDFS3'\n" +
            "            WHEN 64 THEN 'DORIS_HTTP'\n" +
            "            WHEN 65 THEN 'HIVE3X_CDP'\n" +
            "            WHEN 66 THEN 'ICEBERG'\n" +
            "            WHEN 67 THEN 'DMDB_FOR_ORACLE'\n" +
            "            WHEN 68 THEN 'HIVE_METASTORE'\n" +
            "            WHEN 69 THEN 'VERTICA_11'\n" +
            "            WHEN 70 THEN 'KAFKA_HUAWEICLOUD'\n" +
            "            WHEN 71 THEN 'HBASE_HUAWEICLOUD'\n" +
            "            WHEN 72 THEN 'DRDS'\n" +
            "            WHEN 73 THEN 'UPDRDB'\n" +
            "            WHEN 74 THEN 'UPRedis'\n" +
            "            WHEN 75 THEN 'CSP_S3'\n" +
            "            WHEN 76 THEN 'SAP_HANA1x'\n" +
            "            WHEN 77 THEN 'SAP_HANA2x'\n" +
            "            WHEN 78 THEN 'HUAWEI_HDFS'\n" +
            "            WHEN 79 THEN 'Confluent5'\n" +
            "            WHEN 86 THEN 'MySQL_SHARDING'\n" +
            "            WHEN 87 THEN 'TDENGINE'\n" +
            "            WHEN 89 THEN 'RANGER'\n" +
            "            WHEN 90 THEN 'LDAP'\n" +
            "            WHEN 91 THEN 'STARROCKS'\n" +
            "            WHEN 92 THEN 'RABBIT_MQ'\n" +
            "            WHEN 93 THEN 'ROCKET_MQ'\n" +
            "            WHEN 94 THEN 'TBDS_HIVE'\n" +
            "            WHEN 95 THEN 'TDSQL'\n" +
            "            WHEN 96 THEN 'GREAT_DB'\n" +
            "            WHEN 97 THEN 'SYBASE'\n" +
            "            WHEN 98 THEN 'MySQLPXC'\n" +
            "            WHEN 99 THEN 'HBASE_REST'\n" +
            "            WHEN 100 THEN 'ARGODB'\n" +
            "            WHEN 101 THEN 'VASTBASE'\n" +
            "            WHEN 102 THEN 'ES_HUAWEI'\n" +
            "            WHEN 103 THEN 'HYPER_BASE'\n" +
            "            WHEN 104 THEN 'HASH_DATA'\n" +
            "            WHEN 105 THEN 'CKAFKA1X'\n" +
            "            WHEN 106 THEN 'CKAFKA2X'\n" +
            "            WHEN 107 THEN 'CMQ'\n" +
            "            WHEN 108 THEN 'HIVE3X_HUAWEI'\n" +
            "            WHEN 109 THEN 'TBDS_PRESTO'\n" +
            "            WHEN 112 THEN 'ADS_MYSQL_2_X'\n" +
            "            WHEN 113 THEN 'OUSHUDB'\n" +
            "            WHEN 114 THEN 'GITLAB'\n" +
            "            WHEN 116 THEN 'TDENGINE_31X'\n" +
            "            WHEN 117 THEN 'OceanBase_For_Oracle'\n" +
            "            WHEN 118 THEN 'STARROCKS_3_X'\n" +
            "            WHEN 119 THEN 'DORIS_2_X'\n" +
            "            WHEN 120 THEN 'KAFKA_3X'\n" +
            "            WHEN 121 THEN 'KINGBASE8_R6'\n" +
            "            WHEN 122 THEN 'SEABOX'\n" +
            "            WHEN 123 THEN 'HOLOGRES'\n" +
            "            WHEN 124 THEN 'HIVE_METASTORE_3X'\n" +
            "            WHEN 125 THEN 'KEYBYTE9'\n" +
            "            WHEN 126 THEN 'HYPER_BASE_9X'\n" +
            "            ELSE CONCAT('Unknown(', data_type_code, ')')\n" +
            "        END\n" +
            "        SEPARATOR ','\n" +
            "    ) AS data_type_names\n" +
            "FROM lc_table_process\n" +
            "GROUP BY life_process, app_type;\n" +
            "```\n" +
            "\n" +
            "效果如下：\n" +
            "\n" +
            "![1743074858296-cf01890b-6982-432a-9c69-73b40c40c37c.png](./img/rUXMH6ZMv9NmVrcp/1743074858296-cf01890b-6982-432a-9c69-73b40c40c37c-064800.png)\n" +
            "\n" +
            "\n" +
            "\n" +
            "## 后续增量sql补充\n" +
            "参考如下（子产品类型、数据源类型）：\n" +
            "\n" +
            "```sql\n" +
            "-- 处理 star rocks 生命周期\n" +
            "delete from lc_table_process where app_type = 4 and data_type_code in (91, 118) and life_process = 'starRocks';\n" +
            "INSERT INTO lc_table_process(app_type, data_type_code, life_process)\n" +
            "VALUES (4, 91, 'starRocks'), (4, 118, 'starRocks3.x');\n" +
            "```\n" +
            "\n" +
            "\n" +
            "\n" +
            "---\n" +
            "\n" +
            "# 技术支持排查思路（首次排查）\n" +
            "提示：通过日志定位下是清理报错，还是未触发清理操作\n" +
            "\n" +
            "参考文档：[https://dtstack.yuque.com/rd-center/sm6war/vris4e5hsphz1rxp](https://dtstack.yuque.com/rd-center/sm6war/vris4e5hsphz1rxp)\n" +
            "\n" +
            "[http://zenpms.dtstack.cn/zentao/bug-view-104495.html](http://zenpms.dtstack.cn/zentao/bug-view-104495.html)\n" +
            "\n" +
            "代码入口：com.dtstack.pubsvc.job.TableLifecycleJob#checkTableLife\n" +
            "\n" +
            "情况1：分区不存在问题情况（修复禅道#98485）\n" +
            "\n" +
            "情况2：死锁问题导致生命周期没有清理（禅道排查#）\n" +
            "\n" +
            "\n" +
            "\n" +
            "**背景：**技术支持经常会找到问某张表的生命周期没有生效，本来到期该删除的表或分区实际却没有删除。（可能是垃圾数据过多导致阻塞卡住）\n" +
            "\n" +
            "**技术支持侧排查流程：**\n" +
            "\n" +
            "```java\n" +
            "1、首先根据技术支持说的有问题的没有正常删除的表的名称，到dt-pub-service的mysql库下，通过\n" +
            "select * from lc_table_life where table_name = '';获取到对应表的id。\n" +
            "2、到dt-pub-service服务器下的logs目录，根据pub_svc_job.log日志内容判断生命周期的定时任务在哪台服务器上运行。（底层用 mysql 实现的分布式锁，任务只在一台服务器上运行）\n" +
            "3、vim进入pub_svc_job.log，根据table = #{tableId}来定位到报错的地方，根据报错信息找到所在代码行数。\n" +
            "4、通过tag号，切到对应分支，对比报错行数来分析报错原因。\n" +
            "5、当在pub_svc_job.log日志中看不出问题所在的原因时，可以vi打开pub_svc_web.log文件，搜索SafeExecuteUtil\n" +
            "```\n" +
            "\n" +
            "\n" +
            "\n" +
            "# 生命周期接口触发\n" +
            "**生命周期立即生效：**\n" +
            "\n" +
            "分支：POST请求\n" +
            "\n" +
            "```shell\n" +
            "curl --location --request POST 'http://portalfront-test-53x-ll-easyindex.base53.devops.dtstack.cn/api/publicService/tableLife/job/manualHandle' \\\n" +
            "  --header 'Cookie: dt_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ0ZW5hbnRfaWQiOiIxMDQ3MyIsInVzZXJfaWQiOiIxIiwidXNlcl9uYW1lIjoiYWRtaW5AZHRzdGFjay5jb20iLCJleHAiOjE3MzEyMDQ0OTgsImlhdCI6MTczMDY4NjExM30.6GiqDRVjyRITir-rOK6Zf7idRRhdS9TTkgAAn6y0JsY;' \\\n" +
            "  --header 'Content-Type: application/x-www-form-urlencoded' \\\n" +
            "  --data-urlencode 'tableIds=46027'\n" +
            "```\n" +
            "\n" +
            "**泸州老窖定制化客户的测试接口：**\n" +
            "\n" +
            "```shell\n" +
            "curl --location --request GET \"http://10.0.3.11/api/publicService/tableLife/job/manualHandle?tableIds=269417\" ^\n" +
            "--header \"Cookie: dt_expire_cycle=0; dt_user_id=1; dt_username=admin%40dtstack.com; dt_can_redirect=false; dt_cookie_time=2024-12-06+16%3A02%3A49; sysLoginType=%7B%22sysId%22%3A1%2C%22sysType%22%3A0%2C%22sysName%22%3A%22UIC%u8D26%u53F7%u767B%u5F55%22%7D; track_rdos=true; dt_tenant_id=1; dt_tenant_name=LZLJ_%E7%94%9F%E4%BA%A7%E7%A7%9F%E6%88%B7; dt_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ0ZW5hbnRfaWQiOiIxIiwidXNlcl9pZCI6IjEiLCJ1c2VyX25hbWUiOiJhZG1pbkBkdHN0YWNrLmNvbSIsImV4cCI6MTczODkxNTM2OSwiaWF0IjoxNzMzNDQ1MzA4fQ.pfpzZCPMCLl57z2P2qeqIv0Rku0_G69IERWqFra5K4c; dt_is_tenant_admin=true; dt_is_tenant_creator=false; DT_SESSION_ID=a4354dd4-78b0-4464-8d2e-ed2b8a998675\" ^\n" +
            "--header \"User-Agent: Apifox/1.0.0 (https://apifox.com)\"\n" +
            "```\n" +
            "\n" +
            "说明：cookie要整体替换，对应的tableId在url的后面，请求方式为GET\n" +
            "\n" +
            "\n" +
            "\n" +
            "**判断接口是否调用成功：**cmd调用接口如果是等待了一会，然后没有响应值，就表示调用成功！或者可以到pub_svc_job.log搜下有没有对应的tableId，如果有的话也就是调用成功。\n" +
            "\n" +
            "---\n" +
            "\n" +
            "# 业务逻辑\n" +
            "## hive表删除分区表结构的逻辑\n" +
            "相关历史禅道：\n" +
            "\n" +
            " 【6.0】脏数据表生命周期到达不应该删除表结构：[http://zenpms.dtstack.cn/zentao/bug-view-79146.html](http://zenpms.dtstack.cn/zentao/bug-view-79146.html)\n" +
            "\n" +
            "问题：\n" +
            "\n" +
            "![1737857335548-c06f27f0-a32d-4e54-8d58-df07253e7f9d.png](./img/rUXMH6ZMv9NmVrcp/1737857335548-c06f27f0-a32d-4e54-8d58-df07253e7f9d-030489.png)\n" +
            "\n" +
            "逻辑：\n" +
            "\n" +
            "publicservice针对hive删除表结构的逻辑为：![1737857379051-93aff6f8-791d-49be-88e7-532b01a2ab78.png](./img/rUXMH6ZMv9NmVrcp/1737857379051-93aff6f8-791d-49be-88e7-532b01a2ab78-337416.png)\n" +
            "\n" +
            "是根据这个字段：tableLife.getDeleteMeta()\n" +
            "\n" +
            "![1737857412308-fe93004a-31d4-4346-aafb-d83680ca1e6e.png](./img/rUXMH6ZMv9NmVrcp/1737857412308-fe93004a-31d4-4346-aafb-d83680ca1e6e-571606.png)\n" +
            "\n" +
            "也就是lc_table_life表的delete_meta字段，对应的表结构为：\n" +
            "\n" +
            "```shell\n" +
            "select id, table_name, delete_meta from lc_table_life where table_name = '';\n" +
            "```\n" +
            "\n" +
            "\n" +
            "\n" +
            "这个字段何时为1呢？\n" +
            "\n" +
            "离线会根据是否是脏数据表来判断，如果是则为1：\n" +
            "\n" +
            "![1737857487701-1c36be4d-1e2f-4a09-af2d-b17af0ac8f87.png](./img/rUXMH6ZMv9NmVrcp/1737857487701-1c36be4d-1e2f-4a09-af2d-b17af0ac8f87-541106.png)\n" +
            "\n" +
            "调用engine更新表结构的接口调用：\n" +
            "\n" +
            "```shell\n" +
            "@RequestLine(\"POST /api/publicService/lc/table/update\")\n" +
            "@Headers({\"Content-Type: application/json\"})\n" +
            "ApiResponse<AddOrUpdateResultDTO> update(LcTableLifeParam var1);\n" +
            "```\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "## starrocks清理分区逻辑\n" +
            "**针对starrocks分区清理，针对不同的子产品有不同逻辑，目前涉及到的子产品为**：标签tag、指标easyindex。\n" +
            "\n" +
            "1）标签tag清理逻辑：根据字段tag_engine_partition走。\n" +
            "\n" +
            "2）指标easyIndex清理逻辑：根据字段yw_date走。【62分支新加逻辑】\n" +
            "\n" +
            "相关任务单：[http://zenpms.dtstack.cn/zentao/story-view-14048.html](http://zenpms.dtstack.cn/zentao/story-view-14048.html)\n" +
            "\n" +
            "禅道（根据yw_date走逻辑）：[http://zenpms.dtstack.cn/zentao/bug-view-127981.html](http://zenpms.dtstack.cn/zentao/bug-view-127981.html)\n" +
            "\n" +
            "![1743566077191-4657c2f4-c754-4ea7-a9bd-5d8a04808b7e.png](./img/rUXMH6ZMv9NmVrcp/1743566077191-4657c2f4-c754-4ea7-a9bd-5d8a04808b7e-920525.png)\n" +
            "\n" +
            "\n" +
            "\n" +
            "# 客户异常场景\n" +
            "## 问题1：出现网络异常Broken pipe\n" +
            "**出现异常情况：**生命周期表清理异常。\n" +
            "\n" +
            "![image-20240618114920985.png](./img/rUXMH6ZMv9NmVrcp/1733393066243-18f60a3b-2e9a-4fe3-8c62-fff8b31b7c59-443239.png)  \n" +
            "\n" +
            "**排查过程：**根据堆栈找到对应执行的sql看是否有超时情况。\n" +
            "\n" +
            "**问题描述：**清理过程中，连接thriftserver查询表信息出现异常，可能是网络连接复用缓存的问题导致的，这边需要改下publicservice配置参数观察下，修改参数后就不会走缓存。\n" +
            "\n" +
            "```properties\n" +
            "# 需要publicservice服务配置文件里application.properties加下这个参数：\n" +
            "dtstack.tableLife.openCache=0,0,0,0\n" +
            "```\n" +
            "\n" +
            "**观察结果：**lzlj客户反映修改参数之后，能够正常删除分区。\n" +
            "\n" +
            "\n" +
            "\n" +
            "---\n" +
            "\n" +
            "## 问题2：生命周期时间为7天，但是界面上显示还有7天外的分区\n" +
            "**场景如下：**\n" +
            "\n" +
            "![1733464862630-775ebfbd-bedc-4849-9a7a-1cfe9116757b.png](./img/rUXMH6ZMv9NmVrcp/1733464862630-775ebfbd-bedc-4849-9a7a-1cfe9116757b-115673.png)\n" +
            "\n" +
            "**技术支持侧排查**：\n" +
            "\n" +
            "需要通过hdfs查看下对应的分区，如果查询某个表下面的分区修改时间依旧是指定生命周期时间内的，那么就是没有问题的，如下示例就是没有问题的：\n" +
            "\n" +
            "![1733465072497-8d2bcc13-e99c-4ec1-88ba-91388db28b9e.png](./img/rUXMH6ZMv9NmVrcp/1733465072497-8d2bcc13-e99c-4ec1-88ba-91388db28b9e-094132.png)\n" +
            "\n" +
            "如果修改时间是在生命周期以外的，则按照上面【技术支持排查思路（首次排查）】过程搜索下指定表的报错信息，找开发处理下。\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "---\n" +
            "\n" +
            "# 各类问题\n" +
            "## 1）表中分区的清除时间的代码逻辑\n" +
            "首先就是客户侧设置的生命周期时间，**对比的是指定分区的修改时间，并不是分区名字**：\n" +
            "\n" +
            "![202411041640487.png](./img/rUXMH6ZMv9NmVrcp/1733391152628-f1c62d97-54e1-4dd7-9168-836ff089b47b-817574.png)\n" +
            "\n" +
            "**指定分区的修改时间怎么看？**hdfs dfs -ls查看到的分区列表中就有修改时间\n" +
            "\n" +
            "![1733465800834-19801a42-1377-422c-9bad-7719cfc69959.png](./img/rUXMH6ZMv9NmVrcp/1733465800834-19801a42-1377-422c-9bad-7719cfc69959-763039.png)\n" +
            "\n" +
            "****\n" +
            "\n" +
            "**若是想要快速的将当前表里的分区删除掉，那么步骤如下：**\n" +
            "\n" +
            "1、将lc_table_life表对应id的life_time时间改为0。\n" +
            "\n" +
            "2、接着手动触发下接口。\n" +
            "\n" +
            "\n" +
            "\n" +
            "## 2）客户从5.2升级到6.0后出现分区表为空时删除表结构的情况\n" +
            "相关禅道：[http://zenpms.dtstack.cn/zentao/bug-view-117730.html](http://zenpms.dtstack.cn/zentao/bug-view-117730.html)\n" +
            "\n" +
            "结论如下：\n" +
            "\n" +
            "![1738813557188-d05bf5b0-73e4-4475-ad29-fda9b7d0d877.png](./img/rUXMH6ZMv9NmVrcp/1738813557188-d05bf5b0-73e4-4475-ad29-fda9b7d0d877-233203.png)\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "---\n" +
            "\n" +
            "整理者：长路 时间：2024.11.4\n" +
            "\n" +
            "\n" +
            "\n" +
            "> 更新: 2025-04-02 11:55:03  \n" +
            "> 原文: <https://dtstack.yuque.com/rd-center/sm6war/zg1x3g21ct19gfn7>";

    // 标准分词
    @Test
    public void test_01() {
        List<Term> segment = HanLP.segment(articleContent);
        System.out.println(segment);
    }

    // 关键词提取
    @Test
    public void test_02() {
        List<String> keywordList = HanLP.extractKeyword(articleContent, 5);
        System.out.println(keywordList);
    }

    // 自动摘要
    @Test
    public void test_03() {
        List<String> res = HanLP.extractSummary(articleContent, 3);
        for (String s : res) {
            System.out.println(s);
        }
    }

    // 测试分段 hanlpUtil & langchain4j
    @Test
    public void test_04() {
        List<String> lines = HanlpUtil.splitParagraphsHanLP(articleContent);
        System.out.println(lines);

        List<String> lines2 = Langchain4jUtil.splitParagraphsLangChain(articleContent);
        System.out.println(lines2);
    }

}
