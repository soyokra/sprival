package com.soyokra.sprival.app.service;

import com.soyokra.sprival.app.client.SprivalClient;
import com.soyokra.sprival.app.controller.response.FeignResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Feign 服务类
 *
 * @author sprival
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FeignService {

    private final SprivalClient sprivalClient;

    /**
     * 测试熔断器
     *
     * @param shouldFail 是否失败
     * @param delay 延迟时间（毫秒）
     * @return 响应结果
     */
    public FeignResponse testCircuitBreaker(Boolean shouldFail, Long delay) {
        log.info("调用熔断器测试接口 - shouldFail: {}, delay: {}", shouldFail, delay);
        try {
            FeignResponse response = sprivalClient.testCircuitBreaker(shouldFail, delay);
            log.info("熔断器测试接口调用成功 - success: {}, fallback: {}", response.getSuccess(), response.getFallback());
            return response;
        } catch (Exception e) {
            log.error("熔断器测试接口调用失败", e);
            return FeignResponse.builder()
                    .success(false)
                    .message("调用失败: " + e.getMessage())
                    .fallback(true)
                    .timestamp(System.currentTimeMillis())
                    .build();
        }
    }

    /**
     * 测试重试
     *
     * @param shouldFail 是否失败
     * @param failTimes 失败次数
     * @return 响应结果
     */
    public FeignResponse testRetry(Boolean shouldFail, Integer failTimes) {
        log.info("调用重试测试接口 - shouldFail: {}, failTimes: {}", shouldFail, failTimes);
        try {
            FeignResponse response = sprivalClient.testRetry(shouldFail, failTimes);
            log.info("重试测试接口调用成功 - success: {}, fallback: {}, attemptCount: {}",
                    response.getSuccess(), response.getFallback(), response.getAttemptCount());
            return response;
        } catch (Exception e) {
            log.error("重试测试接口调用失败", e);
            return FeignResponse.builder()
                    .success(false)
                    .message("调用失败: " + e.getMessage())
                    .fallback(true)
                    .timestamp(System.currentTimeMillis())
                    .build();
        }
    }

    /**
     * 测试超时
     *
     * @param delay 延迟时间（毫秒）
     * @return 响应结果
     */
    public FeignResponse testTimeout(Long delay) {
        log.info("调用超时测试接口 - delay: {}", delay);
        try {
            FeignResponse response = sprivalClient.testTimeout(delay);
            log.info("超时测试接口调用成功 - success: {}, fallback: {}", response.getSuccess(), response.getFallback());
            return response;
        } catch (Exception e) {
            log.error("超时测试接口调用失败", e);
            return FeignResponse.builder()
                    .success(false)
                    .message("调用失败: " + e.getMessage())
                    .fallback(true)
                    .timestamp(System.currentTimeMillis())
                    .build();
        }
    }

    /**
     * 组合测试
     *
     * @param shouldFail 是否失败
     * @return 响应结果
     */
    public FeignResponse testCombined(Boolean shouldFail) {
        log.info("调用组合测试接口 - shouldFail: {}", shouldFail);
        try {
            // 先测试重试，重试失败后触发熔断器
            FeignResponse response = sprivalClient.testRetry(shouldFail, 5);
            log.info("组合测试接口调用成功 - success: {}, fallback: {}", response.getSuccess(), response.getFallback());
            return response;
        } catch (Exception e) {
            log.error("组合测试接口调用失败", e);
            return FeignResponse.builder()
                    .success(false)
                    .message("调用失败: " + e.getMessage())
                    .fallback(true)
                    .timestamp(System.currentTimeMillis())
                    .build();
        }
    }
}
