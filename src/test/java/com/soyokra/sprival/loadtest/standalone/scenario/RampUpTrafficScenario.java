package com.soyokra.sprival.loadtest.standalone.scenario;

import com.soyokra.sprival.loadtest.standalone.LoadTestContext;
import com.soyokra.sprival.util.LoadTestConfig;
import com.soyokra.sprival.util.LoadTestResult;
import com.soyokra.sprival.util.LoadTestStatistics;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 渐进增长流量场景
 * 
 * <p>特点：</p>
 * <ul>
 *   <li>并发数逐步增加</li>
 *   <li>找到系统性能临界点</li>
 *   <li>观察不同负载下的表现</li>
 * </ul>
 * 
 * <p>执行流程：</p>
 * <ul>
 *   <li>第1阶段：startThreads线程，持续stepDuration秒</li>
 *   <li>第2阶段：startThreads+step线程，持续stepDuration秒</li>
 *   <li>...</li>
 *   <li>最后阶段：maxThreads线程，持续stepDuration秒</li>
 * </ul>
 * 
 * <p>适用场景：</p>
 * <ul>
 *   <li>寻找系统性能临界点</li>
 *   <li>容量规划</li>
 *   <li>性能瓶颈分析</li>
 * </ul>
 * 
 * @author sprival-test-framework
 * @since 2025-11-03
 */
@AllArgsConstructor
public class RampUpTrafficScenario implements LoadTestScenario {
    
    private final int startThreads;
    private final int maxThreads;
    private final int step;
    private final int stepDuration;
    
    @Override
    public String getName() {
        return "渐进增长测试 (Ramp Up Traffic)";
    }
    
    @Override
    public String getDescription() {
        return String.format("从%d并发开始，每次增加%d，每阶段%d秒，直到%d并发", 
                startThreads, step, stepDuration, maxThreads);
    }
    
    @Override
    public LoadTestResult execute(LoadTestContext context) throws Exception {
        System.out.println("开始执行渐进增长测试...");
        System.out.println("起始并发: " + startThreads);
        System.out.println("最大并发: " + maxThreads);
        System.out.println("增长步长: " + step);
        System.out.println("阶段时长: " + stepDuration + "秒");
        System.out.println();
        
        // 计算阶段数
        List<Integer> stages = new ArrayList<>();
        for (int threads = startThreads; threads <= maxThreads; threads += step) {
            stages.add(threads);
        }
        if (stages.get(stages.size() - 1) < maxThreads) {
            stages.add(maxThreads);
        }
        
        System.out.println("总阶段数: " + stages.size());
        System.out.println("========================================");
        
        // 汇总统计
        LoadTestStatistics overallStats = new LoadTestStatistics();
        
        // 执行各个阶段
        for (int i = 0; i < stages.size(); i++) {
            int currentThreads = stages.get(i);
            System.out.println(String.format("\n阶段 %d/%d: %d 并发，%d 秒", 
                    i + 1, stages.size(), currentThreads, stepDuration));
            System.out.println("----------------------------------------");
            
            LoadTestConfig config = LoadTestConfig.builder()
                    .url(context.getUrl())
                    .httpMethod("POST")
                    .concurrentThreads(currentThreads)
                    .durationSeconds(stepDuration)
                    .warmupSeconds(0)  // 阶段性测试不需要预热
                    .requestBodySupplier(context.getRequestBodySupplier())
                    .header("Content-Type", "application/json")
                    .build();
            
            LoadTestResult stageResult = context.getExecutor().execute(config);
            
            // 打印阶段结果
            System.out.println(String.format("阶段结果: 请求数=%d, 成功率=%.1f%%, TPS=%.2f, 平均响应时间=%.2fms",
                    stageResult.getTotalRequests(),
                    stageResult.getSuccessRate(),
                    stageResult.getTps(),
                    stageResult.getAvgResponseTimeMs()));
        }
        
        // 返回最后一个阶段的结果（最大负载）
        System.out.println("\n========================================");
        System.out.println("渐进增长测试完成");
        System.out.println("最终并发: " + stages.get(stages.size() - 1));
        System.out.println("========================================\n");
        
        // 执行最终完整测试
        LoadTestConfig finalConfig = LoadTestConfig.builder()
                .url(context.getUrl())
                .httpMethod("POST")
                .concurrentThreads(maxThreads)
                .durationSeconds(stepDuration)
                .warmupSeconds(0)
                .requestBodySupplier(context.getRequestBodySupplier())
                .header("Content-Type", "application/json")
                .build();
        
        return context.getExecutor().execute(finalConfig);
    }
}

