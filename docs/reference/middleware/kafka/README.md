# Kafka

## 组件说明

采用官方组件[Spring Kafka](https://spring.io/projects/spring-kafka)

## 配置说明

### 配置方式

Kafka主要通过bootstrap-servers连接，配置分为三个部分：**基础连接配置**、**生产者配置**、**消费者配置**和**监听器配置**。

#### 1. 单点模式配置（推荐用于开发环境）

单点模式配置适用于开发环境或简单的单点Kafka服务，配置简单直观。

```properties
# 基础连接配置
spring.kafka.bootstrap-servers = localhost:9092
spring.kafka.client-id = sprival-client

# 生产者配置
spring.kafka.producer.key-serializer = org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer = org.springframework.kafka.support.serializer.JsonSerializer
spring.kafka.producer.acks = all
spring.kafka.producer.retries = 3
spring.kafka.producer.retry-backoff-ms = 100
spring.kafka.producer.batch-size = 16384
spring.kafka.producer.linger-ms = 1
spring.kafka.producer.buffer-memory = 33554432
spring.kafka.producer.compression-type = gzip
spring.kafka.producer.enable-idempotence = true
spring.kafka.producer.transaction-id-prefix = sprival-tx-

# 消费者配置
spring.kafka.consumer.group-id = sprival-group
spring.kafka.consumer.client-id = sprival-consumer
spring.kafka.consumer.key-deserializer = org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer = org.springframework.kafka.support.serializer.JsonDeserializer
spring.kafka.consumer.auto-offset-reset = earliest
spring.kafka.consumer.enable-auto-commit = false
spring.kafka.consumer.auto-commit-interval-ms = 1000
spring.kafka.consumer.max-poll-records = 500
spring.kafka.consumer.max-poll-interval-ms = 300000
spring.kafka.consumer.session-timeout-ms = 30000
spring.kafka.consumer.heartbeat-interval-ms = 3000

# 监听器配置
spring.kafka.listener.ack-mode = manual_immediate
spring.kafka.listener.ack-count = 10
spring.kafka.listener.ack-time = 1000
spring.kafka.listener.concurrency = 3
spring.kafka.listener.poll-timeout = 3000
spring.kafka.listener.auto-startup = true
spring.kafka.listener.missing-topics-fatal = false

# JSON反序列化配置
spring.kafka.consumer.properties.spring.json.trusted.packages = *
spring.kafka.consumer.properties.spring.json.use.type.headers = false
spring.kafka.consumer.properties.spring.json.value.default.type = java.lang.Object
```

#### 2. 集群模式配置（推荐用于生产环境）

集群模式配置支持多个Kafka节点，适合生产环境。

```properties
# 基础连接配置（多个节点用逗号分隔）
spring.kafka.bootstrap-servers = node1:9092,node2:9092,node3:9092
spring.kafka.client-id = sprival-client

# 生产者配置（生产环境优化）
spring.kafka.producer.key-serializer = org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer = org.springframework.kafka.support.serializer.JsonSerializer
spring.kafka.producer.acks = all
spring.kafka.producer.retries = 3
spring.kafka.producer.retry-backoff-ms = 100
spring.kafka.producer.batch-size = 32768
spring.kafka.producer.linger-ms = 10
spring.kafka.producer.buffer-memory = 67108864
spring.kafka.producer.compression-type = snappy
spring.kafka.producer.enable-idempotence = true
spring.kafka.producer.transaction-id-prefix = sprival-tx-

# 消费者配置（生产环境优化）
spring.kafka.consumer.group-id = sprival-group
spring.kafka.consumer.client-id = sprival-consumer
spring.kafka.consumer.key-deserializer = org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer = org.springframework.kafka.support.serializer.JsonDeserializer
spring.kafka.consumer.auto-offset-reset = earliest
spring.kafka.consumer.enable-auto-commit = false
spring.kafka.consumer.auto-commit-interval-ms = 5000
spring.kafka.consumer.max-poll-records = 500
spring.kafka.consumer.max-poll-interval-ms = 300000
spring.kafka.consumer.session-timeout-ms = 30000
spring.kafka.consumer.heartbeat-interval-ms = 3000

# 监听器配置（生产环境优化）
spring.kafka.listener.ack-mode = manual_immediate
spring.kafka.listener.ack-count = 10
spring.kafka.listener.ack-time = 1000
spring.kafka.listener.concurrency = 5
spring.kafka.listener.poll-timeout = 3000
spring.kafka.listener.auto-startup = true
spring.kafka.listener.missing-topics-fatal = false
```

### 标准Bootstrap Servers格式

```plaintext
host1:port1[,host2:port2,...[,hostN:portN]]
```

## 配置项详解

### 基础配置项

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `spring.kafka.bootstrap-servers` | List<String> | - | Kafka服务器地址列表，多个节点用逗号分隔，必须配置 |
| `spring.kafka.client-id` | String | - | 客户端ID，用于标识客户端 |

### 生产者配置项

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `spring.kafka.producer.key-serializer` | String | - | Key序列化器类名 |
| `spring.kafka.producer.value-serializer` | String | - | Value序列化器类名 |
| `spring.kafka.producer.acks` | String | 1 | 生产者确认级别（0/1/all） |
| `spring.kafka.producer.retries` | Integer | 0 | 发送失败时的重试次数 |
| `spring.kafka.producer.retry-backoff-ms` | Long | 100 | 重试间隔时间（毫秒） |
| `spring.kafka.producer.batch-size` | Integer | 16384 | 批处理大小（字节） |
| `spring.kafka.producer.linger-ms` | Long | 0 | 等待更多消息的时间（毫秒） |
| `spring.kafka.producer.buffer-memory` | Long | 33554432 | 生产者缓冲区大小（字节） |
| `spring.kafka.producer.compression-type` | String | none | 压缩类型（none/gzip/snappy/lz4/zstd） |
| `spring.kafka.producer.enable-idempotence` | Boolean | false | 是否启用幂等性 |
| `spring.kafka.producer.transaction-id-prefix` | String | - | 事务ID前缀，启用事务时使用 |

### 消费者配置项

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `spring.kafka.consumer.group-id` | String | - | 消费者组ID，必须配置 |
| `spring.kafka.consumer.client-id` | String | - | 消费者客户端ID |
| `spring.kafka.consumer.key-deserializer` | String | - | Key反序列化器类名 |
| `spring.kafka.consumer.value-deserializer` | String | - | Value反序列化器类名 |
| `spring.kafka.consumer.auto-offset-reset` | String | latest | Offset重置策略（earliest/latest/none） |
| `spring.kafka.consumer.enable-auto-commit` | Boolean | true | 是否自动提交Offset |
| `spring.kafka.consumer.auto-commit-interval-ms` | Long | 5000 | 自动提交Offset间隔（毫秒） |
| `spring.kafka.consumer.max-poll-records` | Integer | 500 | 单次拉取最大记录数 |
| `spring.kafka.consumer.max-poll-interval-ms` | Integer | 300000 | 最大拉取间隔（毫秒） |
| `spring.kafka.consumer.session-timeout-ms` | Integer | 45000 | 会话超时时间（毫秒） |
| `spring.kafka.consumer.heartbeat-interval-ms` | Integer | 3000 | 心跳间隔时间（毫秒） |

### 监听器配置项

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `spring.kafka.listener.ack-mode` | String | BATCH | 确认模式（RECORD/BATCH/TIME/MANUAL/MANUAL_IMMEDIATE） |
| `spring.kafka.listener.ack-count` | Integer | - | 批量确认的记录数（TIME模式） |
| `spring.kafka.listener.ack-time` | Long | - | 批量确认的时间间隔（毫秒，TIME模式） |
| `spring.kafka.listener.concurrency` | Integer | 1 | 监听器并发线程数 |
| `spring.kafka.listener.poll-timeout` | Long | 5000 | 拉取超时时间（毫秒） |
| `spring.kafka.listener.auto-startup` | Boolean | true | 是否自动启动监听器 |
| `spring.kafka.listener.missing-topics-fatal` | Boolean | true | Topic不存在时是否失败 |

### 配置注意事项

> **重要**: 生产者和消费者配置是独立的，可以根据实际需求分别配置。

- **单点模式**: 适用于开发环境，配置简单，但功能有限
- **集群模式**: 适用于生产环境，支持高可用和负载均衡，建议配置多个节点
- **生产者配置**: 
  - 生产环境建议设置 `acks=all` 确保消息可靠性
  - 启用 `enable-idempotence=true` 防止重复消息
  - 根据消息大小和吞吐量调整 `batch-size` 和 `linger-ms`
  - 选择合适的压缩类型（gzip/snappy/lz4）提高性能
- **消费者配置**: 
  - 必须配置 `group-id`，同一组内的消费者共享消息
  - 生产环境建议设置 `enable-auto-commit=false`，手动控制Offset提交
  - 根据消息处理时间调整 `max-poll-interval-ms`，避免被踢出消费者组
  - `auto-offset-reset=earliest` 适合需要处理历史消息的场景
- **监听器配置**: 
  - 根据消息处理速度和服务器资源调整 `concurrency`
  - 生产环境建议使用 `ack-mode=manual_immediate` 确保消息处理完成后再确认
  - 设置合适的 `poll-timeout` 平衡响应性和资源占用
- **序列化器选择**: 
  - 简单场景使用 `StringSerializer/StringDeserializer`
  - 复杂对象使用 `JsonSerializer/JsonDeserializer`，注意配置 `trusted.packages`

## 监控指标

Spring Kafka提供以下关键监控指标：

| 指标名称 | 类型 | 说明 |
|----------|------|------|
| `spring.kafka.listener` | Timer | 监听器处理消息的耗时分布，标签包括：name（容器Bean名称）、result（success/failure）、exception（异常类型） |
| `spring.kafka.template` | Timer | KafkaTemplate发送消息的耗时分布，标签包括：name（模板Bean名称）、result（success/failure）、exception（异常类型） |
| `spring.kafka.producer.record-send-total` | Counter | 生产者发送记录总数 |
| `spring.kafka.producer.record-send-rate` | Gauge | 生产者发送记录速率 |
| `spring.kafka.producer.record-error-total` | Counter | 生产者发送失败总数 |
| `spring.kafka.consumer.records-consumed-total` | Counter | 消费者消费记录总数 |
| `spring.kafka.consumer.records-consumed-rate` | Gauge | 消费者消费记录速率 |
| `spring.kafka.consumer.records-lag-max` | Gauge | 消费者最大延迟记录数 |
| `spring.kafka.consumer.fetch-rate` | Gauge | 消费者拉取速率 |

### 监控说明

- **监听器监控**: 从 Spring Kafka 2.3 版本开始，如果在类路径中检测到 Micrometer 且应用程序上下文中存在单个 `MeterRegistry`，监听器容器将自动为监听器创建和更新 Micrometer `Timer`
- **KafkaTemplate监控**: 从 Spring Kafka 2.5 版本开始，如果在类路径中检测到 Micrometer 且应用程序上下文中存在单个 `MeterRegistry`，`KafkaTemplate` 将自动为发送操作创建和更新 Micrometer `Timer`
- **指标标签**: 可以通过设置 `ContainerProperties` 和 `KafkaTemplate` 的 `micrometerTags` 属性添加额外的标签
- **监控建议**: 
  - 监控监听器处理耗时，识别慢消息处理
  - 监控生产者发送失败率，及时发现连接或配置问题
  - 监控消费者延迟（records-lag-max），确保消息及时处理
  - 监控消费速率，评估系统吞吐量

