## 说明
sprival是spring boot 组件集成模板，提供一份全面的pom.xml组件引入，各个组件配置使用说明，监控指标方案，以及后续k8s部署方案(gitlab,docker,docker-hub...)，elk日志集成方案，prometheus监控集成方案

## 组件模块说明

### http-server
http请求相关组件，目前还是研究中
- jetty web服务器
- Guava RateLimiter 接口限流

### http-client
http请求相关组件，这块还在选择中

- 负责处理请求响应的http客户端，例如jdk自带的HttpURLConnection，Apache HttpClient，OkHttpClient
- 熔断器，例如hystrix，resilience4j-circuitbreaker，Spring Cloud CircuitBreaker, sentinel
- 重试器，例如Resilience4j-retry
- 负载均衡器，例如Ribbon，load-balance

Retrofit 和 Feign两个声明式http客户端集成了相关的组件

### spring-mysql
数据库，这块基本选定
- mybatis-plus
- dynamic-datasource
- hikari
- p6spy
- jdbc(mysql-connector-java)

### spring-data
kafka,mongodb,rabbit,elasticsearch选用的都是spring官方的

### spring-redis
redis操作，这部分还在研究中
- spring cache
- spring data redis 
- redisson

spring cache 是声明式缓存，使用多种缓存驱动，包括本地内存缓存和redis缓存，使用redis驱动时，操作redis取决于
org.springframework.data.redis.connection.RedisConnectionFactory的接口实现

spring data redis 是操作redis的集成客户端，底层可以选择使用jedis或lettuce

redisson 是比较全面的redis客户端操作，引入redisson的时候，其自动装配类会优先注册基于redisson client 的 RedisConnectionFactory。也就意味着
spring cache 和 spring data redis用的都是redisson client操作redis

### spring-clickhouse
选用clickhouse，集成到了mybatis-plus


## 文档
docs目录

## 需求
由于需要ai辅助开发，需要有很多提示词需求文档，上下文管理问题，请先生成ai辅助开发文档规范，以便后续sprival项目的持续迭代开发