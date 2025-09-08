package com.soyokra.sprival.config.http;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import com.soyokra.sprival.client.UserServiceClient;

/**
 * HTTP客户端健康检查指示器 监控Feign客户端的健康状态
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Component
public class SprivalHttpClientHealthIndicator implements HealthIndicator {

    @Autowired(required = false)
    private UserServiceClient userServiceClient;

    @Override
    public Health health() {
        try {
            Map<String, Object> details = new HashMap<>();

            // 检查用户服务连接
            if (userServiceClient != null) {
                try {
                    UserServiceClient.UserResponse user = userServiceClient.getUserById(1L);
                    details.put("userService", "Available");
                    details.put("userServiceResponse", user != null ? "Success" : "Empty Response");
                } catch (Exception e) {
                    details.put("userService", "Unavailable");
                    details.put("userServiceError", e.getMessage());
                    // 不设置为不健康，因为可能是正常的降级响应
                }
            } else {
                details.put("userService", "Not Configured");
            }

            // 添加时间戳
            details.put("timestamp", System.currentTimeMillis());
            details.put("checkTime", java.time.Instant.now().toString());

            return Health.up().withDetails(details).build();

        } catch (Exception e) {
            return Health.down().withDetail("error", e.getMessage())
                    .withDetail("exception", e.getClass().getSimpleName())
                    .withDetail("timestamp", System.currentTimeMillis()).build();
        }
    }
}
