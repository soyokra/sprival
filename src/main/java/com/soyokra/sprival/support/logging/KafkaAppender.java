package com.soyokra.sprival.support.logging;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
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
    private ExecutorService workerExecutor;
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

    // 统一配置管理
    private KafkaAppenderConfiguration configuration;

    // 内部组件
    private Producer<String, String> producer;
    private Layout<ILoggingEvent> layout;
    private ObjectMapper objectMapper;
    private Validator validator;
    private KafkaAppenderMetrics metrics;
    private KafkaConnectionManager connectionManager;

    // 缓存常用字段，避免重复获取（使用类级缓存，因为hostname和application在整个应用生命周期中不变）
    private static volatile String cachedHostname;
    private static volatile String cachedAppName;

    @Override
    public void start() {
        addInfo("KafkaAppender.start() called");

        if (isStarted()) {
            addWarn("KafkaAppender already started, skipping");
            return;
        }

        try {
            // 初始化验证器（必须在验证配置之前）
            if (validator == null) {
                try {
                    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
                    validator = factory.getValidator();
                    // 注意：使用默认工厂时不需要关闭，但如果使用自定义工厂则需要关闭
                    addInfo("Initialized Validator");
                } catch (Throwable e) {
                    // 在某些测试环境中，Hibernate Validator 可能无法初始化（如 logback.xml 配置问题）
                    // 这种情况下使用 null validator，让配置验证跳过 JSR-303 验证
                    addWarn("Failed to initialize Validator: " + e.getMessage()
                            + ". Configuration validation will be skipped.");
                    validator = null;
                }
            }

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

            // 初始化监控指标
            metrics =
                    new KafkaAppenderMetrics(getName() != null ? getName() : "KafkaAppender", null);
            addInfo("Initialized Metrics");

            // 初始化连接管理器
            Properties connectionConfig = new Properties();
            connectionConfig.putAll(props);
            connectionConfig.setProperty("connection.maxRetryAttempts",
                    String.valueOf(configuration.getMaxConnectionRetries()));
            connectionConfig.setProperty("connection.retryIntervalMs",
                    String.valueOf(configuration.getConnectionRetryIntervalMs()));
            connectionConfig.setProperty("connection.timeoutMs",
                    String.valueOf(configuration.getRequestTimeoutMs()));
            connectionConfig.setProperty("connection.topic", configuration.getTopic());

            connectionManager = new KafkaConnectionManager(connectionConfig,
                    configuration.isEnableConnectionFallback(),
                    configuration.getFallbackFilePath());
            addInfo("Initialized Connection Manager");

            // 启动异步处理（如果启用）
            if (configuration.isAsyncMode()) {
                startAsyncWorker();
            }

            // 调用父类start方法，设置started状态
            super.start();
            addInfo("KafkaAppender started successfully with bootstrapServers: "
                    + configuration.getBootstrapServers() + ", topic: " + configuration.getTopic()
                    + ", asyncMode: " + configuration.isAsyncMode());

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
        eventQueue = new LinkedBlockingQueue<>(configuration.getQueueCapacity());
        final long threadId = System.currentTimeMillis();
        workerExecutor = Executors.newFixedThreadPool(configuration.getWorkerThreadCount(), r -> {
            Thread t = new Thread(r, "KafkaAppender-Worker-" + threadId + "-" + System.nanoTime());
            t.setDaemon(true);
            return t;
        });
        workerRunning.set(true);

        for (int i = 0; i < configuration.getWorkerThreadCount(); i++) {
            workerExecutor.submit(this::asyncWorkerLoop);
        }

        addInfo("Async worker threads started - count: " + configuration.getWorkerThreadCount()
                + ", queue capacity: " + configuration.getQueueCapacity());
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
        io.micrometer.core.instrument.Timer.Sample sample = null;
        if (metrics != null) {
            sample = metrics.startEventProcessingTimer();
        }
        try {
            totalEvents.incrementAndGet();
            if (metrics != null) {
                metrics.incrementTotalEvents();
            }

            if (configuration != null && configuration.isEnableBatching()) {
                addToBatch(event);
            } else {
                sendSingleEvent(event);
            }

        } catch (Exception e) {
            failedEvents.incrementAndGet();
            if (metrics != null) {
                metrics.incrementFailedEvents();
            }
            addError("Unexpected error processing log event", e);
        } finally {
            if (metrics != null && sample != null) {
                metrics.stopEventProcessingTimer(sample);
            }
        }
    }

    /**
     * 添加事件到批处理缓冲区
     */
    private void addToBatch(ILoggingEvent event) {
        synchronized (batchLock) {
            if (configuration == null) {
                return;
            }

            // 首次添加事件时，初始化时间戳
            if (lastBatchTime == 0) {
                lastBatchTime = System.currentTimeMillis();
            }

            batchBuffer.add(event);
            if (metrics != null) {
                metrics.updateBatchBufferSize(batchBuffer.size());
            }

            // 检查是否需要发送批次
            boolean shouldSend = false;

            // 检查是否达到批次大小
            if (batchBuffer.size() >= configuration.getMaxBatchSize()) {
                shouldSend = true;
                addInfo("Batch size reached, sending batch of " + batchBuffer.size() + " events");
            }
            // 检查是否超时（只有当有事件时才检查超时）
            else if (configuration.getBatchTimeoutMs() > 0 && !batchBuffer.isEmpty()) {
                long currentTime = System.currentTimeMillis();
                long timeSinceLastBatch = currentTime - lastBatchTime;
                if (timeSinceLastBatch >= configuration.getBatchTimeoutMs()) {
                    shouldSend = true;
                    addInfo("Batch timeout reached, sending batch of " + batchBuffer.size()
                            + " events");
                }
            }

            if (shouldSend) {
                sendBatch();
            }
        }
    }

    /**
     * 发送单个事件
     */
    private void sendSingleEvent(ILoggingEvent event) {
        if (configuration == null || objectMapper == null || connectionManager == null) {
            return;
        }

        try {
            LogMessage logMessage = createLogMessage(event);
            String messageJson = objectMapper.writeValueAsString(logMessage);
            ProducerRecord<String, String> record = new ProducerRecord<>(configuration.getTopic(),
                    logMessage.getThreadName(), messageJson);

            if (connectionManager.sendMessage(record)) {
                successfulEvents.incrementAndGet();
                if (metrics != null) {
                    metrics.incrementSuccessfulEvents();
                }
            } else {
                failedEvents.incrementAndGet();
                if (metrics != null) {
                    metrics.incrementFailedEvents();
                }
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
        if (batchBuffer.isEmpty() || configuration == null || objectMapper == null
                || connectionManager == null) {
            return;
        }

        io.micrometer.core.instrument.Timer.Sample sample = null;
        if (metrics != null) {
            sample = metrics.startBatchProcessingTimer();
        }
        java.util.List<ILoggingEvent> eventsToSend = null;
        int alreadyCountedFailed = 0;
        try {
            eventsToSend = new java.util.ArrayList<>(batchBuffer);
            batchBuffer.clear();
            if (metrics != null) {
                metrics.updateBatchBufferSize(0);
            }
            lastBatchTime = System.currentTimeMillis();

            addInfo("Sending batch of " + eventsToSend.size() + " events to Kafka");

            // 为批次中的每个事件创建记录
            for (ILoggingEvent event : eventsToSend) {
                try {
                    LogMessage logMessage = createLogMessage(event);
                    String messageJson = objectMapper.writeValueAsString(logMessage);
                    ProducerRecord<String, String> record = new ProducerRecord<>(
                            configuration.getTopic(), logMessage.getThreadName(), messageJson);

                    if (connectionManager.sendMessage(record)) {
                        successfulEvents.incrementAndGet();
                        if (metrics != null) {
                            metrics.incrementSuccessfulEvents();
                        }
                    } else {
                        failedEvents.incrementAndGet();
                        if (metrics != null) {
                            metrics.incrementFailedEvents();
                        }
                        alreadyCountedFailed++;
                        addError("Failed to send batch event to Kafka");
                    }
                } catch (Exception e) {
                    failedEvents.incrementAndGet();
                    if (metrics != null) {
                        metrics.incrementFailedEvents();
                    }
                    alreadyCountedFailed++;
                    addError("Error sending batch event", e);
                }
            }

        } catch (Exception e) {
            // 只有未在循环中计数的失败事件才需要补充计数
            int totalEvents = eventsToSend != null ? eventsToSend.size() : 0;
            int uncountedFailed = totalEvents - alreadyCountedFailed;
            if (uncountedFailed > 0) {
                failedEvents.addAndGet(uncountedFailed);
                if (metrics != null) {
                    metrics.incrementFailedEvents();
                }
            }
            addError("Error sending batch", e);
        } finally {
            if (metrics != null && sample != null) {
                metrics.stopBatchProcessingTimer(sample);
            }
        }
    }

    /**
     * 清理资源
     */
    private void cleanup() {
        // 1. 清理 Kafka Producer
        cleanupProducer();

        // 2. 清理连接管理器
        cleanupConnectionManager();

        // 3. 清理工作线程
        cleanupWorkerThreads();

        // 4. 清理其他资源
        cleanupOtherResources();
    }

    /**
     * 清理 Kafka Producer
     */
    private void cleanupProducer() {
        if (producer != null) {
            try {
                int timeoutSeconds =
                        configuration != null ? configuration.getShutdownTimeoutSeconds() : 5;
                producer.close(Duration.ofSeconds(timeoutSeconds));
                addInfo("Kafka Producer closed successfully");
            } catch (Exception e) {
                addError("Error closing Kafka producer", e);
            } finally {
                producer = null;
            }
        }
    }

    /**
     * 清理连接管理器
     */
    private void cleanupConnectionManager() {
        if (connectionManager != null) {
            try {
                connectionManager.close();
                addInfo("Connection Manager closed successfully");
            } catch (Exception e) {
                addError("Error closing Connection Manager", e);
            } finally {
                connectionManager = null;
            }
        }
    }

    /**
     * 清理工作线程 注意：此方法在stop()中通过stopAsyncWorker()调用，这里只做最终清理
     */
    private void cleanupWorkerThreads() {
        if (workerExecutor != null) {
            // workerRunning已在stopAsyncWorker()中设置为false，这里不需要重复设置
            try {
                // 如果线程池还没关闭，强制关闭
                if (!workerExecutor.isShutdown()) {
                    workerExecutor.shutdownNow();
                }
            } catch (Exception e) {
                addError("Error shutting down worker executor", e);
            } finally {
                workerExecutor = null;
            }
        }
    }

    /**
     * 清理其他资源
     */
    private void cleanupOtherResources() {
        // 清理批处理缓冲区
        synchronized (batchLock) {
            if (!batchBuffer.isEmpty()) {
                addWarn("Clearing " + batchBuffer.size() + " remaining events in batch buffer");
                batchBuffer.clear();
            }
        }

        // 清理事件队列
        if (eventQueue != null && !eventQueue.isEmpty()) {
            int remainingEvents = eventQueue.size();
            eventQueue.clear();
            addWarn("Cleared " + remainingEvents + " remaining events from queue");
        }
    }

    @Override
    public void stop() {
        if (!isStarted()) {
            return;
        }

        try {
            // 停止异步工作线程
            if (configuration != null && configuration.isAsyncMode() && workerExecutor != null) {
                stopAsyncWorker();
            }

            // 发送剩余的批次
            if (configuration != null && configuration.isEnableBatching()) {
                sendRemainingBatch();
            }

            // 关闭连接管理器
            if (connectionManager != null) {
                connectionManager.close();
                addInfo("Connection Manager closed successfully");
            }

            // 关闭Kafka Producer
            if (producer != null) {
                int timeoutSeconds =
                        configuration != null ? configuration.getShutdownTimeoutSeconds() : 5;
                producer.close(Duration.ofSeconds(timeoutSeconds));
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
        if (workerExecutor != null) {
            workerRunning.set(false);

            try {
                // 优雅关闭：停止接受新任务，等待现有任务完成
                workerExecutor.shutdown();

                // 等待所有任务完成，最多等待3秒
                if (!workerExecutor.awaitTermination(3, TimeUnit.SECONDS)) {
                    addWarn("Worker threads did not terminate gracefully, forcing shutdown");
                    // 强制关闭
                    workerExecutor.shutdownNow();

                    // 再次等待强制关闭完成，总共不超过5秒
                    if (!workerExecutor.awaitTermination(2, TimeUnit.SECONDS)) {
                        addError("Worker threads did not terminate after forced shutdown");
                    }
                } else {
                    addInfo("All async worker threads stopped gracefully");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                addWarn("Interrupted while waiting for worker threads to stop");
                // 强制关闭
                workerExecutor.shutdownNow();
            } finally {
                workerExecutor = null;
            }
        }
    }

    @Override
    protected void append(ILoggingEvent event) {
        if (!isStarted() || configuration == null) {
            return;
        }

        try {
            if (configuration.isAsyncMode()) {
                // 异步模式：将事件放入队列
                if (eventQueue != null && !eventQueue.offer(event)) {
                    // 队列满了，丢弃事件
                    droppedEvents.incrementAndGet();
                    if (metrics != null) {
                        metrics.incrementDroppedEvents();
                    }
                    addWarn("Event queue is full, dropping event. Queue size: "
                            + eventQueue.size());
                }
                if (eventQueue != null && metrics != null) {
                    metrics.updateQueueSize(eventQueue.size());
                }
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

        // 使用统一配置
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, configuration.getBootstrapServers());
        props.put(ProducerConfig.CLIENT_ID_CONFIG,
                configuration.getClientId() != null ? configuration.getClientId()
                        : "kafka-appender");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, configuration.getKeySerializer());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, configuration.getValueSerializer());
        props.put(ProducerConfig.ACKS_CONFIG, configuration.getAcks());
        props.put(ProducerConfig.RETRIES_CONFIG, configuration.getRetries());
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, configuration.getKafkaBatchSize());
        props.put(ProducerConfig.LINGER_MS_CONFIG, configuration.getLingerMs());
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, configuration.getBufferMemory());
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, configuration.getCompressionType());
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, configuration.isEnableIdempotence());
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, configuration.getRequestTimeoutMs());
        props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, configuration.getDeliveryTimeoutMs());
        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, configuration.getMaxBlockMs());

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
        customFields.put("hostname", getCachedHostname());
        customFields.put("application", getCachedAppName());
        logMessage.setCustomFields(customFields);

        return logMessage;
    }

    /**
     * 获取缓存的主机名
     */
    private String getCachedHostname() {
        if (cachedHostname == null) {
            synchronized (KafkaAppender.class) {
                if (cachedHostname == null) {
                    cachedHostname = LoggingUtils.getHostname();
                }
            }
        }
        return cachedHostname;
    }

    /**
     * 获取缓存的应用名称
     */
    private String getCachedAppName() {
        if (cachedAppName == null) {
            synchronized (KafkaAppender.class) {
                if (cachedAppName == null) {
                    cachedAppName = LoggingUtils.getApplicationName();
                }
            }
        }
        return cachedAppName;
    }

    // Layout 相关方法
    public Layout<ILoggingEvent> getLayout() {
        return layout;
    }

    public void setLayout(Layout<ILoggingEvent> layout) {
        this.layout = layout;
    }

    /**
     * 检查KafkaAppender是否健康
     * 
     * @return true如果已启动且生产者可用，否则false
     */
    public boolean isHealthy() {
        if (!isStarted() || producer == null) {
            return false;
        }

        if (configuration != null && configuration.isAsyncMode() && workerExecutor != null) {
            // 检查 ExecutorService 是否关闭
            return !workerExecutor.isShutdown() && !workerExecutor.isTerminated();
        }

        return true; // 同步模式或异步模式但工作线程正常
    }

    /**
     * 验证配置参数
     */
    private boolean validateConfiguration() {
        try {
            // 如果配置对象为空，创建默认配置
            if (configuration == null) {
                configuration = createDefaultConfiguration();
            }

            // 执行 JSR-303 验证（如果 validator 可用）
            if (validator != null) {
                Set<ConstraintViolation<KafkaAppenderConfiguration>> violations =
                        validator.validate(configuration);

                if (!violations.isEmpty()) {
                    for (ConstraintViolation<KafkaAppenderConfiguration> violation : violations) {
                        addError("Configuration validation failed: " + violation.getPropertyPath()
                                + " " + violation.getMessage() + " (value: "
                                + violation.getInvalidValue() + ")");
                    }
                    return false;
                }
                addInfo("Configuration validation passed: "
                        + configuration.getConfigurationSummary());
            } else {
                // validator 不可用时，只进行基本检查
                if (configuration.getBootstrapServers() == null
                        || configuration.getBootstrapServers().isEmpty()) {
                    addError("Bootstrap servers cannot be empty");
                    return false;
                }
                if (configuration.getTopic() == null || configuration.getTopic().isEmpty()) {
                    addError("Topic cannot be empty");
                    return false;
                }
                addInfo("Basic configuration validation passed (JSR-303 validation skipped)");
            }

            return true;

        } catch (Exception e) {
            addError("Configuration validation error", e);
            return false;
        }
    }

    /**
     * 创建默认配置
     */
    private KafkaAppenderConfiguration createDefaultConfiguration() {
        return KafkaAppenderConfiguration.builder().bootstrapServers("localhost:9092")
                .topic("default-topic").clientId("kafka-appender").asyncMode(true)
                .workerThreadCount(1).queueCapacity(10000).enableBatching(true).maxBatchSize(100)
                .batchTimeoutMs(1000).enableConnectionFallback(true)
                .fallbackFilePath("logs/kafka-fallback.log").build();
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

        // 安全获取队列大小，避免NPE
        int queueSize = 0;
        if (configuration != null && configuration.isAsyncMode() && eventQueue != null) {
            queueSize = eventQueue.size();
        }
        stats.put("queueSize", queueSize);

        stats.put("queueCapacity", configuration != null ? configuration.getQueueCapacity() : 0);
        stats.put("asyncMode", configuration != null ? configuration.isAsyncMode() : false);

        // 检查工作线程状态
        boolean workerThreadsAlive = false;
        if (configuration != null && configuration.isAsyncMode() && workerExecutor != null) {
            workerThreadsAlive = !workerExecutor.isShutdown() && !workerExecutor.isTerminated();
        }
        stats.put("workerThreadCount",
                configuration != null ? configuration.getWorkerThreadCount() : 0);
        stats.put("workerThreadAlive", workerThreadsAlive);
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
        return configuration != null ? configuration.getQueueCapacity() : 10000;
    }

    public void setQueueCapacity(int queueCapacity) {
        if (configuration != null) {
            configuration.setQueueCapacity(queueCapacity);
        }
    }

    public boolean isAsyncMode() {
        return configuration != null ? configuration.isAsyncMode() : true;
    }

    public void setAsyncMode(boolean asyncMode) {
        if (configuration != null) {
            configuration.setAsyncMode(asyncMode);
        }
    }

    public int getWorkerThreadCount() {
        return configuration != null ? configuration.getWorkerThreadCount() : 1;
    }

    public void setWorkerThreadCount(int workerThreadCount) {
        if (configuration != null) {
            configuration.setWorkerThreadCount(workerThreadCount);
        }
    }

    // 批处理配置的getter和setter
    public int getMaxBatchSize() {
        return configuration != null ? configuration.getMaxBatchSize() : 100;
    }

    public void setMaxBatchSize(int maxBatchSize) {
        if (configuration != null) {
            configuration.setMaxBatchSize(maxBatchSize);
        }
    }

    public long getBatchTimeoutMs() {
        return configuration != null ? configuration.getBatchTimeoutMs() : 1000;
    }

    public void setBatchTimeoutMs(long batchTimeoutMs) {
        if (configuration != null) {
            configuration.setBatchTimeoutMs(batchTimeoutMs);
        }
    }

    public boolean isEnableBatching() {
        return configuration != null ? configuration.isEnableBatching() : true;
    }

    public void setEnableBatching(boolean enableBatching) {
        if (configuration != null) {
            configuration.setEnableBatching(enableBatching);
        }
    }

    // 连接容错配置的getter和setter
    public boolean isEnableConnectionFallback() {
        return configuration != null ? configuration.isEnableConnectionFallback() : true;
    }

    public void setEnableConnectionFallback(boolean enableConnectionFallback) {
        if (configuration != null) {
            configuration.setEnableConnectionFallback(enableConnectionFallback);
        }
    }

    public String getFallbackFilePath() {
        return configuration != null ? configuration.getFallbackFilePath()
                : "logs/kafka-fallback.log";
    }

    public void setFallbackFilePath(String fallbackFilePath) {
        if (configuration != null) {
            configuration.setFallbackFilePath(fallbackFilePath);
        }
    }

    public int getMaxConnectionRetries() {
        return configuration != null ? configuration.getMaxConnectionRetries() : 5;
    }

    public void setMaxConnectionRetries(int maxConnectionRetries) {
        if (configuration != null) {
            configuration.setMaxConnectionRetries(maxConnectionRetries);
        }
    }

    public long getConnectionRetryIntervalMs() {
        return configuration != null ? configuration.getConnectionRetryIntervalMs() : 5000;
    }

    public void setConnectionRetryIntervalMs(long connectionRetryIntervalMs) {
        if (configuration != null) {
            configuration.setConnectionRetryIntervalMs(connectionRetryIntervalMs);
        }
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

    /**
     * 设置统一配置
     */
    public void setConfiguration(KafkaAppenderConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * 获取统一配置
     */
    public KafkaAppenderConfiguration getConfiguration() {
        return configuration;
    }

    // ========== 基础配置的getter和setter（向后兼容） ==========

    /**
     * 获取Bootstrap服务器地址
     */
    public String getBootstrapServers() {
        if (configuration == null) {
            ensureConfiguration();
        }
        return configuration != null ? configuration.getBootstrapServers() : "localhost:9092";
    }

    /**
     * 设置Bootstrap服务器地址
     */
    public void setBootstrapServers(String bootstrapServers) {
        ensureConfiguration();
        configuration.setBootstrapServers(bootstrapServers);
    }

    /**
     * 获取Topic名称
     */
    public String getTopic() {
        if (configuration == null) {
            ensureConfiguration();
        }
        return configuration != null ? configuration.getTopic() : "default-topic";
    }

    /**
     * 设置Topic名称
     */
    public void setTopic(String topic) {
        ensureConfiguration();
        configuration.setTopic(topic);
    }

    /**
     * 获取客户端ID
     */
    public String getClientId() {
        if (configuration == null) {
            ensureConfiguration();
        }
        return configuration != null ? configuration.getClientId() : "kafka-appender";
    }

    /**
     * 设置客户端ID
     */
    public void setClientId(String clientId) {
        ensureConfiguration();
        configuration.setClientId(clientId);
    }

    /**
     * 获取Key序列化器
     */
    public String getKeySerializer() {
        if (configuration == null) {
            ensureConfiguration();
        }
        return configuration != null ? configuration.getKeySerializer()
                : "org.apache.kafka.common.serialization.StringSerializer";
    }

    /**
     * 设置Key序列化器
     */
    public void setKeySerializer(String keySerializer) {
        ensureConfiguration();
        configuration.setKeySerializer(keySerializer);
    }

    /**
     * 获取Value序列化器
     */
    public String getValueSerializer() {
        if (configuration == null) {
            ensureConfiguration();
        }
        return configuration != null ? configuration.getValueSerializer()
                : "org.apache.kafka.common.serialization.StringSerializer";
    }

    /**
     * 设置Value序列化器
     */
    public void setValueSerializer(String valueSerializer) {
        ensureConfiguration();
        configuration.setValueSerializer(valueSerializer);
    }

    /**
     * 获取Acks配置
     */
    public String getAcks() {
        if (configuration == null) {
            ensureConfiguration();
        }
        return configuration != null ? configuration.getAcks() : "1";
    }

    /**
     * 设置Acks配置
     */
    public void setAcks(String acks) {
        ensureConfiguration();
        configuration.setAcks(acks);
    }

    /**
     * 获取重试次数
     */
    public int getRetries() {
        if (configuration == null) {
            ensureConfiguration();
        }
        return configuration != null ? configuration.getRetries() : 3;
    }

    /**
     * 设置重试次数
     */
    public void setRetries(int retries) {
        ensureConfiguration();
        configuration.setRetries(retries);
    }

    /**
     * 获取Kafka批处理大小
     */
    public int getKafkaBatchSize() {
        if (configuration == null) {
            ensureConfiguration();
        }
        return configuration != null ? configuration.getKafkaBatchSize() : 16384;
    }

    /**
     * 设置Kafka批处理大小
     */
    public void setKafkaBatchSize(int kafkaBatchSize) {
        ensureConfiguration();
        configuration.setKafkaBatchSize(kafkaBatchSize);
    }

    /**
     * 获取Linger时间（ms）
     */
    public int getLingerMs() {
        if (configuration == null) {
            ensureConfiguration();
        }
        return configuration != null ? configuration.getLingerMs() : 1;
    }

    /**
     * 设置Linger时间（ms）
     */
    public void setLingerMs(int lingerMs) {
        ensureConfiguration();
        configuration.setLingerMs(lingerMs);
    }

    /**
     * 获取缓冲区内存大小
     */
    public long getBufferMemory() {
        if (configuration == null) {
            ensureConfiguration();
        }
        return configuration != null ? configuration.getBufferMemory() : 33554432L;
    }

    /**
     * 设置缓冲区内存大小
     */
    public void setBufferMemory(long bufferMemory) {
        ensureConfiguration();
        configuration.setBufferMemory(bufferMemory);
    }

    /**
     * 获取压缩类型
     */
    public String getCompressionType() {
        if (configuration == null) {
            ensureConfiguration();
        }
        return configuration != null ? configuration.getCompressionType() : "none";
    }

    /**
     * 设置压缩类型
     */
    public void setCompressionType(String compressionType) {
        ensureConfiguration();
        configuration.setCompressionType(compressionType);
    }

    /**
     * 是否启用幂等性
     */
    public boolean isEnableIdempotence() {
        if (configuration == null) {
            ensureConfiguration();
        }
        return configuration != null ? configuration.isEnableIdempotence() : false;
    }

    /**
     * 设置是否启用幂等性
     */
    public void setEnableIdempotence(boolean enableIdempotence) {
        ensureConfiguration();
        configuration.setEnableIdempotence(enableIdempotence);
    }

    /**
     * 获取请求超时时间（ms）
     */
    public int getRequestTimeoutMs() {
        if (configuration == null) {
            ensureConfiguration();
        }
        return configuration != null ? configuration.getRequestTimeoutMs() : 30000;
    }

    /**
     * 设置请求超时时间（ms）
     */
    public void setRequestTimeoutMs(int requestTimeoutMs) {
        ensureConfiguration();
        configuration.setRequestTimeoutMs(requestTimeoutMs);
    }

    /**
     * 获取交付超时时间（ms）
     */
    public int getDeliveryTimeoutMs() {
        if (configuration == null) {
            ensureConfiguration();
        }
        return configuration != null ? configuration.getDeliveryTimeoutMs() : 120000;
    }

    /**
     * 设置交付超时时间（ms）
     */
    public void setDeliveryTimeoutMs(int deliveryTimeoutMs) {
        ensureConfiguration();
        configuration.setDeliveryTimeoutMs(deliveryTimeoutMs);
    }

    /**
     * 获取最大阻塞时间（ms）
     */
    public int getMaxBlockMs() {
        if (configuration == null) {
            ensureConfiguration();
        }
        return configuration != null ? configuration.getMaxBlockMs() : 60000;
    }

    /**
     * 设置最大阻塞时间（ms）
     */
    public void setMaxBlockMs(int maxBlockMs) {
        ensureConfiguration();
        configuration.setMaxBlockMs(maxBlockMs);
    }

    /**
     * 获取关闭超时时间（秒）
     */
    public int getShutdownTimeoutSeconds() {
        if (configuration == null) {
            ensureConfiguration();
        }
        return configuration != null ? configuration.getShutdownTimeoutSeconds() : 5;
    }

    /**
     * 设置关闭超时时间（秒）
     */
    public void setShutdownTimeoutSeconds(int shutdownTimeoutSeconds) {
        ensureConfiguration();
        configuration.setShutdownTimeoutSeconds(shutdownTimeoutSeconds);
    }

    /**
     * 获取批处理大小（兼容旧方法名）
     */
    public int getBatchSize() {
        return getKafkaBatchSize();
    }

    /**
     * 设置批处理大小（兼容旧方法名）
     */
    public void setBatchSize(int batchSize) {
        setKafkaBatchSize(batchSize);
    }

    /**
     * 确保配置对象已初始化
     */
    private void ensureConfiguration() {
        if (configuration == null) {
            configuration = new KafkaAppenderConfiguration();
        }
    }
}
