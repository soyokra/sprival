package com.soyokra.sprival.app.repository.db.order.service.impl;

import com.soyokra.sprival.app.repository.db.order.entity.Order;
import com.soyokra.sprival.app.repository.db.order.mapper.OrderMapper;
import com.soyokra.sprival.app.repository.db.order.service.IOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author soyokra
 * @since 2025-10-14
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

}
