package com.soyokra.sprival.base;

import org.openjdk.jmh.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 性能测试基类
 * 
 * <p>用途：JMH性能基准测试基类</p>
 * 
 * <p>功能：</p>
 * <ul>
 *   <li>提供JMH基准测试默认配置</li>
 *   <li>统一的预热和测量参数</li>
 *   <li>标准的测试模式设置</li>
 * </ul>
 * 
 * <p>使用示例：</p>
 * <pre>{@code
 * @BenchmarkMode(Mode.Throughput)
 * @OutputTimeUnit(TimeUnit.SECONDS)
 * @State(Scope.Benchmark)
 * public class OrderServiceBenchmark extends BasePerformanceTest {
 *     
 *     @Benchmark
 *     public void benchmarkGetOrder() {
 *         // 性能测试代码
 *     }
 *     
 *     public static void main(String[] args) throws RunnerException {
 *         Options opt = new OptionsBuilder()
 *                 .include(OrderServiceBenchmark.class.getSimpleName())
 *                 .build();
 *         new Runner(opt).run();
 *     }
 * }
 * }</pre>
 * 
 * <p>默认配置：</p>
 * <ul>
 *   <li>测试模式：吞吐量（Throughput）</li>
 *   <li>预热：3次迭代，每次1秒</li>
 *   <li>测量：5次迭代，每次1秒</li>
 *   <li>Fork：1次</li>
 * </ul>
 * 
 * @author sprival-test-framework
 * @since 2025-11-03
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Benchmark)
public abstract class BasePerformanceTest {
    
    protected final Logger log = LoggerFactory.getLogger(getClass());
    
    /**
     * 基准测试初始化
     * 
     * <p>在整个测试开始前执行一次</p>
     */
    @Setup(Level.Trial)
    public void setup() {
        log.info("初始化性能测试: {}", getClass().getSimpleName());
    }
    
    /**
     * 基准测试清理
     * 
     * <p>在整个测试结束后执行一次</p>
     */
    @TearDown(Level.Trial)
    public void tearDown() {
        log.info("清理性能测试: {}", getClass().getSimpleName());
    }
}

