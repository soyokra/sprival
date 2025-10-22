# Jetty 日志配置指南

## 概述

Sprival 支持灵活配置 Jetty 访问日志和 Application 日志的输出目标，可以选择输出到本地文件、Kafka 或同时输出到两者。

## 功能特性

- **灵活的输出目标**：支持文件、Kafka、同时输出三种模式
- **统一配置管理**：通过 `application.properties` 集中管理日志配置
- **独立配置**：Application 日志和 Jetty 访问日志独立配置互不影响
- **动态切换**：只需修改配置文件即可切换日志输出方式

## 配置说明

### 1. Application 日志配置

Application 日志是应用程序的业务日志和框架日志。

#### 配置项

```properties
# 日志输出目标：file（文件）、kafka、both（同时输出）
sprival.logging.application.output-target = file

# Kafka服务器配置
sprival.logging.application.bootstrap-servers = localhost:9092
sprival.logging.application.topic = application-logs
sprival.logging.application.client-id = application-log-producer

# 生产者配置
sprival.logging.application.acks = 1
sprival.logging.application.retries = 3
sprival.logging.application.batch-size = 16384
sprival.logging.application.linger-ms = 1
sprival.logging.application.buffer-memory = 33554432
sprival.logging.application.compression-type = none

# 超时配置
sprival.logging.application.request-timeout-ms = 30000
sprival.logging.application.delivery-timeout-ms = 120000
sprival.logging.application.max-block-ms = 60000
```

#### 输出目标选项

- **file**：仅输出到本地文件 `logs/application.log`
- **kafka**：仅输出到 Kafka（不生成本地文件）
- **both**：同时输出到本地文件和 Kafka

### 2. Jetty 访问日志配置

Jetty 访问日志记录所有HTTP请求的访问信息。

#### 配置项

```properties
# 日志输出目标：file（文件）、kafka、both（同时输出）
sprival.logging.jetty-access.output-target = file

# Kafka服务器配置
sprival.logging.jetty-access.bootstrap-servers = localhost:9092
sprival.logging.jetty-access.topic = jetty-access-logs
sprival.logging.jetty-access.client-id = jetty-access-log-producer

# 生产者配置
sprival.logging.jetty-access.acks = 1
sprival.logging.jetty-access.retries = 3
sprival.logging.jetty-access.batch-size = 16384
sprival.logging.jetty-access.linger-ms = 1
sprival.logging.jetty-access.buffer-memory = 33554432
sprival.logging.jetty-access.compression-type = none

# 超时配置
sprival.logging.jetty-access.request-timeout-ms = 30000
sprival.logging.jetty-access.delivery-timeout-ms = 120000
sprival.logging.jetty-access.max-block-ms = 60000
```

#### 输出目标选项

- **file**：仅输出到本地文件 `logs/jetty-access.log`
- **kafka**：仅输出到 Kafka（不生成本地文件）
- **both**：同时输出到本地文件和 Kafka

## 使用场景

### 场景1：开发环境 - 仅本地文件

开发环境下通常只需要本地文件日志，方便调试：

```properties
sprival.logging.application.output-target = file
sprival.logging.jetty-access.output-target = file
```

### 场景2：生产环境 - 仅 Kafka

生产环境下可以将日志直接发送到 Kafka，由日志收集系统统一处理：

```properties
sprival.logging.application.output-target = kafka
sprival.logging.application.bootstrap-servers = kafka-cluster:9092
sprival.logging.application.topic = prod-application-logs

sprival.logging.jetty-access.output-target = kafka
sprival.logging.jetty-access.bootstrap-servers = kafka-cluster:9092
sprival.logging.jetty-access.topic = prod-jetty-access-logs
```

### 场景3：过渡期 - 同时输出

在从本地文件日志向 Kafka 迁移的过渡期，可以同时输出到两处：

```properties
sprival.logging.application.output-target = both
sprival.logging.jetty-access.output-target = both
```

## Kafka 消息格式

### Application 日志消息格式

```json
{
  "timestamp": 1729570000000,
  "level": "INFO",
  "loggerName": "com.soyokra.sprival.SprivalApplication",
  "threadName": "main",
  "message": "Application started successfully",
  "mdc": {
    "requestId": "abc123",
    "userId": "user001"
  },
  "customFields": {
    "hostname": "server01",
    "application": "sprival"
  }
}
```

### Jetty 访问日志消息格式

```json
{
  "timestamp": 1729570000000,
  "logType": "jetty-access",
  "clientIp": "192.168.1.100",
  "method": "GET",
  "uri": "/api/orders/123",
  "protocol": "HTTP/1.1",
  "statusCode": 200,
  "responseBytes": 1024,
  "processingTime": 45,
  "userAgent": "Mozilla/5.0 ...",
  "referer": "http://example.com",
  "customFields": {
    "hostname": "server01",
    "application": "sprival"
  }
}
```

## 性能考虑

### 1. Kafka 配置优化

对于高并发场景，建议调整以下参数：

```properties
# 批量大小 - 增加可提高吞吐量
sprival.logging.application.batch-size = 32768

# 延迟时间 - 增加可提高吞吐量但会增加延迟
sprival.logging.application.linger-ms = 10

# 压缩 - 启用压缩可减少网络带宽
sprival.logging.application.compression-type = gzip
```

### 2. 异步处理

Application 日志使用异步 Appender（AsyncAppender），不会阻塞业务线程。Jetty 访问日志也是异步写入的。

### 3. 资源占用

- **文件模式**：占用少量磁盘 I/O
- **Kafka模式**：占用网络带宽和少量内存（缓冲区）
- **both模式**：同时占用磁盘和网络资源

## 故障处理

### 1. Kafka 连接失败

如果 Kafka 不可用，日志系统会：
- 在内存缓冲区中保留日志消息
- 自动重试发送（根据 `retries` 配置）
- 超过重试次数后丢弃消息（避免内存溢出）
- 记录错误日志到控制台

建议：
- 设置合理的 `max-block-ms` 避免长时间阻塞
- 监控 Kafka 集群状态
- 在 Kafka 不稳定时使用 `both` 模式保留本地备份

### 2. 磁盘空间不足

使用文件模式时注意：
- 配置日志滚动策略（已在 `logback-kafka.xml` 中配置）
- 定期清理旧日志文件
- 监控磁盘空间使用情况

## 监控指标

可以通过 Actuator 端点监控日志系统状态：

```bash
# 查看健康状态
curl http://localhost:8338/api/actuator/health

# 查看指标
curl http://localhost:8338/api/actuator/metrics
```

## 注意事项

1. **配置变更**：修改日志配置后需要重启应用才能生效
2. **Kafka Topic**：确保在使用前已创建对应的 Kafka Topic
3. **权限**：确保应用有写入日志文件的权限
4. **网络**：使用 Kafka 模式时确保网络连接稳定
5. **数据安全**：敏感信息会在日志中进行脱敏处理

## 相关文档

- [Jetty 服务器配置](README.md)
- [日志规范](../../../ai-development/development-standards.md)
- [Kafka 组件配置](../../kafka/README.md)

