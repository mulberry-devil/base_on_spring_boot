package com.caston.base_on_spring_boot.ehcache.controller;

import com.caston.base_on_spring_boot.ehcache.entity.EhcacheUser;
import com.caston.base_on_spring_boot.ehcache.service.EhcacheService;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/ehcache")
public class EhcacheController {
    @Resource
    private EhcacheService ehcacheService;

    @GetMapping("/get")
    public EhcacheUser get(String id) {
        return ehcacheService.get(id);
    }

    @GetMapping("/getCacheEvict")
    public void getCacheEvict(String id) {
        ehcacheService.getCacheEvict(id);
    }
}
