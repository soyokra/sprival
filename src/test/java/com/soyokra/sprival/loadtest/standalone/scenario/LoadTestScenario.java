package com.soyokra.sprival.loadtest.standalone.scenario;

import com.soyokra.sprival.loadtest.standalone.LoadTestContext;
import com.soyokra.sprival.util.LoadTestResult;

/**
 * 压力测试场景接口
 * 
 * <p>定义压力测试场景的标准接口</p>
 * 
 * <p>实现类：</p>
 * <ul>
 *   <li>{@link SteadyTrafficScenario} - 稳定流量</li>
 *   <li>{@link BurstTrafficScenario} - 突发流量</li>
 *   <li>{@link RampUpTrafficScenario} - 渐进增长</li>
 *   <li>{@link SpikeTrafficScenario} - 脉冲流量</li>
 *   <li>{@link EnduranceTrafficScenario} - 持久性测试</li>
 * </ul>
 * 
 * @author sprival-test-framework
 * @since 2025-11-03
 */
public interface LoadTestScenario {
    
    /**
     * 获取场景名称
     * 
     * @return 场景名称
     */
    String getName();
    
    /**
     * 获取场景描述
     * 
     * @return 场景描述
     */
    String getDescription();
    
    /**
     * 执行测试场景
     * 
     * @param context 测试上下文
     * @return 测试结果
     * @throws Exception 执行异常
     */
    LoadTestResult execute(LoadTestContext context) throws Exception;
}

