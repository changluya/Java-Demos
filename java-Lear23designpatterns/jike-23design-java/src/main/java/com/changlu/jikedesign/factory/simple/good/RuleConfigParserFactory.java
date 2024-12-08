package com.changlu.jikedesign.factory.simple.good;

import com.changlu.jikedesign.factory.simple.IRuleConfigParser;
import com.changlu.jikedesign.factory.simple.JsonRuleParser;
import com.changlu.jikedesign.factory.simple.YamlRuleParser;

import java.util.HashMap;
import java.util.Map;

public class RuleConfigParserFactory {

    private static Map<String, IRuleConfigParser> cachedParsers = new HashMap<>();

    static {
        cachedParsers.put("json", new JsonRuleParser());
        cachedParsers.put("xml", new JsonRuleParser());
        cachedParsers.put("yaml", new YamlRuleParser());
    }

    // 获取到匹配的配置解析器
    public static IRuleConfigParser createParser(String ruleConfigFileExtension) {
        IRuleConfigParser ruleConfigParser = cachedParsers.get(ruleConfigFileExtension);
        if (ruleConfigParser == null) {
            throw new RuntimeException("no support type:" + ruleConfigFileExtension);
        }
        return ruleConfigParser;
    }


}
