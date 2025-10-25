package com.soyokra.sprival.support.logging;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

/**
 * Kafka 连接管理器 - 提供连接容错和降级策略
 * 
 * @author sprival
 * @since 2.0.0
 */
public class KafkaConnectionManager {

    private Producer<String, String> producer;
    private final Properties producerConfig;
    private final AtomicBoolean isConnected = new AtomicBoolean(false);
    private final AtomicInteger connectionRetryCount = new AtomicInteger(0);
    private final AtomicLong lastConnectionAttempt = new AtomicLong(0);

    // 降级策略配置
    private final boolean enableFallback;
    private final String fallbackFilePath;
    private final int maxRetryAttempts;
    private final long retryIntervalMs;
    private final long connectionTimeoutMs;
    private final String topic;

    // 降级文件管理
    private Path fallbackFile;
    private final Object fallbackLock = new Object();

    public KafkaConnectionManager(Properties config, boolean enableFallback,
            String fallbackFilePath) {
        this.producerConfig = new Properties();
        this.producerConfig.putAll(config);

        this.enableFallback = enableFallback;
        this.fallbackFilePath = fallbackFilePath;
        this.maxRetryAttempts =
                Integer.parseInt(config.getProperty("connection.maxRetryAttempts", "5"));
        this.retryIntervalMs =
                Long.parseLong(config.getProperty("connection.retryIntervalMs", "5000"));
        this.connectionTimeoutMs =
                Long.parseLong(config.getProperty("connection.timeoutMs", "30000"));
        this.topic = config.getProperty("connection.topic", "__test_topic__");

        if (enableFallback && fallbackFilePath != null) {
            initializeFallbackFile();
        }
    }

    /**
     * 初始化降级文件
     */
    private void initializeFallbackFile() {
        try {
            fallbackFile = Paths.get(fallbackFilePath);

            // 确保父目录存在
            if (fallbackFile.getParent() != null) {
                Files.createDirectories(fallbackFile.getParent());
            }

            // 创建文件（如果不存在）
            if (!Files.exists(fallbackFile)) {
                Files.createFile(fallbackFile);
            }
        } catch (IOException e) {
            System.err.println("Failed to initialize fallback file: " + e.getMessage());
            // 不抛出异常，允许降级功能不可用，但不影响主要功能
        }
    }

    /**
     * 建立 Kafka 连接
     */
    public boolean connect() {
        if (isConnected.get()) {
            return true;
        }

        // 检查是否超过最大重试次数
        if (connectionRetryCount.get() >= maxRetryAttempts) {
            System.err.println("Kafka connection retry limit (" + maxRetryAttempts + ") exceeded");
            return false;
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastConnectionAttempt.get() < retryIntervalMs) {
            return false; // 还在重试间隔内
        }

        lastConnectionAttempt.set(currentTime);

        Producer<String, String> newProducer = null;
        try {
            if (producer != null) {
                producer.close(Duration.ofSeconds(5));
            }

            newProducer = new KafkaProducer<>(producerConfig);

            // 测试连接
            if (testConnection(newProducer)) {
                producer = newProducer;
                isConnected.set(true);
                connectionRetryCount.set(0);
                return true;
            } else {
                isConnected.set(false);
                connectionRetryCount.incrementAndGet();
                // 清理失败的producer
                if (newProducer != null) {
                    try {
                        newProducer.close(Duration.ofSeconds(2));
                    } catch (Exception ignored) {
                    }
                }
                return false;
            }

        } catch (Exception e) {
            isConnected.set(false);
            connectionRetryCount.incrementAndGet();
            // 清理可能创建的producer
            if (newProducer != null) {
                try {
                    newProducer.close(Duration.ofSeconds(2));
                } catch (Exception ignored) {
                }
            }
            return false;
        }
    }

    /**
     * 测试连接是否可用
     */
    private boolean testConnection(Producer<String, String> producerToTest) {
        try {
            // 使用配置的 topic 发送测试消息来验证连接
            ProducerRecord<String, String> testRecord = new ProducerRecord<>(topic, "test", "test");
            CompletableFuture<RecordMetadata> future = new CompletableFuture<>();

            producerToTest.send(testRecord, (metadata, exception) -> {
                if (exception != null) {
                    future.completeExceptionally(exception);
                } else {
                    future.complete(metadata);
                }
            });

            // 等待连接测试结果，设置超时
            future.get(connectionTimeoutMs, java.util.concurrent.TimeUnit.MILLISECONDS);
            return true;

        } catch (java.util.concurrent.TimeoutException e) {
            System.err.println("Kafka connection test timeout after " + connectionTimeoutMs + "ms");
            return false;
        } catch (Exception e) {
            System.err.println("Kafka connection test failed: " + e.getClass().getSimpleName()
                    + " - " + e.getMessage());
            return false;
        }
    }

    /**
     * 发送消息到 Kafka
     */
    public boolean sendMessage(ProducerRecord<String, String> record) {
        if (!isConnected.get() && !connect()) {
            // 连接失败，尝试降级策略
            if (enableFallback) {
                return writeToFallbackFile(record);
            }
            return false;
        }

        try {
            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    isConnected.set(false);
                    connectionRetryCount.incrementAndGet();

                    // 如果启用降级，写入降级文件
                    if (enableFallback) {
                        writeToFallbackFile(record);
                    }
                }
            });
            return true;

        } catch (Exception e) {
            isConnected.set(false);
            connectionRetryCount.incrementAndGet();

            if (enableFallback) {
                return writeToFallbackFile(record);
            }
            return false;
        }
    }

    /**
     * 写入降级文件
     */
    private boolean writeToFallbackFile(ProducerRecord<String, String> record) {
        if (fallbackFile == null) {
            return false;
        }

        synchronized (fallbackLock) {
            try (BufferedWriter writer = Files.newBufferedWriter(fallbackFile,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                String logEntry = String.format("[%s] Topic: %s, Key: %s, Value: %s%n",
                        java.time.Instant.now().toString(), record.topic(), record.key(),
                        record.value());
                writer.write(logEntry);
                // 使用带缓冲的写入，减少 flush 频率以提升性能
                // BufferedWriter 会自动处理缓冲，try-with-resources 会自动 flush
                return true;

            } catch (IOException e) {
                System.err.println("Failed to write to fallback file: " + e.getMessage());
                return false;
            }
        }
    }

    /**
     * 重放降级文件中的消息
     */
    public int replayFallbackMessages() {
        if (fallbackFile == null || !Files.exists(fallbackFile)) {
            return 0;
        }

        // 避免死锁：使用独立的锁读取文件，避免与sendMessage()的回调中的fallbackLock冲突
        java.util.List<String> lines = new java.util.ArrayList<>();
        synchronized (fallbackLock) {
            try {
                try (java.io.BufferedReader reader = Files.newBufferedReader(fallbackFile)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (!line.trim().isEmpty()) {
                            lines.add(line);
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Failed to read fallback file: " + e.getMessage());
                return 0;
            }
        }

        if (lines.isEmpty()) {
            return 0;
        }

        // 尝试连接
        if (!isConnected.get() && !connect()) {
            return 0; // 连接不可用，无法重放
        }

        // 处理读取到的行
        int replayedCount = 0;
        java.util.List<String> remainingLines = new java.util.ArrayList<>();

        for (String line : lines) {
            try {
                // 解析日志条目
                ProducerRecord<String, String> record = parseLogEntry(line);
                if (record != null && sendMessage(record)) {
                    replayedCount++;
                } else {
                    remainingLines.add(line);
                }
            } catch (Exception e) {
                remainingLines.add(line);
            }
        }

        // 重写文件，只保留未成功发送的消息
        if (!remainingLines.isEmpty()) {
            synchronized (fallbackLock) {
                try {
                    Files.write(fallbackFile, remainingLines);
                } catch (IOException e) {
                    System.err.println("Failed to write remaining lines: " + e.getMessage());
                }
            }
        } else {
            // 如果所有消息都成功发送，删除文件
            cleanupFallbackFile();
        }

        return replayedCount;
    }

    /**
     * 解析日志条目
     */
    private ProducerRecord<String, String> parseLogEntry(String line) {
        try {
            // 简单的解析逻辑，实际应用中可能需要更复杂的解析
            if (line.contains("Topic:") && line.contains("Key:") && line.contains("Value:")) {
                String[] parts = line.split("Topic: |, Key: |, Value: ");
                if (parts.length >= 4) {
                    String topic = parts[1].trim();
                    String key = parts[2].trim();
                    String value = parts[3].trim();
                    return new ProducerRecord<>(topic, key, value);
                }
            }
        } catch (Exception e) {
            // 解析失败，忽略该行
        }
        return null;
    }

    /**
     * 检查连接状态
     */
    public boolean isConnected() {
        return isConnected.get();
    }

    /**
     * 获取重试次数
     */
    public int getRetryCount() {
        return connectionRetryCount.get();
    }

    /**
     * 关闭连接
     */
    public void close() {
        if (producer != null) {
            try {
                producer.close(Duration.ofSeconds(5));
            } catch (Exception e) {
                System.err.println("Error closing Kafka producer: " + e.getMessage());
            }
        }
        isConnected.set(false);
    }

    /**
     * 清理降级文件
     */
    public void cleanupFallbackFile() {
        if (fallbackFile != null && Files.exists(fallbackFile)) {
            try {
                Files.delete(fallbackFile);
            } catch (IOException e) {
                System.err.println("Failed to cleanup fallback file: " + e.getMessage());
            }
        }
    }
}
