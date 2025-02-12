package com.changlu.classloader.demo2;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class TestClassLoaderDemo2 {

    public static void main(String[] args) throws Exception{
//        test01();
//        test02();
        test03();
    }

    // URLClassLoader demo，默认第二个参数不填写就是appclassloader
    // 效果：由于走双亲委派，会加载appclassloader中的DiyMain
    public static void test01() throws Exception{
        // URLClassLoader（走双亲委派，第二个参数不填父类加载器为appclassloader）
        URLClassLoader loader = new URLClassLoader(new URL[]{new File("/Users/edy/changlu_workspace/mymd/demo-exer/java-jvm/classloader-jar-demo/target/classloader-jar-demo-1.0-SNAPSHOT-jar-with-dependencies.jar").toURI().toURL()});
        Class<?> clazz = loader.loadClass("com.changlu.DiyMain");
        System.out.println(clazz.getConstructor().newInstance());
    }

    // 自定义classloader 测试对于同类路径，优先读取自定义jar包中的类
    public static void test02() throws Exception{
        // 第二个参数设置为null，parent就是bootstrapclassloader
        DiyDemo2ClassLoader loader = new DiyDemo2ClassLoader(new URL[]{new File("/Users/edy/changlu_workspace/mymd/demo-exer/java-jvm/classloader-jar-demo/target/classloader-jar-demo-1.0-SNAPSHOT-jar-with-dependencies.jar").toURI().toURL()}
        , null);
        // 自定义jar包、当前应用中都有同名com.changlu.DiyMain。效果：优先加载自定义jar包中
        Class<?> clazz = loader.loadClass("com.changlu.DiyMain");
        System.out.println(clazz.getConstructor().newInstance());

        // 仅仅只有当前应用中有com.changlu.DiyMain2，而初始化DiyDemo2ClassLoader的第二个参数是null，一旦子jar包加载不到，会尝试使用bootstrapclassloader，默认还是加载不到
        // 效果：出现class not found
        Class<?> clazz2 = loader.loadClass("com.changlu.DiyMain2");
        System.out.println(clazz2.getConstructor().newInstance());
    }

    // 自定义classloader 测试对于同类路径，优先读取自定义jar包中的类
    public static void test03() throws Exception{
        // 第二个参数设置为所在线程的类加载器，默认就是AppClassLoader
        DiyDemo2ClassLoader loader = new DiyDemo2ClassLoader(new URL[]{new File("/Users/edy/changlu_workspace/mymd/demo-exer/java-jvm/classloader-jar-demo/target/classloader-jar-demo-1.0-SNAPSHOT-jar-with-dependencies.jar").toURI().toURL()}
                , Thread.currentThread().getContextClassLoader());
        // 自定义jar包、当前应用中都有同名com.changlu.DiyMain。效果：优先加载自定义jar包中
        Class<?> clazz = loader.loadClass("com.changlu.DiyMain");
        System.out.println(clazz.getConstructor().newInstance());

        // 仅仅只有当前应用中有com.changlu.DiyMain2，而初始化DiyDemo2ClassLoader的第二个参数是AppClassLoader，子类加载器加载不到，会使用parent来进行加载
        // 效果：使用parent appclassloader可以完成加载
        Class<?> clazz2 = loader.loadClass("com.changlu.DiyMain2");
        System.out.println(clazz2.getConstructor().newInstance());
    }

}
