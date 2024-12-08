package com.changlu.jikedesign.factory.di.parser;

import com.changlu.jikedesign.factory.di.pojo.BeanDefinition;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class XmlBeanConfigParser implements BeanConfigParser{
    @Override
    public List<BeanDefinition> parse(InputStream inputStream) {
        String content = null;
        // todo
        return parse(content);
    }

    @Override
    public List<BeanDefinition> parse(String configContent) {
        List<BeanDefinition> beanDefinitions = new ArrayList<>();
        // todo
        return beanDefinitions;
    }
}
