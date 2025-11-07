package com.soyokra.sprival.app.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Feign 测试响应
 *
 * @author sprival
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeignResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 是否降级
     */
    private Boolean fallback;

    /**
     * 尝试次数
     */
    private Integer attemptCount;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 延迟时间（毫秒）
     */
    private Long delay;

    /**
     * 失败次数
     */
    private Integer failTimes;
}
