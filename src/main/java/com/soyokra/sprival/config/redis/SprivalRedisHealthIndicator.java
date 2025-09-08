package com.soyokra.sprival.config.redis;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
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
public class SprivalRedisHealthIndicator implements HealthIndicator {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

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

            // 测试StringRedisTemplate
            String stringTestKey = "health:string:" + System.currentTimeMillis();
            stringRedisTemplate.opsForValue().set(stringTestKey, testValue, Duration.ofSeconds(10));
            String stringRetrievedValue = stringRedisTemplate.opsForValue().get(stringTestKey);
            stringRedisTemplate.delete(stringTestKey);

            // 验证结果
            if (testValue.equals(retrievedValue) && testValue.equals(stringRetrievedValue)) {
                return Health.up().withDetail("redis", "Available")
                        .withDetail("redisTemplate", "Working")
                        .withDetail("stringRedisTemplate", "Working")
                        .withDetail("timestamp", System.currentTimeMillis())
                        .withDetail("testKey", testKey).build();
            } else {
                return Health.down().withDetail("redis", "Unavailable")
                        .withDetail("error", "Health check failed - value mismatch")
                        .withDetail("expected", testValue)
                        .withDetail("redisTemplateResult", retrievedValue)
                        .withDetail("stringRedisTemplateResult", stringRetrievedValue).build();
            }
        } catch (Exception e) {
            return Health.down().withDetail("redis", "Unavailable")
                    .withDetail("error", e.getMessage())
                    .withDetail("exception", e.getClass().getSimpleName())
                    .withDetail("timestamp", System.currentTimeMillis()).build();
        }
    }
}
