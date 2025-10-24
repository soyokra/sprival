package com.soyokra.sprival.support.logging;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.read.ListAppender;

/**
 * KafkaAppender调试测试 用于调试KafkaAppender的配置和日志路由问题
 * 
 * @author sprival
 * @since 1.0.0
 */
public class KafkaAppenderDebugTest {

    private LoggerContext loggerContext;
    private Logger testLogger;

    @BeforeEach
    void setUp() {
        // 获取LoggerContext
        loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        // 创建测试logger
        testLogger = loggerContext.getLogger("com.soyokra.sprival.test");
        testLogger.setLevel(Level.INFO);
    }

    @Test
    void testKafkaAppenderConfiguration() {
        System.out.println("=== 测试KafkaAppender配置 ===");

        // 检查KafkaAppender是否被正确配置
        Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
        System.out.println("Root logger appenders: " + rootLogger.iteratorForAppenders().hasNext());

        // 遍历所有appender
        java.util.Iterator<Appender<ILoggingEvent>> iterator = rootLogger.iteratorForAppenders();
        while (iterator.hasNext()) {
            Appender<ILoggingEvent> appender = iterator.next();
            System.out.println("Appender: " + appender.getName() + " - "
                    + appender.getClass().getSimpleName());
            if (appender instanceof KafkaAppender) {
                KafkaAppender kafkaAppender = (KafkaAppender) appender;
                System.out.println("  - KafkaAppender found: " + kafkaAppender.getName());
                System.out.println("  - Started: " + kafkaAppender.isStarted());
            }
        }
    }

    @Test
    void testKafkaAppenderDirectly() {
        System.out.println("=== 直接测试KafkaAppender ===");

        // 创建KafkaAppender实例
        KafkaAppender kafkaAppender = new KafkaAppender();
        kafkaAppender.setBootstrapServers("localhost:9092");
        kafkaAppender.setTopic("test-topic");
        kafkaAppender.setContext(loggerContext);
        kafkaAppender.setName("TEST_KAFKA");

        // 启动appender
        kafkaAppender.start();
        System.out.println("KafkaAppender started: " + kafkaAppender.isStarted());

        // 创建测试日志事件
        LoggingEvent event = new LoggingEvent();
        event.setLoggerName("test.logger");
        event.setLevel(Level.INFO);
        event.setMessage("测试消息");
        event.setTimeStamp(System.currentTimeMillis());

        // 测试append方法
        System.out.println("调用append方法...");
        kafkaAppender.append(event);
        System.out.println("append方法调用完成");

        // 停止appender
        kafkaAppender.stop();
    }

    @Test
    void testLogbackConfiguration() {
        System.out.println("=== 测试Logback配置 ===");

        // 检查logback配置
        System.out.println("LoggerContext status: "
                + loggerContext.getStatusManager().getCopyOfStatusList().size());

        // 检查root logger的appender
        Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
        System.out.println("Root logger level: " + rootLogger.getLevel());
        System.out.println("Root logger effective level: " + rootLogger.getEffectiveLevel());

        // 检查是否有KafkaAppender
        boolean hasKafkaAppender = false;
        java.util.Iterator<Appender<ILoggingEvent>> iterator = rootLogger.iteratorForAppenders();
        while (iterator.hasNext()) {
            Appender<ILoggingEvent> appender = iterator.next();
            if (appender instanceof KafkaAppender) {
                hasKafkaAppender = true;
                KafkaAppender kafkaAppender = (KafkaAppender) appender;
                System.out.println("Found KafkaAppender: " + kafkaAppender.getName());
                System.out.println("  - Started: " + kafkaAppender.isStarted());
                System.out.println("  - Bootstrap servers: " + kafkaAppender.getBootstrapServers());
                System.out.println("  - Topic: " + kafkaAppender.getTopic());
            }
        }

        if (!hasKafkaAppender) {
            System.out.println("❌ 没有找到KafkaAppender！");
        } else {
            System.out.println("✅ 找到KafkaAppender");
        }
    }

    @Test
    void testLogRouting() {
        System.out.println("=== 测试日志路由 ===");

        // 创建一个ListAppender来捕获日志事件
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.setContext(loggerContext);
        listAppender.setName("TEST_LIST");
        listAppender.start();

        // 将ListAppender添加到root logger
        Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.addAppender(listAppender);

        // 记录一条日志
        testLogger.info("测试日志路由消息");

        // 检查是否捕获到日志事件
        List<ILoggingEvent> events = listAppender.list;
        System.out.println("捕获到的日志事件数量: " + events.size());

        for (ILoggingEvent event : events) {
            System.out.println("日志事件: " + event.getLevel() + " - " + event.getMessage());
        }

        // 清理
        rootLogger.detachAppender(listAppender);
    }

    @Test
    void testKafkaAppenderWithMockKafka() {
        System.out.println("=== 测试KafkaAppender（模拟Kafka） ===");

        // 创建一个KafkaAppender，但使用无效的Kafka地址来避免实际连接
        KafkaAppender kafkaAppender = new KafkaAppender();
        kafkaAppender.setBootstrapServers("invalid:9092"); // 使用无效地址
        kafkaAppender.setTopic("test-topic");
        kafkaAppender.setContext(loggerContext);
        kafkaAppender.setName("MOCK_KAFKA");

        try {
            // 启动appender（应该会失败，但我们可以测试append方法）
            kafkaAppender.start();
            System.out.println("KafkaAppender启动状态: " + kafkaAppender.isStarted());

            // 创建测试日志事件
            LoggingEvent event = new LoggingEvent();
            event.setLoggerName("test.logger");
            event.setLevel(Level.INFO);
            event.setMessage("模拟Kafka测试消息");
            event.setTimeStamp(System.currentTimeMillis());

            // 测试append方法
            System.out.println("调用append方法（应该不会发送到Kafka）...");
            kafkaAppender.append(event);
            System.out.println("append方法调用完成");

        } catch (Exception e) {
            System.out.println("KafkaAppender启动失败（预期）: " + e.getMessage());
        } finally {
            kafkaAppender.stop();
        }
    }

    @Test
    void testLogbackDebugMode() {
        System.out.println("=== 测试Logback调试模式 ===");

        // 检查logback配置是否启用了debug模式
        System.out.println("LoggerContext debug enabled: " + loggerContext.isStarted());

        // 检查所有logger的状态
        for (Logger logger : loggerContext.getLoggerList()) {
            if (logger.getName().contains("KafkaAppender")
                    || logger.getName().contains("com.soyokra.sprival.support.logging")) {
                System.out.println("Logger: " + logger.getName() + " - Level: " + logger.getLevel()
                        + " - Effective Level: " + logger.getEffectiveLevel());
            }
        }
    }
}
