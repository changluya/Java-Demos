package com.changlu;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Integer> rs = new ArrayList<>();
        rs.add(1);
        rs.add(9);
        if (CollectionUtils.isEmpty(rs) ){
            return;
        }
        // 过滤check处理
        // 资产：没有项目概念，之前质量创建项目没有sku限制
        rs.removeIf(appType -> new Integer(9).equals(appType) || new Integer(1).equals(appType));
        System.out.println(rs);
    }
}