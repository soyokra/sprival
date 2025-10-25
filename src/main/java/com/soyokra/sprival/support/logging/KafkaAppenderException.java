package com.soyokra.sprival.support.logging;

/**
 * KafkaAppender 自定义异常类 提供细化的异常分类和处理机制
 * 
 * @author sprival
 * @since 2.0.0
 */
public class KafkaAppenderException extends RuntimeException {

    /**
     * 异常类型枚举
     */
    public enum ErrorType {
        CONNECTION_FAILED("连接失败"), SERIALIZATION_ERROR("序列化错误"), BATCH_PROCESSING_ERROR(
                "批处理错误"), CONFIGURATION_ERROR("配置错误"), RESOURCE_CLEANUP_ERROR(
                        "资源清理错误"), THREAD_MANAGEMENT_ERROR("线程管理错误");

        private final String description;

        ErrorType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    private final ErrorType errorType;
    private final long timestamp;

    public KafkaAppenderException(ErrorType errorType, String message) {
        super(message);
        this.errorType = errorType;
        this.timestamp = System.currentTimeMillis();
    }

    public KafkaAppenderException(ErrorType errorType, String message, Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
        this.timestamp = System.currentTimeMillis();
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("KafkaAppenderException{type=%s, message='%s', timestamp=%d}",
                errorType, getMessage(), timestamp);
    }
}
