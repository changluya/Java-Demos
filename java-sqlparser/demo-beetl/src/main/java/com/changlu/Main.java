package com.changlu;


import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.StringTemplateResourceLoader;

import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        try {
            // 创建资源加载器，加载模板内容
            StringTemplateResourceLoader resourceLoader = new StringTemplateResourceLoader();

            // 创建配置对象
            Configuration cfg = Configuration.defaultConfiguration();

            // 创建模板组
            GroupTemplate gt = new GroupTemplate(resourceLoader, cfg);

            // 加载模板
            String content = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "    <title>${title!}</title>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <h1>${message!}</h1>\n" +
                    "    <p>当前时间：${currentTime!}</p>\n" +
                    "</body>\n" +
                    "</html>";
            Template t = gt.getTemplate(content);

            // 创建变量绑定
            Map<String, Object> replaceParamMap = new HashMap<>();
            replaceParamMap.put("title", "Beetl 示例");
            replaceParamMap.put("message", "欢迎使用 Beetl 模板引擎！");
            replaceParamMap.put("currentTime", java.time.LocalDateTime.now().toString());

            // 绑定变量
            t.binding(replaceParamMap);

            // 渲染模板
            String result = t.render();

            // 输出结果
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
