package com.soyokra.sprival.app.client;

import com.soyokra.sprival.app.controller.response.FeignResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Sprival Feign 客户端
 *
 * @author sprival
 */
@FeignClient(
        name = "sprival-service",
        url = "http://127.0.0.1:8338",
        fallback = SprivalClientFallback.class,
        configuration = SprivalClientConfiguration.class
)
public interface SprivalClient {

    /**
     * 测试熔断器
     *
     * @param shouldFail 是否失败
     * @param delay 延迟时间（毫秒）
     * @return 响应结果
     */
    @GetMapping("/api/feign/self-call/circuit-breaker")
    FeignResponse testCircuitBreaker(
            @RequestParam(value = "shouldFail", defaultValue = "false") Boolean shouldFail,
            @RequestParam(value = "delay", defaultValue = "0") Long delay
    );

    /**
     * 测试重试
     *
     * @param shouldFail 是否失败
     * @param failTimes 失败次数
     * @return 响应结果
     */
    @GetMapping("/api/feign/self-call/retry")
    FeignResponse testRetry(
            @RequestParam(value = "shouldFail", defaultValue = "false") Boolean shouldFail,
            @RequestParam(value = "failTimes", defaultValue = "2") Integer failTimes
    );

    /**
     * 测试超时
     *
     * @param delay 延迟时间（毫秒）
     * @return 响应结果
     */
    @GetMapping("/api/feign/self-call/timeout")
    FeignResponse testTimeout(@RequestParam(value = "delay", defaultValue = "0") Long delay);
}
