package com.caston.base_on_spring_boot.redis.controller;

import com.caston.base_on_spring_boot.redis.service.RedisService;
import com.caston.base_on_spring_boot.swagger.entity.Hello;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/redis")
public class RedisController {

    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private RedisService redisService;

    @GetMapping("/set")
    public void set() {
        redisTemplate.opsForValue().set("key4", new Hello("caston", 28), 1, TimeUnit.MINUTES);
    }

    @GetMapping("/get")
    public Hello get() {
        return (Hello) redisTemplate.opsForValue().get("key3");
    }

    @GetMapping("/setName")
    public Hello setName() {
        return (Hello) redisService.selectByPrimaryKey(17);
    }
}
