package com.changlu.jikedesign.factory.di.parser;

import com.changlu.jikedesign.factory.di.pojo.BeanDefinition;

import java.io.InputStream;
import java.util.List;

public interface BeanConfigParser {

    List<BeanDefinition> parse(InputStream inputStream);

    List<BeanDefinition> parse(String configContent);

}
