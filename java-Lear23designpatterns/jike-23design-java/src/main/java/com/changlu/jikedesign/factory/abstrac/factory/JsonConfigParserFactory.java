package com.changlu.jikedesign.factory.abstrac.factory;

import com.changlu.jikedesign.factory.abstrac.parser.system.ISystemConfigParser;
import com.changlu.jikedesign.factory.abstrac.parser.system.JsonSystemConfigParser;
import com.changlu.jikedesign.factory.simple.IRuleConfigParser;
import com.changlu.jikedesign.factory.simple.JsonRuleParser;

public class JsonConfigParserFactory implements IConfigParserFactory {
    @Override
    public IRuleConfigParser createRuleParser() {
        return new JsonRuleParser();
    }

    @Override
    public ISystemConfigParser createSystemParser() {
        return new JsonSystemConfigParser();
    }
}
