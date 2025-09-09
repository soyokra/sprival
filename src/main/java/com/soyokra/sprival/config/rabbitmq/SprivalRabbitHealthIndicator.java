package com.soyokra.sprival.config.rabbitmq;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * Sprival RabbitMQ 健康检查指示器 提供增强的健康检查功能，包括连接状态、性能指标等
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Slf4j
@Component
@ConditionalOnClass(RabbitTemplate.class)
@ConditionalOnProperty(name = "sprival.rabbitmq.enabled", havingValue = "true",
        matchIfMissing = true)
@ConditionalOnProperty(prefix = "sprival.rabbitmq.monitor", name = "health-check-enabled",
        havingValue = "true", matchIfMissing = true)
public class SprivalRabbitHealthIndicator implements HealthIndicator {

    private final RabbitTemplate rabbitTemplate;
    private final SprivalRabbitProperties properties;

    public SprivalRabbitHealthIndicator(RabbitTemplate rabbitTemplate,
            SprivalRabbitProperties properties) {
        this.rabbitTemplate = rabbitTemplate;
        this.properties = properties;
    }

    @Override
    public Health health() {
        try {
            return performHealthCheck();
        } catch (Exception e) {
            log.error("RabbitMQ 健康检查失败", e);
            return Health.down().withDetail("error", e.getMessage())
                    .withDetail("timestamp", LocalDateTime.now()).build();
        }
    }

    private Health performHealthCheck() throws Exception {
        Map<String, Object> details = new HashMap<>();

        // 基础连接检查
        String version = getRabbitMQVersion();
        details.put("version", version);
        details.put("timestamp", LocalDateTime.now());

        // 连接池状态检查
        if (rabbitTemplate
                .getConnectionFactory() instanceof org.springframework.amqp.rabbit.connection.CachingConnectionFactory) {
            org.springframework.amqp.rabbit.connection.CachingConnectionFactory factory =
                    (org.springframework.amqp.rabbit.connection.CachingConnectionFactory) rabbitTemplate
                            .getConnectionFactory();

            details.put("connection-cache-size", factory.getConnectionCacheSize());
            details.put("channel-cache-size", factory.getChannelCacheSize());
            details.put("cache-mode", factory.getCacheMode().name());
        }

        // 性能指标检查
        try {
            long startTime = System.currentTimeMillis();
            rabbitTemplate.convertAndSend("health-check-queue", "health-check-message");
            long responseTime = System.currentTimeMillis() - startTime;

            details.put("response-time-ms", responseTime);
            details.put("status", "UP");

            // 根据响应时间设置健康状态
            if (responseTime > properties.getMonitor().getHealthCheckTimeout()) {
                return Health.down().withDetails(details)
                        .withDetail("warning", "响应时间过长: " + responseTime + "ms").build();
            }

            return Health.up().withDetails(details).build();

        } catch (Exception e) {
            details.put("status", "DOWN");
            details.put("error", e.getMessage());
            return Health.down().withDetails(details).build();
        }
    }

    private String getRabbitMQVersion() {
        try {
            return rabbitTemplate.execute(channel -> {
                try {
                    return channel.getConnection().getServerProperties().get("version").toString();
                } catch (Exception e) {
                    log.warn("无法获取 RabbitMQ 版本信息", e);
                    return "unknown";
                }
            });
        } catch (Exception e) {
            log.warn("获取 RabbitMQ 版本失败", e);
            return "unknown";
        }
    }
}
