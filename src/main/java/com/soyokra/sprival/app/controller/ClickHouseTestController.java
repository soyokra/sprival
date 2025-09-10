package com.soyokra.sprival.app.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.soyokra.sprival.app.data.clickhouse.service.ClickHouseService;
import lombok.extern.slf4j.Slf4j;

/**
 * ClickHouse测试控制器 用于测试ClickHouse功能
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/clickhouse")
@ConditionalOnProperty(name = "sprival.clickhouse.enabled", havingValue = "true",
        matchIfMissing = true)
public class ClickHouseTestController {

    @Autowired
    private ClickHouseService clickHouseService;

    /**
     * 测试ClickHouse连接
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testConnection() {
        Map<String, Object> response = new HashMap<>();

        try {
            String version = clickHouseService.getVersion();
            response.put("success", true);
            response.put("message", "ClickHouse连接正常");
            response.put("version", version);
            response.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("ClickHouse连接测试失败", e);
            response.put("success", false);
            response.put("message", "ClickHouse连接失败: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 执行自定义查询
     */
    @PostMapping("/query")
    public ResponseEntity<Map<String, Object>> executeQuery(
            @RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            String sql = request.get("sql");
            if (sql == null || sql.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "SQL语句不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            List<Map<String, Object>> results = clickHouseService.executeQuery(sql);
            response.put("success", true);
            response.put("message", "查询执行成功");
            response.put("results", results);
            response.put("count", results.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("ClickHouse查询执行失败", e);
            response.put("success", false);
            response.put("message", "查询执行失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 执行更新操作
     */
    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> executeUpdate(
            @RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            String sql = request.get("sql");
            if (sql == null || sql.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "SQL语句不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            int affectedRows = clickHouseService.executeUpdate(sql);
            response.put("success", true);
            response.put("message", "更新操作执行成功");
            response.put("affectedRows", affectedRows);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("ClickHouse更新操作执行失败", e);
            response.put("success", false);
            response.put("message", "更新操作执行失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 获取数据库统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDatabaseStats() {
        Map<String, Object> response = new HashMap<>();

        try {
            Map<String, Object> stats = clickHouseService.getDatabaseStats();
            response.put("success", true);
            response.put("message", "数据库统计信息获取成功");
            response.put("stats", stats);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取ClickHouse数据库统计信息失败", e);
            response.put("success", false);
            response.put("message", "获取数据库统计信息失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 创建表
     */
    @PostMapping("/table")
    public ResponseEntity<Map<String, Object>> createTable(
            @RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            String tableName = request.get("tableName");
            String createTableSql = request.get("createTableSql");

            if (tableName == null || createTableSql == null) {
                response.put("success", false);
                response.put("message", "表名和建表SQL不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            clickHouseService.createTable(tableName, createTableSql);
            response.put("success", true);
            response.put("message", "表创建成功");
            response.put("tableName", tableName);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("ClickHouse表创建失败", e);
            response.put("success", false);
            response.put("message", "表创建失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 删除表
     */
    @DeleteMapping("/table/{tableName}")
    public ResponseEntity<Map<String, Object>> dropTable(@PathVariable String tableName) {
        Map<String, Object> response = new HashMap<>();

        try {
            clickHouseService.dropTable(tableName);
            response.put("success", true);
            response.put("message", "表删除成功");
            response.put("tableName", tableName);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("ClickHouse表删除失败", e);
            response.put("success", false);
            response.put("message", "表删除失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 检查表是否存在
     */
    @GetMapping("/table/{tableName}/exists")
    public ResponseEntity<Map<String, Object>> tableExists(@PathVariable String tableName) {
        Map<String, Object> response = new HashMap<>();

        try {
            boolean exists = clickHouseService.tableExists(tableName);
            response.put("success", true);
            response.put("exists", exists);
            response.put("tableName", tableName);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("检查ClickHouse表是否存在失败", e);
            response.put("success", false);
            response.put("message", "检查表是否存在失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 获取表结构
     */
    @GetMapping("/table/{tableName}/schema")
    public ResponseEntity<Map<String, Object>> getTableSchema(@PathVariable String tableName) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Map<String, Object>> schema = clickHouseService.getTableSchema(tableName);
            response.put("success", true);
            response.put("message", "表结构获取成功");
            response.put("tableName", tableName);
            response.put("schema", schema);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取ClickHouse表结构失败", e);
            response.put("success", false);
            response.put("message", "获取表结构失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 创建测试数据
     */
    @PostMapping("/test-data")
    public ResponseEntity<Map<String, Object>> createTestData() {
        Map<String, Object> response = new HashMap<>();

        try {
            // 创建测试表
            String createTableSql =
                    "(id UInt32, name String, age UInt8, email String, created_at DateTime) ENGINE = MergeTree() ORDER BY id";
            clickHouseService.createTable("test_users", createTableSql);

            // 插入测试数据
            String insertSql = "INSERT INTO test_users (id, name, age, email, created_at) VALUES "
                    + "(1, '张三', 25, 'zhangsan@example.com', now()), "
                    + "(2, '李四', 30, 'lisi@example.com', now()), "
                    + "(3, '王五', 28, 'wangwu@example.com', now())";
            clickHouseService.executeUpdate(insertSql);

            response.put("success", true);
            response.put("message", "测试数据创建成功");
            response.put("tableName", "test_users");
            response.put("recordCount", 3);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("创建ClickHouse测试数据失败", e);
            response.put("success", false);
            response.put("message", "创建测试数据失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 查询测试数据
     */
    @GetMapping("/test-data")
    public ResponseEntity<Map<String, Object>> queryTestData() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Map<String, Object>> results =
                    clickHouseService.executeQuery("SELECT * FROM test_users ORDER BY id");
            response.put("success", true);
            response.put("message", "测试数据查询成功");
            response.put("results", results);
            response.put("count", results.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("查询ClickHouse测试数据失败", e);
            response.put("success", false);
            response.put("message", "查询测试数据失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
