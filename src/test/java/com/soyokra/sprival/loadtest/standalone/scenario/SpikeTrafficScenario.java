package com.soyokra.sprival.loadtest.standalone.scenario;

import com.soyokra.sprival.loadtest.standalone.LoadTestContext;
import com.soyokra.sprival.util.LoadTestConfig;
import com.soyokra.sprival.util.LoadTestResult;
import lombok.AllArgsConstructor;

/**
 * 脉冲流量场景
 * 
 * <p>特点：</p>
 * <ul>
 *   <li>高低并发交替</li>
 *   <li>测试系统快速恢复能力</li>
 *   <li>模拟不稳定流量</li>
 * </ul>
 * 
 * <p>执行流程：</p>
 * <ul>
 *   <li>周期1：高并发（spikeDuration秒）-> 低并发（spikeDuration秒）</li>
 *   <li>周期2：高并发（spikeDuration秒）-> 低并发（spikeDuration秒）</li>
 *   <li>...</li>
 *   <li>重复cycles次</li>
 * </ul>
 * 
 * <p>适用场景：</p>
 * <ul>
 *   <li>测试系统弹性恢复能力</li>
 *   <li>验证自动扩缩容</li>
 *   <li>不稳定流量场景</li>
 * </ul>
 * 
 * @author sprival-test-framework
 * @since 2025-11-03
 */
@AllArgsConstructor
public class SpikeTrafficScenario implements LoadTestScenario {
    
    private final int highThreads;
    private final int lowThreads;
    private final int spikeDuration;
    private final int cycles;
    
    @Override
    public String getName() {
        return "脉冲流量测试 (Spike Traffic)";
    }
    
    @Override
    public String getDescription() {
        return String.format("高并发%d与低并发%d交替，每次%d秒，共%d个周期", 
                highThreads, lowThreads, spikeDuration, cycles);
    }
    
    @Override
    public LoadTestResult execute(LoadTestContext context) throws Exception {
        System.out.println("开始执行脉冲流量测试...");
        System.out.println("高并发数: " + highThreads);
        System.out.println("低并发数: " + lowThreads);
        System.out.println("脉冲时长: " + spikeDuration + "秒");
        System.out.println("脉冲周期: " + cycles);
        System.out.println();
        
        // 执行多个周期
        for (int cycle = 1; cycle <= cycles; cycle++) {
            System.out.println(String.format("\n=== 周期 %d/%d ===", cycle, cycles));
            
            // 高并发阶段
            System.out.println("高并发阶段: " + highThreads + " 线程");
            LoadTestConfig highConfig = LoadTestConfig.builder()
                    .url(context.getUrl())
                    .httpMethod("POST")
                    .concurrentThreads(highThreads)
                    .durationSeconds(spikeDuration)
                    .warmupSeconds(0)
                    .requestBodySupplier(context.getRequestBodySupplier())
                    .header("Content-Type", "application/json")
                    .build();
            
            LoadTestResult highResult = context.getExecutor().execute(highConfig);
            System.out.println(String.format("高并发结果: TPS=%.2f, 成功率=%.1f%%, 平均响应=%.2fms",
                    highResult.getTps(), highResult.getSuccessRate(), highResult.getAvgResponseTimeMs()));
            
            // 低并发阶段
            System.out.println("\n低并发阶段: " + lowThreads + " 线程");
            LoadTestConfig lowConfig = LoadTestConfig.builder()
                    .url(context.getUrl())
                    .httpMethod("POST")
                    .concurrentThreads(lowThreads)
                    .durationSeconds(spikeDuration)
                    .warmupSeconds(0)
                    .requestBodySupplier(context.getRequestBodySupplier())
                    .header("Content-Type", "application/json")
                    .build();
            
            LoadTestResult lowResult = context.getExecutor().execute(lowConfig);
            System.out.println(String.format("低并发结果: TPS=%.2f, 成功率=%.1f%%, 平均响应=%.2fms",
                    lowResult.getTps(), lowResult.getSuccessRate(), lowResult.getAvgResponseTimeMs()));
        }
        
        System.out.println("\n========================================");
        System.out.println("脉冲流量测试完成");
        System.out.println("========================================\n");
        
        // 返回最后一次高并发的结果
        LoadTestConfig finalConfig = LoadTestConfig.builder()
                .url(context.getUrl())
                .httpMethod("POST")
                .concurrentThreads(highThreads)
                .durationSeconds(spikeDuration)
                .warmupSeconds(0)
                .requestBodySupplier(context.getRequestBodySupplier())
                .header("Content-Type", "application/json")
                .build();
        
        return context.getExecutor().execute(finalConfig);
    }
}

