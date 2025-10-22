# Package com.soyokra.sprival.support.logging

## 包概述

日志支持包，提供与 Kafka 集成的日志功能，支持将应用日志和 Jetty 访问日志发送到 Kafka。

## 核心功能

- 应用日志发送到 Kafka (Logback Appender)
- Jetty 访问日志发送到 Kafka
- 灵活的日志输出目标配置 (文件、Kafka、同时输出)
- 统一的日志配置管理
- 自定义日志消息格式

## 主要类

| 类名 | 描述 |
|------|------|
| [SprivalLoggingProperties](SprivalLoggingProperties.md) | 日志配置属性类 |
| [BaseKafkaLogConfig](BaseKafkaLogConfig.md) | Kafka 日志配置基类 |
| [KafkaAppender](KafkaAppender.md) | Logback Kafka 日志追加器 |
| [KafkaRequestLog](KafkaRequestLog.md) | Jetty 访问日志 Kafka 输出器 |
| [LogMessage](LogMessage.md) | 应用日志消息对象 |
| [JettyAccessLogMessage](JettyAccessLogMessage.md) | Jetty 访问日志消息对象 |
| [LogOutputTarget](LogOutputTarget.md) | 日志输出目标枚举 |
| [LoggingUtils](LoggingUtils.md) | 日志工具类 |
| [KafkaAppenderProperties](KafkaAppenderProperties.md) | KafkaAppender 配置属性 |
| [KafkaAppenderAutoConfiguration](KafkaAppenderAutoConfiguration.md) | KafkaAppender 自动配置类 |

## 使用场景

### 1. 应用日志发送到 Kafka

```java
// 在 application.properties 中配置
sprival.logging.application.output-target=kafka
sprival.logging.application.bootstrap-servers=localhost:9092
sprival.logging.application.topic=app-logs

// 在代码中使用标准的 SLF4J API
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    
    public void createUser(String username) {
        log.info("Creating user: {}", username);
    }
}
```

### 2. Jetty 访问日志发送到 Kafka

```java
// 在 application.properties 中配置
sprival.logging.jetty-access.output-target=kafka
sprival.logging.jetty-access.bootstrap-servers=localhost:9092
sprival.logging.jetty-access.topic=access-logs
```

### 3. 同时输出到文件和 Kafka

```java
// 在 application.properties 中配置
sprival.logging.application.output-target=both
sprival.logging.jetty-access.output-target=both
```

## 设计模式

- **Builder 模式**: LogMessage 和 JettyAccessLogMessage 使用 Builder 模式创建对象
- **策略模式**: LogOutputTarget 枚举定义不同的输出策略
- **模板方法模式**: KafkaAppender 继承 Logback 的 AppenderBase
- **单例模式**: LoggingUtils 中的主机名缓存

## 依赖关系

```
SprivalLoggingProperties
    ├── ApplicationLogConfig (extends BaseKafkaLogConfig)
    └── JettyAccessConfig (extends BaseKafkaLogConfig)

KafkaAppender
    ├── LogMessage
    └── LoggingUtils

KafkaRequestLog
    ├── JettyAccessLogMessage
    └── LoggingUtils
```

## 线程安全

- `KafkaAppender`: 使用 AtomicBoolean 保证线程安全
- `KafkaRequestLog`: 继承 Jetty 的 AbstractLifeCycle，线程安全
- `LoggingUtils`: 使用 volatile 和双重检查锁定保证主机名缓存的线程安全

## 性能考虑

1. **异步发送**: Kafka Producer 采用异步发送，不阻塞主线程
2. **批处理**: 支持批处理配置，提高发送效率
3. **压缩**: 支持 gzip、snappy 等压缩算法
4. **缓存**: 主机名只获取一次并缓存

## 相关文档

- [日志集成参考文档](../../../../../reference/logging/README.md)
- [项目目录结构](../../../../../reference/DIRECTORY-STRUCTURE.md)

## 版本历史

- **1.0.0** (当前版本)
  - 初始版本
  - 支持应用日志发送到 Kafka
  - 支持 Jetty 访问日志发送到 Kafka
  - 提供灵活的输出目标配置

