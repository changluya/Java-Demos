package com.changlu.jikedesign.factory.simple.bad;

import com.changlu.jikedesign.factory.simple.IRuleConfigParser;
import com.changlu.jikedesign.factory.simple.JsonRuleParser;
import com.changlu.jikedesign.factory.simple.RuleConfig;
import com.changlu.jikedesign.factory.simple.YamlRuleParser;

public class RuleConfigSource {

    public RuleConfig load(String ruleConfigPath) {
        // 根据类型选择策略
        String ruleConfigFileExtension = getFileExtension(ruleConfigPath);
        IRuleConfigParser ruleConfigParser = null;
        if ("json".equals(ruleConfigFileExtension)) {
            ruleConfigParser = new JsonRuleParser();
        }else if ("xml".equals(ruleConfigFileExtension)) {
            ruleConfigParser = new JsonRuleParser();
        }else if ("yaml".equals(ruleConfigFileExtension)) {
            ruleConfigParser = new YamlRuleParser();
        }else {
            throw new RuntimeException("no support type:" + ruleConfigFileExtension);
        }
        // 文本内容
        String content = "";
        // 根据策略进行解析
        RuleConfig ruleConfig = ruleConfigParser.parse(content);
        return ruleConfig;
    }

    // 获取文件路径的文件类型
    public String getFileExtension(String filePath) {
        return "json";
    }
}
