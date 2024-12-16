package com.changlu.jikedesign.bridge;

import com.changlu.jikedesign.bridge.bad.Notification;
import com.changlu.jikedesign.bridge.bad.NotificationEmergencyLevel;

public class ErrorAlertHandler extends AlertHandler{

    public ErrorAlertHandler(AlertRule rule, Notification notification) {
        super(rule, notification);
    }

    @Override
    public void check() {
        // 条件符合
        notification.notify(NotificationEmergencyLevel.SEVERE, "xxx");
    }
}
