package com.changlu;

import io.github.melin.superior.common.relational.Statement;
import io.github.melin.superior.parser.starrocks.StarRocksHelper;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Statement statement = StarRocksHelper.parseStatement("select * from changlu");
        System.out.println(statement);
    }
}