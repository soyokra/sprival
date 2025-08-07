package com.soyokra.sprival.config.rabbit;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.actuate.autoconfigure.health.CompositeHealthContributorConfiguration;
import org.springframework.boot.actuate.autoconfigure.health.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.actuate.health.HealthContributor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(RabbitTemplate.class)
@ConditionalOnEnabledHealthIndicator("rabbit")

// @AutoConfigureAfter(RabbitAutoConfiguration.class) // 这个只有通过AutoConfigurationImportSelector方式导入的类才有排序的意义
// ConditionalOnBean要求RabbitTemplate的BeanDefinition要存在
// RabbitHealthPlusContributorAutoConfiguration在注册BeanDefinition 会先于 RabbitAutoConfiguration
//@ConditionalOnBean(RabbitTemplate.class)
public class SprivalRabbitHealthContributorAutoConfiguration
		extends CompositeHealthContributorConfiguration<SprivalRabbitHealthIndicator, RabbitTemplate> {

	@Bean
	public HealthContributor rabbitHealthContributor(Map<String, RabbitTemplate> rabbitTemplates) {
		return createContributor(rabbitTemplates);
	}

}
