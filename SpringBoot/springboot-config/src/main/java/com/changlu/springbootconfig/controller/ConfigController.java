package com.changlu.springbootconfig.controller;


import com.changlu.springbootconfig.config.ConfigFileReloader;
import com.changlu.springbootconfig.config.DynamicPropertyUpdater;
import com.changlu.springbootconfig.config.EnvironmentContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/config")
public class ConfigController {

    @Autowired
    private DynamicPropertyUpdater dynamicPropertyUpdater;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ConfigFileReloader configFileReloader;

    /**
     * 动态更新配置项的值
     *
     * @param key   配置项的键
     * @param value 配置项的新值
     * @return 更新结果
     */
    @PostMapping("/update")
    public String updateConfig(@RequestParam String key, @RequestParam String value) {
        dynamicPropertyUpdater.updateProperty(key, value);
        return "Config updated successfully: " + key + " = " + value;
    }

    /**
     * 根据 key 获取配置项的值
     *
     * @param key 配置项的键
     * @return 配置项的值
     */
    @GetMapping("/get")
    public String getConfig(@RequestParam String key) {
        String value = environmentContext.getProperty(key, "未找到对应的配置项");
        return key + " = " + value;
    }

    /**
     * 刷新配置文件
     *
     * @return 配置项的值
     */
    @GetMapping("/refresh")
    public String refreshConfig() throws IOException {
        configFileReloader.reloadConfig();
        return "success";
    }


}