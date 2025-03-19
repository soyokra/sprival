package com.soyokra.sprival.support.ck;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "spring.clickhouse")
public class ClickHouseProperties {
    private String user;

    private String password;

    private String database;

    private String url;

}
