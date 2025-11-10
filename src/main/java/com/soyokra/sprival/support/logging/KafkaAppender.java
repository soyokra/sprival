package com.soyokra.sprival.support.logging;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.InterruptException;
import org.apache.kafka.common.errors.RetriableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.layout.EchoLayout;
import ch.qos.logback.core.util.OptionHelper;

/**
 * Logback Appender 实现，将日志事件发送到 Kafka。
 * 
 * <p>
 * 支持通过 logback-spring.xml 配置 Kafka Producer 的所有主要参数，包括：
 * <ul>
 * <li>基本配置：bootstrapServers、topic、clientId</li>
 * <li>生产者配置：acks、retries、batchSize、lingerMs、bufferMemory、compressionType、enableIdempotence</li>
 * <li>超时配置：requestTimeoutMs、deliveryTimeoutMs、maxBlockMs</li>
 * <li>序列化器配置：keySerializer、valueSerializer</li>
 * </ul>
 * 
 * <p>
 * 日志格式化支持 Encoder 或 Layout，默认使用 EchoLayout。
 * 
 * <p>
 * 异常处理：发送失败时仅记录错误日志，不会影响应用运行。
 * 
 * @author Sprival Team
 * @version 1.0
 */
public class KafkaAppender extends AppenderBase<ILoggingEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaAppender.class);

    // 基本配置
    private String bootstrapServers;
    private String topic;
    private String clientId;

    // 生产者配置
    private String acks;
    private Integer retries;
    private Integer batchSize;
    private Long lingerMs;
    private Long bufferMemory;
    private String compressionType;
    private Boolean enableIdempotence;

    // 超时配置
    private Integer requestTimeoutMs;
    private Integer deliveryTimeoutMs;
    private Long maxBlockMs;

    // 序列化器配置
    private String keySerializer;
    private String valueSerializer;

    // 日志格式化
    private Encoder<ILoggingEvent> encoder;
    private Layout<ILoggingEvent> layout;

    // Kafka Producer 实例
    private Producer<String, String> producer;

    @Override
    public void start() {
        if (!checkPrerequisites()) {
            return;
        }

        try {
            Properties props = buildProducerProperties();
            producer = new KafkaProducer<>(props);
            super.start();
            LOGGER.info("KafkaAppender started successfully, topic: {}, bootstrapServers: {}",
                    topic, bootstrapServers);
        } catch (Exception e) {
            addError("Failed to start KafkaAppender", e);
            LOGGER.error("Failed to start KafkaAppender", e);
        }
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        if (!isStarted() || producer == null) {
            return;
        }

        try {
            String logMessage = formatLogEvent(eventObject);
            if (logMessage == null || logMessage.isEmpty()) {
                return;
            }

            ProducerRecord<String, String> record = new ProducerRecord<>(topic, null, logMessage);
            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    handleSendException(exception, logMessage);
                }
            });

            // 非阻塞调用，避免影响主线程
        } catch (InterruptException e) {
            Thread.currentThread().interrupt();
            addError("Interrupted while sending log to Kafka", e);
        } catch (Exception e) {
            addError("Failed to send log to Kafka", e);
            LOGGER.error("Failed to send log to Kafka", e);
        }
    }

    @Override
    public void stop() {
        if (!isStarted()) {
            return;
        }

        if (producer != null) {
            try {
                producer.flush();
                producer.close(Duration.ofSeconds(30));
                LOGGER.info("KafkaAppender stopped successfully");
            } catch (Exception e) {
                addError("Error closing Kafka producer", e);
                LOGGER.error("Error closing Kafka producer", e);
            } finally {
                producer = null;
            }
        }

        super.stop();
    }

    /**
     * 检查启动前置条件
     * 
     * @return 如果满足所有前置条件返回 true，否则返回 false
     */
    private boolean checkPrerequisites() {
        if (OptionHelper.isEmpty(bootstrapServers)) {
            addError("bootstrapServers is required");
            return false;
        }

        if (OptionHelper.isEmpty(topic)) {
            addError("topic is required");
            return false;
        }

        return true;
    }

    /**
     * 构建 Kafka Producer 配置 Properties
     * 
     * @return Kafka Producer 配置
     */
    private Properties buildProducerProperties() {
        Properties props = new Properties();

        // 必需配置
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer != null ? keySerializer
                : "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                valueSerializer != null ? valueSerializer
                        : "org.apache.kafka.common.serialization.StringSerializer");

        // 客户端 ID
        if (clientId != null && !clientId.isEmpty()) {
            props.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
        }

        // 生产者配置
        if (acks != null && !acks.isEmpty()) {
            props.put(ProducerConfig.ACKS_CONFIG, acks);
        }
        if (retries != null) {
            props.put(ProducerConfig.RETRIES_CONFIG, retries);
        }
        if (batchSize != null) {
            props.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
        }
        if (lingerMs != null) {
            props.put(ProducerConfig.LINGER_MS_CONFIG, lingerMs);
        }
        if (bufferMemory != null) {
            props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemory);
        }
        if (compressionType != null && !compressionType.isEmpty()) {
            props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, compressionType);
        }
        if (enableIdempotence != null) {
            props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, enableIdempotence);
        }

        // 超时配置
        if (requestTimeoutMs != null) {
            props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, requestTimeoutMs);
        }
        if (deliveryTimeoutMs != null) {
            props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, deliveryTimeoutMs);
        }
        if (maxBlockMs != null) {
            props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, maxBlockMs);
        }

        return props;
    }

    /**
     * 格式化日志事件为字符串
     * 
     * @param event 日志事件
     * @return 格式化后的日志字符串
     */
    private String formatLogEvent(ILoggingEvent event) {
        try {
            if (encoder != null) {
                byte[] bytes = encoder.encode(event);
                return new String(bytes, StandardCharsets.UTF_8);
            } else if (layout != null) {
                return layout.doLayout(event);
            } else {
                // 默认格式化
                return new EchoLayout<ILoggingEvent>().doLayout(event);
            }
        } catch (Exception e) {
            addError("Failed to format log event", e);
            return null;
        }
    }

    /**
     * 处理发送异常
     * 
     * @param exception 异常信息
     * @param logMessage 日志消息
     */
    private void handleSendException(Exception exception, String logMessage) {
        if (exception instanceof RetriableException) {
            // 可重试异常，记录警告
            addWarn("Retriable exception while sending log to Kafka", exception);
            LOGGER.warn("Retriable exception while sending log to Kafka: {}",
                    exception.getMessage());
        } else {
            // 不可重试异常，记录错误
            addError("Failed to send log to Kafka", exception);
            LOGGER.error("Failed to send log to Kafka: {}", logMessage, exception);
        }
    }

    /**
     * 判断字符串是否是未定义的值（格式：xxx_IS_UNDEFINED）
     * 
     * @param value 待判断的字符串
     * @return 如果是未定义的值返回 true，否则返回 false
     */
    private boolean isUndefined(String value) {
        return value != null && value.endsWith("_IS_UNDEFINED");
    }

    // ========== Getter 和 Setter 方法 ==========

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = isUndefined(bootstrapServers) ? null : bootstrapServers;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = isUndefined(topic) ? null : topic;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = isUndefined(clientId) ? null : clientId;
    }

    public String getAcks() {
        return acks;
    }

    public void setAcks(String acks) {
        this.acks = isUndefined(acks) ? null : acks;
    }

    public Integer getRetries() {
        return retries;
    }

    public void setRetries(String retries) {
        if (isUndefined(retries)) {
            this.retries = null;
            return;
        }
        try {
            this.retries = retries != null && !retries.isEmpty() ? Integer.parseInt(retries) : null;
        } catch (NumberFormatException e) {
            addWarn("Invalid retries value: " + retries + ", using default", e);
            this.retries = null;
        }
    }

    public Integer getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(String batchSize) {
        if (isUndefined(batchSize)) {
            this.batchSize = null;
            return;
        }
        try {
            this.batchSize =
                    batchSize != null && !batchSize.isEmpty() ? Integer.parseInt(batchSize) : null;
        } catch (NumberFormatException e) {
            addWarn("Invalid batchSize value: " + batchSize + ", using default", e);
            this.batchSize = null;
        }
    }

    public Long getLingerMs() {
        return lingerMs;
    }

    public void setLingerMs(String lingerMs) {
        if (isUndefined(lingerMs)) {
            this.lingerMs = null;
            return;
        }
        try {
            this.lingerMs =
                    lingerMs != null && !lingerMs.isEmpty() ? Long.parseLong(lingerMs) : null;
        } catch (NumberFormatException e) {
            addWarn("Invalid lingerMs value: " + lingerMs + ", using default", e);
            this.lingerMs = null;
        }
    }

    public Long getBufferMemory() {
        return bufferMemory;
    }

    public void setBufferMemory(String bufferMemory) {
        if (isUndefined(bufferMemory)) {
            this.bufferMemory = null;
            return;
        }
        try {
            this.bufferMemory =
                    bufferMemory != null && !bufferMemory.isEmpty() ? Long.parseLong(bufferMemory)
                            : null;
        } catch (NumberFormatException e) {
            addWarn("Invalid bufferMemory value: " + bufferMemory + ", using default", e);
            this.bufferMemory = null;
        }
    }

    public String getCompressionType() {
        return compressionType;
    }

    public void setCompressionType(String compressionType) {
        this.compressionType = isUndefined(compressionType) ? null : compressionType;
    }

    public Boolean getEnableIdempotence() {
        return enableIdempotence;
    }

    public void setEnableIdempotence(String enableIdempotence) {
        if (isUndefined(enableIdempotence)) {
            this.enableIdempotence = null;
            return;
        }
        try {
            this.enableIdempotence = enableIdempotence != null && !enableIdempotence.isEmpty()
                    ? Boolean.parseBoolean(enableIdempotence)
                    : null;
        } catch (Exception e) {
            addWarn("Invalid enableIdempotence value: " + enableIdempotence + ", using default", e);
            this.enableIdempotence = null;
        }
    }

    public Integer getRequestTimeoutMs() {
        return requestTimeoutMs;
    }

    public void setRequestTimeoutMs(String requestTimeoutMs) {
        if (isUndefined(requestTimeoutMs)) {
            this.requestTimeoutMs = null;
            return;
        }
        try {
            this.requestTimeoutMs = requestTimeoutMs != null && !requestTimeoutMs.isEmpty()
                    ? Integer.parseInt(requestTimeoutMs)
                    : null;
        } catch (NumberFormatException e) {
            addWarn("Invalid requestTimeoutMs value: " + requestTimeoutMs + ", using default", e);
            this.requestTimeoutMs = null;
        }
    }

    public Integer getDeliveryTimeoutMs() {
        return deliveryTimeoutMs;
    }

    public void setDeliveryTimeoutMs(String deliveryTimeoutMs) {
        if (isUndefined(deliveryTimeoutMs)) {
            this.deliveryTimeoutMs = null;
            return;
        }
        try {
            this.deliveryTimeoutMs = deliveryTimeoutMs != null && !deliveryTimeoutMs.isEmpty()
                    ? Integer.parseInt(deliveryTimeoutMs)
                    : null;
        } catch (NumberFormatException e) {
            addWarn("Invalid deliveryTimeoutMs value: " + deliveryTimeoutMs + ", using default", e);
            this.deliveryTimeoutMs = null;
        }
    }

    public Long getMaxBlockMs() {
        return maxBlockMs;
    }

    public void setMaxBlockMs(String maxBlockMs) {
        if (isUndefined(maxBlockMs)) {
            this.maxBlockMs = null;
            return;
        }
        try {
            this.maxBlockMs =
                    maxBlockMs != null && !maxBlockMs.isEmpty() ? Long.parseLong(maxBlockMs) : null;
        } catch (NumberFormatException e) {
            addWarn("Invalid maxBlockMs value: " + maxBlockMs + ", using default", e);
            this.maxBlockMs = null;
        }
    }

    public String getKeySerializer() {
        return keySerializer;
    }

    public void setKeySerializer(String keySerializer) {
        this.keySerializer = isUndefined(keySerializer) ? null : keySerializer;
    }

    public String getValueSerializer() {
        return valueSerializer;
    }

    public void setValueSerializer(String valueSerializer) {
        this.valueSerializer = isUndefined(valueSerializer) ? null : valueSerializer;
    }

    public Encoder<ILoggingEvent> getEncoder() {
        return encoder;
    }

    public void setEncoder(Encoder<ILoggingEvent> encoder) {
        this.encoder = encoder;
    }

    public Layout<ILoggingEvent> getLayout() {
        return layout;
    }

    public void setLayout(Layout<ILoggingEvent> layout) {
        this.layout = layout;
    }
}
