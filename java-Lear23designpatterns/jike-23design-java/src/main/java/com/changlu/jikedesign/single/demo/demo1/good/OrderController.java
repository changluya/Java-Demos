package com.changlu.jikedesign.single.demo.demo1.good;

import java.io.IOException;

public class OrderController {

    public OrderController() throws IOException {
    }

    public void createOrder (String productName) throws IOException {
        // doCreate
        Logger.getInstance().log(productName + " create order !");
    }

}
