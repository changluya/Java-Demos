package com.changlu.jikedesign.single.demo.demo1.bad;

import java.io.IOException;

public class OrderController {

    private Logger logger = new Logger();

    public OrderController() throws IOException {
    }

    public void createOrder (String productName) throws IOException {
        // doCreate
        logger.log(productName + " create order !");
    }

}
