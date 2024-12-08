package com.changlu.jikedesign.factory.method;

import com.changlu.jikedesign.factory.simple.IRuleConfigParser;
import com.changlu.jikedesign.factory.simple.JsonRuleParser;

public class JsonRuleConfigParserFactory implements IRuleConfigParserFactory {

    @Override
    public IRuleConfigParser createParser() {
        return new JsonRuleParser();
    }
}
