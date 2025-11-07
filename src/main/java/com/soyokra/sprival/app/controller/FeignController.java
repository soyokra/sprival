package com.soyokra.sprival.app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.soyokra.sprival.app.controller.response.FeignResponse;
import com.soyokra.sprival.app.service.FeignService;
import com.soyokra.sprival.app.service.ResilienceCounterService;
import com.soyokra.sprival.app.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Feign 测试控制器
 *
 * @author sprival
 */
@Slf4j
@RestController
@RequestMapping("/feign")
@RequiredArgsConstructor
public class FeignController {

    private final FeignService feignService;
    private final ResilienceCounterService counterService;

    // ==================== 测试接口（供 api-test 调用）====================

    /**
     * 熔断器测试接口
     *
     * @param shouldFail 是否失败
     * @param delay 延迟时间（毫秒）
     * @return 响应结果
     */
    @GetMapping("/circuit-breaker")
    public ResponseUtil<FeignResponse> testCircuitBreaker(
            @RequestParam(value = "shouldFail", defaultValue = "false") Boolean shouldFail,
            @RequestParam(value = "delay", defaultValue = "0") Long delay) {
        FeignResponse response = feignService.testCircuitBreaker(shouldFail, delay);
        return ResponseUtil.success(response);
    }

    /**
     * 重试测试接口
     *
     * @param shouldFail 是否失败
     * @param failTimes 失败次数
     * @return 响应结果
     */
    @GetMapping("/retry")
    public ResponseUtil<FeignResponse> testRetry(
            @RequestParam(value = "shouldFail", defaultValue = "false") Boolean shouldFail,
            @RequestParam(value = "failTimes", defaultValue = "2") Integer failTimes) {
        FeignResponse response = feignService.testRetry(shouldFail, failTimes);
        return ResponseUtil.success(response);
    }

    /**
     * 超时测试接口
     *
     * @param delay 延迟时间（毫秒）
     * @return 响应结果
     */
    @GetMapping("/timeout")
    public ResponseUtil<FeignResponse> testTimeout(
            @RequestParam(value = "delay", defaultValue = "0") Long delay) {
        FeignResponse response = feignService.testTimeout(delay);
        return ResponseUtil.success(response);
    }

    /**
     * 组合测试接口
     *
     * @param shouldFail 是否失败
     * @return 响应结果
     */
    @GetMapping("/combined")
    public ResponseUtil<FeignResponse> testCombined(
            @RequestParam(value = "shouldFail", defaultValue = "true") Boolean shouldFail) {
        FeignResponse response = feignService.testCombined(shouldFail);
        return ResponseUtil.success(response);
    }

    // ==================== 自调用接口（供 SprivalClient 调用）====================

    /**
     * 熔断器测试服务端接口
     *
     * @param shouldFail 是否失败
     * @param delay 延迟时间（毫秒）
     * @return 响应结果
     */
    @GetMapping("/self-call/circuit-breaker")
    public FeignResponse selfCallCircuitBreaker(
            @RequestParam(value = "shouldFail", defaultValue = "false") Boolean shouldFail,
            @RequestParam(value = "delay", defaultValue = "0") Long delay) {
        log.info("自调用接口 - 熔断器测试 - shouldFail: {}, delay: {}", shouldFail, delay);

        // 模拟延迟
        if (delay > 0) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("延迟被中断", e);
            }
        }

        // 模拟失败
        if (shouldFail) {
            log.warn("自调用接口 - 模拟服务异常");
            throw new RuntimeException("熔断器测试：模拟服务异常");
        }

        return FeignResponse.builder().success(true).message("熔断器测试成功").fallback(false)
                .timestamp(System.currentTimeMillis()).delay(delay).build();
    }

    /**
     * 重试测试服务端接口
     *
     * @param shouldFail 是否失败
     * @param failTimes 失败次数
     * @return 响应结果
     */
    @GetMapping("/self-call/retry")
    public FeignResponse selfCallRetry(
            @RequestParam(value = "shouldFail", defaultValue = "false") Boolean shouldFail,
            @RequestParam(value = "failTimes", defaultValue = "2") Integer failTimes) {
        String counterKey = "retry-test";
        int currentCount = counterService.getAndIncrement(counterKey);
        log.info("自调用接口 - 重试测试 - shouldFail: {}, failTimes: {}, currentCount: {}", shouldFail,
                failTimes, currentCount);

        if (shouldFail && currentCount < failTimes) {
            log.warn("自调用接口 - 重试测试 - 第 {} 次尝试失败（共需失败 {} 次）", currentCount + 1, failTimes);
            throw new RuntimeException("重试测试：第 " + (currentCount + 1) + " 次尝试失败");
        }

        // 成功时重置计数器
        if (currentCount >= failTimes || !shouldFail) {
            counterService.reset(counterKey);
            log.info("自调用接口 - 重试测试 - 第 {} 次尝试成功，重置计数器", currentCount + 1);
        }

        return FeignResponse.builder().success(true).message("重试测试成功").fallback(false)
                .attemptCount(currentCount + 1).timestamp(System.currentTimeMillis())
                .failTimes(failTimes).build();
    }

    /**
     * 超时测试服务端接口
     *
     * @param delay 延迟时间（毫秒）
     * @return 响应结果
     */
    @GetMapping("/self-call/timeout")
    public FeignResponse selfCallTimeout(
            @RequestParam(value = "delay", defaultValue = "0") Long delay) {
        log.info("自调用接口 - 超时测试 - delay: {}", delay);

        // 模拟延迟（可能超过读取超时时间）
        if (delay > 0) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("延迟被中断", e);
            }
        }

        return FeignResponse.builder().success(true).message("超时测试成功").fallback(false)
                .timestamp(System.currentTimeMillis()).delay(delay).build();
    }

    /**
     * 重置计数器
     *
     * @return 响应结果
     */
    @GetMapping("/self-call/reset")
    public FeignResponse reset() {
        log.info("自调用接口 - 重置计数器");
        counterService.resetAll();
        return FeignResponse.builder().success(true).message("重置计数器成功").fallback(false)
                .timestamp(System.currentTimeMillis()).build();
    }
}
