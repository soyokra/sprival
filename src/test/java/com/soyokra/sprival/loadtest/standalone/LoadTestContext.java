package com.soyokra.sprival.loadtest.standalone;

import com.soyokra.sprival.util.HttpLoadTestExecutor;
import lombok.Builder;
import lombok.Data;
import org.apache.http.impl.client.CloseableHttpClient;

import java.util.function.Supplier;

/**
 * 压力测试上下文
 * 
 * <p>包含压力测试执行所需的所有上下文信息</p>
 * 
 * @author sprival-test-framework
 * @since 2025-11-03
 */
@Data
@Builder
public class LoadTestContext {
    
    /**
     * 目标URL
     */
    private String url;
    
    /**
     * HTTP客户端
     */
    private CloseableHttpClient httpClient;
    
    /**
     * HTTP负载测试执行器
     */
    private HttpLoadTestExecutor executor;
    
    /**
     * 请求体生成器
     */
    private Supplier<String> requestBodySupplier;
    
    /**
     * 默认并发线程数
     */
    @Builder.Default
    private int defaultThreads = 10;
    
    /**
     * 默认测试持续时间（秒）
     */
    @Builder.Default
    private int defaultDuration = 60;
    
    /**
     * 默认预热时间（秒）
     */
    @Builder.Default
    private int defaultWarmup = 10;
    
    /**
     * 实时报告输出间隔（秒）
     */
    @Builder.Default
    private int reportInterval = 10;
    
    /**
     * 报告输出文件路径（可选）
     */
    private String reportFile;
}

