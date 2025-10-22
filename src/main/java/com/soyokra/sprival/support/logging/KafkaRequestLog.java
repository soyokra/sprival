package com.soyokra.sprival.support.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.RequestLog;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Jetty访问日志Kafka输出器 将Jetty访问日志发送到Kafka
 * 
 * @author Sprival Team
 * @since 1.0.0
 */
public class KafkaRequestLog extends AbstractLifeCycle implements RequestLog {

    private static final Logger log = LoggerFactory.getLogger(KafkaRequestLog.class);

    private final SprivalLoggingProperties properties;
    private Producer<String, String> producer;
    private ObjectMapper objectMapper;
    private Set<String> ignorePaths;

    public KafkaRequestLog(SprivalLoggingProperties properties, Set<String> ignorePaths) {
        this.properties = properties;
        this.ignorePaths = ignorePaths;
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();

        try {
            // 创建Kafka生产者配置
            Properties props = createProducerProperties();

            // 创建Kafka生产者
            producer = new KafkaProducer<>(props);

            // 初始化JSON序列化器
            objectMapper = new ObjectMapper();

            log.info("KafkaRequestLog started successfully");

        } catch (Exception e) {
            log.error("Failed to start KafkaRequestLog", e);
            throw e;
        }
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();

        try {
            if (producer != null) {
                producer.close();
                producer = null;
            }
            log.info("KafkaRequestLog stopped successfully");
        } catch (Exception e) {
            log.error("Error stopping KafkaRequestLog", e);
            throw e;
        }
    }

    @Override
    public void log(Request request, Response response) {
        if (!isStarted() || producer == null) {
            return;
        }

        try {
            String requestUri = request.getRequestURI();

            // 检查是否需要忽略此路径
            if (shouldIgnore(requestUri)) {
                return;
            }

            // 创建访问日志消息
            JettyAccessLogMessage logMessage = createAccessLogMessage(request, response);

            // 序列化消息
            String messageJson = objectMapper.writeValueAsString(logMessage);

            // 创建Kafka记录
            ProducerRecord<String, String> record = new ProducerRecord<>(
                    properties.getJettyAccess().getTopic(), logMessage.getClientIp(), messageJson);

            // 异步发送消息
            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    log.error("Failed to send access log message to Kafka", exception);
                }
            });

        } catch (Exception e) {
            log.error("Error logging request to Kafka", e);
        }
    }

    /**
     * 检查是否需要忽略此路径
     */
    private boolean shouldIgnore(String uri) {
        if (ignorePaths == null || ignorePaths.isEmpty()) {
            return false;
        }

        for (String ignorePath : ignorePaths) {
            if (uri.startsWith(ignorePath)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 创建访问日志消息对象
     */
    private JettyAccessLogMessage createAccessLogMessage(Request request, Response response) {
        JettyAccessLogMessage logMessage = new JettyAccessLogMessage();

        logMessage.setTimestamp(request.getTimeStamp());
        logMessage.setClientIp(request.getRemoteAddr());
        logMessage.setMethod(request.getMethod());
        logMessage.setUri(request.getRequestURI());
        logMessage.setProtocol(request.getProtocol());
        logMessage.setStatusCode(response.getStatus());
        logMessage.setResponseBytes(response.getHttpChannel().getBytesWritten());
        logMessage.setProcessingTime(System.currentTimeMillis() - request.getTimeStamp());
        logMessage.setUserAgent(request.getHeader("User-Agent"));
        logMessage.setReferer(request.getHeader("Referer"));

        // 添加自定义字段
        Map<String, Object> customFields = new HashMap<>();
        customFields.put("hostname", LoggingUtils.getHostname());
        customFields.put("application", LoggingUtils.getApplicationName());
        logMessage.setCustomFields(customFields);

        return logMessage;
    }

    /**
     * 创建生产者配置
     */
    private Properties createProducerProperties() {
        SprivalLoggingProperties.JettyAccessConfig config = properties.getJettyAccess();

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServers());
        props.put(ProducerConfig.CLIENT_ID_CONFIG, config.getClientId());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, config.getAcks());
        props.put(ProducerConfig.RETRIES_CONFIG, config.getRetries());
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, config.getBatchSize());
        props.put(ProducerConfig.LINGER_MS_CONFIG, config.getLingerMs());
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, config.getBufferMemory());
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, config.getCompressionType());
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, config.getRequestTimeoutMs());
        props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, config.getDeliveryTimeoutMs());
        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, config.getMaxBlockMs());

        return props;
    }
}

