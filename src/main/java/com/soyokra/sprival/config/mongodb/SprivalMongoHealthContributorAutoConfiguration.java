package com.soyokra.sprival.config.mongodb;

import org.springframework.boot.actuate.autoconfigure.health.CompositeHealthContributorConfiguration;
import org.springframework.boot.actuate.autoconfigure.health.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.actuate.health.HealthContributor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Map;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(MongoTemplate.class)
@ConditionalOnEnabledHealthIndicator("mongo")
public class SprivalMongoHealthContributorAutoConfiguration
		extends CompositeHealthContributorConfiguration<SprivalMongoHealthIndicator, MongoTemplate> {

	@Bean
	public HealthContributor mongoHealthContributor(Map<String, MongoTemplate> mongoTemplates) {
		return createContributor(mongoTemplates);
	}

}
