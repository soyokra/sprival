package com.soyokra.sprival.config.rabbitmq;

import java.util.HashMap;
import java.util.Map;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import lombok.extern.slf4j.Slf4j;

/**
 * RabbitMQ 配置类 提供连接池、重试机制、死信队列等增强功能
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Slf4j
@Configuration
@ConditionalOnClass(RabbitTemplate.class)
@ConditionalOnProperty(prefix = "sprival.rabbitmq", name = "enabled", havingValue = "true",
        matchIfMissing = true)
@EnableConfigurationProperties(SprivalRabbitProperties.class)
public class SprivalRabbitConfiguration {

    private final SprivalRabbitProperties properties;

    public SprivalRabbitConfiguration(SprivalRabbitProperties properties) {
        this.properties = properties;
    }

    /**
     * 配置消息转换器
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 配置连接工厂
     */
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();

        // 基础连接配置
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setVirtualHost("/");

        // 连接池配置
        SprivalRabbitProperties.Pool pool = properties.getPool();
        factory.setCacheMode(
                CachingConnectionFactory.CacheMode.valueOf(pool.getMode().toUpperCase()));
        factory.setConnectionCacheSize(pool.getConnectionSize());
        factory.setChannelCacheSize(pool.getChannelSize());
        factory.setChannelCheckoutTimeout(pool.getCheckoutTimeout());
        factory.setConnectionTimeout((int) pool.getConnectionTimeout());
        factory.setRequestedHeartBeat(pool.getHeartbeat());
        factory.setConnectionNameStrategy(connectionFactory -> pool.getConnectionName());

        // 发布确认配置
        factory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        factory.setPublisherReturns(true);

        log.info("RabbitMQ 连接工厂配置完成: mode={}, connectionSize={}, channelSize={}", pool.getMode(),
                pool.getConnectionSize(), pool.getChannelSize());

        return factory;
    }

    /**
     * 配置 RabbitTemplate
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);

        // 设置消息转换器
        template.setMessageConverter(messageConverter());

        // 设置重试模板
        if (properties.getRetry().isEnabled()) {
            template.setRetryTemplate(retryTemplate());
        }

        // 设置确认回调
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.debug("消息发送成功: {}",
                        correlationData != null ? correlationData.getId() : "unknown");
            } else {
                log.error("消息发送失败: {}, 原因: {}",
                        correlationData != null ? correlationData.getId() : "unknown", cause);
            }
        });

        // 设置返回回调
        template.setMandatory(true);
        template.setReturnsCallback(returned -> {
            log.error("消息返回: exchange={}, routingKey={}, replyCode={}, replyText={}",
                    returned.getExchange(), returned.getRoutingKey(), returned.getReplyCode(),
                    returned.getReplyText());
        });

        log.info("RabbitTemplate 配置完成");
        return template;
    }

    /**
     * 配置重试模板
     */
    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        SprivalRabbitProperties.Retry retry = properties.getRetry();

        // 重试策略
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(retry.getMaxAttempts());
        retryTemplate.setRetryPolicy(retryPolicy);

        // 退避策略
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(retry.getInitialInterval());
        backOffPolicy.setMultiplier(retry.getMultiplier());
        backOffPolicy.setMaxInterval(retry.getMaxInterval());
        retryTemplate.setBackOffPolicy(backOffPolicy);

        log.info("重试模板配置完成: maxAttempts={}, initialInterval={}ms", retry.getMaxAttempts(),
                retry.getInitialInterval());

        return retryTemplate;
    }

    /**
     * 配置消息监听器容器工厂
     */
    @Bean
    public RabbitListenerContainerFactory<SimpleMessageListenerContainer> rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());

        // 确认模式
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);

        // 并发配置
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(5);

        // 重试配置
        if (properties.getRetry().isEnabled()) {
            factory.setRetryTemplate(retryTemplate());
        }

        // 预取数量
        factory.setPrefetchCount(1);

        log.info("消息监听器容器工厂配置完成");
        return factory;
    }

    /**
     * 配置死信交换器
     */
    @Bean
    @ConditionalOnProperty(prefix = "sprival.rabbitmq.dead-letter", name = "enabled",
            havingValue = "true")
    public DirectExchange deadLetterExchange() {
        SprivalRabbitProperties.DeadLetter deadLetter = properties.getDeadLetter();
        DirectExchange exchange = new DirectExchange(deadLetter.getExchange(), true, false);
        log.info("死信交换器配置完成: {}", deadLetter.getExchange());
        return exchange;
    }

    /**
     * 配置死信队列
     */
    @Bean
    @ConditionalOnProperty(prefix = "sprival.rabbitmq.dead-letter", name = "enabled",
            havingValue = "true")
    public Queue deadLetterQueue() {
        SprivalRabbitProperties.DeadLetter deadLetter = properties.getDeadLetter();
        Queue queue = QueueBuilder.durable(deadLetter.getQueue())
                .withArgument("x-message-ttl", deadLetter.getMessageTtl())
                .withArgument("x-max-length", deadLetter.getMaxLength()).build();
        log.info("死信队列配置完成: {}", deadLetter.getQueue());
        return queue;
    }

    /**
     * 绑定死信队列和交换器
     */
    @Bean
    @ConditionalOnProperty(prefix = "sprival.rabbitmq.dead-letter", name = "enabled",
            havingValue = "true")
    public Binding deadLetterBinding() {
        SprivalRabbitProperties.DeadLetter deadLetter = properties.getDeadLetter();
        Binding binding = BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange())
                .with(deadLetter.getRoutingKey());
        log.info("死信队列绑定配置完成: {} -> {}", deadLetter.getQueue(), deadLetter.getExchange());
        return binding;
    }

    /**
     * 配置示例队列
     */
    @Bean
    public Queue exampleQueue() {
        Map<String, Object> arguments = new HashMap<>();
        if (properties.getDeadLetter().isEnabled()) {
            arguments.put("x-dead-letter-exchange", properties.getDeadLetter().getExchange());
            arguments.put("x-dead-letter-routing-key", properties.getDeadLetter().getRoutingKey());
        }
        arguments.put("x-message-ttl", properties.getDeadLetter().getMessageTtl());

        Queue queue = QueueBuilder.durable("example.queue").withArguments(arguments).build();
        log.info("示例队列配置完成: example.queue");
        return queue;
    }

    /**
     * 配置示例交换器
     */
    @Bean
    public DirectExchange exampleExchange() {
        DirectExchange exchange = new DirectExchange("example.exchange", true, false);
        log.info("示例交换器配置完成: example.exchange");
        return exchange;
    }

    /**
     * 绑定示例队列和交换器
     */
    @Bean
    public Binding exampleBinding() {
        Binding binding = BindingBuilder.bind(exampleQueue()).to(exampleExchange())
                .with("example.routing.key");
        log.info("示例队列绑定配置完成: example.queue -> example.exchange");
        return binding;
    }
}
