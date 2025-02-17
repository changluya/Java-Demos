package com.changlu.springbootconfig;

import com.changlu.springbootconfig.utils.SystemPropertyUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringbootConfigApplication {

    public static void main(String[] args) {
        SystemPropertyUtil.setSystemUserDir();
        SpringApplication.run(SpringbootConfigApplication.class, args);
    }

}
