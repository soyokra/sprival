package com.soyokra.sprival.fixture;

import com.soyokra.sprival.app.repository.db.shop.model.OrderTbl;

import java.time.LocalDateTime;
import java.util.function.Consumer;

/**
 * Order测试数据构造器
 * 
 * <p>提供订单实体的测试数据构造方法</p>
 * 
 * <p>使用示例：</p>
 * <pre>{@code
 * // 创建默认订单
 * OrderTbl order = OrderTblFixture.create();
 * 
 * // 创建自定义订单
 * OrderTbl order = OrderTblFixture.create(o -> {
 *     o.setUserId("USER001");
 *     o.setStatusNo(2);
 * });
 * 
 * // 创建指定用户的订单
 * OrderTbl order = OrderTblFixture.createForUser("USER001");
 * 
 * // 创建指定状态的订单
 * OrderTbl order = OrderTblFixture.createWithStatus(2);
 * }</pre>
 * 
 * @author sprival-test-framework
 * @since 2025-11-03
 */
public final class OrderTblFixture {
    
    private OrderTblFixture() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }
    
    /**
     * 创建订单（使用默认值）
     * 
     * @return 订单对象
     */
    public static OrderTbl create() {
        return create(null);
    }
    
    /**
     * 创建订单（支持自定义）
     * 
     * @param customizer 自定义配置函数
     * @return 订单对象
     */
    public static OrderTbl create(Consumer<OrderTbl> customizer) {
        OrderTbl order = new OrderTbl();
        
        // 设置默认值
        LocalDateTime now = TestDataBuilder.now();
        order.setOrderId(TestDataBuilder.generateId("ORDER"));
        order.setTradeId(TestDataBuilder.generateId("TRADE"));
        order.setParentOrderId(null);
        order.setOrderType("NORMAL");
        order.setUserId(TestDataBuilder.generateId("USER"));
        order.setPartnerId(TestDataBuilder.generateId("PARTNER"));
        order.setSupplierId(TestDataBuilder.generateId("SUPPLIER"));
        order.setIdempotentId(TestDataBuilder.generateUUID("IDEM"));
        order.setStatusNo(1);
        order.setBusinessStatus(0);
        order.setStartTime(now);
        order.setEndTime(now.plusDays(1));
        order.setCreateTime(now);
        order.setUpdateTime(now);
        
        // 应用自定义配置
        if (customizer != null) {
            customizer.accept(order);
        }
        
        return order;
    }
    
    /**
     * 创建指定订单ID的订单
     * 
     * @param orderId 订单ID
     * @return 订单对象
     */
    public static OrderTbl createWithId(String orderId) {
        return create(order -> order.setOrderId(orderId));
    }
    
    /**
     * 创建指定用户的订单
     * 
     * @param userId 用户ID
     * @return 订单对象
     */
    public static OrderTbl createForUser(String userId) {
        return create(order -> order.setUserId(userId));
    }
    
    /**
     * 创建指定状态的订单
     * 
     * @param statusNo 订单状态
     * @return 订单对象
     */
    public static OrderTbl createWithStatus(Integer statusNo) {
        return create(order -> order.setStatusNo(statusNo));
    }
    
    /**
     * 创建已保存的订单（带完整ID）
     * 
     * @return 订单对象
     */
    public static OrderTbl createSaved() {
        return create(order -> {
            // 已保存的订单通常有固定格式的ID
            order.setOrderId("ORDER-" + TestDataBuilder.generateUUID());
        });
    }
}

