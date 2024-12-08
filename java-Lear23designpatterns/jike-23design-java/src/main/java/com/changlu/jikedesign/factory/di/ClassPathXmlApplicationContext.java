package com.changlu.jikedesign.factory.di;

import com.changlu.jikedesign.factory.di.factory.BeansFactory;
import com.changlu.jikedesign.factory.di.parser.BeanConfigParser;
import com.changlu.jikedesign.factory.di.parser.XmlBeanConfigParser;
import com.changlu.jikedesign.factory.di.pojo.BeanDefinition;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ClassPathXmlApplicationContext implements ApplicationContext{

    private BeansFactory beansFactory;
    private BeanConfigParser beanConfigParser;

    public ClassPathXmlApplicationContext (String configLocation) {
        this.beansFactory = new BeansFactory();
        this.beanConfigParser = new XmlBeanConfigParser();
        loadBeanDefinitions(configLocation);
    }

    private void loadBeanDefinitions(String configLocation) {
        InputStream in = null;
        try {
            in = this.getClass().getResourceAsStream("/" + configLocation);
            if (in == null) {
                throw new RuntimeException("Can not find config file: " + configLocation);
            }
            List<BeanDefinition> beanDefinitions = beanConfigParser.parse(in);
            beansFactory.addBeanDefinitions(beanDefinitions);
        }finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // todo
                }
            }
        }
    }

    @Override
    public Object getBean(String beanId) {
        return beansFactory.getBean(beanId);
    }
}
