package com.changlu.springbootconfig.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DynamicPropertyUpdater {

    private static final Logger logger = LoggerFactory.getLogger(DynamicPropertyUpdater.class);

    private final ConfigurableEnvironment environment;

    public DynamicPropertyUpdater(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    /**
     * 动态更新 Environment 中的配置项值
     *
     * @param key   配置项的键
     * @param value 配置项的新值
     */
    public void updateProperty(String key, String value) {
        logger.info("Updating property: {} = {}", key, value);
        MutablePropertySources propertySources = environment.getPropertySources();
        // 添加或更新属性
        propertySources.addFirst(new DynamicPropertySource(key, value));
    }

    /**
     * 批量动态更新 Environment 中的配置项值
     * @param properties 配置项
     */
    public void updateProperties(Map<String, String> properties) {
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            // 复用单独更新配置项逻辑
            this.updateProperty(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 自定义动态属性源
     */
    private static class DynamicPropertySource extends PropertySource<String> {

        private final String key;
        private final String value;

        public DynamicPropertySource(String key, String value) {
            super("dynamicPropertySource"); // 属性源的名称
            this.key = key;
            this.value = value;
        }

        @Override
        public Object getProperty(String name) {
            // 如果传入的 key 匹配，则返回对应的 value
            if (key.equals(name)) {
                return value;
            }
            return null; // 如果不匹配，返回 null
        }
    }
}