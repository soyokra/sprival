# Spring Boot默认健康检查禁用机制

## Spring Boot默认健康检查机制

### 1. Spring Boot自动配置的健康检查

Spring Boot为大多数中间件提供了自动配置的健康检查，这些健康检查会在以下条件下自动启用：

#### 自动启用的健康检查组件
- **DataSourceHealthIndicator** - 数据库健康检查
- **RedisHealthIndicator** - Redis健康检查  
- **MongoHealthIndicator** - MongoDB健康检查
- **RabbitHealthIndicator** - RabbitMQ健康检查
- **ElasticsearchHealthIndicator** - Elasticsearch健康检查
- **DiskSpaceHealthIndicator** - 磁盘空间检查
- **PingHealthIndicator** - 基础ping检查

#### 自动配置条件
```java
@ConditionalOnClass(RedisTemplate.class)
@ConditionalOnProperty(name = "spring.redis.host")
public class RedisHealthIndicator implements HealthIndicator {
    // 自动配置
}
```

### 2. 默认健康检查的启用条件

Spring Boot的健康检查遵循以下规则：

1. **类路径存在** - 相关中间件的类在classpath中
2. **配置存在** - 相关配置属性已设置
3. **Bean存在** - 相关的Bean已创建
4. **默认启用** - 除非明确禁用，否则默认启用

## 实现"默认不运行"的机制

### 1. 通过配置禁用Spring Boot默认健康检查

#### 方法一：禁用特定组件的健康检查
```properties
# 禁用Redis健康检查
management.health.redis.enabled = false

# 禁用MongoDB健康检查  
management.health.mongo.enabled = false

# 禁用数据库健康检查
management.health.db.enabled = false

# 禁用RabbitMQ健康检查
management.health.rabbit.enabled = false

# 禁用Elasticsearch健康检查
management.health.elasticsearch.enabled = false
```

#### 方法二：禁用所有健康检查
```properties
# 禁用所有健康检查
management.health.defaults.enabled = false

# 或者禁用健康检查端点
management.endpoint.health.enabled = false
```

#### 方法三：选择性启用健康检查
```properties
# 只启用指定的健康检查
management.health.defaults.enabled = false
management.health.redis.enabled = true
management.health.db.enabled = true
```

### 2. 通过条件注解控制

#### 使用@ConditionalOnProperty
```java
@Component
@ConditionalOnProperty(name = "sprival.health.redis.enabled", havingValue = "true", matchIfMissing = false)
public class SprivalRedisHealthIndicator extends SprivalBaseHealthIndicator {
    // 只有明确配置为true时才启用
}
```

#### 使用@ConditionalOnClass
```java
@Component
@ConditionalOnClass(RedisTemplate.class)
@ConditionalOnProperty(name = "sprival.health.redis.enabled", havingValue = "true", matchIfMissing = false)
public class SprivalRedisHealthIndicator extends SprivalBaseHealthIndicator {
    // 需要RedisTemplate类存在且明确启用
}
```

### 3. 通过配置类控制

#### 创建健康检查配置类
```java
@Configuration
@EnableConfigurationProperties(SprivalHealthProperties.class)
public class SprivalHealthAutoConfiguration {
    
    @Bean
    @ConditionalOnProperty(name = "sprival.health.redis.enabled", havingValue = "true", matchIfMissing = false)
    public SprivalRedisHealthIndicator redisHealthIndicator(RedisTemplate<String, Object> redisTemplate) {
        return new SprivalRedisHealthIndicator(redisTemplate);
    }
    
    @Bean
    @ConditionalOnProperty(name = "sprival.health.mongodb.enabled", havingValue = "true", matchIfMissing = false)
    public SprivalMongoHealthIndicator mongoHealthIndicator(MongoTemplate mongoTemplate) {
        return new SprivalMongoHealthIndicator(mongoTemplate);
    }
}
```

## 我们的实现方案

### 1. 在SprivalBaseHealthIndicator中实现默认禁用

```java
@Slf4j
public abstract class SprivalBaseHealthIndicator implements HealthIndicator {
    
    @Override
    protected boolean isHealthCheckEnabled() {
        // 默认不启用，需要明确配置
        return healthProperties.isEnabled() && isComponentEnabled();
    }
    
    /**
     * 检查组件是否启用
     * 子类可以重写此方法
     */
    protected boolean isComponentEnabled() {
        String componentName = getComponentName();
        return healthProperties.getComponentEnabled(componentName);
    }
}
```

### 2. 在SprivalHealthProperties中添加组件启用控制

```java
@Data
@Component
@ConfigurationProperties(prefix = "sprival.health")
public class SprivalHealthProperties {
    
    /**
     * 是否启用健康检查功能
     */
    private boolean enabled = true;
    
    /**
     * 各组件是否启用健康检查
     * key: 组件名称, value: 是否启用
     */
    private Map<String, Boolean> componentEnabled = new HashMap<>();
    
    /**
     * 获取组件是否启用
     */
    public boolean getComponentEnabled(String component) {
        return componentEnabled.getOrDefault(component, false); // 默认不启用
    }
    
    /**
     * 设置组件是否启用
     */
    public void setComponentEnabled(String component, boolean enabled) {
        componentEnabled.put(component, enabled);
    }
}
```

### 3. 配置示例

```properties
# 全局健康检查开关
sprival.health.enabled = true

# 各组件健康检查开关（默认不启用）
sprival.health.component-enabled.redis = true
sprival.health.component-enabled.mongodb = true
sprival.health.component-enabled.kafka = false
sprival.health.component-enabled.elasticsearch = false

# 禁用Spring Boot默认健康检查
management.health.redis.enabled = false
management.health.mongo.enabled = false
management.health.db.enabled = false
management.health.rabbit.enabled = false
management.health.elasticsearch.enabled = false
```

## 完整的实现方案

### 1. 更新SprivalHealthProperties

```java
@Data
@Component
@ConfigurationProperties(prefix = "sprival.health")
public class SprivalHealthProperties {
    
    private boolean enabled = true;
    private boolean alertLogEnabled = true;
    private String alertLogLevel = "WARN";
    private long timeout = 5000;
    private long interval = 30000;
    private String defaultDependencyMode = "strong";
    
    /**
     * 中间件依赖模式配置
     */
    private Map<String, String> dependencyModes = new HashMap<>();
    
    /**
     * 各组件是否启用健康检查（默认不启用）
     */
    private Map<String, Boolean> componentEnabled = new HashMap<>();
    
    /**
     * 获取组件是否启用
     */
    public boolean getComponentEnabled(String component) {
        return componentEnabled.getOrDefault(component, false);
    }
    
    /**
     * 设置组件是否启用
     */
    public void setComponentEnabled(String component, boolean enabled) {
        componentEnabled.put(component, enabled);
    }
    
    /**
     * 初始化默认配置
     */
    public void initDefaultConfig() {
        if (dependencyModes.isEmpty()) {
            // 设置默认的强依赖中间件
            dependencyModes.put("mysql", "strong");
            dependencyModes.put("redis", "strong");
            
            // 设置默认的弱依赖中间件
            dependencyModes.put("kafka", "weak");
            dependencyModes.put("mongodb", "weak");
            dependencyModes.put("elasticsearch", "weak");
            dependencyModes.put("clickhouse", "weak");
            dependencyModes.put("rabbitmq", "weak");
        }
        
        // 默认不启用任何组件的健康检查
        if (componentEnabled.isEmpty()) {
            componentEnabled.put("redis", false);
            componentEnabled.put("mongodb", false);
            componentEnabled.put("kafka", false);
            componentEnabled.put("elasticsearch", false);
            componentEnabled.put("clickhouse", false);
            componentEnabled.put("rabbitmq", false);
        }
    }
}
```

### 2. 更新SprivalBaseHealthIndicator

```java
@Override
protected boolean isHealthCheckEnabled() {
    // 全局开关 && 组件开关
    return healthProperties.isEnabled() && 
           healthProperties.getComponentEnabled(getComponentName());
}
```

### 3. 更新application.properties

```properties
# 禁用Spring Boot默认健康检查
management.health.redis.enabled = false
management.health.mongo.enabled = false
management.health.db.enabled = false
management.health.rabbit.enabled = false
management.health.elasticsearch.enabled = false

# Sprival健康检查配置
sprival.health.enabled = true
sprival.health.alert-log-enabled = true
sprival.health.alert-log-level = WARN

# 明确启用需要的组件健康检查
sprival.health.component-enabled.redis = true
sprival.health.component-enabled.mongodb = true
sprival.health.component-enabled.kafka = false
```

## 优势

### 1. 明确控制
- 默认不启用任何健康检查
- 需要明确配置才能启用
- 避免意外的健康检查影响

### 2. 灵活配置
- 可以单独控制每个组件
- 支持运行时动态配置
- 与Spring Boot配置兼容

### 3. 性能优化
- 避免不必要的健康检查
- 减少资源消耗
- 提高应用启动速度

### 4. 运维友好
- 清晰的配置说明
- 易于排查问题
- 支持渐进式启用

## 总结

通过以上方案，我们实现了：

1. **默认不运行** - 所有健康检查默认禁用
2. **明确启用** - 需要明确配置才能启用
3. **灵活控制** - 可以单独控制每个组件
4. **Spring Boot兼容** - 与Spring Boot默认机制兼容
5. **性能优化** - 避免不必要的健康检查

这样既保持了Spring Boot的灵活性，又实现了我们需要的"默认不运行"的控制机制。
