package com.soyokra.sprival.dao.sprival.mapper;

import com.soyokra.sprival.dao.sprival.model.Trade;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zhengchenping
 * @since 2025-02-19
 */
public interface TradeMapper extends BaseMapper<Trade> {
    Trade selectOneTradeByTradeId(@Param("tradeId") String tradeId);
}
