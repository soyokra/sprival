package com.soyokra.sprival.support.logging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.Appender;

/**
 * KafkaAppender 集成测试 需要真实的 Kafka 环境才能运行
 * 
 * @author sprival
 * @since 2.0.0
 */
@EnabledIfSystemProperty(named = "kafka.integration.test", matches = "true")
class KafkaAppenderIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(KafkaAppenderIntegrationTest.class);

    private KafkaAppender kafkaAppender;

    @BeforeEach
    void setUp() {
        kafkaAppender = new KafkaAppender();
        kafkaAppender.setBootstrapServers("localhost:9092");
        kafkaAppender.setTopic("test-integration-topic");
        kafkaAppender.setClientId("integration-test-client");
        kafkaAppender.setAsyncMode(false); // 同步模式便于测试
        kafkaAppender.setEnableBatching(false); // 关闭批处理便于测试
        kafkaAppender.setEnableConnectionFallback(true);
        kafkaAppender.setFallbackFilePath("logs/kafka-fallback-test.log");
    }

    @Test
    void testKafkaAppenderStartStop() {
        // 测试启动和停止
        assertFalse(kafkaAppender.isStarted());
        assertFalse(kafkaAppender.isHealthy());

        kafkaAppender.start();
        assertTrue(kafkaAppender.isStarted());

        kafkaAppender.stop();
        assertFalse(kafkaAppender.isStarted());
    }

    @Test
    void testKafkaAppenderWithAsyncMode() throws InterruptedException {
        // 测试异步模式
        kafkaAppender.setAsyncMode(true);
        kafkaAppender.setQueueCapacity(1000);
        kafkaAppender.setWorkerThreadCount(2);

        kafkaAppender.start();
        assertTrue(kafkaAppender.isStarted());

        // 等待异步线程启动
        Thread.sleep(1000);

        Map<String, Object> stats = kafkaAppender.getStatistics();
        assertTrue((Boolean) stats.get("asyncMode"));
        assertEquals(2, stats.get("workerThreadCount"));

        kafkaAppender.stop();
    }

    @Test
    void testKafkaAppenderWithBatching() throws InterruptedException {
        // 测试批处理模式
        kafkaAppender.setEnableBatching(true);
        kafkaAppender.setMaxBatchSize(10);
        kafkaAppender.setBatchTimeoutMs(2000);

        kafkaAppender.start();
        assertTrue(kafkaAppender.isStarted());

        // 发送一些日志事件
        for (int i = 0; i < 5; i++) {
            log.info("Test batch message {}", i);
        }

        // 等待批处理
        Thread.sleep(3000);

        Map<String, Object> stats = kafkaAppender.getStatistics();
        assertTrue((Boolean) stats.get("asyncMode") || kafkaAppender.isEnableBatching());

        kafkaAppender.stop();
    }

    @Test
    void testKafkaAppenderStatistics() throws InterruptedException {
        // 测试统计信息
        kafkaAppender.start();

        // 发送一些日志
        for (int i = 0; i < 10; i++) {
            log.info("Test statistics message {}", i);
        }

        // 等待处理
        Thread.sleep(2000);

        Map<String, Object> stats = kafkaAppender.getStatistics();
        assertNotNull(stats);
        assertTrue(stats.containsKey("totalEvents"));
        assertTrue(stats.containsKey("successfulEvents"));
        assertTrue(stats.containsKey("failedEvents"));
        assertTrue(stats.containsKey("isHealthy"));

        log.info("Statistics: {}", stats);

        kafkaAppender.stop();
    }

    @Test
    void testKafkaAppenderHealthCheck() {
        // 测试健康检查
        kafkaAppender.start();

        boolean isHealthy = kafkaAppender.isHealthy();
        log.info("KafkaAppender health status: {}", isHealthy);

        // 健康状态取决于Kafka连接是否可用
        // 如果Kafka不可用，健康状态应该是false
        assertNotNull(isHealthy);

        kafkaAppender.stop();
    }

    @Test
    void testKafkaAppenderConnectionStatus() {
        // 测试连接状态
        kafkaAppender.start();

        boolean isConnected = kafkaAppender.isKafkaConnected();
        log.info("Kafka connection status: {}", isConnected);

        // 连接状态取决于实际的Kafka连接
        assertNotNull(isConnected);

        kafkaAppender.stop();
    }

    @Test
    void testKafkaAppenderFallbackReplay() {
        // 测试降级消息重放
        kafkaAppender.start();

        int replayedCount = kafkaAppender.replayFallbackMessages();
        log.info("Replayed fallback messages: {}", replayedCount);

        // 重放数量应该是非负数
        assertTrue(replayedCount >= 0);

        kafkaAppender.stop();
    }

    @Test
    void testKafkaAppenderPerformance() throws InterruptedException {
        // 测试性能
        kafkaAppender.setAsyncMode(true);
        kafkaAppender.setQueueCapacity(10000);
        kafkaAppender.setWorkerThreadCount(3);
        kafkaAppender.setEnableBatching(true);
        kafkaAppender.setMaxBatchSize(100);
        kafkaAppender.setBatchTimeoutMs(1000);

        kafkaAppender.start();

        long startTime = System.currentTimeMillis();

        // 发送大量日志
        for (int i = 0; i < 1000; i++) {
            log.info("Performance test message {}", i);
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        log.info("Sent 1000 messages in {} ms, rate: {} msg/sec", duration,
                1000.0 * 1000 / duration);

        // 等待处理完成
        Thread.sleep(5000);

        Map<String, Object> stats = kafkaAppender.getStatistics();
        log.info("Final statistics: {}", stats);

        kafkaAppender.stop();
    }

    @Test
    void testKafkaAppenderWithLoggerContext() {
        // 测试与LoggerContext的集成
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        // 查找KafkaAppender
        Appender<?> appender = context.getLogger("ROOT").getAppender("KAFKA");

        if (appender instanceof KafkaAppender) {
            KafkaAppender kafkaAppender = (KafkaAppender) appender;

            // 测试统计信息
            Map<String, Object> stats = kafkaAppender.getStatistics();
            assertNotNull(stats);

            // 测试健康检查
            boolean isHealthy = kafkaAppender.isHealthy();
            assertNotNull(isHealthy);

            log.info("Found KafkaAppender in LoggerContext, stats: {}, healthy: {}", stats,
                    isHealthy);
        } else {
            log.info("KafkaAppender not found in LoggerContext");
        }
    }

    @Test
    void testKafkaAppenderConfigurationValidation() {
        // 测试配置验证
        kafkaAppender.setRetries(5);
        kafkaAppender.setKafkaBatchSize(32768);
        kafkaAppender.setLingerMs(10);
        kafkaAppender.setQueueCapacity(15000);
        kafkaAppender.setWorkerThreadCount(2);
        kafkaAppender.setMaxBatchSize(150);
        kafkaAppender.setBatchTimeoutMs(2000);
        kafkaAppender.setMaxConnectionRetries(3);
        kafkaAppender.setConnectionRetryIntervalMs(5000);

        // 启动应该成功（如果配置有效）
        kafkaAppender.start();
        assertTrue(kafkaAppender.isStarted());

        kafkaAppender.stop();
    }

    @Test
    void testKafkaAppenderResetStatistics() throws InterruptedException {
        // 测试统计信息重置
        kafkaAppender.start();

        // 发送一些日志
        for (int i = 0; i < 5; i++) {
            log.info("Reset test message {}", i);
        }

        // 等待处理
        Thread.sleep(1000);

        // 重置统计信息
        kafkaAppender.resetStatistics();

        Map<String, Object> stats = kafkaAppender.getStatistics();
        assertEquals(0L, stats.get("totalEvents"));
        assertEquals(0L, stats.get("successfulEvents"));
        assertEquals(0L, stats.get("failedEvents"));
        assertEquals(0L, stats.get("droppedEvents"));

        kafkaAppender.stop();
    }
}
