package com.soyokra.sprival.config.clickhouse;

import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceCreatorAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import ru.yandex.clickhouse.ClickHouseDataSource;


@Configuration
public class SprivalClickHouseDataSourceCreatorAutoConfiguration {
    @ConditionalOnClass(ClickHouseDataSource.class)
    @Configuration
    static class ClickHouseDataSourceCreatorConfiguration {
        @Bean
        @Order(DynamicDataSourceCreatorAutoConfiguration.DEFAULT_ORDER-1)
        public SprivalClickHouseDataSourceCreator clickHouseDataSourceCreator(SprivalClickHouseProperties clickHouseProperties) {
            return new SprivalClickHouseDataSourceCreator(clickHouseProperties);
        }
    }
}
