package com.changlu.jikedesign.single.demo.demo1.good;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {
    private static final Logger instance;
    private FileWriter writer;

    static {
        try {
            instance = new Logger();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 设置为私有，无法通过构造器创建对象实例
    private Logger() throws IOException {
        File file = new File("/users/changlu/log.txt");
        writer = new FileWriter(file, true);// true表示追加写入
    }

    public void log(String message) throws IOException {
        // 本身FileWriter就带有对象级别的锁
        writer.write(message);
    }

    public static Logger getInstance() {
        return instance;
    }

}
