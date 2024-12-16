package com.changlu.jikedesign.bridge.bad;

import java.util.List;

/**
 * 通知类
 */
public class Notification {

    private List<String> emailAddresses;
    private List<String> telephones;
    private List<String> wechatIds;

    public Notification() {}

    public void notify(NotificationEmergencyLevel level, String message) {
        if (level.equals(NotificationEmergencyLevel.SEVERE)) {
            // 自动语音电话
        }else if (level.equals(NotificationEmergencyLevel.URGENCY)) {
            // 发微信
        }else if (level.equals(NotificationEmergencyLevel.NORMAL)) {
            // 发邮件
        }else if (level.equals(NotificationEmergencyLevel.TRIVIAL)) {
            // 发邮件
        }
    }

    public void setEmailAddresses(List<String> emailAddresses) {
        this.emailAddresses = emailAddresses;
    }

    public void setTelephones(List<String> telephones) {
        this.telephones = telephones;
    }

    public void setWechatIds(List<String> wechatIds) {
        this.wechatIds = wechatIds;
    }
}
