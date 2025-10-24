package com.soyokra.sprival.support.logging;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

/**
 * KafkaAppender 配置属性验证类
 * 
 * @author sprival
 * @since 2.0.0
 */
public class KafkaAppenderProperties {

    @NotBlank(message = "bootstrapServers cannot be blank")
    private String bootstrapServers;

    @NotBlank(message = "topic cannot be blank")
    private String topic;

    private String clientId;

    @Positive(message = "retries must be positive")
    @Max(value = 10, message = "retries cannot exceed 10")
    private int retries = 3;

    @Positive(message = "kafkaBatchSize must be positive")
    @Max(value = 1048576, message = "kafkaBatchSize cannot exceed 1MB")
    private int kafkaBatchSize = 16384;

    @PositiveOrZero(message = "lingerMs must be non-negative")
    @Max(value = 30000, message = "lingerMs cannot exceed 30 seconds")
    private int lingerMs = 1;

    @Positive(message = "bufferMemory must be positive")
    @Min(value = 1024, message = "bufferMemory must be at least 1KB")
    private long bufferMemory = 33554432L;

    @PositiveOrZero(message = "requestTimeoutMs must be non-negative")
    @Max(value = 300000, message = "requestTimeoutMs cannot exceed 5 minutes")
    private int requestTimeoutMs = 30000;

    @PositiveOrZero(message = "deliveryTimeoutMs must be non-negative")
    @Max(value = 600000, message = "deliveryTimeoutMs cannot exceed 10 minutes")
    private int deliveryTimeoutMs = 120000;

    @PositiveOrZero(message = "maxBlockMs must be non-negative")
    @Max(value = 300000, message = "maxBlockMs cannot exceed 5 minutes")
    private int maxBlockMs = 60000;

    @PositiveOrZero(message = "shutdownTimeoutSeconds must be non-negative")
    @Max(value = 60, message = "shutdownTimeoutSeconds cannot exceed 60 seconds")
    private int shutdownTimeoutSeconds = 5;

    // 异步处理配置
    @Positive(message = "queueCapacity must be positive")
    @Max(value = 100000, message = "queueCapacity cannot exceed 100,000")
    private int queueCapacity = 10000;

    @Positive(message = "workerThreadCount must be positive")
    @Max(value = 10, message = "workerThreadCount cannot exceed 10")
    private int workerThreadCount = 1;

    // 批处理配置
    @Positive(message = "maxBatchSize must be positive")
    @Max(value = 1000, message = "maxBatchSize cannot exceed 1000")
    private int maxBatchSize = 100;

    @PositiveOrZero(message = "batchTimeoutMs must be non-negative")
    @Max(value = 60000, message = "batchTimeoutMs cannot exceed 60 seconds")
    private long batchTimeoutMs = 1000;

    // Getters and Setters
    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public int getKafkaBatchSize() {
        return kafkaBatchSize;
    }

    public void setKafkaBatchSize(int kafkaBatchSize) {
        this.kafkaBatchSize = kafkaBatchSize;
    }

    public int getLingerMs() {
        return lingerMs;
    }

    public void setLingerMs(int lingerMs) {
        this.lingerMs = lingerMs;
    }

    public long getBufferMemory() {
        return bufferMemory;
    }

    public void setBufferMemory(long bufferMemory) {
        this.bufferMemory = bufferMemory;
    }

    public int getRequestTimeoutMs() {
        return requestTimeoutMs;
    }

    public void setRequestTimeoutMs(int requestTimeoutMs) {
        this.requestTimeoutMs = requestTimeoutMs;
    }

    public int getDeliveryTimeoutMs() {
        return deliveryTimeoutMs;
    }

    public void setDeliveryTimeoutMs(int deliveryTimeoutMs) {
        this.deliveryTimeoutMs = deliveryTimeoutMs;
    }

    public int getMaxBlockMs() {
        return maxBlockMs;
    }

    public void setMaxBlockMs(int maxBlockMs) {
        this.maxBlockMs = maxBlockMs;
    }

    public int getShutdownTimeoutSeconds() {
        return shutdownTimeoutSeconds;
    }

    public void setShutdownTimeoutSeconds(int shutdownTimeoutSeconds) {
        this.shutdownTimeoutSeconds = shutdownTimeoutSeconds;
    }

    public int getQueueCapacity() {
        return queueCapacity;
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public int getWorkerThreadCount() {
        return workerThreadCount;
    }

    public void setWorkerThreadCount(int workerThreadCount) {
        this.workerThreadCount = workerThreadCount;
    }

    public int getMaxBatchSize() {
        return maxBatchSize;
    }

    public void setMaxBatchSize(int maxBatchSize) {
        this.maxBatchSize = maxBatchSize;
    }

    public long getBatchTimeoutMs() {
        return batchTimeoutMs;
    }

    public void setBatchTimeoutMs(long batchTimeoutMs) {
        this.batchTimeoutMs = batchTimeoutMs;
    }
}
