package com.soyokra.sprival.config.elasticsearch;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Data;

/**
 * Elasticsearch 配置属性类
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Component
@Data
@ConfigurationProperties(prefix = "sprival.elasticsearch")
public class SprivalElasticsearchProperties {

    /**
     * 是否启用Elasticsearch
     */
    private boolean enabled = true;

    /**
     * 集群名称
     */
    private String clusterName = "sprival-cluster";

    /**
     * 节点地址列表
     */
    private String[] nodes = {"localhost:9200"};

    /**
     * 连接超时时间（毫秒）
     */
    private int connectTimeout = 5000;

    /**
     * 读取超时时间（毫秒）
     */
    private int readTimeout = 10000;

    /**
     * 最大连接数
     */
    private int maxConnections = 100;

    /**
     * 最大连接数（每个路由）
     */
    private int maxConnectionsPerRoute = 10;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 是否启用SSL
     */
    private boolean ssl = false;

    /**
     * 是否验证SSL证书
     */
    private boolean verifySsl = true;

    /**
     * 索引设置
     */
    private Index index = new Index();

    /**
     * 监控设置
     */
    private Monitor monitor = new Monitor();

    @Data
    public static class Index {
        /**
         * 默认分片数
         */
        private int numberOfShards = 1;

        /**
         * 默认副本数
         */
        private int numberOfReplicas = 1;

        /**
         * 刷新间隔
         */
        private String refreshInterval = "1s";

        /**
         * 索引前缀
         */
        private String prefix = "sprival";
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
