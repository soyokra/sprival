package com.soyokra.sprival.support.health;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import lombok.extern.slf4j.Slf4j;

/**
 * Sprival基础健康检查指示器
 * 提供统一的健康检查框架，支持强依赖和弱依赖模式
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Slf4j
public abstract class SprivalBaseHealthIndicator implements HealthIndicator {
    
    @Autowired(required = false)
    protected SprivalHealthManager healthManager;
    
    @Autowired
    protected SprivalHealthProperties healthProperties;
    
    /**
     * 获取组件名称
     * 子类必须实现此方法
     */
    protected abstract String getComponentName();
    
    /**
     * 执行具体的健康检查逻辑
     * 子类必须实现此方法
     * 
     * @return 健康检查结果
     */
    protected abstract Health doHealthCheck();
    
    /**
     * 获取健康检查超时时间（毫秒）
     * 子类可以重写此方法来自定义超时时间
     */
    protected long getHealthCheckTimeout() {
        return healthProperties.getTimeout();
    }
    
    /**
     * 是否启用健康检查
     * 子类可以重写此方法来自定义启用条件
     */
    protected boolean isHealthCheckEnabled() {
        // 全局开关 && 组件开关
        return healthProperties.isEnabled() && 
               healthProperties.getComponentEnabled(getComponentName());
    }
    
    /**
     * 获取健康检查的详细信息
     * 子类可以重写此方法来添加特定的详细信息
     */
    protected Map<String, Object> getHealthCheckDetails() {
        Map<String, Object> details = new HashMap<>();
        details.put("component", getComponentName());
        details.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        details.put("checkTime", System.currentTimeMillis());
        return details;
    }
    
    /**
     * 执行健康检查前的预处理
     * 子类可以重写此方法来进行预处理
     */
    protected void beforeHealthCheck() {
        // 默认实现为空，子类可以重写
    }
    
    /**
     * 执行健康检查后的后处理
     * 子类可以重写此方法来进行后处理
     * 
     * @param health 健康检查结果
     */
    protected void afterHealthCheck(Health health) {
        // 默认实现为空，子类可以重写
    }
    
    /**
     * 处理健康检查异常
     * 子类可以重写此方法来自定义异常处理
     * 
     * @param e 异常
     * @return 健康检查结果
     */
    protected Health handleHealthCheckException(Exception e) {
        log.error("组件 {} 健康检查发生异常", getComponentName(), e);
        
        Map<String, Object> details = getHealthCheckDetails();
        details.put("error", e.getMessage());
        details.put("errorType", e.getClass().getSimpleName());
        details.put("status", "ERROR");
        
        return Health.down()
                .withDetails(details)
                .build();
    }
    
    /**
     * 创建健康状态
     * 子类可以重写此方法来自定义健康状态创建
     * 
     * @param status 健康状态
     * @param details 详细信息
     * @return 健康检查结果
     */
    protected Health createHealth(org.springframework.boot.actuate.health.Status status, Map<String, Object> details) {
        return Health.status(status)
                .withDetails(details)
                .build();
    }
    
    /**
     * 创建健康状态（UP）
     * 
     * @param details 详细信息
     * @return 健康检查结果
     */
    protected Health createUpHealth(Map<String, Object> details) {
        return createHealth(org.springframework.boot.actuate.health.Status.UP, details);
    }
    
    /**
     * 创建健康状态（DOWN）
     * 
     * @param details 详细信息
     * @return 健康检查结果
     */
    protected Health createDownHealth(Map<String, Object> details) {
        return createHealth(org.springframework.boot.actuate.health.Status.DOWN, details);
    }
    
    /**
     * 执行带超时的健康检查
     * 
     * @param timeout 超时时间（毫秒）
     * @return 健康检查结果
     */
    protected Health executeHealthCheckWithTimeout(long timeout) {
        try {
            return doHealthCheck();
        } catch (Exception e) {
            return handleHealthCheckException(e);
        }
    }
    
    /**
     * 执行带超时的健康检查（使用默认超时时间）
     * 
     * @return 健康检查结果
     */
    protected Health executeHealthCheckWithTimeout() {
        return executeHealthCheckWithTimeout(getHealthCheckTimeout());
    }
    
    @Override
    public final Health health() {
        // 检查是否启用健康检查
        if (!isHealthCheckEnabled()) {
            return createUpHealth(getHealthCheckDetails());
        }
        
        // 执行预处理
        beforeHealthCheck();
        
        Health result = null;
        
        try {
            // 如果启用了健康管理器，使用强依赖/弱依赖模式
            if (healthManager != null) {
                result = healthManager.checkComponentHealth(getComponentName(), this::executeHealthCheckWithTimeout);
            } else {
                // 否则使用默认的健康检查逻辑
                result = executeHealthCheckWithTimeout();
            }
        } catch (Exception e) {
            result = handleHealthCheckException(e);
        } finally {
            // 执行后处理
            if (result != null) {
                afterHealthCheck(result);
            }
        }
        
        return result;
    }
    
    /**
     * 获取组件依赖模式
     * 
     * @return 依赖模式
     */
    protected SprivalHealthDependencyMode getDependencyMode() {
        return healthProperties.getDependencyMode(getComponentName());
    }
    
    /**
     * 检查是否为强依赖模式
     * 
     * @return true如果是强依赖模式
     */
    protected boolean isStrongDependency() {
        return getDependencyMode() == SprivalHealthDependencyMode.STRONG;
    }
    
    /**
     * 检查是否为弱依赖模式
     * 
     * @return true如果是弱依赖模式
     */
    protected boolean isWeakDependency() {
        return getDependencyMode() == SprivalHealthDependencyMode.WEAK;
    }
    
    /**
     * 记录健康检查日志
     * 
     * @param level 日志级别
     * @param message 日志消息
     * @param args 参数
     */
    protected void logHealthCheck(String level, String message, Object... args) {
        String fullMessage = String.format("[%s健康检查] %s", getComponentName(), message);
        
        switch (level.toUpperCase()) {
            case "ERROR":
                log.error(fullMessage, args);
                break;
            case "WARN":
                log.warn(fullMessage, args);
                break;
            case "INFO":
                log.info(fullMessage, args);
                break;
            case "DEBUG":
                log.debug(fullMessage, args);
                break;
            default:
                log.info(fullMessage, args);
        }
    }
}
