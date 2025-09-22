package com.soyokra.sprival.config.clickhouse;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Data;

/**
 * ClickHouse 配置属性类
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Component
@Data
@ConfigurationProperties(prefix = "sprival.clickhouse")
public class SprivalClickHouseProperties {

    /**
     * 是否启用ClickHouse
     */
    private boolean enabled = true;

    /**
     * 数据库名称
     */
    private String database = "sprival";

    /**
     * 服务器地址
     */
    private String host = "localhost";

    /**
     * 服务器端口
     */
    private int port = 8123;

    /**
     * 用户名
     */
    private String username = "default";

    /**
     * 密码
     */
    private String password = "";

    /**
     * 连接超时时间（毫秒）
     */
    private int connectTimeout = 10000;

    /**
     * 读取超时时间（毫秒）
     */
    private int readTimeout = 30000;

    /**
     * 最大连接数
     */
    private int maxConnections = 20;

    /**
     * 连接池配置
     */
    private Pool pool = new Pool();

    /**
     * 监控配置
     */
    private Monitor monitor = new Monitor();

    @Data
    public static class Pool {
        /**
         * 最小空闲连接数
         */
        private int minIdle = 2;

        /**
         * 最大空闲连接数
         */
        private int maxIdle = 10;

        /**
         * 连接最大生存时间（毫秒）
         */
        private long maxLifetime = 1800000;

        /**
         * 连接空闲超时时间（毫秒）
         */
        private long idleTimeout = 300000;

        /**
         * 连接验证查询
         */
        private String validationQuery = "SELECT 1";
    }

    @Data
    public static class Monitor {
        /**
         * 是否启用监控
         */
        private boolean enabled = true;

        /**
         * 健康检查间隔（毫秒）
         */
        private long healthCheckInterval = 30000;

        /**
         * 健康检查超时（毫秒）
         */
        private long healthCheckTimeout = 5000;

        /**
         * 是否启用指标收集
         */
        private boolean metricsEnabled = true;
    }
}
