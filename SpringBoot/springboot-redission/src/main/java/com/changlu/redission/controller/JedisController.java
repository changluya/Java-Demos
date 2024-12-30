package com.changlu.redission.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.commands.JedisCommands;
import redis.clients.jedis.params.SetParams;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/jedis")
public class JedisController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping("/key/{key}")
    public Map<String, String> redission2(@PathVariable("key")String key) {
        String execute = redisTemplate.execute((RedisCallback<String>) connection -> {
            JedisCommands commands = (JedisCommands) connection.getNativeConnection();
            SetParams setParams = SetParams.setParams();
            setParams.nx().ex(100);
            return commands.set(key, "-1", setParams);
        });
        String execute2 = redisTemplate.execute((RedisCallback<String>) connection -> {
            JedisCommands commands = (JedisCommands) connection.getNativeConnection();
            SetParams setParams = SetParams.setParams();
            setParams.nx().ex(100);
            return commands.set(key, "-2", setParams);
        });
        HashMap<String, String> result = new HashMap<>();
        result.put("flag", execute);
        return result;
    }

}
