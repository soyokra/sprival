package com.soyokra.sprival.support.logging;

import java.util.concurrent.atomic.AtomicLong;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

/**
 * KafkaAppender 监控指标集成
 * 
 * @author sprival
 * @since 2.0.0
 */
public class KafkaAppenderMetrics {

    private final MeterRegistry meterRegistry;
    private final String appenderName;

    // 计数器指标
    private final Counter totalEventsCounter;
    private final Counter successfulEventsCounter;
    private final Counter failedEventsCounter;
    private final Counter droppedEventsCounter;

    // 计时器指标
    private final Timer eventProcessingTimer;
    private final Timer batchProcessingTimer;

    // 仪表指标
    private final AtomicLong queueSizeGauge;
    private final AtomicLong batchBufferSizeGauge;

    public KafkaAppenderMetrics(String appenderName, MeterRegistry meterRegistry) {
        this.appenderName = appenderName;
        this.meterRegistry = meterRegistry != null ? meterRegistry : new SimpleMeterRegistry();

        // 初始化计数器
        this.totalEventsCounter = Counter.builder("kafka.appender.events.total")
                .description("Total number of events processed by KafkaAppender")
                .tag("appender", appenderName).register(this.meterRegistry);

        this.successfulEventsCounter = Counter.builder("kafka.appender.events.successful")
                .description("Number of successfully sent events").tag("appender", appenderName)
                .register(this.meterRegistry);

        this.failedEventsCounter = Counter.builder("kafka.appender.events.failed")
                .description("Number of failed events").tag("appender", appenderName)
                .register(this.meterRegistry);

        this.droppedEventsCounter = Counter.builder("kafka.appender.events.dropped")
                .description("Number of dropped events due to queue full")
                .tag("appender", appenderName).register(this.meterRegistry);

        // 初始化计时器
        this.eventProcessingTimer = Timer.builder("kafka.appender.processing.time")
                .description("Time taken to process events").tag("appender", appenderName)
                .register(this.meterRegistry);

        this.batchProcessingTimer = Timer.builder("kafka.appender.batch.processing.time")
                .description("Time taken to process batches").tag("appender", appenderName)
                .register(this.meterRegistry);

        // 初始化仪表
        this.queueSizeGauge = new AtomicLong(0);
        this.batchBufferSizeGauge = new AtomicLong(0);

        Gauge.builder("kafka.appender.queue.size", this.queueSizeGauge, AtomicLong::doubleValue)
                .description("Current queue size").tag("appender", appenderName)
                .register(this.meterRegistry);

        Gauge.builder("kafka.appender.batch.buffer.size", this.batchBufferSizeGauge,
                AtomicLong::doubleValue).description("Current batch buffer size")
                .tag("appender", appenderName).register(this.meterRegistry);
    }

    /**
     * 记录总事件数
     */
    public void incrementTotalEvents() {
        totalEventsCounter.increment();
    }

    /**
     * 记录成功事件数
     */
    public void incrementSuccessfulEvents() {
        successfulEventsCounter.increment();
    }

    /**
     * 记录失败事件数
     */
    public void incrementFailedEvents() {
        failedEventsCounter.increment();
    }

    /**
     * 记录丢弃事件数
     */
    public void incrementDroppedEvents() {
        droppedEventsCounter.increment();
    }

    /**
     * 记录事件处理时间
     */
    public io.micrometer.core.instrument.Timer.Sample startEventProcessingTimer() {
        return Timer.start(meterRegistry);
    }

    /**
     * 停止事件处理计时器
     */
    public void stopEventProcessingTimer(io.micrometer.core.instrument.Timer.Sample sample) {
        sample.stop(eventProcessingTimer);
    }

    /**
     * 记录批次处理时间
     */
    public io.micrometer.core.instrument.Timer.Sample startBatchProcessingTimer() {
        return Timer.start(meterRegistry);
    }

    /**
     * 停止批次处理计时器
     */
    public void stopBatchProcessingTimer(io.micrometer.core.instrument.Timer.Sample sample) {
        sample.stop(batchProcessingTimer);
    }

    /**
     * 更新队列大小
     */
    public void updateQueueSize(long size) {
        queueSizeGauge.set(size);
    }

    /**
     * 更新批次缓冲区大小
     */
    public void updateBatchBufferSize(long size) {
        batchBufferSizeGauge.set(size);
    }

    /**
     * 获取 MeterRegistry
     */
    public MeterRegistry getMeterRegistry() {
        return meterRegistry;
    }

    /**
     * 获取应用名称
     */
    public String getAppenderName() {
        return appenderName;
    }
}
