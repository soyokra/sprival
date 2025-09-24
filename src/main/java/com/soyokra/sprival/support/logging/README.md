# KafkaAppender

KafkaAppender是一个logback日志追加器，可以将应用程序的日志消息发送到Kafka主题中。它支持异步日志发送、自定义字段、MDC上下文信息等功能。

## 功能特性

- **异步日志发送**: 使用异步方式发送日志，不影响应用程序性能
- **JSON格式**: 日志消息以JSON格式发送到Kafka，便于后续处理
- **MDC支持**: 自动包含MDC上下文信息
- **自定义字段**: 支持添加自定义字段到日志消息
- **异常信息**: 自动包含异常堆栈信息
- **配置灵活**: 支持通过Spring Boot配置属性进行配置

## 快速开始

### 1. 启用KafkaAppender

在`application.properties`中启用KafkaAppender：

```properties
# 启用Kafka日志追加器
sprival.logging.kafka.enabled=true

# Kafka服务器配置
sprival.logging.kafka.bootstrap-servers=localhost:9092
sprival.logging.kafka.topic=application-logs
sprival.logging.kafka.client-id=kafka-appender
```

### 2. 配置logback.xml

使用提供的`logback-kafka.xml`配置文件，或者在你的logback配置中添加KafkaAppender：

```xml
<appender name="KAFKA" class="com.soyokra.sprival.support.logging.KafkaAppender">
    <bootstrapServers>${sprival.logging.kafka.bootstrap-servers:localhost:9092}</bootstrapServers>
    <topic>${sprival.logging.kafka.topic:application-logs}</topic>
    <clientId>${sprival.logging.kafka.client-id:kafka-appender}</clientId>
    <!-- 其他配置... -->
</appender>
```

### 3. 使用示例

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class ExampleService {
    private static final Logger logger = LoggerFactory.getLogger(ExampleService.class);
    
    public void processRequest() {
        // 基本日志
        logger.info("处理用户请求");
        
        // 带参数的日志
        String userId = "12345";
        logger.info("用户 {} 执行了操作", userId);
        
        // 异常日志
        try {
            // 业务逻辑
        } catch (Exception e) {
            logger.error("处理请求时发生异常", e);
        }
        
        // 使用MDC添加上下文信息
        MDC.put("userId", "12345");
        MDC.put("sessionId", "session-abc-123");
        logger.info("带有上下文的日志消息");
        MDC.clear();
    }
}
```

## 配置说明

### 基本配置

| 属性 | 默认值 | 说明 |
|------|--------|------|
| `sprival.logging.kafka.enabled` | `false` | 是否启用KafkaAppender |
| `sprival.logging.kafka.bootstrap-servers` | `localhost:9092` | Kafka服务器地址 |
| `sprival.logging.kafka.topic` | `application-logs` | 日志主题名称 |
| `sprival.logging.kafka.client-id` | `kafka-appender` | 客户端ID |

### 生产者配置

| 属性 | 默认值 | 说明 |
|------|--------|------|
| `sprival.logging.kafka.acks` | `1` | 确认机制 |
| `sprival.logging.kafka.retries` | `3` | 重试次数 |
| `sprival.logging.kafka.batch-size` | `16384` | 批处理大小 |
| `sprival.logging.kafka.linger-ms` | `1` | 延迟时间（毫秒） |
| `sprival.logging.kafka.buffer-memory` | `33554432` | 缓冲区内存大小 |
| `sprival.logging.kafka.compression-type` | `none` | 压缩类型 |
| `sprival.logging.kafka.enable-idempotence` | `false` | 是否启用幂等性 |

### 超时配置

| 属性 | 默认值 | 说明 |
|------|--------|------|
| `sprival.logging.kafka.request-timeout-ms` | `30000` | 请求超时时间（毫秒） |
| `sprival.logging.kafka.delivery-timeout-ms` | `120000` | 投递超时时间（毫秒） |
| `sprival.logging.kafka.max-block-ms` | `60000` | 最大阻塞时间（毫秒） |

### 日志配置

| 属性 | 默认值 | 说明 |
|------|--------|------|
| `sprival.logging.kafka.level` | `INFO` | 日志级别过滤 |
| `sprival.logging.kafka.include-mdc` | `true` | 是否包含MDC信息 |
| `sprival.logging.kafka.include-stack-trace` | `true` | 是否包含异常堆栈信息 |

## 日志消息格式

发送到Kafka的日志消息为JSON格式，包含以下字段：

```json
{
  "timestamp": 1640995200000,
  "level": "INFO",
  "loggerName": "com.example.Service",
  "threadName": "http-nio-8080-exec-1",
  "message": "处理用户请求",
  "throwable": null,
  "mdc": {
    "userId": "12345",
    "sessionId": "session-abc-123"
  },
  "customFields": {
    "hostname": "server-01",
    "application": "sprival"
  }
}
```

## 性能优化

### 1. 使用异步Appender

推荐使用异步Appender来避免阻塞主线程：

```xml
<appender name="ASYNC_KAFKA" class="ch.qos.logback.classic.AsyncAppender">
    <appender-ref ref="KAFKA"/>
    <queueSize>1024</queueSize>
    <discardingThreshold>0</discardingThreshold>
    <includeCallerData>false</includeCallerData>
</appender>
```

### 2. 调整批处理参数

根据日志量调整批处理参数：

```properties
# 增加批处理大小
sprival.logging.kafka.batch-size=32768
# 增加延迟时间
sprival.logging.kafka.linger-ms=5
```

### 3. 启用压缩

启用压缩可以减少网络传输：

```properties
sprival.logging.kafka.compression-type=gzip
```

## 监控和故障排除

### 1. 检查Kafka连接

确保Kafka服务器可访问：

```bash
# 检查Kafka服务器状态
kafka-topics.sh --bootstrap-server localhost:9092 --list
```

### 2. 查看日志

KafkaAppender会输出自己的日志信息，可以通过以下方式查看：

```properties
# 启用KafkaAppender调试日志
logging.level.com.soyokra.sprival.support.logging=DEBUG
```

### 3. 性能监控

监控Kafka生产者的性能指标：

- 发送速率
- 批处理大小
- 重试次数
- 错误率

## 注意事项

1. **网络延迟**: 确保应用程序与Kafka服务器之间的网络延迟较低
2. **内存使用**: 异步Appender会占用一定内存，需要合理设置队列大小
3. **日志级别**: 避免在生产环境启用DEBUG级别日志
4. **异常处理**: KafkaAppender会捕获发送异常，不会影响应用程序运行

## 故障排除

### 常见问题

1. **连接超时**: 检查Kafka服务器地址和端口
2. **主题不存在**: 确保Kafka主题已创建
3. **权限问题**: 检查Kafka访问权限
4. **序列化错误**: 检查日志消息格式

### 调试步骤

1. 启用调试日志
2. 检查Kafka服务器状态
3. 验证网络连接
4. 查看错误日志
