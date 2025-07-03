package com.soyokra.sprival.config.rabbit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.actuate.amqp.RabbitHealthIndicator;
import org.springframework.boot.actuate.health.Health;

/**
 * 健康检查，指标监控
 */
@Slf4j
public class SprivalRabbitHealthIndicator extends RabbitHealthIndicator {

    public SprivalRabbitHealthIndicator(RabbitTemplate rabbitTemplate) {
        super(rabbitTemplate);
    }

    protected void doHealthCheck(Health.Builder builder) throws Exception {
        // 全局降级
        // 异常告警
        try {
            super.doHealthCheck(builder);
        } catch (Exception e) {
            // 预警
            log.error(e.getMessage(), e);
        }
    }
}
