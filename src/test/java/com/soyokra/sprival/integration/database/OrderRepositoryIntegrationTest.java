package com.soyokra.sprival.integration.database;

import com.soyokra.sprival.app.repository.db.shop.model.OrderTbl;
import com.soyokra.sprival.app.repository.db.shop.provider.OrderTblProvider;
import com.soyokra.sprival.base.BaseIntegrationTest;
import com.soyokra.sprival.fixture.OrderTblFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Order Repository集成测试
 * 
 * <p>测试订单数据访问层的数据库操作</p>
 * 
 * <p>注意：</p>
 * <ul>
 *   <li>此测试使用本地数据库</li>
 *   <li>测试数据会保留在数据库中（不回滚）</li>
 * </ul>
 * 
 * @author sprival-test-framework
 * @since 2025-11-03
 */
@DisplayName("Order Repository集成测试")
class OrderRepositoryIntegrationTest extends BaseIntegrationTest {
    
    @Autowired
    private OrderTblProvider orderTblProvider;
    
    /**
     * 测试保存订单
     */
    @Test
    @DisplayName("测试保存订单")
    void testSave() {
        // Given: 准备测试数据
        OrderTbl order = OrderTblFixture.create();
        
        logTest("准备保存订单: {}", order.getOrderId());
        
        // When: 保存订单
        boolean result = orderTblProvider.save(order);
        
        // Then: 验证保存成功
        assertThat(result).isTrue();
        assertThat(order.getOrderId()).isNotNull();
        
        logTest("订单保存成功: {}", order.getOrderId());
    }
    
    /**
     * 测试根据ID查询订单
     */
    @Test
    @DisplayName("测试根据ID查询订单")
    void testGetById() {
        // Given: 先保存一个订单
        OrderTbl order = OrderTblFixture.create();
        orderTblProvider.save(order);
        
        logTest("订单已保存: {}", order.getOrderId());
        
        // When: 根据ID查询
        OrderTbl foundOrder = orderTblProvider.getById(order.getOrderId());
        
        // Then: 验证查询结果
        assertThat(foundOrder).isNotNull();
        assertThat(foundOrder.getOrderId()).isEqualTo(order.getOrderId());
        assertThat(foundOrder.getUserId()).isEqualTo(order.getUserId());
        
        logTest("订单查询成功: {}", foundOrder.getOrderId());
    }
}

