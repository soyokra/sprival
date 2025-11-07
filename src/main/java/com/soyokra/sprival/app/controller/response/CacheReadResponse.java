package com.soyokra.sprival.app.controller.response;

import lombok.Data;

/**
 * 缓存读取响应DTO
 *
 * @author sprival
 */
@Data
public class CacheReadResponse {

    /**
     * 缓存键
     */
    private String key;

    /**
     * 缓存值
     */
    private Object value;

    /**
     * 是否命中缓存
     */
    private Boolean hit;

    /**
     * 缓存名称
     */
    private String cacheName;
}

