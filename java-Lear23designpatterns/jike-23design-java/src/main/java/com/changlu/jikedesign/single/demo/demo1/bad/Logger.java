package com.changlu.jikedesign.single.demo.demo1.bad;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {

    private FileWriter writer;

    public Logger() throws IOException {
        File file = new File("/users/changlu/log.txt");
        writer = new FileWriter(file, true);// true表示追加写入
    }

    public void log(String message) throws IOException {
        // 本身FileWriter就带有对象级别的锁
        writer.write(message);
    }

}
