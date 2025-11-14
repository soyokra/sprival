# MySQL

## 组件说明

### 技术选型

采用「ORM 框架 + 多数据源管理 + 连接池 + 可观测性工具」的组合方案，覆盖 MySQL 操作全场景需求。

| 组件 | 选型理由 |
|------|---------|
| [MyBatis](https://mybatis.org/mybatis-3/) | 成熟的半自动 ORM 框架，提供灵活的 SQL 映射能力，支持复杂查询场景 |
| [MyBatis-Plus](https://mybatis.plus) | MyBatis 增强工具，提供单表 CRUD 无代码化、自动分页、代码生成等能力，提升开发效率 |
| [Dynamic-Datasource](https://github.com/baomidou/dynamic-datasource) | 轻量级多数据源管理框架，支持主从分离、读写分离等场景，与 MyBatis-Plus 深度集成 |
| [HikariCP](https://github.com/brettwooldridge/HikariCP) | 高性能 JDBC 连接池，Spring Boot 默认连接池，零开销特性保障连接管理效率 |
| [P6Spy](https://p6spy.readthedocs.io/) | 无侵入式 SQL 监控工具，自动记录 SQL 执行日志，辅助调试与性能分析 |

### 架构设计

采用分层架构设计，各层职责清晰，组件协作紧密：

```
应用层
  ↓
ORM 层（MyBatis + MyBatis-Plus）
  ↓
数据源管理层（Dynamic-Datasource）
  ↓
连接池层（HikariCP）
  ↓
数据库（MySQL）
  ↑
可观测层（P6Spy）
```

**架构层次说明：**

- **ORM 层**：MyBatis 提供核心 SQL 映射能力，MyBatis-Plus 提供增强功能（单表 CRUD、分页、代码生成等），两者完全兼容，只增强不改变
- **数据源管理层**：Dynamic-Datasource 实现多数据源动态切换，支持主从分离、读写分离等场景，通过注解简化切换逻辑
- **连接池层**：HikariCP 提供高性能连接池管理，保障数据库连接的高效复用与稳定性
- **可观测层**：P6Spy 无侵入式拦截数据库操作，记录 SQL 执行日志（含参数、耗时等），辅助调试与性能分析

**组件协作关系：**

- MyBatis-Plus 基于 MyBatis 扩展，提供增强能力的同时保持完全兼容
- Dynamic-Datasource 管理多个数据源，每个数据源使用 HikariCP 作为连接池
- P6Spy 在连接池层进行拦截，监控所有数据库操作，无需修改业务代码


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
spring.datasource.dynamic.datasource.master.password = ${DB_PASSWORD:your_password}
spring.datasource.dynamic.datasource.master.url = jdbc:mysql://localhost:3306/sprival?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
spring.datasource.dynamic.datasource.master.driver-class-name = com.mysql.cj.jdbc.Driver
spring.datasource.dynamic.datasource.master.type = com.zaxxer.hikari.HikariDataSource

# HikariCP连接池配置
spring.datasource.dynamic.hikari.connectionTestQuery = SELECT 1
spring.datasource.dynamic.hikari.autoCommit = true
spring.datasource.dynamic.hikari.maximumPoolSize = 20
spring.datasource.dynamic.hikari.minimumIdle = 20
spring.datasource.dynamic.hikari.maxLifetime = 600000
spring.datasource.dynamic.hikari.idleTimeout = 300000
spring.datasource.dynamic.hikari.connectionTimeout = 10000
spring.datasource.dynamic.hikari.validationTimeout = 3000
spring.datasource.dynamic.hikari.leakDetectionThreshold = 60000
spring.datasource.dynamic.hikari.connectionInitSql = set session wait_timeout=28800,interactive_timeout=28800;
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
spring.datasource.dynamic.datasource.master.password = ${DB_PASSWORD:your_password}
spring.datasource.dynamic.datasource.master.url = jdbc:mysql://master-host:3306/sprival?useSSL=true&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=false
spring.datasource.dynamic.datasource.master.driver-class-name = com.mysql.cj.jdbc.Driver
spring.datasource.dynamic.datasource.master.type = com.zaxxer.hikari.HikariDataSource

# 从库配置（读库）
spring.datasource.dynamic.datasource.slave.username = root
spring.datasource.dynamic.datasource.slave.password = ${DB_PASSWORD:your_password}
spring.datasource.dynamic.datasource.slave.url = jdbc:mysql://slave-host:3306/sprival?useSSL=true&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=false
spring.datasource.dynamic.datasource.slave.driver-class-name = com.mysql.cj.jdbc.Driver
spring.datasource.dynamic.datasource.slave.type = com.zaxxer.hikari.HikariDataSource

# HikariCP连接池优化配置
spring.datasource.dynamic.hikari.connectionTestQuery = SELECT 1
spring.datasource.dynamic.hikari.autoCommit = true
spring.datasource.dynamic.hikari.maximumPoolSize = 20
spring.datasource.dynamic.hikari.minimumIdle = 20
spring.datasource.dynamic.hikari.maxLifetime = 600000
spring.datasource.dynamic.hikari.idleTimeout = 300000
spring.datasource.dynamic.hikari.connectionTimeout = 10000
spring.datasource.dynamic.hikari.validationTimeout = 3000
spring.datasource.dynamic.hikari.leakDetectionThreshold = 60000
spring.datasource.dynamic.hikari.connectionInitSql = set session wait_timeout=28800,interactive_timeout=28800;
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
| `spring.datasource.dynamic.hikari.connectionTestQuery` | String | - | 连接测试查询SQL |
| `spring.datasource.dynamic.hikari.autoCommit` | Boolean | true | 是否自动提交事务 |
| `spring.datasource.dynamic.hikari.maximumPoolSize` | Integer | 10 | 连接池最大连接数 |
| `spring.datasource.dynamic.hikari.minimumIdle` | Integer | 10 | 连接池最小空闲连接数（默认等于maximumPoolSize） |
| `spring.datasource.dynamic.hikari.maxLifetime` | Long | 1800000 | 连接最大生存时间（毫秒） |
| `spring.datasource.dynamic.hikari.idleTimeout` | Long | 600000 | 连接空闲超时时间（毫秒） |
| `spring.datasource.dynamic.hikari.connectionTimeout` | Long | 30000 | 获取连接超时时间（毫秒） |
| `spring.datasource.dynamic.hikari.validationTimeout` | Long | 5000 | 连接验证超时时间（毫秒） |
| `spring.datasource.dynamic.hikari.leakDetectionThreshold` | Long | 0 | 连接泄漏检测阈值（毫秒），0表示禁用 |
| `spring.datasource.dynamic.hikari.connectionInitSql` | String | - | 连接初始化SQL语句 |

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


