package com.soyokra.sprival.config.kafka;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * Sprival Kafka 健康检查指示器
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "sprival.kafka.monitor", name = "healthCheckEnabled",
        havingValue = "true", matchIfMissing = true)
public class SprivalKafkaHealthIndicator implements HealthIndicator {

    @Autowired(required = false)
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private SprivalKafkaProperties kafkaProperties;

    @Override
    public Health health() {
        try {
            // 检查Kafka连接状态
            if (kafkaTemplate == null) {
                return Health.down().withDetail("kafka", "KafkaTemplate not available")
                        .withDetail("timestamp",
                                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                        .build();
            }

            // 使用AdminClient检查Kafka集群状态
            Properties props = new Properties();
            props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
            props.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG,
                    kafkaProperties.getMonitor().getHealthCheckTimeout());

            try (AdminClient adminClient = AdminClient.create(props)) {
                // 尝试列出主题来验证连接
                ListTopicsResult listTopics = adminClient.listTopics();
                listTopics.names().get(kafkaProperties.getMonitor().getHealthCheckTimeout(),
                        TimeUnit.MILLISECONDS);

                return Health.up().withDetail("kafka", "Available")
                        .withDetail("bootstrap-servers", "localhost:9092")
                        .withDetail("producer-enabled", kafkaProperties.getProducer().isEnabled())
                        .withDetail("consumer-enabled", kafkaProperties.getConsumer().isEnabled())
                        .withDetail("consumer-group", kafkaProperties.getConsumer().getGroupId())
                        .withDetail("timestamp",
                                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                        .build();
            }

        } catch (Exception e) {
            log.error("Kafka健康检查失败", e);
            return Health.down().withDetail("kafka", "Unavailable")
                    .withDetail("error", e.getMessage())
                    .withDetail("timestamp",
                            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .build();
        }
    }
}
