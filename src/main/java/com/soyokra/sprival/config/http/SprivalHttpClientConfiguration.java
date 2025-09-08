package com.soyokra.sprival.config.http;

import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import feign.Logger;
import feign.Request;
import feign.codec.ErrorDecoder;

/**
 * HTTP客户端配置类 配置Feign、OkHttp、Resilience4j等组件
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Configuration
@Import(FeignClientsConfiguration.class)
public class SprivalHttpClientConfiguration {

    /**
     * 配置Feign日志级别
     */
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }

    /**
     * 配置Feign请求选项
     */
    @Bean
    public Request.Options feignRequestOptions() {
        return new Request.Options(5000, // 连接超时时间(毫秒)
                10000 // 读取超时时间(毫秒)
        );
    }

    /**
     * 配置错误解码器
     */
    @Bean
    public ErrorDecoder errorDecoder() {
        return new SprivalErrorDecoder();
    }

    /**
     * 配置请求拦截器
     */
    @Bean
    public feign.RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            // 添加自定义请求头
            requestTemplate.header("X-Client-Version", "1.0");
            requestTemplate.header("X-Request-Source", "sprival");
        };
    }
}
