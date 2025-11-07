package com.soyokra.sprival.app.controller.request;

import lombok.Data;
import javax.validation.constraints.NotBlank;

/**
 * 缓存清除请求DTO
 *
 * @author sprival
 */
@Data
public class CacheEvictRequest {

    /**
     * 缓存键
     */
    @NotBlank(message = "缓存键不能为空")
    private String key;

    /**
     * 缓存名称，默认post
     */
    private String cacheName = "post";
}

