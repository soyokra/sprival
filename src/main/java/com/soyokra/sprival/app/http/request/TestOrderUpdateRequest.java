package com.soyokra.sprival.app.http.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 性能测试-订单更新请求
 *
 * @author soyokra
 * @since 2025-11-03
 */
@Data
public class TestOrderUpdateRequest {

    /**
     * 订单ID
     */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    /**
     * 订单状态
     */
    private Integer status;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 备注
     */
    private String remark;
}

