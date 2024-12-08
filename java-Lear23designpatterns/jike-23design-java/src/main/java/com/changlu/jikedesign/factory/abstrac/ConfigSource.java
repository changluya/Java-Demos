package com.changlu.jikedesign.factory.abstrac;

import com.changlu.jikedesign.factory.abstrac.factory.IConfigParserFactory;
import com.changlu.jikedesign.factory.abstrac.parser.system.ISystemConfigParser;
import com.changlu.jikedesign.factory.simple.IRuleConfigParser;
import com.changlu.jikedesign.factory.simple.RuleConfig;

public class ConfigSource {

    public RuleConfig load(String ruleConfigPath) {
        // 解析文件路径为什么类型的文件
        String ruleConfigFileExtension = getFileExtension(ruleConfigPath);
        IConfigParserFactory configParserFactory = ConfigParserFactoryMap.getConfigParserFactory(ruleConfigFileExtension);
        // 通过抽象工厂来创建rule规则解析器 & system配置解析器
        IRuleConfigParser ruleParser = configParserFactory.createRuleParser();
        ISystemConfigParser systemParser = configParserFactory.createSystemParser();
        String content = "";
        // 根据策略进行解析
        RuleConfig ruleConfig = ruleParser.parse(content);
        return ruleConfig;
    }

    // 获取文件路径的文件类型
    public String getFileExtension(String filePath) {
        return "json";
    }

}
