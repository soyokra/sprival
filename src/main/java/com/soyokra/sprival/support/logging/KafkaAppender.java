package com.soyokra.sprival.support.logging;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
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
 * Kafka日志追加器 - 优化版本
 * 
 * 基于Logback框架最佳实践设计的Kafka日志追加器，提供： - 异步处理和批处理机制 - 完善的异常处理和状态管理 - 性能监控和健康检查 - 遵循Logback框架规范
 * 
 * @author sprival
 * @since 2.0.0
 */
public class KafkaAppender extends AppenderBase<ILoggingEvent> {

    // 异步处理相关
    private BlockingQueue<ILoggingEvent> eventQueue;
    private Thread[] workerThreads;
    private final AtomicBoolean workerRunning = new AtomicBoolean(false);

    // 统计信息
    private final AtomicLong totalEvents = new AtomicLong(0);
    private final AtomicLong successfulEvents = new AtomicLong(0);
    private final AtomicLong failedEvents = new AtomicLong(0);
    private final AtomicLong droppedEvents = new AtomicLong(0);

    // 批处理相关
    private final java.util.List<ILoggingEvent> batchBuffer = new java.util.ArrayList<>();
    private final Object batchLock = new Object();
    private long lastBatchTime = 0;
    private int maxBatchSize = 100;
    private long batchTimeoutMs = 1000;
    private boolean enableBatching = true;

    // 连接容错配置
    private boolean enableConnectionFallback = true;
    private String fallbackFilePath = "logs/kafka-fallback.log";
    private int maxConnectionRetries = 5;
    private long connectionRetryIntervalMs = 5000;

    // 异步处理配置
    private int queueCapacity = 10000;
    private boolean asyncMode = true;
    private int workerThreadCount = 1;

    // Kafka配置属性
    private String bootstrapServers;
    private String topic;
    private String clientId;
    private String keySerializer = StringSerializer.class.getName();
    private String valueSerializer = StringSerializer.class.getName();
    private String acks = "1";
    private int retries = 3;
    private int kafkaBatchSize = 16384;
    private int lingerMs = 1;
    private long bufferMemory = 33554432L;
    private String compressionType = "none";
    private boolean enableIdempotence = false;
    private int requestTimeoutMs = 30000;
    private int deliveryTimeoutMs = 120000;
    private int maxBlockMs = 60000;
    private int shutdownTimeoutSeconds = 5;

    // 内部组件
    private Producer<String, String> producer;
    private Layout<ILoggingEvent> layout;
    private ObjectMapper objectMapper;
    private Validator validator;
    private KafkaAppenderMetrics metrics;
    private KafkaConnectionManager connectionManager;

    @Override
    public void start() {
        addInfo("KafkaAppender.start() called");

        if (isStarted()) {
            addWarn("KafkaAppender already started, skipping");
            return;
        }

        try {
            // 验证配置参数
            if (!validateConfiguration()) {
                addError("Configuration validation failed");
                return;
            }

            // 创建Kafka生产者配置
            Properties props = createProducerProperties();
            addInfo("Created producer properties with " + props.size() + " configurations");

            // 创建Kafka生产者
            producer = new KafkaProducer<>(props);
            addInfo("Created KafkaProducer instance");

            // 初始化JSON序列化器
            objectMapper = new ObjectMapper();
            addInfo("Initialized ObjectMapper");

            // 初始化验证器
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            validator = factory.getValidator();
            addInfo("Initialized Validator");

            // 初始化监控指标
            metrics =
                    new KafkaAppenderMetrics(getName() != null ? getName() : "KafkaAppender", null);
            addInfo("Initialized Metrics");

            // 初始化连接管理器
            Properties connectionConfig = new Properties();
            connectionConfig.putAll(props);
            connectionConfig.setProperty("connection.maxRetryAttempts",
                    String.valueOf(maxConnectionRetries));
            connectionConfig.setProperty("connection.retryIntervalMs",
                    String.valueOf(connectionRetryIntervalMs));
            connectionConfig.setProperty("connection.timeoutMs", String.valueOf(requestTimeoutMs));

            connectionManager = new KafkaConnectionManager(connectionConfig,
                    enableConnectionFallback, fallbackFilePath);
            addInfo("Initialized Connection Manager");

            // 启动异步处理（如果启用）
            if (asyncMode) {
                startAsyncWorker();
            }

            // 调用父类start方法，设置started状态
            super.start();
            addInfo("KafkaAppender started successfully with bootstrapServers: " + bootstrapServers
                    + ", topic: " + topic + ", asyncMode: " + asyncMode);

        } catch (Exception e) {
            addError("Failed to start KafkaAppender", e);
            // 清理已初始化的资源
            cleanup();
        }
    }

    /**
     * 启动异步工作线程
     */
    private void startAsyncWorker() {
        eventQueue = new LinkedBlockingQueue<>(queueCapacity);
        workerThreads = new Thread[workerThreadCount];
        workerRunning.set(true);

        for (int i = 0; i < workerThreadCount; i++) {
            workerThreads[i] = new Thread(this::asyncWorkerLoop, "KafkaAppender-Worker-" + i);
            workerThreads[i].setDaemon(true);
            workerThreads[i].start();
        }

        addInfo("Async worker threads started - count: " + workerThreadCount + ", queue capacity: "
                + queueCapacity);
    }

    /**
     * 异步工作线程主循环
     */
    private void asyncWorkerLoop() {
        addInfo("Async worker thread started");

        while (workerRunning.get() && isStarted()) {
            try {
                // 从队列中获取事件，支持超时
                ILoggingEvent event =
                        eventQueue.poll(100, java.util.concurrent.TimeUnit.MILLISECONDS);
                if (event != null) {
                    processEvent(event);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                addWarn("Async worker thread interrupted");
                break;
            } catch (Exception e) {
                addError("Error in async worker thread", e);
            }
        }

        addInfo("Async worker thread stopped");
    }

    /**
     * 处理单个日志事件
     */
    private void processEvent(ILoggingEvent event) {
        io.micrometer.core.instrument.Timer.Sample sample = metrics.startEventProcessingTimer();
        try {
            totalEvents.incrementAndGet();
            metrics.incrementTotalEvents();

            if (enableBatching) {
                addToBatch(event);
            } else {
                sendSingleEvent(event);
            }

        } catch (Exception e) {
            failedEvents.incrementAndGet();
            metrics.incrementFailedEvents();
            addError("Error processing log event", e);
        } finally {
            metrics.stopEventProcessingTimer(sample);
        }
    }

    /**
     * 添加事件到批处理缓冲区
     */
    private void addToBatch(ILoggingEvent event) {
        synchronized (batchLock) {
            batchBuffer.add(event);
            metrics.updateBatchBufferSize(batchBuffer.size());

            // 检查是否需要发送批次
            boolean shouldSend = false;
            if (batchBuffer.size() >= maxBatchSize) {
                shouldSend = true;
                addInfo("Batch size reached, sending batch of " + batchBuffer.size() + " events");
            } else if (batchTimeoutMs > 0 && lastBatchTime > 0) {
                long timeSinceLastBatch = System.currentTimeMillis() - lastBatchTime;
                if (timeSinceLastBatch >= batchTimeoutMs) {
                    shouldSend = true;
                    addInfo("Batch timeout reached, sending batch of " + batchBuffer.size()
                            + " events");
                }
            }

            if (shouldSend) {
                sendBatch();
            } else if (lastBatchTime == 0) {
                lastBatchTime = System.currentTimeMillis();
            }
        }
    }

    /**
     * 发送单个事件
     */
    private void sendSingleEvent(ILoggingEvent event) {
        try {
            LogMessage logMessage = createLogMessage(event);
            String messageJson = objectMapper.writeValueAsString(logMessage);
            ProducerRecord<String, String> record =
                    new ProducerRecord<>(topic, logMessage.getThreadName(), messageJson);

            if (connectionManager.sendMessage(record)) {
                successfulEvents.incrementAndGet();
                metrics.incrementSuccessfulEvents();
            } else {
                failedEvents.incrementAndGet();
                metrics.incrementFailedEvents();
                addError("Failed to send log to Kafka");
            }
        } catch (Exception e) {
            failedEvents.incrementAndGet();
            addError("Error sending single event", e);
        }
    }

    /**
     * 发送批次事件
     */
    private void sendBatch() {
        if (batchBuffer.isEmpty()) {
            return;
        }

        io.micrometer.core.instrument.Timer.Sample sample = metrics.startBatchProcessingTimer();
        try {
            java.util.List<ILoggingEvent> eventsToSend = new java.util.ArrayList<>(batchBuffer);
            batchBuffer.clear();
            metrics.updateBatchBufferSize(0);
            lastBatchTime = System.currentTimeMillis();

            addInfo("Sending batch of " + eventsToSend.size() + " events to Kafka");

            // 为批次中的每个事件创建记录
            for (ILoggingEvent event : eventsToSend) {
                LogMessage logMessage = createLogMessage(event);
                String messageJson = objectMapper.writeValueAsString(logMessage);
                ProducerRecord<String, String> record =
                        new ProducerRecord<>(topic, logMessage.getThreadName(), messageJson);

                if (connectionManager.sendMessage(record)) {
                    successfulEvents.incrementAndGet();
                    metrics.incrementSuccessfulEvents();
                } else {
                    failedEvents.incrementAndGet();
                    metrics.incrementFailedEvents();
                    addError("Failed to send batch event to Kafka");
                }
            }

        } catch (Exception e) {
            failedEvents.addAndGet(batchBuffer.size());
            metrics.incrementFailedEvents();
            addError("Error sending batch", e);
            batchBuffer.clear(); // 清空缓冲区避免重复发送
        } finally {
            metrics.stopBatchProcessingTimer(sample);
        }
    }

    /**
     * 清理资源
     */
    private void cleanup() {
        if (producer != null) {
            try {
                producer.close(Duration.ofSeconds(shutdownTimeoutSeconds));
            } catch (Exception e) {
                addError("Error closing Kafka producer", e);
            }
            producer = null;
        }

        if (workerThreads != null) {
            workerRunning.set(false);
            for (Thread thread : workerThreads) {
                if (thread != null && thread.isAlive()) {
                    thread.interrupt();
                }
            }
        }
    }

    @Override
    public void stop() {
        if (!isStarted()) {
            return;
        }

        try {
            // 停止异步工作线程
            if (asyncMode && workerThreads != null) {
                stopAsyncWorker();
            }

            // 发送剩余的批次
            if (enableBatching) {
                sendRemainingBatch();
            }

            // 关闭连接管理器
            if (connectionManager != null) {
                connectionManager.close();
                addInfo("Connection Manager closed successfully");
            }

            // 关闭Kafka Producer
            if (producer != null) {
                producer.close(Duration.ofSeconds(shutdownTimeoutSeconds));
                addInfo("KafkaProducer closed successfully");
            }

            addInfo("KafkaAppender stopped successfully");

        } catch (Exception e) {
            addError("Error stopping KafkaAppender", e);
        } finally {
            // 确保资源被清理
            cleanup();
            // 调用父类stop方法，设置started状态为false
            super.stop();
        }
    }

    /**
     * 发送剩余的批次
     */
    private void sendRemainingBatch() {
        synchronized (batchLock) {
            if (!batchBuffer.isEmpty()) {
                addInfo("Sending remaining batch of " + batchBuffer.size()
                        + " events before shutdown");
                sendBatch();
            }
        }
    }

    /**
     * 停止异步工作线程
     */
    private void stopAsyncWorker() {
        if (workerThreads != null) {
            workerRunning.set(false);

            // 中断所有工作线程
            for (Thread thread : workerThreads) {
                if (thread != null && thread.isAlive()) {
                    thread.interrupt();
                }
            }

            // 等待所有线程结束
            int aliveCount = 0;
            for (Thread thread : workerThreads) {
                if (thread != null && thread.isAlive()) {
                    try {
                        thread.join(5000); // 等待最多5秒
                        if (thread.isAlive()) {
                            aliveCount++;
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        addWarn("Interrupted while waiting for worker thread to stop");
                    }
                }
            }

            if (aliveCount > 0) {
                addWarn("Async worker threads did not stop gracefully: " + aliveCount
                        + " threads still alive");
            } else {
                addInfo("All async worker threads stopped gracefully");
            }
        }
    }

    @Override
    protected void append(ILoggingEvent event) {
        if (!isStarted()) {
            return;
        }

        try {
            totalEvents.incrementAndGet();

            if (asyncMode) {
                // 异步模式：将事件放入队列
                if (!eventQueue.offer(event)) {
                    // 队列满了，丢弃事件
                    droppedEvents.incrementAndGet();
                    metrics.incrementDroppedEvents();
                    addWarn("Event queue is full, dropping event. Queue size: "
                            + eventQueue.size());
                }
                metrics.updateQueueSize(eventQueue.size());
            } else {
                // 同步模式：直接处理事件
                processEvent(event);
            }

        } catch (Exception e) {
            failedEvents.incrementAndGet();
            addError("Error in KafkaAppender.append(): " + e.getMessage(), e);
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
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, kafkaBatchSize);
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
        // 处理异常信息
        if (event.getThrowableProxy() != null) {
            logMessage.setThrowable(event.getThrowableProxy().toString());
        }

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
        addInfo("KafkaAppender.setBootstrapServers() called with: " + bootstrapServers);
        this.bootstrapServers = bootstrapServers;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        addInfo("KafkaAppender.setTopic() called with: " + topic);
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
        return kafkaBatchSize;
    }

    public void setBatchSize(int batchSize) {
        this.kafkaBatchSize = batchSize;
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

    public int getShutdownTimeoutSeconds() {
        return shutdownTimeoutSeconds;
    }

    public void setShutdownTimeoutSeconds(int shutdownTimeoutSeconds) {
        this.shutdownTimeoutSeconds = shutdownTimeoutSeconds;
    }

    /**
     * 检查KafkaAppender是否健康运行
     * 
     * @return true如果已启动且生产者可用，否则false
     */
    /**
     * 检查KafkaAppender是否健康
     */
    public boolean isHealthy() {
        if (!isStarted() || producer == null) {
            return false;
        }

        if (asyncMode && workerThreads != null) {
            // 检查是否至少有一个工作线程存活
            for (Thread thread : workerThreads) {
                if (thread != null && thread.isAlive()) {
                    return true;
                }
            }
            return false; // 异步模式下没有存活的工作线程
        }

        return true; // 同步模式或异步模式但工作线程正常
    }

    /**
     * 验证配置参数
     */
    private boolean validateConfiguration() {
        try {
            // 创建配置对象进行验证
            KafkaAppenderProperties props = new KafkaAppenderProperties();
            props.setBootstrapServers(bootstrapServers);
            props.setTopic(topic);
            props.setClientId(clientId);
            props.setRetries(retries);
            props.setKafkaBatchSize(kafkaBatchSize);
            props.setLingerMs(lingerMs);
            props.setBufferMemory(bufferMemory);
            props.setRequestTimeoutMs(requestTimeoutMs);
            props.setDeliveryTimeoutMs(deliveryTimeoutMs);
            props.setMaxBlockMs(maxBlockMs);
            props.setShutdownTimeoutSeconds(shutdownTimeoutSeconds);
            props.setQueueCapacity(queueCapacity);
            props.setWorkerThreadCount(workerThreadCount);
            props.setMaxBatchSize(maxBatchSize);
            props.setBatchTimeoutMs(batchTimeoutMs);

            // 执行验证
            Set<ConstraintViolation<KafkaAppenderProperties>> violations =
                    validator.validate(props);

            if (!violations.isEmpty()) {
                for (ConstraintViolation<KafkaAppenderProperties> violation : violations) {
                    addError("Configuration validation failed: " + violation.getPropertyPath() + " "
                            + violation.getMessage() + " (value: " + violation.getInvalidValue()
                            + ")");
                }
                return false;
            }

            addInfo("Configuration validation passed");
            return true;

        } catch (Exception e) {
            addError("Configuration validation error", e);
            return false;
        }
    }

    /**
     * 获取统计信息
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalEvents", totalEvents.get());
        stats.put("successfulEvents", successfulEvents.get());
        stats.put("failedEvents", failedEvents.get());
        stats.put("droppedEvents", droppedEvents.get());
        stats.put("queueSize", asyncMode ? eventQueue.size() : 0);
        stats.put("queueCapacity", queueCapacity);
        stats.put("asyncMode", asyncMode);
        // 计算存活的工作线程数
        int aliveThreads = 0;
        if (asyncMode && workerThreads != null) {
            for (Thread thread : workerThreads) {
                if (thread != null && thread.isAlive()) {
                    aliveThreads++;
                }
            }
        }
        stats.put("workerThreadCount", workerThreadCount);
        stats.put("aliveWorkerThreads", aliveThreads);
        stats.put("workerThreadAlive", aliveThreads > 0);
        stats.put("isHealthy", isHealthy());

        // 计算成功率
        long total = totalEvents.get();
        if (total > 0) {
            stats.put("successRate", (double) successfulEvents.get() / total * 100);
        } else {
            stats.put("successRate", 0.0);
        }

        return stats;
    }

    /**
     * 重置统计信息
     */
    public void resetStatistics() {
        totalEvents.set(0);
        successfulEvents.set(0);
        failedEvents.set(0);
        droppedEvents.set(0);
        addInfo("Statistics reset");
    }

    // 异步处理配置的getter和setter
    public int getQueueCapacity() {
        return queueCapacity;
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public boolean isAsyncMode() {
        return asyncMode;
    }

    public void setAsyncMode(boolean asyncMode) {
        this.asyncMode = asyncMode;
    }

    public int getWorkerThreadCount() {
        return workerThreadCount;
    }

    public void setWorkerThreadCount(int workerThreadCount) {
        this.workerThreadCount = workerThreadCount;
    }

    // 批处理配置的getter和setter
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

    public boolean isEnableBatching() {
        return enableBatching;
    }

    public void setEnableBatching(boolean enableBatching) {
        this.enableBatching = enableBatching;
    }

    // Kafka配置的getter和setter（重命名后的字段）
    public int getKafkaBatchSize() {
        return kafkaBatchSize;
    }

    public void setKafkaBatchSize(int kafkaBatchSize) {
        this.kafkaBatchSize = kafkaBatchSize;
    }

    // 连接容错配置的getter和setter
    public boolean isEnableConnectionFallback() {
        return enableConnectionFallback;
    }

    public void setEnableConnectionFallback(boolean enableConnectionFallback) {
        this.enableConnectionFallback = enableConnectionFallback;
    }

    public String getFallbackFilePath() {
        return fallbackFilePath;
    }

    public void setFallbackFilePath(String fallbackFilePath) {
        this.fallbackFilePath = fallbackFilePath;
    }

    public int getMaxConnectionRetries() {
        return maxConnectionRetries;
    }

    public void setMaxConnectionRetries(int maxConnectionRetries) {
        this.maxConnectionRetries = maxConnectionRetries;
    }

    public long getConnectionRetryIntervalMs() {
        return connectionRetryIntervalMs;
    }

    public void setConnectionRetryIntervalMs(long connectionRetryIntervalMs) {
        this.connectionRetryIntervalMs = connectionRetryIntervalMs;
    }

    /**
     * 重放降级文件中的消息
     */
    public int replayFallbackMessages() {
        if (connectionManager != null) {
            return connectionManager.replayFallbackMessages();
        }
        return 0;
    }

    /**
     * 检查连接状态
     */
    public boolean isKafkaConnected() {
        return connectionManager != null && connectionManager.isConnected();
    }
}
