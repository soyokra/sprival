package com.soyokra.sprival.support.logging;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
            Files.createDirectories(fallbackFile.getParent());
            if (!Files.exists(fallbackFile)) {
                Files.createFile(fallbackFile);
            }
        } catch (IOException e) {
            System.err.println("Failed to initialize fallback file: " + e.getMessage());
        }
    }

    /**
     * 建立 Kafka 连接
     */
    public boolean connect() {
        if (isConnected.get()) {
            return true;
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastConnectionAttempt.get() < retryIntervalMs) {
            return false; // 还在重试间隔内
        }

        lastConnectionAttempt.set(currentTime);

        try {
            if (producer != null) {
                producer.close(Duration.ofSeconds(5));
            }

            producer = new KafkaProducer<>(producerConfig);

            // 测试连接
            if (testConnection()) {
                isConnected.set(true);
                connectionRetryCount.set(0);
                return true;
            } else {
                isConnected.set(false);
                return false;
            }

        } catch (Exception e) {
            isConnected.set(false);
            connectionRetryCount.incrementAndGet();
            return false;
        }
    }

    /**
     * 测试连接是否可用
     */
    private boolean testConnection() {
        try {
            // 发送一个测试消息来验证连接
            ProducerRecord<String, String> testRecord =
                    new ProducerRecord<>("__test_topic__", "test", "test");
            CompletableFuture<RecordMetadata> future = new CompletableFuture<>();

            producer.send(testRecord, (metadata, exception) -> {
                if (exception != null) {
                    future.completeExceptionally(exception);
                } else {
                    future.complete(metadata);
                }
            });

            // 等待连接测试结果，设置超时
            future.get(connectionTimeoutMs, java.util.concurrent.TimeUnit.MILLISECONDS);
            return true;

        } catch (Exception e) {
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
            try (FileWriter writer = new FileWriter(fallbackFile.toFile(), true)) {
                String logEntry = String.format("[%s] Topic: %s, Key: %s, Value: %s%n",
                        java.time.Instant.now().toString(), record.topic(), record.key(),
                        record.value());
                writer.write(logEntry);
                writer.flush();
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

        int replayedCount = 0;
        synchronized (fallbackLock) {
            try {
                if (!isConnected.get() && !connect()) {
                    return 0; // 连接不可用，无法重放
                }

                java.util.List<String> lines = Files.readAllLines(fallbackFile);
                java.util.List<String> remainingLines = new java.util.ArrayList<>();

                for (String line : lines) {
                    if (line.trim().isEmpty()) {
                        continue;
                    }

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
                Files.write(fallbackFile, remainingLines);

            } catch (IOException e) {
                System.err.println("Failed to replay fallback messages: " + e.getMessage());
            }
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
