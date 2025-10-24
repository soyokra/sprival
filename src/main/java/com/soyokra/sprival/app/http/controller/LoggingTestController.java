package com.soyokra.sprival.app.http.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.soyokra.sprival.support.logging.KafkaAppender;
import com.soyokra.sprival.support.logging.LoggingUtils;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.Appender;
import lombok.extern.slf4j.Slf4j;

/**
 * 日志测试控制器 用于测试 Kafka + ELK 日志集成
 * 
 * @author sprival
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/test/logging")
public class LoggingTestController {

    private final Environment environment;

    public LoggingTestController(Environment environment) {
        this.environment = environment;
    }

    /**
     * 测试基本日志输出
     */
    @GetMapping("/basic")
    public Map<String, Object> testBasicLogging() {
        log.error("测试基本日志输出 - 时间戳: {}", System.currentTimeMillis());
        log.error("这是一条DEBUG级别的日志");
        log.error("这是一条WARN级别的日志");

        Map<String, Object> result = new HashMap<>();
        result.put("message", "基本日志测试完成");
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }

    /**
     * 测试异常日志输出
     */
    @GetMapping("/exception")
    public Map<String, Object> testExceptionLogging() {
        try {
            // 故意抛出异常来测试异常日志
            throw new RuntimeException("这是一个测试异常");
        } catch (Exception e) {
            log.error("捕获到异常", e);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("message", "异常日志测试完成");
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }

    /**
     * 测试MDC日志输出
     */
    @GetMapping("/mdc")
    public Map<String, Object> testMdcLogging(
            @RequestParam(defaultValue = "test-user") String userId) {
        // 设置MDC信息
        org.slf4j.MDC.put("userId", userId);
        org.slf4j.MDC.put("requestId", "req-" + System.currentTimeMillis());
        org.slf4j.MDC.put("sessionId", "session-" + userId);

        log.info("用户操作日志 - 用户ID: {}", userId);
        log.debug("MDC测试日志 - 包含用户上下文信息");

        // 清理MDC
        org.slf4j.MDC.clear();

        Map<String, Object> result = new HashMap<>();
        result.put("message", "MDC日志测试完成");
        result.put("userId", userId);
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }

    /**
     * 测试结构化日志输出
     */
    @GetMapping("/structured")
    public Map<String, Object> testStructuredLogging(
            @RequestParam(defaultValue = "test") String action) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("action", action);
        logData.put("userId", "user123");
        logData.put("timestamp", System.currentTimeMillis());
        logData.put("ip", "192.168.1.100");

        log.info("结构化日志测试 - 操作: {}, 数据: {}", action, logData);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "结构化日志测试完成");
        result.put("logData", logData);
        return result;
    }

    /**
     * 测试批量日志输出
     */
    @GetMapping("/batch")
    public Map<String, Object> testBatchLogging(@RequestParam(defaultValue = "10") int count) {
        log.info("开始批量日志测试，数量: {}", count);

        for (int i = 1; i <= count; i++) {
            log.info("批量日志消息 #{} - 时间戳: {}", i, System.currentTimeMillis());

            if (i % 3 == 0) {
                log.warn("批量日志警告消息 #{}", i);
            }

            if (i % 5 == 0) {
                log.error("批量日志错误消息 #{}", i);
            }
        }

        log.info("批量日志测试完成，共输出 {} 条日志", count);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "批量日志测试完成");
        result.put("count", count);
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }

    /**
     * 获取日志配置信息
     */
    @GetMapping("/config")
    public Map<String, Object> getLoggingConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("kafkaEnabled",
                environment.getProperty("sprival.logging.kafka.enabled", "false"));
        config.put("outputTarget",
                environment.getProperty("sprival.logging.application.output-target", "file"));
        config.put("bootstrapServers", environment
                .getProperty("sprival.logging.application.bootstrap-servers", "localhost:9092"));
        config.put("topic",
                environment.getProperty("sprival.logging.application.topic", "application-logs"));
        config.put("applicationName", LoggingUtils.getApplicationName());
        config.put("hostname", LoggingUtils.getHostname());
        config.put("timestamp", System.currentTimeMillis());

        return config;
    }

    /**
     * 获取KafkaAppender的统计信息
     */
    @GetMapping("/kafka/stats")
    public Map<String, Object> getKafkaAppenderStats() {
        Map<String, Object> result = new HashMap<>();

        try {
            LoggerContext context = (LoggerContext) org.slf4j.LoggerFactory.getILoggerFactory();
            Appender<?> kafkaAppender = context.getLogger("ROOT").getAppender("KAFKA");

            if (kafkaAppender instanceof KafkaAppender) {
                KafkaAppender appender = (KafkaAppender) kafkaAppender;
                result.put("found", true);
                result.put("healthy", appender.isHealthy());
                result.put("started", appender.isStarted());
                result.put("statistics", appender.getStatistics());
            } else {
                result.put("found", false);
                result.put("message", "KafkaAppender not found or not configured");
            }
        } catch (Exception e) {
            result.put("error", e.getMessage());
            log.error("Error getting KafkaAppender stats", e);
        }

        return result;
    }

    /**
     * 重置KafkaAppender的统计信息
     */
    @PostMapping("/kafka/reset-stats")
    public Map<String, Object> resetKafkaAppenderStats() {
        Map<String, Object> result = new HashMap<>();

        try {
            LoggerContext context = (LoggerContext) org.slf4j.LoggerFactory.getILoggerFactory();
            Appender<?> kafkaAppender = context.getLogger("ROOT").getAppender("KAFKA");

            if (kafkaAppender instanceof KafkaAppender) {
                KafkaAppender appender = (KafkaAppender) kafkaAppender;
                appender.resetStatistics();
                result.put("success", true);
                result.put("message", "Statistics reset successfully");
            } else {
                result.put("success", false);
                result.put("message", "KafkaAppender not found");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
            log.error("Error resetting KafkaAppender stats", e);
        }

        return result;
    }

    /**
     * 测试KafkaAppender的性能
     */
    @GetMapping("/kafka/performance")
    public Map<String, Object> testKafkaAppenderPerformance(
            @RequestParam(defaultValue = "100") int messageCount,
            @RequestParam(defaultValue = "100") int delayMs) {

        Map<String, Object> result = new HashMap<>();
        long startTime = System.currentTimeMillis();

        try {
            log.info("开始KafkaAppender性能测试 - 消息数量: {}, 延迟: {}ms", messageCount, delayMs);

            for (int i = 1; i <= messageCount; i++) {
                log.info("性能测试消息 #{} - 时间戳: {}", i, System.currentTimeMillis());

                if (delayMs > 0) {
                    Thread.sleep(delayMs);
                }
            }

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            result.put("success", true);
            result.put("messageCount", messageCount);
            result.put("duration", duration);
            result.put("avgTimePerMessage", duration / (double) messageCount);
            result.put("messagesPerSecond", messageCount * 1000.0 / duration);

            log.info("KafkaAppender性能测试完成 - 总耗时: {}ms, 平均每条消息: {}ms", duration,
                    duration / (double) messageCount);

        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
            log.error("KafkaAppender性能测试失败", e);
        }

        return result;
    }
}
