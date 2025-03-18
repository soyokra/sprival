package com.soyokra.sprival.config.rabbit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.actuate.amqp.RabbitHealthIndicator;
import org.springframework.boot.actuate.health.Health;

@Slf4j
public class RabbitHealthPlusIndicator extends RabbitHealthIndicator {

    public RabbitHealthPlusIndicator(RabbitTemplate rabbitTemplate) {
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
