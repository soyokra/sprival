package com.soyokra.sprival.performance.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

/**
 * 性能测试工具类
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Slf4j
public class PerformanceTestUtils {

    /**
     * 性能测试结果
     */
    public static class PerformanceResult {
        private String testName;
        private long totalRequests;
        private long successRequests;
        private long failedRequests;
        private long totalDurationMs;
        private long minResponseTimeMs;
        private long maxResponseTimeMs;
        private double avgResponseTimeMs;
        private double tps;
        private double successRate;

        // Getters and Setters
        public String getTestName() {
            return testName;
        }

        public void setTestName(String testName) {
            this.testName = testName;
        }

        public long getTotalRequests() {
            return totalRequests;
        }

        public void setTotalRequests(long totalRequests) {
            this.totalRequests = totalRequests;
        }

        public long getSuccessRequests() {
            return successRequests;
        }

        public void setSuccessRequests(long successRequests) {
            this.successRequests = successRequests;
        }

        public long getFailedRequests() {
            return failedRequests;
        }

        public void setFailedRequests(long failedRequests) {
            this.failedRequests = failedRequests;
        }

        public long getTotalDurationMs() {
            return totalDurationMs;
        }

        public void setTotalDurationMs(long totalDurationMs) {
            this.totalDurationMs = totalDurationMs;
        }

        public long getMinResponseTimeMs() {
            return minResponseTimeMs;
        }

        public void setMinResponseTimeMs(long minResponseTimeMs) {
            this.minResponseTimeMs = minResponseTimeMs;
        }

        public long getMaxResponseTimeMs() {
            return maxResponseTimeMs;
        }

        public void setMaxResponseTimeMs(long maxResponseTimeMs) {
            this.maxResponseTimeMs = maxResponseTimeMs;
        }

        public double getAvgResponseTimeMs() {
            return avgResponseTimeMs;
        }

        public void setAvgResponseTimeMs(double avgResponseTimeMs) {
            this.avgResponseTimeMs = avgResponseTimeMs;
        }

        public double getTps() {
            return tps;
        }

        public void setTps(double tps) {
            this.tps = tps;
        }

        public double getSuccessRate() {
            return successRate;
        }

        public void setSuccessRate(double successRate) {
            this.successRate = successRate;
        }
    }

    /**
     * 计算性能测试结果
     */
    public static PerformanceResult calculateResult(String testName, List<Long> responseTimes,
            long totalDurationMs, long failedCount) {

        PerformanceResult result = new PerformanceResult();
        result.setTestName(testName);
        result.setTotalRequests(responseTimes.size() + failedCount);
        result.setSuccessRequests(responseTimes.size());
        result.setFailedRequests(failedCount);
        result.setTotalDurationMs(totalDurationMs);

        if (!responseTimes.isEmpty()) {
            result.setMinResponseTimeMs(responseTimes.stream().min(Long::compare).orElse(0L));
            result.setMaxResponseTimeMs(responseTimes.stream().max(Long::compare).orElse(0L));
            result.setAvgResponseTimeMs(
                    responseTimes.stream().mapToLong(Long::longValue).average().orElse(0.0));
        }

        double durationSeconds = totalDurationMs / 1000.0;
        result.setTps(result.getTotalRequests() / durationSeconds);
        result.setSuccessRate(
                (double) result.getSuccessRequests() / result.getTotalRequests() * 100);

        return result;
    }

    /**
     * 打印性能测试结果
     */
    public static void printResult(PerformanceResult result) {
        log.info("========================================");
        log.info("性能测试结果: {}", result.getTestName());
        log.info("========================================");
        log.info("总请求数: {}", result.getTotalRequests());
        log.info("成功请求: {}", result.getSuccessRequests());
        log.info("失败请求: {}", result.getFailedRequests());
        log.info("成功率: {:.2f}%", result.getSuccessRate());
        log.info("----------------------------------------");
        log.info("最小响应时间: {} ms", result.getMinResponseTimeMs());
        log.info("最大响应时间: {} ms", result.getMaxResponseTimeMs());
        log.info("平均响应时间: {:.2f} ms", result.getAvgResponseTimeMs());
        log.info("----------------------------------------");
        log.info("TPS (每秒事务数): {:.2f}", result.getTps());
        log.info("总耗时: {} ms ({} s)", result.getTotalDurationMs(),
                result.getTotalDurationMs() / 1000);
        log.info("========================================");
    }

    /**
     * 保存性能测试报告
     */
    public static void saveReport(PerformanceResult result, String outputDir) {
        try {
            File dir = new File(outputDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = String.format("%s_%s.txt", result.getTestName(), timestamp);
            File reportFile = new File(dir, fileName);

            try (FileWriter writer = new FileWriter(reportFile)) {
                writer.write("========================================\n");
                writer.write(String.format("性能测试报告: %s\n", result.getTestName()));
                writer.write(String.format("测试时间: %s\n",
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));
                writer.write("========================================\n\n");

                writer.write("测试结果:\n");
                writer.write(String.format("  总请求数: %d\n", result.getTotalRequests()));
                writer.write(String.format("  成功请求: %d\n", result.getSuccessRequests()));
                writer.write(String.format("  失败请求: %d\n", result.getFailedRequests()));
                writer.write(String.format("  成功率: %.2f%%\n\n", result.getSuccessRate()));

                writer.write("响应时间:\n");
                writer.write(String.format("  最小: %d ms\n", result.getMinResponseTimeMs()));
                writer.write(String.format("  最大: %d ms\n", result.getMaxResponseTimeMs()));
                writer.write(String.format("  平均: %.2f ms\n\n", result.getAvgResponseTimeMs()));

                writer.write("吞吐量:\n");
                writer.write(String.format("  TPS: %.2f 请求/秒\n", result.getTps()));
                writer.write(String.format("  总耗时: %d ms (%.2f s)\n", result.getTotalDurationMs(),
                        result.getTotalDurationMs() / 1000.0));
            }

            log.info("性能测试报告已保存到: {}", reportFile.getAbsolutePath());
        } catch (IOException e) {
            log.error("保存性能测试报告失败", e);
        }
    }

    /**
     * 休眠指定时间
     */
    public static void sleep(long duration, TimeUnit unit) {
        try {
            unit.sleep(duration);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Sleep interrupted", e);
        }
    }
}

