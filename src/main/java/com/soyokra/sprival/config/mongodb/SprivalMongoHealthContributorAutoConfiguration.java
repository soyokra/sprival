package com.soyokra.sprival.config.mongodb;

import org.springframework.boot.actuate.autoconfigure.health.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * MongoDB健康检查自动配置类
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(MongoTemplate.class)
@ConditionalOnEnabledHealthIndicator("mongo")
@ConditionalOnProperty(prefix = "sprival.mongodb", name = "enabled", havingValue = "true",
		matchIfMissing = true)
public class SprivalMongoHealthContributorAutoConfiguration {

	@Bean
	public HealthIndicator mongoHealthIndicator(SprivalMongoHealthIndicator mongoHealthIndicator) {
		return mongoHealthIndicator;
	}

}
