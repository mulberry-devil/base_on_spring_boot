package com.caston.base_on_spring_boot.ehcache.service;

import com.caston.base_on_spring_boot.ehcache.entity.EhcacheUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class EhcacheService {
    private static final Logger log = LoggerFactory.getLogger(EhcacheService.class);
    private static final Map<String, EhcacheUser> map = new HashMap<>();

    static {
        map.put("111", new EhcacheUser("111", "aaa", 1));
        map.put("222", new EhcacheUser("222", "bbb", 2));
        map.put("333", new EhcacheUser("333", "ccc", 3));
        map.put("444", new EhcacheUser("444", "ddd", 4));
    }

    @Cacheable(value = "users", key = "#id")
    public EhcacheUser get(String id) {
        log.info("测试是否走缓存");
        return map.get(id);
    }

    @Cacheable(value = "users", keyGenerator = "keyGenerator")
    public EhcacheUser getById(String id) {
        log.info("测试是否走缓存");
        return map.get(id);
    }

    @CachePut(value = "users", key = "#id")
    public EhcacheUser getCachePut(String id) {
        log.info("测试是否走缓存");
        return map.get(id);
    }

    @CacheEvict(value = "users", key = "#id")
    public void getCacheEvict(String id) {
        log.info("删除缓存");
    }
}
