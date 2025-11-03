package com.soyokra.sprival.unit.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.soyokra.sprival.app.repository.db.shop.model.OrderTbl;
import com.soyokra.sprival.app.repository.db.shop.provider.OrderTblProvider;
import com.soyokra.sprival.app.service.OrderService;
import com.soyokra.sprival.base.BaseUnitTest;
import com.soyokra.sprival.fixture.OrderTblFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * OrderService单元测试
 * 
 * <p>测试OrderService的业务逻辑</p>
 * 
 * @author sprival-test-framework
 * @since 2025-11-03
 */
@DisplayName("OrderService单元测试")
class OrderServiceTest extends BaseUnitTest {
    
    @Mock
    private OrderTblProvider orderTblProvider;
    
    @InjectMocks
    private OrderService orderService;
    
    /**
     * 测试根据订单ID获取订单
     */
    @Test
    @DisplayName("测试根据订单ID获取订单")
    void testGetOrder() {
        // Given: 准备测试数据
        String orderId = "ORDER-TEST-001";
        OrderTbl expectedOrder = OrderTblFixture.createWithId(orderId);
        
        when(orderTblProvider.getOne(any(QueryWrapper.class)))
                .thenReturn(expectedOrder);
        
        // When: 执行被测试方法
        OrderTbl actualOrder = orderService.getOrder(orderId);
        
        // Then: 验证结果
        assertThat(actualOrder).isNotNull();
        assertThat(actualOrder.getOrderId()).isEqualTo(orderId);
        
        // 验证方法调用
        verify(orderTblProvider).getOne(any(QueryWrapper.class));
    }
    
    /**
     * 测试获取不存在的订单
     */
    @Test
    @DisplayName("测试获取不存在的订单")
    void testGetOrder_NotFound() {
        // Given: Mock返回null
        String orderId = "ORDER-NOT-EXIST";
        when(orderTblProvider.getOne(any(QueryWrapper.class)))
                .thenReturn(null);
        
        // When: 执行被测试方法
        OrderTbl actualOrder = orderService.getOrder(orderId);
        
        // Then: 验证结果为null
        assertThat(actualOrder).isNull();
    }
}

