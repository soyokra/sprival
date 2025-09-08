package com.soyokra.sprival.config.rabbitmq;

import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Data;


/**
 * RabbitMQ 配置属性类
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Data
@ConfigurationProperties(prefix = "sprival.rabbitmq")
public class SprivalRabbitProperties {

    /**
     * 是否启用 RabbitMQ 增强配置
     */
    private boolean enabled = true;

    /**
     * 连接池配置
     */
    private Pool pool = new Pool();

    /**
     * 重试配置
     */
    private Retry retry = new Retry();

    /**
     * 监控配置
     */
    private Monitor monitor = new Monitor();

    /**
     * 死信队列配置
     */
    private DeadLetter deadLetter = new DeadLetter();

    @Data
    public static class Pool {
        /**
         * 连接池模式：connection 或 channel
         */
        private String mode = "connection";

        /**
         * 连接池大小
         */
        private int connectionSize = 10;

        /**
         * 通道池大小
         */
        private int channelSize = 25;

        /**
         * 通道获取超时时间（毫秒）
         */
        private long checkoutTimeout = 30000;

        /**
         * 连接超时时间（毫秒）
         */
        private long connectionTimeout = 15000;

        /**
         * 心跳间隔（秒）
         */
        private int heartbeat = 30;

        /**
         * 连接名称
         */
        private String connectionName = "sprival-connection";
    }

    @Data
    public static class Retry {
        /**
         * 是否启用重试
         */
        private boolean enabled = true;

        /**
         * 最大重试次数
         */
        private int maxAttempts = 3;

        /**
         * 初始重试间隔（毫秒）
         */
        private long initialInterval = 1000;

        /**
         * 重试间隔倍数
         */
        private double multiplier = 2.0;

        /**
         * 最大重试间隔（毫秒）
         */
        private long maxInterval = 10000;

        /**
         * 重试异常类型
         */
        private String[] retryableExceptions = {"java.net.ConnectException",
                "java.net.SocketTimeoutException", "java.io.IOException"};
    }

    @Data
    public static class Monitor {
        /**
         * 是否启用监控指标
         */
        private boolean enabled = true;

        /**
         * 指标收集间隔（秒）
         */
        private int collectInterval = 30;

        /**
         * 是否启用健康检查
         */
        private boolean healthCheckEnabled = true;

        /**
         * 健康检查间隔（秒）
         */
        private int healthCheckInterval = 60;

        /**
         * 健康检查超时时间（毫秒）
         */
        private long healthCheckTimeout = 5000;
    }

    @Data
    public static class DeadLetter {
        /**
         * 是否启用死信队列
         */
        private boolean enabled = true;

        /**
         * 死信交换器名称
         */
        private String exchange = "dlx.exchange";

        /**
         * 死信队列名称
         */
        private String queue = "dlx.queue";

        /**
         * 死信路由键
         */
        private String routingKey = "dlx.routing.key";

        /**
         * 消息TTL（毫秒）
         */
        private long messageTtl = 300000; // 5分钟

        /**
         * 队列最大长度
         */
        private int maxLength = 10000;
    }
}
