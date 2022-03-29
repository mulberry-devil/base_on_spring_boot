package com.caston.base_on_spring_boot.rabbitmq.customer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

@Component
public class Customer {
    private static final Logger log = LoggerFactory.getLogger(Customer.class);

    // 点对点模式
    @RabbitListener(queuesToDeclare = {@Queue("point"), @Queue("points")})
    @RabbitHandler
    public void receivel1(String message) {
        log.info("message1 = {}", message);
    }

    @RabbitListener(queuesToDeclare = @Queue("points"))
    @RabbitHandler
    public void receivel2(String message) {
        log.info("message2 = {}", message);
    }

    @RabbitListener(queuesToDeclare = @Queue("points"))
    @RabbitHandler
    public void receivel3(String message) {
        log.info("message3 = {}", message);
    }

    // =========================================================================================

    // 广播模式
    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue, // 创建临时队列
                    exchange = @Exchange(value = "fanoutExchange", type = "fanout")) // 绑定交换机
    })
    @RabbitHandler
    public void receive1(String message) {
        log.info("message1 = {}", message);
    }

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue,
                    exchange = @Exchange(value = "fanoutExchange", type = "fanout"))
    })
    @RabbitHandler
    public void receive2(String message) {
        log.info("message2 = {}", message);
    }

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue,
                    exchange = @Exchange(value = "directsExchange", type = "direct"),
                    key = {"directsKey1"})
    })
    @RabbitHandler
    public void receive3(String message) {
        log.info("message3 = {}", message);
    }

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue,
                    exchange = @Exchange(value = "directsExchange", type = "direct"),
                    key = {"directsKey2"})
    })
    @RabbitHandler
    public void receive4(String message) {
        log.info("message4 = {}", message);
    }

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue,
                    exchange = @Exchange(value = "directsExchange", type = "direct"),
                    key = {"directsKey1", "directsKey2"})
    })
    @RabbitHandler
    public void receive5(String message) {
        log.info("message5 = {}", message);
    }

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue,
                    exchange = @Exchange(value = "topicExchange", type = "topic"),
                    key = {"topicKey.*"}) // *为匹配一个单词
    })
    @RabbitHandler
    public void receive6(String message) {
        log.info("message6 = {}", message);
    }

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue,
                    exchange = @Exchange(value = "topicExchange", type = "topic"),
                    key = {"topicKey.#"}) // #为匹配多个单词
    })
    @RabbitHandler
    public void receive7(String message) {
        log.info("message7 = {}", message);
    }
}
