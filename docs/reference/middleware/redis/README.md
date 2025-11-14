# Redis

## 组件说明
- [Spring Cache](https://docs.spring.io/spring-boot/docs/2.7.18/reference/htmlsingle/#io.caching)
- [Spring Data Redis](https://docs.spring.io/spring-data/redis/docs/2.7.18/reference/html/)
- [Redisson](https://redisson.pro/docs/overview/)

```
Spring Cache (注解抽象: @Cacheable 等)
    ↓ (使用 CacheManager 接口)
RedisCacheManager (Spring Data Redis 提供)
    ↓ (依赖 RedisConnectionFactory)
RedissonConnectionFactory (Redisson 提供)
    ↓ (底层客户端)
Redisson (高级 Redis 客户端: 分布式锁、异步等)
```

### Spring Cache
- 声明式缓存

### Spring Data Redis
- 数据访问层抽象，简化 Redis 操作和 Spring 集成

### Redisson
- 分布式框架，提供高级数据结构和服务，抽象 Redis 复杂性

## 配置说明

### 配置方式

Spring Data Redis支持两种配置方式：**Host配置**和**URI配置**。

#### 1. Host配置（推荐用于单点模式）

Host配置适用于单点模式的Redis服务，配置简单直观。

```properties
# 基础连接配置
spring.redis.host = localhost
spring.redis.port = 6379
spring.redis.password = workdock
spring.redis.database = 0
spring.redis.timeout = 2000ms
spring.redis.connect-timeout = 2000ms

# Lettuce连接池配置
spring.redis.lettuce.pool.max-active = 20
spring.redis.lettuce.pool.max-idle = 10
spring.redis.lettuce.pool.min-idle = 5
spring.redis.lettuce.pool.max-wait = 2000ms
spring.redis.lettuce.pool.time-between-eviction-runs = 30s
spring.redis.lettuce.shutdown-timeout = 100ms

# Spring Cache配置
spring.cache.type = redis
spring.cache.redis.time-to-live = 600000
spring.cache.redis.cache-null-values = false
spring.cache.redis.key-prefix = sprival:
spring.cache.redis.use-key-prefix = true
spring.cache.redis.enable-statistics = true
spring.cache.cache-names = user,product,order,session
```

#### 2. URI配置（推荐用于集群和哨兵模式）

URI配置支持集群、哨兵模式和丰富的连接参数，适合生产环境。

```properties
# 单点配置示例
spring.redis.uri = redis://:workdock@localhost:6379/0

# 哨兵模式配置示例
spring.redis.sentinel.master = mymaster
spring.redis.sentinel.nodes = sentinel1:26379,sentinel2:26379,sentinel3:26379
spring.redis.sentinel.password = workdock

# 集群模式配置示例
spring.redis.cluster.nodes = node1:6379,node2:6379,node3:6379
spring.redis.cluster.password = workdock
spring.redis.cluster.max-redirects = 3

# Lettuce连接池配置
spring.redis.lettuce.pool.max-active = 20
spring.redis.lettuce.pool.max-idle = 10
spring.redis.lettuce.pool.min-idle = 5
spring.redis.lettuce.pool.max-wait = 2000ms

# Spring Cache配置
spring.cache.type = redis
spring.cache.redis.time-to-live = 600000
spring.cache.redis.cache-null-values = false
spring.cache.redis.key-prefix = sprival:
spring.cache.redis.use-key-prefix = true
spring.cache.redis.enable-statistics = true
spring.cache.cache-names = user,product,order,session
```

#### 3. Redisson配置（高级功能）

Redisson配置适用于需要分布式锁、分布式对象等高级功能的场景。

```properties
# 基础连接配置
spring.redis.host = localhost
spring.redis.port = 6379
spring.redis.password = workdock
spring.redis.database = 0

# Redisson配置
spring.redis.redisson.config.singleServerConfig.address = redis://localhost:6379
spring.redis.redisson.config.singleServerConfig.password = workdock
spring.redis.redisson.config.singleServerConfig.database = 0
spring.redis.redisson.config.singleServerConfig.connectionPoolSize = 20
spring.redis.redisson.config.singleServerConfig.connectionMinimumIdleSize = 5
spring.redis.redisson.config.singleServerConfig.idleConnectionTimeout = 10000
spring.redis.redisson.config.singleServerConfig.connectTimeout = 10000
spring.redis.redisson.config.singleServerConfig.timeout = 3000
spring.redis.redisson.config.singleServerConfig.retryAttempts = 3
spring.redis.redisson.config.singleServerConfig.retryInterval = 1500
spring.redis.redisson.config.singleServerConfig.keepAlive = true
spring.redis.redisson.config.singleServerConfig.tcpKeepAlive = true
spring.redis.redisson.config.threads = 16
spring.redis.redisson.config.nettyThreads = 32
spring.redis.redisson.config.transportMode = NIO
```

### 标准URI格式

```plaintext
redis://[password@]host[:port][/database]
rediss://[password@]host[:port][/database]  (SSL连接)
```

## 配置项详解

### Spring Data Redis配置项

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `spring.redis.host` | String | localhost | Redis服务器主机地址 |
| `spring.redis.port` | Integer | 6379 | Redis服务器端口 |
| `spring.redis.password` | String | - | Redis认证密码 |
| `spring.redis.database` | Integer | 0 | 数据库索引（0-15） |
| `spring.redis.timeout` | Duration | - | 命令超时时间 |
| `spring.redis.connect-timeout` | Duration | - | 连接超时时间 |
| `spring.redis.uri` | String | - | Redis连接URI（优先级高于host/port） |
| `spring.redis.ssl.enabled` | Boolean | false | 是否启用SSL连接 |
| `spring.redis.ssl.bundle` | String | - | SSL Bundle名称 |

### Lettuce连接池配置项

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `spring.redis.lettuce.pool.max-active` | Integer | 8 | 连接池最大连接数 |
| `spring.redis.lettuce.pool.max-idle` | Integer | 8 | 连接池最大空闲连接数 |
| `spring.redis.lettuce.pool.min-idle` | Integer | 0 | 连接池最小空闲连接数 |
| `spring.redis.lettuce.pool.max-wait` | Duration | -1 | 获取连接最大等待时间 |
| `spring.redis.lettuce.pool.time-between-eviction-runs` | Duration | - | 空闲连接回收运行间隔 |
| `spring.redis.lettuce.shutdown-timeout` | Duration | 100ms | 关闭超时时间 |
| `spring.redis.lettuce.cluster.refresh.adaptive` | Boolean | false | 是否启用自适应集群拓扑刷新 |
| `spring.redis.lettuce.cluster.refresh.period` | Duration | - | 集群拓扑刷新周期 |

### Spring Cache配置项

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `spring.cache.type` | String | - | 缓存类型（redis） |
| `spring.cache.redis.time-to-live` | Duration | - | 缓存过期时间（毫秒） |
| `spring.cache.redis.cache-null-values` | Boolean | true | 是否缓存null值 |
| `spring.cache.redis.key-prefix` | String | - | 缓存键前缀 |
| `spring.cache.redis.use-key-prefix` | Boolean | true | 是否使用键前缀 |
| `spring.cache.redis.enable-statistics` | Boolean | false | 是否启用统计 |
| `spring.cache.cache-names` | List<String> | - | 缓存名称列表 |

### Redisson配置项

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `spring.redis.redisson.config.singleServerConfig.address` | String | - | 单服务器地址 |
| `spring.redis.redisson.config.singleServerConfig.password` | String | - | 认证密码 |
| `spring.redis.redisson.config.singleServerConfig.database` | Integer | 0 | 数据库索引 |
| `spring.redis.redisson.config.singleServerConfig.connectionPoolSize` | Integer | 64 | 连接池大小 |
| `spring.redis.redisson.config.singleServerConfig.connectionMinimumIdleSize` | Integer | 24 | 最小空闲连接数 |
| `spring.redis.redisson.config.singleServerConfig.idleConnectionTimeout` | Integer | 10000 | 空闲连接超时（毫秒） |
| `spring.redis.redisson.config.singleServerConfig.connectTimeout` | Integer | 10000 | 连接超时（毫秒） |
| `spring.redis.redisson.config.singleServerConfig.timeout` | Integer | 3000 | 命令超时（毫秒） |
| `spring.redis.redisson.config.singleServerConfig.retryAttempts` | Integer | 3 | 重试次数 |
| `spring.redis.redisson.config.singleServerConfig.retryInterval` | Integer | 1500 | 重试间隔（毫秒） |
| `spring.redis.redisson.config.singleServerConfig.keepAlive` | Boolean | false | 是否保持连接 |
| `spring.redis.redisson.config.singleServerConfig.tcpKeepAlive` | Boolean | false | 是否启用TCP KeepAlive |
| `spring.redis.redisson.config.threads` | Integer | 16 | 线程数 |
| `spring.redis.redisson.config.nettyThreads` | Integer | 32 | Netty线程数 |
| `spring.redis.redisson.config.transportMode` | String | NIO | 传输模式（NIO/EPOLL/KQUEUE） |

### 配置注意事项

> **重要**: Host配置和URI配置不能同时使用，选择其中一种方式即可。

- **Host配置**: 适用于单点模式，配置简单，但功能有限
- **URI配置**: 适用于集群、哨兵模式和生产环境，功能丰富，支持更多参数
- **认证配置**: 生产环境建议使用密码认证
- **连接池配置**: 生产环境建议根据实际负载配置合适的连接池大小
- **Spring Cache vs Redisson**: 
  - Spring Cache适合简单的缓存场景，使用注解即可
  - Redisson适合需要分布式锁、分布式对象等高级功能的场景
- **SSL配置**: 生产环境建议启用SSL加密连接

## Redis URI参数详解

### 连接配置

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `timeout` | Integer | 0 | 命令超时时间（秒），0表示无超时 |
| `connectTimeout` | Integer | 0 | 连接超时时间（秒），0表示无超时 |
| `socketTimeout` | Integer | 0 | Socket超时时间（秒），0表示无超时 |

### 连接池配置

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `maxActive` | Integer | 8 | 连接池最大连接数 |
| `maxIdle` | Integer | 8 | 连接池最大空闲连接数 |
| `minIdle` | Integer | 0 | 连接池最小空闲连接数 |
| `maxWait` | Integer | -1 | 获取连接最大等待时间（毫秒），-1表示无限等待 |

### SSL/TLS配置

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `ssl` | Boolean | false | 是否使用SSL连接（rediss://协议） |
| `tls` | Boolean | false | 是否使用TLS连接 |
| `insecure` | Boolean | false | 是否允许不安全的SSL连接 |

### 认证配置

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `password` | String | - | 认证密码（在URI中通过@符号前指定） |
| `database` | Integer | 0 | 数据库索引（0-15，在URI中通过/符号后指定） |

### 集群配置

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `maxRedirects` | Integer | 3 | 集群模式下最大重定向次数 |
| `refresh` | Boolean | false | 是否启用集群拓扑自动刷新 |
| `refreshPeriod` | Integer | - | 集群拓扑刷新周期（毫秒） |

### 哨兵配置

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `master` | String | - | 哨兵模式下的主节点名称 |
| `sentinels` | String | - | 哨兵节点列表（逗号分隔） |
| `sentinelPassword` | String | - | 哨兵节点认证密码 |

## 监控指标

### spring cache指标
|指标名称|类型|描述|监控建议|
|-------|---|------|------|
|cache_gets_total|Counter|缓存查找操作的总次数，包括命中（hit，返回缓存值）和未命中（miss，新加载或 null 值）。这是一个累积计数器，可用于计算命中率（hit / total）。|监控缓存效率：查询 rate(cache_gets_total{result="hit"}[5m]) / rate(cache_gets_total[5m]) 计算 5 分钟命中率。如果 miss 率 > 20%，考虑优化缓存策略。|
|cache_puts_total|Counter|向缓存中添加条目的总次数，包括新插入或覆盖现有值。|跟踪缓存填充速率：高 put 率可能表示数据频繁更新，结合 gets 分析负载。|
|cache_removals_total|Counter|从缓存中移除条目的总次数，包括手动 evict 或过期清除。|监控缓存清理：如果 removals 率高，检查 TTL 或最大大小设置，避免内存泄漏。|
|cache_lock_duration_seconds|Counter|缓存等待锁的时间（秒），反映并发访问时的锁竞争开销。主要在 Caffeine 等支持锁的实现中出现。如果无竞争，则值为 0。|诊断并发瓶颈：如果值持续 > 0.1s，优化锁粒度或使用读写锁。适用于高并发场景。|


### Spring Data Redis指标
|指标名称|类型|描述|监控建议|
|-------|---|------|------|
|lettuce_command_completion_seconds|Histogram|Redis命令的完整执行延迟（秒），从发送到完全完成的总时间。按命令类型、本地套接字和远程端点跟踪每个完成的命令。|监控命令性能：使用histogram分位数计算p99延迟，如histogram_quantile(0.99, rate(lettuce_command_completion_seconds_bucket[5m]))。如果p99 > 1s，检查网络延迟或Redis负载。|
|lettuce_command_completion_seconds_bucket|Histogram|命令完成延迟的累积分布函数（CDF）桶，用于生成可聚合的分位数近似。仅在启用histogram时可用，默认桶从1ms到5分钟。|启用histogram以支持分位数查询：监控高分位数桶的增长率，识别尾部延迟问题。结合_count分析命令量。|
|lettuce_command_completion_seconds_count|Counter|记录的Redis命令完成次数的总计数，按命令类型、本地和远程端点分组。|跟踪命令吞吐量：查询rate(lettuce_command_completion_seconds_count[5m])计算每秒完成率。如果率突然下降，调查连接中断或错误。|
|lettuce_command_completion_seconds_max|Gauge|观察到的最大命令完成延迟（秒）。受maxLatency设置影响，当启用histogram时。|警报峰值延迟：如果max > 10s，触发警报检查异常，如Redis节点故障或网络分区。定期重置以避免旧峰值影响。|
|lettuce_command_completion_seconds_sum|Counter|所有记录的命令完成延迟的总和（秒）。用于计算平均延迟（sum / count）。|计算平均完成时间：rate(lettuce_command_completion_seconds_sum[5m]) / rate(lettuce_command_completion_seconds_count[5m])。如果平均 > 0.1s，优化命令批处理或连接池大小。|
|lettuce_command_firstresponse_seconds|Histogram|Redis命令的首次响应延迟（秒），从发送到第一个字节响应的时间。按命令类型、本地套接字和远程端点跟踪。|监控响应性：使用p95分位数如histogram_quantile(0.95, rate(lettuce_command_firstresponse_seconds_bucket[5m]))。如果p95 > 0.5s，检查Redis查询复杂性或CPU使用。|
|lettuce_command_firstresponse_seconds_bucket|Histogram|首次响应延迟的累积分布函数（CDF）桶，用于分位数近似。仅在启用histogram时可用，默认范围1ms到5分钟。|利用桶数据分析延迟分布：监控桶填充以检测延迟抖动。启用localDistinction以细粒度跟踪连接。|
|lettuce_command_firstresponse_seconds_count|Counter|记录的Redis命令首次响应次数的总计数，按命令类型分组。|评估系统响应率：rate(lettuce_command_firstresponse_seconds_count[5m])表示每秒首次响应数。高率结合低延迟表示健康系统。|
|lettuce_command_firstresponse_seconds_max|Gauge|观察到的最大首次响应延迟（秒）。histogram启用时受maxLatency限制。|检测最坏情况：如果max > 5s，调查阻塞命令或慢查询。设置警报阈值以捕获间歇性问题。|
|lettuce_command_firstresponse_seconds_sum|Counter|所有记录的首次响应延迟的总和（秒）。用于平均响应时间计算。|监控平均首次响应：rate(lettuce_command_firstresponse_seconds_sum[5m]) / rate(lettuce_command_firstresponse_seconds_count[5m])。如果平均 > 0.05s，考虑增加连接池或优化网络。|