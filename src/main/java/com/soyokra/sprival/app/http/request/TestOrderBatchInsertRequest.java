package com.soyokra.sprival.app.http.request;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 性能测试-订单批量插入请求（用于预填充数据）
 *
 * @author soyokra
 * @since 2025-11-03
 */
@Data
public class TestOrderBatchInsertRequest {

    /**
     * 批次大小（每批插入的记录数）
     */
    @NotNull(message = "批次大小不能为空")
    @Min(value = 1, message = "批次大小至少为1")
    @Max(value = 1000, message = "批次大小最大为1000")
    private Integer batchSize;

    /**
     * 批次数量（总共插入多少批）
     */
    @NotNull(message = "批次数量不能为空")
    @Min(value = 1, message = "批次数量至少为1")
    @Max(value = 1000, message = "批次数量最大为1000")
    private Integer batchCount;

    /**
     * 用户ID起始值
     */
    private Long startUserId;
}

