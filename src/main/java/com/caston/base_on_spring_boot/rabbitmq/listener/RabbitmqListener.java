package com.caston.base_on_spring_boot.rabbitmq.listener;

import com.caston.base_on_spring_boot.rabbitmq.controller.RabbitController;
import com.caston.base_on_spring_boot.swagger.entity.Hello;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

@Component
public class RabbitmqListener {
    private static final Logger log = LoggerFactory.getLogger(RabbitmqListener.class);

    @Resource
    private ObjectMapper objectMapper;

    /*************************** 异步 ***************************/
    @RabbitListener(queues = "${log.user.queue.name}", containerFactory = "singleListenerContainer")
    public void logsQueue(@Payload byte[] message) throws IOException {
        log.info("log监听消费用户日志 监听到消息： {} ", message);
        Hello hello = objectMapper.readValue(message, Hello.class);
        log.info("log监听消费用户日志 监听到消息： {} ", hello);
        // TODO: 真正在这执行写日志操作
    }

    @RabbitListener(queues = "${mail.queue.name}", containerFactory = "singleListenerContainer")
    public void mailQueue(@Payload byte[] message) throws IOException {
        log.info("mail监听消费用户日志 监听到消息： {} ", message);
        Hello hello = objectMapper.readValue(message, Hello.class);
        log.info("mail监听消费用户日志 监听到消息： {} ", hello);
        // TODO: 真正在这执行发送邮件操作
    }

    /*************************** 死信队列（延迟队列） ***************************/
    @RabbitListener(queues = "${user.order.dead.real.queue.name}", containerFactory = "multiListenerContainer")
    public void consumeMessage(@Payload Integer id) {
        try {
            log.info("死信队列-用户超时监听信息：{}", id);
            if ((Integer) RabbitController.MAP.get("status") == 1) {
                RabbitController.MAP.replace("status", 3);
                log.info("这里为未支付模拟情况");
            } else {
                // TODO: 其他逻辑操作
                log.info("这里为已支付模拟情况");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
