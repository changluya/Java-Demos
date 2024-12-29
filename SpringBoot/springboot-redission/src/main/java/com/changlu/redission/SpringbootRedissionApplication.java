package com.changlu.redission;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class SpringbootRedissionApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootRedissionApplication.class, args);
    }


}
