# Spring ClickHouse 模块

## 概述

Spring ClickHouse 模块提供了完整的 ClickHouse 分析数据库集成解决方案，包括数据源管理、连接池优化、健康检查、监控告警等功能。该模块基于 ClickHouse JDBC 驱动和 HikariCP 连接池，为 Sprival 项目提供高性能、高可用的分析数据库服务。

## 核心特性

- ✅ **数据源管理**: 基于 Dynamic-Datasource 的多数据源支持
- ✅ **连接池优化**: 使用 HikariCP 提供高性能连接池管理
- ✅ **健康检查**: 数据库连接状态监控和健康检查
- ✅ **监控集成**: 与 Prometheus + Grafana 无缝集成
- ✅ **类型映射**: 完整的 ClickHouse 与 Java 类型映射支持
- ✅ **批量操作**: 支持批量插入、更新等操作
- ✅ **SQL 执行**: 支持原生 SQL 查询和更新操作
- ✅ **表管理**: 支持表的创建、删除、结构查询等操作

## 组件清单

### 核心组件
- [clickhouse-jdbc 0.3.2-patch11](https://github.com/ClickHouse/clickhouse-jdbc) - ClickHouse JDBC 驱动
- [dynamic-datasource 4.3.1](https://github.com/baomidou/dynamic-datasource) - 多数据源动态切换组件
- [hikari 4.0.3](https://github.com/brettwooldridge/HikariCP) - 高性能数据库连接池

### 功能组件
- **数据源管理**: Dynamic-Datasource + ClickHouse 集成
- **连接池**: HikariCP 连接池管理
- **健康检查**: 数据库状态监控
- **监控指标**: Micrometer 指标收集

## 快速开始

### 1. 添加依赖

项目已在 `pom.xml` 中配置了所需依赖：

```xml
<!-- ClickHouse JDBC Driver -->
<dependency>
    <groupId>com.clickhouse</groupId>
    <artifactId>clickhouse-jdbc</artifactId>
    <version>0.3.2-patch11</version>
</dependency>

<!-- Dynamic DataSource -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>dynamic-datasource-spring-boot-starter</artifactId>
    <version>4.3.1</version>
</dependency>
```

### 2. 基础配置

在 `application.properties` 中配置 ClickHouse 连接信息：

```properties
# 基础连接配置
spring.datasource.dynamic.datasource.clickhouse.username = default
spring.datasource.dynamic.datasource.clickhouse.password = 
spring.datasource.dynamic.datasource.clickhouse.url = jdbc:clickhouse://localhost:8123/sprival
spring.datasource.dynamic.datasource.clickhouse.driver-class-name = ru.yandex.clickhouse.ClickHouseDriver
spring.datasource.dynamic.datasource.clickhouse.type = ru.yandex.clickhouse.ClickHouseDataSource

# Sprival ClickHouse 增强配置
sprival.clickhouse.enabled = true
sprival.clickhouse.database = sprival
sprival.clickhouse.host = localhost
sprival.clickhouse.port = 8123
sprival.clickhouse.username = default
sprival.clickhouse.password = 
sprival.clickhouse.connect-timeout = 10000
sprival.clickhouse.read-timeout = 30000
sprival.clickhouse.max-connections = 20
```

### 3. 使用服务类

```java
@Service
public class MyService {
    
    @Autowired
    private ClickHouseService clickHouseService;
    
    public void queryData() {
        // 执行查询
        List<Map<String, Object>> results = clickHouseService.executeQuery("SELECT * FROM my_table LIMIT 10");
        
        // 执行更新
        int affectedRows = clickHouseService.executeUpdate("INSERT INTO my_table (id, name) VALUES (1, 'test')");
        
        // 获取数据库统计信息
        Map<String, Object> stats = clickHouseService.getDatabaseStats();
    }
}
```

## 配置详解

### 基础配置

#### 连接配置
```properties
# ClickHouse 服务器配置
sprival.clickhouse.host = localhost
sprival.clickhouse.port = 8123
sprival.clickhouse.database = sprival
sprival.clickhouse.username = default
sprival.clickhouse.password = 

# 超时配置
sprival.clickhouse.connect-timeout = 10000
sprival.clickhouse.read-timeout = 30000
```

#### 连接池配置
```properties
# 连接池配置
sprival.clickhouse.max-connections = 20
sprival.clickhouse.pool.min-idle = 2
sprival.clickhouse.pool.max-idle = 10
sprival.clickhouse.pool.max-lifetime = 1800000
sprival.clickhouse.pool.idle-timeout = 300000
sprival.clickhouse.pool.validation-query = SELECT 1
```

#### 监控配置
```properties
# 监控配置
sprival.clickhouse.monitor.enabled = true
sprival.clickhouse.monitor.health-check-interval = 30000
sprival.clickhouse.monitor.health-check-timeout = 5000
sprival.clickhouse.monitor.metrics-enabled = true
```

## 使用示例

### 1. 基础操作

#### 查询数据
```java
@Service
public class DataAnalysisService {
    
    @Autowired
    private ClickHouseService clickHouseService;
    
    // 查询用户行为数据
    public List<Map<String, Object>> getUserBehaviorData(String userId) {
        String sql = "SELECT * FROM user_behavior WHERE user_id = ? ORDER BY event_time DESC LIMIT 100";
        return clickHouseService.executeQuery(sql);
    }
    
    // 聚合查询
    public List<Map<String, Object>> getDailyStats(String date) {
        String sql = "SELECT " +
                    "    toDate(event_time) as date, " +
                    "    count() as total_events, " +
                    "    uniq(user_id) as unique_users " +
                    "FROM user_behavior " +
                    "WHERE toDate(event_time) = ? " +
                    "GROUP BY date";
        return clickHouseService.executeQuery(sql);
    }
}
```

#### 插入数据
```java
@Service
public class DataIngestionService {
    
    @Autowired
    private ClickHouseService clickHouseService;
    
    // 单条插入
    public void insertUserBehavior(UserBehavior behavior) {
        String sql = String.format(
            "INSERT INTO user_behavior (user_id, event_type, event_time, properties) VALUES (%d, '%s', '%s', '%s')",
            behavior.getUserId(),
            behavior.getEventType(),
            behavior.getEventTime(),
            behavior.getProperties()
        );
        clickHouseService.executeUpdate(sql);
    }
    
    // 批量插入
    public void batchInsertUserBehavior(List<UserBehavior> behaviors) {
        List<String> sqlList = behaviors.stream()
            .map(behavior -> String.format(
                "INSERT INTO user_behavior (user_id, event_type, event_time, properties) VALUES (%d, '%s', '%s', '%s')",
                behavior.getUserId(),
                behavior.getEventType(),
                behavior.getEventTime(),
                behavior.getProperties()
            ))
            .collect(Collectors.toList());
        
        clickHouseService.executeBatch(sqlList);
    }
}
```

### 2. 表管理

#### 创建表
```java
@Service
public class TableManagementService {
    
    @Autowired
    private ClickHouseService clickHouseService;
    
    public void createUserBehaviorTable() {
        String createTableSql = "(user_id UInt64, event_type String, event_time DateTime, properties String) " +
                               "ENGINE = MergeTree() " +
                               "ORDER BY (user_id, event_time) " +
                               "PARTITION BY toYYYYMM(event_time)";
        clickHouseService.createTable("user_behavior", createTableSql);
    }
    
    public void createAnalyticsTable() {
        String createTableSql = "(date Date, metric_name String, metric_value Float64, dimensions Map(String, String)) " +
                               "ENGINE = SummingMergeTree() " +
                               "ORDER BY (date, metric_name) " +
                               "PARTITION BY toYYYYMM(date)";
        clickHouseService.createTable("analytics_metrics", createTableSql);
    }
}
```

#### 表操作
```java
@Service
public class TableOperationsService {
    
    @Autowired
    private ClickHouseService clickHouseService;
    
    // 检查表是否存在
    public boolean isTableExists(String tableName) {
        return clickHouseService.tableExists(tableName);
    }
    
    // 获取表结构
    public List<Map<String, Object>> getTableSchema(String tableName) {
        return clickHouseService.getTableSchema(tableName);
    }
    
    // 删除表
    public void dropTable(String tableName) {
        clickHouseService.dropTable(tableName);
    }
}
```

### 3. 数据分析

#### 实时分析
```java
@Service
public class RealTimeAnalyticsService {
    
    @Autowired
    private ClickHouseService clickHouseService;
    
    // 获取实时用户活跃度
    public Map<String, Object> getRealTimeUserActivity() {
        String sql = "SELECT " +
                    "    count() as total_events, " +
                    "    uniq(user_id) as active_users, " +
                    "    max(event_time) as last_event_time " +
                    "FROM user_behavior " +
                    "WHERE event_time >= now() - INTERVAL 1 HOUR";
        
        List<Map<String, Object>> results = clickHouseService.executeQuery(sql);
        return results.isEmpty() ? new HashMap<>() : results.get(0);
    }
    
    // 获取热门事件类型
    public List<Map<String, Object>> getTopEventTypes(int limit) {
        String sql = String.format(
            "SELECT event_type, count() as event_count " +
            "FROM user_behavior " +
            "WHERE event_time >= now() - INTERVAL 24 HOUR " +
            "GROUP BY event_type " +
            "ORDER BY event_count DESC " +
            "LIMIT %d", limit
        );
        return clickHouseService.executeQuery(sql);
    }
}
```

## 监控和健康检查

### 1. 健康检查端点

访问健康检查端点：
```bash
# 应用健康检查
curl http://localhost:8338/api/actuator/health

# ClickHouse 健康检查
curl http://localhost:8338/api/actuator/health/clickhouse
```

### 2. 监控指标

#### 应用指标
- `clickhouse.connections.active` - 活跃连接数
- `clickhouse.connections.idle` - 空闲连接数
- `clickhouse.queries.total` - 查询总数
- `clickhouse.queries.duration` - 查询耗时

#### 数据库指标
- `clickhouse.database.version` - 数据库版本
- `clickhouse.database.uptime` - 数据库运行时间
- `clickhouse.tables.count` - 表数量
- `clickhouse.rows.count` - 总行数

## 测试接口

项目提供了完整的测试接口，可以通过以下端点测试 ClickHouse 功能：

### 1. 连接测试
```bash
GET /api/clickhouse/test
```

### 2. 查询操作
```bash
# 执行自定义查询
POST /api/clickhouse/query
Content-Type: application/json

{
  "sql": "SELECT version(), now()"
}

# 执行更新操作
POST /api/clickhouse/update
Content-Type: application/json

{
  "sql": "INSERT INTO test_table (id, name) VALUES (1, 'test')"
}
```

### 3. 表管理
```bash
# 创建表
POST /api/clickhouse/table
Content-Type: application/json

{
  "tableName": "test_users",
  "createTableSql": "(id UInt32, name String, age UInt8) ENGINE = MergeTree() ORDER BY id"
}

# 检查表是否存在
GET /api/clickhouse/table/test_users/exists

# 获取表结构
GET /api/clickhouse/table/test_users/schema

# 删除表
DELETE /api/clickhouse/table/test_users
```

### 4. 测试数据
```bash
# 创建测试数据
POST /api/clickhouse/test-data

# 查询测试数据
GET /api/clickhouse/test-data
```

## Java类型映射参考

ClickHouse 数据类型与 Java 类型的映射关系：

### 整数类型

| ClickHouse 类型 | Java 类型 | 说明 |
|----------------|-----------|------|
| Int8 | byte | 8位有符号整数 |
| Int16 | short | 16位有符号整数 |
| Int32 | int | 32位有符号整数 |
| Int64 | long | 64位有符号整数 |
| Int128 | String | 128位有符号整数 |
| Int256 | String | 256位有符号整数 |
| UInt8 | short | 8位无符号整数 |
| UInt16 | int | 16位无符号整数 |
| UInt32 | long | 32位无符号整数 |
| UInt64 | String | 64位无符号整数 |
| UInt128 | String | 128位无符号整数 |
| UInt256 | String | 256位无符号整数 |

### 浮点类型

| ClickHouse 类型 | Java 类型 | 说明 |
|----------------|-----------|------|
| Float32 | float/String | 32位浮点数 |
| Float64 | double/String | 64位浮点数 |
| BFloat16 | String | 16位浮点数 |

> **注意**: ClickHouse 的浮点型支持 NaN 和 Inf 值，这些特殊值只能用字符串表示。

### 小数类型

| ClickHouse 类型 | Java 类型 | 说明 |
|----------------|-----------|------|
| Decimal32 | BigDecimal | 32位小数 |
| Decimal64 | BigDecimal | 64位小数 |
| Decimal128 | BigDecimal | 128位小数 |
| Decimal256 | BigDecimal | 256位小数 |

### 字符串类型

| ClickHouse 类型 | Java 类型 | 说明 |
|----------------|-----------|------|
| String | String | 变长字符串 |
| FixedString | String | 定长字符串 |

### 日期时间类型

| ClickHouse 类型 | Java 类型 | 说明 |
|----------------|-----------|------|
| Date | Date | 日期 |
| Date32 | Date | 32位日期 |
| DateTime | Timestamp | 日期时间 |
| DateTime64 | Timestamp | 64位日期时间 |

### 其他类型

| ClickHouse 类型 | Java 类型 | 说明 |
|----------------|-----------|------|
| Enum | String | 枚举类型 |
| Bool | boolean | 布尔类型 |
| UUID | String | UUID类型 |
| Array | List | 数组类型 |
| Map | Map | 映射类型 |

## 性能优化

### 1. 连接池优化

```properties
# 连接池配置优化
sprival.clickhouse.max-connections = 50
sprival.clickhouse.pool.min-idle = 5
sprival.clickhouse.pool.max-idle = 20
sprival.clickhouse.pool.max-lifetime = 1800000
sprival.clickhouse.pool.idle-timeout = 300000
```

### 2. 查询优化

```java
@Service
public class OptimizedQueryService {
    
    @Autowired
    private ClickHouseService clickHouseService;
    
    // 使用预编译语句
    public List<Map<String, Object>> optimizedQuery(String userId) {
        String sql = "SELECT * FROM user_behavior WHERE user_id = ? ORDER BY event_time DESC LIMIT 100";
        // 注意：当前实现中需要手动处理参数，实际项目中可以使用 JdbcTemplate
        return clickHouseService.executeQuery(sql);
    }
    
    // 使用聚合函数优化
    public Map<String, Object> getAggregatedData() {
        String sql = "SELECT " +
                    "    count() as total_rows, " +
                    "    uniq(user_id) as unique_users, " +
                    "    min(event_time) as first_event, " +
                    "    max(event_time) as last_event " +
                    "FROM user_behavior";
        
        List<Map<String, Object>> results = clickHouseService.executeQuery(sql);
        return results.isEmpty() ? new HashMap<>() : results.get(0);
    }
}
```

### 3. 批量操作优化

```java
@Service
public class BatchOperationService {
    
    @Autowired
    private ClickHouseService clickHouseService;
    
    // 批量插入优化
    public void batchInsert(List<Map<String, Object>> data) {
        // 分批处理，避免单次操作数据量过大
        int batchSize = 1000;
        for (int i = 0; i < data.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, data.size());
            List<Map<String, Object>> batch = data.subList(i, endIndex);
            
            List<String> sqlList = batch.stream()
                .map(this::buildInsertSql)
                .collect(Collectors.toList());
            
            clickHouseService.executeBatch(sqlList);
        }
    }
    
    private String buildInsertSql(Map<String, Object> record) {
        // 构建插入SQL
        return String.format("INSERT INTO my_table (id, name, value) VALUES (%d, '%s', %f)",
            (Integer) record.get("id"),
            (String) record.get("name"),
            (Double) record.get("value")
        );
    }
}
```

## 部署指南

### 1. Docker 部署

```yaml
# docker-compose.yml
version: '3.8'
services:
  clickhouse:
    image: clickhouse/clickhouse-server:23.8
    container_name: sprival-clickhouse
    ports:
      - "8123:8123"
      - "9000:9000"
    environment:
      - CLICKHOUSE_DB=sprival
      - CLICKHOUSE_USER=default
      - CLICKHOUSE_PASSWORD=
    volumes:
      - clickhouse_data:/var/lib/clickhouse
      - ./clickhouse-config.xml:/etc/clickhouse-server/config.xml
    networks:
      - sprival-network

volumes:
  clickhouse_data:

networks:
  sprival-network:
    driver: bridge
```

### 2. Kubernetes 部署

```yaml
# clickhouse-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: clickhouse
spec:
  replicas: 1
  selector:
    matchLabels:
      app: clickhouse
  template:
    metadata:
      labels:
        app: clickhouse
    spec:
      containers:
      - name: clickhouse
        image: clickhouse/clickhouse-server:23.8
        ports:
        - containerPort: 8123
        - containerPort: 9000
        env:
        - name: CLICKHOUSE_DB
          value: "sprival"
        resources:
          requests:
            memory: "2Gi"
            cpu: "1000m"
          limits:
            memory: "4Gi"
            cpu: "2000m"
```

## 更新历史

| 日期 | 版本 | 更新内容 | 更新人 |
|------|------|----------|--------|
| 2025-01-08 | 1.0 | 初始创建，完整的 ClickHouse 集成方案 | AI Assistant |

## 相关链接

- [ClickHouse 官方文档](https://clickhouse.com/docs/)
- [ClickHouse JDBC 驱动](https://github.com/ClickHouse/clickhouse-jdbc)
- [Dynamic DataSource 文档](https://github.com/baomidou/dynamic-datasource)

---

*本文档提供了完整的 ClickHouse 集成方案，包括配置、使用、监控和部署指南。*