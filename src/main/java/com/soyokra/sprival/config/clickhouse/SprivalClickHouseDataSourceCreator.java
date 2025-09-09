package com.soyokra.sprival.config.clickhouse;

import javax.sql.DataSource;
import org.springframework.beans.factory.InitializingBean;
import com.baomidou.dynamic.datasource.creator.DataSourceCreator;
import com.baomidou.dynamic.datasource.creator.DataSourceProperty;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import ru.yandex.clickhouse.ClickHouseDataSource;
import ru.yandex.clickhouse.settings.ClickHouseProperties;

/**
 * ClickHouse 数据源创建器
 * 
 * @author Sprival Team
 * @version 1.0
 */
public class SprivalClickHouseDataSourceCreator implements DataSourceCreator, InitializingBean {

    private final SprivalClickHouseProperties clickHouseProperties;

    public static final String CLICK_HOUSE_DATASOURCE = "ru.yandex.clickhouse.ClickHouseDataSource";

    public SprivalClickHouseDataSourceCreator(SprivalClickHouseProperties clickHouseProperties) {
        this.clickHouseProperties = clickHouseProperties;
    }

    @Override
    public DataSource createDataSource(DataSourceProperty dataSourceProperty) {
        // 创建 ClickHouse 属性
        ClickHouseProperties chProperties = new ClickHouseProperties();
        chProperties.setUser(dataSourceProperty.getUsername());
        chProperties.setPassword(dataSourceProperty.getPassword());
        chProperties.setDatabase(clickHouseProperties.getDatabase());
        chProperties.setConnectionTimeout(clickHouseProperties.getConnectTimeout());
        chProperties.setSocketTimeout(clickHouseProperties.getReadTimeout());

        // 创建 ClickHouse 数据源
        ClickHouseDataSource clickHouseDataSource =
                new ClickHouseDataSource(dataSourceProperty.getUrl(), chProperties);

        // 使用 HikariCP 连接池包装
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDataSource(clickHouseDataSource);
        hikariConfig.setMaximumPoolSize(clickHouseProperties.getMaxConnections());
        hikariConfig.setMinimumIdle(clickHouseProperties.getPool().getMinIdle());
        hikariConfig.setMaxLifetime(clickHouseProperties.getPool().getMaxLifetime());
        hikariConfig.setIdleTimeout(clickHouseProperties.getPool().getIdleTimeout());
        hikariConfig.setConnectionTestQuery(clickHouseProperties.getPool().getValidationQuery());
        hikariConfig.setPoolName("ClickHouse-HikariCP");
        hikariConfig.setConnectionTimeout(clickHouseProperties.getConnectTimeout());
        hikariConfig
                .setValidationTimeout(clickHouseProperties.getMonitor().getHealthCheckTimeout());

        return new HikariDataSource(hikariConfig);
    }

    @Override
    public boolean support(DataSourceProperty dataSourceProperty) {
        Class<? extends DataSource> type = dataSourceProperty.getType();
        return CLICK_HOUSE_DATASOURCE.equals(type.getName());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 初始化完成后的处理
    }
}
