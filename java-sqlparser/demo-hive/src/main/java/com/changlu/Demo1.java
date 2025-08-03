package com.changlu;

import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.ParseDriver;
import org.apache.hadoop.hive.ql.parse.ParseException;

/**
 * 使用hive来完成sql解析
 */
public class Demo1 {
    public static void main(String[] args) {
        ParseDriver parseDriver = new ParseDriver();
        String sql = "SELECT * \n" +
                "FROM batch63.sales_order_details \n" +
                "WHERE quantity IN (\n" +
                "    SELECT MAX(quantity) \n" +
                "    FROM batch63.sales_order_details\n" +
                ")";
        ASTNode root = null;
        try {
            // 解析sql为node语法树
            root = parseDriver.parse(sql);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        System.out.println(root);
    }
}