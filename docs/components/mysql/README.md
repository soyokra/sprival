# Spring MySQL 模块

## 概述

Spring MySQL 模块为Sprival项目提供完整的数据库集成解决方案，基于MyBatis-Plus和Dynamic-Datasource实现查询构造，多数据源管理、高性能连接池、SQL监控等功能。

## 核心特性

- **多数据源支持**: 基于Dynamic-Datasource实现主从库动态切换
- **高性能连接池**: 使用HikariCP提供最佳性能的数据库连接池
- **SQL监控**: 集成P6Spy实现SQL执行监控和性能分析
- **ORM增强**: MyBatis-Plus提供代码生成、分页、查询构造等功能
- **事务管理**: 支持声明式和编程式事务管理
- **监控集成**: 与Prometheus + Grafana无缝集成

## 组件清单

### 核心组件
- [MyBatis-Plus](https://mybatis.plus) - mybatis增强
- [Dynamic-Datasource](https://github.com/baomidou/dynamic-datasource) - 动态数据源
- [HikariCP官方文档](https://github.com/brettwooldridge/HikariCP) - 数据库连接池
- [P6Spy官方文档](https://p6spy.readthedocs.io/) - sql日志


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
spring.datasource.dynamic.hikari.min_idle = 20
spring.datasource.dynamic.hikari.max_lifetime = 600000
spring.datasource.dynamic.hikari.idle_timeout = 300000
spring.datasource.dynamic.hikari.connection_timeout = 10000
spring.datasource.dynamic.hikari.validation_timeout = 3000
spring.datasource.dynamic.hikari.leak_detection_threshold = 60000
spring.datasource.dynamic.hikari.connection_init_sql = set session wait_timeout=28800,interactive_timeout=28800;
```

## 监控

### 连接池指标
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


### 监控示例
连接池数量监控
```promsql
# 总连接数
hikaricp_connections{instance="host.docker.internal:8338", job="spring-boot-app", pool="master"}
# 当前活动连接数
hikaricp_connections_active{instance="host.docker.internal:8338", job="spring-boot-app", pool="master"}
# 当前空闲连接数
hikaricp_connections_idle{instance="host.docker.internal:8338", job="spring-boot-app", pool="master"}
# 等待连接的线程数
hikaricp_connections_pending{instance="host.docker.internal:8338", job="spring-boot-app", pool="master"}
# 连接池允许的最大连接数
hikaricp_connections_min{instance="host.docker.internal:8338", job="spring-boot-app", pool="master"}
# 连接池维护的最小空闲连接数
hikaricp_connections_max{instance="host.docker.internal:8338", job="spring-boot-app", pool="master"}
```


