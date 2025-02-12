package com.changlu.classloader.demo2;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;

public class DiyDemo2ClassLoader extends URLClassLoader {

    // 父类加载器（作为双亲委派的起点）
    private ClassLoader parent;

    public DiyDemo2ClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
        this.parent = parent;
    }

    // findClass直接使用urlClassLoader即可，父类已实现加载逻辑

    // 模仿ClassLoader#loadClass
    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            Class<?> clazz = null;
            // 步骤1：检查是否已加载
            clazz = findLoadedClass(name);
            if (clazz != null) {
                if (resolve) {
                    resolveClass(clazz);
                }
                return (clazz);
            }
            // 步骤2：尝试自己加载（打破双亲委派）
            try {
                clazz = findClass(name);
                if (clazz != null) {
                    if (resolve) {
                        resolveClass(clazz);
                    }
                    return (clazz);
                }
            } catch (ClassNotFoundException e) {
                // 忽略，继续委托父类
            }

            // 步骤3：委托父类加载 （兼容自定义jar包provided情况）
            // 如果自己没有加载到，则以指定的parent作为类加载器来尝试进行加载
            // 【指定了 `parent`，则使用该加载器加载类，如果为 `null`，则使用引导类加载器。】
            try {
                clazz = Class.forName(name, false, parent);// 使用当前类的类加载器加载类，但不执行静态初始化块
                if (clazz != null) {
                    if (resolve) {
                        resolveClass(clazz);
                    }
                    return (clazz);
                }
            } catch (ClassNotFoundException e) {
                // 忽略，最终抛出异常
            }
        }
        throw new ClassNotFoundException(name);
    }


    // 优先从当前类加载器的路径加载资源，失败后委托父类
    @Override
    public URL getResource(String name) {
        // 步骤1：检查是否已加载
        URL url = null;
        url = findResource(name);
        if (url != null) {
            return (url);
        }
        // 步骤2：父类进行加载
        url = parent.getResource(name);
        if (url != null) {
            return (url);
        }
        return (null);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        // 优先使用当前类加载器的资源
        Enumeration<URL> resources = findResources(name);
        if (resources.hasMoreElements()) {
            return resources;
        }
        // 子类找不到资源时调用父类
        return super.getResources(name);
    }

    // 合并所有资源路径（包括动态添加的外部仓库）
    @Override
    public Enumeration<URL> findResources(String name) throws IOException {
        LinkedHashSet<URL> result = new LinkedHashSet<>();
        // 合并当前类加载器的资源（包括动态添加的外部仓库）
        Enumeration<URL> superResource = super.findResources(name);
        while (superResource.hasMoreElements()) {
            result.add(superResource.nextElement());
        }
        return Collections.enumeration(result);
    }


}
