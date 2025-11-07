package com.soyokra.sprival.app.controller;

import com.soyokra.sprival.app.controller.request.CacheEvictAllRequest;
import com.soyokra.sprival.app.controller.request.CacheEvictRequest;
import com.soyokra.sprival.app.controller.request.CacheWarmUpRequest;
import com.soyokra.sprival.app.controller.request.CacheWriteRequest;
import com.soyokra.sprival.app.controller.request.RedisCommandRequest;
import com.soyokra.sprival.app.controller.response.CacheHitRateResponse;
import com.soyokra.sprival.app.controller.response.CacheReadResponse;
import com.soyokra.sprival.app.controller.response.RedisCommandResponse;
import com.soyokra.sprival.app.service.CacheService;
import com.soyokra.sprival.app.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 缓存测试控制器
 *
 * @author sprival
 */
@Slf4j
@RequestMapping(value = "/cache")
@RestController
public class CacheController {

    @Resource
    private CacheService cacheService;

    /**
     * 缓存读取测试
     *
     * @param key 缓存键
     * @param cacheName 缓存名称，默认post
     * @return 响应结果
     */
    @GetMapping("/read")
    public ResponseUtil<CacheReadResponse> readCache(
            @RequestParam String key,
            @RequestParam(required = false, defaultValue = "post") String cacheName) {
        CacheReadResponse response = cacheService.readCache(key, cacheName);
        return ResponseUtil.success(response);
    }

    /**
     * 缓存写入测试
     *
     * @param request 写入请求
     * @return 响应结果
     */
    @PostMapping("/write")
    public ResponseUtil<Boolean> writeCache(@Validated @RequestBody CacheWriteRequest request) {
        boolean result = cacheService.writeCache(request);
        if (!result) {
            return ResponseUtil.error(500, "缓存写入失败");
        }
        return ResponseUtil.success(true);
    }

    /**
     * 缓存清除（单个）
     *
     * @param request 清除请求
     * @return 响应结果
     */
    @PostMapping("/evict")
    public ResponseUtil<Boolean> evictCache(@Validated @RequestBody CacheEvictRequest request) {
        boolean result = cacheService.evictCache(request);
        if (!result) {
            return ResponseUtil.error(500, "缓存清除失败");
        }
        return ResponseUtil.success(true);
    }

    /**
     * 缓存清除（全部）
     *
     * @param request 清除全部请求
     * @return 响应结果
     */
    @PostMapping("/evict-all")
    public ResponseUtil<Boolean> evictAllCache(@Validated @RequestBody CacheEvictAllRequest request) {
        boolean result = cacheService.evictAllCache(request);
        if (!result) {
            return ResponseUtil.error(500, "缓存清除失败");
        }
        return ResponseUtil.success(true);
    }

    /**
     * 缓存预热
     *
     * @param request 预热请求
     * @return 响应结果
     */
    @PostMapping("/warm-up")
    public ResponseUtil<CacheService.CacheWarmUpResult> warmUpCache(@Validated @RequestBody CacheWarmUpRequest request) {
        CacheService.CacheWarmUpResult result = cacheService.warmUpCache(request);
        return ResponseUtil.success(result);
    }

    /**
     * 缓存命中率测试
     *
     * @param cacheName 缓存名称，默认post
     * @param testCount 测试次数，默认1000
     * @return 响应结果
     */
    @GetMapping("/hit-rate")
    public ResponseUtil<CacheHitRateResponse> testHitRate(
            @RequestParam(required = false, defaultValue = "post") String cacheName,
            @RequestParam(required = false, defaultValue = "1000") Integer testCount) {
        CacheHitRateResponse response = cacheService.testHitRate(cacheName, testCount);
        return ResponseUtil.success(response);
    }

    /**
     * Redis直接操作测试
     *
     * @param request Redis命令请求
     * @return 响应结果
     */
    @PostMapping("/redis-command")
    public ResponseUtil<RedisCommandResponse> executeRedisCommand(@Validated @RequestBody RedisCommandRequest request) {
        RedisCommandResponse response = cacheService.executeRedisCommand(request);
        return ResponseUtil.success(response);
    }
}
