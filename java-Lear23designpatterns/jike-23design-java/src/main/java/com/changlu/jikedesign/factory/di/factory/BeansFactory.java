package com.changlu.jikedesign.factory.di.factory;

import com.changlu.jikedesign.factory.di.pojo.BeanDefinition;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class BeansFactory {

    private ConcurrentHashMap<String, Object> singletonObjects = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, BeanDefinition> beanDefinitions = new ConcurrentHashMap<>();

    public void addBeanDefinitions(List<BeanDefinition> beanDefinitionList) {
        for (BeanDefinition beanDefinition : beanDefinitionList) {
            beanDefinitions.putIfAbsent(beanDefinition.getId(), beanDefinition);
        }

        for (BeanDefinition beanDefinition : beanDefinitionList) {
            // 非延迟加载 + 单例  => 创建bean实例
            if (beanDefinition.isLazyInit() == false && beanDefinition.isSingleton()) {
                createBean(beanDefinition);
            }
        }
    }

    /**
     * 创建bean实例
     * @param beanDefinition bean定义对象
     * @return
     */
    protected Object createBean(BeanDefinition beanDefinition) {
        // check下：单例 & 当前已创建该key的对象实例  => 直接返回创建了的对象
        if (beanDefinition.isSingleton() && singletonObjects.containsKey(beanDefinition.getId())) {
            return singletonObjects.get(beanDefinition.getId());
        }

        Object bean = null;
        try {
            Class<?> beanClass = Class.forName(beanDefinition.getClassName());
            List<BeanDefinition.ConstructorArg> args = beanDefinition.getConstructorArgs();
            if (args.isEmpty()) {
                bean = beanClass.newInstance();
            }else {
                // 通过构造器参数来创建实例
                Class[] argClasses = new Class[args.size()];
                Object[] argObjects = new Object[args.size()];
                for (int i = 0; i < args.size(); i++) {
                    BeanDefinition.ConstructorArg arg = args.get(i);
                    // 参数并非引用类型
                    if (!arg.isRef()) {
                        argClasses[i] = arg.getType();
                        argObjects[i] = arg.getArg();
                    }else {
                        BeanDefinition refBeanDefinition = beanDefinitions.get(arg.getArg());
                        if (refBeanDefinition == null) {
                            throw new RuntimeException("Bean is not Defined");
                        }
                        argClasses[i] = Class.forName(refBeanDefinition.getClassName());
                        argObjects[i] = createBean(refBeanDefinition); // 创建引用类型对象实例
                    }
                }
                bean = beanClass.getConstructor(argClasses).newInstance(argObjects);
            }
        }catch (ClassNotFoundException |  InvocationTargetException  | InstantiationException |
                IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException("", e);
        }

        if (bean != null && beanDefinition.isSingleton()) {
            singletonObjects.putIfAbsent(beanDefinition.getId(), bean);
            return singletonObjects.get(beanDefinition.getId());
        }
        return bean;
    }

    /**
     * 获取bean实例
     * @param beanId
     * @return
     */
    public Object getBean(String beanId) {
        BeanDefinition beanDefinition = beanDefinitions.get(beanId);
        if (beanDefinition == null) {
            throw new RuntimeException("Bean is not defined: " + beanId);
        }
        return createBean(beanDefinition);
    }

}
