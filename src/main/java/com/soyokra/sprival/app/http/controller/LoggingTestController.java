package com.soyokra.sprival.app.http.controller;

import java.util.HashMap;
import java.util.Map;

import com.soyokra.sprival.support.logging.LoggingUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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

    /**
     * 测试基本日志输出
     */
    @GetMapping("/basic")
    public Map<String, Object> testBasicLogging() {
        log.info("测试基本日志输出 - 时间戳: {}", System.currentTimeMillis());
        log.debug("这是一条DEBUG级别的日志");
        log.warn("这是一条WARN级别的日志");

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
        config.put("kafkaEnabled", System.getProperty("sprival.logging.kafka.enabled", "false"));
        config.put("outputTarget",
                System.getProperty("sprival.logging.application.output-target", "file"));
        config.put("bootstrapServers", System
                .getProperty("sprival.logging.application.bootstrap-servers", "localhost:9092"));
        config.put("topic",
                System.getProperty("sprival.logging.application.topic", "application-logs"));
        config.put("applicationName", LoggingUtils.getApplicationName());
        config.put("hostname", LoggingUtils.getHostname());
        config.put("timestamp", System.currentTimeMillis());

        return config;
    }
}
