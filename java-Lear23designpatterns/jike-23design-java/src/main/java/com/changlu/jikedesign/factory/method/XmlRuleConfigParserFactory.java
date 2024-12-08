package com.changlu.jikedesign.factory.method;

import com.changlu.jikedesign.factory.simple.IRuleConfigParser;
import com.changlu.jikedesign.factory.simple.JsonRuleParser;
import com.changlu.jikedesign.factory.simple.XmlRuleParser;

public class XmlRuleConfigParserFactory implements IRuleConfigParserFactory {

    @Override
    public IRuleConfigParser createParser() {
        return new XmlRuleParser();
    }
}
