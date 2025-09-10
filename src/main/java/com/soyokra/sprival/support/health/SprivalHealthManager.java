package com.soyokra.sprival.support.health;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * Sprival健康检查管理器
 * 负责管理中间件的强依赖和弱依赖健康检查逻辑
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Slf4j
@Component
public class SprivalHealthManager {
    
    @Autowired
    private SprivalHealthProperties healthProperties;
    
    /**
     * 组件健康状态缓存
     * key: 组件名称, value: 健康状态
     */
    private final Map<String, Health> componentHealthCache = new ConcurrentHashMap<>();
    
    /**
     * 组件最后检查时间缓存
     * key: 组件名称, value: 最后检查时间
     */
    private final Map<String, LocalDateTime> lastCheckTimeCache = new ConcurrentHashMap<>();
    
    /**
     * 检查组件健康状态
     * 
     * @param component 组件名称
     * @param healthIndicator 健康检查指示器
     * @return 健康状态
     */
    public Health checkComponentHealth(String component, HealthIndicator healthIndicator) {
        try {
            // 执行健康检查
            Health health = healthIndicator.health();
            
            // 更新缓存
            componentHealthCache.put(component, health);
            lastCheckTimeCache.put(component, LocalDateTime.now());
            
            // 根据依赖模式处理结果
            SprivalHealthDependencyMode mode = healthProperties.getDependencyMode(component);
            
            if (health.getStatus().equals(org.springframework.boot.actuate.health.Status.UP)) {
                // 组件健康，直接返回
                return health;
            } else {
                // 组件不健康，根据依赖模式处理
                return handleUnhealthyComponent(component, health, mode);
            }
            
        } catch (Exception e) {
            log.error("检查组件 {} 健康状态时发生异常", component, e);
            return handleUnhealthyComponent(component, 
                Health.down().withDetail("error", e.getMessage()).build(), 
                healthProperties.getDependencyMode(component));
        }
    }
    
    /**
     * 处理不健康的组件
     */
    private Health handleUnhealthyComponent(String component, Health health, SprivalHealthDependencyMode mode) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        
        if (mode == SprivalHealthDependencyMode.STRONG) {
            // 强依赖模式：返回DOWN状态
            log.error("强依赖组件 {} 不可用，应用健康状态为DOWN", component);
            return Health.down()
                .withDetail("component", component)
                .withDetail("dependencyMode", "STRONG")
                .withDetail("status", "DOWN")
                .withDetail("timestamp", timestamp)
                .withDetails(health.getDetails())
                .build();
                
        } else {
            // 弱依赖模式：返回UP状态，但记录告警日志
            logAlert(component, health, timestamp);
            
            return Health.up()
                .withDetail("component", component)
                .withDetail("dependencyMode", "WEAK")
                .withDetail("status", "UP")
                .withDetail("warning", "组件不可用但应用仍可运行")
                .withDetail("timestamp", timestamp)
                .withDetails(health.getDetails())
                .build();
        }
    }
    
    /**
     * 记录告警日志
     */
    private void logAlert(String component, Health health, String timestamp) {
        if (!healthProperties.isAlertLogEnabled()) {
            return;
        }
        
        String logMessage = String.format(
            "【健康检查告警】弱依赖组件 %s 不可用，但应用仍可正常运行。错误信息: %s",
            component,
            health.getDetails().getOrDefault("error", "未知错误")
        );
        
        // 根据配置的日志级别记录
        String logLevel = healthProperties.getAlertLogLevel().toUpperCase();
        switch (logLevel) {
            case "ERROR":
                log.error(logMessage);
                break;
            case "WARN":
                log.warn(logMessage);
                break;
            case "INFO":
                log.info(logMessage);
                break;
            default:
                log.warn(logMessage);
        }
    }
    
    /**
     * 获取组件健康状态
     */
    public Health getComponentHealth(String component) {
        return componentHealthCache.get(component);
    }
    
    /**
     * 获取组件最后检查时间
     */
    public LocalDateTime getLastCheckTime(String component) {
        return lastCheckTimeCache.get(component);
    }
    
    /**
     * 清除组件健康状态缓存
     */
    public void clearComponentHealth(String component) {
        componentHealthCache.remove(component);
        lastCheckTimeCache.remove(component);
    }
    
    /**
     * 清除所有健康状态缓存
     */
    public void clearAllHealthCache() {
        componentHealthCache.clear();
        lastCheckTimeCache.clear();
    }
    
    /**
     * 获取所有组件健康状态
     */
    public Map<String, Health> getAllComponentHealth() {
        return new HashMap<>(componentHealthCache);
    }
}
