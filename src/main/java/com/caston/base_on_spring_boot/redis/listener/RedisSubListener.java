package com.caston.base_on_spring_boot.redis.listener;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
public class RedisSubListener implements MessageListener {

    public RedisSubListener(RedisMessageListenerContainer listenerContainer) {
        listenerContainer.addMessageListener(this, new ChannelTopic("caston"));
        listenerContainer.addMessageListener(this, new ChannelTopic("chen"));
    }

    @Override
    public void onMessage(Message message, byte[] bytes) {
        System.out.println(getClass().getName() + ":" + "channel:" + new String(bytes) + ":" + message.toString());
    }
}
