# 日志集成

## 概述

Sprival 提供了与 Kafka 集成的日志功能，支持将应用日志和 HTTP 访问日志发送到 Kafka 主题，便于日志的集中收集、分析和监控。

## 功能特性

### 核心功能

- **应用日志发送**: 基于 Logback 的 KafkaAppender，将应用日志异步发送到 Kafka
- **访问日志发送**: 集成 Jetty 访问日志，将 HTTP 请求日志发送到 Kafka
- **灵活输出**: 支持输出到文件、Kafka 或同时输出到两者
- **异步非阻塞**: 日志发送采用异步方式，不影响应用性能
- **JSON 格式**: 日志消息以 JSON 格式发送，便于后续处理
- **性能优化**: 支持批处理、压缩、缓存等性能优化机制

### 高级特性

- **MDC 支持**: 自动包含 Logback MDC 上下文信息
- **自定义字段**: 支持添加自定义字段到日志消息
- **异常堆栈**: 自动包含完整的异常堆栈信息
- **主机名缓存**: 自动添加主机名和应用名，并缓存提高性能
- **路径过滤**: 支持忽略特定路径的访问日志（如健康检查端点）

## 快速开始

### 1. 启用 Kafka 日志功能

在 `application.properties` 中配置日志输出到 Kafka：

```properties
# 应用日志输出到 Kafka
sprival.logging.application.output-target=kafka
sprival.logging.application.bootstrap-servers=localhost:9092
sprival.logging.application.topic=application-logs

# Jetty 访问日志输出到 Kafka
sprival.logging.jetty-access.output-target=kafka
sprival.logging.jetty-access.bootstrap-servers=localhost:9092
sprival.logging.jetty-access.topic=access-logs
```

### 2. 使用标准日志 API

使用标准的 SLF4J API 记录日志，无需修改代码：

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    
    public void createUser(String userId, String username) {
        // 基本日志
        log.info("Creating user: {}", username);
        
        // 使用 MDC 添加上下文信息
        try {
            MDC.put("userId", userId);
            MDC.put("action", "create_user");
            
            // 业务逻辑
            log.debug("Validating user data");
            // ...
            
            log.info("User created successfully");
            
        } catch (Exception e) {
            log.error("Failed to create user", e);
        } finally {
            MDC.clear();
        }
    }
}
```

### 3. 验证日志发送

使用 Kafka 消费者验证日志是否正确发送：

```bash
# 查看应用日志主题
kafka-console-consumer.sh --bootstrap-server localhost:9092 \
    --topic application-logs --from-beginning

# 查看访问日志主题
kafka-console-consumer.sh --bootstrap-server localhost:9092 \
    --topic access-logs --from-beginning
```

## 配置说明

### 输出目标配置

| 配置值 | 说明 | 使用场景 |
|--------|------|----------|
| `file` | 仅输出到本地文件 | 开发环境、测试环境 |
| `kafka` | 仅输出到 Kafka | 生产环境（日志集中管理） |
| `both` | 同时输出到文件和 Kafka | 生产环境（保留本地备份） |

### 应用日志配置

```properties
# 输出目标
sprival.logging.application.output-target=both

# Kafka 基本配置
sprival.logging.application.bootstrap-servers=kafka1:9092,kafka2:9092,kafka3:9092
sprival.logging.application.topic=my-app-logs
sprival.logging.application.client-id=my-app-log-producer

# 生产者配置
sprival.logging.application.acks=1
sprival.logging.application.retries=3
sprival.logging.application.batch-size=32768
sprival.logging.application.linger-ms=5
sprival.logging.application.compression-type=gzip

# 超时配置
sprival.logging.application.request-timeout-ms=30000
sprival.logging.application.delivery-timeout-ms=120000
sprival.logging.application.max-block-ms=60000
```

### Jetty 访问日志配置

```properties
# 输出目标
sprival.logging.jetty-access.output-target=kafka

# Kafka 基本配置
sprival.logging.jetty-access.bootstrap-servers=localhost:9092
sprival.logging.jetty-access.topic=jetty-access-logs
sprival.logging.jetty-access.client-id=jetty-access-producer

# 性能优化
sprival.logging.jetty-access.batch-size=32768
sprival.logging.jetty-access.linger-ms=5
sprival.logging.jetty-access.compression-type=gzip
```

### 完整配置参数

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `output-target` | String | file | 输出目标：file, kafka, both |
| `bootstrap-servers` | String | localhost:9092 | Kafka 服务器地址列表 |
| `topic` | String | application-logs / jetty-access-logs | Kafka 主题名称 |
| `client-id` | String | *-log-producer | Kafka 客户端 ID |
| `acks` | String | 1 | 确认机制：0, 1, all |
| `retries` | int | 3 | 重试次数 |
| `batch-size` | int | 16384 | 批处理大小（字节） |
| `linger-ms` | int | 1 | 批处理延迟时间（毫秒） |
| `buffer-memory` | long | 33554432 | 缓冲区内存大小（字节） |
| `compression-type` | String | none | 压缩类型：none, gzip, snappy, lz4, zstd |
| `request-timeout-ms` | int | 30000 | 请求超时时间（毫秒） |
| `delivery-timeout-ms` | int | 120000 | 投递超时时间（毫秒） |
| `max-block-ms` | int | 60000 | 最大阻塞时间（毫秒） |

## 日志消息格式

### 应用日志消息格式

发送到 Kafka 的应用日志消息为 JSON 格式：

```json
{
  "timestamp": 1729584000000,
  "level": "INFO",
  "loggerName": "com.example.UserService",
  "threadName": "http-nio-8080-exec-1",
  "message": "Creating user: alice",
  "throwable": null,
  "mdc": {
    "userId": "12345",
    "action": "create_user",
    "requestId": "req-abc-123"
  },
  "customFields": {
    "hostname": "server-01",
    "application": "sprival"
  }
}
```

**字段说明**:

- `timestamp`: Unix 时间戳（毫秒）
- `level`: 日志级别（TRACE, DEBUG, INFO, WARN, ERROR）
- `loggerName`: Logger 名称（通常是类全限定名）
- `threadName`: 线程名称
- `message`: 格式化后的日志消息
- `throwable`: 异常信息（如果有）
- `mdc`: MDC 上下文信息
- `customFields`: 自定义字段（主机名、应用名等）

### Jetty 访问日志消息格式

```json
{
  "timestamp": 1729584000000,
  "logType": "jetty-access",
  "clientIp": "192.168.1.100",
  "method": "GET",
  "uri": "/api/users/12345",
  "protocol": "HTTP/1.1",
  "statusCode": 200,
  "responseBytes": 1024,
  "processingTime": 45,
  "userAgent": "Mozilla/5.0 ...",
  "referer": "https://example.com/",
  "customFields": {
    "hostname": "server-01",
    "application": "sprival"
  }
}
```

**字段说明**:

- `timestamp`: 请求时间戳
- `logType`: 日志类型（固定为 "jetty-access"）
- `clientIp`: 客户端 IP 地址
- `method`: HTTP 方法
- `uri`: 请求 URI
- `protocol`: HTTP 协议版本
- `statusCode`: 响应状态码
- `responseBytes`: 响应字节数
- `processingTime`: 处理时间（毫秒）
- `userAgent`: User-Agent 头
- `referer`: Referer 头
- `customFields`: 自定义字段

## 使用场景

### 场景 1: 开发环境

**需求**: 本地开发时查看日志，无需 Kafka

**配置**:
```properties
sprival.logging.application.output-target=file
sprival.logging.jetty-access.output-target=file
```

**说明**: 日志仅输出到本地文件，使用标准的 Logback 文件 Appender

### 场景 2: 测试环境

**需求**: 同时保留本地日志和发送到 Kafka

**配置**:
```properties
sprival.logging.application.output-target=both
sprival.logging.jetty-access.output-target=both
sprival.logging.application.bootstrap-servers=test-kafka:9092
```

**说明**: 日志同时输出到本地文件和 Kafka，便于调试和验证

### 场景 3: 生产环境

**需求**: 日志集中收集，高性能，启用压缩

**配置**:
```properties
# 输出到 Kafka
sprival.logging.application.output-target=kafka
sprival.logging.jetty-access.output-target=kafka

# Kafka 集群地址
sprival.logging.application.bootstrap-servers=kafka1:9092,kafka2:9092,kafka3:9092

# 性能优化
sprival.logging.application.compression-type=gzip
sprival.logging.application.batch-size=32768
sprival.logging.application.linger-ms=5
sprival.logging.application.acks=1
```

**说明**: 仅输出到 Kafka，启用压缩和批处理提高性能

### 场景 4: 使用 MDC 添加追踪信息

**代码示例**:
```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import java.util.UUID;

public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    
    public void createOrder(String userId, String productId) {
        // 生成请求 ID
        String requestId = UUID.randomUUID().toString();
        
        try {
            // 添加 MDC 上下文
            MDC.put("requestId", requestId);
            MDC.put("userId", userId);
            MDC.put("action", "create_order");
            
            log.info("Starting to create order");
            
            // 业务逻辑
            validateProduct(productId);
            log.debug("Product validated: {}", productId);
            
            processPayment();
            log.info("Payment processed");
            
            saveOrder();
            log.info("Order created successfully");
            
        } catch (ValidationException e) {
            log.warn("Invalid product: {}", productId);
        } catch (PaymentException e) {
            log.error("Payment failed", e);
        } finally {
            // 清除 MDC
            MDC.clear();
        }
    }
}
```

**Kafka 中的日志消息**:
```json
{
  "timestamp": 1729584000000,
  "level": "INFO",
  "loggerName": "com.example.OrderService",
  "message": "Starting to create order",
  "mdc": {
    "requestId": "a1b2c3d4-e5f6-...",
    "userId": "user123",
    "action": "create_order"
  }
}
```

## 性能优化

### 1. 批处理优化

批处理可以显著提高日志发送效率：

```properties
# 增加批处理大小
sprival.logging.application.batch-size=32768

# 增加延迟时间，累积更多日志
sprival.logging.application.linger-ms=5
```

**效果**: 减少网络请求次数，提高吞吐量

### 2. 启用压缩

压缩可以减少网络传输量：

```properties
# 使用 gzip 压缩（通用性好）
sprival.logging.application.compression-type=gzip

# 或使用 snappy 压缩（速度快）
sprival.logging.application.compression-type=snappy
```

**压缩比对比**:
- none: 无压缩，传输速度快但流量大
- gzip: 压缩比约 4-6 倍，CPU 占用中等
- snappy: 压缩比约 2-3 倍，CPU 占用低
- lz4: 压缩比约 2-3 倍，速度最快
- zstd: 压缩比约 4-5 倍，速度较快（推荐）

### 3. 异步 Appender

使用 Logback 的 AsyncAppender 避免阻塞业务线程：

```xml
<!-- logback-kafka.xml -->
<appender name="ASYNC_KAFKA" class="ch.qos.logback.classic.AsyncAppender">
    <appender-ref ref="KAFKA"/>
    <queueSize>2048</queueSize>
    <discardingThreshold>0</discardingThreshold>
    <includeCallerData>false</includeCallerData>
    <neverBlock>true</neverBlock>
</appender>
```

**参数说明**:
- `queueSize`: 队列大小，建议 1024-4096
- `discardingThreshold`: 丢弃阈值，设为 0 不丢弃
- `includeCallerData`: 是否包含调用者信息，建议 false（性能）
- `neverBlock`: 队列满时不阻塞，建议 true

### 4. 过滤不重要的日志

通过日志级别过滤减少日志量：

```xml
<logger name="org.springframework" level="WARN"/>
<logger name="com.zaxxer.hikari" level="WARN"/>
<logger name="org.apache.kafka" level="WARN"/>
```

### 5. 主机名缓存

Sprival 自动缓存主机名，无需手动配置。首次获取后会缓存，避免重复的系统调用。

## 监控和运维

### 查看 Kafka 主题

```bash
# 列出所有主题
kafka-topics.sh --bootstrap-server localhost:9092 --list

# 查看主题详情
kafka-topics.sh --bootstrap-server localhost:9092 \
    --topic application-logs --describe
```

### 消费日志消息

```bash
# 从最新位置开始消费
kafka-console-consumer.sh --bootstrap-server localhost:9092 \
    --topic application-logs

# 从头开始消费
kafka-console-consumer.sh --bootstrap-server localhost:9092 \
    --topic application-logs --from-beginning

# 消费访问日志
kafka-console-consumer.sh --bootstrap-server localhost:9092 \
    --topic access-logs --from-beginning
```

### 查看消费者组

```bash
# 列出消费者组
kafka-consumer-groups.sh --bootstrap-server localhost:9092 --list

# 查看消费者组详情
kafka-consumer-groups.sh --bootstrap-server localhost:9092 \
    --group my-log-consumer-group --describe
```

### 监控指标

建议监控以下指标：

- **生产速率**: 每秒发送的日志消息数
- **消费速率**: 每秒消费的日志消息数
- **延迟**: 从产生到消费的延迟时间
- **错误率**: 发送失败的比例
- **队列大小**: AsyncAppender 队列的大小

## 故障排除

### 问题 1: 日志未发送到 Kafka

**症状**: Kafka 主题中没有日志消息

**排查步骤**:

1. 检查配置是否正确：
```properties
sprival.logging.application.output-target=kafka  # 确认是 kafka 或 both
sprival.logging.application.bootstrap-servers=localhost:9092  # 确认地址正确
```

2. 检查 Kafka 服务器是否可访问：
```bash
telnet localhost 9092
```

3. 查看应用日志是否有错误信息：
```
grep -i "kafka" logs/application.log
```

4. 启用 Kafka 客户端调试日志：
```properties
logging.level.org.apache.kafka=DEBUG
```

### 问题 2: 日志发送延迟高

**症状**: 日志延迟几秒甚至更久才在 Kafka 中出现

**解决方案**:

1. 减少 `linger-ms`：
```properties
sprival.logging.application.linger-ms=1
```

2. 减少 `batch-size`：
```properties
sprival.logging.application.batch-size=8192
```

3. 增加 Kafka 分区数提高并发

### 问题 3: 日志丢失

**症状**: 部分日志没有记录到 Kafka

**解决方案**:

1. 检查 AsyncAppender 配置：
```xml
<appender name="ASYNC_KAFKA" class="ch.qos.logback.classic.AsyncAppender">
    <queueSize>4096</queueSize>
    <discardingThreshold>0</discardingThreshold>  <!-- 设为 0 -->
    <neverBlock>false</neverBlock>  <!-- 改为 false -->
</appender>
```

2. 增加重试次数：
```properties
sprival.logging.application.retries=5
```

3. 使用更可靠的 acks 设置：
```properties
sprival.logging.application.acks=all
```

### 问题 4: 性能影响

**症状**: 应用响应变慢

**解决方案**:

1. 使用异步 Appender
2. 启用压缩减少网络传输
3. 增大批处理大小和延迟时间
4. 过滤不重要的日志
5. 禁用 `includeCallerData`

## 最佳实践

### 1. 日志级别

- **开发环境**: DEBUG
- **测试环境**: INFO
- **生产环境**: WARN 或 ERROR

### 2. MDC 使用

始终在 finally 块中清除 MDC：

```java
try {
    MDC.put("key", "value");
    // 业务逻辑
} finally {
    MDC.clear();
}
```

### 3. 异常日志

记录异常时提供上下文信息：

```java
try {
    processOrder(orderId);
} catch (Exception e) {
    log.error("Failed to process order: orderId={}", orderId, e);
}
```

### 4. 敏感信息

避免记录敏感信息（密码、信用卡号等）：

```java
// ❌ 错误：记录了密码
log.info("User login: username={}, password={}", username, password);

// ✅ 正确：不记录敏感信息
log.info("User login: username={}", username);
```

### 5. 结构化日志

使用 MDC 和结构化消息：

```java
MDC.put("orderId", orderId);
MDC.put("userId", userId);
log.info("Order created: amount={}, items={}", amount, itemCount);
```

### 6. 日志分类

使用不同的 topic 区分日志类型：

```properties
# 应用日志
sprival.logging.application.topic=app-logs-prod

# 访问日志
sprival.logging.jetty-access.topic=access-logs-prod

# 错误日志（可以单独配置一个 appender）
error-logs.topic=error-logs-prod
```

## 相关文档

- [API 文档 - logging 包](../../api/com/soyokra/sprival/support/logging/package-summary.md)
- [Kafka 集成](../components/kafka/README.md)
- [监控集成](../monitoring/README.md)

## 参考资料

- [Logback 官方文档](https://logback.qos.ch/documentation.html)
- [Kafka 生产者配置](https://kafka.apache.org/documentation/#producerconfigs)
- [SLF4J 用户手册](https://www.slf4j.org/manual.html)

---

*最后更新: 2025-10-22*

