package com.soyokra.sprival.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 压力测试统计工具类
 * 
 * <p>用于收集和计算压力测试的统计数据</p>
 * 
 * <p>功能：</p>
 * <ul>
 *   <li>线程安全的统计数据收集</li>
 *   <li>响应时间分位数计算</li>
 *   <li>TPS计算</li>
 *   <li>成功率计算</li>
 * </ul>
 * 
 * @author sprival-test-framework
 * @since 2025-11-03
 */
public class LoadTestStatistics {
    
    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong successRequests = new AtomicLong(0);
    private final AtomicLong failedRequests = new AtomicLong(0);
    
    private final ConcurrentLinkedQueue<Long> responseTimes = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<String> errors = new ConcurrentLinkedQueue<>();
    
    private final long startTime;
    
    /**
     * 构造函数
     * 
     * <p>记录测试开始时间</p>
     */
    public LoadTestStatistics() {
        this.startTime = System.currentTimeMillis();
    }
    
    /**
     * 记录成功请求
     * 
     * @param responseTimeMs 响应时间（毫秒）
     */
    public void recordSuccess(long responseTimeMs) {
        totalRequests.incrementAndGet();
        successRequests.incrementAndGet();
        responseTimes.add(responseTimeMs);
    }
    
    /**
     * 记录失败请求
     * 
     * @param error 错误信息
     */
    public void recordFailure(String error) {
        totalRequests.incrementAndGet();
        failedRequests.incrementAndGet();
        errors.add(error);
    }
    
    /**
     * 生成测试结果
     * 
     * @param concurrentThreads 并发线程数
     * @return 测试结果对象
     */
    public LoadTestResult generateResult(int concurrentThreads) {
        long endTime = System.currentTimeMillis();
        double durationSeconds = (endTime - startTime) / 1000.0;
        
        long total = totalRequests.get();
        long success = successRequests.get();
        long failed = failedRequests.get();
        
        // 计算TPS
        double tps = total / durationSeconds;
        
        // 计算成功率
        double successRate = total > 0 ? (success * 100.0 / total) : 0.0;
        
        // 计算响应时间统计
        List<Long> sortedTimes = new ArrayList<>(responseTimes);
        Collections.sort(sortedTimes);
        
        long minTime = 0;
        long maxTime = 0;
        double avgTime = 0.0;
        long p50 = 0;
        long p90 = 0;
        long p95 = 0;
        long p99 = 0;
        
        if (!sortedTimes.isEmpty()) {
            minTime = sortedTimes.get(0);
            maxTime = sortedTimes.get(sortedTimes.size() - 1);
            
            // 计算平均响应时间
            long sum = sortedTimes.stream().mapToLong(Long::longValue).sum();
            avgTime = (double) sum / sortedTimes.size();
            
            // 计算分位数
            p50 = calculatePercentile(sortedTimes, 50);
            p90 = calculatePercentile(sortedTimes, 90);
            p95 = calculatePercentile(sortedTimes, 95);
            p99 = calculatePercentile(sortedTimes, 99);
        }
        
        return LoadTestResult.builder()
                .durationSeconds(durationSeconds)
                .concurrentThreads(concurrentThreads)
                .totalRequests(total)
                .successRequests(success)
                .failedRequests(failed)
                .avgResponseTimeMs(avgTime)
                .minResponseTimeMs(minTime)
                .maxResponseTimeMs(maxTime)
                .p50ResponseTimeMs(p50)
                .p90ResponseTimeMs(p90)
                .p95ResponseTimeMs(p95)
                .p99ResponseTimeMs(p99)
                .tps(tps)
                .successRate(successRate)
                .errors(new ArrayList<>(errors))
                .build();
    }
    
    /**
     * 计算分位数
     * 
     * @param sortedTimes 已排序的响应时间列表
     * @param percentile 分位数（0-100）
     * @return 分位数值
     */
    private long calculatePercentile(List<Long> sortedTimes, int percentile) {
        if (sortedTimes.isEmpty()) {
            return 0;
        }
        
        int index = (int) Math.ceil(percentile / 100.0 * sortedTimes.size()) - 1;
        index = Math.max(0, Math.min(index, sortedTimes.size() - 1));
        
        return sortedTimes.get(index);
    }
    
    /**
     * 获取当前总请求数
     * 
     * @return 总请求数
     */
    public long getTotalRequests() {
        return totalRequests.get();
    }
    
    /**
     * 获取当前成功请求数
     * 
     * @return 成功请求数
     */
    public long getSuccessRequests() {
        return successRequests.get();
    }
    
    /**
     * 获取当前失败请求数
     * 
     * @return 失败请求数
     */
    public long getFailedRequests() {
        return failedRequests.get();
    }
}

