package com.changlu.jikedesign.builder.bad;

/**
 * 构造器参数校验
 */
public class ResourcePoolConfig {
    // 统一默认值
    private static final int DEFAULT_MAX_TOTAL = 8;
    private static final int DEFAULT_MAX_IDLE = 8;
    private static final int DEFAULT_MIN_IDLE = 8;

    // 可填参数
    private String name;
    private Integer maxTotal = DEFAULT_MAX_TOTAL;
    private Integer maxIdle = DEFAULT_MAX_IDLE;
    private Integer minIdle = DEFAULT_MIN_IDLE;

    public ResourcePoolConfig(String name, Integer maxTotal, Integer maxIdle, Integer minIdle) {
        // 名称必填，为空抛出异常
        if (name == null || "".equals(name.trim())) {
            throw new IllegalArgumentException("name should not be empty.");
        }
        // maxTotal 最大资源总数 非必填，默认为只为8
        if (maxTotal != null) {
            if (maxTotal <= 0) {
                throw new IllegalArgumentException("maxTotal should be positive.");
            }
            this.maxTotal = maxTotal;
        }
        // maxIdle 最大空闲资源数量：非必填，默认为只为8
        if (maxIdle != null) {
            if (maxIdle < 0) {
                throw new IllegalArgumentException("maxIdle should be positive.");
            }
            this.maxIdle = maxIdle;
        }
        // minIdle 最小空闲资源数量：非必填，默认为只为8
        if (minIdle != null) {
            if (minIdle < 0) {
                throw new IllegalArgumentException("min should be positive.");
            }
            this.minIdle = minIdle;
        }
    }

}
