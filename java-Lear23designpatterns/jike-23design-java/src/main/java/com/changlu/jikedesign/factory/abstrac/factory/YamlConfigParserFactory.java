package com.changlu.jikedesign.factory.abstrac.factory;

import com.changlu.jikedesign.factory.abstrac.parser.system.ISystemConfigParser;
import com.changlu.jikedesign.factory.abstrac.parser.rule.YamlRuleConfigParser;
import com.changlu.jikedesign.factory.abstrac.parser.system.YamlSystemConfigParser;
import com.changlu.jikedesign.factory.simple.IRuleConfigParser;

public class YamlConfigParserFactory implements IConfigParserFactory {
    @Override
    public IRuleConfigParser createRuleParser() {
        return new YamlRuleConfigParser();
    }

    @Override
    public ISystemConfigParser createSystemParser() {
        return new YamlSystemConfigParser();
    }
}