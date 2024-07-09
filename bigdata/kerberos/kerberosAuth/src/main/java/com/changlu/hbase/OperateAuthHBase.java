package com.changlu.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Java API 操作Kerberos 认证的HBase
 */
public class OperateAuthHBase {

    private static Configuration conf;
    private static Connection connection;
    private static Admin admin;

    /**
     * 初始化:进行kerberos认证并设置hbase configuration
     */
    static {
        try {
            String principal = "zhangsan@EXAMPLE.COM";
            String keytabPath = "E:\\自学历程\\Gitee仓库\\demo-exer\\bigdata\\kerberos\\kerberosAuth\\kerberosAuth\\src\\main\\resources\\zhangsan.keytab";

            conf = HBaseConfiguration.create();
            // 设置Kerberos 认证
            System.setProperty("java.security.krb5.conf", "E:\\自学历程\\Gitee仓库\\demo-exer\\bigdata\\kerberos\\kerberosAuth\\kerberosAuth\\src\\main\\resources\\krb5.conf");
            conf.set("hadoop.security.authentication", "kerberos");
            UserGroupInformation.setConfiguration(conf);
            UserGroupInformation.loginUserFromKeytab(principal, keytabPath);

            //设置zookeeper集群地址及端口号
            conf.set("hbase.zookeeper.quorum","node3,node4,node5");
            conf.set("hbase.zookeeper.property.clientPort","2181");

            //创建连接
            connection = ConnectionFactory.createConnection(conf);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) throws IOException {
        try{
            //获取Admin对象
            admin = connection.getAdmin();

            //创建表
            createHBaseTable("tbl1", "cf1");
            System.out.println("===========================");

            //查看Hbase中所有表
            listHBaseTables();
            System.out.println("===========================");

            //向表中插入数据
            putTableData("tbl1","row1", "cf1", "id", "100");
            putTableData("tbl1","row1", "cf1", "name", "zs");
            putTableData("tbl1","row1", "cf1", "age", "18");

            putTableData("tbl1","row2", "cf1", "id", "200");
            putTableData("tbl1","row2", "cf1", "name", "ls");
            putTableData("tbl1","row2", "cf1", "age", "19");
            System.out.println("===========================");

            //查询表中的数据
            scanTableData("tbl1");
            System.out.println("===========================");

            //查询指定row数据
            getRowData("tbl1", "row1","cf1","name");
            System.out.println("===========================");

            //删除指定row数据
            deleteRowData("tbl1", "row1","cf1","name");
            System.out.println("===========================");

            //查询表中的数据
            scanTableData("tbl1");
            System.out.println("===========================");

            //删除表
            deleteHBaseTable("tbl1");

            //查看Hbase中所有表
            listHBaseTables();
            System.out.println("===========================");
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(admin != null) {
                admin.close();
            }
            if(connection != null) {
                connection.close();
            }
        }

    }

    //判断表是否存在
    private static boolean isTableExist(String tableName) {
        try {
            return admin.tableExists(TableName.valueOf(tableName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void createHBaseTable(String tableName, String... cfs) {
        if(isTableExist(tableName)) {
            System.out.println("表已经存在！");
            return;
        }
        if(cfs == null || cfs.length == 0){
            System.out.println("列族不能为空！");
            return;
        }
        TableDescriptorBuilder tableDescriptorBuilder = TableDescriptorBuilder.newBuilder(TableName.valueOf(tableName));
        List<ColumnFamilyDescriptor> cFDBList = new ArrayList<>();
        for (String cf : cfs) {
            cFDBList.add(ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(cf)).build());
        }
        tableDescriptorBuilder.setColumnFamilies(cFDBList);
        try {
            admin.createTable(tableDescriptorBuilder.build());
            System.out.println("创建表" + tableName + "成功！");
        } catch (IOException e) {
            System.out.println("创建失败！");
            e.printStackTrace();
        }

    }

    //查看Hbase中所有表
    private static void listHBaseTables() {
        try {
            TableName[] tableNames = admin.listTableNames();
            System.out.println("打印所有命名空间表名：");
            for (TableName tableName : tableNames) {
                System.out.println(tableName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //向表中插入数据
    private static void putTableData(String tableName, String rowKey, String cf, String column, String value) {
        Table table = null;
        try {
            table = connection.getTable(TableName.valueOf(tableName));
            Put p = new Put(Bytes.toBytes(rowKey));
            p.addColumn(Bytes.toBytes(cf), Bytes.toBytes(column), Bytes.toBytes(value));
            table.put(p);
            System.out.println("插入数据成功！");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeTable(table);
        }


    }

    //查询表中的数据
    private static void scanTableData(String tableName) {
        if(tableName == null || tableName.length() == 0) {
            System.out.println("请正确输入表名！");
            return;
        }
        Table table = null;
        try {
            table = connection.getTable(TableName.valueOf(tableName));
            Scan scan = new Scan();

            ResultScanner resultScanner = table.getScanner(scan);
            Iterator<Result> iterator = resultScanner.iterator();
            while(iterator.hasNext()) {
                Result result = iterator.next();
                Cell[] cells = result.rawCells();
                for(Cell cell : cells) {
                    //得到rowkey
                    System.out.println("rowkey: " + Bytes.toString(CellUtil.cloneRow(cell)));
                    //得到列族
                    System.out.println("列族: " + Bytes.toString(CellUtil.cloneFamily(cell)));
                    System.out.println("列: " + Bytes.toString(CellUtil.cloneQualifier(cell)));
                    System.out.println("值: " + Bytes.toString(CellUtil.cloneValue(cell)));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeTable(table);
        }
    }

    //查询指定row数据
    private static void getRowData(String tableName, String rowkey,String colFamily, String col) {
        Table table = null;
        try {
            table = connection.getTable(TableName.valueOf(tableName));
            Get g = new Get(Bytes.toBytes(rowkey));
            // 获取指定列族数据
            if(col == null && colFamily != null) {
                g.addFamily(Bytes.toBytes(colFamily));
            } else if(col != null && colFamily != null) {
                // 获取指定列数据
                g.addColumn(Bytes.toBytes(colFamily), Bytes.toBytes(col));
            }
            Result result = table.get(g);
            System.out.println("查询指定rowkey 数据如下");

            for(Cell cell : result.rawCells()) {
                //得到rowkey
                System.out.println("rowkey: " + Bytes.toString(CellUtil.cloneRow(cell)));
                //得到列族
                System.out.println("列族: " + Bytes.toString(CellUtil.cloneFamily(cell)));
                System.out.println("列: " + Bytes.toString(CellUtil.cloneQualifier(cell)));
                System.out.println("值: " + Bytes.toString(CellUtil.cloneValue(cell)));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeTable(table);
        }

    }

    //删除指定row数据
    private static void deleteRowData(String tableName , String rowKey, String colFamily, String ... cols) {
        if(tableName == null || tableName.length() == 0) {
            System.out.println("请正确输入表名！");
            return;
        }
        Table table = null;
        try {
            table = connection.getTable(TableName.valueOf(tableName));
            Delete del = new Delete(Bytes.toBytes(rowKey));
            // 删除指定列族
            del.addFamily(Bytes.toBytes(colFamily));
            // 删除指定列
            for (String col : cols) {
                del.addColumn(Bytes.toBytes(colFamily), Bytes.toBytes(col));
            }
            table.delete(del);
            System.out.println("删除数据成功！");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeTable(table);
        }

    }

    //删除表
    private static void deleteHBaseTable(String tableName) {
        try {
            if(!isTableExist(tableName)) {
                System.out.println("要删除的表不存在！");
                return;
            }
            admin.disableTable(TableName.valueOf(tableName));
            admin.deleteTable(TableName.valueOf(tableName));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //关闭表
    private static void closeTable(Table table) {
        if(table != null) {
            try {
                table.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
