package com.changlu.springbootconfig.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.stereotype.Component;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Component
public class ConfigFileReloader {

    private final ConfigurableEnvironment environment;

    @Autowired
    public ConfigFileReloader(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    /**
     * 重新加载配置文件
     *
     * @throws IOException 如果文件读取失败
     */
    public void reloadConfig() throws IOException {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(System.getProperty("user.dir.conf") + "/application.properties")) {
            properties.load(fis);
        }

        MutablePropertySources propertySources = environment.getPropertySources();
        propertySources.addFirst(new PropertiesPropertySource("dynamicConfig", properties));
    }
}