package com.soyokra.sprival.support.logging;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Data;

/**
 * Sprival日志配置属性 统一管理application日志和jetty-access日志的输出配置
 * 
 * @author Sprival Team
 * @since 1.0.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "sprival.logging")
public class SprivalLoggingProperties {

    /**
     * Application日志配置
     */
    private ApplicationLogConfig application = new ApplicationLogConfig();

    /**
     * Jetty访问日志配置
     */
    private JettyAccessConfig jettyAccess = new JettyAccessConfig();

    /**
     * Application日志配置 继承基类配置，提供默认值
     */
    @Data
    @lombok.EqualsAndHashCode(callSuper = true)
    public static class ApplicationLogConfig extends BaseKafkaLogConfig {

        public ApplicationLogConfig() {
            setTopic("application-logs");
            setClientId("application-log-producer");
        }
    }

    /**
     * Jetty访问日志配置 继承基类配置，提供默认值
     */
    @Data
    @lombok.EqualsAndHashCode(callSuper = true)
    public static class JettyAccessConfig extends BaseKafkaLogConfig {

        public JettyAccessConfig() {
            setTopic("jetty-access-logs");
            setClientId("jetty-access-log-producer");
        }
    }
}

