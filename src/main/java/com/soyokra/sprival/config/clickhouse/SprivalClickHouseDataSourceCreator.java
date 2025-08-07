package com.soyokra.sprival.config.clickhouse;

import com.baomidou.dynamic.datasource.creator.DataSourceCreator;
import com.baomidou.dynamic.datasource.creator.DataSourceProperty;
import org.springframework.beans.factory.InitializingBean;
import ru.yandex.clickhouse.ClickHouseDataSource;
import ru.yandex.clickhouse.settings.ClickHouseProperties;

import javax.sql.DataSource;

public class SprivalClickHouseDataSourceCreator implements DataSourceCreator, InitializingBean {

    private final SprivalClickHouseProperties clickHouseProperties;

    public static final String CLICK_HOUSE_DATASOURCE = "ru.yandex.clickhouse.ClickHouseDataSource";

    public SprivalClickHouseDataSourceCreator(SprivalClickHouseProperties clickHouseProperties) {
        this.clickHouseProperties = clickHouseProperties;
    }

    @Override
    public DataSource createDataSource(DataSourceProperty dataSourceProperty) {
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
