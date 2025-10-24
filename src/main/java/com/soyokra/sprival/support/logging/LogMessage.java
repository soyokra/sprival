package com.soyokra.sprival.support.logging;

import java.util.Map;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 日志消息对象 用于封装发送到Kafka的日志信息
 * 
 * @author Sprival Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String throwable;

    /**
     * MDC上下文信息
     */
    private Map<String, String> mdc;

    /**
     * 自定义字段
     */
    private Map<String, Object> customFields;
}

