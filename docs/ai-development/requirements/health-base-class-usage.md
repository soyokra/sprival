# Sprival基础健康检查类使用指南

## 概述

`SprivalBaseHealthIndicator` 是一个抽象基础类，为所有中间件健康检查提供统一的框架和通用功能。通过继承这个基础类，可以简化健康检查指示器的开发，并确保所有健康检查都遵循相同的模式和最佳实践。

## 核心特性

### 1. 统一的生命周期管理
- **预处理**: `beforeHealthCheck()` - 健康检查前的准备工作
- **核心检查**: `doHealthCheck()` - 具体的健康检查逻辑
- **后处理**: `afterHealthCheck()` - 健康检查后的清理工作

### 2. 强依赖/弱依赖模式支持
- 自动集成 `SprivalHealthManager`
- 支持配置化的依赖模式管理
- 统一的告警日志处理

### 3. 通用功能
- 统一的异常处理
- 标准化的健康状态创建
- 可配置的超时时间
- 详细的日志记录

## 使用方法

### 1. 创建健康检查指示器

```java
@Component
@ConditionalOnProperty(name = "your.component.enabled", havingValue = "true")
public class YourComponentHealthIndicator extends SprivalBaseHealthIndicator {
    
    // 注入必要的依赖
    private final YourService yourService;
    
    public YourComponentHealthIndicator(YourService yourService) {
        this.yourService = yourService;
    }
    
    @Override
    protected String getComponentName() {
        return "your-component"; // 组件名称，用于配置和日志
    }
    
    @Override
    protected Health doHealthCheck() {
        try {
            // 执行具体的健康检查逻辑
            boolean isHealthy = yourService.isHealthy();
            
            Map<String, Object> details = getHealthCheckDetails();
            details.put("status", isHealthy ? "正常" : "异常");
            details.put("responseTime", yourService.getResponseTime());
            
            return isHealthy ? createUpHealth(details) : createDownHealth(details);
            
        } catch (Exception e) {
            return handleHealthCheckException(e);
        }
    }
}
```

### 2. 重写可选方法

```java
@Override
protected Map<String, Object> getHealthCheckDetails() {
    Map<String, Object> details = super.getHealthCheckDetails();
    // 添加组件特定的详细信息
    details.put("version", "1.0.0");
    details.put("endpoint", "http://localhost:8080");
    return details;
}

@Override
protected void beforeHealthCheck() {
    logHealthCheck("DEBUG", "开始执行{}健康检查", getComponentName());
}

@Override
protected void afterHealthCheck(Health health) {
    if (health.getStatus().equals(org.springframework.boot.actuate.health.Status.UP)) {
        logHealthCheck("DEBUG", "{}健康检查完成，状态正常", getComponentName());
    } else {
        logHealthCheck("WARN", "{}健康检查完成，状态异常", getComponentName());
    }
}

@Override
protected long getHealthCheckTimeout() {
    return 5000; // 自定义超时时间
}

@Override
protected boolean isHealthCheckEnabled() {
    return super.isHealthCheckEnabled() && yourService.isAvailable();
}
```

## 抽象方法说明

### 必须实现的方法

#### `getComponentName()`
- **用途**: 返回组件名称
- **用途**: 用于配置依赖模式、日志标识等
- **示例**: `return "redis";`

#### `doHealthCheck()`
- **用途**: 执行具体的健康检查逻辑
- **返回**: `Health` 对象
- **注意**: 不要处理异常，基础类会统一处理

### 可选重写的方法

#### `getHealthCheckDetails()`
- **用途**: 获取健康检查的详细信息
- **默认**: 包含组件名称、时间戳等基础信息
- **建议**: 添加组件特定的信息

#### `beforeHealthCheck()`
- **用途**: 健康检查前的预处理
- **默认**: 空实现
- **用途**: 记录开始日志、准备资源等

#### `afterHealthCheck(Health health)`
- **用途**: 健康检查后的后处理
- **参数**: 健康检查结果
- **用途**: 记录结果日志、清理资源等

#### `getHealthCheckTimeout()`
- **用途**: 获取健康检查超时时间
- **默认**: 使用配置中的超时时间
- **单位**: 毫秒

#### `isHealthCheckEnabled()`
- **用途**: 判断是否启用健康检查
- **默认**: 使用全局配置
- **用途**: 添加组件特定的启用条件

#### `handleHealthCheckException(Exception e)`
- **用途**: 处理健康检查异常
- **默认**: 记录错误日志并返回DOWN状态
- **用途**: 自定义异常处理逻辑

## 工具方法

### 健康状态创建
```java
// 创建UP状态
Health upHealth = createUpHealth(details);

// 创建DOWN状态  
Health downHealth = createDownHealth(details);

// 创建自定义状态
Health customHealth = createHealth(Status.UP, details);
```

### 日志记录
```java
// 记录不同级别的日志
logHealthCheck("DEBUG", "调试信息: {}", value);
logHealthCheck("INFO", "信息日志");
logHealthCheck("WARN", "警告信息: {}", warning);
logHealthCheck("ERROR", "错误信息: {}", error);
```

### 依赖模式检查
```java
// 检查依赖模式
if (isStrongDependency()) {
    // 强依赖逻辑
}

if (isWeakDependency()) {
    // 弱依赖逻辑
}

// 获取依赖模式
SprivalHealthDependencyMode mode = getDependencyMode();
```

## 配置示例

### application.properties
```properties
# 启用健康检查
sprival.health.enabled = true

# 配置组件依赖模式
sprival.health.dependency-modes.your-component = strong

# 组件特定配置
your.component.enabled = true
```

## 最佳实践

### 1. 异常处理
- 在 `doHealthCheck()` 中不要捕获异常
- 让基础类统一处理异常
- 如需自定义异常处理，重写 `handleHealthCheckException()`

### 2. 日志记录
- 使用 `logHealthCheck()` 方法记录日志
- 日志会自动包含组件名称前缀
- 合理使用日志级别

### 3. 性能考虑
- 健康检查应该快速执行
- 避免在健康检查中执行耗时操作
- 合理设置超时时间

### 4. 详细信息
- 在 `getHealthCheckDetails()` 中添加有用的诊断信息
- 包含版本、配置、状态等关键信息
- 避免包含敏感信息

## 示例：完整的健康检查指示器

```java
@Component
@ConditionalOnProperty(name = "sprival.database.enabled", havingValue = "true")
public class DatabaseHealthIndicator extends SprivalBaseHealthIndicator {
    
    private final DataSource dataSource;
    private final DatabaseProperties properties;
    
    public DatabaseHealthIndicator(DataSource dataSource, DatabaseProperties properties) {
        this.dataSource = dataSource;
        this.properties = properties;
    }
    
    @Override
    protected String getComponentName() {
        return "database";
    }
    
    @Override
    protected Health doHealthCheck() {
        try (Connection connection = dataSource.getConnection()) {
            // 执行数据库健康检查
            boolean isValid = connection.isValid(5);
            long responseTime = measureResponseTime();
            
            Map<String, Object> details = getHealthCheckDetails();
            details.put("status", isValid ? "连接正常" : "连接异常");
            details.put("responseTime", responseTime);
            details.put("maxConnections", properties.getMaxConnections());
            details.put("activeConnections", getActiveConnections());
            
            return isValid ? createUpHealth(details) : createDownHealth(details);
            
        } catch (Exception e) {
            return handleHealthCheckException(e);
        }
    }
    
    @Override
    protected Map<String, Object> getHealthCheckDetails() {
        Map<String, Object> details = super.getHealthCheckDetails();
        details.put("database", properties.getDatabaseName());
        details.put("host", properties.getHost());
        details.put("port", properties.getPort());
        details.put("driver", properties.getDriverClassName());
        return details;
    }
    
    @Override
    protected void beforeHealthCheck() {
        logHealthCheck("DEBUG", "开始执行数据库健康检查，数据库: {}", properties.getDatabaseName());
    }
    
    @Override
    protected void afterHealthCheck(Health health) {
        if (health.getStatus().equals(org.springframework.boot.actuate.health.Status.UP)) {
            logHealthCheck("DEBUG", "数据库健康检查完成，状态正常");
        } else {
            logHealthCheck("WARN", "数据库健康检查完成，状态异常");
        }
    }
    
    @Override
    protected long getHealthCheckTimeout() {
        return 10000; // 数据库检查超时10秒
    }
    
    private long measureResponseTime() {
        long start = System.currentTimeMillis();
        // 执行简单的查询
        long end = System.currentTimeMillis();
        return end - start;
    }
    
    private int getActiveConnections() {
        // 获取活跃连接数
        return 0; // 示例实现
    }
}
```

## 迁移指南

### 从现有HealthIndicator迁移

1. **继承基础类**
   ```java
   // 从
   public class OldHealthIndicator implements HealthIndicator
   
   // 改为
   public class NewHealthIndicator extends SprivalBaseHealthIndicator
   ```

2. **实现抽象方法**
   ```java
   @Override
   protected String getComponentName() {
       return "component-name";
   }
   
   @Override
   protected Health doHealthCheck() {
       // 将原来的health()方法逻辑移到这里
   }
   ```

3. **移除重复代码**
   - 删除健康管理器的注入和调用
   - 删除异常处理代码
   - 删除日志记录代码

4. **利用新功能**
   - 使用工具方法简化代码
   - 添加预处理和后处理逻辑
   - 自定义详细信息

通过使用 `SprivalBaseHealthIndicator`，可以大大简化健康检查指示器的开发，并确保所有健康检查都遵循统一的标准和最佳实践。
