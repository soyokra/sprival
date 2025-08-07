package com.soyokra.sprival.dao.ck.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author zhengchenping
 * @since 2025-02-19
 */
@Getter
@Setter
@TableName("test_tbl")
public class Test implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    private String orderId;

    /**
     * 订单类型
     */
    private String orderType;

    /**
     * 业务类型
     */
    private Integer businessStatus;

    /**
     * 创建时间
     */
    private Date createTime;

}
