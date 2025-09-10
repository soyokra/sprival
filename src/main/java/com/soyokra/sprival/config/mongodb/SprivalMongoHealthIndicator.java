package com.soyokra.sprival.config.mongodb;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.soyokra.sprival.support.health.SprivalHealthManager;
import lombok.extern.slf4j.Slf4j;

/**
 * MongoDB健康检查指示器
 * 支持强依赖和弱依赖模式配置
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "sprival.mongodb.enabled", havingValue = "true",
                matchIfMissing = true)
public class SprivalMongoHealthIndicator implements HealthIndicator {

        @Autowired
        private MongoTemplate mongoTemplate;

        @Autowired
        private MongoClient mongoClient;

        @Autowired
        private SprivalMongoProperties mongoProperties;
        
        @Autowired(required = false)
        private SprivalHealthManager healthManager;

        @Override
        public Health health() {
                // 如果启用了健康管理器，使用强依赖/弱依赖模式
                if (healthManager != null) {
                        return healthManager.checkComponentHealth("mongodb", this::performMongoHealthCheck);
                }
                
                // 否则使用默认的健康检查逻辑
                return performMongoHealthCheck();
        }
        
        /**
         * 执行MongoDB健康检查
         */
        private Health performMongoHealthCheck() {
                try {
                        // 执行ping命令检查连接
                        MongoDatabase database =
                                        mongoClient.getDatabase(mongoProperties.getDatabase());
                        database.runCommand(org.bson.Document.parse("{ping: 1}"));

                        // 获取数据库统计信息
                        org.bson.Document stats = database
                                        .runCommand(org.bson.Document.parse("{dbStats: 1}"));

                        // 获取服务器状态
                        org.bson.Document serverStatus = database
                                        .runCommand(org.bson.Document.parse("{serverStatus: 1}"));

                        return Health.up().withDetail("status", "MongoDB连接正常")
                                        .withDetail("database", mongoProperties.getDatabase())
                                        .withDetail("host", mongoProperties.getHost())
                                        .withDetail("port", mongoProperties.getPort())
                                        .withDetail("checkTime", LocalDateTime.now().format(
                                                        DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                                        .withDetail("version", serverStatus.getString("version"))
                                        .withDetail("uptime", serverStatus.getInteger("uptime"))
                                        .withDetail("connections",
                                                        serverStatus.get("connections",
                                                                        org.bson.Document.class))
                                        .withDetail("dbStats", stats).build();

                } catch (Exception e) {
                        log.error("MongoDB健康检查失败: {}", e.getMessage(), e);

                        return Health.down().withDetail("status", "MongoDB连接异常")
                                        .withDetail("database", mongoProperties.getDatabase())
                                        .withDetail("host", mongoProperties.getHost())
                                        .withDetail("port", mongoProperties.getPort())
                                        .withDetail("checkTime", LocalDateTime.now().format(
                                                        DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                                        .withDetail("error", e.getMessage())
                                        .withDetail("errorType", e.getClass().getSimpleName())
                                        .build();
                }
        }
}
