package com.soyokra.sprival.support.logging;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Layout;

/**
 * Kafka日志追加器 将日志事件发送到Kafka主题
 * 
 * @author sprival
 * @since 1.0.0
 */
public class KafkaAppender extends AppenderBase<ILoggingEvent> {


    // 配置属性
    private String bootstrapServers;
    private String topic;
    private String clientId;
    private String keySerializer = StringSerializer.class.getName();
    private String valueSerializer = StringSerializer.class.getName();
    private String acks = "1";
    private int retries = 3;
    private int batchSize = 16384;
    private int lingerMs = 1;
    private long bufferMemory = 33554432L;
    private String compressionType = "none";
    private boolean enableIdempotence = false;
    private int requestTimeoutMs = 30000;
    private int deliveryTimeoutMs = 120000;
    private int maxBlockMs = 60000;

    // 内部组件
    private Producer<String, String> producer;
    private Layout<ILoggingEvent> layout;
    private ObjectMapper objectMapper;
    private final AtomicBoolean started = new AtomicBoolean(false);

    @Override
    public void start() {
        if (!started.compareAndSet(false, true)) {
            return;
        }

        try {
            // 验证必需参数
            if (bootstrapServers == null || bootstrapServers.trim().isEmpty()) {
                addError("bootstrapServers is required");
                return;
            }

            if (topic == null || topic.trim().isEmpty()) {
                addError("topic is required");
                return;
            }

            // 创建Kafka生产者配置
            Properties props = createProducerProperties();

            // 创建Kafka生产者
            producer = new KafkaProducer<>(props);

            // 初始化JSON序列化器
            objectMapper = new ObjectMapper();

            addInfo("KafkaAppender started successfully");

        } catch (Exception e) {
            started.set(false);
            addError("Failed to start KafkaAppender", e);
        }
    }

    @Override
    public void stop() {
        if (!started.compareAndSet(true, false)) {
            return;
        }

        try {
            if (producer != null) {
                producer.close(Duration.ofSeconds(5));
                producer = null;
            }
            addInfo("KafkaAppender stopped successfully");
        } catch (Exception e) {
            addError("Error stopping KafkaAppender", e);
        }
    }

    @Override
    protected void append(ILoggingEvent event) {
        if (!started.get() || producer == null) {
            return;
        }

        try {
            // 创建日志消息
            LogMessage logMessage = createLogMessage(event);

            // 序列化消息
            String messageJson = objectMapper.writeValueAsString(logMessage);

            // 创建Kafka记录
            ProducerRecord<String, String> record =
                    new ProducerRecord<>(topic, logMessage.getThreadName(), messageJson);

            // 异步发送消息
            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    addError("Failed to send log message to Kafka", exception);
                }
                // 成功时不打印日志，避免日志过多
            });

        } catch (Exception e) {
            addError("Error appending log event to Kafka", e);
        }
    }

    /**
     * 创建生产者配置
     */
    private Properties createProducerProperties() {
        Properties props = new Properties();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, clientId != null ? clientId : "kafka-appender");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);
        props.put(ProducerConfig.ACKS_CONFIG, acks);
        props.put(ProducerConfig.RETRIES_CONFIG, retries);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
        props.put(ProducerConfig.LINGER_MS_CONFIG, lingerMs);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemory);
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, compressionType);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, enableIdempotence);
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, requestTimeoutMs);
        props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, deliveryTimeoutMs);
        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, maxBlockMs);

        return props;
    }

    /**
     * 创建日志消息对象
     */
    private LogMessage createLogMessage(ILoggingEvent event) {
        LogMessage logMessage = new LogMessage();

        logMessage.setTimestamp(event.getTimeStamp());
        logMessage.setLevel(event.getLevel().toString());
        logMessage.setLoggerName(event.getLoggerName());
        logMessage.setThreadName(event.getThreadName());
        logMessage.setMessage(event.getFormattedMessage());
        logMessage.setThrowable(event.getThrowableProxy());

        // 添加MDC信息
        if (event.getMDCPropertyMap() != null && !event.getMDCPropertyMap().isEmpty()) {
            logMessage.setMdc(event.getMDCPropertyMap());
        }

        // 添加自定义字段
        Map<String, Object> customFields = new HashMap<>();
        customFields.put("hostname", LoggingUtils.getHostname());
        customFields.put("application", LoggingUtils.getApplicationName());
        logMessage.setCustomFields(customFields);

        return logMessage;
    }

    // Getter和Setter方法
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

    public Layout<ILoggingEvent> getLayout() {
        return layout;
    }

    public void setLayout(Layout<ILoggingEvent> layout) {
        this.layout = layout;
    }
}
