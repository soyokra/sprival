package com.soyokra.sprival.config.clickhouse;

import com.baomidou.dynamic.datasource.creator.AbstractDataSourceCreator;
import com.baomidou.dynamic.datasource.creator.DataSourceCreator;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import org.springframework.beans.factory.InitializingBean;
import ru.yandex.clickhouse.ClickHouseDataSource;
import ru.yandex.clickhouse.settings.ClickHouseProperties;

import javax.sql.DataSource;

public class ClickHouseDataSourceCreator  extends AbstractDataSourceCreator implements DataSourceCreator, InitializingBean {

    private final com.soyokra.sprival.config.clickhouse.ClickHouseProperties clickHouseProperties;

    public static final String CLICK_HOUSE_DATASOURCE = "ru.yandex.clickhouse.ClickHouseDataSource";

    public ClickHouseDataSourceCreator(com.soyokra.sprival.config.clickhouse.ClickHouseProperties clickHouseProperties) {
        this.clickHouseProperties = clickHouseProperties;
    }

    @Override
    public DataSource doCreateDataSource(DataSourceProperty dataSourceProperty) {
        ClickHouseProperties clickHouseProperties = new ClickHouseProperties();
        clickHouseProperties.setUser(dataSourceProperty.getUsername());
        clickHouseProperties.setPassword(dataSourceProperty.getPassword());
        clickHouseProperties.setDatabase(this.clickHouseProperties.getDatabase());
        return new ClickHouseDataSource(dataSourceProperty.getUrl(), clickHouseProperties);
    }

    @Override
    public boolean support(DataSourceProperty dataSourceProperty) {
        Class<? extends DataSource> type = dataSourceProperty.getType();
        return CLICK_HOUSE_DATASOURCE.equals(type.getName());
    }

    //  Spring 容器完成对 Bean 的属性设置之后调用
    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
