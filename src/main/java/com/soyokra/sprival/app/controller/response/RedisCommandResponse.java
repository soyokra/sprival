package com.soyokra.sprival.app.controller.response;

import lombok.Data;

/**
 * Redis命令响应DTO
 *
 * @author sprival
 */
@Data
public class RedisCommandResponse {

    /**
     * 命令类型
     */
    private String command;

    /**
     * 执行次数
     */
    private Integer count;

    /**
     * 总耗时（毫秒）
     */
    private Long totalTime;

    /**
     * 平均耗时（毫秒）
     */
    private Double avgTime;

    /**
     * QPS（每秒查询数）
     */
    private Double qps;

    /**
     * 执行结果（最后一个命令的结果）
     */
    private Object result;
}

