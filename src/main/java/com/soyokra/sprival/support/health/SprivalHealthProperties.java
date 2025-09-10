package com.soyokra.sprival.support.health;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Data;

/**
 * Sprival健康检查配置属性
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "sprival.health")
public class SprivalHealthProperties {
    
    /**
     * 是否启用健康检查功能
     */
    private boolean enabled = true;
    
    /**
     * 是否启用告警日志
     */
    private boolean alertLogEnabled = true;
    
    /**
     * 告警日志级别
     */
    private String alertLogLevel = "WARN";
    
    /**
     * 健康检查超时时间（毫秒）
     */
    private long timeout = 5000;
    
    /**
     * 健康检查间隔时间（毫秒）
     */
    private long interval = 30000;
    
    /**
     * 中间件依赖模式配置
     * key: 中间件名称 (mysql, redis, kafka, mongodb, elasticsearch, clickhouse, rabbitmq)
     * value: 依赖模式 (strong/weak)
     */
    private Map<String, String> dependencyModes = new HashMap<>();
    
    /**
     * 默认依赖模式
     */
    private String defaultDependencyMode = "strong";
    
    /**
     * 各组件是否启用健康检查（默认不启用）
     * key: 组件名称, value: 是否启用
     */
    private Map<String, Boolean> componentEnabled = new HashMap<>();
    
    /**
     * 获取指定中间件的依赖模式
     */
    public SprivalHealthDependencyMode getDependencyMode(String component) {
        String modeCode = dependencyModes.getOrDefault(component, defaultDependencyMode);
        return SprivalHealthDependencyMode.fromCode(modeCode);
    }
    
    /**
     * 设置中间件依赖模式
     */
    public void setDependencyMode(String component, SprivalHealthDependencyMode mode) {
        dependencyModes.put(component, mode.getCode());
    }
    
    /**
     * 获取组件是否启用
     */
    public boolean getComponentEnabled(String component) {
        return componentEnabled.getOrDefault(component, false);
    }
    
    /**
     * 设置组件是否启用
     */
    public void setComponentEnabled(String component, boolean enabled) {
        componentEnabled.put(component, enabled);
    }
    
    /**
     * 初始化默认配置
     */
    public void initDefaultConfig() {
        if (dependencyModes.isEmpty()) {
            // 设置默认的强依赖中间件
            dependencyModes.put("mysql", "strong");
            dependencyModes.put("redis", "strong");
            
            // 设置默认的弱依赖中间件
            dependencyModes.put("kafka", "weak");
            dependencyModes.put("mongodb", "weak");
            dependencyModes.put("elasticsearch", "weak");
            dependencyModes.put("clickhouse", "weak");
            dependencyModes.put("rabbitmq", "weak");
        }
        
        // 默认不启用任何组件的健康检查
        if (componentEnabled.isEmpty()) {
            componentEnabled.put("redis", false);
            componentEnabled.put("mongodb", false);
            componentEnabled.put("kafka", false);
            componentEnabled.put("elasticsearch", false);
            componentEnabled.put("clickhouse", false);
            componentEnabled.put("rabbitmq", false);
        }
    }
}
