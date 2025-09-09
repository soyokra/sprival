package com.soyokra.sprival.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

/**
 * ClickHouse 服务类
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "sprival.clickhouse.enabled", havingValue = "true",
        matchIfMissing = true)
public class ClickHouseService {

    @Autowired
    @Qualifier("clickHouseDataSource")
    private DataSource clickHouseDataSource;

    /**
     * 执行查询并返回结果列表
     */
    public List<Map<String, Object>> executeQuery(String sql) {
        List<Map<String, Object>> results = new ArrayList<>();

        try (Connection connection = clickHouseDataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = resultSet.getObject(i);
                    row.put(columnName, value);
                }
                results.add(row);
            }

            log.info("ClickHouse查询执行成功，返回 {} 条记录", results.size());
            return results;

        } catch (SQLException e) {
            log.error("ClickHouse查询执行失败: {}", sql, e);
            throw new RuntimeException("ClickHouse查询执行失败", e);
        }
    }

    /**
     * 执行更新操作（INSERT、UPDATE、DELETE等）
     */
    public int executeUpdate(String sql) {
        try (Connection connection = clickHouseDataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            int affectedRows = statement.executeUpdate();
            log.info("ClickHouse更新操作执行成功，影响 {} 行", affectedRows);
            return affectedRows;

        } catch (SQLException e) {
            log.error("ClickHouse更新操作执行失败: {}", sql, e);
            throw new RuntimeException("ClickHouse更新操作执行失败", e);
        }
    }

    /**
     * 批量执行更新操作
     */
    public int[] executeBatch(List<String> sqlList) {
        try (Connection connection = clickHouseDataSource.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement statement = connection.prepareStatement("")) {
                for (String sql : sqlList) {
                    statement.addBatch(sql);
                }

                int[] results = statement.executeBatch();
                connection.commit();

                log.info("ClickHouse批量操作执行成功，执行 {} 条语句", results.length);
                return results;

            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            log.error("ClickHouse批量操作执行失败", e);
            throw new RuntimeException("ClickHouse批量操作执行失败", e);
        }
    }

    /**
     * 获取数据库版本信息
     */
    public String getVersion() {
        try (Connection connection = clickHouseDataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT version()");
                ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getString(1);
            }
            return "Unknown";
        } catch (SQLException e) {
            log.error("获取ClickHouse版本失败", e);
            return "Error";
        }
    }

    /**
     * 获取数据库统计信息
     */
    public Map<String, Object> getDatabaseStats() {
        Map<String, Object> stats = new HashMap<>();

        try {
            // 获取版本信息
            stats.put("version", getVersion());

            // 获取当前时间
            List<Map<String, Object>> timeResult = executeQuery("SELECT now() as current_time");
            if (!timeResult.isEmpty()) {
                stats.put("currentTime", timeResult.get(0).get("current_time"));
            }

            // 获取数据库信息
            List<Map<String, Object>> dbResult = executeQuery(
                    "SELECT name FROM system.databases WHERE name = currentDatabase()");
            if (!dbResult.isEmpty()) {
                stats.put("currentDatabase", dbResult.get(0).get("name"));
            }

            // 获取表数量
            List<Map<String, Object>> tableResult = executeQuery(
                    "SELECT count() as table_count FROM system.tables WHERE database = currentDatabase()");
            if (!tableResult.isEmpty()) {
                stats.put("tableCount", tableResult.get(0).get("table_count"));
            }

            log.info("ClickHouse数据库统计信息获取成功");
            return stats;

        } catch (Exception e) {
            log.error("获取ClickHouse数据库统计信息失败", e);
            stats.put("error", e.getMessage());
            return stats;
        }
    }

    /**
     * 创建表
     */
    public void createTable(String tableName, String createTableSql) {
        String fullSql =
                String.format("CREATE TABLE IF NOT EXISTS %s %s", tableName, createTableSql);
        executeUpdate(fullSql);
        log.info("ClickHouse表创建成功: {}", tableName);
    }

    /**
     * 删除表
     */
    public void dropTable(String tableName) {
        String sql = String.format("DROP TABLE IF EXISTS %s", tableName);
        executeUpdate(sql);
        log.info("ClickHouse表删除成功: {}", tableName);
    }

    /**
     * 检查表是否存在
     */
    public boolean tableExists(String tableName) {
        String sql = String.format(
                "SELECT count() as cnt FROM system.tables WHERE database = currentDatabase() AND name = '%s'",
                tableName);
        List<Map<String, Object>> result = executeQuery(sql);
        return !result.isEmpty() && ((Number) result.get(0).get("cnt")).intValue() > 0;
    }

    /**
     * 获取表结构信息
     */
    public List<Map<String, Object>> getTableSchema(String tableName) {
        String sql = String.format("DESCRIBE TABLE %s", tableName);
        return executeQuery(sql);
    }
}
