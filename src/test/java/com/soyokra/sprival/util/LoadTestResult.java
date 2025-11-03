package com.soyokra.sprival.util;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 压力测试结果类
 * 
 * <p>包含压力测试的所有统计数据</p>
 * 
 * <p>统计指标：</p>
 * <ul>
 *   <li>总请求数、成功数、失败数</li>
 *   <li>平均响应时间、最小/最大响应时间</li>
 *   <li>P50/P90/P95/P99响应时间</li>
 *   <li>TPS（每秒事务数）</li>
 *   <li>成功率</li>
 *   <li>错误详情列表</li>
 * </ul>
 * 
 * @author sprival-test-framework
 * @since 2025-11-03
 */
@Data
@Builder
public class LoadTestResult {
    
    /**
     * 测试持续时间（秒）
     */
    private double durationSeconds;
    
    /**
     * 并发线程数
     */
    private int concurrentThreads;
    
    /**
     * 总请求数
     */
    private long totalRequests;
    
    /**
     * 成功请求数
     */
    private long successRequests;
    
    /**
     * 失败请求数
     */
    private long failedRequests;
    
    /**
     * 平均响应时间（毫秒）
     */
    private double avgResponseTimeMs;
    
    /**
     * 最小响应时间（毫秒）
     */
    private long minResponseTimeMs;
    
    /**
     * 最大响应时间（毫秒）
     */
    private long maxResponseTimeMs;
    
    /**
     * P50响应时间（毫秒）
     */
    private long p50ResponseTimeMs;
    
    /**
     * P90响应时间（毫秒）
     */
    private long p90ResponseTimeMs;
    
    /**
     * P95响应时间（毫秒）
     */
    private long p95ResponseTimeMs;
    
    /**
     * P99响应时间（毫秒）
     */
    private long p99ResponseTimeMs;
    
    /**
     * TPS（每秒事务数）
     */
    private double tps;
    
    /**
     * 成功率（百分比）
     */
    private double successRate;
    
    /**
     * 错误详情列表
     */
    @Builder.Default
    private List<String> errors = new ArrayList<>();
    
    /**
     * 打印测试报告
     * 
     * <p>将测试结果格式化输出到控制台</p>
     */
    public void printReport() {
        System.out.println("\n========================================");
        System.out.println("Load Test Report");
        System.out.println("========================================");
        System.out.printf("Test Duration: %.2f seconds%n", durationSeconds);
        System.out.printf("Concurrent Threads: %d%n", concurrentThreads);
        System.out.println("----------------------------------------");
        System.out.printf("Total Requests: %,d%n", totalRequests);
        System.out.printf("Successful Requests: %,d (%.1f%%)%n", successRequests, successRate);
        System.out.printf("Failed Requests: %,d (%.1f%%)%n", failedRequests, 100.0 - successRate);
        System.out.println("----------------------------------------");
        System.out.printf("TPS: %.2f requests/sec%n", tps);
        System.out.printf("Avg Response Time: %.2f ms%n", avgResponseTimeMs);
        System.out.printf("Min Response Time: %d ms%n", minResponseTimeMs);
        System.out.printf("Max Response Time: %d ms%n", maxResponseTimeMs);
        System.out.println("----------------------------------------");
        System.out.printf("P50 Response Time: %d ms%n", p50ResponseTimeMs);
        System.out.printf("P90 Response Time: %d ms%n", p90ResponseTimeMs);
        System.out.printf("P95 Response Time: %d ms%n", p95ResponseTimeMs);
        System.out.printf("P99 Response Time: %d ms%n", p99ResponseTimeMs);
        System.out.println("========================================");
        
        if (!errors.isEmpty()) {
            System.out.println("\nError Details (first 10):");
            errors.stream()
                    .limit(10)
                    .forEach(error -> System.out.println("  - " + error));
        }
        System.out.println();
    }
    
    /**
     * 获取成功率
     * 
     * @return 成功率（百分比）
     */
    public double getSuccessRate() {
        return successRate;
    }
    
    /**
     * 判断是否通过测试
     * 
     * @param minSuccessRate 最小成功率阈值（百分比）
     * @param maxAvgResponseTime 最大平均响应时间阈值（毫秒）
     * @return 是否通过测试
     */
    public boolean isPassed(double minSuccessRate, double maxAvgResponseTime) {
        return successRate >= minSuccessRate && avgResponseTimeMs <= maxAvgResponseTime;
    }
}

