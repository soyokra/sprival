package com.soyokra.sprival.config.kafka;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import com.soyokra.sprival.support.health.SprivalBaseHealthIndicator;

/**
 * Sprival Kafka 健康检查指示器
 * 支持强依赖和弱依赖模式配置
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Component
@ConditionalOnProperty(prefix = "sprival.kafka.monitor", name = "healthCheckEnabled",
        havingValue = "true", matchIfMissing = true)
public class SprivalKafkaHealthIndicatorV2 extends SprivalBaseHealthIndicator {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final SprivalKafkaProperties kafkaProperties;

    public SprivalKafkaHealthIndicatorV2(KafkaTemplate<String, Object> kafkaTemplate,
                                       SprivalKafkaProperties kafkaProperties) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaProperties = kafkaProperties;
    }
    
    @Override
    protected String getComponentName() {
        return "kafka";
    }
    
    @Override
    protected Health doHealthCheck() {
        try {
            // 检查Kafka连接状态
            if (kafkaTemplate == null) {
                Map<String, Object> details = getHealthCheckDetails();
                details.put("kafka", "KafkaTemplate not available");
                return createDownHealth(details);
            }

            // 使用AdminClient检查Kafka集群状态
            Properties props = new Properties();
            props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
            props.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG,
                    kafkaProperties.getMonitor().getHealthCheckTimeout());

            try (AdminClient adminClient = AdminClient.create(props)) {
                // 尝试列出主题来验证连接
                ListTopicsResult listTopics = adminClient.listTopics();
                listTopics.names().get(kafkaProperties.getMonitor().getHealthCheckTimeout(),
                        TimeUnit.MILLISECONDS);

                Map<String, Object> details = getHealthCheckDetails();
                details.put("kafka", "Available");
                details.put("bootstrap-servers", "localhost:9092");
                details.put("producer-enabled", kafkaProperties.getProducer().isEnabled());
                details.put("consumer-enabled", kafkaProperties.getConsumer().isEnabled());
                details.put("consumer-group", kafkaProperties.getConsumer().getGroupId());
                
                return createUpHealth(details);
            }

        } catch (Exception e) {
            return handleHealthCheckException(e);
        }
    }
    
    @Override
    protected Map<String, Object> getHealthCheckDetails() {
        Map<String, Object> details = super.getHealthCheckDetails();
        details.put("bootstrap-servers", "localhost:9092");
        details.put("producer-enabled", kafkaProperties.getProducer().isEnabled());
        details.put("consumer-enabled", kafkaProperties.getConsumer().isEnabled());
        details.put("consumer-group", kafkaProperties.getConsumer().getGroupId());
        return details;
    }
    
    @Override
    protected void beforeHealthCheck() {
        logHealthCheck("DEBUG", "开始执行Kafka健康检查，Bootstrap服务器: localhost:9092");
    }
    
    @Override
    protected void afterHealthCheck(Health health) {
        if (health.getStatus().equals(org.springframework.boot.actuate.health.Status.UP)) {
            logHealthCheck("DEBUG", "Kafka健康检查完成，状态正常");
        } else {
            logHealthCheck("WARN", "Kafka健康检查完成，状态异常: {}", health.getDetails());
        }
    }
    
    @Override
    protected Health handleHealthCheckException(Exception e) {
        Map<String, Object> details = getHealthCheckDetails();
        details.put("kafka", "Unavailable");
        details.put("error", e.getMessage());
        
        return createDownHealth(details);
    }
}
