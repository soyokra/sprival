package com.soyokra.sprival.support.logging;

import java.util.Map;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Jetty访问日志消息对象 用于封装发送到Kafka的Jetty访问日志信息
 * 
 * @author Sprival Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JettyAccessLogMessage {

    /**
     * 时间戳
     */
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private long timestamp;

    /**
     * 日志类型
     */
    @Builder.Default
    private String logType = "jetty-access";

    /**
     * 客户端IP地址
     */
    private String clientIp;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 请求URI
     */
    private String uri;

    /**
     * 请求协议
     */
    private String protocol;

    /**
     * 响应状态码
     */
    private int statusCode;

    /**
     * 响应字节数
     */
    private long responseBytes;

    /**
     * 请求处理时间（毫秒）
     */
    private long processingTime;

    /**
     * User-Agent
     */
    private String userAgent;

    /**
     * Referer
     */
    private String referer;

    /**
     * 自定义字段
     */
    private Map<String, Object> customFields;
}

