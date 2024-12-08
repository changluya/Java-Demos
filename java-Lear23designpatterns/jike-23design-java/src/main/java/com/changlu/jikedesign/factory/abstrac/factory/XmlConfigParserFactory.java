package com.changlu.jikedesign.factory.abstrac.factory;

import com.changlu.jikedesign.factory.abstrac.parser.system.ISystemConfigParser;
import com.changlu.jikedesign.factory.abstrac.parser.rule.XmlRuleConfigParser;
import com.changlu.jikedesign.factory.abstrac.parser.system.XmlSystemConfigParser;
import com.changlu.jikedesign.factory.simple.IRuleConfigParser;

public class XmlConfigParserFactory implements IConfigParserFactory {
    @Override
    public IRuleConfigParser createRuleParser() {
        return new XmlRuleConfigParser();
    }

    @Override
    public ISystemConfigParser createSystemParser() {
        return new XmlSystemConfigParser();
    }
}