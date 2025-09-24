package com.soyokra.sprival.support.logging;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * KafkaAppender配置属性
 * 
 * @author sprival
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "sprival.logging.kafka")
public class KafkaAppenderProperties {
    
    /**
     * 是否启用Kafka日志追加器
     */
    private boolean enabled = false;
    
    /**
     * Kafka服务器地址
     */
    private String bootstrapServers = "localhost:9092";
    
    /**
     * 日志主题名称
     */
    private String topic = "application-logs";
    
    /**
     * 客户端ID
     */
    private String clientId = "kafka-appender";
    
    /**
     * 键序列化器
     */
    private String keySerializer = "org.apache.kafka.common.serialization.StringSerializer";
    
    /**
     * 值序列化器
     */
    private String valueSerializer = "org.apache.kafka.common.serialization.StringSerializer";
    
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
     * 是否启用幂等性
     */
    private boolean enableIdempotence = false;
    
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
     * 日志级别过滤
     */
    private String level = "INFO";
    
    /**
     * 是否包含MDC信息
     */
    private boolean includeMdc = true;
    
    /**
     * 是否包含异常堆栈信息
     */
    private boolean includeStackTrace = true;
    
    /**
     * 自定义字段
     */
    private java.util.Map<String, Object> customFields = new java.util.HashMap<>();
    
    // Getter和Setter方法
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
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
    
    public String getKeySerializer() {
        return keySerializer;
    }
    
    public void setKeySerializer(String keySerializer) {
        this.keySerializer = keySerializer;
    }
    
    public String getValueSerializer() {
        return valueSerializer;
    }
    
    public void setValueSerializer(String valueSerializer) {
        this.valueSerializer = valueSerializer;
    }
    
    public String getAcks() {
        return acks;
    }
    
    public void setAcks(String acks) {
        this.acks = acks;
    }
    
    public int getRetries() {
        return retries;
    }
    
    public void setRetries(int retries) {
        this.retries = retries;
    }
    
    public int getBatchSize() {
        return batchSize;
    }
    
    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
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
    
    public String getCompressionType() {
        return compressionType;
    }
    
    public void setCompressionType(String compressionType) {
        this.compressionType = compressionType;
    }
    
    public boolean isEnableIdempotence() {
        return enableIdempotence;
    }
    
    public void setEnableIdempotence(boolean enableIdempotence) {
        this.enableIdempotence = enableIdempotence;
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
    
    public String getLevel() {
        return level;
    }
    
    public void setLevel(String level) {
        this.level = level;
    }
    
    public boolean isIncludeMdc() {
        return includeMdc;
    }
    
    public void setIncludeMdc(boolean includeMdc) {
        this.includeMdc = includeMdc;
    }
    
    public boolean isIncludeStackTrace() {
        return includeStackTrace;
    }
    
    public void setIncludeStackTrace(boolean includeStackTrace) {
        this.includeStackTrace = includeStackTrace;
    }
    
    public java.util.Map<String, Object> getCustomFields() {
        return customFields;
    }
    
    public void setCustomFields(java.util.Map<String, Object> customFields) {
        this.customFields = customFields;
    }
}
