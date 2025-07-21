package com.soyokra.sprival.config.mysql;

import com.baomidou.dynamic.datasource.creator.HikariDataSourceCreator;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.zaxxer.hikari.HikariDataSource;
import io.micrometer.core.instrument.MeterRegistry;

import javax.sql.DataSource;

/**
 * hikari数据源创建器增加micrometer监控指标采集注入
 */
public class SprivalHikariDataSourceCreator extends HikariDataSourceCreator {

    private final MeterRegistry meterRegistry;

    public SprivalHikariDataSourceCreator(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public DataSource doCreateDataSource(DataSourceProperty dataSourceProperty) {
        HikariDataSource dataSource = (HikariDataSource) super.doCreateDataSource(dataSourceProperty);
        dataSource.setMetricRegistry(meterRegistry);
        return dataSource;
    }
}
