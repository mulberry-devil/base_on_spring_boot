package com.caston.base_on_spring_boot.rabbitmq.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class RabbitService {
    private static final Logger log = LoggerFactory.getLogger(RabbitService.class);
    private static Integer NUM = 100;
    public static final Map<String, Integer> map = new HashMap<>(100);

    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private Environment environment;

    public void manageNum(String phone) {
        try {
            if (NUM > 0) {
                // TODO: 处理数据库中数据，下面是模拟操作数据库
                map.put(phone, NUM);
                NUM--;
            }
        } catch (Exception e) {
            log.error("处理抢单发送异常：mobile={}", phone);
        }
    }
}
