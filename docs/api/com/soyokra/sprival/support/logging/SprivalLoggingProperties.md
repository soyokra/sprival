# Class SprivalLoggingProperties

```java
@Data
@Component
@ConfigurationProperties(prefix = "sprival.logging")
public class SprivalLoggingProperties
```

## 类说明

Sprival 日志配置属性类，统一管理应用日志和 Jetty 访问日志的输出配置。

该类使用 Spring Boot 的 `@ConfigurationProperties` 机制，从 `application.properties` 中读取以 `sprival.logging` 为前缀的配置项。

## 包路径

`com.soyokra.sprival.support.logging`

## 类层次

```
java.lang.Object
  └── com.soyokra.sprival.support.logging.SprivalLoggingProperties
```

## 注解

- `@Data` - Lombok注解,自动生成getter/setter/equals/hashCode/toString
- `@Component` - Spring组件,自动注册为Bean
- `@ConfigurationProperties(prefix = "sprival.logging")` - 配置属性绑定

## 字段

### application

```java
private ApplicationLogConfig application
```

应用日志配置对象,包含应用日志的所有配置项。

**默认值**: `new ApplicationLogConfig()`

### jettyAccess

```java
private JettyAccessConfig jettyAccess
```

Jetty 访问日志配置对象,包含访问日志的所有配置项。

**默认值**: `new JettyAccessConfig()`

## 内部类

### ApplicationLogConfig

```java
@Data
@EqualsAndHashCode(callSuper = true)
public static class ApplicationLogConfig extends BaseKafkaLogConfig
```

应用日志配置类,继承 BaseKafkaLogConfig 并提供默认值。

**默认配置**:
- `topic`: "application-logs"
- `clientId`: "application-log-producer"

### JettyAccessConfig

```java
@Data
@EqualsAndHashCode(callSuper = true)
public static class JettyAccessConfig extends BaseKafkaLogConfig
```

Jetty 访问日志配置类,继承 BaseKafkaLogConfig 并提供默认值。

**默认配置**:
- `topic`: "jetty-access-logs"
- `clientId`: "jetty-access-log-producer"

## 方法

由于使用了 `@Data` 注解,自动生成以下方法:

### getApplication()

```java
public ApplicationLogConfig getApplication()
```

获取应用日志配置对象。

**返回值**: ApplicationLogConfig 应用日志配置

### setApplication(ApplicationLogConfig)

```java
public void setApplication(ApplicationLogConfig application)
```

设置应用日志配置对象。

**参数**:
- `application` - 应用日志配置对象

### getJettyAccess()

```java
public JettyAccessConfig getJettyAccess()
```

获取 Jetty 访问日志配置对象。

**返回值**: JettyAccessConfig Jetty 访问日志配置

### setJettyAccess(JettyAccessConfig)

```java
public void setJettyAccess(JettyAccessConfig jettyAccess)
```

设置 Jetty 访问日志配置对象。

**参数**:
- `jettyAccess` - Jetty 访问日志配置对象

## 配置示例

### application.properties

```properties
# 应用日志配置
sprival.logging.application.output-target=kafka
sprival.logging.application.bootstrap-servers=localhost:9092
sprival.logging.application.topic=my-app-logs
sprival.logging.application.client-id=my-app-producer
sprival.logging.application.acks=1
sprival.logging.application.retries=3
sprival.logging.application.batch-size=16384
sprival.logging.application.linger-ms=1
sprival.logging.application.buffer-memory=33554432
sprival.logging.application.compression-type=gzip
sprival.logging.application.request-timeout-ms=30000
sprival.logging.application.delivery-timeout-ms=120000
sprival.logging.application.max-block-ms=60000

# Jetty访问日志配置
sprival.logging.jetty-access.output-target=both
sprival.logging.jetty-access.bootstrap-servers=localhost:9092
sprival.logging.jetty-access.topic=access-logs
```

### application.yml

```yaml
sprival:
  logging:
    application:
      output-target: kafka
      bootstrap-servers: localhost:9092
      topic: my-app-logs
      client-id: my-app-producer
      compression-type: gzip
    jetty-access:
      output-target: both
      bootstrap-servers: localhost:9092
      topic: access-logs
```

## 使用示例

### 注入配置对象

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoggingConfigChecker {
    
    @Autowired
    private SprivalLoggingProperties loggingProperties;
    
    public void printConfig() {
        // 获取应用日志配置
        SprivalLoggingProperties.ApplicationLogConfig appConfig = 
            loggingProperties.getApplication();
        
        System.out.println("Application Log Topic: " + appConfig.getTopic());
        System.out.println("Bootstrap Servers: " + appConfig.getBootstrapServers());
        System.out.println("Output Target: " + appConfig.getOutputTarget());
        
        // 获取访问日志配置
        SprivalLoggingProperties.JettyAccessConfig accessConfig = 
            loggingProperties.getJettyAccess();
        
        System.out.println("Access Log Topic: " + accessConfig.getTopic());
        System.out.println("Compression: " + accessConfig.getCompressionType());
    }
}
```

### 编程式修改配置

```java
@Configuration
public class CustomLoggingConfig {
    
    @Bean
    public SprivalLoggingProperties customLoggingProperties() {
        SprivalLoggingProperties properties = new SprivalLoggingProperties();
        
        // 自定义应用日志配置
        SprivalLoggingProperties.ApplicationLogConfig appConfig = 
            properties.getApplication();
        appConfig.setOutputTarget(LogOutputTarget.BOTH);
        appConfig.setCompressionType("gzip");
        appConfig.setRetries(5);
        
        return properties;
    }
}
```

## 相关类

- [BaseKafkaLogConfig](BaseKafkaLogConfig.md) - Kafka日志配置基类
- [LogOutputTarget](LogOutputTarget.md) - 日志输出目标枚举

## 参考文档

- [日志配置参考文档](../../../../../reference/logging/README.md)

## 作者

Sprival Team

## 版本

1.0.0

