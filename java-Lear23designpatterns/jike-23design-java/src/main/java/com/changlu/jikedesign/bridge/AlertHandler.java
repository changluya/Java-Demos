package com.changlu.jikedesign.bridge;

import com.changlu.jikedesign.bridge.bad.Notification;

public class AlertHandler {

    private AlertRule rule;
    public Notification notification;

    public AlertHandler(AlertRule rule, Notification notification) {
        this.rule = rule;
        this.notification = notification;
    }

    public void check() {
        // 条件
    }

}
