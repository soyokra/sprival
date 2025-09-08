package com.soyokra.sprival.config.http;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Data;

/**
 * HTTP客户端配置属性
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Component
@Data
@ConfigurationProperties(prefix = "sprival.http.client")
public class SprivalHttpClientProperties {

    /**
     * 连接超时时间
     */
    private Duration connectTimeout = Duration.ofSeconds(5);

    /**
     * 读取超时时间
     */
    private Duration readTimeout = Duration.ofSeconds(10);

    /**
     * 写入超时时间
     */
    private Duration writeTimeout = Duration.ofSeconds(10);

    /**
     * 连接池最大连接数
     */
    private int maxConnections = 200;

    /**
     * 连接池最大空闲连接数
     */
    private int maxIdleConnections = 50;

    /**
     * 连接保持活跃时间
     */
    private Duration keepAliveDuration = Duration.ofMinutes(5);

    /**
     * 熔断器配置
     */
    private CircuitBreaker circuitBreaker = new CircuitBreaker();

    /**
     * 重试配置
     */
    private Retry retry = new Retry();

    /**
     * 熔断器配置
     */
    @Data
    public static class CircuitBreaker {
        /**
         * 失败率阈值
         */
        private int failureRateThreshold = 50;

        /**
         * 熔断器打开后等待时间
         */
        private Duration waitDurationInOpenState = Duration.ofSeconds(30);

        /**
         * 滑动窗口大小
         */
        private int slidingWindowSize = 10;

        /**
         * 最小调用次数
         */
        private int minimumNumberOfCalls = 5;

        /**
         * 半开状态允许的调用次数
         */
        private int permittedNumberOfCallsInHalfOpenState = 3;
    }

    /**
     * 重试配置
     */
    @Data
    public static class Retry {
        /**
         * 最大重试次数
         */
        private int maxAttempts = 3;

        /**
         * 重试间隔时间
         */
        private Duration waitDuration = Duration.ofSeconds(1);

        /**
         * 是否启用重试
         */
        private boolean enabled = true;
    }
}
