package com.soyokra.sprival.config.clickhouse;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "spring.clickhouse")
public class SprivalClickHouseProperties {
    private String database;
}
