package com.soyokra.sprival.support.ck;

import org.springframework.stereotype.Service;
import ru.yandex.clickhouse.ClickHouseDataSource;
import ru.yandex.clickhouse.settings.ClickHouseProperties;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Service
public class ConnectionFactory {

    private static DataSource dataSource;

    private static volatile  Connection connection;

    public DataSource createDataSource() {
        if (dataSource == null) {
            ClickHouseProperties clickHouseProperties = new ClickHouseProperties();
            clickHouseProperties.setUser("");
            clickHouseProperties.setPassword("");
            clickHouseProperties.setPassword("");
            clickHouseProperties.setDatabase("");
            dataSource = new ClickHouseDataSource("", clickHouseProperties);
        }

        return dataSource;

    }

    public Connection createConnection() throws SQLException {
        if (connection == null) {
            synchronized (ConnectionFactory.class) {
                if (connection == null) {
                    connection = createDataSource().getConnection();
                }
            }
        }
        return connection;
    }
}
