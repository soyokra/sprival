package com.soyokra.sprival.config.redis;

import java.time.Duration;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Redis健康检查指示器
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Component
@ConditionalOnProperty(name = "spring.redis.host", matchIfMissing = false)
public class SprivalRedisHealthIndicator implements HealthIndicator {

    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;

    public SprivalRedisHealthIndicator(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.stringRedisTemplate = null; // 暂时设为null，避免依赖注入问题
    }

    @Override
    public Health health() {
        try {
            // 执行简单的Redis操作来检查连接
            String testKey = "health:check:" + System.currentTimeMillis();
            String testValue = "ok";

            // 测试RedisTemplate
            redisTemplate.opsForValue().set(testKey, testValue, Duration.ofSeconds(10));
            Object retrievedValue = redisTemplate.opsForValue().get(testKey);
            redisTemplate.delete(testKey);

            // 验证RedisTemplate结果
            if (!testValue.equals(retrievedValue)) {
                return Health.down().withDetail("redis", "Unavailable")
                        .withDetail("error", "Health check failed - value mismatch")
                        .withDetail("expected", testValue)
                        .withDetail("redisTemplateResult", retrievedValue).build();
            }

            // 测试StringRedisTemplate（如果可用）
            String stringRedisTemplateStatus = "Not Available";
            if (stringRedisTemplate != null) {
                try {
                    String stringTestKey = "health:string:" + System.currentTimeMillis();
                    stringRedisTemplate.opsForValue().set(stringTestKey, testValue,
                            Duration.ofSeconds(10));
                    String stringRetrievedValue =
                            stringRedisTemplate.opsForValue().get(stringTestKey);
                    stringRedisTemplate.delete(stringTestKey);

                    if (testValue.equals(stringRetrievedValue)) {
                        stringRedisTemplateStatus = "Working";
                    } else {
                        stringRedisTemplateStatus = "Failed";
                    }
                } catch (Exception e) {
                    stringRedisTemplateStatus = "Error: " + e.getMessage();
                }
            }

            return Health.up().withDetail("redis", "Available")
                    .withDetail("redisTemplate", "Working")
                    .withDetail("stringRedisTemplate", stringRedisTemplateStatus)
                    .withDetail("timestamp", System.currentTimeMillis())
                    .withDetail("testKey", testKey).build();

        } catch (Exception e) {
            return Health.down().withDetail("redis", "Unavailable")
                    .withDetail("error", e.getMessage())
                    .withDetail("exception", e.getClass().getSimpleName())
                    .withDetail("timestamp", System.currentTimeMillis()).build();
        }
    }
}
