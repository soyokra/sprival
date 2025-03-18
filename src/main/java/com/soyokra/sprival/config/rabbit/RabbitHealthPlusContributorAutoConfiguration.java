package com.soyokra.sprival.config.rabbit;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.actuate.autoconfigure.health.CompositeHealthContributorConfiguration;
import org.springframework.boot.actuate.autoconfigure.health.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.actuate.health.HealthContributor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(RabbitTemplate.class)
@ConditionalOnEnabledHealthIndicator("rabbit")
@AutoConfigureAfter(RabbitAutoConfiguration.class)
// @ConditionalOnBean(RabbitTemplate.class) 需要研究一下为什么加了这个条件不满足
public class RabbitHealthPlusContributorAutoConfiguration
		extends CompositeHealthContributorConfiguration<RabbitHealthPlusIndicator, RabbitTemplate> {

	@Bean
	public HealthContributor rabbitHealthContributor(Map<String, RabbitTemplate> rabbitTemplates) {
		return createContributor(rabbitTemplates);
	}

}
