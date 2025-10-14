# Spring MySQL 模块

## 概述

Spring MySQL 模块为Sprival项目提供完整的数据库集成解决方案，基于MyBatis-Plus和Dynamic-Datasource实现多数据源管理、高性能连接池、SQL监控等功能。

## 核心特性

- **多数据源支持**: 基于Dynamic-Datasource实现主从库动态切换
- **高性能连接池**: 使用HikariCP提供最佳性能的数据库连接池
- **SQL监控**: 集成P6Spy实现SQL执行监控和性能分析
- **ORM增强**: MyBatis-Plus提供代码生成、分页、逻辑删除等功能
- **事务管理**: 支持声明式和编程式事务管理
- **监控集成**: 与Prometheus + Grafana无缝集成

## 组件清单

### 核心组件
- [spring-kafka](https://spring.io/projects/spring-kafka) - Spring Kafka 集成框架
- [kafka-clients](https://kafka.apache.org/documentation/) - Kafka Java 客户端（通过 spring-kafka 引入）
- [spring-kafka-test](https://spring.io/projects/spring-kafka) - Kafka 测试支持

### 功能组件

## 配置说明


### 基础配置
```properties
# 启用P6Spy SQL监控
spring.datasource.dynamic.p6spy = true
# 设置默认数据源
spring.datasource.dynamic.primary = master

# 主库配置
spring.datasource.dynamic.datasource.master.username = root
spring.datasource.dynamic.datasource.master.password = workdock
spring.datasource.dynamic.datasource.master.url = jdbc:mysql://localhost:3306/sprival?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
spring.datasource.dynamic.datasource.master.driver-class-name = com.mysql.cj.jdbc.Driver
spring.datasource.dynamic.datasource.master.type = com.zaxxer.hikari.HikariDataSource

# 从库配置
spring.datasource.dynamic.datasource.slave.username = root
spring.datasource.dynamic.datasource.slave.password = workdock
spring.datasource.dynamic.datasource.slave.url = jdbc:mysql://localhost:3306/sprival?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
spring.datasource.dynamic.datasource.slave.driver-class-name = com.mysql.cj.jdbc.Driver
spring.datasource.dynamic.datasource.slave.type = com.zaxxer.hikari.HikariDataSource
```

### 高级配置
```properties
# HikariCP连接池优化配置
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
```

## 使用示例

### 基本用法
```java
@Service
public class UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    // 默认使用主库
    public User findById(Long id) {
        return userMapper.selectById(id);
    }
    
    // 强制使用从库
    @DS("slave")
    public List<User> findAll() {
        return userMapper.selectList(null);
    }
}
```

### 高级用法
```java
// 多数据源事务管理
@Service
@Transactional(rollbackFor = Exception.class)
public class UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    // 默认事务（主库）
    @Transactional
    public void createUser(User user) {
        userMapper.insert(user);
    }
    
    // 只读事务（可路由到从库）
    @Transactional(readOnly = true)
    @DS("slave")
    public List<User> getAllUsers() {
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

## 监控

### 健康检查
```bash
# 查看数据库健康状态
curl http://localhost:8338/api/actuator/health

# 查看连接池指标
curl http://localhost:8338/api/actuator/metrics/hikaricp.connections.active

# 查看所有Prometheus指标
curl http://localhost:8338/api/actuator/prometheus | grep hikaricp
```

### 监控指标
| 指标名称 | 类型 | 说明 |
|----------|------|------|
| `hikaricp.connections.active` | Gauge | 活跃连接数 |
| `hikaricp.connections.idle` | Gauge | 空闲连接数 |
| `hikaricp.connections.pending` | Gauge | 等待连接数 |
| `hikaricp.connections.acquire` | Timer | 连接获取时间 |
| `hikaricp.connections.usage` | Timer | 连接使用时间 |

## 常见问题

**Q: 为什么使用MyBatis-Plus 3.5.7而不是最新版本？**
A: 本项目使用Java 8环境，MyBatis-Plus 3.5.9+版本需要Java 11+支持。为确保兼容性，使用3.5.7版本。

**Q: 如何解决循环依赖问题？**
A: 在启动类中排除了默认的数据源自动配置：
```java
@SpringBootApplication(exclude = {
    DataSourceAutoConfiguration.class,
    DataSourceTransactionManagerAutoConfiguration.class
})
```

**Q: 多数据源事务如何处理？**
A: 建议使用`@DS`注解明确指定数据源，避免在同一事务中切换数据源。

**Q: 如何自定义数据源？**
A: 可以通过实现`DataSourceCreator`接口来自定义数据源创建逻辑。

**Q: P6Spy影响性能吗？**
A: P6Spy会有轻微的性能影响，生产环境建议通过配置过滤不必要的日志。

## 参考文档

- [MyBatis-Plus官方文档](https://baomidou.com/pages/24112f/)
- [Dynamic-Datasource文档](https://github.com/baomidou/dynamic-datasource)
- [HikariCP官方文档](https://github.com/brettwooldridge/HikariCP)
- [P6Spy官方文档](https://p6spy.readthedocs.io/)