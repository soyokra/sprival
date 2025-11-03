package com.soyokra.sprival.fixture;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 测试数据构造器
 * 
 * <p>提供生成测试数据的通用工具方法</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>生成唯一ID</li>
 *   <li>生成时间戳</li>
 *   <li>生成测试字符串</li>
 * </ul>
 * 
 * <p>使用示例：</p>
 * <pre>{@code
 * // 生成唯一ID
 * String orderId = TestDataBuilder.generateId("ORDER");
 * 
 * // 获取当前时间
 * LocalDateTime now = TestDataBuilder.now();
 * 
 * // 生成序列号
 * long seq = TestDataBuilder.nextSequence();
 * }</pre>
 * 
 * @author sprival-test-framework
 * @since 2025-11-03
 */
public final class TestDataBuilder {
    
    private static final AtomicLong sequence = new AtomicLong(0);
    
    private TestDataBuilder() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }
    
    /**
     * 生成唯一ID
     * 
     * <p>根据不同前缀生成符合数据库字段长度限制的唯一ID</p>
     * <ul>
     *   <li>ORDER: 13位时间戳 + 3位线程 + 6位序列 = 22字符</li>
     *   <li>TRADE: 13位时间戳 + 3位线程 + 4位序列 = 20字符</li>
     *   <li>USER: 13位时间戳 + 3位线程 + 4位序列 = 20字符</li>
     *   <li>其他: prefix + 时间戳等</li>
     * </ul>
     * 
     * @param prefix ID前缀
     * @return 唯一ID
     */
    public static String generateId(String prefix) {
        long timestamp = System.currentTimeMillis();
        long threadId = Thread.currentThread().getId() % 1000;  // 3位线程ID
        long seq = sequence.incrementAndGet();
        
        // 根据不同的前缀生成不同长度的ID，确保符合数据库字段限制
        switch (prefix) {
            case "ORDER":
                // order_id: varchar(22)
                // 格式: 13位时间戳 + 3位线程 + 6位序列 = 22字符
                return String.format("%013d%03d%06d", timestamp, threadId, seq % 1000000);
                
            case "TRADE":
                // trade_id: varchar(20)
                // 格式: 13位时间戳 + 3位线程 + 4位序列 = 20字符
                return String.format("%013d%03d%04d", timestamp, threadId, seq % 10000);
                
            case "USER":
                // user_id: varchar(20)
                // 格式: 13位时间戳 + 3位线程 + 4位序列 = 20字符
                return String.format("%013d%03d%04d", timestamp, threadId, seq % 10000);
                
            case "PARTNER":
                // partner_id: varchar(50)
                // 格式: PARTNER + 13位时间戳 + 3位线程 + 6位序列 = 29字符
                return String.format("%s%013d%03d%06d", prefix, timestamp, threadId, seq % 1000000);
                
            case "SUPPLIER":
                // supplier_id: varchar(64)
                // 格式: SUPPLIER + 13位时间戳 + 3位线程 + 6位序列 = 30字符
                return String.format("%s%013d%03d%06d", prefix, timestamp, threadId, seq % 1000000);
                
            default:
                // 默认格式: prefix + 13位时间戳 + 3位线程 + 6位序列
                return String.format("%s%013d%03d%06d", prefix, timestamp, threadId, seq % 1000000);
        }
    }
    
    /**
     * 生成唯一ID（无前缀）
     * 
     * <p>格式: 13位时间戳 + 3位线程ID + 6位序列号 = 22字符</p>
     * <p>示例: 1730610123045001000001</p>
     * 
     * @return 唯一ID
     */
    public static String generateId() {
        long timestamp = System.currentTimeMillis();
        long threadId = Thread.currentThread().getId() % 1000;
        long seq = sequence.incrementAndGet() % 1000000;
        return String.format("%013d%03d%06d", timestamp, threadId, seq);
    }
    
    /**
     * 生成UUID
     * 
     * @return UUID字符串（32字符）
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    /**
     * 生成带前缀的UUID
     * 
     * <p>针对idempotent_id字段（varchar(50)）优化</p>
     * <p>格式: 16位UUID片段（取前16位） + 时间戳后6位 = 22字符</p>
     * 
     * @param prefix 前缀（仅用于标识，实际不加入ID中）
     * @return UUID字符串
     */
    public static String generateUUID(String prefix) {
        // idempotent_id: varchar(50)，生成一个较短的唯一ID
        String uuid = UUID.randomUUID().toString().replace("-", "");
        long timestamp = System.currentTimeMillis() % 1000000;  // 取后6位
        // 16位UUID + 6位时间戳 = 22字符（符合50字符限制）
        return uuid.substring(0, 16) + String.format("%06d", timestamp);
    }
    
    /**
     * 获取当前时间
     * 
     * @return 当前时间
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }
    
    /**
     * 生成测试字符串
     * 
     * @param prefix 前缀
     * @return 测试字符串
     */
    public static String generateString(String prefix) {
        return prefix + "_" + System.currentTimeMillis();
    }
    
    /**
     * 获取下一个序列号
     * 
     * @return 序列号
     */
    public static long nextSequence() {
        return sequence.incrementAndGet();
    }
    
    /**
     * 重置序列号
     */
    public static void resetSequence() {
        sequence.set(0);
    }
}

