package com.soyokra.sprival.app.controller.request;

import lombok.Data;

/**
 * 清除全部缓存请求DTO
 *
 * @author sprival
 */
@Data
public class CacheEvictAllRequest {

    /**
     * 缓存名称，默认post
     */
    private String cacheName = "post";
}

