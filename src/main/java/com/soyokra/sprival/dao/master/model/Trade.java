package com.soyokra.sprival.dao.master.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

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
public class Trade implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 交易ID
     */
    private String tradeId;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 修改时间
     */
    private LocalDateTime updatedAt;


}
