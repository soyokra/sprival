package com.soyokra.sprival.config.clickhouse;

import javax.sql.DataSource;
import org.springframework.beans.factory.InitializingBean;
import com.baomidou.dynamic.datasource.creator.DataSourceCreator;
import com.baomidou.dynamic.datasource.creator.DataSourceProperty;
import ru.yandex.clickhouse.ClickHouseDataSource;
import ru.yandex.clickhouse.settings.ClickHouseProperties;

/**
 * ClickHouse 数据源创建器
 * 
 * @author Sprival Team
 * @version 1.0
 */
public class SprivalClickHouseDataSourceCreator implements DataSourceCreator, InitializingBean {


    public static final String CLICK_HOUSE_DATASOURCE = "ru.yandex.clickhouse.ClickHouseDataSource";

    public SprivalClickHouseDataSourceCreator() {

    }

    @Override
    public DataSource createDataSource(DataSourceProperty dataSourceProperty) {
        // 使用转换器创建 ClickHouse 属性
        ClickHouseProperties clickHouseProperties =
                ClickHousePropertiesConverter.builder().user(dataSourceProperty.getUsername())
                        .password(dataSourceProperty.getPassword()).build();

        // 创建 ClickHouse 数据源
        return new ClickHouseDataSource(dataSourceProperty.getUrl(), clickHouseProperties);
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
