package com.soyokra.sprival.support.logging;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.status.StatusManager;

/**
 * KafkaAppender 单元测试
 * 
 * @author sprival
 * @since 2.0.0
 */
@ExtendWith(MockitoExtension.class)
class KafkaAppenderTest {

    private KafkaAppender kafkaAppender;

    @Mock
    private ILoggingEvent loggingEvent;

    @Mock
    private StatusManager statusManager;

    @BeforeEach
    void setUp() {
        kafkaAppender = new KafkaAppender();
        kafkaAppender.setBootstrapServers("localhost:9092");
        kafkaAppender.setTopic("test-topic");
        kafkaAppender.setAsyncMode(false); // 使用同步模式进行测试
    }

    @Test
    void testConfigurationValidation() {
        // 测试配置验证 - 现在会自动创建默认配置，所以能正常启动
        KafkaAppender appender = new KafkaAppender();
        appender.start(); // 会自动使用默认配置
        assertTrue(appender.isStarted());
        appender.stop();
    }

    @Test
    void testStartWithValidConfiguration() {
        // 测试有效配置启动
        assertDoesNotThrow(() -> {
            kafkaAppender.start();
            assertTrue(kafkaAppender.isStarted());
        });
    }

    @Test
    void testStop() {
        // 测试停止功能
        kafkaAppender.start();
        assertTrue(kafkaAppender.isStarted());

        kafkaAppender.stop();
        assertFalse(kafkaAppender.isStarted());
    }

    @Test
    void testAsyncModeConfiguration() {
        // 测试异步模式配置
        kafkaAppender.setAsyncMode(true);
        kafkaAppender.setQueueCapacity(5000);

        assertEquals(true, kafkaAppender.isAsyncMode());
        assertEquals(5000, kafkaAppender.getQueueCapacity());
    }

    @Test
    void testBatchConfiguration() {
        // 测试批处理配置
        kafkaAppender.setMaxBatchSize(200);
        kafkaAppender.setBatchTimeoutMs(2000);

        assertEquals(200, kafkaAppender.getMaxBatchSize());
        assertEquals(2000, kafkaAppender.getBatchTimeoutMs());
    }

    @Test
    void testKafkaConfiguration() {
        // 测试Kafka配置
        kafkaAppender.setKafkaBatchSize(32768);
        kafkaAppender.setLingerMs(5);
        kafkaAppender.setRetries(5);

        assertEquals(32768, kafkaAppender.getKafkaBatchSize());
        assertEquals(5, kafkaAppender.getLingerMs());
        assertEquals(5, kafkaAppender.getRetries());
    }

    @Test
    void testStatistics() {
        // 测试统计功能
        kafkaAppender.start();

        Map<String, Object> stats = kafkaAppender.getStatistics();
        assertNotNull(stats);
        assertTrue(stats.containsKey("totalEvents"));
        assertTrue(stats.containsKey("successfulEvents"));
        assertTrue(stats.containsKey("failedEvents"));
        assertTrue(stats.containsKey("droppedEvents"));
        assertTrue(stats.containsKey("isHealthy"));

        // 测试重置统计
        kafkaAppender.resetStatistics();
        Map<String, Object> resetStats = kafkaAppender.getStatistics();
        assertEquals(0L, resetStats.get("totalEvents"));
        assertEquals(0L, resetStats.get("successfulEvents"));
        assertEquals(0L, resetStats.get("failedEvents"));
        assertEquals(0L, resetStats.get("droppedEvents"));
    }

    @Test
    void testHealthCheck() {
        // 测试健康检查
        assertFalse(kafkaAppender.isHealthy()); // 未启动时应该不健康

        kafkaAppender.start();
        assertTrue(kafkaAppender.isHealthy()); // 启动后应该健康

        kafkaAppender.stop();
        assertFalse(kafkaAppender.isHealthy()); // 停止后应该不健康
    }

    @Test
    void testAppendWithoutStart() {
        // 测试未启动时append的行为
        kafkaAppender.append(loggingEvent);
        // 应该不会抛出异常，但也不会处理事件
        assertFalse(kafkaAppender.isStarted());
    }

    @Test
    void testMultipleStartStop() {
        // 测试多次启动和停止
        kafkaAppender.start();
        assertTrue(kafkaAppender.isStarted());

        kafkaAppender.start(); // 再次启动应该被忽略
        assertTrue(kafkaAppender.isStarted());

        kafkaAppender.stop();
        assertFalse(kafkaAppender.isStarted());

        kafkaAppender.stop(); // 再次停止应该被忽略
        assertFalse(kafkaAppender.isStarted());
    }

    @Test
    void testConfigurationGettersAndSetters() {
        // 测试所有配置的getter和setter
        kafkaAppender.setBootstrapServers("test:9092");
        kafkaAppender.setTopic("test-topic");
        kafkaAppender.setClientId("test-client");
        kafkaAppender.setAcks("all");
        kafkaAppender.setCompressionType("gzip");
        kafkaAppender.setEnableIdempotence(true);
        kafkaAppender.setRequestTimeoutMs(60000);
        kafkaAppender.setDeliveryTimeoutMs(180000);
        kafkaAppender.setMaxBlockMs(120000);
        kafkaAppender.setShutdownTimeoutSeconds(10);

        assertEquals("test:9092", kafkaAppender.getBootstrapServers());
        assertEquals("test-topic", kafkaAppender.getTopic());
        assertEquals("test-client", kafkaAppender.getClientId());
        assertEquals("all", kafkaAppender.getAcks());
        assertEquals("gzip", kafkaAppender.getCompressionType());
        assertEquals(true, kafkaAppender.isEnableIdempotence());
        assertEquals(60000, kafkaAppender.getRequestTimeoutMs());
        assertEquals(180000, kafkaAppender.getDeliveryTimeoutMs());
        assertEquals(120000, kafkaAppender.getMaxBlockMs());
        assertEquals(10, kafkaAppender.getShutdownTimeoutSeconds());
    }
}
