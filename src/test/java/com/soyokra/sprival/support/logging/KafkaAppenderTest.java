package com.soyokra.sprival.support.logging;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * KafkaAppender测试类
 * 
 * @author sprival
 * @since 1.0.0
 */
@SpringBootTest
@TestPropertySource(properties = {
    "sprival.logging.kafka.enabled=true",
    "sprival.logging.kafka.bootstrap-servers=localhost:9092",
    "sprival.logging.kafka.topic=test-logs",
    "sprival.logging.kafka.client-id=test-kafka-appender"
})
public class KafkaAppenderTest {
    
    private static final Logger logger = LoggerFactory.getLogger(KafkaAppenderTest.class);
    
    @Test
    public void testKafkaAppender() {
        // 测试基本日志输出
        logger.info("这是一条测试日志消息");
        logger.warn("这是一条警告日志消息");
        logger.error("这是一条错误日志消息");
        
        // 测试带参数的日志
        String userId = "12345";
        String action = "login";
        logger.info("用户 {} 执行了 {} 操作", userId, action);
        
        // 测试异常日志
        try {
            throw new RuntimeException("这是一个测试异常");
        } catch (Exception e) {
            logger.error("捕获到异常", e);
        }
        
        // 测试MDC上下文
        MDC.put("userId", "12345");
        MDC.put("sessionId", "session-abc-123");
        logger.info("带有MDC上下文的日志消息");
        MDC.clear();
        
        // 测试不同级别的日志
        logger.debug("调试日志消息");
        logger.info("信息日志消息");
        logger.warn("警告日志消息");
        logger.error("错误日志消息");
    }
    
    @Test
    public void testKafkaAppenderWithCustomFields() {
        // 添加自定义字段到MDC
        MDC.put("service", "user-service");
        MDC.put("version", "1.0.0");
        MDC.put("environment", "test");
        
        logger.info("服务启动完成");
        logger.info("处理用户请求");
        logger.warn("检测到性能问题");
        
        MDC.clear();
    }
    
    @Test
    public void testKafkaAppenderPerformance() {
        long startTime = System.currentTimeMillis();
        
        // 发送大量日志消息测试性能
        for (int i = 0; i < 1000; i++) {
            logger.info("性能测试日志消息 #{}", i);
        }
        
        long endTime = System.currentTimeMillis();
        logger.info("发送1000条日志消息耗时: {}ms", endTime - startTime);
    }
}
