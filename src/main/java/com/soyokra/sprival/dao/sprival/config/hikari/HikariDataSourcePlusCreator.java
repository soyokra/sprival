package com.soyokra.sprival.dao.sprival.config.hikari;

import com.baomidou.dynamic.datasource.creator.HikariDataSourceCreator;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.zaxxer.hikari.HikariDataSource;
import io.micrometer.core.instrument.MeterRegistry;

import javax.sql.DataSource;

/**
 * HikariDataSourceCreator加强版，支持监控
 */
public class HikariDataSourcePlusCreator extends HikariDataSourceCreator {

    private final MeterRegistry meterRegistry;

    public HikariDataSourcePlusCreator(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public DataSource doCreateDataSource(DataSourceProperty dataSourceProperty) {
        HikariDataSource dataSource = (HikariDataSource) super.doCreateDataSource(dataSourceProperty);
        dataSource.setMetricRegistry(meterRegistry);
        return dataSource;
    }

    @Override
    public boolean support(DataSourceProperty dataSourceProperty) {
        return true;
    }

}
