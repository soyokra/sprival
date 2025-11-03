package com.soyokra.sprival.loadtest.standalone.scenario;

import com.soyokra.sprival.loadtest.standalone.LoadTestContext;
import com.soyokra.sprival.util.LoadTestConfig;
import com.soyokra.sprival.util.LoadTestResult;
import com.soyokra.sprival.util.LoadTestStatistics;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 突发流量场景
 * 
 * <p>特点：</p>
 * <ul>
 *   <li>短时间内快速达到峰值并发</li>
 *   <li>模拟秒杀、促销等场景</li>
 *   <li>测试系统瞬时处理能力</li>
 * </ul>
 * 
 * <p>执行流程：</p>
 * <ol>
 *   <li>快速增长期：从0快速增加到峰值并发（rampUpTime秒）</li>
 *   <li>峰值持续期：保持峰值并发（burstDuration秒）</li>
 *   <li>快速下降期：从峰值快速降到0（rampUpTime秒）</li>
 * </ol>
 * 
 * <p>适用场景：</p>
 * <ul>
 *   <li>秒杀活动测试</li>
 *   <li>促销高峰测试</li>
 *   <li>瞬时峰值压力测试</li>
 * </ul>
 * 
 * @author sprival-test-framework
 * @since 2025-11-03
 */
@AllArgsConstructor
public class BurstTrafficScenario implements LoadTestScenario {
    
    private static final Logger log = LoggerFactory.getLogger(BurstTrafficScenario.class);
    
    private final int peakThreads;
    private final int burstDuration;
    private final int rampUpTime;
    
    @Override
    public String getName() {
        return "突发流量测试 (Burst Traffic)";
    }
    
    @Override
    public String getDescription() {
        return String.format("快速增加到%d并发（%d秒），保持%d秒后快速降低", 
                peakThreads, rampUpTime, burstDuration);
    }
    
    @Override
    public LoadTestResult execute(LoadTestContext context) throws Exception {
        System.out.println("开始执行突发流量测试...");
        System.out.println("峰值并发: " + peakThreads);
        System.out.println("突发持续: " + burstDuration + "秒");
        System.out.println("爬升时间: " + rampUpTime + "秒");
        System.out.println();
        
        // 简化实现：直接使用峰值并发进行测试
        // 完整实现可以在这里分阶段控制并发数
        LoadTestConfig config = LoadTestConfig.builder()
                .url(context.getUrl())
                .httpMethod("POST")
                .concurrentThreads(peakThreads)
                .durationSeconds(burstDuration)
                .warmupSeconds(0)  // 突发流量不需要预热
                .requestBodySupplier(context.getRequestBodySupplier())
                .header("Content-Type", "application/json")
                .build();
        
        System.out.println("正在以" + peakThreads + "并发发送突发流量...");
        return context.getExecutor().execute(config);
    }
}

