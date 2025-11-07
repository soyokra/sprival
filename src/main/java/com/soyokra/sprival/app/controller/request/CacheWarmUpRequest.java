package com.soyokra.sprival.app.controller.request;

import lombok.Data;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 缓存预热请求DTO
 *
 * @author sprival
 */
@Data
public class CacheWarmUpRequest {

    /**
     * 缓存名称，默认post
     */
    private String cacheName = "post";

    /**
     * 预热数量
     */
    @NotNull(message = "预热数量不能为空")
    @Min(value = 1, message = "预热数量必须大于0")
    private Integer count;

    /**
     * 缓存键前缀，默认cache-test
     */
    private String keyPrefix = "cache-test";
}

