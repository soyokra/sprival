package com.soyokra.sprival.app.controller.response;

import lombok.Data;

/**
 * 缓存命中率响应DTO
 *
 * @author sprival
 */
@Data
public class CacheHitRateResponse {

    /**
     * 测试次数
     */
    private Integer testCount;

    /**
     * 命中次数
     */
    private Integer hitCount;

    /**
     * 未命中次数
     */
    private Integer missCount;

    /**
     * 命中率（百分比）
     */
    private Double hitRate;

    /**
     * 缓存名称
     */
    private String cacheName;

    /**
     * 总耗时（毫秒）
     */
    private Long totalTime;
}

