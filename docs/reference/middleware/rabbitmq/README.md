# RabbitMQ

## 组件说明
- [spring-boot-starter-amqp](https://spring.io/projects/spring-amqp)
- [spring-rabbit](https://github.com/spring-projects/spring-amqp) 
- [rabbitmq-java-client](https://github.com/rabbitmq/rabbitmq-java-client)

## 配置说明

### 配置方式

Spring AMQP支持两种配置方式：**Host配置**和**URI配置**。

#### 1. Host配置（推荐用于单点模式）

Host配置适用于单点模式的RabbitMQ服务，配置简单直观。

```properties
# 基础连接配置
spring.rabbitmq.host = localhost
spring.rabbitmq.port = 5672
spring.rabbitmq.username = guest
spring.rabbitmq.password = guest
spring.rabbitmq.virtual-host = /
spring.rabbitmq.connection-timeout = 15000

# 连接池配置
spring.rabbitmq.cache.connection.mode = connection
spring.rabbitmq.cache.connection.size = 10
spring.rabbitmq.cache.channel.size = 25
spring.rabbitmq.cache.channel.checkout-timeout = 30000

# 消息确认配置
spring.rabbitmq.publisher-confirm-type = correlated
spring.rabbitmq.publisher-returns = true
spring.rabbitmq.listener.simple.acknowledge-mode = manual

# 重试配置
spring.rabbitmq.listener.simple.retry.enabled = true
spring.rabbitmq.listener.simple.retry.max-attempts = 3
spring.rabbitmq.listener.simple.retry.initial-interval = 1000
spring.rabbitmq.listener.simple.retry.multiplier = 2
spring.rabbitmq.listener.simple.retry.max-interval = 10000
```

#### 2. URI配置（推荐用于生产环境）

URI配置支持SSL和丰富的连接参数，适合生产环境。

```properties
# URI配置示例
spring.rabbitmq.uri = amqp://guest:guest@localhost:5672/

# SSL配置示例
spring.rabbitmq.uri = amqps://guest:guest@localhost:5671/
spring.rabbitmq.ssl.enabled = true
spring.rabbitmq.ssl.algorithm = TLSv1.2

# 连接池配置
spring.rabbitmq.cache.connection.mode = connection
spring.rabbitmq.cache.connection.size = 10
spring.rabbitmq.cache.channel.size = 25
spring.rabbitmq.cache.channel.checkout-timeout = 30000

# 消息确认配置
spring.rabbitmq.publisher-confirm-type = correlated
spring.rabbitmq.publisher-returns = true
spring.rabbitmq.listener.simple.acknowledge-mode = manual

# 重试配置
spring.rabbitmq.listener.simple.retry.enabled = true
spring.rabbitmq.listener.simple.retry.max-attempts = 3
spring.rabbitmq.listener.simple.retry.initial-interval = 1000
spring.rabbitmq.listener.simple.retry.multiplier = 2
spring.rabbitmq.listener.simple.retry.max-interval = 10000
```

### 标准URI格式

```plaintext
amqp://[username:password@]host[:port][/virtual-host]
amqps://[username:password@]host[:port][/virtual-host]  (SSL连接)
```

## 配置项详解

### 基础连接配置项

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `spring.rabbitmq.host` | String | localhost | RabbitMQ服务器主机地址 |
| `spring.rabbitmq.port` | Integer | 5672 | RabbitMQ服务器端口（AMQP） |
| `spring.rabbitmq.username` | String | guest | 认证用户名 |
| `spring.rabbitmq.password` | String | guest | 认证密码 |
| `spring.rabbitmq.virtual-host` | String | / | 虚拟主机名称 |
| `spring.rabbitmq.uri` | String | - | RabbitMQ连接URI（优先级高于host/port） |
| `spring.rabbitmq.connection-timeout` | Duration | - | 连接超时时间（毫秒） |
| `spring.rabbitmq.requested-heartbeat` | Duration | - | 请求心跳间隔（秒） |

### 连接池配置项

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `spring.rabbitmq.cache.connection.mode` | String | channel | 连接缓存模式（channel/connection） |
| `spring.rabbitmq.cache.connection.size` | Integer | 1 | 连接缓存大小 |
| `spring.rabbitmq.cache.channel.size` | Integer | 25 | 通道缓存大小 |
| `spring.rabbitmq.cache.channel.checkout-timeout` | Duration | - | 通道检出超时时间（毫秒） |

### 消息确认配置项

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `spring.rabbitmq.publisher-confirm-type` | String | none | 发布确认类型（none/simple/correlated） |
| `spring.rabbitmq.publisher-returns` | Boolean | false | 是否启用发布返回 |
| `spring.rabbitmq.listener.simple.acknowledge-mode` | String | auto | 消费者确认模式（none/manual/auto） |
| `spring.rabbitmq.listener.simple.prefetch` | Integer | - | 消费者预取数量 |
| `spring.rabbitmq.listener.simple.concurrency` | Integer | 1 | 消费者并发数 |
| `spring.rabbitmq.listener.simple.max-concurrency` | Integer | - | 消费者最大并发数 |

### 重试配置项

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `spring.rabbitmq.listener.simple.retry.enabled` | Boolean | false | 是否启用重试 |
| `spring.rabbitmq.listener.simple.retry.max-attempts` | Integer | 3 | 最大重试次数 |
| `spring.rabbitmq.listener.simple.retry.initial-interval` | Duration | 1000ms | 初始重试间隔（毫秒） |
| `spring.rabbitmq.listener.simple.retry.multiplier` | Double | 1.0 | 重试间隔倍数 |
| `spring.rabbitmq.listener.simple.retry.max-interval` | Duration | 10000ms | 最大重试间隔（毫秒） |
| `spring.rabbitmq.listener.simple.retry.stateless` | Boolean | true | 是否无状态重试 |

### 监听器配置项

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `spring.rabbitmq.listener.simple.auto-startup` | Boolean | true | 是否自动启动监听器 |
| `spring.rabbitmq.listener.simple.default-requeue-rejected` | Boolean | true | 是否默认重新入队被拒绝的消息 |
| `spring.rabbitmq.listener.simple.idle-event-interval` | Duration | - | 空闲事件间隔 |
| `spring.rabbitmq.listener.simple.transaction-size` | Integer | - | 事务大小 |
| `spring.rabbitmq.listener.simple.missing-queues-fatal` | Boolean | true | 队列不存在时是否致命错误 |
| `spring.rabbitmq.listener.simple.consumer-batch-size` | Integer | - | 消费者批处理大小 |

### SSL/TLS配置项

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `spring.rabbitmq.ssl.enabled` | Boolean | false | 是否启用SSL |
| `spring.rabbitmq.ssl.algorithm` | String | - | SSL算法（如：TLSv1.2） |
| `spring.rabbitmq.ssl.key-store` | String | - | 密钥库路径 |
| `spring.rabbitmq.ssl.key-store-password` | String | - | 密钥库密码 |
| `spring.rabbitmq.ssl.key-store-type` | String | - | 密钥库类型 |
| `spring.rabbitmq.ssl.trust-store` | String | - | 信任库路径 |
| `spring.rabbitmq.ssl.trust-store-password` | String | - | 信任库密码 |
| `spring.rabbitmq.ssl.trust-store-type` | String | - | 信任库类型 |
| `spring.rabbitmq.ssl.verify-hostname` | Boolean | true | 是否验证主机名 |

### 配置注意事项

> **重要**: Host配置和URI配置不能同时使用，选择其中一种方式即可。

- **Host配置**: 适用于单点模式，配置简单，但功能有限
- **URI配置**: 适用于生产环境，功能丰富，支持SSL等高级参数
- **认证配置**: 生产环境必须修改默认用户名和密码
- **连接池配置**: 生产环境建议根据实际负载配置合适的连接池和通道缓存大小
- **消息确认**: 
  - 生产环境建议启用`publisher-confirm-type`和`publisher-returns`确保消息可靠投递
  - 消费者建议使用`manual`确认模式，确保消息处理完成后再确认
- **重试配置**: 
  - 建议启用重试机制，但需要合理设置重试次数和间隔
  - 注意幂等性处理，避免重复处理导致数据问题
- **SSL配置**: 生产环境建议启用SSL加密连接
- **并发配置**: 根据消息处理能力和服务器性能合理设置消费者并发数

## 监控指标

|指标名称|类型|描述|监控建议|
|-------|---|------|------|
|rabbitmq_acknowledged_published_total|Counter|已确认发布的消息总次数，表示成功发送到RabbitMQ经纪人的消息数量（使用publisher confirms）。这是一个累积计数器，可用于计算发布确认率（acknowledged / published）。|监控发布可靠性：查询 rate(rabbitmq_acknowledged_published_total[5m]) / rate(rabbitmq_published_total[5m]) 计算 5 分钟确认率。如果确认率 < 99%，检查网络或经纪人配置。|
|rabbitmq_acknowledged_total|Counter|消费者确认的消息总次数，表示成功处理的消费消息数量（使用consumer acks）。这是一个累积计数器，可用于计算消费确认率（acknowledged / consumed）。|监控消费可靠性：查询 rate(rabbitmq_acknowledged_total[5m]) / rate(rabbitmq_consumed_total[5m]) 计算 5 分钟确认率。如果确认率 < 95%，调查消费者延迟或错误处理。|
|rabbitmq_channels|Gauge|当前活动的RabbitMQ通道数，每个通道代表一个逻辑连接，用于发布或消费。|监控资源使用：如果 channels > 预期阈值（如 1000），优化通道复用或检查连接泄漏。高值可能导致性能下降。|
|rabbitmq_connections|Gauge|当前活动的RabbitMQ连接数，每个连接可支持多个通道。|监控连接健康：如果 connections 突然增加，检查客户端重连逻辑。保持在合理范围内（如 < 500）以避免资源耗尽。|
|rabbitmq_consumed_total|Counter|从RabbitMQ消费的消息总次数，包括所有接收到的消息。|跟踪消费负载：高 consumed 率表示高流量，结合 acknowledged 分析处理效率。如果 consumed 率 > 处理能力，考虑水平扩展消费者。|
|rabbitmq_failed_to_publish_total|Counter|发布消息失败的总次数，通常由于经纪人不可用或配置错误。|诊断发布问题：监控失败率 rate(rabbitmq_failed_to_publish_total[5m]) / rate(rabbitmq_published_total[5m])。如果 > 1%，实施重试机制或警报经纪人状态。|
|rabbitmq_not_acknowledged_published_total|Counter|未确认发布的消息总次数，表示可能丢失或延迟确认的消息。|监控发布确认：如果 not_acknowledged 持续增加，检查 publisher confirms 配置。目标：保持为 0 或很低。|
|rabbitmq_published_total|Counter|向RabbitMQ发布的消息总次数，包括所有尝试发送的消息。|跟踪发布流量：使用 rate(rabbitmq_published_total[5m]) 监控发布速率。高峰期结合 failed 分析瓶颈。|
|rabbitmq_rejected_total|Counter|被消费者拒绝的消息总次数，通常由于负确认（nack）或异常。|监控拒绝率：查询 rate(rabbitmq_rejected_total[5m]) / rate(rabbitmq_consumed_total[5m])。如果 > 5%，优化消费者逻辑以减少无效处理。|
|rabbitmq_unrouted_published_total|Counter|无法路由到队列的发布消息总次数，通常由于交换器/路由键问题。|诊断路由问题：如果 unrouted 率 rate(rabbitmq_unrouted_published_total[5m]) / rate(rabbitmq_published_total[5m]) > 0.1%，验证交换器绑定和路由键配置。|



