package com.soyokra.sprival.support.logging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;

/**
 * KafkaAppender简化测试 使用硬编码配置直接测试KafkaAppender
 * 
 * @author sprival
 * @since 1.0.0
 */
public class KafkaAppenderSimpleTest {

    private Logger testLogger;
    private LoggerContext loggerContext;

    @BeforeEach
    void setUp() {
        loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        testLogger = loggerContext.getLogger("com.soyokra.sprival.test");
        testLogger.setLevel(Level.INFO);
    }

    @Test
    void testKafkaAppenderDirectCreation() {
        System.out.println("=== 直接创建KafkaAppender测试 ===");

        // 创建KafkaAppender实例
        KafkaAppender kafkaAppender = new KafkaAppender();
        kafkaAppender.setBootstrapServers("localhost:9092");
        kafkaAppender.setTopic("test-application-logs");
        kafkaAppender.setClientId("test-kafka-appender");
        kafkaAppender.setAcks("1");
        kafkaAppender.setRetries(3);
        kafkaAppender.setBatchSize(16384);
        kafkaAppender.setLingerMs(1);
        kafkaAppender.setBufferMemory(33554432);
        kafkaAppender.setCompressionType("none");
        kafkaAppender.setEnableIdempotence(false);
        kafkaAppender.setRequestTimeoutMs(30000);
        kafkaAppender.setDeliveryTimeoutMs(120000);
        kafkaAppender.setMaxBlockMs(60000);
        kafkaAppender.setContext(loggerContext);
        kafkaAppender.setName("TEST_KAFKA");

        // 启动appender
        kafkaAppender.start();
        System.out.println("KafkaAppender启动状态: " + kafkaAppender.isStarted());

        // 创建测试日志事件
        LoggingEvent event = new LoggingEvent();
        event.setLoggerName("test.logger");
        event.setLevel(Level.INFO);
        event.setMessage("直接创建测试消息 - " + System.currentTimeMillis());
        event.setTimeStamp(System.currentTimeMillis());

        // 测试append方法
        System.out.println("调用append方法...");
        kafkaAppender.append(event);
        System.out.println("append方法调用完成");

        // 验证配置
        assertEquals("localhost:9092", kafkaAppender.getBootstrapServers());
        assertEquals("test-application-logs", kafkaAppender.getTopic());
        assertEquals("test-kafka-appender", kafkaAppender.getClientId());

        // 停止appender
        kafkaAppender.stop();

        System.out.println("✅ KafkaAppender直接创建测试完成");
    }

    @Test
    void testKafkaAppenderWithRootLogger() {
        System.out.println("=== 将KafkaAppender添加到Root Logger测试 ===");

        // 创建KafkaAppender实例
        KafkaAppender kafkaAppender = new KafkaAppender();
        kafkaAppender.setBootstrapServers("localhost:9092");
        kafkaAppender.setTopic("test-application-logs");
        kafkaAppender.setClientId("test-kafka-appender");
        kafkaAppender.setAcks("1");
        kafkaAppender.setRetries(3);
        kafkaAppender.setBatchSize(16384);
        kafkaAppender.setLingerMs(1);
        kafkaAppender.setBufferMemory(33554432);
        kafkaAppender.setCompressionType("none");
        kafkaAppender.setEnableIdempotence(false);
        kafkaAppender.setRequestTimeoutMs(30000);
        kafkaAppender.setDeliveryTimeoutMs(120000);
        kafkaAppender.setMaxBlockMs(60000);
        kafkaAppender.setContext(loggerContext);
        kafkaAppender.setName("TEST_KAFKA");

        // 启动appender
        kafkaAppender.start();

        // 将KafkaAppender添加到root logger
        Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.addAppender(kafkaAppender);

        System.out.println("KafkaAppender已添加到root logger");

        // 记录测试日志
        String testMessage = "Root Logger测试消息 - " + System.currentTimeMillis();
        testLogger.info(testMessage);

        System.out.println("✅ 日志消息已发送: " + testMessage);

        // 验证KafkaAppender是否在root logger中
        boolean foundKafkaAppender = false;
        java.util.Iterator<ch.qos.logback.core.Appender<ILoggingEvent>> iterator =
                rootLogger.iteratorForAppenders();
        while (iterator.hasNext()) {
            ch.qos.logback.core.Appender<ILoggingEvent> appender = iterator.next();
            if (appender instanceof KafkaAppender) {
                foundKafkaAppender = true;
                System.out.println("✅ 在root logger中找到KafkaAppender: " + appender.getName());
                break;
            }
        }

        assertTrue(foundKafkaAppender, "应该在root logger中找到KafkaAppender");

        // 清理
        rootLogger.detachAppender(kafkaAppender);
        kafkaAppender.stop();

        System.out.println("✅ KafkaAppender Root Logger测试完成");
    }

    @Test
    void testKafkaAppenderLifecycle() {
        System.out.println("=== KafkaAppender生命周期测试 ===");

        // 创建KafkaAppender实例
        KafkaAppender kafkaAppender = new KafkaAppender();
        kafkaAppender.setBootstrapServers("localhost:9092");
        kafkaAppender.setTopic("test-application-logs");
        kafkaAppender.setClientId("test-kafka-appender");
        kafkaAppender.setContext(loggerContext);
        kafkaAppender.setName("LIFECYCLE_TEST");

        // 测试初始状态
        assertFalse(kafkaAppender.isStarted(), "初始状态应该是未启动");

        // 启动appender
        kafkaAppender.start();
        assertTrue(kafkaAppender.isStarted(), "启动后状态应该是已启动");

        // 测试append方法
        LoggingEvent event = new LoggingEvent();
        event.setLoggerName("lifecycle.test");
        event.setLevel(Level.INFO);
        event.setMessage("生命周期测试消息");
        event.setTimeStamp(System.currentTimeMillis());

        kafkaAppender.append(event);
        System.out.println("✅ append方法调用成功");

        // 停止appender
        kafkaAppender.stop();
        assertFalse(kafkaAppender.isStarted(), "停止后状态应该是未启动");

        System.out.println("✅ KafkaAppender生命周期测试完成");
    }

    @Test
    void testKafkaAppenderConfiguration() {
        System.out.println("=== KafkaAppender配置测试 ===");

        // 创建KafkaAppender实例
        KafkaAppender kafkaAppender = new KafkaAppender();
        kafkaAppender.setBootstrapServers("localhost:9092");
        kafkaAppender.setTopic("test-application-logs");
        kafkaAppender.setClientId("test-kafka-appender");
        kafkaAppender.setAcks("1");
        kafkaAppender.setRetries(3);
        kafkaAppender.setBatchSize(16384);
        kafkaAppender.setLingerMs(1);
        kafkaAppender.setBufferMemory(33554432);
        kafkaAppender.setCompressionType("none");
        kafkaAppender.setEnableIdempotence(false);
        kafkaAppender.setRequestTimeoutMs(30000);
        kafkaAppender.setDeliveryTimeoutMs(120000);
        kafkaAppender.setMaxBlockMs(60000);
        kafkaAppender.setContext(loggerContext);
        kafkaAppender.setName("CONFIG_TEST");

        // 验证配置
        assertNotNull(kafkaAppender.getBootstrapServers(), "Bootstrap服务器不应为空");
        assertNotNull(kafkaAppender.getTopic(), "主题不应为空");
        assertNotNull(kafkaAppender.getClientId(), "客户端ID不应为空");

        assertEquals("localhost:9092", kafkaAppender.getBootstrapServers());
        assertEquals("test-application-logs", kafkaAppender.getTopic());
        assertEquals("test-kafka-appender", kafkaAppender.getClientId());
        assertEquals("1", kafkaAppender.getAcks());
        assertEquals(3, kafkaAppender.getRetries());
        assertEquals(16384, kafkaAppender.getBatchSize());
        assertEquals(1, kafkaAppender.getLingerMs());
        assertEquals(33554432, kafkaAppender.getBufferMemory());
        assertEquals("none", kafkaAppender.getCompressionType());
        assertEquals(false, kafkaAppender.isEnableIdempotence());
        assertEquals(30000, kafkaAppender.getRequestTimeoutMs());
        assertEquals(120000, kafkaAppender.getDeliveryTimeoutMs());
        assertEquals(60000, kafkaAppender.getMaxBlockMs());

        System.out.println("✅ KafkaAppender配置验证通过");
        System.out.println("  - Bootstrap服务器: " + kafkaAppender.getBootstrapServers());
        System.out.println("  - 主题: " + kafkaAppender.getTopic());
        System.out.println("  - 客户端ID: " + kafkaAppender.getClientId());
        System.out.println("  - ACKS: " + kafkaAppender.getAcks());
        System.out.println("  - 重试次数: " + kafkaAppender.getRetries());
        System.out.println("  - 批处理大小: " + kafkaAppender.getBatchSize());
    }
}
