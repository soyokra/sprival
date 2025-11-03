package com.soyokra.sprival.loadtest.standalone.scenario;

import com.soyokra.sprival.loadtest.standalone.LoadTestContext;
import com.soyokra.sprival.util.LoadTestConfig;
import com.soyokra.sprival.util.LoadTestResult;
import lombok.AllArgsConstructor;

/**
 * 持久性流量场景
 * 
 * <p>特点：</p>
 * <ul>
 *   <li>中等并发长时间运行</li>
 *   <li>发现内存泄漏、资源泄漏</li>
 *   <li>验证系统稳定性</li>
 * </ul>
 * 
 * <p>测试重点：</p>
 * <ul>
 *   <li>长时间运行的稳定性</li>
 *   <li>内存使用趋势</li>
 *   <li>资源泄漏检测</li>
 *   <li>性能退化监控</li>
 * </ul>
 * 
 * <p>适用场景：</p>
 * <ul>
 *   <li>稳定性测试</li>
 *   <li>内存泄漏检测</li>
 *   <li>长期运行验证</li>
 * </ul>
 * 
 * @author sprival-test-framework
 * @since 2025-11-03
 */
@AllArgsConstructor
public class EnduranceTrafficScenario implements LoadTestScenario {
    
    private final int threads;
    private final int durationSeconds;
    
    @Override
    public String getName() {
        return "持久性测试 (Endurance Traffic)";
    }
    
    @Override
    public String getDescription() {
        int hours = durationSeconds / 3600;
        int minutes = (durationSeconds % 3600) / 60;
        String timeDesc;
        if (hours > 0) {
            timeDesc = String.format("%d小时%d分钟", hours, minutes);
        } else {
            timeDesc = String.format("%d分钟", minutes > 0 ? minutes : 1);
        }
        return String.format("%d并发，持续运行%s", threads, timeDesc);
    }
    
    @Override
    public LoadTestResult execute(LoadTestContext context) throws Exception {
        System.out.println("开始执行持久性测试...");
        System.out.println("并发线程: " + threads);
        System.out.println("测试时长: " + durationSeconds + "秒 (" + getDescription() + ")");
        System.out.println();
        System.out.println("提示：持久性测试运行时间较长，建议同时监控：");
        System.out.println("  - 应用内存使用");
        System.out.println("  - GC频率和耗时");
        System.out.println("  - 数据库连接池");
        System.out.println("  - 系统资源（CPU、磁盘）");
        System.out.println();
        
        LoadTestConfig config = LoadTestConfig.builder()
                .url(context.getUrl())
                .httpMethod("POST")
                .concurrentThreads(threads)
                .durationSeconds(durationSeconds)
                .warmupSeconds(30)  // 持久性测试需要充分预热
                .requestBodySupplier(context.getRequestBodySupplier())
                .header("Content-Type", "application/json")
                .build();
        
        return context.getExecutor().execute(config);
    }
}

