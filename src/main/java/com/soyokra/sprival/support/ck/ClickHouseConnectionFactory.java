package com.soyokra.sprival.support.ck;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.clickhouse.ClickHouseDataSource;
import ru.yandex.clickhouse.settings.ClickHouseProperties;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
public class ClickHouseConnectionFactory {

    private static DataSource dataSource;

    private static volatile  Connection connection;

    @Getter
    @Setter
    private String user;

    @Getter
    @Setter
    private String password;

    @Getter
    @Setter
    private String database;

    @Getter
    @Setter
    private String url;

    public DataSource createDataSource() {
        if (dataSource == null) {
            ClickHouseProperties clickHouseProperties = new ClickHouseProperties();
            clickHouseProperties.setUser(this.user);
            clickHouseProperties.setPassword(this.password);
            clickHouseProperties.setDatabase(this.database);
            dataSource = new ClickHouseDataSource(this.url, clickHouseProperties);
        }

        return dataSource;

    }

    public Connection createConnection() {
        try {
            if (connection == null) {
                synchronized (ClickHouseConnectionFactory.class) {
                    if (connection == null) {
                        connection = createDataSource().getConnection();
                    }
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }

        return connection;
    }
}
