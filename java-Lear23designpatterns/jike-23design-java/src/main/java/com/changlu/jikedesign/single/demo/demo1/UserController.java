package com.changlu.jikedesign.single.demo.demo1;

import java.io.IOException;

public class UserController {

    private Logger logger = new Logger();

    public UserController() throws IOException {
    }

    public void login(String username, String password) throws IOException {
        // doLogin逻辑
        logger.log(username + " logined");
    }

}
