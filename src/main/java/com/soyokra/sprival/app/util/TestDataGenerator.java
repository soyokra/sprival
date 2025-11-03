package com.soyokra.sprival.app.util;

import com.soyokra.sprival.app.repository.db.test.model.TestOrder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import java.util.UUID;

/**
 * 测试数据生成工具类
 * 
 * 用于生成性能测试所需的测试数据
 *
 * @author soyokra
 * @since 2025-11-03
 */
public class TestDataGenerator {

    private static final Random RANDOM = new Random();
    
    private static final String[] PRODUCT_NAMES = {
        "iPhone 15 Pro", "MacBook Pro", "iPad Air", "AirPods Pro",
        "Samsung Galaxy S24", "小米14", "华为Mate 60", "OPPO Find X7",
        "戴尔XPS 13", "ThinkPad X1", "Surface Pro", "Sony WH-1000XM5"
    };
    
    private static final String[] PAYMENT_METHODS = {
        "支付宝", "微信支付", "银行卡", "信用卡", "花呗", "京东支付"
    };
    
    private static final String[] ADDRESSES = {
        "北京市朝阳区建国门外大街1号",
        "上海市浦东新区世纪大道88号",
        "广州市天河区天河路208号",
        "深圳市南山区科技园南区",
        "杭州市西湖区文三路90号",
        "成都市高新区天府大道中段500号",
        "武汉市洪山区珞瑜路号",
        "南京市玄武区中山路1号"
    };

    /**
     * 生成测试订单数据
     *
     * @param userId 用户ID
     * @return 测试订单
     */
    public static TestOrder generateTestOrder(Long userId) {
        TestOrder order = new TestOrder();
        
        // 生成唯一订单号（格式：ORD + 纳秒时间戳 + 线程ID + 随机数）
        // 使用纳秒时间戳 + 线程ID 确保高并发下订单号唯一
        long nanoTime = System.nanoTime();
        long threadId = Thread.currentThread().getId();
        int random = RANDOM.nextInt(1000);
        String orderNo = String.format("ORD%d%03d%03d", nanoTime % 1000000000000L, threadId % 1000, random);
        order.setOrderNo(orderNo);
        
        // 用户ID
        order.setUserId(userId);
        
        // 商品信息
        Long productId = (long) (RANDOM.nextInt(1000) + 1);
        order.setProductId(productId);
        order.setProductName(PRODUCT_NAMES[RANDOM.nextInt(PRODUCT_NAMES.length)]);
        
        // 数量和价格
        int quantity = RANDOM.nextInt(5) + 1;
        BigDecimal unitPrice = BigDecimal.valueOf(RANDOM.nextDouble() * 9000 + 1000)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalAmount = unitPrice.multiply(BigDecimal.valueOf(quantity))
                .setScale(2, RoundingMode.HALF_UP);
        
        order.setQuantity(quantity);
        order.setUnitPrice(unitPrice);
        order.setTotalAmount(totalAmount);
        
        // 订单状态（随机）
        order.setStatus(RANDOM.nextInt(5));
        
        // 支付方式（有50%概率为空）
        if (RANDOM.nextBoolean()) {
            order.setPaymentMethod(PAYMENT_METHODS[RANDOM.nextInt(PAYMENT_METHODS.length)]);
        }
        
        // 收货地址
        order.setShippingAddress(ADDRESSES[RANDOM.nextInt(ADDRESSES.length)]);
        
        // 备注（30%概率有备注）
        if (RANDOM.nextDouble() < 0.3) {
            order.setRemark("测试订单 " + UUID.randomUUID().toString().substring(0, 8));
        }
        
        return order;
    }

    /**
     * 生成指定长度的随机字符串
     *
     * @param length 长度
     * @return 随机字符串
     */
    public static String randomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(RANDOM.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /**
     * 生成随机整数
     *
     * @param min 最小值
     * @param max 最大值
     * @return 随机整数
     */
    public static int randomInt(int min, int max) {
        return RANDOM.nextInt(max - min + 1) + min;
    }

    /**
     * 生成随机价格
     *
     * @param min 最小价格
     * @param max 最大价格
     * @return 随机价格
     */
    public static BigDecimal randomPrice(double min, double max) {
        double randomValue = min + (max - min) * RANDOM.nextDouble();
        return BigDecimal.valueOf(randomValue).setScale(2, RoundingMode.HALF_UP);
    }
}

