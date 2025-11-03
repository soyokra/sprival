package com.soyokra.sprival.app.http.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderInsertRequest {
    /**
     * 订单ID
     */
    private String orderId;

    /**
     * 合并支付id
     */
    private String tradeId;

    /**
     * 父订单id
     */
    private String parentOrderId;

    /**
     * 订单类型
     */
    private String orderType;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 分销商id
     */
    private String partnerId;

    /**
     * 供应商id
     */
    private String supplierId;

    /**
     * 幂等Id
     */
    private String idempotentId;

    /**
     * 订单状态
     */
    private Integer statusNo;

    /**
     * 业务订单状态
     */
    private Integer businessStatus;
}
