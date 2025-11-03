package com.soyokra.sprival.util;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * HTTP压力测试执行器
 * 
 * <p>核心功能：</p>
 * <ul>
 *   <li>多线程并发HTTP请求</li>
 *   <li>实时统计收集</li>
 *   <li>支持预热</li>
 *   <li>生成测试报告</li>
 * </ul>
 * 
 * <p>使用示例：</p>
 * <pre>{@code
 * CloseableHttpClient httpClient = HttpClients.createDefault();
 * HttpLoadTestExecutor executor = new HttpLoadTestExecutor(httpClient);
 * 
 * LoadTestConfig config = LoadTestConfig.builder()
 *         .url("http://localhost:8338/api/order/insert")
 *         .concurrentThreads(10)
 *         .durationSeconds(60)
 *         .requestBodySupplier(() -> createOrderRequest())
 *         .build();
 * 
 * LoadTestResult result = executor.execute(config);
 * result.printReport();
 * }</pre>
 * 
 * @author sprival-test-framework
 * @since 2025-11-03
 */
public class HttpLoadTestExecutor {
    
    private static final Logger log = LoggerFactory.getLogger(HttpLoadTestExecutor.class);
    
    private final CloseableHttpClient httpClient;
    
    /**
     * 构造函数
     * 
     * @param httpClient HTTP客户端
     */
    public HttpLoadTestExecutor(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }
    
    /**
     * 执行压力测试
     * 
     * @param config 测试配置
     * @return 测试结果
     */
    public LoadTestResult execute(LoadTestConfig config) {
        log.info("开始压力测试: {}", config.getUrl());
        log.info("并发线程数: {}, 持续时间: {}秒", config.getConcurrentThreads(), config.getDurationSeconds());
        
        // 预热
        if (config.getWarmupSeconds() > 0) {
            log.info("开始预热，预热时间: {}秒", config.getWarmupSeconds());
            warmUp(config);
            log.info("预热完成");
        }
        
        // 执行压力测试
        LoadTestStatistics statistics = new LoadTestStatistics();
        ExecutorService executorService = Executors.newFixedThreadPool(config.getConcurrentThreads());
        CountDownLatch latch = new CountDownLatch(config.getConcurrentThreads());
        
        long testEndTime = System.currentTimeMillis() + config.getDurationSeconds() * 1000L;
        
        // 启动所有测试线程
        for (int i = 0; i < config.getConcurrentThreads(); i++) {
            final int threadId = i;
            executorService.submit(() -> {
                try {
                    runLoadTest(config, statistics, testEndTime, threadId);
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // 等待所有线程完成
        try {
            latch.await();
        } catch (InterruptedException e) {
            log.error("等待测试线程完成时被中断", e);
            Thread.currentThread().interrupt();
        }
        
        // 关闭线程池
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        // 生成测试结果
        LoadTestResult result = statistics.generateResult(config.getConcurrentThreads());
        log.info("压力测试完成，总请求数: {}, 成功率: {:.2f}%, TPS: {:.2f}", 
                result.getTotalRequests(), result.getSuccessRate(), result.getTps());
        
        return result;
    }
    
    /**
     * 预热
     * 
     * @param config 测试配置
     */
    private void warmUp(LoadTestConfig config) {
        LoadTestStatistics warmupStats = new LoadTestStatistics();
        long warmupEndTime = System.currentTimeMillis() + config.getWarmupSeconds() * 1000L;
        
        while (System.currentTimeMillis() < warmupEndTime) {
            sendRequest(config, warmupStats, 0);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        log.info("预热请求数: {}", warmupStats.getTotalRequests());
    }
    
    /**
     * 执行负载测试
     * 
     * @param config 测试配置
     * @param statistics 统计对象
     * @param endTime 结束时间
     * @param threadId 线程ID
     */
    private void runLoadTest(LoadTestConfig config, LoadTestStatistics statistics, long endTime, int threadId) {
        log.debug("测试线程 {} 启动", threadId);
        
        int requestCount = 0;
        while (System.currentTimeMillis() < endTime) {
            sendRequest(config, statistics, threadId);
            requestCount++;
            
            // 每100个请求打印一次日志
            if (requestCount % 100 == 0) {
                log.debug("线程 {} 已发送 {} 个请求", threadId, requestCount);
            }
        }
        
        log.debug("测试线程 {} 完成，共发送 {} 个请求", threadId, requestCount);
    }
    
    /**
     * 发送单个HTTP请求
     * 
     * @param config 测试配置
     * @param statistics 统计对象
     * @param threadId 线程ID
     */
    private void sendRequest(LoadTestConfig config, LoadTestStatistics statistics, int threadId) {
        long startTime = System.currentTimeMillis();
        
        try {
            // 创建HTTP请求
            HttpPost httpPost = new HttpPost(config.getUrl());
            
            // 设置超时
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(config.getConnectTimeoutMs())
                    .setSocketTimeout(config.getReadTimeoutMs())
                    .build();
            httpPost.setConfig(requestConfig);
            
            // 设置请求头
            if (config.getHeaders() != null) {
                for (Map.Entry<String, String> header : config.getHeaders().entrySet()) {
                    httpPost.setHeader(header.getKey(), header.getValue());
                }
            }
            
            // 设置请求体
            if (config.getRequestBodySupplier() != null) {
                String requestBody = config.getRequestBodySupplier().get();
                httpPost.setEntity(new StringEntity(requestBody, "UTF-8"));
                httpPost.setHeader("Content-Type", "application/json");
            }
            
            // 发送请求
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getStatusLine().getStatusCode();
                
                // 消费响应内容
                EntityUtils.consume(response.getEntity());
                
                long responseTime = System.currentTimeMillis() - startTime;
                
                // 判断是否成功（2xx状态码）
                if (statusCode >= 200 && statusCode < 300) {
                    statistics.recordSuccess(responseTime);
                } else {
                    statistics.recordFailure("HTTP " + statusCode + ": " + response.getStatusLine().getReasonPhrase());
                }
            }
            
        } catch (Exception e) {
            statistics.recordFailure(e.getClass().getSimpleName() + ": " + e.getMessage());
            log.trace("请求失败 [线程{}]: {}", threadId, e.getMessage());
        }
    }
}

