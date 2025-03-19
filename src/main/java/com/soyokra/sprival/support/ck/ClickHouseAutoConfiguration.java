package com.soyokra.sprival.support.ck;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitOperations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.amqp.RabbitRetryTemplateCustomizer;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ClickHouseProperties.class)
public class ClickHouseAutoConfiguration {
    @Configuration(proxyBeanMethods = false)
    protected static class ClickHouseConnectionFactoryCreator {
        @Bean
        public ClickHouseConnectionFactory clickHouseConnectionFactory(ClickHouseProperties clickHouseProperties)  {
            ClickHouseConnectionFactory clickHouseConnectionFactory = new ClickHouseConnectionFactory();
            clickHouseConnectionFactory.setUser(clickHouseProperties.getUser());
            clickHouseConnectionFactory.setPassword(clickHouseProperties.getPassword());
            clickHouseConnectionFactory.setDatabase(clickHouseProperties.getDatabase());
            clickHouseConnectionFactory.setUrl(clickHouseProperties.getUrl());
            return clickHouseConnectionFactory;
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Import(ClickHouseConnectionFactoryCreator.class)
    protected static class ClickHouseTemplateConfiguration {
        @Bean
        public ClickHouseTemplate clickHouseTemplate(ClickHouseConnectionFactory connectionFactory) {
            return new ClickHouseTemplate(connectionFactory);
        }

    }
}
