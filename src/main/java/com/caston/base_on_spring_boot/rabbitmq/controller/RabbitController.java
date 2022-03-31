package com.caston.base_on_spring_boot.rabbitmq.controller;

import com.caston.base_on_spring_boot.rabbitmq.service.RabbitService;
import com.caston.base_on_spring_boot.swagger.entity.Hello;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.AbstractJavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@RestController
@RequestMapping("/rabbitmq")
public class RabbitController {
    private static final Logger log = LoggerFactory.getLogger(RabbitController.class);
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private Environment environment;
    @Resource
    private ObjectMapper objectMapper;

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

    /************************************************ rabbitmq异步实践 ************************************************/

    @ApiOperation(value = "实践异步记录用户操作日志", notes = "将与业务不相关的代码通过异步请求执行")
    @GetMapping("/logs")
    public void logs(Hello hello) throws JsonProcessingException {
        // TODO: 在这里执行其他逻辑操作
        // 异步写日志
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        rabbitTemplate.setExchange(environment.getProperty("log.user.exchange.name"));
        rabbitTemplate.setRoutingKey(environment.getProperty("log.user.routing.key.name"));
        Message message = MessageBuilder.withBody(objectMapper.writeValueAsBytes(hello)).setDeliveryMode(MessageDeliveryMode.PERSISTENT).build();
        message.getMessageProperties().setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME, MessageProperties.CONTENT_TYPE_JSON);
        rabbitTemplate.convertAndSend(message);
    }

    @ApiOperation(value = "实践异步发送邮件", notes = "将与业务不相关的代码通过异步请求执行")
    @GetMapping("/mail")
    public void mail(Hello hello) throws JsonProcessingException {
        // TODO: 在这里执行其他逻辑操作
        // 异步发送邮件
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        rabbitTemplate.setExchange(environment.getProperty("mail.exchange.name"));
        rabbitTemplate.setRoutingKey(environment.getProperty("mail.routing.key.name"));
        Message message = MessageBuilder.withBody(objectMapper.writeValueAsBytes(hello)).setDeliveryMode(MessageDeliveryMode.PERSISTENT).build();
        message.getMessageProperties().setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME, MessageProperties.CONTENT_TYPE_JSON);
        rabbitTemplate.convertAndSend(message);
    }

    /************************************************ rabbitmq削峰实践 ************************************************/

    private static final int ThreadNum = 5000;
    private static int phone = 0;
    @Resource
    private RabbitService rabbitService;

    /**
     * 将抢单请求的手机号信息压入队列，等待排队处理
     *
     * @param phone
     */
    public void sendRabbitMsg(String phone) {
        try {
            rabbitTemplate.setExchange(environment.getProperty("user.order.exchange.name"));
            rabbitTemplate.setRoutingKey(environment.getProperty("user.order.routing.key.name"));
            Message message = MessageBuilder.withBody(phone.getBytes("UTF-8")).setDeliveryMode(MessageDeliveryMode.PERSISTENT).build();
            rabbitTemplate.send(message);
        } catch (Exception e) {
            log.error("发送抢单信息入队列 发送异常：phone={}", phone);
        }
    }

    /**
     * 使用CountDownLatch模拟高并发同时发送5000个请求
     */
    public void generateMultiThread() {
        log.info("开始初始化线程数-----> ");
        try {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            for (int i = 0; i < ThreadNum; i++) {
                new Thread(new RunThread(countDownLatch)).start();
            }
            countDownLatch.countDown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class RunThread implements Runnable {
        private final CountDownLatch startLatch;

        private RunThread(CountDownLatch startLatch) {
            this.startLatch = startLatch;
        }

        @Override
        public void run() {
            try {
                startLatch.await();
                phone += 1;
                sendRabbitMsg(String.valueOf(phone)); // 发送消息到队列中
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @ApiOperation(value = "实践高并发下的队列请求", notes = "将请求发送到队列中")
    @GetMapping("/userOrder")
    public void userOrder() {
        generateMultiThread();
    }

    @ApiOperation(value = "获取并发结束后的值")
    @GetMapping("/getMap")
    public Map<String, Integer> getMap() {
        log.info("map长度为{}", RabbitService.map.size());
        return RabbitService.map;
    }

    /************************************************ rabbitmq死信队列实践 ************************************************/
    public static final Map<String, Object> MAP = new HashMap<>(3);

    @PostMapping("/deadQueue")
    public void pushUserOrder() {
        MAP.put("id", 10);
        MAP.put("status", 1);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        rabbitTemplate.setExchange(environment.getProperty("user.order.dead.produce.exchange.name"));
        rabbitTemplate.setRoutingKey(environment.getProperty("user.order.dead.produce.routing.key.name"));
        rabbitTemplate.convertAndSend(10, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                MessageProperties properties = message.getMessageProperties();
                properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                properties.setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME, Integer.class);
                return message;
            }
        });
    }
}
