package com.changlu.jikedesign.single.demo.demo1.good;

import java.io.IOException;

public class UserController {
    public UserController() throws IOException {
    }

    public void login(String username, String password) throws IOException {
        // doLogin逻辑
        Logger.getInstance().log(username + " logined");
    }

}
