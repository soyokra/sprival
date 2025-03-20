package com.soyokra.sprival.support.ck;

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
