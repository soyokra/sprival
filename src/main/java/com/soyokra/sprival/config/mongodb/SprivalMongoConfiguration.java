package com.soyokra.sprival.config.mongodb;

import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.extern.slf4j.Slf4j;

/**
 * MongoDB配置类
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Slf4j
@Configuration
@ConditionalOnClass(MongoClient.class)
@ConditionalOnProperty(prefix = "sprival.mongodb", name = "enabled", havingValue = "true",
        matchIfMissing = true)
public class SprivalMongoConfiguration {

    @Autowired
    private SprivalMongoProperties mongoProperties;

    @Bean
    public MongoClient mongoClient() {
        log.info("配置MongoDB客户端，数据库: {}", mongoProperties.getDatabase());

        // 构建连接字符串
        String connectionString = buildConnectionString();
        log.info("MongoDB连接字符串: {}", connectionString.replaceAll(":[^:@]*@", ":***@"));

        // 构建MongoClientSettings
        MongoClientSettings.Builder settingsBuilder = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .applyToConnectionPoolSettings(builder -> {
                    builder.maxSize(mongoProperties.getMaxPoolSize())
                            .minSize(mongoProperties.getMinPoolSize())
                            .maxWaitTime(mongoProperties.getWaitQueueTimeoutMs(),
                                    TimeUnit.MILLISECONDS)
                            .maxConnectionIdleTime(mongoProperties.getMaxIdleTimeMs(),
                                    TimeUnit.MILLISECONDS)
                            .maxConnectionLifeTime(mongoProperties.getMaxLifeTimeMs(),
                                    TimeUnit.MILLISECONDS);
                }).applyToSocketSettings(builder -> {
                    builder.connectTimeout(mongoProperties.getConnectTimeoutMs(),
                            TimeUnit.MILLISECONDS).readTimeout(mongoProperties.getSocketTimeoutMs(),
                                    TimeUnit.MILLISECONDS);
                }).applyToServerSettings(builder -> {
                    builder.heartbeatFrequency(mongoProperties.getHeartbeatFrequencyMs(),
                            TimeUnit.MILLISECONDS);
                }).applyToClusterSettings(builder -> {
                    builder.serverSelectionTimeout(mongoProperties.getServerSelectionTimeoutMs(),
                            TimeUnit.MILLISECONDS);
                }).retryWrites(mongoProperties.getRetryWrites())
                .retryReads(mongoProperties.getRetryReads())
                .applicationName(mongoProperties.getApplicationName());

        // 设置读偏好
        if (mongoProperties.getReadPreference() != null) {
            try {
                ReadPreference readPreference =
                        ReadPreference.valueOf(mongoProperties.getReadPreference());
                settingsBuilder.readPreference(readPreference);
            } catch (IllegalArgumentException e) {
                log.warn("无效的读偏好设置: {}, 使用默认值", mongoProperties.getReadPreference());
            }
        }

        // 设置写关注
        if (mongoProperties.getWriteConcern() != null) {
            try {
                WriteConcern writeConcern = WriteConcern.valueOf(mongoProperties.getWriteConcern());
                settingsBuilder.writeConcern(writeConcern);
            } catch (IllegalArgumentException e) {
                log.warn("无效的写关注设置: {}, 使用默认值", mongoProperties.getWriteConcern());
            }
        }

        MongoClientSettings settings = settingsBuilder.build();

        log.info("MongoDB客户端配置完成");
        return MongoClients.create(settings);
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient, MongoConverter mongoConverter) {
        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, mongoProperties.getDatabase());

        // 移除_class字段
        if (mongoConverter instanceof MappingMongoConverter) {
            MappingMongoConverter converter = (MappingMongoConverter) mongoConverter;
            converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        }

        log.info("MongoDB模板配置完成");
        return mongoTemplate;
    }

    /**
     * 构建MongoDB连接字符串
     */
    private String buildConnectionString() {
        StringBuilder connectionString = new StringBuilder("mongodb://");

        // 添加认证信息
        if (mongoProperties.getUsername() != null && mongoProperties.getPassword() != null) {
            connectionString.append(mongoProperties.getUsername()).append(":")
                    .append(mongoProperties.getPassword()).append("@");
        }

        // 添加主机和端口
        connectionString.append(mongoProperties.getHost()).append(":")
                .append(mongoProperties.getPort()).append("/")
                .append(mongoProperties.getDatabase());

        // 添加查询参数
        StringBuilder queryParams = new StringBuilder();

        if (mongoProperties.getAuthenticationDatabase() != null) {
            appendQueryParam(queryParams, "authSource",
                    mongoProperties.getAuthenticationDatabase());
        }

        if (mongoProperties.getMaxPoolSize() != null) {
            appendQueryParam(queryParams, "maxPoolSize",
                    mongoProperties.getMaxPoolSize().toString());
        }

        if (mongoProperties.getMinPoolSize() != null) {
            appendQueryParam(queryParams, "minPoolSize",
                    mongoProperties.getMinPoolSize().toString());
        }

        if (mongoProperties.getConnectTimeoutMs() != null) {
            appendQueryParam(queryParams, "connectTimeoutMS",
                    mongoProperties.getConnectTimeoutMs().toString());
        }

        if (mongoProperties.getSocketTimeoutMs() != null) {
            appendQueryParam(queryParams, "socketTimeoutMS",
                    mongoProperties.getSocketTimeoutMs().toString());
        }

        if (mongoProperties.getServerSelectionTimeoutMs() != null) {
            appendQueryParam(queryParams, "serverSelectionTimeoutMS",
                    mongoProperties.getServerSelectionTimeoutMs().toString());
        }

        if (mongoProperties.getHeartbeatFrequencyMs() != null) {
            appendQueryParam(queryParams, "heartbeatFrequencyMS",
                    mongoProperties.getHeartbeatFrequencyMs().toString());
        }

        if (mongoProperties.getRetryWrites() != null) {
            appendQueryParam(queryParams, "retryWrites",
                    mongoProperties.getRetryWrites().toString());
        }

        if (mongoProperties.getRetryReads() != null) {
            appendQueryParam(queryParams, "retryReads", mongoProperties.getRetryReads().toString());
        }

        if (mongoProperties.getReadPreference() != null) {
            appendQueryParam(queryParams, "readPreference", mongoProperties.getReadPreference());
        }

        if (mongoProperties.getWriteConcern() != null) {
            appendQueryParam(queryParams, "w", mongoProperties.getWriteConcern());
        }

        if (mongoProperties.getWriteTimeoutMs() != null) {
            appendQueryParam(queryParams, "wtimeoutMS",
                    mongoProperties.getWriteTimeoutMs().toString());
        }

        if (mongoProperties.getJournal() != null) {
            appendQueryParam(queryParams, "journal", mongoProperties.getJournal().toString());
        }

        if (mongoProperties.getSsl() != null) {
            appendQueryParam(queryParams, "ssl", mongoProperties.getSsl().toString());
        }

        if (mongoProperties.getTls() != null) {
            appendQueryParam(queryParams, "tls", mongoProperties.getTls().toString());
        }

        if (mongoProperties.getReplicaSet() != null) {
            appendQueryParam(queryParams, "replicaSet", mongoProperties.getReplicaSet());
        }

        if (mongoProperties.getCompression() != null && mongoProperties.getCompression()) {
            if (mongoProperties.getCompressors() != null) {
                appendQueryParam(queryParams, "compressors", mongoProperties.getCompressors());
            }
        }

        if (queryParams.length() > 0) {
            connectionString.append("?").append(queryParams);
        }

        return connectionString.toString();
    }

    /**
     * 添加查询参数
     */
    private void appendQueryParam(StringBuilder queryParams, String key, String value) {
        if (queryParams.length() > 0) {
            queryParams.append("&");
        }
        queryParams.append(key).append("=").append(value);
    }
}
