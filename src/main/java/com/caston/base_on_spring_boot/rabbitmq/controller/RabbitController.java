package com.caston.base_on_spring_boot.rabbitmq.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rabbitmq")
public class RabbitController {
    @Resource
    private RabbitTemplate rabbitTemplate;

    @ApiOperation(value = "点对点发送", notes = "根据路由键向队列发送消息")
    @GetMapping("/point2point")
    public void point2point(String message) {
        rabbitTemplate.convertAndSend("point", message);
    }

    @ApiOperation(value = "点对点发送", notes = "根据路由键向队列发送消息但有多个消费者，随机一个消费者去消费信息")
    @GetMapping("/point2points")
    public void point2points() {
        for (int i = 0; i < 10; i++) {
            rabbitTemplate.convertAndSend("points", "work模型" + i);
        }
    }

    @ApiOperation(value = "广播", notes = "与点对点不同的是，广播是将消息发送到交换机，再由交换机发送到交换机绑定的所有队列，和路由键没有关系")
    @GetMapping("/fanout")
    public void fanout(String message) {
        rabbitTemplate.convertAndSend("fanoutExchange", "log", message);
    }

    @ApiOperation(value = "根据路由键广播", notes = "将消息发送到交换机，再根据路由键由交换机发送到交换机绑定的队列")
    @GetMapping("/directs")
    public void directs(String message) {
        rabbitTemplate.convertAndSend("directsExchange", "directsKey1", message);
    }

    @ApiOperation(value = "根据路由键规则广播", notes = "将消息发送到交换机，再根据路由键规则由交换机发送到交换机绑定的队列")
    @GetMapping("/topic")
    public void topic(String message) {
        rabbitTemplate.convertAndSend("topicExchange", "topicKey.topic.topic", message);
    }
}
