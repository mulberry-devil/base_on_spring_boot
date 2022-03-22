package com.caston.base_on_spring_boot.redis.service;

import com.caston.base_on_spring_boot.swagger.entity.Hello;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

    @Cacheable(cacheNames = "user", key = "#age")
    public Hello selectByPrimaryKey(Integer age) {
        System.out.println("222222222222222222");
        return new Hello("caston", age);
    }
}
