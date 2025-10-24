package com.soyokra.sprival.support.logging;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * KafkaAppender 性能测试
 * 
 * @author sprival
 * @since 2.0.0
 */
@EnabledIfSystemProperty(named = "kafka.performance.test", matches = "true")
class KafkaAppenderPerformanceTest {

    private static final Logger log = LoggerFactory.getLogger(KafkaAppenderPerformanceTest.class);

    private KafkaAppender kafkaAppender;

    @BeforeEach
    void setUp() {
        kafkaAppender = new KafkaAppender();
        kafkaAppender.setBootstrapServers("localhost:9092");
        kafkaAppender.setTopic("test-performance-topic");
        kafkaAppender.setClientId("performance-test-client");
    }

    @Test
    void testSyncModePerformance() throws InterruptedException {
        // 测试同步模式性能
        kafkaAppender.setAsyncMode(false);
        kafkaAppender.setEnableBatching(false);

        kafkaAppender.start();

        int messageCount = 1000;
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < messageCount; i++) {
            log.info("Sync performance test message {}", i);
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        double rate = messageCount * 1000.0 / duration;

        log.info("Sync mode: {} messages in {} ms, rate: {} msg/sec", messageCount, duration, rate);

        // 等待处理完成
        Thread.sleep(2000);

        Map<String, Object> stats = kafkaAppender.getStatistics();
        log.info("Sync mode statistics: {}", stats);

        kafkaAppender.stop();
    }

    @Test
    void testAsyncModePerformance() throws InterruptedException {
        // 测试异步模式性能
        kafkaAppender.setAsyncMode(true);
        kafkaAppender.setQueueCapacity(50000);
        kafkaAppender.setWorkerThreadCount(4);
        kafkaAppender.setEnableBatching(false);

        kafkaAppender.start();

        int messageCount = 10000;
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < messageCount; i++) {
            log.info("Async performance test message {}", i);
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        double rate = messageCount * 1000.0 / duration;

        log.info("Async mode: {} messages in {} ms, rate: {} msg/sec", messageCount, duration,
                rate);

        // 等待异步处理完成
        Thread.sleep(5000);

        Map<String, Object> stats = kafkaAppender.getStatistics();
        log.info("Async mode statistics: {}", stats);

        kafkaAppender.stop();
    }

    @Test
    void testBatchModePerformance() throws InterruptedException {
        // 测试批处理模式性能
        kafkaAppender.setAsyncMode(true);
        kafkaAppender.setQueueCapacity(50000);
        kafkaAppender.setWorkerThreadCount(2);
        kafkaAppender.setEnableBatching(true);
        kafkaAppender.setMaxBatchSize(500);
        kafkaAppender.setBatchTimeoutMs(1000);

        kafkaAppender.start();

        int messageCount = 5000;
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < messageCount; i++) {
            log.info("Batch performance test message {}", i);
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        double rate = messageCount * 1000.0 / duration;

        log.info("Batch mode: {} messages in {} ms, rate: {} msg/sec", messageCount, duration,
                rate);

        // 等待批处理完成
        Thread.sleep(3000);

        Map<String, Object> stats = kafkaAppender.getStatistics();
        log.info("Batch mode statistics: {}", stats);

        kafkaAppender.stop();
    }

    @Test
    void testConcurrentPerformance() throws InterruptedException {
        // 测试并发性能
        kafkaAppender.setAsyncMode(true);
        kafkaAppender.setQueueCapacity(100000);
        kafkaAppender.setWorkerThreadCount(8);
        kafkaAppender.setEnableBatching(true);
        kafkaAppender.setMaxBatchSize(1000);
        kafkaAppender.setBatchTimeoutMs(500);

        kafkaAppender.start();

        int threadCount = 10;
        int messagesPerThread = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicLong totalMessages = new AtomicLong(0);

        long startTime = System.currentTimeMillis();

        for (int t = 0; t < threadCount; t++) {
            final int threadId = t;
            executor.submit(() -> {
                try {
                    for (int i = 0; i < messagesPerThread; i++) {
                        log.info("Concurrent test thread-{} message-{}", threadId, i);
                        totalMessages.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        // 等待所有线程完成
        latch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        long totalSent = totalMessages.get();
        double rate = totalSent * 1000.0 / duration;

        log.info("Concurrent test: {} messages from {} threads in {} ms, rate: {} msg/sec",
                totalSent, threadCount, duration, rate);

        // 等待异步处理完成
        Thread.sleep(10000);

        Map<String, Object> stats = kafkaAppender.getStatistics();
        log.info("Concurrent test statistics: {}", stats);

        kafkaAppender.stop();
    }

    @Test
    void testHighThroughputPerformance() throws InterruptedException {
        // 测试高吞吐量性能
        kafkaAppender.setAsyncMode(true);
        kafkaAppender.setQueueCapacity(200000);
        kafkaAppender.setWorkerThreadCount(16);
        kafkaAppender.setEnableBatching(true);
        kafkaAppender.setMaxBatchSize(2000);
        kafkaAppender.setBatchTimeoutMs(100);
        kafkaAppender.setKafkaBatchSize(131072); // 128KB
        kafkaAppender.setLingerMs(5);
        kafkaAppender.setBufferMemory(134217728L); // 128MB

        kafkaAppender.start();

        int messageCount = 50000;
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < messageCount; i++) {
            log.info("High throughput test message {}", i);
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        double rate = messageCount * 1000.0 / duration;

        log.info("High throughput: {} messages in {} ms, rate: {} msg/sec", messageCount, duration,
                rate);

        // 等待处理完成
        Thread.sleep(15000);

        Map<String, Object> stats = kafkaAppender.getStatistics();
        log.info("High throughput statistics: {}", stats);

        // 检查是否有丢弃的消息
        long droppedEvents = (Long) stats.get("droppedEvents");
        if (droppedEvents > 0) {
            log.warn("Dropped {} events due to queue full", droppedEvents);
        }

        kafkaAppender.stop();
    }

    @Test
    void testMemoryUsagePerformance() throws InterruptedException {
        // 测试内存使用性能
        kafkaAppender.setAsyncMode(true);
        kafkaAppender.setQueueCapacity(10000); // 较小的队列
        kafkaAppender.setWorkerThreadCount(2);
        kafkaAppender.setEnableBatching(true);
        kafkaAppender.setMaxBatchSize(100);
        kafkaAppender.setBatchTimeoutMs(2000);

        kafkaAppender.start();

        // 监控内存使用
        Runtime runtime = Runtime.getRuntime();
        long initialMemory = runtime.totalMemory() - runtime.freeMemory();

        int messageCount = 20000;
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < messageCount; i++) {
            log.info("Memory test message {}", i);

            // 每1000条消息检查一次内存
            if (i % 1000 == 0) {
                long currentMemory = runtime.totalMemory() - runtime.freeMemory();
                long memoryUsed = currentMemory - initialMemory;
                log.info("Memory usage at message {}: {} MB", i, memoryUsed / 1024 / 1024);
            }
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        double rate = messageCount * 1000.0 / duration;

        long finalMemory = runtime.totalMemory() - runtime.freeMemory();
        long totalMemoryUsed = finalMemory - initialMemory;

        log.info("Memory test: {} messages in {} ms, rate: {} msg/sec, memory used: {} MB",
                messageCount, duration, rate, totalMemoryUsed / 1024 / 1024);

        // 等待处理完成
        Thread.sleep(5000);

        Map<String, Object> stats = kafkaAppender.getStatistics();
        log.info("Memory test statistics: {}", stats);

        kafkaAppender.stop();
    }

    @Test
    void testLatencyPerformance() throws InterruptedException {
        // 测试延迟性能
        kafkaAppender.setAsyncMode(false); // 同步模式测试延迟
        kafkaAppender.setEnableBatching(false);
        kafkaAppender.setRequestTimeoutMs(1000);
        kafkaAppender.setDeliveryTimeoutMs(2000);

        kafkaAppender.start();

        int messageCount = 100;
        long[] latencies = new long[messageCount];

        for (int i = 0; i < messageCount; i++) {
            long startTime = System.nanoTime();
            log.info("Latency test message {}", i);
            long endTime = System.nanoTime();
            latencies[i] = (endTime - startTime) / 1000; // 转换为微秒
        }

        // 计算延迟统计
        long totalLatency = 0;
        long minLatency = Long.MAX_VALUE;
        long maxLatency = Long.MIN_VALUE;

        for (long latency : latencies) {
            totalLatency += latency;
            minLatency = Math.min(minLatency, latency);
            maxLatency = Math.max(maxLatency, latency);
        }

        double avgLatency = totalLatency / (double) messageCount;

        log.info("Latency test: {} messages, avg: {} μs, min: {} μs, max: {} μs", messageCount,
                avgLatency, minLatency, maxLatency);

        kafkaAppender.stop();
    }
}
