package com.soyokra.sprival.support.ck;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.List;

@Slf4j
@Service
public class ClickhouseTemplate {
    @Autowired
    ConnectionFactory connectionFactory;

    public Connection getConnection() throws SQLException {
        return connectionFactory.createConnection();
    }


    public int update(String sql) throws SQLException {
        Connection connection = getConnection();
        try {
            Statement statement = getConnection().createStatement();
            int update = statement.executeUpdate(sql);
            connection.commit();// 执行
            return update;
        } catch (Exception e) {
            try {
                connection.rollback();//异常回滚
            } catch (Exception e1) {
                log.error(e1.getMessage(), e1);
            }
            log.error(e.getMessage(), e);
        }
        return 0;
    }

    public ResultSet query(String sql, List<Object> values) {
        ResultSet resultSet = null;
        try {
            PreparedStatement preparedStatement = createPreparedStatement(sql, values);
            resultSet = preparedStatement.executeQuery();
        } catch (Exception e) {
           log.error(e.getMessage(), e);
        }
        return resultSet;
    }

    public PreparedStatement createPreparedStatement(String sql, List<Object> values) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = getConnection().prepareStatement(sql);
            for (int i = 0; i < values.size(); i++) {
                preparedStatement.setObject(i + 1, values.get(i));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return preparedStatement;
    }
}
