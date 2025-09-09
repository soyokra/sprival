package com.soyokra.sprival.service;

import java.io.IOException;
import java.util.UUID;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import com.rabbitmq.client.Channel;
import com.soyokra.sprival.dto.UserMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * RabbitMQ 示例服务 演示消息发送和接收的各种场景
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "sprival.rabbitmq.enabled", havingValue = "true",
        matchIfMissing = true)
public class RabbitMQExampleService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送简单文本消息
     */
    public void sendSimpleMessage(String message) {
        try {
            rabbitTemplate.convertAndSend("example.exchange", "example.routing.key", message);
            log.info("发送简单消息成功: {}", message);
        } catch (Exception e) {
            log.error("发送简单消息失败: {}", message, e);
        }
    }

    /**
     * 发送用户对象消息
     */
    public void sendUserMessage(UserMessage userMessage) {
        try {
            rabbitTemplate.convertAndSend("example.exchange", "example.routing.key", userMessage);
            log.info("发送用户消息成功: {}", userMessage);
        } catch (Exception e) {
            log.error("发送用户消息失败: {}", userMessage, e);
        }
    }

    /**
     * 发送消息并等待确认
     */
    public void sendMessageWithConfirm(String message) {
        try {
            String correlationId = UUID.randomUUID().toString();
            rabbitTemplate.convertAndSend("example.exchange", "example.routing.key", message,
                    msg -> {
                        MessageProperties properties = msg.getMessageProperties();
                        properties.setCorrelationId(correlationId);
                        properties.setTimestamp(new java.util.Date());
                        return msg;
                    });
            log.info("发送消息并等待确认: {}, correlationId: {}", message, correlationId);
        } catch (Exception e) {
            log.error("发送消息失败: {}", message, e);
        }
    }

    /**
     * 发送延迟消息
     */
    public void sendDelayedMessage(String message, long delayMs) {
        try {
            rabbitTemplate.convertAndSend("example.exchange", "example.routing.key", message,
                    msg -> {
                        MessageProperties properties = msg.getMessageProperties();
                        properties.setDelay((int) delayMs);
                        return msg;
                    });
            log.info("发送延迟消息: {}, 延迟: {}ms", message, delayMs);
        } catch (Exception e) {
            log.error("发送延迟消息失败: {}", message, e);
        }
    }

    /**
     * 发送到死信队列的消息
     */
    public void sendToDeadLetter(String message) {
        try {
            // 发送到不存在的队列，触发死信
            rabbitTemplate.convertAndSend("nonexistent.exchange", "nonexistent.routing.key",
                    message);
            log.info("发送到死信队列: {}", message);
        } catch (Exception e) {
            log.error("发送到死信队列失败: {}", message, e);
        }
    }

    /**
     * 监听简单消息
     */
    @RabbitListener(queues = "example.queue")
    public void handleSimpleMessage(String message) {
        log.info("接收到简单消息: {}", message);
        // 模拟业务处理
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 监听用户消息
     */
    @RabbitListener(queues = "example.queue")
    public void handleUserMessage(UserMessage userMessage) {
        log.info("接收到用户消息: {}", userMessage);
        // 模拟业务处理
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 手动确认消息
     */
    @RabbitListener(queues = "example.queue")
    public void handleMessageWithManualAck(String message, Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        try {
            log.info("接收到消息(手动确认): {}", message);

            // 模拟业务处理
            Thread.sleep(300);

            // 手动确认消息
            channel.basicAck(deliveryTag, false);
            log.info("消息确认成功: {}", message);

        } catch (Exception e) {
            log.error("处理消息失败: {}", message, e);
            try {
                // 拒绝消息并重新入队
                channel.basicNack(deliveryTag, false, true);
                log.warn("消息拒绝并重新入队: {}", message);
            } catch (IOException ioException) {
                log.error("拒绝消息失败", ioException);
            }
        }
    }

    /**
     * 处理死信消息
     */
    @RabbitListener(queues = "dlx.queue")
    public void handleDeadLetterMessage(String message) {
        log.error("接收到死信消息: {}", message);
        // 处理死信消息，如记录日志、发送告警等
        // 这里可以发送邮件、短信等告警通知
    }

    /**
     * 批量处理消息
     */
    @RabbitListener(queues = "example.queue",
            containerFactory = "batchRabbitListenerContainerFactory")
    public void handleBatchMessages(java.util.List<String> messages) {
        log.info("批量处理消息，数量: {}", messages.size());
        for (String message : messages) {
            log.info("批量处理消息: {}", message);
        }
    }
}
