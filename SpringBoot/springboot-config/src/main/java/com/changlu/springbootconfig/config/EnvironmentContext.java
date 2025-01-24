package com.changlu.springbootconfig.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value = "file:${user.dir.conf}/application.properties")
public class EnvironmentContext {

    @Autowired
    private Environment environment;

    public String getProperty(String key, String defaultVal) {
        return environment.getProperty(key, defaultVal);
    }
}