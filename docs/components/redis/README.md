# Spring Redis 模块

## 概述

Spring Redis 模块提供了完整的 Redis 缓存和分布式数据存储解决方案，包括声明式缓存、分布式锁、会话管理等功能。该模块基于现代化的 Redis 客户端技术栈，为 Sprival 项目提供高性能、高可用的缓存服务。

## 核心特性

- ✅ **声明式缓存**: 基于 Spring Cache 的注解式缓存管理
- ✅ **多客户端支持**: 支持 Jedis、Lettuce、Redisson 多种客户端
- ✅ **分布式锁**: 基于 Redisson 的分布式锁实现
- ✅ **连接池管理**: 高性能连接池配置和监控
- ✅ **序列化支持**: 多种序列化方案支持（JSON、JDK、Kryo）
- ✅ **监控集成**: 与 Prometheus + Grafana 无缝集成
- ✅ **集群支持**: 支持 Redis 单机、哨兵、集群模式
- ✅ **会话管理**: 基于 Redis 的分布式会话存储

## 组件清单

### 核心组件
- [spring-boot-starter-cache](https://spring.io/projects/spring-boot) - Spring 缓存抽象
- [spring-boot-starter-data-redis](https://spring.io/projects/spring-data-redis) - Spring Data Redis 集成
- [redisson-spring-boot-starter 3.19.3](https://github.com/redisson/redisson) - 分布式 Redis 客户端
- [lettuce-core](https://github.com/lettuce-io/lettuce-core) - 异步 Redis 客户端（默认）

### 功能组件
- **缓存管理**: Spring Cache + Redis 实现
- **分布式锁**: Redisson 分布式锁
- **连接池**: Lettuce 连接池管理
- **序列化**: Jackson、JDK、Kryo 序列化支持
- **监控指标**: Micrometer 指标收集

## 快速开始

### 1. 添加依赖

项目已在 `pom.xml` 中配置了所需依赖：

```xml
<!-- Spring Cache -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>

<!-- Spring Data Redis -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>

<!-- Redisson -->
<dependency>
    <groupId>org.redisson</groupId>
    <artifactId>redisson-spring-boot-starter</artifactId>
    <version>3.19.3</version>
</dependency>
```

### 2. Redis 服务准备

```bash
# 启动 Redis 服务（Docker 方式）
docker run -d --name redis -p 6379:6379 redis:7-alpine

# 或使用项目提供的 Docker Compose
cd dockers && docker-compose up redis
```

### 3. 基础配置

```properties
# Redis 连接配置
spring.redis.host = localhost
spring.redis.port = 6379
spring.redis.password = workdock
spring.redis.database = 0
spring.redis.timeout = 2000ms

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

## 配置详解

### Redis 连接配置

```properties
# 基础连接配置
spring.redis.host = localhost                    # Redis 服务器地址
spring.redis.port = 6379                         # Redis 端口
spring.redis.password = workdock                 # Redis 密码
spring.redis.database = 0                        # 数据库索引
spring.redis.timeout = 2000ms                    # 连接超时时间
spring.redis.connect-timeout = 2000ms            # 连接建立超时时间

# SSL 配置（可选）
spring.redis.ssl = false                         # 是否启用 SSL
spring.redis.ssl.key-store = classpath:keystore.p12
spring.redis.ssl.key-store-password = password
spring.redis.ssl.key-store-type = PKCS12
```

### Lettuce 连接池配置

```properties
# Lettuce 连接池配置
spring.redis.lettuce.pool.max-active = 20        # 连接池最大连接数
spring.redis.lettuce.pool.max-idle = 10          # 连接池最大空闲连接数
spring.redis.lettuce.pool.min-idle = 5           # 连接池最小空闲连接数
spring.redis.lettuce.pool.max-wait = 2000ms      # 连接池最大阻塞等待时间
spring.redis.lettuce.pool.time-between-eviction-runs = 30s  # 空闲连接回收器运行间隔

# Lettuce 客户端配置
spring.redis.lettuce.shutdown-timeout = 100ms    # 关闭超时时间
spring.redis.lettuce.cluster.refresh.adaptive = true  # 自适应集群拓扑刷新
spring.redis.lettuce.cluster.refresh.period = 30s     # 集群拓扑刷新周期
```

### Spring Cache 配置

```properties
# 缓存类型和基础配置
spring.cache.type = redis                        # 缓存类型
spring.cache.redis.time-to-live = 600000        # 缓存过期时间（毫秒）
spring.cache.redis.cache-null-values = false    # 是否缓存 null 值
spring.cache.redis.key-prefix = sprival:        # 缓存键前缀
spring.cache.redis.use-key-prefix = true        # 是否使用键前缀
spring.cache.redis.enable-statistics = true     # 是否启用缓存统计

# 缓存名称配置
spring.cache.cache-names = user,product,order   # 预定义的缓存名称
```

### Redisson 配置

```properties
# Redisson 配置
spring.redis.redisson.config = classpath:redisson.yml  # Redisson 配置文件路径

# 或使用内联配置
spring.redis.redisson.file = classpath:redisson-single.yml
```

#### Redisson 配置文件示例 (redisson.yml)

```yaml
# 单机模式配置
singleServerConfig:
  address: "redis://localhost:6379"
  password: "workdock"
  database: 0
  connectionPoolSize: 20
  connectionMinimumIdleSize: 5
  idleConnectionTimeout: 10000
  connectTimeout: 10000
  timeout: 3000
  retryAttempts: 3
  retryInterval: 1500

# 集群模式配置
clusterServersConfig:
  nodeAddresses:
    - "redis://127.0.0.1:7000"
    - "redis://127.0.0.1:7001"
    - "redis://127.0.0.1:7002"
  password: "workdock"
  masterConnectionPoolSize: 20
  slaveConnectionPoolSize: 20
  masterConnectionMinimumIdleSize: 5
  slaveConnectionMinimumIdleSize: 5
  idleConnectionTimeout: 10000
  connectTimeout: 10000
  timeout: 3000
  retryAttempts: 3
  retryInterval: 1500

# 通用配置
codec: !<org.redisson.codec.JsonJacksonCodec> {}
threads: 16
nettyThreads: 32
```

## 使用示例

### 1. Spring Cache 注解使用

```java
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
        logger.info("从数据库查询用户: {}", userId);
        return userMapper.selectById(userId);
    }
    
    /**
     * 更新用户信息并清除缓存
     */
    @CacheEvict(value = "user", key = "#user.id")
    public void updateUser(User user) {
        userMapper.updateById(user);
    }
    
    /**
     * 更新用户信息并更新缓存
     */
    @CachePut(value = "user", key = "#user.id")
    public User saveUser(User user) {
        userMapper.insert(user);
        return user;
    }
    
    /**
     * 清除所有用户缓存
     */
    @CacheEvict(value = "user", allEntries = true)
    public void clearUserCache() {
        logger.info("清除所有用户缓存");
    }
    
    /**
     * 条件缓存
     */
    @Cacheable(value = "user", key = "#userId", condition = "#userId > 0")
    public User findByIdWithCondition(Long userId) {
        return userMapper.selectById(userId);
    }
    
    /**
     * 多级缓存
     */
    @Caching(
        cacheable = {
            @Cacheable(value = "user", key = "#username"),
            @Cacheable(value = "userProfile", key = "#username")
        }
    )
    public User findByUsername(String username) {
        return userMapper.selectByUsername(username);
    }
}
```

### 2. RedisTemplate 操作示例

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
        Boolean setIfAbsent = stringRedisTemplate.opsForValue().setIfAbsent("lock", "locked");
    }
    
    /**
     * 哈希操作
     */
    public void hashOperations() {
        String hashKey = "user:1001";
        
        // 设置哈希字段
        redisTemplate.opsForHash().put(hashKey, "name", "张三");
        redisTemplate.opsForHash().put(hashKey, "age", 25);
        redisTemplate.opsForHash().put(hashKey, "email", "zhangsan@example.com");
        
        // 获取哈希字段
        String name = (String) redisTemplate.opsForHash().get(hashKey, "name");
        Map<Object, Object> userMap = redisTemplate.opsForHash().entries(hashKey);
        
        // 删除哈希字段
        redisTemplate.opsForHash().delete(hashKey, "age");
    }
    
    /**
     * 列表操作
     */
    public void listOperations() {
        String listKey = "task:queue";
        
        // 添加元素
        redisTemplate.opsForList().leftPush(listKey, "task1");
        redisTemplate.opsForList().rightPush(listKey, "task2");
        
        // 获取元素
        Object task = redisTemplate.opsForList().leftPop(listKey);
        List<Object> tasks = redisTemplate.opsForList().range(listKey, 0, -1);
        
        // 获取列表长度
        Long size = redisTemplate.opsForList().size(listKey);
    }
    
    /**
     * 集合操作
     */
    public void setOperations() {
        String setKey = "tags";
        
        // 添加元素
        redisTemplate.opsForSet().add(setKey, "java", "spring", "redis");
        
        // 获取所有元素
        Set<Object> tags = redisTemplate.opsForSet().members(setKey);
        
        // 检查元素是否存在
        Boolean isMember = redisTemplate.opsForSet().isMember(setKey, "java");
        
        // 集合运算
        String setKey2 = "tags2";
        redisTemplate.opsForSet().add(setKey2, "java", "python", "go");
        Set<Object> intersection = redisTemplate.opsForSet().intersect(setKey, setKey2);
    }
    
    /**
     * 有序集合操作
     */
    public void zSetOperations() {
        String zSetKey = "leaderboard";
        
        // 添加元素
        redisTemplate.opsForZSet().add(zSetKey, "player1", 100);
        redisTemplate.opsForZSet().add(zSetKey, "player2", 200);
        redisTemplate.opsForZSet().add(zSetKey, "player3", 150);
        
        // 获取排名
        Set<Object> topPlayers = redisTemplate.opsForZSet().reverseRange(zSetKey, 0, 2);
        Set<ZSetOperations.TypedTuple<Object>> topPlayersWithScore = 
            redisTemplate.opsForZSet().reverseRangeWithScores(zSetKey, 0, 2);
        
        // 获取分数
        Double score = redisTemplate.opsForZSet().score(zSetKey, "player1");
    }
}
```

### 3. Redisson 分布式锁示例

```java
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
            // 尝试获取锁，最多等待 10 秒，锁定时间 30 秒
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
    
    /**
     * 公平锁
     */
    public void fairLock() {
        RLock fairLock = redissonClient.getFairLock("fairLock");
        
        try {
            fairLock.lock(30, TimeUnit.SECONDS);
            doBusinessLogic();
        } finally {
            fairLock.unlock();
        }
    }
    
    /**
     * 读写锁
     */
    public void readWriteLock() {
        RReadWriteLock rwLock = redissonClient.getReadWriteLock("rwLock");
        
        // 读锁
        RLock readLock = rwLock.readLock();
        try {
            readLock.lock();
            // 执行读操作
            readData();
        } finally {
            readLock.unlock();
        }
        
        // 写锁
        RLock writeLock = rwLock.writeLock();
        try {
            writeLock.lock();
            // 执行写操作
            writeData();
        } finally {
            writeLock.unlock();
        }
    }
    
    /**
     * 信号量
     */
    public void semaphore() {
        RSemaphore semaphore = redissonClient.getSemaphore("semaphore");
        
        try {
            // 获取许可
            semaphore.acquire(2);
            // 执行业务逻辑
            doBusinessLogic();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            // 释放许可
            semaphore.release(2);
        }
    }
    
    /**
     * 闭锁
     */
    public void countDownLatch() {
        RCountDownLatch latch = redissonClient.getCountDownLatch("latch");
        
        try {
            // 等待其他线程完成
            latch.await();
            // 所有线程完成后执行
            doAfterAllComplete();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private void doBusinessLogic() {
        // 模拟业务逻辑
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private void readData() {
        // 模拟读操作
    }
    
    private void writeData() {
        // 模拟写操作
    }
    
    private void doAfterAllComplete() {
        // 模拟所有线程完成后的操作
    }
}
```

### 4. 缓存配置类

```java
@Configuration
@EnableCaching
public class RedisCacheConfiguration {
    
    /**
     * RedisTemplate 配置
     */
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
    
    /**
     * 缓存管理器配置
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10))  // 默认过期时间
            .serializeKeysWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new GenericJackson2JsonRedisSerializer()))
            .disableCachingNullValues();  // 不缓存 null 值
        
        // 为不同缓存设置不同的过期时间
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put("user", config.entryTtl(Duration.ofMinutes(30)));
        cacheConfigurations.put("product", config.entryTtl(Duration.ofHours(1)));
        cacheConfigurations.put("order", config.entryTtl(Duration.ofMinutes(5)));
        
        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(config)
            .withInitialCacheConfigurations(cacheConfigurations)
            .build();
    }
    
    /**
     * 缓存键生成器
     */
    @Bean
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getSimpleName());
            sb.append(":");
            sb.append(method.getName());
            for (Object param : params) {
                sb.append(":");
                sb.append(param.toString());
            }
            return sb.toString();
        };
    }
}
```

## Redis 监控配置

### 监控指标

Spring Redis 模块提供以下监控指标：

#### 连接池监控指标
```properties
# Lettuce 连接池指标
lettuce.pool.active.connections     # 活跃连接数
lettuce.pool.idle.connections       # 空闲连接数
lettuce.pool.max.connections        # 最大连接数
lettuce.pool.min.connections        # 最小连接数
lettuce.pool.pending.connections    # 等待连接数

# Redis 操作指标
redis.commands.duration             # Redis 命令执行时间
redis.commands.count                # Redis 命令执行次数
redis.connections.active            # Redis 活跃连接数
```

#### 缓存监控指标
```properties
# Spring Cache 指标
cache.gets                          # 缓存获取次数
cache.puts                          # 缓存写入次数
cache.evictions                     # 缓存驱逐次数
cache.hits                          # 缓存命中次数
cache.misses                        # 缓存未命中次数
```

### 健康检查

```java
@Component
public class RedisHealthIndicator implements HealthIndicator {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Override
    public Health health() {
        try {
            // 执行简单的 Redis 操作来检查连接
            String testKey = "health:check:" + System.currentTimeMillis();
            redisTemplate.opsForValue().set(testKey, "ok", Duration.ofSeconds(10));
            String value = (String) redisTemplate.opsForValue().get(testKey);
            redisTemplate.delete(testKey);
            
            if ("ok".equals(value)) {
                return Health.up()
                    .withDetail("redis", "Available")
                    .withDetail("timestamp", System.currentTimeMillis())
                    .build();
            } else {
                return Health.down()
                    .withDetail("redis", "Unavailable")
                    .withDetail("error", "Health check failed")
                    .build();
            }
        } catch (Exception e) {
            return Health.down()
                .withDetail("redis", "Unavailable")
                .withDetail("error", e.getMessage())
                .build();
        }
    }
}
```

### 监控端点访问

```bash
# 查看 Redis 健康状态
curl http://localhost:8338/api/actuator/health

# 查看连接池指标
curl http://localhost:8338/api/actuator/metrics/lettuce.pool.active.connections

# 查看缓存指标
curl http://localhost:8338/api/actuator/metrics/cache.gets

# 查看所有 Prometheus 指标
curl http://localhost:8338/api/actuator/prometheus | grep redis
```

### 告警配置建议

```yaml
# Redis 告警规则
groups:
  - name: redis-monitoring
    rules:
      # Redis 连接池使用率告警
      - alert: HighRedisConnectionPoolUsage
        expr: (lettuce_pool_active_connections / lettuce_pool_max_connections) * 100 > 80
        for: 2m
        labels:
          severity: warning
          service: sprival
          component: redis
        annotations:
          summary: "Redis 连接池使用率过高"
          description: "连接池使用率 {{ $value }}% 超过80%阈值"
          
      # Redis 命令执行时间告警
      - alert: SlowRedisCommand
        expr: histogram_quantile(0.95, redis_commands_duration_seconds) > 0.1
        for: 1m
        labels:
          severity: warning
          service: sprival
          component: redis
        annotations:
          summary: "Redis 命令执行时间过长"
          description: "95%的命令执行时间超过100ms: {{ $value }}s"
          
      # 缓存命中率过低告警
      - alert: LowCacheHitRate
        expr: (cache_hits / (cache_hits + cache_misses)) * 100 < 70
        for: 5m
        labels:
          severity: warning
          service: sprival
          component: cache
        annotations:
          summary: "缓存命中率过低"
          description: "缓存命中率 {{ $value }}% 低于70%阈值"
```

## 常见问题

### Q1: 如何选择 Redis 客户端？
A: 项目推荐使用 Lettuce + Redisson 组合：
- **Lettuce**: 异步非阻塞，性能更好，适合高并发场景
- **Redisson**: 功能丰富，提供分布式锁、分布式集合等高级功能
- **Jedis**: 同步阻塞，连接池管理复杂，不推荐使用

### Q2: 缓存穿透、缓存击穿、缓存雪崩如何解决？
A: 
- **缓存穿透**: 使用布隆过滤器或缓存空值
- **缓存击穿**: 使用分布式锁或设置热点数据永不过期
- **缓存雪崩**: 设置随机过期时间或使用多级缓存

### Q3: 如何优化 Redis 性能？
A: 
- 合理设置连接池大小
- 使用 Pipeline 批量操作
- 选择合适的序列化方式
- 避免大 key 和热 key
- 使用 Redis 集群分散负载

### Q4: 分布式锁的注意事项？
A: 
- 设置合理的锁超时时间
- 确保业务逻辑在锁超时前完成
- 使用 try-finally 确保锁释放
- 避免锁重入问题
- 考虑锁的公平性

### Q5: 如何监控 Redis 性能？
A: 
- 监控连接池使用率
- 监控命令执行时间
- 监控内存使用情况
- 监控缓存命中率
- 设置合理的告警阈值

## 最佳实践

### 1. 缓存策略选择
- **读多写少**: 使用 `@Cacheable` 缓存查询结果
- **写多读少**: 使用 `@CacheEvict` 及时清除缓存
- **读写均衡**: 使用 `@CachePut` 更新缓存

### 2. 序列化方案选择
- **JSON 序列化**: 可读性好，跨语言兼容
- **JDK 序列化**: 性能好，但可读性差
- **Kryo 序列化**: 性能最好，但配置复杂

### 3. 连接池配置原则
- **开发环境**: 较小的连接池配置
- **测试环境**: 模拟生产环境的配置
- **生产环境**: 根据并发量动态调整

### 4. 分布式锁使用规范
- 锁的粒度要合适，避免锁竞争
- 设置合理的锁超时时间
- 使用 try-finally 确保锁释放
- 避免在锁内执行耗时操作

## 故障排查

### 连接问题
```bash
# 检查 Redis 连接状态
redis-cli -h localhost -p 6379 ping

# 查看连接池状态
curl http://localhost:8338/api/actuator/metrics/lettuce.pool.active.connections

# 查看 Redis 日志
tail -f /var/log/redis/redis.log
```

### 性能问题
```bash
# 查看 Redis 慢日志
redis-cli slowlog get 10

# 查看 Redis 内存使用
redis-cli info memory

# 查看 Redis 统计信息
redis-cli info stats
```

### 缓存问题
```bash
# 查看缓存命中率
curl http://localhost:8338/api/actuator/metrics/cache.hits
curl http://localhost:8338/api/actuator/metrics/cache.misses

# 查看缓存配置
curl http://localhost:8338/api/actuator/configprops | grep cache
```

## 参考文档

- [Spring Data Redis 官方文档](https://spring.io/projects/spring-data-redis)
- [Redisson 官方文档](https://github.com/redisson/redisson)
- [Spring Cache 官方文档](https://spring.io/guides/gs/caching/)
- [Redis 官方文档](https://redis.io/documentation)

---

*本模块提供了企业级的 Redis 缓存解决方案，确保高性能、高可用性和易用性。*
