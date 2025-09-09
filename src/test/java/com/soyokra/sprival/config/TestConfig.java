package com.soyokra.sprival.config;

import javax.sql.DataSource;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

/**
 * 测试配置类 提供测试环境的基础配置
 * 
 * @author Sprival Team
 * @version 1.0
 */
@TestConfiguration
@Profile("test")
public class TestConfig {

    /**
     * 测试数据源配置
     */
    @Bean
    @Primary
    public DataSource testDataSource() {
        return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).setName("testdb")
                .addScript("classpath:test-schema.sql").addScript("classpath:test-data.sql")
                .build();
    }

    /**
     * 测试JdbcTemplate配置
     */
    @Bean
    @Primary
    public JdbcTemplate testJdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
