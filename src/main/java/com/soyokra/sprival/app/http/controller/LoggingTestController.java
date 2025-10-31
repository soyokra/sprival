package com.soyokra.sprival.app.http.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

}
