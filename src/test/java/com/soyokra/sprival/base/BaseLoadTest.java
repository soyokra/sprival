package com.soyokra.sprival.base;

import com.soyokra.sprival.util.HttpLoadTestExecutor;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

/**
 * 压力测试基类
 * 
 * <p>用途：HTTP接口压力测试基类</p>
 * 
 * <p>功能：</p>
 * <ul>
 *   <li>提供HTTP客户端连接池</li>
 *   <li>提供压力测试执行器</li>
 *   <li>统计指标收集</li>
 *   <li>测试结果报告</li>
 * </ul>
 * 
 * <p>使用示例：</p>
 * <pre>{@code
 * @DisplayName("Order Insert压力测试")
 * class OrderInsertLoadTest extends BaseLoadTest {
 *     
 *     @Test
 *     @DisplayName("基准压力测试")
 *     void testInsert_BaselineLoad() {
 *         LoadTestConfig config = LoadTestConfig.builder()
 *                 .url(baseUrl + "/order/insert")
 *                 .concurrentThreads(10)
 *                 .durationSeconds(60)
 *                 .requestBodySupplier(() -> createOrderRequest())
 *                 .build();
 *         
 *         LoadTestResult result = executor.execute(config);
 *         result.printReport();
 *         
 *         assertThat(result.getSuccessRate()).isGreaterThan(99.0);
 *     }
 * }
 * }</pre>
 * 
 * <p>HTTP客户端配置：</p>
 * <ul>
 *   <li>最大连接数：200</li>
 *   <li>每个路由最大连接数：50</li>
 *   <li>连接超时：5秒</li>
 *   <li>读取超时：30秒</li>
 * </ul>
 * 
 * @author sprival-test-framework
 * @since 2025-11-03
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseLoadTest {
    
    protected final Logger log = LoggerFactory.getLogger(getClass());
    
    @LocalServerPort
    protected int port;
    
    protected String baseUrl;
    
    protected CloseableHttpClient httpClient;
    
    protected HttpLoadTestExecutor executor;
    
    /**
     * 初始化压力测试环境
     * 
     * <p>创建HTTP客户端连接池和压力测试执行器</p>
     */
    @BeforeEach
    public void setUp() {
        log.info("初始化压力测试: {}", getClass().getSimpleName());
        
        // 构建基础URL
        baseUrl = "http://127.0.0.1:" + port + "/api";
        log.info("测试URL基础路径: {}", baseUrl);
        
        // 创建HTTP连接池
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(200);
        connectionManager.setDefaultMaxPerRoute(50);
        
        // 创建HTTP客户端
        httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();
        
        // 创建压力测试执行器
        executor = new HttpLoadTestExecutor(httpClient);
        
        log.info("压力测试环境初始化完成");
    }
    
    /**
     * 清理压力测试环境
     * 
     * <p>关闭HTTP客户端和连接池</p>
     */
    @AfterEach
    public void tearDown() {
        try {
            if (httpClient != null) {
                httpClient.close();
                log.info("HTTP客户端已关闭");
            }
        } catch (Exception e) {
            log.error("关闭HTTP客户端失败", e);
        }
    }
    
    /**
     * 打印测试日志
     * 
     * @param message 日志消息
     * @param args 日志参数
     */
    protected void logTest(String message, Object... args) {
        log.info(message, args);
    }
}

