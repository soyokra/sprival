package com.soyokra.sprival.config.mongodb;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Data;

/**
 * MongoDB配置属性
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Component
@Data
@ConfigurationProperties(prefix = "sprival.mongodb")
public class SprivalMongoProperties {

    /**
     * 是否启用MongoDB
     */
    private Boolean enabled = true;

    /**
     * 数据库名称
     */
    private String database = "sprival";

    /**
     * 主机地址
     */
    private String host = "localhost";

    /**
     * 端口
     */
    private Integer port = 27017;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 认证数据库
     */
    private String authenticationDatabase = "admin";

    /**
     * 连接超时时间（毫秒）
     */
    private Integer connectTimeoutMs = 10000;

    /**
     * Socket超时时间（毫秒）
     */
    private Integer socketTimeoutMs = 30000;

    /**
     * 服务器选择超时时间（毫秒）
     */
    private Integer serverSelectionTimeoutMs = 5000;

    /**
     * 连接池最大连接数
     */
    private Integer maxPoolSize = 20;

    /**
     * 连接池最小连接数
     */
    private Integer minPoolSize = 5;

    /**
     * 等待连接超时时间（毫秒）
     */
    private Integer waitQueueTimeoutMs = 120000;

    /**
     * 连接最大空闲时间（毫秒）
     */
    private Integer maxIdleTimeMs = 0;

    /**
     * 连接最大生存时间（毫秒）
     */
    private Integer maxLifeTimeMs = 0;

    /**
     * 心跳频率（毫秒）
     */
    private Integer heartbeatFrequencyMs = 10000;

    /**
     * 是否启用重试写入
     */
    private Boolean retryWrites = true;

    /**
     * 是否启用重试读取
     */
    private Boolean retryReads = true;

    /**
     * 读偏好
     */
    private String readPreference = "primary";

    /**
     * 写关注级别
     */
    private String writeConcern = "1";

    /**
     * 写超时时间（毫秒）
     */
    private Integer writeTimeoutMs = 5000;

    /**
     * 是否等待日志提交
     */
    private Boolean journal = false;

    /**
     * 是否自动创建索引
     */
    private Boolean autoIndexCreation = true;

    /**
     * UUID表示方式
     */
    private String uuidRepresentation = "javaLegacy";

    /**
     * 应用程序名称
     */
    private String applicationName = "sprival";

    /**
     * 是否启用SSL
     */
    private Boolean ssl = false;

    /**
     * 是否启用TLS
     */
    private Boolean tls = false;

    /**
     * 副本集名称
     */
    private String replicaSet;

    /**
     * 是否启用压缩
     */
    private Boolean compression = false;

    /**
     * 压缩器列表
     */
    private String compressors;
}
