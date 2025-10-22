# Redis模块

## 概述

Sprival Redis模块，基于Spring Cache，Spring Data Redis, Redisson实现声明式缓存, Redis操作，分布式锁等功能。

## 核心特性

- **声明式缓存**: 基于Spring Cache的注解式缓存管理
- **Redis操作**: 基于Spring Data Redis操作集成
- **分布式锁**: 基于Redisson的分布式锁实现
- **连接池管理**: 高性能连接池配置和监控
- **序列化支持**: 多种序列化方案支持（JSON、JDK、Kryo）
- **监控集成**: 与Prometheus + Grafana无缝集成
- **集群支持**: 支持Redis单机、哨兵、集群模式


## 组件清单
- [Spring Cache](https://docs.spring.io/spring-boot/docs/2.7.18/reference/htmlsingle/#io.caching) - 声明式缓存
- [Spring Data Redis](https://docs.spring.io/spring-data/redis/docs/2.7.18/reference/html/) - Redis操作集成
- [Redisson](https://redisson.pro/docs/overview/) - Redis操作增强，支持分布式锁，原子操作


## 配置说明
```properties
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