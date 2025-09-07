# Spring MySQL 模块

## 概述

Spring MySQL 模块提供了完整的 MySQL 数据库集成解决方案，包括多数据源支持、连接池优化、SQL监控和MyBatis-Plus增强功能。该模块专为高性能、高可用的数据库访问而设计。

## 核心特性

- ✅ **多数据源支持**: 基于 Dynamic-Datasource 实现主从库动态切换
- ✅ **高性能连接池**: 使用 HikariCP 提供最佳性能的连接池管理
- ✅ **SQL监控**: 集成 P6Spy 实现SQL执行监控和性能分析
- ✅ **MyBatis增强**: 基于 MyBatis-Plus 提供代码生成、分页、逻辑删除等功能
- ✅ **监控集成**: 与 Prometheus + Grafana 无缝集成
- ✅ **事务管理**: 支持分布式事务和声明式事务管理

## 组件
- [mybatis-plus 3.5.7](https://github.com/baomidou/mybatis-plus) - MyBatis增强工具，提供代码生成、分页等功能（Java 8兼容版本）
- [dynamic-datasource 4.3.1](https://github.com/baomidou/dynamic-datasource) - 多数据源动态切换组件
- [mybatis 3.5.16](https://github.com/mybatis/mybatis-3) - 持久层框架
- [hikari 4.0.3](https://github.com/brettwooldridge/HikariCP) - 高性能数据库连接池
- [p6spy 3.9.1](https://github.com/p6spy/p6spy) - SQL监控和分析工具
- [mysql-connector-j 8.0.33](https://github.com/mysql/mysql-connector-j) - MySQL JDBC驱动（新坐标）

> **⚠️ 版本说明**: 本项目使用Java 8环境，MyBatis-Plus版本限制为3.5.7。3.5.9+版本需要Java 11+支持。详见[系统环境配置](../SYSTEM-ENVIRONMENT.md)。

## 快速开始

### 1. 添加依赖

项目已在 `pom.xml` 中配置了所需依赖，无需额外添加。

### 2. 数据库准备

```sql
-- 创建数据库
CREATE DATABASE IF NOT EXISTS sprival DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建示例表（可选）
USE sprival;
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

## 配置详解

### 多数据源配置

```properties
# 启用 P6Spy SQL监控
spring.datasource.dynamic.p6spy = true
# 设置默认数据源
spring.datasource.dynamic.primary = master

# 主库
spring.datasource.dynamic.datasource.master.username = root
spring.datasource.dynamic.datasource.master.password = workdock
spring.datasource.dynamic.datasource.master.url = jdbc:mysql://localhost:3306/sprival?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
spring.datasource.dynamic.datasource.master.driver-class-name = com.mysql.cj.jdbc.Driver
spring.datasource.dynamic.datasource.master.type = com.zaxxer.hikari.HikariDataSource #使用Hikaricp

# 从库
spring.datasource.dynamic.datasource.slave.username = root
spring.datasource.dynamic.datasource.slave.password = workdock
spring.datasource.dynamic.datasource.slave.url = jdbc:mysql://localhost:3306/sprival?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
spring.datasource.dynamic.datasource.slave.driver-class-name = com.mysql.cj.jdbc.Driver
spring.datasource.dynamic.datasource.slave.type = com.zaxxer.hikari.HikariDataSource #使用Hikaricp
```

### 数据源使用示例

```java
// 默认使用主库
@Service
public class UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    // 使用默认数据源（master）
    public User findById(Long id) {
        return userMapper.selectById(id);
    }
    
    // 强制使用从库
    @DS("slave")
    public List<User> findAll() {
        return userMapper.selectList(null);
    }
    
    // 动态切换数据源
    public void switchDataSource(String dsName) {
        DynamicDataSourceContextHolder.push(dsName);
        try {
            // 执行数据库操作
        } finally {
            DynamicDataSourceContextHolder.poll();
        }
    }
}
```

参考文档：[Dynamic-Datasource官方文档](https://github.com/baomidou/dynamic-datasource)


### HikariCP连接池配置

```properties
# HikariCP 全局配置（推荐配置）
spring.datasource.dynamic.hikari.connection_test_query = SELECT 1
spring.datasource.dynamic.hikari.is-auto-commit = true
spring.datasource.dynamic.hikari.max_pool_size = 20
spring.datasource.dynamic.hikari.min_idle = 5
spring.datasource.dynamic.hikari.max_lifetime = 600000
spring.datasource.dynamic.hikari.idle_timeout = 300000
spring.datasource.dynamic.hikari.connection_timeout = 10000
spring.datasource.dynamic.hikari.validation_timeout = 3000
spring.datasource.dynamic.hikari.leak_detection_threshold = 60000
spring.datasource.dynamic.hikari.connection_init_sql = set session wait_timeout=28800,interactive_timeout=28800;

# 可选：指定数据源的独立配置
spring.datasource.dynamic.datasource.master.hikari.max_pool_size = 15
spring.datasource.dynamic.datasource.slave.hikari.max_pool_size = 10
```

**配置说明：**
- `max_pool_size`: 连接池最大连接数，根据应用负载调整
- `min_idle`: 最小空闲连接数，建议设为max_pool_size的1/4
- `max_lifetime`: 连接最大存活时间（毫秒），建议10分钟
- `idle_timeout`: 空闲连接超时时间（毫秒），建议5分钟
- `leak_detection_threshold`: 连接泄漏检测阈值（毫秒），建议60秒

### 性能调优建议

| 场景 | max_pool_size | min_idle | 说明 |
|------|---------------|----------|------|
| 低并发应用 | 10-15 | 2-5 | 适合小型应用 |
| 中等并发应用 | 20-30 | 5-10 | 适合中型应用 |
| 高并发应用 | 50-100 | 10-20 | 适合大型应用 |

参考文档：[HikariCP官方文档](https://github.com/brettwooldridge/HikariCP)


## MyBatis-Plus 配置

### 功能特性

- **分页插件**: 自动处理分页查询，支持多数据库
- **乐观锁插件**: 防止并发更新冲突
- **逻辑删除**: 软删除功能，数据不会被物理删除
- **字段策略**: 智能判断字段更新策略
- **代码生成**: 支持根据数据库表生成实体类、Mapper等

### 使用示例

```java
// 实体类示例
@Data
@TableName("sys_user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String username;
    
    private String password;
    
    @TableLogic // 逻辑删除字段
    private Integer deleted;
    
    @Version // 乐观锁字段
    private Integer version;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}

// Mapper接口示例
@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 继承BaseMapper，自动获得CRUD方法
    
    // 自定义查询方法
    @Select("SELECT * FROM sys_user WHERE username = #{username}")
    User findByUsername(@Param("username") String username);
}

// Service示例
@Service
public class UserService extends ServiceImpl<UserMapper, User> {
    
    // 分页查询示例
    public IPage<User> getUserPage(int current, int size) {
        Page<User> page = new Page<>(current, size);
        return this.page(page, Wrappers.<User>lambdaQuery()
            .eq(User::getDeleted, 0)
            .orderByDesc(User::getCreateTime));
    }
}
```

## P6Spy SQL监控

### 配置说明

当前P6Spy配置（`spy.properties`）：
```properties
appender=com.p6spy.engine.spy.appender.StdoutLogger
dateformat=yyyy-MM-dd HH:mm:ss:SSS
includecategories=debug,info,error,batch,statement,commit,rollback,result
```

### 输出示例

```log
2024-01-01 12:00:00:123 | took 2ms | statement | connection 1 | SELECT id,username,email FROM sys_user WHERE deleted=0 LIMIT 10
2024-01-01 12:00:00:125 | took 1ms | commit | connection 1 | 
```

### 生产环境建议

```properties
# 生产环境建议输出到文件
appender=com.p6spy.engine.spy.appender.FileLogger
logfile=logs/spy.log
# 过滤敏感信息
excludecategories=debug
# 设置慢SQL阈值（毫秒）
executionThreshold=1000
```

## 数据库监控

### 监控指标
Spring MySQL模块集成了完整的数据库监控方案，主要监控指标包括：

#### HikariCP连接池指标
```properties
# 关键连接池指标
hikaricp.connections.active       # 活跃连接数
hikaricp.connections.idle         # 空闲连接数
hikaricp.connections.pending      # 等待连接数
hikaricp.connections.acquire      # 连接获取时间
hikaricp.connections.usage        # 连接使用时间
```

#### 数据库健康检查
```java
@Component
public class DatabaseHealthIndicator implements HealthIndicator {
    
    @Autowired
    @Qualifier("master")
    private DataSource masterDataSource;
    
    @Autowired  
    @Qualifier("slave")
    private DataSource slaveDataSource;
    
    @Override
    public Health health() {
        try {
            // 检查主库和从库连接状态
            checkDataSource(masterDataSource, "master");
            checkDataSource(slaveDataSource, "slave");
            
            return Health.up()
                .withDetail("master", "UP")
                .withDetail("slave", "UP")
                .build();
        } catch (Exception e) {
            return Health.down()
                .withDetail("error", e.getMessage())
                .build();
        }
    }
    
    private void checkDataSource(DataSource dataSource, String name) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeQuery("SELECT 1").close();
        }
    }
}
```

### 监控端点访问
```bash
# 查看数据库健康状态
curl http://localhost:8338/api/actuator/health

# 查看连接池指标
curl http://localhost:8338/api/actuator/metrics/hikaricp.connections.active

# 查看所有Prometheus指标
curl http://localhost:8338/api/actuator/prometheus | grep hikaricp
```

### 告警配置建议
```yaml
# 数据库连接池告警规则
- alert: HighDatabaseConnectionUsage
  expr: (hikaricp_connections_active / hikaricp_connections_max) * 100 > 80
  for: 2m
  labels:
    severity: warning
  annotations:
    summary: "数据库连接池使用率过高"
    
- alert: SlowDatabaseConnection  
  expr: histogram_quantile(0.95, hikaricp_connections_acquire_seconds) > 1
  for: 1m
  labels:
    severity: critical
  annotations:
    summary: "数据库连接获取时间过长"
```

> **📊 完整监控方案**: 详细的监控架构、Prometheus配置、Grafana面板等请参考 [Spring监控模块文档](../spring-monitoring/README.md)

## 事务管理

### 声明式事务

```java
@Service
@Transactional(rollbackFor = Exception.class)
public class UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    // 默认事务（主库）
    @Transactional
    public void createUser(User user) {
        userMapper.insert(user);
        // 其他业务逻辑
    }
    
    // 只读事务（可路由到从库）
    @Transactional(readOnly = true)
    @DS("slave")
    public List<User> getAllUsers() {
        return userMapper.selectList(null);
    }
    
    // 指定事务管理器
    @Transactional(transactionManager = "masterTransactionManager")
    public void updateUser(User user) {
        userMapper.updateById(user);
    }
}
```

### 编程式事务

```java
@Service
public class UserService {
    
    @Autowired
    private TransactionTemplate transactionTemplate;
    
    public void complexOperation() {
        transactionTemplate.execute(status -> {
            try {
                // 业务逻辑
                return "success";
            } catch (Exception e) {
                status.setRollbackOnly();
                throw e;
            }
        });
    }
}
```

## 常见问题

### Q1: 为什么使用MyBatis-Plus 3.5.7而不是最新版本？
A: 本项目使用Java 8环境，MyBatis-Plus 3.5.9+版本需要Java 11+支持。为确保兼容性，使用3.5.7版本：
- ✅ 3.5.7: Java 8兼容，分页插件内置
- ❌ 3.5.9+: 需要Java 11+，分页插件需要额外的`mybatis-plus-jsqlparser`依赖

### Q2: 如何解决循环依赖问题？
A: 在启动类中排除了默认的数据源自动配置：
```java
@SpringBootApplication(exclude = {
    DataSourceAutoConfiguration.class,
    DataSourceTransactionManagerAutoConfiguration.class
})
```

### Q3: 多数据源事务如何处理？
A: 建议使用 `@DS` 注解明确指定数据源，避免在同一事务中切换数据源。

### Q4: 如何自定义数据源？
A: 可以通过实现 `DataSourceCreator` 接口来自定义数据源创建逻辑。

### Q5: P6Spy影响性能吗？
A: P6Spy会有轻微的性能影响，生产环境建议通过配置过滤不必要的日志。

## 最佳实践

### 1. 数据源使用原则
- **写操作**: 统一使用主库（master）
- **读操作**: 优先使用从库（slave），提高查询性能
- **事务操作**: 在同一事务内保持使用同一数据源

### 2. 连接池配置原则
- **开发环境**: 较小的连接池配置，便于调试
- **测试环境**: 模拟生产环境的连接池配置
- **生产环境**: 根据实际负载动态调整

### 3. 监控告警建议
- 连接池使用率 > 80% 时告警
- 连接获取时间 > 1秒时告警
- 连接泄漏检测触发时告警
- SQL执行时间 > 5秒时记录慢日志

## 故障排查

### 连接池问题
```bash
# 查看连接池状态
curl http://localhost:8338/api/actuator/metrics/hikaricp.connections.active

# 查看连接池配置
curl http://localhost:8338/api/actuator/configprops | grep hikari
```

### SQL性能问题
```bash
# 查看慢SQL日志
tail -f logs/spy.log | grep "took.*ms" | awk '$3 > 1000'

# 分析SQL执行计划
# 在MySQL中使用 EXPLAIN 分析具体的慢SQL
```