package com.soyokra.sprival.app.repository.db.shop.provider;

import com.soyokra.sprival.app.repository.db.shop.model.OrderTbl;
import com.soyokra.sprival.app.repository.db.shop.mapper.OrderTblMapper;
import com.soyokra.sprival.app.repository.db.shop.contract.OrderTblContract;
import com.soyokra.sprival.app.repository.db.shop.ShopBaseProvider;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author soyokra
 * @since 2025-10-15
 */
@Service
public class OrderTblProvider extends ShopBaseProvider<OrderTblMapper, OrderTbl> implements OrderTblContract {

}
