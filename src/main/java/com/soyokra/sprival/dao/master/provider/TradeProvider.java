package com.soyokra.sprival.dao.master.provider;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.soyokra.sprival.dao.master.contract.TradeContract;
import com.soyokra.sprival.dao.master.mapper.TradeMapper;
import com.soyokra.sprival.dao.master.model.Trade;
import com.soyokra.sprival.dao.master.BaseTblProvider;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhengchenping
 * @since 2025-02-19
 */
@Service
public class TradeProvider extends BaseTblProvider<TradeMapper, Trade> implements TradeContract {
    public List<Trade> getTrades(String tradeId) {
        QueryWrapper<Trade> queryWrapper = new QueryWrapper<Trade>();
        queryWrapper.lambda().eq(Trade::getTradeId, tradeId);
        return list(queryWrapper);
    }
}
