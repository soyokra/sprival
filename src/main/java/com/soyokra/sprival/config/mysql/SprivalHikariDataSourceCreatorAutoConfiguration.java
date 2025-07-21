package com.soyokra.sprival.config.mysql;

import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceCreatorAutoConfiguration;
import com.zaxxer.hikari.HikariDataSource;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * 自定义hikari数据源创建器自动装配
 */
@Configuration
public class SprivalHikariDataSourceCreatorAutoConfiguration {
    @ConditionalOnClass(HikariDataSource.class)
    @Configuration
    static class HikariDataSourcePlusCreatorConfiguration {

        /**
         * 在源码的之前注册bean就能替换掉默认的hikari数据源创建器
         */
        @Bean
        @Order(DynamicDataSourceCreatorAutoConfiguration.HIKARI_ORDER-1)
        public SprivalHikariDataSourceCreator sprivalHikariDataSourceCreator(MeterRegistry meterRegistry) {
            return new SprivalHikariDataSourceCreator(meterRegistry);
        }
    }
}
