package com.soyokra.sprival.dao.sprival.provider;

import com.soyokra.sprival.dao.sprival.model.Trade;
import com.soyokra.sprival.dao.sprival.mapper.TradeMapper;
import com.soyokra.sprival.dao.sprival.contract.TradeContract;
import com.soyokra.sprival.dao.sprival.BaseTblProvider;
import org.springframework.stereotype.Service;

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

}
