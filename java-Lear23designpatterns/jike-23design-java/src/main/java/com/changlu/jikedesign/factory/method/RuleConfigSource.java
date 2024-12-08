package com.changlu.jikedesign.factory.method;

import com.changlu.jikedesign.factory.simple.IRuleConfigParser;
import com.changlu.jikedesign.factory.simple.RuleConfig;
import com.changlu.jikedesign.factory.simple.good.RuleConfigParserFactory;

public class RuleConfigSource {

    public RuleConfig load(String ruleConfigPath) {
        String ruleConfigFileExtension = getFileExtension(ruleConfigPath);
        // 创建工厂类
        // 方式一：通过if else选择工厂类
//        IRuleConfigParserFactory ruleConfigParserFactory = null;
//        if ("json".equals(ruleConfigFileExtension)) {
//            ruleConfigParserFactory = new JsonRuleConfigParserFactory();
//        }else if ("xml".equals(ruleConfigFileExtension)) {
//            ruleConfigParserFactory = new XmlRuleConfigParserFactory();
//        }else if ("yaml".equals(ruleConfigFileExtension)) {
//            ruleConfigParserFactory = new YamlRuleConfigParserFactory();
//        }else {
//            throw new RuntimeException("not support type: " + ruleConfigFileExtension);
//        }
        // 方式二：根据类型选择指定的解析器工厂类
        IRuleConfigParserFactory ruleConfigParserFactory = RuleConfigParserFactoryMap.getParserFactory(ruleConfigFileExtension);
        // 通过指定类型的工厂类来创建Parser解析器
        IRuleConfigParser parser = ruleConfigParserFactory.createParser();
        String content = "";
        // 根据策略进行解析
        RuleConfig ruleConfig = parser.parse(content);
        return ruleConfig;
    }

    // 获取文件路径的文件类型
    public String getFileExtension(String filePath) {
        return "json";
    }
}
