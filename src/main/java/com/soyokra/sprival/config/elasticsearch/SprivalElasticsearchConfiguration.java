package com.soyokra.sprival.config.elasticsearch;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import lombok.extern.slf4j.Slf4j;

/**
 * Elasticsearch 配置类 提供连接池、认证、监控等增强功能
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "sprival.elasticsearch", name = "enabled", havingValue = "true",
        matchIfMissing = true)
@EnableConfigurationProperties(SprivalElasticsearchProperties.class)
@EnableElasticsearchRepositories(basePackages = "com.soyokra.sprival.repository.elasticsearch")
public class SprivalElasticsearchConfiguration {

    public SprivalElasticsearchConfiguration(SprivalElasticsearchProperties properties) {
        log.info("Elasticsearch配置初始化完成: nodes={}, clusterName={}",
                String.join(",", properties.getNodes()), properties.getClusterName());
    }
}
