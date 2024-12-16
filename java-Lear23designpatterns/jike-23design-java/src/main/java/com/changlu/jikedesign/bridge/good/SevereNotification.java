package com.changlu.jikedesign.bridge.good;

public class SevereNotification extends Notification{

    public SevereNotification(MsgSender msgSender) {
        super(msgSender);
    }

    @Override
    public void notify(String message) {
        msgSender.send(message);
    }
}
