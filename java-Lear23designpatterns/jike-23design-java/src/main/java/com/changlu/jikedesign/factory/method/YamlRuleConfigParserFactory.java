package com.changlu.jikedesign.factory.method;

import com.changlu.jikedesign.factory.simple.IRuleConfigParser;
import com.changlu.jikedesign.factory.simple.YamlRuleParser;

public class YamlRuleConfigParserFactory implements IRuleConfigParserFactory{
    @Override
    public IRuleConfigParser createParser() {
        return new YamlRuleParser();
    }
}
