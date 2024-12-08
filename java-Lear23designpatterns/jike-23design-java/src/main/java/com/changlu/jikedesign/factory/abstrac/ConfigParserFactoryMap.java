package com.changlu.jikedesign.factory.abstrac;

import com.changlu.jikedesign.factory.abstrac.factory.IConfigParserFactory;
import com.changlu.jikedesign.factory.abstrac.factory.JsonConfigParserFactory;
import com.changlu.jikedesign.factory.abstrac.factory.XmlConfigParserFactory;
import com.changlu.jikedesign.factory.abstrac.factory.YamlConfigParserFactory;

import java.util.HashMap;
import java.util.Map;

public class ConfigParserFactoryMap {

    private static Map<String, IConfigParserFactory> cachedFactories = new HashMap<>();

    static {
        cachedFactories.put("json", new JsonConfigParserFactory());
        cachedFactories.put("xml", new XmlConfigParserFactory());
        cachedFactories.put("yaml", new YamlConfigParserFactory());
    }

    public static IConfigParserFactory getConfigParserFactory(String type) {
        IConfigParserFactory iConfigParserFactory = cachedFactories.get(type);
        if (iConfigParserFactory == null) {
            throw new RuntimeException("no configParserFactory support type: " + type);
        }
        return iConfigParserFactory;
    }

}
