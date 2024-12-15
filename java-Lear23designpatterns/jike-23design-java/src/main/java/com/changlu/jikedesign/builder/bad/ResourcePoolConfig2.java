package com.changlu.jikedesign.builder.bad;

/**
 * set参数校验
 */
public class ResourcePoolConfig2 {
    // 统一默认值
    private static final int DEFAULT_MAX_TOTAL = 8;
    private static final int DEFAULT_MAX_IDLE = 8;
    private static final int DEFAULT_MIN_IDLE = 8;

    // 可填参数
    private String name;
    private Integer maxTotal = DEFAULT_MAX_TOTAL;
    private Integer maxIdle = DEFAULT_MAX_IDLE;
    private Integer minIdle = DEFAULT_MIN_IDLE;

    public ResourcePoolConfig2(String name) {
        // 名称必填，为空抛出异常
        if (name == null || "".equals(name.trim())) {
            throw new IllegalArgumentException("name should not be empty.");
        }
        this.name = name;
    }

    public void setMaxTotal(Integer maxTotal) {
        // maxTotal 最大资源总数 非必填，默认为只为8
        if (maxTotal != null && maxTotal <= 0) {
            throw new IllegalArgumentException("maxTotal should be positive.");
        }
        this.maxTotal = maxTotal;
    }

    public void setMaxIdle(Integer maxIdle) {
        // maxIdle 最大空闲资源数量：非必填，默认为只为8
        if (maxIdle != null && maxIdle < 0) {
            throw new IllegalArgumentException("maxIdle should be positive.");
        }
        this.maxIdle = maxIdle;
    }

    public void setMinIdle(Integer minIdle) {
        // minIdle 最小空闲资源数量：非必填，默认为只为8
        if (minIdle != null && minIdle < 0) {
            throw new IllegalArgumentException("min should be positive.");
        }
        this.minIdle = minIdle;
    }
}
