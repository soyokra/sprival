package com.soyokra.sprival.loadtest.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soyokra.sprival.app.http.request.OrderInsertRequest;
import com.soyokra.sprival.base.BaseLoadTest;
import com.soyokra.sprival.fixture.TestDataBuilder;
import com.soyokra.sprival.util.LoadTestConfig;
import com.soyokra.sprival.util.LoadTestResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Order Insert API 压力测试
 * 
 * <p>测试场景：</p>
 * <ul>
 *   <li>基准压力测试：10并发，60秒</li>
 *   <li>高并发测试：50并发，60秒</li>
 *   <li>持久性测试：10并发，300秒</li>
 * </ul>
 * 
 * <p>测试目标：</p>
 * <ul>
 *   <li>成功率 >= 99%</li>
 *   <li>平均响应时间 <= 500ms</li>
 *   <li>P99响应时间 <= 1000ms</li>
 * </ul>
 * 
 * @author sprival-test-framework
 * @since 2025-11-03
 */
@DisplayName("Order Insert API 压力测试")
public class OrderInsertLoadTest extends BaseLoadTest {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 创建订单请求对象
     * 
     * @return 订单请求的JSON字符串
     */
    private String createOrderRequest() {
        try {
            OrderInsertRequest request = new OrderInsertRequest();
            
            // 生成唯一ID，避免主键冲突
            request.setOrderId(TestDataBuilder.generateId("ORDER"));
            request.setTradeId(TestDataBuilder.generateId("TRADE"));
            request.setParentOrderId(null);
            request.setOrderType("NORMAL");
            request.setUserId(TestDataBuilder.generateId("USER"));
            request.setPartnerId(TestDataBuilder.generateId("PARTNER"));
            request.setSupplierId(TestDataBuilder.generateId("SUPPLIER"));
            request.setIdempotentId(TestDataBuilder.generateUUID("IDEM"));
            request.setStatusNo(1);
            request.setBusinessStatus(0);
            
            return objectMapper.writeValueAsString(request);
        } catch (Exception e) {
            throw new RuntimeException("创建订单请求失败", e);
        }
    }
    
    /**
     * 测试场景1：基准压力测试
     * 
     * <p>配置：10并发，60秒</p>
     * <p>预期：成功率 >= 99%，平均响应时间 <= 500ms</p>
     */
    @Test
    @DisplayName("基准压力测试（10并发，60秒）")
    void testInsert_BaselineLoad() {
        logTest("开始基准压力测试：10并发，60秒");
        
        // 配置压力测试
        LoadTestConfig config = LoadTestConfig.builder()
                .url(baseUrl + "/order/insert")
                .httpMethod("POST")
                .concurrentThreads(10)
                .durationSeconds(60)
                .warmupSeconds(10)
                .requestBodySupplier(this::createOrderRequest)
                .header("Content-Type", "application/json")
                .build();
        
        // 执行压力测试
        LoadTestResult result = executor.execute(config);
        
        // 打印测试报告
        result.printReport();
        
        // 验证测试结果
        assertThat(result.getSuccessRate())
                .as("成功率应大于等于99%")
                .isGreaterThanOrEqualTo(99.0);
        
        assertThat(result.getAvgResponseTimeMs())
                .as("平均响应时间应小于等于500ms")
                .isLessThanOrEqualTo(500.0);
        
        logTest("基准压力测试完成，TPS: {}, 成功率: {}%", 
                String.format("%.2f", result.getTps()), 
                String.format("%.2f", result.getSuccessRate()));
    }
    
    /**
     * 测试场景2：高并发测试
     * 
     * <p>配置：50并发，60秒</p>
     * <p>预期：成功率 >= 95%，平均响应时间 <= 1000ms</p>
     */
    @Test
    @DisplayName("高并发测试（50并发，60秒）")
    void testInsert_HighConcurrency() {
        logTest("开始高并发测试：50并发，60秒");
        
        // 配置压力测试
        LoadTestConfig config = LoadTestConfig.builder()
                .url(baseUrl + "/order/insert")
                .httpMethod("POST")
                .concurrentThreads(50)
                .durationSeconds(60)
                .warmupSeconds(10)
                .requestBodySupplier(this::createOrderRequest)
                .header("Content-Type", "application/json")
                .build();
        
        // 执行压力测试
        LoadTestResult result = executor.execute(config);
        
        // 打印测试报告
        result.printReport();
        
        // 验证测试结果（高并发下降低期望）
        assertThat(result.getSuccessRate())
                .as("成功率应大于等于95%")
                .isGreaterThanOrEqualTo(95.0);
        
        assertThat(result.getAvgResponseTimeMs())
                .as("平均响应时间应小于等于1000ms")
                .isLessThanOrEqualTo(1000.0);
        
        logTest("高并发测试完成，TPS: {}, 成功率: {}%", 
                String.format("%.2f", result.getTps()), 
                String.format("%.2f", result.getSuccessRate()));
    }
    
    /**
     * 测试场景3：持久性测试
     * 
     * <p>配置：10并发，300秒（5分钟）</p>
     * <p>预期：成功率 >= 99%，平均响应时间 <= 500ms，长时间稳定运行</p>
     */
    @Test
    @DisplayName("持久性测试（10并发，300秒）")
    void testInsert_Endurance() {
        logTest("开始持久性测试：10并发，300秒");
        
        // 配置压力测试
        LoadTestConfig config = LoadTestConfig.builder()
                .url(baseUrl + "/order/insert")
                .httpMethod("POST")
                .concurrentThreads(10)
                .durationSeconds(300)  // 5分钟
                .warmupSeconds(10)
                .requestBodySupplier(this::createOrderRequest)
                .header("Content-Type", "application/json")
                .build();
        
        // 执行压力测试
        LoadTestResult result = executor.execute(config);
        
        // 打印测试报告
        result.printReport();
        
        // 验证测试结果
        assertThat(result.getSuccessRate())
                .as("成功率应大于等于99%")
                .isGreaterThanOrEqualTo(99.0);
        
        assertThat(result.getAvgResponseTimeMs())
                .as("平均响应时间应小于等于500ms")
                .isLessThanOrEqualTo(500.0);
        
        // 验证长时间运行稳定性
        assertThat(result.getP99ResponseTimeMs())
                .as("P99响应时间应小于等于1000ms")
                .isLessThanOrEqualTo(1000L);
        
        logTest("持久性测试完成，TPS: {}, 成功率: {}%", 
                String.format("%.2f", result.getTps()), 
                String.format("%.2f", result.getSuccessRate()));
    }
    
    /**
     * 测试场景4：快速压力测试（用于快速验证）
     * 
     * <p>配置：10并发，10秒</p>
     * <p>用途：快速验证接口可用性和基本性能</p>
     */
    @Test
    @DisplayName("快速压力测试（10并发，10秒）")
    void testInsert_QuickLoad() {
        logTest("开始快速压力测试：10并发，10秒");
        
        // 配置压力测试
        LoadTestConfig config = LoadTestConfig.builder()
                .url(baseUrl + "/order/insert")
                .httpMethod("POST")
                .concurrentThreads(10)
                .durationSeconds(10)
                .warmupSeconds(5)
                .requestBodySupplier(this::createOrderRequest)
                .header("Content-Type", "application/json")
                .build();
        
        // 执行压力测试
        LoadTestResult result = executor.execute(config);
        
        // 打印测试报告
        result.printReport();
        
        // 验证测试结果
        assertThat(result.getSuccessRate())
                .as("成功率应大于等于95%")
                .isGreaterThanOrEqualTo(95.0);
        
        logTest("快速压力测试完成，TPS: {}, 成功率: {}%", 
                String.format("%.2f", result.getTps()), 
                String.format("%.2f", result.getSuccessRate()));
    }
}

