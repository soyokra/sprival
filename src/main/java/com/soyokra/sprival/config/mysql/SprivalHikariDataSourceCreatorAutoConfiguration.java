package com.soyokra.sprival.config.mysql;

import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceCreatorAutoConfiguration;
import com.zaxxer.hikari.HikariDataSource;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * HikariDataSourceCreator加强版自动配置
 */
@Configuration
public class SprivalHikariDataSourceCreatorAutoConfiguration {
    @ConditionalOnClass(HikariDataSource.class)
    @Configuration
    static class HikariDataSourcePlusCreatorConfiguration {
        @Bean
        @Order(DynamicDataSourceCreatorAutoConfiguration.HIKARI_ORDER-1)
        public SprivalHikariDataSourceCreator hikariDataSourcePlusCreator(MeterRegistry meterRegistry) {
            return new SprivalHikariDataSourceCreator(meterRegistry);
        }
    }
}
