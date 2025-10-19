package com.soyokra.sprival.performance.util;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import lombok.extern.slf4j.Slf4j;

/**
 * 数据长度验证测试 验证生成的测试数据符合数据库表字段长度限制
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Slf4j
@DisplayName("测试数据长度验证")
public class DataLengthValidationTest {

    /**
     * 测试 order_id 长度限制
     */
    @RepeatedTest(100)
    @DisplayName("验证 order_id 长度不超过 22 字符")
    public void testOrderIdLength() {
        long timestamp = System.currentTimeMillis();
        String timestampSuffix = String.valueOf(timestamp).substring(3);
        int randomSuffix = (int) (Math.random() * 1000);

        // 测试不同的 userId
        for (int userId = 1; userId <= 10000; userId += 1000) {
            String orderId =
                    String.format("O%s%d%03d", timestampSuffix, userId % 10000, randomSuffix);

            int length = orderId.length();
            assertTrue(length <= 22,
                    String.format("order_id 长度超出限制: %d > 22, 值: %s", length, orderId));

            log.debug("order_id: {} (长度: {})", orderId, length);
        }
    }

    /**
     * 测试 trade_id 长度限制
     */
    @RepeatedTest(100)
    @DisplayName("验证 trade_id 长度不超过 20 字符")
    public void testTradeIdLength() {
        long timestamp = System.currentTimeMillis();
        int randomSuffix = (int) (Math.random() * 1000);

        String tradeId = String.format("T%d%03d", timestamp, randomSuffix);

        int length = tradeId.length();
        assertTrue(length <= 20, String.format("trade_id 长度超出限制: %d > 20, 值: %s", length, tradeId));

        log.debug("trade_id: {} (长度: {})", tradeId, length);
    }

    /**
     * 测试 idempotent_id 长度限制
     */
    @RepeatedTest(100)
    @DisplayName("验证 idempotent_id 长度不超过 50 字符")
    public void testIdempotentIdLength() {
        long timestamp = System.currentTimeMillis();

        // 测试不同的 userId
        for (int userId = 1; userId <= 10000; userId += 1000) {
            String idempotentId = String.format("I%d_%d_%06d", timestamp, userId,
                    (int) (Math.random() * 1000000));

            int length = idempotentId.length();
            assertTrue(length <= 50,
                    String.format("idempotent_id 长度超出限制: %d > 50, 值: %s", length, idempotentId));

            log.debug("idempotent_id: {} (长度: {})", idempotentId, length);
        }
    }

    /**
     * 综合测试：模拟压力测试中的数据生成
     */
    @Test
    @DisplayName("模拟压力测试数据生成并验证")
    public void testPerformanceTestDataGeneration() {
        int totalTests = 1000;
        int maxOrderIdLength = 0;
        int maxTradeIdLength = 0;
        int maxIdempotentIdLength = 0;

        log.info("开始模拟 {} 次数据生成...", totalTests);

        for (int i = 1; i <= totalTests; i++) {
            long timestamp = System.currentTimeMillis();
            String timestampSuffix = String.valueOf(timestamp).substring(3);
            int randomSuffix = (int) (Math.random() * 1000);
            int userId = (int) (Math.random() * 10000) + 1;

            // 生成数据
            String orderId =
                    String.format("O%s%d%03d", timestampSuffix, userId % 10000, randomSuffix);
            String tradeId = String.format("T%d%03d", timestamp, randomSuffix);
            String idempotentId = String.format("I%d_%d_%06d", timestamp, userId,
                    (int) (Math.random() * 1000000));

            // 验证长度
            assertTrue(orderId.length() <= 22, "order_id 超长: " + orderId);
            assertTrue(tradeId.length() <= 20, "trade_id 超长: " + tradeId);
            assertTrue(idempotentId.length() <= 50, "idempotent_id 超长: " + idempotentId);

            // 记录最大长度
            maxOrderIdLength = Math.max(maxOrderIdLength, orderId.length());
            maxTradeIdLength = Math.max(maxTradeIdLength, tradeId.length());
            maxIdempotentIdLength = Math.max(maxIdempotentIdLength, idempotentId.length());

            // 短暂休眠，模拟真实场景
            if (i % 100 == 0) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        log.info("========================================");
        log.info("数据生成测试完成");
        log.info("========================================");
        log.info("测试次数: {}", totalTests);
        log.info("order_id 最大长度: {} / 22", maxOrderIdLength);
        log.info("trade_id 最大长度: {} / 20", maxTradeIdLength);
        log.info("idempotent_id 最大长度: {} / 50", maxIdempotentIdLength);
        log.info("========================================");

        // 确保有安全余量
        assertTrue(maxOrderIdLength <= 22, "order_id 最大长度超出限制");
        assertTrue(maxTradeIdLength <= 20, "trade_id 最大长度超出限制");
        assertTrue(maxIdempotentIdLength <= 50, "idempotent_id 最大长度超出限制");

        // 建议至少保留2个字符的安全余量
        assertTrue(maxOrderIdLength <= 20,
                String.format("order_id 最大长度 %d 接近上限，建议优化", maxOrderIdLength));
        assertTrue(maxTradeIdLength <= 18,
                String.format("trade_id 最大长度 %d 接近上限，建议优化", maxTradeIdLength));
        assertTrue(maxIdempotentIdLength <= 48,
                String.format("idempotent_id 最大长度 %d 接近上限，建议优化", maxIdempotentIdLength));
    }

    /**
     * 测试数据唯一性
     */
    @Test
    @DisplayName("验证生成数据的唯一性")
    public void testDataUniqueness() {
        int testCount = 10000;
        java.util.Set<String> orderIds = new java.util.HashSet<>();
        java.util.Set<String> tradeIds = new java.util.HashSet<>();
        java.util.Set<String> idempotentIds = new java.util.HashSet<>();

        log.info("开始测试数据唯一性，生成 {} 条数据...", testCount);

        for (int userId = 1; userId <= testCount; userId++) {
            long timestamp = System.currentTimeMillis();
            String timestampSuffix = String.valueOf(timestamp).substring(3);
            int randomSuffix = (int) (Math.random() * 1000);

            String orderId =
                    String.format("O%s%d%03d", timestampSuffix, userId % 10000, randomSuffix);
            String tradeId = String.format("T%d%03d", timestamp, randomSuffix);
            String idempotentId = String.format("I%d_%d_%06d", timestamp, userId,
                    (int) (Math.random() * 1000000));

            orderIds.add(orderId);
            tradeIds.add(tradeId);
            idempotentIds.add(idempotentId);

            // 短暂休眠确保时间戳变化
            if (userId % 100 == 0) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        log.info("========================================");
        log.info("唯一性测试结果");
        log.info("========================================");
        log.info("生成总数: {}", testCount);
        log.info("order_id 唯一数: {}", orderIds.size());
        log.info("trade_id 唯一数: {}", tradeIds.size());
        log.info("idempotent_id 唯一数: {}", idempotentIds.size());
        log.info("========================================");

        // 由于时间戳+随机数的组合，某些情况下可能有少量重复，但应该很少
        double orderIdUniquenessRate = (double) orderIds.size() / testCount * 100;
        double tradeIdUniquenessRate = (double) tradeIds.size() / testCount * 100;
        double idempotentIdUniquenessRate = (double) idempotentIds.size() / testCount * 100;

        log.info("order_id 唯一率: {:.2f}%", orderIdUniquenessRate);
        log.info("trade_id 唯一率: {:.2f}%", tradeIdUniquenessRate);
        log.info("idempotent_id 唯一率: {:.2f}%", idempotentIdUniquenessRate);

        // idempotent_id 包含了 userId，应该是100%唯一
        assertTrue(idempotentIds.size() == testCount,
                "idempotent_id 应该100%唯一，实际: " + idempotentIdUniquenessRate + "%");

        // order_id 和 trade_id 唯一率应该 > 95%
        assertTrue(orderIdUniquenessRate > 95.0, "order_id 唯一率过低: " + orderIdUniquenessRate + "%");
        assertTrue(tradeIdUniquenessRate > 90.0, "trade_id 唯一率过低: " + tradeIdUniquenessRate + "%");
    }
}

