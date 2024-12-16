package com.changlu.jikedesign.bridge.good;

import java.util.List;

public class WechatMsgSender implements MsgSender{

    private List<String> telephones;

    public WechatMsgSender(List<String> telephones) {
        this.telephones = telephones;
    }

    @Override
    public void send(String message) {
        
    }
}
