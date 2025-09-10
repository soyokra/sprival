package com.soyokra.sprival.support.health;

/**
 * 中间件健康检查依赖模式枚举
 * 
 * @author Sprival Team
 * @version 1.0
 */
public enum SprivalHealthDependencyMode {
    
    /**
     * 强依赖模式
     * 中间件不可用时，应用健康检查状态为DOWN
     */
    STRONG("strong", "强依赖"),
    
    /**
     * 弱依赖模式  
     * 中间件不可用时，应用健康检查状态仍为UP，但记录告警日志
     */
    WEAK("weak", "弱依赖");
    
    private final String code;
    private final String description;
    
    SprivalHealthDependencyMode(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据代码获取枚举值
     */
    public static SprivalHealthDependencyMode fromCode(String code) {
        for (SprivalHealthDependencyMode mode : values()) {
            if (mode.code.equals(code)) {
                return mode;
            }
        }
        throw new IllegalArgumentException("Unknown dependency mode code: " + code);
    }
}
