package com.soyokra.sprival.config.redis;

import java.util.Map;
import org.springframework.boot.actuate.autoconfigure.health.CompositeHealthContributorConfiguration;
import org.springframework.boot.actuate.autoconfigure.health.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.actuate.health.HealthContributor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Redis健康检查自动配置类
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(RedisTemplate.class)
@ConditionalOnEnabledHealthIndicator("redis")
public class SprivalRedisHealthContributorAutoConfiguration extends
        CompositeHealthContributorConfiguration<SprivalRedisHealthIndicator, RedisTemplate> {

    @Bean
    public HealthContributor redisHealthContributor(Map<String, RedisTemplate> redisTemplates) {
        return createContributor(redisTemplates);
    }
}
