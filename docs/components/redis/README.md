# Redis 组件

## 简介

Redis组件为Sprival项目提供完整的缓存和分布式数据存储解决方案，基于Spring Cache、Lettuce和Redisson实现声明式缓存、分布式锁、会话管理等功能。

## 功能特性

- **声明式缓存**: 基于Spring Cache的注解式缓存管理
- **多客户端支持**: 支持Lettuce、Redisson等多种客户端
- **分布式锁**: 基于Redisson的分布式锁实现
- **连接池管理**: 高性能连接池配置和监控
- **序列化支持**: 多种序列化方案支持（JSON、JDK、Kryo）
- **监控集成**: 与Prometheus + Grafana无缝集成
- **集群支持**: 支持Redis单机、哨兵、集群模式

## 环境要求

- **Java版本**: 1.8+
- **Spring Boot版本**: 2.7.18
- **Redis版本**: 5.0+ 或 6.0+
- **Maven版本**: 3.6+

## 快速开始

### 安装步骤
1. 项目已配置所需依赖，无需额外添加
2. 启动Redis服务器
3. 配置application.properties中的Redis连接信息
4. 启动Spring Boot应用

### 基础配置
```properties
# Redis连接配置
spring.redis.host = localhost
spring.redis.port = 6379
spring.redis.password = workdock
spring.redis.database = 0
spring.redis.timeout = 2000ms
spring.redis.connect-timeout = 2000ms

# 连接池配置
spring.redis.lettuce.pool.max-active = 20
spring.redis.lettuce.pool.max-idle = 10
spring.redis.lettuce.pool.min-idle = 5
spring.redis.lettuce.pool.max-wait = 2000ms

# 缓存配置
spring.cache.type = redis
spring.cache.redis.time-to-live = 600000
spring.cache.redis.cache-null-values = false
spring.cache.redis.key-prefix = sprival:
spring.cache.redis.use-key-prefix = true
```

### 基础使用
```java
// 缓存使用示例
@Service
@EnableCaching
public class UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    /**
     * 缓存用户信息
     */
    @Cacheable(value = "user", key = "#userId")
    public User findById(Long userId) {
        return userMapper.selectById(userId);
    }
    
    /**
     * 更新用户信息并清除缓存
     */
    @CacheEvict(value = "user", key = "#user.id")
    public void updateUser(User user) {
        userMapper.updateById(user);
    }
}
```

## 配置说明

### 配置参数
| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `spring.redis.host` | String | localhost | Redis服务器地址 |
| `spring.redis.port` | Integer | 6379 | Redis服务器端口 |
| `spring.redis.password` | String | - | Redis密码 |
| `spring.redis.database` | Integer | 0 | 数据库索引 |
| `spring.redis.timeout` | Duration | 2000ms | 连接超时时间 |
| `lettuce.pool.max-active` | Integer | 8 | 连接池最大连接数 |
| `lettuce.pool.max-idle` | Integer | 8 | 连接池最大空闲连接数 |
| `lettuce.pool.min-idle` | Integer | 0 | 连接池最小空闲连接数 |
| `cache.redis.time-to-live` | Duration | 600000ms | 缓存过期时间 |

### 高级配置
```properties
# Redisson配置
spring.redis.redisson.config.singleServerConfig.address = redis://localhost:6379
spring.redis.redisson.config.singleServerConfig.password = workdock
spring.redis.redisson.config.singleServerConfig.database = 0
spring.redis.redisson.config.singleServerConfig.connectionPoolSize = 20
spring.redis.redisson.config.singleServerConfig.connectionMinimumIdleSize = 5
spring.redis.redisson.config.singleServerConfig.idleConnectionTimeout = 10000
spring.redis.redisson.config.singleServerConfig.connectTimeout = 10000
spring.redis.redisson.config.singleServerConfig.timeout = 3000
spring.redis.redisson.config.singleServerConfig.retryAttempts = 3
spring.redis.redisson.config.singleServerConfig.retryInterval = 1500
```

## 使用示例

### 基本用法
```java
@Service
public class RedisService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    
    /**
     * 字符串操作
     */
    public void stringOperations() {
        // 设置值
        stringRedisTemplate.opsForValue().set("key1", "value1");
        stringRedisTemplate.opsForValue().set("key2", "value2", Duration.ofMinutes(10));
        
        // 获取值
        String value = stringRedisTemplate.opsForValue().get("key1");
        
        // 原子操作
        Long increment = stringRedisTemplate.opsForValue().increment("counter");
    }
    
    /**
     * 哈希操作
     */
    public void hashOperations() {
        String hashKey = "user:1001";
        
        // 设置哈希字段
        redisTemplate.opsForHash().put(hashKey, "name", "张三");
        redisTemplate.opsForHash().put(hashKey, "age", 25);
        
        // 获取哈希字段
        String name = (String) redisTemplate.opsForHash().get(hashKey, "name");
        Map<Object, Object> userMap = redisTemplate.opsForHash().entries(hashKey);
    }
}
```

### 高级用法
```java
// Redisson分布式锁
@Service
public class DistributedLockService {
    
    @Autowired
    private RedissonClient redissonClient;
    
    /**
     * 基础分布式锁
     */
    public void basicLock() {
        RLock lock = redissonClient.getLock("myLock");
        
        try {
            // 尝试获取锁，最多等待10秒，锁定时间30秒
            boolean isLocked = lock.tryLock(10, 30, TimeUnit.SECONDS);
            
            if (isLocked) {
                try {
                    // 执行业务逻辑
                    doBusinessLogic();
                } finally {
                    // 释放锁
                    lock.unlock();
                }
            } else {
                throw new RuntimeException("获取锁失败");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("锁等待被中断", e);
        }
    }
}

// 自定义缓存配置
@Configuration
@EnableCaching
public class RedisCacheConfiguration {
    
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // 设置序列化器
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        
        template.afterPropertiesSet();
        return template;
    }
}
```

## 监控

### 健康检查
```bash
# 查看Redis健康状态
curl http://localhost:8338/api/actuator/health

# 查看连接池指标
curl http://localhost:8338/api/actuator/metrics/lettuce.pool.active.connections

# 查看缓存指标
curl http://localhost:8338/api/actuator/metrics/cache.hits

# 查看所有Prometheus指标
curl http://localhost:8338/api/actuator/prometheus | grep redis
```

### 监控指标
| 指标名称 | 类型 | 说明 |
|----------|------|------|
| `lettuce.pool.active.connections` | Gauge | 活跃连接数 |
| `lettuce.pool.idle.connections` | Gauge | 空闲连接数 |
| `lettuce.pool.max.connections` | Gauge | 最大连接数 |
| `redis.commands.duration` | Timer | Redis命令执行时间 |
| `cache.gets` | Counter | 缓存获取次数 |
| `cache.hits` | Counter | 缓存命中次数 |
| `cache.misses` | Counter | 缓存未命中次数 |

## 常见问题

**Q: 如何选择Redis客户端？**
A: 项目推荐使用Lettuce + Redisson组合：
- **Lettuce**: 异步非阻塞，性能更好，适合高并发场景
- **Redisson**: 功能丰富，提供分布式锁、分布式集合等高级功能
- **Jedis**: 同步阻塞，连接池管理复杂，不推荐使用

**Q: 缓存穿透、缓存击穿、缓存雪崩如何解决？**
A: 
- **缓存穿透**: 使用布隆过滤器或缓存空值
- **缓存击穿**: 使用分布式锁或设置热点数据永不过期
- **缓存雪崩**: 设置随机过期时间或使用多级缓存

**Q: 如何优化Redis性能？**
A: 
- 合理设置连接池大小
- 使用Pipeline批量操作
- 选择合适的序列化方式
- 避免大key和热key

**Q: 分布式锁的注意事项？**
A: 
- 设置合理的锁超时时间
- 确保业务逻辑在锁超时前完成
- 使用try-finally确保锁释放
- 避免锁重入问题

**Q: 如何监控Redis性能？**
A: 
- 监控连接池使用率
- 监控命令执行时间
- 监控内存使用情况
- 监控缓存命中率

## 参考文档

- [Spring Data Redis官方文档](https://spring.io/projects/spring-data-redis)
- [Redisson官方文档](https://github.com/redisson/redisson)
- [Spring Cache官方文档](https://spring.io/guides/gs/caching/)
- [Redis官方文档](https://redis.io/documentation)