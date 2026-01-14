package com.soyokra.sprival.config.rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ配置类
 *
 * @author sprival
 */
@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.post.queue}")
    private String postQueueName;

    /**
     * 配置JSON消息转换器
     *
     * @return JSON消息转换器
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 配置RabbitTemplate使用JSON消息转换器
     *
     * @param connectionFactory 连接工厂
     * @return RabbitTemplate
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }

    /**
     * 配置RabbitListener容器工厂使用JSON消息转换器
     *
     * @param connectionFactory 连接工厂
     * @return SimpleRabbitListenerContainerFactory
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        return factory;
    }

    /**
     * 声明Post队列（持久化队列） 应用启动时自动创建队列，如果队列已存在则不会重复创建
     *
     * @return Queue对象
     */
    @Bean
    public Queue postQueue() {
        return QueueBuilder.durable(postQueueName).build();
    }
}

