package com.soyokra.sprival.support.health.example;

import java.util.Map;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import com.soyokra.sprival.support.health.SprivalBaseHealthIndicator;

/**
 * 示例健康检查指示器
 * 展示如何使用SprivalBaseHealthIndicator
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Component
@ConditionalOnProperty(name = "sprival.health.example.enabled", havingValue = "true", matchIfMissing = false)
public class SprivalExampleHealthIndicator extends SprivalBaseHealthIndicator {
    
    @Override
    protected String getComponentName() {
        return "example";
    }
    
    @Override
    protected Health doHealthCheck() {
        try {
            // 模拟健康检查逻辑
            boolean isHealthy = performExampleHealthCheck();
            
            Map<String, Object> details = getHealthCheckDetails();
            details.put("status", isHealthy ? "正常" : "异常");
            details.put("checkResult", isHealthy ? "PASS" : "FAIL");
            details.put("responseTime", System.currentTimeMillis() % 1000); // 模拟响应时间
            
            return isHealthy ? createUpHealth(details) : createDownHealth(details);
            
        } catch (Exception e) {
            return handleHealthCheckException(e);
        }
    }
    
    /**
     * 执行示例健康检查
     */
    private boolean performExampleHealthCheck() {
        // 模拟健康检查逻辑
        // 这里可以添加实际的健康检查代码
        return true; // 示例中总是返回健康
    }
    
    @Override
    protected Map<String, Object> getHealthCheckDetails() {
        Map<String, Object> details = super.getHealthCheckDetails();
        details.put("componentType", "Example");
        details.put("version", "1.0.0");
        return details;
    }
    
    @Override
    protected void beforeHealthCheck() {
        logHealthCheck("DEBUG", "开始执行示例健康检查");
    }
    
    @Override
    protected void afterHealthCheck(Health health) {
        if (health.getStatus().equals(org.springframework.boot.actuate.health.Status.UP)) {
            logHealthCheck("DEBUG", "示例健康检查完成，状态正常");
        } else {
            logHealthCheck("WARN", "示例健康检查完成，状态异常: {}", health.getDetails());
        }
    }
    
    @Override
    protected long getHealthCheckTimeout() {
        // 自定义超时时间为3秒
        return 3000;
    }
    
    @Override
    protected boolean isHealthCheckEnabled() {
        // 自定义启用条件
        return super.isHealthCheckEnabled() && isExampleServiceAvailable();
    }
    
    /**
     * 检查示例服务是否可用
     */
    private boolean isExampleServiceAvailable() {
        // 这里可以添加服务可用性检查逻辑
        return true;
    }
}
