package com.soyokra.sprival.app.http.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 性能测试-订单统计响应
 *
 * @author soyokra
 * @since 2025-11-03
 */
@Data
@Builder
public class TestOrderStatisticsResponse {

    /**
     * 总订单数
     */
    private Long totalOrders;

    /**
     * 总金额
     */
    private BigDecimal totalAmount;

    /**
     * 平均订单金额
     */
    private BigDecimal avgOrderAmount;

    /**
     * 待支付订单数
     */
    private Long pendingOrders;

    /**
     * 已完成订单数
     */
    private Long completedOrders;
}

