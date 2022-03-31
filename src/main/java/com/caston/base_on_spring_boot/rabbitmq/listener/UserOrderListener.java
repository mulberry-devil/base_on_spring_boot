package com.caston.base_on_spring_boot.rabbitmq.listener;

import com.caston.base_on_spring_boot.rabbitmq.service.RabbitService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class UserOrderListener implements ChannelAwareMessageListener {
    private static final Logger log = LoggerFactory.getLogger(UserOrderListener.class);
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private RabbitService rabbitService;

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        long tag = message.getMessageProperties().getDeliveryTag();
        try {
            byte[] body = message.getBody();
            String phone = new String(body, "UTF-8");
            log.info("监听到抢单手机号：{}", phone);
            // TODO: 请求到这时去服务层处理业务逻辑
            rabbitService.manageNum(String.valueOf(phone));
            // 确认消费
            channel.basicAck(tag, true);
        } catch (Exception e) {
            log.error("用户抢单 发送异常：", e.fillInStackTrace());
            // 确认消费
            channel.basicReject(tag, false);
        }
    }
}
