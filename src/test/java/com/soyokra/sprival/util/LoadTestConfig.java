package com.soyokra.sprival.util;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 压力测试配置类
 * 
 * <p>用于配置压力测试的各项参数</p>
 * 
 * <p>配置项说明：</p>
 * <ul>
 *   <li>url: 测试目标URL（必填）</li>
 *   <li>httpMethod: HTTP方法，默认POST</li>
 *   <li>concurrentThreads: 并发线程数，默认10</li>
 *   <li>durationSeconds: 测试持续时间（秒），默认60</li>
 *   <li>warmupSeconds: 预热时间（秒），默认10</li>
 *   <li>requestBodySupplier: 请求体生成器（可选）</li>
 *   <li>headers: 请求头（可选）</li>
 * </ul>
 * 
 * <p>使用示例：</p>
 * <pre>{@code
 * LoadTestConfig config = LoadTestConfig.builder()
 *         .url("http://localhost:8338/api/order/insert")
 *         .httpMethod("POST")
 *         .concurrentThreads(10)
 *         .durationSeconds(60)
 *         .warmupSeconds(10)
 *         .requestBodySupplier(() -> createOrderRequest())
 *         .header("Content-Type", "application/json")
 *         .build();
 * }</pre>
 * 
 * @author sprival-test-framework
 * @since 2025-11-03
 */
@Data
@Builder
public class LoadTestConfig {
    
    /**
     * 测试目标URL
     */
    private String url;
    
    /**
     * HTTP方法
     */
    @Builder.Default
    private String httpMethod = "POST";
    
    /**
     * 并发线程数
     */
    @Builder.Default
    private int concurrentThreads = 10;
    
    /**
     * 测试持续时间（秒）
     */
    @Builder.Default
    private int durationSeconds = 60;
    
    /**
     * 预热时间（秒）
     */
    @Builder.Default
    private int warmupSeconds = 10;
    
    /**
     * 请求体生成器
     * 
     * <p>每次请求调用一次，可用于生成唯一的请求数据</p>
     */
    private Supplier<String> requestBodySupplier;
    
    /**
     * 请求头
     */
    @Builder.Default
    private Map<String, String> headers = new HashMap<>();
    
    /**
     * 连接超时时间（毫秒）
     */
    @Builder.Default
    private int connectTimeoutMs = 5000;
    
    /**
     * 读取超时时间（毫秒）
     */
    @Builder.Default
    private int readTimeoutMs = 30000;
    
    /**
     * 添加请求头的便捷方法
     * 
     * @param name 请求头名称
     * @param value 请求头值
     * @return 当前配置对象
     */
    public LoadTestConfig addHeader(String name, String value) {
        if (this.headers == null) {
            this.headers = new HashMap<>();
        }
        this.headers.put(name, value);
        return this;
    }
    
    /**
     * 自定义Builder，添加便捷方法
     */
    public static class LoadTestConfigBuilder {
        /**
         * 添加请求头
         * 
         * @param name 请求头名称
         * @param value 请求头值
         * @return Builder对象
         */
        public LoadTestConfigBuilder header(String name, String value) {
            if (this.headers$value == null) {
                this.headers$value = new HashMap<>();
            }
            this.headers$value.put(name, value);
            return this;
        }
    }
}

