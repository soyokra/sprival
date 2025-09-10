package com.soyokra.sprival.config.mongodb;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.soyokra.sprival.support.health.SprivalBaseHealthIndicator;

/**
 * MongoDB健康检查指示器
 * 支持强依赖和弱依赖模式配置
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Component
@ConditionalOnProperty(name = "sprival.mongodb.enabled", havingValue = "true",
                matchIfMissing = true)
public class SprivalMongoHealthIndicatorV2 extends SprivalBaseHealthIndicator {

    private final MongoTemplate mongoTemplate;
    private final MongoClient mongoClient;
    private final SprivalMongoProperties mongoProperties;

    public SprivalMongoHealthIndicatorV2(MongoTemplate mongoTemplate, 
                                       MongoClient mongoClient,
                                       SprivalMongoProperties mongoProperties) {
        this.mongoTemplate = mongoTemplate;
        this.mongoClient = mongoClient;
        this.mongoProperties = mongoProperties;
    }
    
    @Override
    protected String getComponentName() {
        return "mongodb";
    }
    
    @Override
    protected Health doHealthCheck() {
        try {
            // 执行ping命令检查连接
            MongoDatabase database = mongoClient.getDatabase(mongoProperties.getDatabase());
            database.runCommand(org.bson.Document.parse("{ping: 1}"));

            // 获取数据库统计信息
            org.bson.Document stats = database
                    .runCommand(org.bson.Document.parse("{dbStats: 1}"));

            // 获取服务器状态
            org.bson.Document serverStatus = database
                    .runCommand(org.bson.Document.parse("{serverStatus: 1}"));

            Map<String, Object> details = getHealthCheckDetails();
            details.put("status", "MongoDB连接正常");
            details.put("database", mongoProperties.getDatabase());
            details.put("host", mongoProperties.getHost());
            details.put("port", mongoProperties.getPort());
            details.put("version", serverStatus.getString("version"));
            details.put("uptime", serverStatus.getInteger("uptime"));
            details.put("connections", serverStatus.get("connections", org.bson.Document.class));
            details.put("dbStats", stats);
            
            return createUpHealth(details);

        } catch (Exception e) {
            return handleHealthCheckException(e);
        }
    }
    
    @Override
    protected Map<String, Object> getHealthCheckDetails() {
        Map<String, Object> details = super.getHealthCheckDetails();
        details.put("database", mongoProperties.getDatabase());
        details.put("host", mongoProperties.getHost());
        details.put("port", mongoProperties.getPort());
        return details;
    }
    
    @Override
    protected void beforeHealthCheck() {
        logHealthCheck("DEBUG", "开始执行MongoDB健康检查，数据库: {}", mongoProperties.getDatabase());
    }
    
    @Override
    protected void afterHealthCheck(Health health) {
        if (health.getStatus().equals(org.springframework.boot.actuate.health.Status.UP)) {
            logHealthCheck("DEBUG", "MongoDB健康检查完成，状态正常");
        } else {
            logHealthCheck("WARN", "MongoDB健康检查完成，状态异常: {}", health.getDetails());
        }
    }
    
    @Override
    protected Health handleHealthCheckException(Exception e) {
        Map<String, Object> details = getHealthCheckDetails();
        details.put("status", "MongoDB连接异常");
        details.put("error", e.getMessage());
        details.put("errorType", e.getClass().getSimpleName());
        
        return createDownHealth(details);
    }
}
