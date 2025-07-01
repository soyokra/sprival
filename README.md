# sprival 
Spring Component Integration Framework

包含组件集成和源码分析。spring官方文档参见[2.3.12.RELEASE](https://docs.spring.io/spring-boot/docs/2.3.12.RELEASE/reference/html/)


监控/告警/日志

health-check
exception-handle
timeout-set


app => app => app
           => mysql
           => redis
           => mongodb
           => clickhouse
           => ...

#### code
- spring 组件集成
- spring cloud集成
- devops部署方案(gitlab,k8s)

#### learn
- spring client + 中间件
- spring ioc, aop
- java
- k8s
- 设计模式

## 集成组件

#### MySQL
- jdbc
- hikari
- mybatis
- mybatis-plus
- p6spy

增加支持hikari的指标监控

#### HTTP请求
- Sentinel(限流：令牌桶、漏水桶、固定窗口、滑动窗口)
- 日志
- jwt
- 分布式锁

#### HTTP客户端
TODO

#### RabbitMQ
TODO

#### Kafka
TODO

#### MongoDB
TODO

#### Redis
TODO

#### ClickHouse
- mybatis
- mybatis-plus
- clickhouse

### 线程池



## 源码分析目录
- [spring-core](doc/spring/spring-core/README.md)
- [spring-mysql](doc/spring/spring-mysql/README.md)
- [spring-clickhouse](doc/spring/spring-clickhouse/README.md)
- [spring-rabbitmq](doc/spring/spring-rabbitmq/README.md)