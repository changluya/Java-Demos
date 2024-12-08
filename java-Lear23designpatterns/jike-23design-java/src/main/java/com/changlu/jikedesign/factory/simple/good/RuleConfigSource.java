package com.changlu.jikedesign.factory.simple.good;

import com.changlu.jikedesign.factory.simple.IRuleConfigParser;
import com.changlu.jikedesign.factory.simple.JsonRuleParser;
import com.changlu.jikedesign.factory.simple.RuleConfig;
import com.changlu.jikedesign.factory.simple.YamlRuleParser;

public class RuleConfigSource {

    public RuleConfig load(String ruleConfigPath) {
        String ruleConfigFileExtension = getFileExtension(ruleConfigPath);
        // 根据类型来选择解析器
        IRuleConfigParser parser = RuleConfigParserFactory.createParser(ruleConfigFileExtension);
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
