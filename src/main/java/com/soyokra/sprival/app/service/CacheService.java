package com.soyokra.sprival.app.service;

import com.soyokra.sprival.app.controller.request.CacheEvictAllRequest;
import com.soyokra.sprival.app.controller.request.CacheEvictRequest;
import com.soyokra.sprival.app.controller.request.CacheWarmUpRequest;
import com.soyokra.sprival.app.controller.request.CacheWriteRequest;
import com.soyokra.sprival.app.controller.request.RedisCommandRequest;
import com.soyokra.sprival.app.controller.response.CacheHitRateResponse;
import com.soyokra.sprival.app.controller.response.CacheReadResponse;
import com.soyokra.sprival.app.controller.response.RedisCommandResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 缓存服务类
 *
 * @author sprival
 */
@Slf4j
@Service
public class CacheService {

    @Resource
    private CacheManager cacheManager;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 从缓存读取数据
     *
     * @param key 缓存键
     * @param cacheName 缓存名称
     * @return 缓存读取响应
     */
    public CacheReadResponse readCache(String key, String cacheName) {
        log.info("读取缓存，key: {}, cacheName: {}", key, cacheName);
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            log.warn("缓存不存在，cacheName: {}", cacheName);
            CacheReadResponse response = new CacheReadResponse();
            response.setKey(key);
            response.setCacheName(cacheName);
            response.setHit(false);
            return response;
        }

        Cache.ValueWrapper wrapper = cache.get(key);
        CacheReadResponse response = new CacheReadResponse();
        response.setKey(key);
        response.setCacheName(cacheName);
        if (wrapper != null) {
            response.setValue(wrapper.get());
            response.setHit(true);
        } else {
            response.setHit(false);
        }
        return response;
    }

    /**
     * 向缓存写入数据
     *
     * @param request 写入请求
     * @return 是否成功
     */
    public boolean writeCache(CacheWriteRequest request) {
        log.info("写入缓存，key: {}, cacheName: {}", request.getKey(), request.getCacheName());
        Cache cache = cacheManager.getCache(request.getCacheName());
        if (cache == null) {
            log.warn("缓存不存在，cacheName: {}", request.getCacheName());
            return false;
        }

        if (request.getTtl() != null && request.getTtl() > 0) {
            // 如果指定了TTL，使用RedisTemplate直接操作
            redisTemplate.opsForValue().set(
                    request.getCacheName() + "::" + request.getKey(),
                    request.getValue(),
                    request.getTtl(),
                    TimeUnit.MILLISECONDS
            );
        } else {
            cache.put(request.getKey(), request.getValue());
        }
        return true;
    }

    /**
     * 清除指定缓存键
     *
     * @param request 清除请求
     * @return 是否成功
     */
    public boolean evictCache(CacheEvictRequest request) {
        log.info("清除缓存，key: {}, cacheName: {}", request.getKey(), request.getCacheName());
        Cache cache = cacheManager.getCache(request.getCacheName());
        if (cache == null) {
            log.warn("缓存不存在，cacheName: {}", request.getCacheName());
            return false;
        }
        cache.evict(request.getKey());
        return true;
    }

    /**
     * 清除指定缓存名称的所有缓存
     *
     * @param request 清除全部请求
     * @return 是否成功
     */
    public boolean evictAllCache(CacheEvictAllRequest request) {
        log.info("清除全部缓存，cacheName: {}", request.getCacheName());
        Cache cache = cacheManager.getCache(request.getCacheName());
        if (cache == null) {
            log.warn("缓存不存在，cacheName: {}", request.getCacheName());
            return false;
        }
        cache.clear();
        return true;
    }

    /**
     * 缓存预热
     *
     * @param request 预热请求
     * @return 预热结果
     */
    public CacheWarmUpResult warmUpCache(CacheWarmUpRequest request) {
        log.info("缓存预热，cacheName: {}, count: {}", request.getCacheName(), request.getCount());
        long startTime = System.currentTimeMillis();
        Cache cache = cacheManager.getCache(request.getCacheName());
        if (cache == null) {
            log.warn("缓存不存在，cacheName: {}", request.getCacheName());
            return new CacheWarmUpResult(0, 0L);
        }

        int successCount = 0;
        for (int i = 0; i < request.getCount(); i++) {
            String key = request.getKeyPrefix() + ":" + i;
            String value = "warm-up-value-" + i;
            try {
                cache.put(key, value);
                successCount++;
            } catch (Exception e) {
                log.error("预热缓存失败，key: {}", key, e);
            }
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        log.info("缓存预热完成，成功: {}, 耗时: {}ms", successCount, totalTime);
        return new CacheWarmUpResult(successCount, totalTime);
    }

    /**
     * 测试缓存命中率
     *
     * @param cacheName 缓存名称
     * @param testCount 测试次数
     * @return 命中率响应
     */
    public CacheHitRateResponse testHitRate(String cacheName, Integer testCount) {
        log.info("测试缓存命中率，cacheName: {}, testCount: {}", cacheName, testCount);
        long startTime = System.currentTimeMillis();
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            log.warn("缓存不存在，cacheName: {}", cacheName);
            CacheHitRateResponse response = new CacheHitRateResponse();
            response.setCacheName(cacheName);
            response.setTestCount(testCount);
            response.setHitCount(0);
            response.setMissCount(testCount);
            response.setHitRate(0.0);
            response.setTotalTime(0L);
            return response;
        }

        // 先写入测试数据
        String keyPrefix = "hit-rate-test";
        for (int i = 0; i < testCount; i++) {
            String key = keyPrefix + ":" + i;
            cache.put(key, "test-value-" + i);
        }

        // 再读取测试数据，计算命中率
        int hitCount = 0;
        int missCount = 0;
        for (int i = 0; i < testCount; i++) {
            String key = keyPrefix + ":" + i;
            Cache.ValueWrapper wrapper = cache.get(key);
            if (wrapper != null) {
                hitCount++;
            } else {
                missCount++;
            }
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        double hitRate = (double) hitCount / testCount * 100;

        CacheHitRateResponse response = new CacheHitRateResponse();
        response.setCacheName(cacheName);
        response.setTestCount(testCount);
        response.setHitCount(hitCount);
        response.setMissCount(missCount);
        response.setHitRate(hitRate);
        response.setTotalTime(totalTime);
        log.info("缓存命中率测试完成，命中: {}, 未命中: {}, 命中率: {}%", hitCount, missCount, hitRate);
        return response;
    }

    /**
     * 执行Redis命令测试
     *
     * @param request Redis命令请求
     * @return Redis命令响应
     */
    public RedisCommandResponse executeRedisCommand(RedisCommandRequest request) {
        log.info("执行Redis命令，command: {}, key: {}, count: {}", request.getCommand(), request.getKey(), request.getCount());
        long startTime = System.currentTimeMillis();
        String command = request.getCommand().toUpperCase();
        Object result = null;

        try {
            for (int i = 0; i < request.getCount(); i++) {
                String key = request.getKey() + ":" + i;
                switch (command) {
                    case "GET":
                        result = redisTemplate.opsForValue().get(key);
                        break;
                    case "SET":
                        redisTemplate.opsForValue().set(key, request.getValue() != null ? request.getValue() : "test-value-" + i);
                        result = "OK";
                        break;
                    case "HGET":
                        result = redisTemplate.opsForHash().get(key, "field");
                        break;
                    case "HSET":
                        redisTemplate.opsForHash().put(key, "field", request.getValue() != null ? request.getValue() : "test-value-" + i);
                        result = 1L;
                        break;
                    case "ZADD":
                        redisTemplate.opsForZSet().add(key, request.getValue() != null ? request.getValue() : "member-" + i, i);
                        result = 1L;
                        break;
                    case "DEL":
                        redisTemplate.delete(key);
                        result = 1L;
                        break;
                    default:
                        log.warn("不支持的Redis命令: {}", command);
                        result = "UNSUPPORTED_COMMAND";
                }
            }
        } catch (Exception e) {
            log.error("执行Redis命令失败", e);
            result = "ERROR: " + e.getMessage();
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        double avgTime = (double) totalTime / request.getCount();
        double qps = request.getCount() * 1000.0 / totalTime;

        RedisCommandResponse response = new RedisCommandResponse();
        response.setCommand(command);
        response.setCount(request.getCount());
        response.setTotalTime(totalTime);
        response.setAvgTime(avgTime);
        response.setQps(qps);
        response.setResult(result);
        log.info("Redis命令执行完成，总耗时: {}ms, 平均耗时: {}ms, QPS: {}", totalTime, avgTime, qps);
        return response;
    }

    /**
     * 缓存预热结果
     */
    public static class CacheWarmUpResult {
        private final int successCount;
        private final long totalTime;

        public CacheWarmUpResult(int successCount, long totalTime) {
            this.successCount = successCount;
            this.totalTime = totalTime;
        }

        public int getSuccessCount() {
            return successCount;
        }

        public long getTotalTime() {
            return totalTime;
        }
    }
}
