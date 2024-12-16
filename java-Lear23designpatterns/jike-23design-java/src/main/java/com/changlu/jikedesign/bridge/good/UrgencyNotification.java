package com.changlu.jikedesign.bridge.good;

public class UrgencyNotification extends Notification{

    public UrgencyNotification(MsgSender msgSender) {
        super(msgSender);
    }

    @Override
    public void notify(String message) {
        msgSender.send(message);
    }
}
