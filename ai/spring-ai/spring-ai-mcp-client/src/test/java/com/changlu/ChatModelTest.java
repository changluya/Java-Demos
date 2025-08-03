package com.changlu;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChatModelTest {

    @Autowired
    private ChatModel chatModel;

}
