package com.changlu.jikedesign.builder.good;

/**
 * 建造者模式
 */
public class ResourcePoolConfig {

    // 可填参数
    private String name;
    private Integer maxTotal;
    private Integer maxIdle;
    private Integer minIdle;

    // 步骤三：构造参数设置为私有属性
    private ResourcePoolConfig(Builder builder) {
        this.name = builder.name;
        this.maxIdle = builder.maxIdle;
        this.maxTotal= builder.maxTotal;
        this.minIdle = builder.minIdle;
    }

    // 步骤一：初始化参数 + set校验 + build最终多参数校验逻辑
    public static class Builder {
        // 统一默认值
        private static final int DEFAULT_MAX_TOTAL = 8;
        private static final int DEFAULT_MAX_IDLE = 8;
        private static final int DEFAULT_MIN_IDLE = 8;

        private String name;
        private Integer maxTotal = DEFAULT_MAX_TOTAL;
        private Integer maxIdle = DEFAULT_MAX_IDLE;
        private Integer minIdle = DEFAULT_MIN_IDLE;

        /**
         * 步骤二：统一全局参数条件判断，最终构建目标配置项
         * @return
         */
        public ResourcePoolConfig build() {
            // 1、name参数不为空
            if (name == null || "".equals(name.trim())) {
                throw new IllegalArgumentException("name should not be empty.");
            }
            // 2、最大空闲资源数不能 > 最大资源总数
            if (maxIdle > maxTotal) {
                throw new IllegalArgumentException("maxIdle should not bigger than maxTotal.");
            }
            // 3、最小空闲资源数不能 > 最大资源总数 | 最小空闲资源数不能 > 最大空闲资源数
            if (minIdle > maxTotal || minIdle > maxIdle) {
                throw new IllegalArgumentException("minIdle should not bigger than maxTotal or maxIdle.");
            }
            return new ResourcePoolConfig(this);
        }

        public Builder setName(String name) {
            // 名称必填，为空抛出异常
            if (name == null || "".equals(name.trim())) {
                throw new IllegalArgumentException("name should not be empty.");
            }
            this.name = name;
            return this;
        }

        public Builder setMaxTotal(int maxTotal) {
            // maxTotal 最大资源总数 非必填，默认为只为8
            if (maxTotal <= 0) {
                throw new IllegalArgumentException("maxTotal should be positive.");
            }
            this.maxTotal = maxTotal;
            return this;
        }

        public Builder setMaxIdle(int maxIdle) {
            // maxIdle 最大空闲资源数量：非必填，默认为只为8
            if (maxIdle < 0) {
                throw new IllegalArgumentException("maxIdle should be positive.");
            }
            this.maxIdle = maxIdle;
            return this;
        }

        public Builder setMinIdle(int minIdle) {
            // minIdle 最小空闲资源数量：非必填，默认为只为8
            if (minIdle < 0) {
                throw new IllegalArgumentException("min should be positive.");
            }
            this.minIdle = minIdle;
            return this;
        }

    }

    public static void main(String[] args) {
        ResourcePoolConfig resourcePoolConfig = new Builder()
                .setName("changlu_thread_pool")
                .setMaxTotal(10)
                .setMaxIdle(4)
                .setMinIdle(6).build();
        System.out.println(resourcePoolConfig);
    }


}
