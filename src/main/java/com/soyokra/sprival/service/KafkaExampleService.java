package com.soyokra.sprival.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

/**
 * Kafka 示例服务 展示如何使用优化后的Kafka配置
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Service
@Slf4j
@ConditionalOnProperty(name = "sprival.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class KafkaExampleService {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * 发送简单消息
     */
    public void sendSimpleMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);
        log.info("发送消息到主题 {}: {}", topic, message);
    }

    /**
     * 发送对象消息
     */
    public void sendObjectMessage(String topic, Object data) {
        kafkaTemplate.send(topic, data);
        log.info("发送对象消息到主题 {}: {}", topic, data);
    }

    /**
     * 发送到指定分区
     */
    public void sendToPartition(String topic, int partition, String key, Object data) {
        kafkaTemplate.send(topic, partition, key, data);
        log.info("发送消息到主题 {} 分区 {}: {}", topic, partition, data);
    }

    /**
     * 批量发送消息
     */
    public void sendBatchMessages(String topic, List<Object> messages) {
        for (Object message : messages) {
            kafkaTemplate.send(topic, message);
        }
        log.info("批量发送 {} 条消息到主题 {}", messages.size(), topic);
    }

    /**
     * 简单消息消费
     */
    @KafkaListener(topics = "simple-topic")
    public void handleSimpleMessage(@Payload String message) {
        log.info("接收到简单消息: {}", message);
        // 处理业务逻辑
    }

    /**
     * 对象消息消费
     */
    @KafkaListener(topics = "user-topic")
    public void handleUserMessage(@Payload Object user) {
        log.info("接收到用户消息: {}", user);
        // 处理用户相关业务
    }

    /**
     * 手动确认消息
     */
    @KafkaListener(topics = "manual-topic")
    public void handleManualMessage(@Payload String message, Acknowledgment ack) {
        try {
            log.info("接收到消息: {}", message);
            // 处理业务逻辑

            // 手动确认消息
            ack.acknowledge();
        } catch (Exception e) {
            log.error("处理消息失败: {}", e.getMessage());
            // 不确认消息，让消息重新消费
        }
    }

    /**
     * 批量消费消息
     */
    @KafkaListener(topics = "batch-topic", containerFactory = "batchKafkaListenerContainerFactory")
    public void handleBatchMessages(@Payload List<String> messages) {
        log.info("接收到批量消息，数量: {}", messages.size());
        for (String message : messages) {
            // 处理每条消息
            log.info("处理消息: {}", message);
        }
    }

    /**
     * 指定分区消费
     */
    @KafkaListener(topicPartitions = @org.springframework.kafka.annotation.TopicPartition(
            topic = "partitioned-topic", partitions = {"0", "1"}))
    public void handlePartitionedMessage(@Payload String message,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
        log.info("从分区 {} 接收到消息: {}", partition, message);
    }
}
