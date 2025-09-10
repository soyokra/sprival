package com.soyokra.sprival.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.soyokra.sprival.support.health.SprivalHealthManager;
import com.soyokra.sprival.support.health.SprivalHealthProperties;
import com.soyokra.sprival.support.health.SprivalHealthDependencyMode;

/**
 * 健康检查依赖模式测试控制器
 * 
 * @author Sprival Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/health-test")
public class HealthDependencyTestController {
    
    @Autowired(required = false)
    private SprivalHealthManager healthManager;
    
    @Autowired
    private SprivalHealthProperties healthProperties;
    
    /**
     * 获取健康检查配置信息
     */
    @GetMapping("/config")
    public Map<String, Object> getHealthConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("enabled", healthProperties.isEnabled());
        config.put("alertLogEnabled", healthProperties.isAlertLogEnabled());
        config.put("alertLogLevel", healthProperties.getAlertLogLevel());
        config.put("timeout", healthProperties.getTimeout());
        config.put("interval", healthProperties.getInterval());
        config.put("defaultDependencyMode", healthProperties.getDefaultDependencyMode());
        config.put("dependencyModes", healthProperties.getDependencyModes());
        return config;
    }
    
    /**
     * 获取所有组件的健康状态
     */
    @GetMapping("/components")
    public Map<String, Object> getComponentHealth() {
        Map<String, Object> result = new HashMap<>();
        
        if (healthManager != null) {
            Map<String, Health> componentHealth = healthManager.getAllComponentHealth();
            result.put("components", componentHealth);
            result.put("managerAvailable", true);
        } else {
            result.put("components", new HashMap<>());
            result.put("managerAvailable", false);
            result.put("message", "健康管理器未启用");
        }
        
        return result;
    }
    
    /**
     * 测试特定组件的依赖模式
     */
    @GetMapping("/test-dependency-mode")
    public Map<String, Object> testDependencyMode() {
        Map<String, Object> result = new HashMap<>();
        
        // 测试各个组件的依赖模式
        String[] components = {"mysql", "redis", "kafka", "mongodb", "elasticsearch", "clickhouse", "rabbitmq"};
        
        for (String component : components) {
            SprivalHealthDependencyMode mode = healthProperties.getDependencyMode(component);
            Map<String, String> componentInfo = new HashMap<>();
            componentInfo.put("mode", mode.getCode());
            componentInfo.put("description", mode.getDescription());
            result.put(component, componentInfo);
        }
        
        return result;
    }
    
    /**
     * 模拟组件故障测试
     */
    @GetMapping("/simulate-failure")
    public Map<String, Object> simulateFailure() {
        Map<String, Object> result = new HashMap<>();
        
        if (healthManager == null) {
            result.put("error", "健康管理器未启用");
            return result;
        }
        
        // 这里可以添加模拟故障的逻辑
        result.put("message", "故障模拟功能需要根据具体组件实现");
        result.put("availableComponents", healthManager.getAllComponentHealth().keySet());
        
        return result;
    }
}
