package com.soyokra.sprival.app.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.soyokra.sprival.app.repository.db.shop.model.OrderTbl;
import com.soyokra.sprival.app.repository.db.shop.provider.OrderTblProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    @Autowired
    private OrderTblProvider orderTblProvider;

    @Cacheable(value = "order", key = "#orderId")
    public OrderTbl getOrder(String orderId) {
        QueryWrapper<OrderTbl> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(OrderTbl::getOrderId, orderId);
        return orderTblProvider.getOne(queryWrapper);
    }
}
