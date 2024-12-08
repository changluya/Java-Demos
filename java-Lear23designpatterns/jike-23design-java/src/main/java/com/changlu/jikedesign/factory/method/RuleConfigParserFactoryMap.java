package com.changlu.jikedesign.factory.method;

import java.util.HashMap;
import java.util.Map;

public class RuleConfigParserFactoryMap {

    private static Map<String, IRuleConfigParserFactory> cachedFactories = new HashMap<>();

    static {
        cachedFactories.put("json", new JsonRuleConfigParserFactory());
        cachedFactories.put("xml", new XmlRuleConfigParserFactory());
        cachedFactories.put("yaml", new YamlRuleConfigParserFactory());
    }

    public static IRuleConfigParserFactory getParserFactory(String type) {
        IRuleConfigParserFactory ruleConfigParserFactory = cachedFactories.get(type);
        if (ruleConfigParserFactory == null) {
            throw new RuntimeException("not ParserFactory support type: " + type);
        }
        return ruleConfigParserFactory;
    }

}
