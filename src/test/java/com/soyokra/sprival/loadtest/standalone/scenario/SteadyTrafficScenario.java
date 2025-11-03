package com.soyokra.sprival.loadtest.standalone.scenario;

import com.soyokra.sprival.loadtest.standalone.LoadTestContext;
import com.soyokra.sprival.util.LoadTestConfig;
import com.soyokra.sprival.util.LoadTestResult;
import lombok.AllArgsConstructor;

/**
 * 稳定流量场景
 * 
 * <p>特点：</p>
 * <ul>
 *   <li>固定并发线程数</li>
 *   <li>持续稳定发送请求</li>
 *   <li>最常用的基准性能测试</li>
 * </ul>
 * 
 * <p>适用场景：</p>
 * <ul>
 *   <li>基准性能测试</li>
 *   <li>系统容量评估</li>
 *   <li>对比测试</li>
 * </ul>
 * 
 * @author sprival-test-framework
 * @since 2025-11-03
 */
@AllArgsConstructor
public class SteadyTrafficScenario implements LoadTestScenario {
    
    private final int threads;
    private final int durationSeconds;
    private final int warmupSeconds;
    
    @Override
    public String getName() {
        return "稳定流量测试 (Steady Traffic)";
    }
    
    @Override
    public String getDescription() {
        return String.format("固定%d个并发线程，持续%d秒，预热%d秒", 
                threads, durationSeconds, warmupSeconds);
    }
    
    @Override
    public LoadTestResult execute(LoadTestContext context) throws Exception {
        System.out.println("开始执行稳定流量测试...");
        System.out.println("并发线程: " + threads);
        System.out.println("测试时长: " + durationSeconds + "秒");
        System.out.println("预热时长: " + warmupSeconds + "秒");
        System.out.println();
        
        LoadTestConfig config = LoadTestConfig.builder()
                .url(context.getUrl())
                .httpMethod("POST")
                .concurrentThreads(threads)
                .durationSeconds(durationSeconds)
                .warmupSeconds(warmupSeconds)
                .requestBodySupplier(context.getRequestBodySupplier())
                .header("Content-Type", "application/json")
                .build();
        
        return context.getExecutor().execute(config);
    }
}

