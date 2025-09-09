package com.soyokra.sprival.config.elasticsearch;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * Elasticsearch健康检查指示器
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "sprival.elasticsearch.enabled", havingValue = "true",
        matchIfMissing = true)
public class SprivalElasticsearchHealthIndicator implements HealthIndicator {

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Override
    public Health health() {
        try {
            // 简单的健康检查 - 尝试执行一个简单的操作
            elasticsearchOperations.indexOps(IndexCoordinates.of("test")).exists();

            Map<String, Object> details = new HashMap<>();
            details.put("status", "UP");
            details.put("message", "Elasticsearch连接正常");
            details.put("checkTime",
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            return Health.up().withDetails(details).build();

        } catch (Exception e) {
            log.error("Elasticsearch健康检查失败", e);
            return Health.down().withDetail("error", e.getMessage())
                    .withDetail("errorType", e.getClass().getSimpleName())
                    .withDetail("checkTime",
                            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .build();
        }
    }
}
