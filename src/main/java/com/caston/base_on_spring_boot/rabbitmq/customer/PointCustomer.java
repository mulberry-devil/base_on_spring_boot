package com.caston.base_on_spring_boot.rabbitmq.customer;

import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PointCustomer {
    @RabbitListener(queuesToDeclare = {@Queue("point"),@Queue("points")})
    @RabbitHandler
    public void receivel1(String message) {
        System.out.println("message1："+message);
    }

    @RabbitListener(queuesToDeclare = @Queue("points"))
    @RabbitHandler
    public void receivel2(String message) {
        System.out.println("message2："+message);
    }

    @RabbitListener(queuesToDeclare = @Queue("points"))
    @RabbitHandler
    public void receivel3(String message) {
        System.out.println("message3："+message);
    }
}
