package com.caston.base_on_spring_boot.redis.service;

import com.caston.base_on_spring_boot.swagger.entity.Hello;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class RedisService {
    private static final Logger log = LoggerFactory.getLogger(RedisService.class);

    @Cacheable(cacheNames = "user", key = "#age")
    public Hello selectByPrimaryKey(Integer age) {
        log.info("进入redis缓存测试1");
        return new Hello("caston", age);
    }

    @Cacheable(cacheNames = "caston", key = "#age")
    public Hello select(Integer age) {
        log.info("进入redis缓存测试2");
        return new Hello("caston", age);
    }
}
