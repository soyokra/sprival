package com.soyokra.sprival.dao.sprival.provider;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.soyokra.sprival.dao.sprival.model.Trade;
import com.soyokra.sprival.dao.sprival.mapper.TradeMapper;
import com.soyokra.sprival.dao.sprival.contract.TradeContract;
import com.soyokra.sprival.dao.sprival.BaseTblProvider;
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
    public List<Trade> getTrades() {
        QueryWrapper<Trade> queryWrapper = new QueryWrapper<Trade>();
        queryWrapper.lambda().eq(Trade::getTradeId, 1);
        return list(queryWrapper);
    }
}
