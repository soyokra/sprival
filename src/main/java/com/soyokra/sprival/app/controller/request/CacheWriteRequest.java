package com.soyokra.sprival.app.controller.request;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 缓存写入请求DTO
 *
 * @author sprival
 */
@Data
public class CacheWriteRequest {

    /**
     * 缓存键
     */
    @NotBlank(message = "缓存键不能为空")
    private String key;

    /**
     * 缓存值
     */
    @NotNull(message = "缓存值不能为空")
    private Object value;

    /**
     * 缓存名称，默认post
     */
    private String cacheName = "post";

    /**
     * TTL（过期时间，毫秒），可选
     */
    private Long ttl;
}

