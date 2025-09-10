package com.soyokra.sprival.support.health;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import lombok.extern.slf4j.Slf4j;

/**
 * Sprival健康检查配置类
 * 负责初始化健康检查相关配置
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = "sprival.health.enabled", havingValue = "true", matchIfMissing = true)
public class SprivalHealthConfiguration {
    
    @Autowired
    private SprivalHealthProperties healthProperties;
    
    @PostConstruct
    public void initHealthConfiguration() {
        // 初始化默认配置
        healthProperties.initDefaultConfig();
        
        log.info("Sprival健康检查配置已初始化");
        log.info("健康检查功能: {}", healthProperties.isEnabled() ? "启用" : "禁用");
        log.info("告警日志功能: {}", healthProperties.isAlertLogEnabled() ? "启用" : "禁用");
        log.info("告警日志级别: {}", healthProperties.getAlertLogLevel());
        log.info("默认依赖模式: {}", healthProperties.getDefaultDependencyMode());
        log.info("中间件依赖模式配置: {}", healthProperties.getDependencyModes());
    }
}
