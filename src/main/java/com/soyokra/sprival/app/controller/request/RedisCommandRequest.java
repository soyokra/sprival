package com.soyokra.sprival.app.controller.request;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Redis命令请求DTO
 *
 * @author sprival
 */
@Data
public class RedisCommandRequest {

    /**
     * Redis命令类型：GET、SET、HGET、HSET、ZADD等
     */
    @NotBlank(message = "命令类型不能为空")
    private String command;

    /**
     * 缓存键
     */
    @NotBlank(message = "缓存键不能为空")
    private String key;

    /**
     * 缓存值（SET、HSET等命令需要）
     */
    private Object value;

    /**
     * 执行次数，默认1000
     */
    @NotNull(message = "执行次数不能为空")
    @Min(value = 1, message = "执行次数必须大于0")
    private Integer count = 1000;
}

