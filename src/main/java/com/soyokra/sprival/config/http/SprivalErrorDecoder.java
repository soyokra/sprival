package com.soyokra.sprival.config.http;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

/**
 * 自定义Feign错误解码器 处理HTTP响应错误，提供更友好的错误信息
 * 
 * @author Sprival Team
 * @version 1.0
 */
public class SprivalErrorDecoder implements ErrorDecoder {

    private static final Logger logger = LoggerFactory.getLogger(SprivalErrorDecoder.class);

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus status = HttpStatus.valueOf(response.status());

        logger.warn("HTTP请求失败: method={}, status={}, reason={}", methodKey, status.value(),
                status.getReasonPhrase());

        switch (status) {
            case BAD_REQUEST:
                return new SprivalHttpClientException("请求参数错误", status.value());
            case UNAUTHORIZED:
                return new SprivalHttpClientException("未授权访问", status.value());
            case FORBIDDEN:
                return new SprivalHttpClientException("禁止访问", status.value());
            case NOT_FOUND:
                return new SprivalHttpClientException("资源不存在", status.value());
            case INTERNAL_SERVER_ERROR:
                return new SprivalHttpClientException("服务器内部错误", status.value());
            case BAD_GATEWAY:
                return new SprivalHttpClientException("网关错误", status.value());
            case SERVICE_UNAVAILABLE:
                return new SprivalHttpClientException("服务不可用", status.value());
            case GATEWAY_TIMEOUT:
                return new SprivalHttpClientException("网关超时", status.value());
            default:
                return defaultErrorDecoder.decode(methodKey, response);
        }
    }

    /**
     * 自定义HTTP客户端异常
     */
    public static class SprivalHttpClientException extends RuntimeException {
        private final int statusCode;

        public SprivalHttpClientException(String message, int statusCode) {
            super(message);
            this.statusCode = statusCode;
        }

        public int getStatusCode() {
            return statusCode;
        }
    }
}
