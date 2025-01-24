[toc]



# 前言

在springboot服务启动之后，想要通过接口来动态修改配置文件参数，实现动态参数配置信息获取。

gitee：https://gitee.com/changluJava/demo-exer/tree/master/SpringBoot/springboot-config/src/main/java/com/changlu/springbootconfig

github：https://github.com/changluya/Java-Demos/tree/master/SpringBoot/springboot-config/src/main/java/com/changlu/springbootconfig

# 实现接口动态刷新配置参数

![image-20250123232945851](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202501232329503.png)

## 代码实现

### 1、DynamicPropertyUpdater（动态配置项更新器）

自定义DynamicPropertySource动态配置类，接着本质通过environment.getPropertySources()来获取到配置项属性器，后续更新配置项去构造DynamicPropertySource添加到属性器即可完成属性配置覆盖。

 ```java
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
 ```

---

### 2、EnvironmentContext（environment管理器）

```java
package com.changlu.springbootconfig.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class EnvironmentContext {

    @Autowired
    private Environment environment;

    public String getProperty(String key, String defaultVal) {
        return environment.getProperty(key, defaultVal);
    }
}
```

### 3、ConfigController 实现查询配置、更新配置接口

![image-20250123233747874](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202501232337393.png)

```java
import com.changlu.springbootconfig.config.DynamicPropertyUpdater;
import com.changlu.springbootconfig.config.EnvironmentContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/config")
public class ConfigController {

    @Autowired
    private DynamicPropertyUpdater dynamicPropertyUpdater;

    @Autowired
    private EnvironmentContext environmentContext;

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

}
```

## 测试

首先在application.properties配置文件中补充一个参数配置：

```properties
server.port=8080
changlu.param=parm1
```

**1）启动服务后，查询下参数信息：changlu.param**

![image-20250123234012131](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202501232340421.png)



正常获取到结果值parm1。

**2）接着我们调用update更新参数接口**

更新changlu.param参数值为：789

![image-20250123234204115](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202501232342241.png)

3）此时我们再次查询下该参数：

![image-20250123234257625](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202501232342723.png)

当前已经得到了最新的参数值为：789

---

# 实现刷新当前使用的配置文件

## 目的

在真实场景下，我们会直接上到服务器上修改配置文件参数，接着想要通过调用接口重新加载该配置文件中的所有参数配置信息。

动作：修改配置文件参数项。

发起：调用/refresh接口实现动态刷新。

## 代码实现

### 1、SystemPropertyUtil （实现自定义conf配置路径user.dir.conf ）

实际场景中，我们的配置文件不会直接打包在jar包中，通常会指定一个配置文件路径。

![image-20250123234911039](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202501232349275.png)

SystemPropertyUtil：

```java
import java.io.File;

public class SystemPropertyUtil {

    public static void setSystemUserDir() {
        String dir = System.getProperty("user.dir");
        String conf = String.format("%s/%s", dir, "conf");
        File file = new File(conf);
        if(!file.exists()) {
            dir = dir.substring(0, dir.lastIndexOf("/"));
            conf = String.format("%s/%s", dir, "conf");
            file = new File(conf);
            if(file.exists()) {
                System.setProperty("user.dir", dir);
            }
        }
        System.setProperty("user.dir.conf", System.getProperty("user.dir") + "/conf");
    }
}
```

接着在启动器服务运行前调用方法来设置全局变量参数user.dir.conf：

![image-20250123235050109](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202501232350246.png)

```java
SystemPropertyUtil.setSystemUserDir();
```

此时我们的配置文件在当前工程目录下的conf中：

![image-20250123235200056](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202501232352261.png)

配置参数值如下：

```properties
server.port=8756
changlu.test=444
```

### 2、ConfigFileReloader（实现配置文件加载器）

![image-20250123235332797](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202501232353877.png)

在加载器中，实现了一个方法会去读取user.dir.conf这个变量路径值，此时得到的是/conf的全路径，接着我们去补充配置文件名application.properties即可：

**ConfigFileReloader.java**：

```java
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
```

### 3、EnvironmentContext 指定默认读取配置文件路径参数

![image-20250123235826147](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202501232358535.png)

补充属性源读取路径信息：PropertySource注解中读取静态变量对应conf路径下application.properties

```java
@Component
@PropertySource(value = "file:${user.dir.conf}/application.properties")
public class EnvironmentContext {
```

### 4、实现刷新配置文件参数接口

![image-20250123235535739](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202501232355905.png)

```java
@Autowired
private ConfigFileReloader configFileReloader;

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
```

后续步骤就是，我们修改conf下的配置文件参数值，然后调用下生效参数接口即可刷新当前应用服务的配置信息。

---

## 测试

确认好配置信息：

![image-20250123235656206](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202501232356282.png)

接着我们启动服务，来调用下获取参数配置信息接口：正常读取到parm1

![image-20250123235939279](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202501232359339.png)

接着我们手动将配置文件参数修改：

![image-20250124000001117](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202501240000241.png)

接着调用刷新配置文件接口：

![image-20250124000035839](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202501240000938.png)

成功刷新，此时我们再去读取下该配置信息：

![image-20250124000109778](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202501240001881.png)

当前读取到的配置参数为parm2，刷新文件配置项生效！











---