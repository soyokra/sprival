package com.soyokra.sprival.app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Resilience 计数器服务
 *
 * @author sprival
 */
@Slf4j
@Service
public class ResilienceCounterService {

    private final ConcurrentHashMap<String, AtomicInteger> counters = new ConcurrentHashMap<>();

    /**
     * 获取并增加计数器
     *
     * @param key 计数器键
     * @return 当前计数值
     */
    public int getAndIncrement(String key) {
        AtomicInteger counter = counters.computeIfAbsent(key, k -> new AtomicInteger(0));
        return counter.getAndIncrement();
    }

    /**
     * 获取当前计数器值
     *
     * @param key 计数器键
     * @return 当前计数值
     */
    public int get(String key) {
        AtomicInteger counter = counters.get(key);
        return counter == null ? 0 : counter.get();
    }

    /**
     * 重置计数器
     *
     * @param key 计数器键
     */
    public void reset(String key) {
        counters.remove(key);
        log.info("重置计数器: {}", key);
    }

    /**
     * 重置所有计数器
     */
    public void resetAll() {
        counters.clear();
        log.info("重置所有计数器");
    }
}
