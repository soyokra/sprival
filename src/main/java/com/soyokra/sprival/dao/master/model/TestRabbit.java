package com.soyokra.sprival.dao.master.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author zhengchenping
 * @since 2025-03-25
 */
@Getter
@Setter
@TableName("test_rabbit")
public class TestRabbit implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 测试ID
     */
    @TableId
    private String testId;

    /**
     * 发布计数
     */
    private Integer pCount;

    /**
     * 消费计数
     */
    private Integer cCount;


}
