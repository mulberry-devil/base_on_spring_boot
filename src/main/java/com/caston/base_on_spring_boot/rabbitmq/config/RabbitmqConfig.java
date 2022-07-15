package com.caston.base_on_spring_boot.rabbitmq.config;

import com.caston.base_on_spring_boot.rabbitmq.listener.UserOrderListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitmqConfig {
    private static final Logger log = LoggerFactory.getLogger(RabbitmqConfig.class);
    @Resource
    private Environment environment;
    @Resource
    private CachingConnectionFactory connectionFactory;
    @Resource
    private SimpleRabbitListenerContainerFactoryConfigurer factoryConfigurer;
    @Resource
    private UserOrderListener userOrderListener;

    /**
     * 单一消费者配置
     *
     * @return
     */
    @Bean(name = "singleListenerContainer")
    public SimpleRabbitListenerContainerFactory listenerContainer() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        // factory.setMessageConverter(new Jackson2JsonMessageConverter());
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(1);
        factory.setPrefetchCount(1);
        factory.setBatchSize(1);
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        return factory;
    }

    /**
     * 多个消费者配置
     *
     * @return
     */
    @Bean(name = "multiListenerContainer")
    public SimpleRabbitListenerContainerFactory multiListenerContainer() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factoryConfigurer.configure(factory, connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        factory.setAcknowledgeMode(AcknowledgeMode.NONE);
        factory.setConcurrentConsumers(environment.getProperty("spring.rabbitmq.listener.simple.concurrency", int.class));
        factory.setMaxConcurrentConsumers(environment.getProperty("spring.rabbitmq.listener.simple.max-concurrency", int.class));
        factory.setPrefetchCount(environment.getProperty("spring.rabbitmq.listener.simple.prefetch", int.class));
        return factory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        connectionFactory.setPublisherReturns(true);
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                log.info("消息发送成功:correlationData({}),ack({}),cause({})", correlationData, ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                log.info("消息丢失:exchange({}),route({}),replyCode({}),replyText({}),message:{}", exchange, routingKey, replyCode, replyText, message);
            }
        });
        return rabbitTemplate;
    }

    /************************************************ 创建队列、交换机以及路由键绑定 ************************************************/

    /*************************** 异步 ***************************/
    @Bean
    public Queue logUserQueue() {
        return new Queue(environment.getProperty("log.user.queue.name"), true);
    }

    @Bean
    public DirectExchange logUserExchange() {
        return new DirectExchange(environment.getProperty("log.user.exchange.name"), true, false);
    }

    @Bean
    public Binding logUserBinding() {
        return BindingBuilder.bind(logUserQueue()).to(logUserExchange()).with(environment.getProperty("log.user.routing.key.name"));
    }

    @Bean
    public Queue mailQueue() {
        return new Queue(environment.getProperty("mail.queue.name"), true);
    }

    @Bean
    public DirectExchange mailExchange() {
        return new DirectExchange(environment.getProperty("mail.exchange.name"), true, false);
    }

    @Bean
    public Binding mailBinding() {
        return BindingBuilder.bind(mailQueue()).to(mailExchange()).with(environment.getProperty("mail.routing.key.name"));
    }

    /*************************** 削峰 ***************************/
    @Bean
    public Queue userOrderQueue() {
        return new Queue(environment.getProperty("user.order.queue.name"), true);
    }

    @Bean
    public TopicExchange userOrderExchange() {
        return new TopicExchange(environment.getProperty("user.order.exchange.name"), true, false);
    }

    @Bean
    public Binding userOrderBinding() {
        return BindingBuilder.bind(userOrderQueue()).to(userOrderExchange()).with(environment.getProperty("user.order.routing.key.name"));
    }

    @Bean
    public SimpleMessageListenerContainer listenerContainer(@Qualifier("userOrderQueue") Queue userOrderQueue) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        MessageListenerAdapter adapter = new MessageListenerAdapter();
        adapter.setMessageConverter(new Jackson2JsonMessageConverter());
        container.setMessageListener(adapter);
        // 并发配置
        container.setConcurrentConsumers(environment.getProperty("spring.rabbitmq.listener.simple.concurrency", Integer.class));
        container.setMaxConcurrentConsumers(environment.getProperty("spring.rabbitmq.listener.simple.max-concurrency", Integer.class));
        container.setPrefetchCount(environment.getProperty("spring.rabbitmq.listener.simple.prefetch", Integer.class));
        /*
         * 消息确认
         * 对于某些消息而言，我们有时候需要严格的知道消息是否已经被 consumer 监听消费处理了，即我们有一种消息确认机制来保证我们的消息是否已经真正的被消费处理
         * 所以消息确认处理机制需要改成手动模式，需要自定义监听器实现 ChannelAwareMessageListener
         */
        container.setQueues(userOrderQueue); // 指定队列
        container.setMessageListener(userOrderListener); // 指定自定义监听器
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return container;
    }

    /*************************** 死信队列（延迟队列） ***************************/
    @Bean
    public Queue userOrderDeadQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", environment.getProperty("user.order.dead.exchange.name"));
        args.put("x-dead-letter-routing-key", environment.getProperty("user.order.dead.routing.key.name"));
        args.put("x-message-ttl", 10000);
        return new Queue(environment.getProperty("user.order.dead.queue.name"), true, false, false, args);
    }

    @Bean
    public TopicExchange userOrderDeadExchange() {
        return new TopicExchange(environment.getProperty("user.order.dead.produce.exchange.name"), true, false);
    }

    @Bean
    public Binding userOrderDeadBinding() {
        return BindingBuilder.bind(userOrderDeadQueue()).to(userOrderDeadExchange()).with(environment.getProperty("user.order.dead.produce.routing.key.name"));
    }

    @Bean
    public Queue userOrderDeadRealQueue() {
        return new Queue(environment.getProperty("user.order.dead.real.queue.name"), true);
    }

    @Bean
    public TopicExchange userOrderDeadRealExchange() {
        return new TopicExchange(environment.getProperty("user.order.dead.exchange.name"));
    }

    @Bean
    public Binding userOrderDeadRealBinding() {
        return BindingBuilder.bind(userOrderDeadRealQueue()).to(userOrderDeadRealExchange()).with(environment.getProperty("user.order.dead.routing.key.name"));
    }
}
