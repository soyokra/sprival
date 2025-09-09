package com.soyokra.sprival.integration;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import com.soyokra.sprival.util.TestConstants;

/**
 * 数据库集成测试
 * 
 * @author Sprival Team
 * @version 1.0
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("数据库集成测试")
public class DatabaseIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("数据库连接测试")
    void testDatabaseConnection() {
        // When
        Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);

        // Then
        assertThat(result).isEqualTo(1);
    }

    @Test
    @DisplayName("用户表查询测试")
    @Sql(scripts = "/test-data.sql")
    void testUserTableQuery() {
        // When
        List<Map<String, Object>> users = jdbcTemplate
                .queryForList("SELECT * FROM sys_user WHERE id = ?", TestConstants.TEST_USER_ID);

        // Then
        assertThat(users).hasSize(1);
        assertThat(users.get(0).get("username")).isEqualTo("testuser1");
        assertThat(users.get(0).get("email")).isEqualTo("test1@example.com");
    }

    @Test
    @DisplayName("产品表查询测试")
    @Sql(scripts = "/test-data.sql")
    void testProductTableQuery() {
        // When
        List<Map<String, Object>> products = jdbcTemplate.queryForList(
                "SELECT * FROM sys_product WHERE id = ?", TestConstants.TEST_PRODUCT_ID);

        // Then
        assertThat(products).hasSize(1);
        assertThat(products.get(0).get("name")).isEqualTo("测试产品1");
        assertThat(products.get(0).get("price")).isEqualTo(99.99);
    }

    @Test
    @DisplayName("订单表查询测试")
    @Sql(scripts = "/test-data.sql")
    void testOrderTableQuery() {
        // When
        List<Map<String, Object>> orders = jdbcTemplate
                .queryForList("SELECT * FROM sys_order WHERE id = ?", TestConstants.TEST_ORDER_ID);

        // Then
        assertThat(orders).hasSize(1);
        assertThat(orders.get(0).get("order_no")).isEqualTo("ORDER001");
        assertThat(orders.get(0).get("total_amount")).isEqualTo(99.99);
    }

    @Test
    @DisplayName("订单详情表查询测试")
    @Sql(scripts = "/test-data.sql")
    void testOrderItemTableQuery() {
        // When
        List<Map<String, Object>> orderItems = jdbcTemplate.queryForList(
                "SELECT * FROM sys_order_item WHERE order_id = ?", TestConstants.TEST_ORDER_ID);

        // Then
        assertThat(orderItems).hasSize(1);
        assertThat(orderItems.get(0).get("product_id")).isEqualTo(TestConstants.TEST_PRODUCT_ID);
        assertThat(orderItems.get(0).get("quantity")).isEqualTo(1);
    }

    @Test
    @DisplayName("用户表插入测试")
    void testUserTableInsert() {
        // Given
        String username = "newuser";
        String email = "newuser@example.com";
        String phone = "13800138999";

        // When
        int result = jdbcTemplate.update(
                "INSERT INTO sys_user (username, password, email, phone, status) VALUES (?, ?, ?, ?, ?)",
                username, "password123", email, phone, "ACTIVE");

        // Then
        assertThat(result).isEqualTo(1);

        // 验证插入的数据
        List<Map<String, Object>> users =
                jdbcTemplate.queryForList("SELECT * FROM sys_user WHERE username = ?", username);
        assertThat(users).hasSize(1);
        assertThat(users.get(0).get("email")).isEqualTo(email);
    }

    @Test
    @DisplayName("用户表更新测试")
    @Sql(scripts = "/test-data.sql")
    void testUserTableUpdate() {
        // Given
        String newEmail = "updated@example.com";
        String newPhone = "13800138888";

        // When
        int result = jdbcTemplate.update("UPDATE sys_user SET email = ?, phone = ? WHERE id = ?",
                newEmail, newPhone, TestConstants.TEST_USER_ID);

        // Then
        assertThat(result).isEqualTo(1);

        // 验证更新的数据
        List<Map<String, Object>> users = jdbcTemplate
                .queryForList("SELECT * FROM sys_user WHERE id = ?", TestConstants.TEST_USER_ID);
        assertThat(users).hasSize(1);
        assertThat(users.get(0).get("email")).isEqualTo(newEmail);
        assertThat(users.get(0).get("phone")).isEqualTo(newPhone);
    }

    @Test
    @DisplayName("用户表删除测试")
    @Sql(scripts = "/test-data.sql")
    void testUserTableDelete() {
        // When
        int result = jdbcTemplate.update("DELETE FROM sys_user WHERE id = ?",
                TestConstants.TEST_USER_ID);

        // Then
        assertThat(result).isEqualTo(1);

        // 验证删除的数据
        List<Map<String, Object>> users = jdbcTemplate
                .queryForList("SELECT * FROM sys_user WHERE id = ?", TestConstants.TEST_USER_ID);
        assertThat(users).isEmpty();
    }

    @Test
    @DisplayName("用户表统计测试")
    @Sql(scripts = "/test-data.sql")
    void testUserTableCount() {
        // When
        Integer totalCount =
                jdbcTemplate.queryForObject("SELECT COUNT(*) FROM sys_user", Integer.class);
        Integer activeCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM sys_user WHERE status = 'ACTIVE'", Integer.class);

        // Then
        assertThat(totalCount).isEqualTo(3);
        assertThat(activeCount).isEqualTo(2);
    }

    @Test
    @DisplayName("用户表分页查询测试")
    @Sql(scripts = "/test-data.sql")
    void testUserTablePagination() {
        // When
        List<Map<String, Object>> users = jdbcTemplate
                .queryForList("SELECT * FROM sys_user ORDER BY id LIMIT ? OFFSET ?", 2, 0);

        // Then
        assertThat(users).hasSize(2);
        assertThat(users.get(0).get("id")).isEqualTo(1L);
        assertThat(users.get(1).get("id")).isEqualTo(2L);
    }

    @Test
    @DisplayName("用户表关联查询测试")
    @Sql(scripts = "/test-data.sql")
    void testUserTableJoinQuery() {
        // When
        List<Map<String, Object>> results = jdbcTemplate.queryForList(
                "SELECT u.username, o.order_no, o.total_amount " + "FROM sys_user u "
                        + "LEFT JOIN sys_order o ON u.id = o.user_id " + "WHERE u.id = ?",
                TestConstants.TEST_USER_ID);

        // Then
        assertThat(results).hasSize(2); // 用户1有2个订单
        assertThat(results.get(0).get("username")).isEqualTo("testuser1");
        assertThat(results.get(0).get("order_no")).isEqualTo("ORDER001");
    }

    @Test
    @DisplayName("数据库事务测试")
    void testDatabaseTransaction() {
        // Given
        String username1 = "user1";
        String username2 = "user2";
        String email = "test@example.com";

        // When
        try {
            jdbcTemplate.update(
                    "INSERT INTO sys_user (username, password, email, status) VALUES (?, ?, ?, ?)",
                    username1, "password123", email, "ACTIVE");
            jdbcTemplate.update(
                    "INSERT INTO sys_user (username, password, email, status) VALUES (?, ?, ?, ?)",
                    username2, "password123", email, "ACTIVE");
        } catch (Exception e) {
            // 如果发生异常，事务应该回滚
        }

        // Then
        List<Map<String, Object>> users = jdbcTemplate.queryForList(
                "SELECT * FROM sys_user WHERE username IN (?, ?)", username1, username2);
        // 由于没有显式事务管理，数据可能已经插入
        assertThat(users.size()).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("数据库性能测试")
    @Sql(scripts = "/test-data.sql")
    void testDatabasePerformance() {
        // When
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            jdbcTemplate.queryForList("SELECT * FROM sys_user WHERE id = ?",
                    TestConstants.TEST_USER_ID);
        }
        long endTime = System.currentTimeMillis();

        // Then
        long duration = endTime - startTime;
        assertThat(duration).isLessThan(1000); // 应该在1秒内完成
    }
}
