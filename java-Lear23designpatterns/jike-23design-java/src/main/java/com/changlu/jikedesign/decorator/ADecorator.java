package com.changlu.jikedesign.decorator;

import java.io.BufferedInputStream;

public class ADecorator implements IA{

    private IA a;

    private ADecorator(IA a) {
        this.a = a;
    }

    @Override
    public void f() {
        // 前置逻辑
        a.f();
        // 后置逻辑
    }
}
