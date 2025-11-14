# MySQL

## 组件说明
- [MyBatis](https://mybatis.org/mybatis-3/)
- [MyBatis-Plus](https://mybatis.plus)
- [Dynamic-Datasource](https://github.com/baomidou/dynamic-datasource) 
- [HikariCP](https://github.com/brettwooldridge/HikariCP)
- [P6Spy](https://p6spy.readthedocs.io/)

### MyBatis
将XML或注解转为SQL，通过SqlSession操作JDBC，执行SQL，将查询返回值映射成Java对象
```
XML/注解 (Mapper) → Configuration (解析) → MappedStatement (SQL 模板)
                          ↓
                   SqlSession (入口)
                          ↓
                   Executor → StatementHandler (执行 SQL) + ParameterHandler (绑定)
                          ↓
                   ResultSetHandler (映射) → Java 对象

```

### MyBatis-Plus
MyBatis基础上增强
- CRUD接口封装，提供 insert、updateById、deleteById、selectById 等单表 CRUD 方法，一行代码搞定常见操作，减少 80% 以上 boilerplate 代码
- 代码生成，自动根据数据库表生成实体类、Mapper 接口、Service、Controller 等，集成模板引擎（如 Velocity），支持自定义模板，大幅加速项目初始化
- 分页支持，内置分页插件，支持物理/逻辑分页，结合 Page 对象自动处理 limit/offset，无需手动 SQL 修改。
- 逻辑删除，支持软删除（将 deleted=1 而非物理删除），自动拦截 SQL 注入 where 条件，防止误删数据，便于数据恢复。
- 乐观锁，版本号控制并发更新，防止数据丢失，适用于高并发场景，自动在 update SQL 中添加 version 检
- Active Record，实体类继承 Model<T>，支持 AR 模式（如 user.save() 直接保存），简化对象操作，像 JPA 一样直观。
- 插件系统，支持全局拦截插件（如 P6Spy 用于 SQL 监控），可扩展多租户、字段自动填充、SQL 注入防护等，增强安全性与可观测性。
- 其他扩展，自定义 TypeHandler 处理枚举；多租户插件隔离数据；@TableField 自动填充创建/更新时间，减少手动设置。
- 组件集成，集成hikari，druid等连接池组件，以及P6Spy组件

### Dynamic-Datasource
- 支持多数据源切换

### HikariCP
- 高性能的 Java JDBC 连接池库

### P6Spy
- SQL日志记录

## 配置说明

### 配置方式

Dynamic-Datasource支持多数据源配置，可以配置主从库、读写分离等场景。

#### 1. 单数据源配置（推荐用于开发环境）

单数据源配置适用于开发环境或简单的单库场景。

```properties
# 启用P6Spy SQL监控
spring.datasource.dynamic.p6spy = true
# 设置默认数据源
spring.datasource.dynamic.primary = master

# 基础连接配置
spring.datasource.dynamic.datasource.master.username = root
spring.datasource.dynamic.datasource.master.password = workdock
spring.datasource.dynamic.datasource.master.url = jdbc:mysql://localhost:3306/sprival?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
spring.datasource.dynamic.datasource.master.driver-class-name = com.mysql.cj.jdbc.Driver
spring.datasource.dynamic.datasource.master.type = com.zaxxer.hikari.HikariDataSource

# HikariCP连接池配置
spring.datasource.dynamic.hikari.connection_test_query = SELECT 1
spring.datasource.dynamic.hikari.is-auto-commit = true
spring.datasource.dynamic.hikari.max_pool_size = 20
spring.datasource.dynamic.hikari.min_idle = 20
spring.datasource.dynamic.hikari.max_lifetime = 600000
spring.datasource.dynamic.hikari.idle_timeout = 300000
spring.datasource.dynamic.hikari.connection_timeout = 10000
spring.datasource.dynamic.hikari.validation_timeout = 3000
spring.datasource.dynamic.hikari.leak_detection_threshold = 60000
spring.datasource.dynamic.hikari.connection_init_sql = set session wait_timeout=28800,interactive_timeout=28800;
```

#### 2. 多数据源配置（推荐用于生产环境）

多数据源配置支持主从库、读写分离等场景，适合生产环境。

```properties
# 启用P6Spy SQL监控
spring.datasource.dynamic.p6spy = true
# 设置默认数据源
spring.datasource.dynamic.primary = master

# 主库配置（写库）
spring.datasource.dynamic.datasource.master.username = root
spring.datasource.dynamic.datasource.master.password = workdock
spring.datasource.dynamic.datasource.master.url = jdbc:mysql://master-host:3306/sprival?useSSL=true&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=false
spring.datasource.dynamic.datasource.master.driver-class-name = com.mysql.cj.jdbc.Driver
spring.datasource.dynamic.datasource.master.type = com.zaxxer.hikari.HikariDataSource

# 从库配置（读库）
spring.datasource.dynamic.datasource.slave.username = root
spring.datasource.dynamic.datasource.slave.password = workdock
spring.datasource.dynamic.datasource.slave.url = jdbc:mysql://slave-host:3306/sprival?useSSL=true&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=false
spring.datasource.dynamic.datasource.slave.driver-class-name = com.mysql.cj.jdbc.Driver
spring.datasource.dynamic.datasource.slave.type = com.zaxxer.hikari.HikariDataSource

# HikariCP连接池优化配置
spring.datasource.dynamic.hikari.connection_test_query = SELECT 1
spring.datasource.dynamic.hikari.is-auto-commit = true
spring.datasource.dynamic.hikari.max_pool_size = 20
spring.datasource.dynamic.hikari.min_idle = 20
spring.datasource.dynamic.hikari.max_lifetime = 600000
spring.datasource.dynamic.hikari.idle_timeout = 300000
spring.datasource.dynamic.hikari.connection_timeout = 10000
spring.datasource.dynamic.hikari.validation_timeout = 3000
spring.datasource.dynamic.hikari.leak_detection_threshold = 60000
spring.datasource.dynamic.hikari.connection_init_sql = set session wait_timeout=28800,interactive_timeout=28800;
```

### 标准JDBC URL格式

```plaintext
jdbc:mysql://[host][:port]/[database][?propertyName1=propertyValue1[&propertyName2=propertyValue2]...]
```

## 配置项详解

### Dynamic-Datasource配置项

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `spring.datasource.dynamic.primary` | String | - | 设置默认数据源，必须配置 |
| `spring.datasource.dynamic.p6spy` | Boolean | false | 是否启用P6Spy SQL监控 |
| `spring.datasource.dynamic.datasource.{name}.username` | String | - | 数据源用户名 |
| `spring.datasource.dynamic.datasource.{name}.password` | String | - | 数据源密码 |
| `spring.datasource.dynamic.datasource.{name}.url` | String | - | 数据源JDBC URL |
| `spring.datasource.dynamic.datasource.{name}.driver-class-name` | String | - | JDBC驱动类名 |
| `spring.datasource.dynamic.datasource.{name}.type` | String | - | 数据源类型（如：com.zaxxer.hikari.HikariDataSource） |

### HikariCP配置项

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `spring.datasource.dynamic.hikari.connection_test_query` | String | - | 连接测试查询SQL |
| `spring.datasource.dynamic.hikari.is-auto-commit` | Boolean | true | 是否自动提交事务 |
| `spring.datasource.dynamic.hikari.max_pool_size` | Integer | 10 | 连接池最大连接数 |
| `spring.datasource.dynamic.hikari.min_idle` | Integer | - | 连接池最小空闲连接数 |
| `spring.datasource.dynamic.hikari.max_lifetime` | Long | 1800000 | 连接最大生存时间（毫秒） |
| `spring.datasource.dynamic.hikari.idle_timeout` | Long | 600000 | 连接空闲超时时间（毫秒） |
| `spring.datasource.dynamic.hikari.connection_timeout` | Long | 30000 | 获取连接超时时间（毫秒） |
| `spring.datasource.dynamic.hikari.validation_timeout` | Long | 5000 | 连接验证超时时间（毫秒） |
| `spring.datasource.dynamic.hikari.leak_detection_threshold` | Long | 0 | 连接泄漏检测阈值（毫秒），0表示禁用 |
| `spring.datasource.dynamic.hikari.connection_init_sql` | String | - | 连接初始化SQL语句 |

### 配置注意事项

> **重要**: 多数据源配置时，必须通过`@DS`注解或编程方式指定使用的数据源。

- **单数据源**: 适用于开发环境，配置简单，功能有限
- **多数据源**: 适用于生产环境，支持读写分离、主从切换等高级功能
- **连接池配置**: 生产环境建议根据实际负载配置合适的连接池大小
- **安全配置**: 生产环境必须启用SSL，禁用`allowPublicKeyRetrieval`
- **监控配置**: 开发环境建议启用P6Spy，生产环境建议使用Micrometer监控

## JDBC URL参数详解

### 连接配置

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `connectTimeout` | Integer | 0 | 连接超时时间（毫秒），0表示无超时 |
| `socketTimeout` | Integer | 0 | Socket超时时间（毫秒），0表示无超时 |
| `autoReconnect` | Boolean | false | 是否自动重连 |
| `maxReconnects` | Integer | 3 | 最大重连次数 |
| `initialTimeout` | Integer | 2 | 初始重连等待时间（秒） |

### SSL/TLS配置

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `useSSL` | Boolean | true | 是否使用SSL连接（MySQL 8.0+默认true） |
| `requireSSL` | Boolean | false | 是否要求SSL连接 |
| `useTLS` | Boolean | true | 是否使用TLS连接 |
| `verifyServerCertificate` | Boolean | true | 是否验证服务器证书 |
| `trustCertificateKeyStoreUrl` | String | - | 信任证书密钥库URL |
| `trustCertificateKeyStorePassword` | String | - | 信任证书密钥库密码 |
| `clientCertificateKeyStoreUrl` | String | - | 客户端证书密钥库URL |
| `clientCertificateKeyStorePassword` | String | - | 客户端证书密钥库密码 |

### 时区配置

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `serverTimezone` | String | - | 服务器时区（如：Asia/Shanghai、UTC） |
| `useLegacyDatetimeCode` | Boolean | true | 是否使用旧版日期时间代码 |

### 字符集配置

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `characterEncoding` | String | - | 字符编码（如：UTF-8） |
| `useUnicode` | Boolean | true | 是否使用Unicode字符集 |

### 连接池配置

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `cachePrepStmts` | Boolean | false | 是否缓存预处理语句 |
| `prepStmtCacheSize` | Integer | 25 | 预处理语句缓存大小 |
| `prepStmtCacheSqlLimit` | Integer | 256 | 预处理语句缓存SQL长度限制 |
| `useServerPrepStmts` | Boolean | false | 是否使用服务器端预处理语句 |

### 性能配置

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `useLocalSessionState` | Boolean | true | 是否使用本地会话状态 |
| `rewriteBatchedStatements` | Boolean | false | 是否重写批量语句 |
| `cacheResultSetMetadata` | Boolean | false | 是否缓存结果集元数据 |
| `cacheServerConfiguration` | Boolean | false | 是否缓存服务器配置 |
| `elideSetAutoCommits` | Boolean | false | 是否省略setAutoCommit调用 |
| `maintainTimeStats` | Boolean | true | 是否维护时间统计 |

### 安全配置

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `allowPublicKeyRetrieval` | Boolean | false | 是否允许公钥检索（生产环境应设为false） |
| `allowLoadLocalInfile` | Boolean | true | 是否允许LOAD LOCAL INFILE |
| `allowUrlInLocalInfile` | Boolean | false | 是否允许URL在LOAD LOCAL INFILE中 |

### 其他配置

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `zeroDateTimeBehavior` | String | EXCEPTION | 零日期时间处理方式（EXCEPTION/CONVERT_TO_NULL/ROUND） |
| `jdbcCompliantTruncation` | Boolean | true | 是否JDBC兼容截断 |
| `useInformationSchema` | Boolean | false | 是否使用INFORMATION_SCHEMA |
| `nullCatalogMeansCurrent` | Boolean | true | null目录是否表示当前目录 |
| `nullNamePatternMatchesAll` | Boolean | true | null名称模式是否匹配所有 |

## 监控指标

### hikaricp指标
|指标名称|类型|描述|监控建议|
|-------|---|------|------|
|hikaricp.connections.acquire|Timer/Histogram|记录从连接池获取连接所花费的时间分布（包括等待和创建时间）。用于检测获取连接的延迟问题，如池耗尽或慢初始化。|监控 P95/P99 分位数；如果持续高于阈值（如 100ms），可能表示池大小不足或数据库响应慢。|
|hikaricp.connections.active|Gauge|当前正在使用的活动连接数。表示应用代码中占用的连接数量。|警报：如果接近最大连接数（max），可能导致瓶颈；理想情况下保持在 50-80% 利用率。|
|hikaricp.connections.creation|Timer/Histogram|记录创建新连接所花费的时间分布。帮助识别数据库连接初始化缓慢或网络问题。|警报：突然增加（如 >1s）可能表示数据库负载高或认证延迟；监控平均值和分位数。|
|hikaricp.connections.idle|Gauge|当前空闲且可用的连接数。表示连接池的备用容量。|警报：如果持续为 0，表示连接耗尽；目标是保持一定空闲以快速响应新请求。|
|hikaricp.connections.max|Gauge (配置值)|连接池允许的最大连接数。这是静态配置值（如 HikariConfig 的 maximumPoolSize）。|不直接用于警报，但用于计算利用率（如 active / max * 100）。|
|hikaricp.connections.min|Gauge (配置值)|连接池维护的最小空闲连接数。这是静态配置值（如 HikariConfig 的 minimumIdle）。|不直接用于警报，但确保 min >= 预期最小负载以避免频繁创建。|
|hikaricp.connections.pending|Gauge|等待连接的线程数。表示连接请求积压，线程在等待可用连接。|警报：任何非零值表示潜在瓶颈；持续 >0 需调查池大小或查询优化。|
|hikaricp.connections.timeout|Counter|等待获取连接而超时的连接总数。关键指标，指示连接池耗尽。|警报：任何增加都需要立即调查；配置超时（如 connectionTimeout）以控制影响。|
|hikaricp.connections.usage|Timer/Histogram|记录连接被使用（借出）所花费的时间分布。帮助识别长运行查询或未及时释放的连接。|监控 P95 值；如果高于预期查询时间，可能表示慢 SQL 或锁争用。|


