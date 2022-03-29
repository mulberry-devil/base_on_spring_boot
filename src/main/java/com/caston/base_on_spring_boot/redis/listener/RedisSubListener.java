package com.caston.base_on_spring_boot.redis.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
public class RedisSubListener implements MessageListener {
    private static final Logger log = LoggerFactory.getLogger(RedisSubListener.class);

    public RedisSubListener(RedisMessageListenerContainer listenerContainer) {
        listenerContainer.addMessageListener(this, new ChannelTopic("caston"));
        listenerContainer.addMessageListener(this, new ChannelTopic("chen"));
    }

    @Override
    public void onMessage(Message message, byte[] bytes) {
        log.info("{}:channel{}:{}", getClass().getName(), new String(bytes), message.toString());
    }
}
