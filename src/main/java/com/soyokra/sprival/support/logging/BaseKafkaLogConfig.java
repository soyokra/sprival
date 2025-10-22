package com.soyokra.sprival.support.logging;

import lombok.Data;

/**
 * Kafka日志配置基类 提供通用的Kafka生产者配置属性
 * 
 * @author Sprival Team
 * @since 1.0.0
 */
@Data
public abstract class BaseKafkaLogConfig {

    /**
     * 日志输出目标：file（文件）、kafka、both（同时输出）
     */
    private LogOutputTarget outputTarget = LogOutputTarget.FILE;

    /**
     * Kafka服务器地址
     */
    private String bootstrapServers = "localhost:9092";

    /**
     * 日志主题名称
     */
    private String topic;

    /**
     * 客户端ID
     */
    private String clientId;

    /**
     * 确认机制
     */
    private String acks = "1";

    /**
     * 重试次数
     */
    private int retries = 3;

    /**
     * 批处理大小
     */
    private int batchSize = 16384;

    /**
     * 延迟时间（毫秒）
     */
    private int lingerMs = 1;

    /**
     * 缓冲区内存大小
     */
    private long bufferMemory = 33554432L;

    /**
     * 压缩类型
     */
    private String compressionType = "none";

    /**
     * 请求超时时间（毫秒）
     */
    private int requestTimeoutMs = 30000;

    /**
     * 投递超时时间（毫秒）
     */
    private int deliveryTimeoutMs = 120000;

    /**
     * 最大阻塞时间（毫秒）
     */
    private int maxBlockMs = 60000;

    /**
     * 设置输出目标（支持字符串类型）
     */
    public void setOutputTarget(String outputTarget) {
        this.outputTarget = LogOutputTarget.fromValue(outputTarget);
    }
}

