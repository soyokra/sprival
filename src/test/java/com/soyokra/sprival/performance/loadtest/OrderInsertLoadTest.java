package com.soyokra.sprival.performance.loadtest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soyokra.sprival.performance.config.PerformanceTestConfig;
import com.soyokra.sprival.performance.util.PerformanceTestUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 下单接口压力测试（HTTP 接口）
 * 
 * <p>
 * 测试目标：
 * <ul>
 * <li>验证系统在高并发下的性能表现</li>
 * <li>测试接口的 TPS (每秒事务数)</li>
 * <li>测试接口的响应时间分布</li>
 * <li>发现性能瓶颈和潜在问题</li>
 * </ul>
 * 
 * <p>
 * 使用方式：
 * 
 * <pre>
 * # Windows - 使用脚本（推荐）
 * .\scripts\run-performance-test.ps1
 * .\scripts\run-performance-test.ps1 -ConcurrentUsers 200 -DurationSeconds 120
 * .\scripts\run-performance-test.ps1 -TestMethod testOrderInsertPeakLoad
 * 
 * # Linux/Mac - 使用脚本
 * ./scripts/run-performance-test.sh
 * ./scripts/run-performance-test.sh OrderInsertLoadTest "" 200 120
 * 
 * # Maven 命令
 * mvn test -Dtest=OrderInsertLoadTest
 * mvn test -Dtest=OrderInsertLoadTest#testOrderInsertWithFixedConcurrency
 * mvn test -Dtest=OrderInsertLoadTest -Dperformance.test.concurrent-users=200
 * </pre>
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class OrderInsertLoadTest {

    @Autowired(required = false)
    private PerformanceTestConfig config;

    private CloseableHttpClient httpClient;
    private ObjectMapper objectMapper;
    private String orderInsertUrl;

    @BeforeEach
    public void setup() {
        // 初始化配置
        if (config == null) {
            config = new PerformanceTestConfig();
        }

        orderInsertUrl = config.getBaseUrl() + "/api/order/insert";
        objectMapper = new ObjectMapper();

        // 配置 HTTP 连接池
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(config.getConcurrentUsers() * 2); // 连接池最大连接数
        cm.setDefaultMaxPerRoute(config.getConcurrentUsers()); // 每个路由的最大连接数

        // 配置超时时间
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5000)
                .setSocketTimeout(10000).setConnectionRequestTimeout(3000).build();

        httpClient = HttpClients.custom().setConnectionManager(cm)
                .setDefaultRequestConfig(requestConfig).build();

        log.info("性能测试初始化完成");
        log.info("测试URL: {}", orderInsertUrl);
        log.info("并发用户数: {}", config.getConcurrentUsers());
        log.info("测试持续时间: {} 秒", config.getDurationSeconds());
    }

    /**
     * 基础压力测试 - 固定并发用户数
     */
    @Test
    public void testOrderInsertWithFixedConcurrency() throws Exception {
        log.info("========================================");
        log.info("开始下单接口压力测试 - 固定并发");
        log.info("========================================");

        int concurrentUsers = config.getConcurrentUsers();
        int durationSeconds = config.getDurationSeconds();

        ExecutorService executor = Executors.newFixedThreadPool(concurrentUsers);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(concurrentUsers);

        List<Long> responseTimes = new CopyOnWriteArrayList<>();
        AtomicLong failedCount = new AtomicLong(0);
        AtomicLong requestCount = new AtomicLong(0);

        long startTime = System.currentTimeMillis();

        // 启动并发任务
        for (int i = 0; i < concurrentUsers; i++) {
            final int userId = i + 1;
            executor.submit(() -> {
                try {
                    startLatch.await(); // 等待统一开始信号

                    while (System.currentTimeMillis() - startTime < durationSeconds * 1000) {
                        long reqStartTime = System.currentTimeMillis();
                        boolean success = sendOrderInsertRequest(userId);
                        long reqEndTime = System.currentTimeMillis();

                        requestCount.incrementAndGet();

                        if (success) {
                            responseTimes.add(reqEndTime - reqStartTime);
                        } else {
                            failedCount.incrementAndGet();
                        }

                        // 短暂休眠，避免过度压测
                        Thread.sleep(10);
                    }
                } catch (Exception e) {
                    log.error("测试执行异常", e);
                } finally {
                    endLatch.countDown();
                }
            });
        }

        // 发送开始信号
        log.info("开始发送请求...");
        startLatch.countDown();

        // 等待所有任务完成
        endLatch.await();
        executor.shutdown();

        long endTime = System.currentTimeMillis();
        long totalDuration = endTime - startTime;

        // 计算并打印结果
        PerformanceTestUtils.PerformanceResult result = PerformanceTestUtils.calculateResult(
                "OrderInsert_FixedConcurrency_" + concurrentUsers, responseTimes, totalDuration,
                failedCount.get());

        PerformanceTestUtils.printResult(result);
        PerformanceTestUtils.saveReport(result, config.getReportOutputDir());

        log.info("压力测试完成！");
    }

    /**
     * 递增压力测试 - 逐步增加并发
     */
    @Test
    public void testOrderInsertWithIncrementalLoad() throws Exception {
        log.info("========================================");
        log.info("开始下单接口压力测试 - 递增并发");
        log.info("========================================");

        int[] concurrencyLevels = {10, 50, 100, 200, 500};
        int durationPerLevel = 30; // 每个并发级别持续30秒

        for (int concurrency : concurrencyLevels) {
            log.info("当前并发级别: {}", concurrency);
            runLoadTestWithConcurrency(concurrency, durationPerLevel);

            // 两次测试之间休息10秒
            log.info("休息10秒后继续下一轮测试...");
            Thread.sleep(10000);
        }

        log.info("递增压力测试完成！");
    }

    /**
     * 峰值压力测试 - 短时间高并发
     */
    @Test
    public void testOrderInsertPeakLoad() throws Exception {
        log.info("========================================");
        log.info("开始下单接口压力测试 - 峰值测试");
        log.info("========================================");

        int peakConcurrency = 1000; // 峰值并发数
        int peakDuration = 10; // 峰值持续10秒

        runLoadTestWithConcurrency(peakConcurrency, peakDuration);

        log.info("峰值压力测试完成！");
    }

    /**
     * 执行指定并发数的压力测试
     */
    private void runLoadTestWithConcurrency(int concurrency, int durationSeconds) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(concurrency);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(concurrency);

        List<Long> responseTimes = new CopyOnWriteArrayList<>();
        AtomicLong failedCount = new AtomicLong(0);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < concurrency; i++) {
            final int userId = i + 1;
            executor.submit(() -> {
                try {
                    startLatch.await();

                    while (System.currentTimeMillis() - startTime < durationSeconds * 1000) {
                        long reqStartTime = System.currentTimeMillis();
                        boolean success = sendOrderInsertRequest(userId);
                        long reqEndTime = System.currentTimeMillis();

                        if (success) {
                            responseTimes.add(reqEndTime - reqStartTime);
                        } else {
                            failedCount.incrementAndGet();
                        }

                        Thread.sleep(5);
                    }
                } catch (Exception e) {
                    log.error("测试执行异常", e);
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        endLatch.await();
        executor.shutdown();

        long endTime = System.currentTimeMillis();
        long totalDuration = endTime - startTime;

        PerformanceTestUtils.PerformanceResult result =
                PerformanceTestUtils.calculateResult("OrderInsert_Concurrency_" + concurrency,
                        responseTimes, totalDuration, failedCount.get());

        PerformanceTestUtils.printResult(result);
        PerformanceTestUtils.saveReport(result, config.getReportOutputDir());
    }

    /**
     * 发送下单请求
     */
    private boolean sendOrderInsertRequest(int userId) {
        HttpPost httpPost = new HttpPost(orderInsertUrl);
        httpPost.setHeader("Content-Type", "application/json");

        try {
            long timestamp = System.currentTimeMillis();
            // 使用后10位时间戳 + 自增序列号，确保唯一性且长度可控
            String timestampSuffix = String.valueOf(timestamp).substring(3);
            int randomSuffix = (int) (Math.random() * 1000);

            // 构造请求体 - 匹配 OrderInsertRequest 的字段定义
            Map<String, Object> orderData = new HashMap<>();
            // 必填字段 - 严格控制长度
            // order_id: varchar(22) - 格式: O{10位时间戳}{用户ID}{3位随机} = 最多21字符
            orderData.put("orderId",
                    String.format("O%s%d%03d", timestampSuffix, userId % 10000, randomSuffix));
            orderData.put("userId", String.valueOf(userId));
            orderData.put("orderType", "NORMAL");
            // idempotent_id: varchar(50) - 格式: I{13位时间戳}_{用户ID}_{6位随机} = 最多35字符
            orderData.put("idempotentId", String.format("I%d_%d_%06d", timestamp, userId,
                    (int) (Math.random() * 1000000)));
            orderData.put("statusNo", 0); // 0-待支付

            // 可选字段
            // trade_id: varchar(20) - 格式: T{13位时间戳}{3位随机} = 最多17字符
            orderData.put("tradeId", String.format("T%d%03d", timestamp, randomSuffix));
            orderData.put("parentOrderId", null);
            orderData.put("partnerId", "PARTNER_" + ((int) (Math.random() * 10) + 1));
            orderData.put("supplierId", "SUPPLIER_" + ((int) (Math.random() * 100) + 1));
            orderData.put("businessStatus", 1); // 1-正常

            String jsonBody = objectMapper.writeValueAsString(orderData);
            httpPost.setEntity(new StringEntity(jsonBody, "UTF-8"));

            // 发送请求
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getStatusLine().getStatusCode();
                return statusCode >= 200 && statusCode < 300;
            }
        } catch (Exception e) {
            if (config.isVerboseLogging()) {
                log.warn("请求失败: {}", e.getMessage());
            }
            return false;
        }
    }
}

