package com.soyokra.sprival.config.redis;

import java.time.Duration;
import java.util.Map;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import com.soyokra.sprival.support.health.SprivalBaseHealthIndicator;

/**
 * Redis健康检查指示器
 * 支持强依赖和弱依赖模式配置
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Component
@ConditionalOnProperty(name = "spring.redis.host", matchIfMissing = false)
public class SprivalRedisHealthIndicatorV2 extends SprivalBaseHealthIndicator {

    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;

    public SprivalRedisHealthIndicatorV2(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.stringRedisTemplate = null; // 暂时设为null，避免依赖注入问题
    }
    
    @Override
    protected String getComponentName() {
        return "redis";
    }
    
    @Override
    protected Health doHealthCheck() {
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
                Map<String, Object> details = getHealthCheckDetails();
                details.put("redis", "Unavailable");
                details.put("error", "Health check failed - value mismatch");
                details.put("expected", testValue);
                details.put("redisTemplateResult", retrievedValue);
                return createDownHealth(details);
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

            Map<String, Object> details = getHealthCheckDetails();
            details.put("redis", "Available");
            details.put("redisTemplate", "Working");
            details.put("stringRedisTemplate", stringRedisTemplateStatus);
            details.put("testKey", testKey);
            
            return createUpHealth(details);

        } catch (Exception e) {
            return handleHealthCheckException(e);
        }
    }
    
    @Override
    protected Map<String, Object> getHealthCheckDetails() {
        Map<String, Object> details = super.getHealthCheckDetails();
        details.put("redisTemplate", "Available");
        return details;
    }
    
    @Override
    protected void beforeHealthCheck() {
        logHealthCheck("DEBUG", "开始执行Redis健康检查");
    }
    
    @Override
    protected void afterHealthCheck(Health health) {
        if (health.getStatus().equals(org.springframework.boot.actuate.health.Status.UP)) {
            logHealthCheck("DEBUG", "Redis健康检查完成，状态正常");
        } else {
            logHealthCheck("WARN", "Redis健康检查完成，状态异常: {}", health.getDetails());
        }
    }
}
