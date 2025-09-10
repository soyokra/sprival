# 健康检查强依赖/弱依赖功能实现

## 功能概述

根据需求文档 `health.md` 的要求，实现了中间件健康检查的强依赖和弱依赖模式配置功能。

## 实现架构

### 核心组件

1. **SprivalHealthDependencyMode** - 依赖模式枚举
   - `STRONG`: 强依赖模式，中间件不可用时应用健康状态为DOWN
   - `WEAK`: 弱依赖模式，中间件不可用时应用健康状态仍为UP，但记录告警日志

2. **SprivalHealthProperties** - 健康检查配置属性
   - 支持配置各个中间件的依赖模式
   - 支持配置告警日志级别和超时时间

3. **SprivalHealthManager** - 健康检查管理器
   - 统一管理中间件的健康检查逻辑
   - 根据配置的依赖模式处理健康检查结果

4. **SprivalHealthConfiguration** - 健康检查配置类
   - 初始化健康检查相关配置

## 配置说明

### application.properties 配置

```properties
# 健康检查基础配置
sprival.health.enabled = true
sprival.health.alert-log-enabled = true
sprival.health.alert-log-level = WARN
sprival.health.timeout = 5000
sprival.health.interval = 30000
sprival.health.default-dependency-mode = strong

# 中间件依赖模式配置
sprival.health.dependency-modes.mysql = strong
sprival.health.dependency-modes.redis = strong
sprival.health.dependency-modes.kafka = weak
sprival.health.dependency-modes.mongodb = weak
sprival.health.dependency-modes.elasticsearch = weak
sprival.health.dependency-modes.clickhouse = weak
sprival.health.dependency-modes.rabbitmq = weak
```

### 配置参数说明

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `sprival.health.enabled` | boolean | true | 是否启用健康检查功能 |
| `sprival.health.alert-log-enabled` | boolean | true | 是否启用告警日志 |
| `sprival.health.alert-log-level` | string | WARN | 告警日志级别 (ERROR/WARN/INFO) |
| `sprival.health.timeout` | long | 5000 | 健康检查超时时间（毫秒） |
| `sprival.health.interval` | long | 30000 | 健康检查间隔时间（毫秒） |
| `sprival.health.default-dependency-mode` | string | strong | 默认依赖模式 |
| `sprival.health.dependency-modes.{component}` | string | - | 各组件依赖模式配置 |

## 使用方式

### 1. 查看健康检查配置

```bash
curl http://localhost:8338/api/health-test/config
```

### 2. 查看组件健康状态

```bash
curl http://localhost:8338/api/health-test/components
```

### 3. 测试依赖模式配置

```bash
curl http://localhost:8338/api/health-test/test-dependency-mode
```

### 4. 查看应用整体健康状态

```bash
curl http://localhost:8338/api/actuator/health
```

## 功能特性

### 强依赖模式 (STRONG)

- **行为**: 中间件不可用时，应用健康检查状态为DOWN
- **适用场景**: 核心业务依赖的中间件（如MySQL、Redis）
- **配置示例**: `sprival.health.dependency-modes.mysql = strong`

### 弱依赖模式 (WEAK)

- **行为**: 中间件不可用时，应用健康检查状态仍为UP，但记录告警日志
- **适用场景**: 非核心业务依赖的中间件（如Kafka、MongoDB、Elasticsearch）
- **配置示例**: `sprival.health.dependency-modes.kafka = weak`

### 告警日志

- **日志级别**: 可配置 (ERROR/WARN/INFO)
- **日志内容**: 包含组件名称、错误信息、时间戳等详细信息
- **示例日志**:
  ```
  [WARN] 【健康检查告警】弱依赖组件 kafka 不可用，但应用仍可正常运行。错误信息: Connection refused
  ```

## 已集成的组件

以下组件的健康检查指示器已更新支持强依赖/弱依赖模式：

1. **Redis** - `SprivalRedisHealthIndicator`
2. **MongoDB** - `SprivalMongoHealthIndicator`  
3. **Kafka** - `SprivalKafkaHealthIndicator`

其他组件（MySQL、Elasticsearch、ClickHouse、RabbitMQ）的健康检查指示器可以按照相同模式进行更新。

## 扩展说明

### 添加新的健康检查指示器

1. 在健康检查指示器中注入 `SprivalHealthManager`
2. 在 `health()` 方法中调用 `healthManager.checkComponentHealth(componentName, healthCheckFunction)`
3. 将实际健康检查逻辑提取到单独的方法中

### 示例代码

```java
@Component
public class CustomHealthIndicator implements HealthIndicator {
    
    @Autowired(required = false)
    private SprivalHealthManager healthManager;
    
    @Override
    public Health health() {
        if (healthManager != null) {
            return healthManager.checkComponentHealth("custom", this::performHealthCheck);
        }
        return performHealthCheck();
    }
    
    private Health performHealthCheck() {
        // 实际的健康检查逻辑
        return Health.up().build();
    }
}
```

## 监控和运维

### 健康检查端点

- **整体健康状态**: `/api/actuator/health`
- **配置信息**: `/api/health-test/config`
- **组件状态**: `/api/health-test/components`
- **依赖模式测试**: `/api/health-test/test-dependency-mode`

### 日志监控

建议配置日志监控系统，监控包含 "【健康检查告警】" 关键字的日志，及时发现中间件异常。

## 注意事项

1. **向后兼容**: 如果未启用健康管理器，组件将使用默认的健康检查逻辑
2. **性能影响**: 健康检查会定期执行，建议合理配置检查间隔
3. **日志级别**: 告警日志级别建议设置为WARN，避免日志过多
4. **配置更新**: 修改配置后需要重启应用才能生效
