package com.soyokra.sprival.app.repository.db.shop.model;

import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 订单表
 * </p>
 *
 * @author soyokra
 * @since 2025-10-15
 */
@Getter
@Setter
public class OrderTbl implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单id
     */
    @TableId("order_id")
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

    /**
     * 订单开始时间
     */
    private LocalDateTime startTime;

    /**
     * 订单结束时间
     */
    private LocalDateTime endTime;

    /**
     * 添加时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;
}
