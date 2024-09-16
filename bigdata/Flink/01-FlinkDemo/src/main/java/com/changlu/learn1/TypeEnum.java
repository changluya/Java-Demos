package com.changlu.learn1;

public enum TypeEnum {

    OCEANBASE(35, "Oceanbase for MySQL"),
    OCEANBASE_FOR_ORACLE(48, "OceanBase For Oracle");

    private Integer type;
    private String name;

    TypeEnum(Integer type, String name){
        this.type = type;
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
