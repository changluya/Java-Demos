package com.changlu.jikedesign.bridge.good;

public class NormalNotification extends Notification{

    public NormalNotification(MsgSender msgSender) {
        super(msgSender);
    }

    @Override
    public void notify(String message) {
        msgSender.send(message);
    }
}
