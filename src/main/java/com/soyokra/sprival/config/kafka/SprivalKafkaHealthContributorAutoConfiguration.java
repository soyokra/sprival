package com.soyokra.sprival.config.kafka;

import org.springframework.boot.actuate.autoconfigure.health.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * Sprival Kafka 健康检查自动配置类
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(KafkaTemplate.class)
@ConditionalOnEnabledHealthIndicator("kafka")
@ConditionalOnProperty(prefix = "sprival.kafka", name = "enabled", havingValue = "true",
        matchIfMissing = true)
public class SprivalKafkaHealthContributorAutoConfiguration {

    @Bean
    public HealthIndicator kafkaHealthIndicator() {
        return new SprivalKafkaHealthIndicator();
    }
}
