package com.soyokra.sprival.performance.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

/**
 * 性能测试配置
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "performance.test")
public class PerformanceTestConfig {

    /**
     * 测试基础URL
     */
    private String baseUrl = "http://localhost:8338";

    /**
     * 并发用户数
     */
    private int concurrentUsers = 100;

    /**
     * 测试持续时间（秒）
     */
    private int durationSeconds = 60;

    /**
     * 预热时间（秒）
     */
    private int warmupSeconds = 10;

    /**
     * 每秒目标请求数（TPS）
     */
    private int targetTps = 1000;

    /**
     * 是否启用详细日志
     */
    private boolean verboseLogging = false;

    /**
     * 报告输出目录
     */
    private String reportOutputDir = "target/performance-reports";
}

