package com.soyokra.sprival.app.http.request;

import lombok.Data;

/**
 * 性能测试-订单查询请求
 *
 * @author soyokra
 * @since 2025-11-03
 */
@Data
public class TestOrderQueryRequest {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 订单状态
     */
    private Integer status;

    /**
     * 开始时间（格式：yyyy-MM-dd HH:mm:ss）
     */
    private String startTime;

    /**
     * 结束时间（格式：yyyy-MM-dd HH:mm:ss）
     */
    private String endTime;

    /**
     * 页码
     */
    private Integer pageNo = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 20;
}

