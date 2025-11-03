package com.soyokra.sprival.app.http.response;

import com.soyokra.sprival.app.repository.db.test.model.TestOrder;
import com.soyokra.sprival.app.repository.db.test.model.TestOrderDetail;
import lombok.Data;

import java.util.List;

/**
 * 性能测试-订单及明细响应
 *
 * @author soyokra
 * @since 2025-11-03
 */
@Data
public class TestOrderWithDetailResponse {

    /**
     * 订单信息
     */
    private TestOrder order;

    /**
     * 订单明细列表
     */
    private List<TestOrderDetail> details;

    /**
     * 明细数量
     */
    private Integer detailCount;
}

