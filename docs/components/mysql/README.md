# MySQL 组件

## 简介

MySQL组件为Sprival项目提供完整的数据库集成解决方案，基于MyBatis-Plus和Dynamic-Datasource实现多数据源管理、高性能连接池、SQL监控等功能。

## 功能特性

- **多数据源支持**: 基于Dynamic-Datasource实现主从库动态切换
- **高性能连接池**: 使用HikariCP提供最佳性能的数据库连接池
- **SQL监控**: 集成P6Spy实现SQL执行监控和性能分析
- **ORM增强**: MyBatis-Plus提供代码生成、分页、逻辑删除等功能
- **事务管理**: 支持声明式和编程式事务管理
- **监控集成**: 与Prometheus + Grafana无缝集成

## 环境要求

- **Java版本**: 1.8+
- **Spring Boot版本**: 2.7.18
- **MySQL版本**: 5.7+ 或 8.0+
- **Maven版本**: 3.6+

## 快速开始

### 安装步骤
1. 项目已配置所需依赖，无需额外添加
2. 创建MySQL数据库和用户
3. 配置application.properties中的数据库连接信息
4. 启动Spring Boot应用

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

### 基础使用
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
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

// Mapper接口示例
@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 继承BaseMapper，自动获得CRUD方法
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

## 配置说明

### 配置参数
| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `spring.datasource.dynamic.primary` | String | master | 默认数据源名称 |
| `spring.datasource.dynamic.p6spy` | Boolean | false | 是否启用P6Spy监控 |
| `hikari.max_pool_size` | Integer | 10 | 连接池最大连接数 |
| `hikari.min_idle` | Integer | 5 | 连接池最小空闲连接数 |
| `hikari.max_lifetime` | Long | 1800000 | 连接最大存活时间(毫秒) |
| `hikari.idle_timeout` | Long | 600000 | 空闲连接超时时间(毫秒) |
| `hikari.connection_timeout` | Long | 30000 | 连接获取超时时间(毫秒) |
| `hikari.leak_detection_threshold` | Long | 0 | 连接泄漏检测阈值(毫秒) |

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