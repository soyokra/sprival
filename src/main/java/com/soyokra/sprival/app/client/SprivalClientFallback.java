package com.soyokra.sprival.app.client;

import com.soyokra.sprival.app.controller.response.FeignResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * SprivalClient 降级处理类
 *
 * @author sprival
 */
@Slf4j
@Component
public class SprivalClientFallback implements SprivalClient {

    @Override
    public FeignResponse testCircuitBreaker(Boolean shouldFail, Long delay) {
        log.warn("熔断器降级 - shouldFail: {}, delay: {}", shouldFail, delay);
        return FeignResponse.builder()
                .success(false)
                .message("熔断器降级：服务不可用")
                .fallback(true)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    @Override
    public FeignResponse testRetry(Boolean shouldFail, Integer failTimes) {
        log.warn("重试降级 - shouldFail: {}, failTimes: {}", shouldFail, failTimes);
        return FeignResponse.builder()
                .success(false)
                .message("重试降级：重试失败后降级")
                .fallback(true)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    @Override
    public FeignResponse testTimeout(Long delay) {
        log.warn("超时降级 - delay: {}", delay);
        return FeignResponse.builder()
                .success(false)
                .message("超时降级：请求超时")
                .fallback(true)
                .timestamp(System.currentTimeMillis())
                .build();
    }
}
