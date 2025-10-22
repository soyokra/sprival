package com.soyokra.sprival.support.logging;

/**
 * 日志输出目标枚举
 * 
 * @author Sprival Team
 * @since 1.0.0
 */
public enum LogOutputTarget {
    /**
     * 仅输出到本地文件
     */
    FILE("file"),
    
    /**
     * 仅输出到Kafka
     */
    KAFKA("kafka"),
    
    /**
     * 同时输出到本地文件和Kafka
     */
    BOTH("both");
    
    private final String value;
    
    LogOutputTarget(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    /**
     * 是否需要输出到文件
     */
    public boolean isFileEnabled() {
        return this == FILE || this == BOTH;
    }
    
    /**
     * 是否需要输出到Kafka
     */
    public boolean isKafkaEnabled() {
        return this == KAFKA || this == BOTH;
    }
    
    /**
     * 根据字符串值获取枚举
     */
    public static LogOutputTarget fromValue(String value) {
        for (LogOutputTarget target : values()) {
            if (target.value.equalsIgnoreCase(value)) {
                return target;
            }
        }
        return FILE; // 默认输出到文件
    }
}

