package com.soyokra.sprival.support.logging;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import ch.qos.logback.classic.spi.IThrowableProxy;

import java.util.Map;

/**
 * 日志消息对象
 * 用于封装发送到Kafka的日志信息
 * 
 * @author sprival
 * @since 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LogMessage {
    
    /**
     * 时间戳
     */
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private long timestamp;
    
    /**
     * 日志级别
     */
    private String level;
    
    /**
     * 日志记录器名称
     */
    private String loggerName;
    
    /**
     * 线程名称
     */
    private String threadName;
    
    /**
     * 日志消息
     */
    private String message;
    
    /**
     * 异常信息
     */
    private IThrowableProxy throwable;
    
    /**
     * MDC上下文信息
     */
    private Map<String, String> mdc;
    
    /**
     * 自定义字段
     */
    private Map<String, Object> customFields;
    
    // 构造函数
    public LogMessage() {
    }
    
    // Getter和Setter方法
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getLevel() {
        return level;
    }
    
    public void setLevel(String level) {
        this.level = level;
    }
    
    public String getLoggerName() {
        return loggerName;
    }
    
    public void setLoggerName(String loggerName) {
        this.loggerName = loggerName;
    }
    
    public String getThreadName() {
        return threadName;
    }
    
    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public IThrowableProxy getThrowable() {
        return throwable;
    }
    
    public void setThrowable(IThrowableProxy throwable) {
        this.throwable = throwable;
    }
    
    public Map<String, String> getMdc() {
        return mdc;
    }
    
    public void setMdc(Map<String, String> mdc) {
        this.mdc = mdc;
    }
    
    public Map<String, Object> getCustomFields() {
        return customFields;
    }
    
    public void setCustomFields(Map<String, Object> customFields) {
        this.customFields = customFields;
    }
    
    @Override
    public String toString() {
        return "LogMessage{" +
                "timestamp=" + timestamp +
                ", level='" + level + '\'' +
                ", loggerName='" + loggerName + '\'' +
                ", threadName='" + threadName + '\'' +
                ", message='" + message + '\'' +
                ", throwable=" + throwable +
                ", mdc=" + mdc +
                ", customFields=" + customFields +
                '}';
    }
}
