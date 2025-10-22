# Class LoggingUtils

```java
public final class LoggingUtils
```

## 类说明

日志工具类，提供日志相关的通用工具方法。该类不能被实例化，所有方法均为静态方法。

主要功能包括获取主机名和应用名称，并对主机名进行缓存以提高性能。

## 包路径

`com.soyokra.sprival.support.logging`

## 类层次

```
java.lang.Object
  └── com.soyokra.sprival.support.logging.LoggingUtils
```

## 类修饰符

`public final` - 工具类，不可继承

## 字段

### UNKNOWN

```java
private static final String UNKNOWN = "unknown"
```

当无法获取主机名时返回的默认值。

### DEFAULT_APP_NAME

```java
private static final String DEFAULT_APP_NAME = "sprival"
```

当系统属性中未设置应用名称时返回的默认值。

### cachedHostname

```java
private static volatile String cachedHostname
```

缓存的主机名，使用 volatile 保证可见性。首次调用 `getHostname()` 时初始化，后续调用直接返回缓存值。

## 构造方法

### LoggingUtils()

```java
private LoggingUtils()
```

私有构造方法，防止工具类被实例化。

**抛出异常**:
- `UnsupportedOperationException` - 如果尝试实例化

## 方法

### getHostname()

```java
public static String getHostname()
```

获取主机名。首次调用时会获取并缓存主机名，后续调用直接返回缓存值。

该方法使用双重检查锁定（Double-Checked Locking）模式保证线程安全，并提高性能。

**返回值**: 
- `String` - 主机名；如果获取失败则返回 "unknown"

**线程安全**: 是

**性能**: 首次调用需要获取主机名（可能较慢），后续调用直接返回缓存值（极快）

**示例**:
```java
String hostname = LoggingUtils.getHostname();
System.out.println("Hostname: " + hostname);
// 输出: Hostname: myserver-01
```

### getApplicationName()

```java
public static String getApplicationName()
```

获取应用名称。从系统属性 `spring.application.name` 中获取，如果未设置则返回默认值 "sprival"。

**返回值**: 
- `String` - 应用名称

**示例**:
```java
// 设置系统属性
System.setProperty("spring.application.name", "my-app");

String appName = LoggingUtils.getApplicationName();
System.out.println("App Name: " + appName);
// 输出: App Name: my-app
```

### resetCache()

```java
static void resetCache()
```

重置缓存的主机名。该方法为包私有方法，主要用于测试场景。

**可见性**: 包私有（package-private）

**用途**: 仅用于测试

## 使用示例

### 基本使用

```java
import com.soyokra.sprival.support.logging.LoggingUtils;

public class ExampleService {
    
    public void logSystemInfo() {
        // 获取主机名
        String hostname = LoggingUtils.getHostname();
        System.out.println("Running on: " + hostname);
        
        // 获取应用名称
        String appName = LoggingUtils.getApplicationName();
        System.out.println("Application: " + appName);
    }
}
```

### 在日志消息中添加上下文信息

```java
import com.soyokra.sprival.support.logging.LoggingUtils;
import org.slf4j.MDC;

public class RequestInterceptor {
    
    public void beforeRequest() {
        // 将主机名和应用名加入 MDC
        MDC.put("hostname", LoggingUtils.getHostname());
        MDC.put("app", LoggingUtils.getApplicationName());
    }
    
    public void afterRequest() {
        MDC.clear();
    }
}
```

### 在自定义 Appender 中使用

```java
import com.soyokra.sprival.support.logging.LoggingUtils;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

public class CustomAppender extends AppenderBase<ILoggingEvent> {
    
    @Override
    protected void append(ILoggingEvent event) {
        // 构建日志消息，包含主机名和应用名
        String message = String.format(
            "[%s][%s] %s",
            LoggingUtils.getHostname(),
            LoggingUtils.getApplicationName(),
            event.getFormattedMessage()
        );
        
        // 发送日志...
    }
}
```

## 线程安全说明

### 主机名缓存的线程安全

`getHostname()` 方法使用双重检查锁定模式确保线程安全：

```java
if (cachedHostname == null) {                    // 第一次检查（不加锁）
    synchronized (LoggingUtils.class) {          // 加锁
        if (cachedHostname == null) {            // 第二次检查（加锁）
            cachedHostname = /* 获取主机名 */;
        }
    }
}
return cachedHostname;
```

**优点**:
1. 避免每次调用都加锁，提高性能
2. 确保多线程环境下主机名只被获取一次
3. 使用 volatile 确保内存可见性

## 性能考虑

### 主机名缓存

获取主机名可能涉及网络 I/O 或系统调用，相对较慢。因此该类将主机名缓存起来，避免重复获取。

**性能对比**:
- 未缓存: 每次调用约 1-10ms
- 已缓存: 每次调用约 0.001ms（快 1000-10000 倍）

### 适用场景

- ✅ 需要频繁获取主机名/应用名的场景
- ✅ 在日志消息中添加上下文信息
- ✅ 构建监控指标
- ❌ 需要动态获取主机名的场景（极少见）

## 注意事项

1. **主机名缓存**: 主机名在应用启动后被缓存，如果主机名在运行时改变（极少见），不会反映在缓存中
2. **应用名称**: 应用名称从系统属性读取，建议在应用启动时设置
3. **工具类**: 该类不能被实例化或继承

## 相关类

- [LogMessage](LogMessage.md) - 使用该工具类添加主机名和应用名
- [JettyAccessLogMessage](JettyAccessLogMessage.md) - 使用该工具类添加主机名和应用名
- [KafkaAppender](KafkaAppender.md) - 使用该工具类构建日志消息
- [KafkaRequestLog](KafkaRequestLog.md) - 使用该工具类构建访问日志消息

## 作者

Sprival Team

## 版本

1.0.0

## 另请参阅

- [日志工具参考文档](../../../../../reference/logging/README.md)

