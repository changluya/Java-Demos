package com.changlu.jikedesign.factory.abstrac.factory;

import com.changlu.jikedesign.factory.abstrac.parser.system.ISystemConfigParser;
import com.changlu.jikedesign.factory.simple.IRuleConfigParser;

public interface IConfigParserFactory {

    IRuleConfigParser createRuleParser();

    ISystemConfigParser createSystemParser();

}
