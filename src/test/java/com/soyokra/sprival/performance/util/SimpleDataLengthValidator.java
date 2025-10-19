package com.soyokra.sprival.performance.util;

/**
 * 简单的数据长度验证器 用于快速验证测试数据格式
 * 
 * @author Sprival Team
 * @version 1.0
 */
public class SimpleDataLengthValidator {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  测试数据长度验证");
        System.out.println("========================================");
        System.out.println();

        // 数据库字段限制
        final int ORDER_ID_LIMIT = 22;
        final int TRADE_ID_LIMIT = 20;
        final int IDEMPOTENT_ID_LIMIT = 50;

        // 统计
        int totalTests = 10000;
        int maxOrderIdLength = 0;
        int maxTradeIdLength = 0;
        int maxIdempotentIdLength = 0;

        String longestOrderId = "";
        String longestTradeId = "";
        String longestIdempotentId = "";

        System.out.println("【1】开始生成和验证测试数据...");
        System.out.println("测试次数: " + totalTests);
        System.out.println();

        boolean allPassed = true;

        for (int i = 1; i <= totalTests; i++) {
            long timestamp = System.currentTimeMillis();
            String timestampSuffix = String.valueOf(timestamp).substring(3);
            int randomSuffix = (int) (Math.random() * 1000);
            int userId = (int) (Math.random() * 10000) + 1;

            // 生成测试数据
            String orderId =
                    String.format("O%s%d%03d", timestampSuffix, userId % 10000, randomSuffix);
            String tradeId = String.format("T%d%03d", timestamp, randomSuffix);
            String idempotentId = String.format("I%d_%d_%06d", timestamp, userId,
                    (int) (Math.random() * 1000000));

            // 检查长度
            if (orderId.length() > ORDER_ID_LIMIT) {
                System.err.println("❌ order_id 超长: " + orderId + " (长度: " + orderId.length() + " > "
                        + ORDER_ID_LIMIT + ")");
                allPassed = false;
            }
            if (tradeId.length() > TRADE_ID_LIMIT) {
                System.err.println("❌ trade_id 超长: " + tradeId + " (长度: " + tradeId.length() + " > "
                        + TRADE_ID_LIMIT + ")");
                allPassed = false;
            }
            if (idempotentId.length() > IDEMPOTENT_ID_LIMIT) {
                System.err.println("❌ idempotent_id 超长: " + idempotentId + " (长度: "
                        + idempotentId.length() + " > " + IDEMPOTENT_ID_LIMIT + ")");
                allPassed = false;
            }

            // 更新最大长度
            if (orderId.length() > maxOrderIdLength) {
                maxOrderIdLength = orderId.length();
                longestOrderId = orderId;
            }
            if (tradeId.length() > maxTradeIdLength) {
                maxTradeIdLength = tradeId.length();
                longestTradeId = tradeId;
            }
            if (idempotentId.length() > maxIdempotentIdLength) {
                maxIdempotentIdLength = idempotentId.length();
                longestIdempotentId = idempotentId;
            }

            // 显示进度
            if (i % 1000 == 0) {
                System.out.print(".");
            }

            // 短暂休眠，模拟真实场景
            if (i % 100 == 0) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        System.out.println();
        System.out.println();
        System.out.println("【2】验证完成，统计结果：");
        System.out.println("========================================");
        System.out.println();

        // 打印结果
        System.out.println("测试总数: " + totalTests);
        System.out.println();

        System.out.println("order_id:");
        System.out.println("  限制长度: " + ORDER_ID_LIMIT);
        System.out.println("  最大长度: " + maxOrderIdLength);
        System.out.println("  安全余量: " + (ORDER_ID_LIMIT - maxOrderIdLength) + " 字符");
        System.out.println("  最长示例: " + longestOrderId);
        if (maxOrderIdLength <= ORDER_ID_LIMIT) {
            System.out.println("  状态: ✅ 通过");
        } else {
            System.out.println("  状态: ❌ 超出限制");
        }
        System.out.println();

        System.out.println("trade_id:");
        System.out.println("  限制长度: " + TRADE_ID_LIMIT);
        System.out.println("  最大长度: " + maxTradeIdLength);
        System.out.println("  安全余量: " + (TRADE_ID_LIMIT - maxTradeIdLength) + " 字符");
        System.out.println("  最长示例: " + longestTradeId);
        if (maxTradeIdLength <= TRADE_ID_LIMIT) {
            System.out.println("  状态: ✅ 通过");
        } else {
            System.out.println("  状态: ❌ 超出限制");
        }
        System.out.println();

        System.out.println("idempotent_id:");
        System.out.println("  限制长度: " + IDEMPOTENT_ID_LIMIT);
        System.out.println("  最大长度: " + maxIdempotentIdLength);
        System.out.println("  安全余量: " + (IDEMPOTENT_ID_LIMIT - maxIdempotentIdLength) + " 字符");
        System.out.println("  最长示例: " + longestIdempotentId);
        if (maxIdempotentIdLength <= IDEMPOTENT_ID_LIMIT) {
            System.out.println("  状态: ✅ 通过");
        } else {
            System.out.println("  状态: ❌ 超出限制");
        }
        System.out.println();

        System.out.println("========================================");
        if (allPassed) {
            System.out.println("✅ 所有测试通过！数据长度符合数据库字段限制");
        } else {
            System.out.println("❌ 测试失败！存在数据长度超出限制的问题");
            System.exit(1);
        }
        System.out.println("========================================");
        System.out.println();

        // 显示数据格式说明
        System.out.println("【3】数据格式说明：");
        System.out.println();
        System.out.println("order_id 格式: O{10位时间戳}{用户ID}{3位随机}");
        System.out.println("  示例: " + longestOrderId);
        System.out.println();
        System.out.println("trade_id 格式: T{13位时间戳}{3位随机}");
        System.out.println("  示例: " + longestTradeId);
        System.out.println();
        System.out.println("idempotent_id 格式: I{13位时间戳}_{用户ID}_{6位随机}");
        System.out.println("  示例: " + longestIdempotentId);
        System.out.println();

        System.out.println("详细文档: docs/components/performance-testing/DATA-LENGTH-FIX.md");
        System.out.println();
    }
}

