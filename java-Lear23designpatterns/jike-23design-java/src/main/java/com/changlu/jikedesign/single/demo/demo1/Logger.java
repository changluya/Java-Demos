package com.changlu.jikedesign.single.demo.demo1;

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
        writer.write(message);
    }

}
