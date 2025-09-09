package com.soyokra.sprival.config.clickhouse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * ClickHouse健康检查指示器
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "sprival.clickhouse.enabled", havingValue = "true",
                matchIfMissing = true)
public class SprivalClickHouseHealthIndicator implements HealthIndicator {

        @Autowired
        @Qualifier("clickHouseDataSource")
        private DataSource clickHouseDataSource;

        @Autowired
        private SprivalClickHouseProperties properties;

        @Override
        public Health health() {
                try {
                        // 执行健康检查查询
                        try (Connection connection = clickHouseDataSource.getConnection();
                                        PreparedStatement statement = connection.prepareStatement(
                                                        "SELECT version(), now()");
                                        ResultSet resultSet = statement.executeQuery()) {

                                Map<String, Object> details = new HashMap<>();

                                if (resultSet.next()) {
                                        String version = resultSet.getString(1);
                                        String currentTime = resultSet.getString(2);

                                        details.put("status", "UP");
                                        details.put("version", version);
                                        details.put("currentTime", currentTime);
                                        details.put("database", properties.getDatabase());
                                        details.put("host", properties.getHost());
                                        details.put("port", properties.getPort());
                                        details.put("checkTime", LocalDateTime.now().format(
                                                        DateTimeFormatter.ISO_LOCAL_DATE_TIME));

                                        // 检查连接池状态
                                        if (clickHouseDataSource instanceof com.zaxxer.hikari.HikariDataSource) {
                                                com.zaxxer.hikari.HikariDataSource hikariDataSource =
                                                                (com.zaxxer.hikari.HikariDataSource) clickHouseDataSource;
                                                details.put("activeConnections", hikariDataSource
                                                                .getHikariPoolMXBean()
                                                                .getActiveConnections());
                                                details.put("idleConnections", hikariDataSource
                                                                .getHikariPoolMXBean()
                                                                .getIdleConnections());
                                                details.put("totalConnections", hikariDataSource
                                                                .getHikariPoolMXBean()
                                                                .getTotalConnections());
                                        }
                                }

                                return Health.up().withDetails(details).build();
                        }

                } catch (Exception e) {
                        log.error("ClickHouse健康检查失败", e);
                        return Health.down().withDetail("error", e.getMessage())
                                        .withDetail("errorType", e.getClass().getSimpleName())
                                        .withDetail("database", properties.getDatabase())
                                        .withDetail("host", properties.getHost())
                                        .withDetail("port", properties.getPort())
                                        .withDetail("checkTime", LocalDateTime.now().format(
                                                        DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                                        .build();
                }
        }
}
