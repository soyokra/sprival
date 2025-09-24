package com.soyokra.sprival.support.logging;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * KafkaAppender自动配置类
 * 
 * @author sprival
 * @since 1.0.0
 */
@Configuration
@ConditionalOnProperty(prefix = "sprival.logging.kafka", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(KafkaAppenderProperties.class)
public class KafkaAppenderAutoConfiguration {
    
    // 配置类，主要用于启用配置属性
    // 实际的KafkaAppender配置通过logback.xml进行
}
