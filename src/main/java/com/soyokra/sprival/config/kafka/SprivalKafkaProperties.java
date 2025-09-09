package com.soyokra.sprival.config.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Data;

/**
 * Sprival Kafka 配置属性
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Component
@Data
@ConfigurationProperties(prefix = "sprival.kafka")
public class SprivalKafkaProperties {

    /**
     * 是否启用Kafka
     */
    private boolean enabled = true;

    /**
     * 生产者配置
     */
    private Producer producer = new Producer();

    /**
     * 消费者配置
     */
    private Consumer consumer = new Consumer();

    /**
     * 监控配置
     */
    private Monitor monitor = new Monitor();

    @Data
    public static class Producer {
        /**
         * 是否启用生产者
         */
        private boolean enabled = true;

        /**
         * 确认级别: 0, 1, all
         */
        private String acks = "all";

        /**
         * 重试次数
         */
        private int retries = 3;

        /**
         * 批量大小
         */
        private int batchSize = 16384;

        /**
         * 等待时间(ms)
         */
        private int lingerMs = 1;

        /**
         * 缓冲区大小
         */
        private long bufferMemory = 33554432L;

        /**
         * 压缩类型
         */
        private String compressionType = "gzip";

        /**
         * 是否启用幂等性
         */
        private boolean enableIdempotence = true;

        /**
         * 事务ID前缀
         */
        private String transactionIdPrefix = "sprival-tx-";
    }

    @Data
    public static class Consumer {
        /**
         * 是否启用消费者
         */
        private boolean enabled = true;

        /**
         * 消费者组ID
         */
        private String groupId = "sprival-group";

        /**
         * 客户端ID
         */
        private String clientId = "sprival-consumer";

        /**
         * 偏移量重置策略
         */
        private String autoOffsetReset = "earliest";

        /**
         * 是否启用自动提交
         */
        private boolean enableAutoCommit = false;

        /**
         * 自动提交间隔(ms)
         */
        private int autoCommitIntervalMs = 1000;

        /**
         * 单次拉取最大记录数
         */
        private int maxPollRecords = 500;

        /**
         * 最大轮询间隔(ms)
         */
        private int maxPollIntervalMs = 300000;

        /**
         * 会话超时时间(ms)
         */
        private int sessionTimeoutMs = 30000;

        /**
         * 心跳间隔(ms)
         */
        private int heartbeatIntervalMs = 3000;

        /**
         * 并发消费者数量
         */
        private int concurrency = 3;

        /**
         * 确认模式
         */
        private String ackMode = "manual_immediate";
    }

    @Data
    public static class Monitor {
        /**
         * 是否启用监控
         */
        private boolean enabled = true;

        /**
         * 是否启用健康检查
         */
        private boolean healthCheckEnabled = true;

        /**
         * 健康检查间隔(ms)
         */
        private int healthCheckInterval = 60000;

        /**
         * 健康检查超时(ms)
         */
        private int healthCheckTimeout = 5000;
    }
}
