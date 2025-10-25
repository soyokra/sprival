package com.soyokra.sprival.support.logging;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import lombok.Data;

/**
 * KafkaAppender 统一配置管理类 整合所有配置属性，提供统一的配置验证和管理
 * 
 * @author sprival
 * @since 2.0.0
 */
@Data
@Validated
@ConfigurationProperties(prefix = "sprival.logging.kafka")
public class KafkaAppenderConfiguration {

    // ========== 基础配置 ==========
    @NotBlank(message = "bootstrapServers cannot be blank")
    private String bootstrapServers = "localhost:9092";

    @NotBlank(message = "topic cannot be blank")
    private String topic = "default-topic";

    private String clientId;

    // ========== Kafka 生产者配置 ==========
    private String keySerializer = "org.apache.kafka.common.serialization.StringSerializer";
    private String valueSerializer = "org.apache.kafka.common.serialization.StringSerializer";
    private String acks = "1";

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

    private String compressionType = "none";
    private boolean enableIdempotence = false;

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

    // ========== 异步处理配置 ==========
    private boolean asyncMode = true;

    @Positive(message = "queueCapacity must be positive")
    @Max(value = 100000, message = "queueCapacity cannot exceed 100,000")
    private int queueCapacity = 10000;

    @Positive(message = "workerThreadCount must be positive")
    @Max(value = 10, message = "workerThreadCount cannot exceed 10")
    private int workerThreadCount = 1;

    // ========== 批处理配置 ==========
    private boolean enableBatching = true;

    @Positive(message = "maxBatchSize must be positive")
    @Max(value = 1000, message = "maxBatchSize cannot exceed 1000")
    private int maxBatchSize = 100;

    @PositiveOrZero(message = "batchTimeoutMs must be non-negative")
    @Max(value = 60000, message = "batchTimeoutMs cannot exceed 60 seconds")
    private long batchTimeoutMs = 1000;

    // ========== 连接容错配置 ==========
    private boolean enableConnectionFallback = true;
    private String fallbackFilePath = "logs/kafka-fallback.log";

    @Positive(message = "maxConnectionRetries must be positive")
    @Max(value = 20, message = "maxConnectionRetries cannot exceed 20")
    private int maxConnectionRetries = 5;

    @PositiveOrZero(message = "connectionRetryIntervalMs must be non-negative")
    @Max(value = 60000, message = "connectionRetryIntervalMs cannot exceed 60 seconds")
    private long connectionRetryIntervalMs = 5000;

    // ========== 监控配置 ==========
    private boolean enableMetrics = true;
    private String metricsPrefix = "kafka.appender";

    /**
     * 获取配置摘要信息
     * 
     * @return 配置摘要
     */
    public String getConfigurationSummary() {
        return String.format(
                "KafkaAppenderConfiguration{bootstrapServers='%s', topic='%s', "
                        + "asyncMode=%s, workerThreadCount=%d, queueCapacity=%d, "
                        + "enableBatching=%s, maxBatchSize=%d, batchTimeoutMs=%d}",
                bootstrapServers, topic, asyncMode, workerThreadCount, queueCapacity,
                enableBatching, maxBatchSize, batchTimeoutMs);
    }

    /**
     * 创建配置构建器
     * 
     * @return 配置构建器
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 配置构建器
     */
    public static class Builder {
        private final KafkaAppenderConfiguration config = new KafkaAppenderConfiguration();

        public Builder bootstrapServers(String bootstrapServers) {
            config.bootstrapServers = bootstrapServers;
            return this;
        }

        public Builder topic(String topic) {
            config.topic = topic;
            return this;
        }

        public Builder clientId(String clientId) {
            config.clientId = clientId;
            return this;
        }

        public Builder asyncMode(boolean asyncMode) {
            config.asyncMode = asyncMode;
            return this;
        }

        public Builder workerThreadCount(int workerThreadCount) {
            config.workerThreadCount = workerThreadCount;
            return this;
        }

        public Builder queueCapacity(int queueCapacity) {
            config.queueCapacity = queueCapacity;
            return this;
        }

        public Builder enableBatching(boolean enableBatching) {
            config.enableBatching = enableBatching;
            return this;
        }

        public Builder maxBatchSize(int maxBatchSize) {
            config.maxBatchSize = maxBatchSize;
            return this;
        }

        public Builder batchTimeoutMs(long batchTimeoutMs) {
            config.batchTimeoutMs = batchTimeoutMs;
            return this;
        }

        public Builder enableConnectionFallback(boolean enableConnectionFallback) {
            config.enableConnectionFallback = enableConnectionFallback;
            return this;
        }

        public Builder fallbackFilePath(String fallbackFilePath) {
            config.fallbackFilePath = fallbackFilePath;
            return this;
        }

        public KafkaAppenderConfiguration build() {
            // JSR-303 注解验证已在外部进行，这里直接返回配置对象
            return config;
        }
    }
}
