package com.caston.base_on_spring_boot.ehcache.config;

import com.alibaba.fastjson.JSON;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

@Configuration
@EnableCaching
public class EhcacheConfig extends CachingConfigurerSupport {
    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            StringBuilder strBuilder = new StringBuilder();
            strBuilder.append(target.getClass().getName());
            strBuilder.append(":");
            strBuilder.append(method.getName());
            for (Object obj : params) {
                if (obj != null) {
                    strBuilder.append(":");
                    strBuilder.append(obj.getClass().getName());
                    strBuilder.append(":");
                    strBuilder.append(JSON.toJSONString(obj));
                }
            }
            //log.info("ehcache key str: " + strBuilder.toString());
            String md5DigestAsHex = DigestUtils.md5DigestAsHex(strBuilder.toString().getBytes(StandardCharsets.UTF_8));
            return md5DigestAsHex;
        };
    }
}
