package com.changlu.springbootaichat.env;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@Data
public class EnvironmentContext {

    @Autowired
    private Environment environment;


}
