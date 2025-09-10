package com.soyokra.sprival.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.soyokra.sprival.support.health.SprivalHealthProperties;

/**
 * 健康检查控制测试控制器
 * 用于测试"默认不运行"功能
 * 
 * @author Sprival Team
 * @version 1.0
 */
@RestController
@RequestMapping("/health-control")
public class HealthControlTestController {

    @Autowired
    private SprivalHealthProperties healthProperties;

    @Autowired
    private HealthEndpoint healthEndpoint;

    /**
     * 获取当前健康检查配置状态
     */
    @GetMapping("/status")
    public Map<String, Object> getHealthStatus() {
        Map<String, Object> status = new HashMap<>();
        
        // 全局状态
        status.put("globalEnabled", healthProperties.isEnabled());
        status.put("alertLogEnabled", healthProperties.isAlertLogEnabled());
        status.put("alertLogLevel", healthProperties.getAlertLogLevel());
        
        // 各组件状态
        Map<String, Object> components = new HashMap<>();
        String[] componentNames = {"redis", "mongodb", "kafka", "elasticsearch", "clickhouse", "rabbitmq"};
        
        for (String component : componentNames) {
            Map<String, Object> componentStatus = new HashMap<>();
            componentStatus.put("enabled", healthProperties.getComponentEnabled(component));
            componentStatus.put("dependencyMode", healthProperties.getDependencyMode(component).getCode());
            components.put(component, componentStatus);
        }
        
        status.put("components", components);
        
        // 整体健康状态
        HealthComponent overallHealth = healthEndpoint.health();
        if (overallHealth instanceof Health) {
            Health health = (Health) overallHealth;
            status.put("overallHealth", health.getStatus().getCode());
            status.put("healthDetails", health.getDetails());
        } else {
            status.put("overallHealth", "UNKNOWN");
            status.put("healthDetails", "HealthComponent type: " + overallHealth.getClass().getSimpleName());
        }
        
        return status;
    }

    /**
     * 启用指定组件的健康检查
     */
    @PostMapping("/enable")
    public Map<String, Object> enableComponent(@RequestParam String component) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            healthProperties.setComponentEnabled(component, true);
            result.put("success", true);
            result.put("message", "组件 " + component + " 健康检查已启用");
            result.put("component", component);
            result.put("enabled", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "启用失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 禁用指定组件的健康检查
     */
    @PostMapping("/disable")
    public Map<String, Object> disableComponent(@RequestParam String component) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            healthProperties.setComponentEnabled(component, false);
            result.put("success", true);
            result.put("message", "组件 " + component + " 健康检查已禁用");
            result.put("component", component);
            result.put("enabled", false);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "禁用失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 批量设置组件健康检查状态
     */
    @PostMapping("/batch-set")
    public Map<String, Object> batchSetComponents(@RequestParam Map<String, String> params) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> changes = new HashMap<>();
        
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String component = entry.getKey();
                boolean enabled = "true".equalsIgnoreCase(entry.getValue());
                
                healthProperties.setComponentEnabled(component, enabled);
                changes.put(component, enabled);
            }
            
            result.put("success", true);
            result.put("message", "批量设置完成");
            result.put("changes", changes);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "批量设置失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取Spring Boot默认健康检查状态
     */
    @GetMapping("/spring-default-status")
    public Map<String, Object> getSpringDefaultStatus() {
        Map<String, Object> status = new HashMap<>();
        
        // 检查Spring Boot默认健康检查是否被禁用
        status.put("redisDisabled", "management.health.redis.enabled = false");
        status.put("mongoDisabled", "management.health.mongo.enabled = false");
        status.put("dbDisabled", "management.health.db.enabled = false");
        status.put("rabbitDisabled", "management.health.rabbit.enabled = false");
        status.put("elasticsearchDisabled", "management.health.elasticsearch.enabled = false");
        
        status.put("message", "Spring Boot默认健康检查已禁用，使用Sprival自定义健康检查");
        
        return status;
    }

    /**
     * 测试健康检查功能
     */
    @GetMapping("/test")
    public Map<String, Object> testHealthCheck() {
        Map<String, Object> result = new HashMap<>();
        
        // 获取当前健康状态
        HealthComponent healthComponent = healthEndpoint.health();
        if (healthComponent instanceof Health) {
            Health health = (Health) healthComponent;
            result.put("overallStatus", health.getStatus().getCode());
            result.put("details", health.getDetails());
        } else {
            result.put("overallStatus", "UNKNOWN");
            result.put("details", "HealthComponent type: " + healthComponent.getClass().getSimpleName());
        }
        
        // 检查各组件是否启用
        Map<String, Object> componentStatus = new HashMap<>();
        String[] components = {"redis", "mongodb", "kafka", "elasticsearch", "clickhouse", "rabbitmq"};
        
        for (String component : components) {
            boolean enabled = healthProperties.getComponentEnabled(component);
            componentStatus.put(component, enabled ? "启用" : "禁用");
        }
        
        result.put("componentStatus", componentStatus);
        result.put("testTime", System.currentTimeMillis());
        
        return result;
    }
}
