package com.soyokra.sprival.app.http.middleware.ratelimiter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.google.common.util.concurrent.RateLimiter;

/**
 * 限流器配置类
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Configuration
public class SprivalRateLimiterConfiguration {

    /**
     * 全局限流器 - 每秒100个请求
     */
    @Bean("globalRateLimiter")
    public RateLimiter globalRateLimiter() {
        return RateLimiter.create(100.0);
    }

    /**
     * API限流器 - 每秒50个请求
     */
    @Bean("apiRateLimiter")
    public RateLimiter apiRateLimiter() {
        return RateLimiter.create(50.0);
    }

    /**
     * 用户操作限流器 - 每秒10个请求
     */
    @Bean("userActionRateLimiter")
    public RateLimiter userActionRateLimiter() {
        return RateLimiter.create(10.0);
    }

    /**
     * 登录限流器 - 每秒5个请求
     */
    @Bean("loginRateLimiter")
    public RateLimiter loginRateLimiter() {
        return RateLimiter.create(5.0);
    }
}
