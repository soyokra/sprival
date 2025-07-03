package com.soyokra.sprival.config.mysql;

import com.baomidou.dynamic.datasource.creator.HikariDataSourceCreator;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.zaxxer.hikari.HikariDataSource;
import io.micrometer.core.instrument.MeterRegistry;

import javax.sql.DataSource;

/**
 * HikariDataSourceCreator加强版，支持监控
 * 健康检查，指标监控
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
