package com.soyokra.sprival.config.mongodb;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.mongo.MongoHealthIndicator;
import org.springframework.data.mongodb.core.MongoTemplate;

@Slf4j
public class SprivalMongoHealthIndicator extends MongoHealthIndicator {

    public SprivalMongoHealthIndicator(MongoTemplate mongoTemplate) {
        super(mongoTemplate);
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) {
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
