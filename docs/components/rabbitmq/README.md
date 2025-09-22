# Spring RabbitMQ 模块

## 概述

Spring RabbitMQ 模块提供了完整的 RabbitMQ 消息队列集成解决方案，包括消息发布订阅、队列管理、死信处理、监控指标等功能。该模块基于 Spring AMQP 框架，为 Sprival 项目提供可靠的消息传递服务。

## 核心特性

- ✅ **消息发布订阅**: 支持多种交换器类型（Direct、Topic、Fanout、Headers）
- ✅ **队列管理**: 自动创建队列、绑定关系、死信队列
- ✅ **消息确认机制**: 支持手动确认和自动确认
- ✅ **重试机制**: 消息发送失败自动重试
- ✅ **死信处理**: 死信队列和死信交换器支持
- ✅ **连接池管理**: 高性能连接池配置
- ✅ **监控集成**: 与 Prometheus + Grafana 无缝集成
- ✅ **健康检查**: 实时监控 RabbitMQ 连接状态

## 组件清单

### 核心组件
- [spring-boot-starter-amqp](https://spring.io/projects/spring-amqp) - Spring AMQP 集成
- [spring-rabbit](https://github.com/spring-projects/spring-amqp) - RabbitMQ 客户端实现
- [rabbitmq-java-client](https://github.com/rabbitmq/rabbitmq-java-client) - RabbitMQ Java 客户端

### 功能组件
- **消息模板**: RabbitTemplate 消息发送
- **消息监听**: @RabbitListener 消息消费
- **连接管理**: CachingConnectionFactory 连接池
- **监控指标**: Micrometer 指标收集
- **健康检查**: Spring Boot Actuator 集成

## 快速开始

### 1. 添加依赖

项目已在 `pom.xml` 中配置了所需依赖：

```xml
<!-- Spring AMQP -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

### 2. RabbitMQ 服务准备

```bash
# 启动 RabbitMQ 服务（Docker 方式）
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management

# 或使用项目提供的 Docker Compose
cd dockers && docker-compose up rabbitmq
```

### 3. 基础配置

```properties
# RabbitMQ 连接配置
spring.rabbitmq.host = localhost
spring.rabbitmq.port = 5672
spring.rabbitmq.username = guest
spring.rabbitmq.password = guest
spring.rabbitmq.virtual-host = /

# 连接池配置
spring.rabbitmq.cache.connection.mode = connection
spring.rabbitmq.cache.connection.size = 10
spring.rabbitmq.cache.channel.size = 25
spring.rabbitmq.cache.channel.checkout-timeout = 30000

# 消息确认配置
spring.rabbitmq.publisher-confirm-type = correlated
spring.rabbitmq.publisher-returns = true
spring.rabbitmq.listener.simple.acknowledge-mode = manual
```

## 配置详解

### RabbitMQ 连接配置

```properties
# 基础连接配置
spring.rabbitmq.host = localhost                    # RabbitMQ 服务器地址
spring.rabbitmq.port = 5672                         # RabbitMQ 端口
spring.rabbitmq.username = guest                    # 用户名
spring.rabbitmq.password = guest                    # 密码
spring.rabbitmq.virtual-host = /                    # 虚拟主机
spring.rabbitmq.connection-timeout = 15000          # 连接超时时间（毫秒）

# SSL 配置（可选）
spring.rabbitmq.ssl.enabled = false                 # 是否启用 SSL
spring.rabbitmq.ssl.key-store = classpath:keystore.p12
spring.rabbitmq.ssl.key-store-password = password
spring.rabbitmq.ssl.trust-store = classpath:truststore.p12
spring.rabbitmq.ssl.trust-store-password = password
```

### 连接池配置

```properties
# 连接池配置
spring.rabbitmq.cache.connection.mode = connection   # 连接模式：connection 或 channel
spring.rabbitmq.cache.connection.size = 10          # 连接池大小
spring.rabbitmq.cache.channel.size = 25             # 通道池大小
spring.rabbitmq.cache.channel.checkout-timeout = 30000  # 通道获取超时时间

# 连接工厂配置
spring.rabbitmq.cache.connection.connection-name = sprival-connection
spring.rabbitmq.cache.connection.connection-timeout = 15000
spring.rabbitmq.cache.connection.requested-heartbeat = 30
```

### 消息确认配置

```properties
# 发布者确认配置
spring.rabbitmq.publisher-confirm-type = correlated  # 确认类型：none, simple, correlated
spring.rabbitmq.publisher-returns = true            # 是否启用返回机制

# 消费者确认配置
spring.rabbitmq.listener.simple.acknowledge-mode = manual  # 确认模式：none, auto, manual
spring.rabbitmq.listener.simple.retry.enabled = true      # 是否启用重试
spring.rabbitmq.listener.simple.retry.max-attempts = 3    # 最大重试次数
spring.rabbitmq.listener.simple.retry.initial-interval = 1000  # 初始重试间隔
spring.rabbitmq.listener.simple.retry.multiplier = 2      # 重试间隔倍数
spring.rabbitmq.listener.simple.retry.max-interval = 10000 # 最大重试间隔
```

### 死信队列配置

```properties
# 死信队列配置
spring.rabbitmq.listener.simple.default-requeue-rejected = false  # 拒绝消息不重新入队
spring.rabbitmq.listener.simple.dead-letter-exchange = dlx.exchange  # 死信交换器
spring.rabbitmq.listener.simple.dead-letter-routing-key = dlx.routing.key  # 死信路由键
```

## 使用示例

### 1. 消息发送示例

```java
@Service
@Slf4j
public class MessageProducerService {
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    /**
     * 发送简单消息
     */
    public void sendSimpleMessage(String message) {
        rabbitTemplate.convertAndSend("simple.queue", message);
        log.info("发送消息: {}", message);
    }
    
    /**
     * 发送对象消息
     */
    public void sendObjectMessage(User user) {
        rabbitTemplate.convertAndSend("user.queue", user);
        log.info("发送用户消息: {}", user);
    }
    
    /**
     * 发送到指定交换器
     */
    public void sendToExchange(String exchange, String routingKey, Object message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
        log.info("发送消息到交换器 {}: {}", exchange, message);
    }
    
    /**
     * 发送消息并等待确认
     */
    public void sendWithConfirm(String message) {
        rabbitTemplate.convertAndSend("confirm.queue", message, new CorrelationData(UUID.randomUUID().toString()));
        log.info("发送消息并等待确认: {}", message);
    }
}
```

### 2. 消息消费示例

```java
@Component
@Slf4j
public class MessageConsumerService {
    
    /**
     * 简单消息消费
     */
    @RabbitListener(queues = "simple.queue")
    public void handleSimpleMessage(String message) {
        log.info("接收到消息: {}", message);
        // 处理业务逻辑
    }
    
    /**
     * 对象消息消费
     */
    @RabbitListener(queues = "user.queue")
    public void handleUserMessage(User user) {
        log.info("接收到用户消息: {}", user);
        // 处理用户相关业务
    }
    
    /**
     * 手动确认消息
     */
    @RabbitListener(queues = "manual.queue")
    public void handleManualMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        try {
            log.info("接收到消息: {}", message);
            // 处理业务逻辑
            
            // 手动确认消息
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            try {
                // 拒绝消息并重新入队
                channel.basicNack(deliveryTag, false, true);
            } catch (IOException ioException) {
                log.error("拒绝消息失败", ioException);
            }
        }
    }
    
    /**
     * 死信队列处理
     */
    @RabbitListener(queues = "dlx.queue")
    public void handleDeadLetterMessage(String message) {
        log.error("接收到死信消息: {}", message);
        // 处理死信消息，如记录日志、发送告警等
    }
}
```

### 3. 队列和交换器配置

```java
@Configuration
@EnableRabbit
public class RabbitMQConfiguration {
    
    /**
     * 声明队列
     */
    @Bean
    public Queue simpleQueue() {
        return QueueBuilder.durable("simple.queue").build();
    }
    
    @Bean
    public Queue userQueue() {
        return QueueBuilder.durable("user.queue").build();
    }
    
    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable("dlx.queue").build();
    }
    
    /**
     * 声明交换器
     */
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange("direct.exchange");
    }
    
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange("topic.exchange");
    }
    
    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange("fanout.exchange");
    }
    
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange("dlx.exchange");
    }
    
    /**
     * 绑定队列和交换器
     */
    @Bean
    public Binding simpleBinding() {
        return BindingBuilder.bind(simpleQueue()).to(directExchange()).with("simple.routing.key");
    }
    
    @Bean
    public Binding userBinding() {
        return BindingBuilder.bind(userQueue()).to(topicExchange()).with("user.*");
    }
    
    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange()).with("dlx.routing.key");
    }
}
```

### 4. 消息确认回调

```java
@Component
@Slf4j
public class RabbitMQConfirmCallback implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {
    
    @PostConstruct
    public void init() {
        // 设置确认回调
        rabbitTemplate.setConfirmCallback(this);
        // 设置返回回调
        rabbitTemplate.setReturnCallback(this);
    }
    
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (ack) {
            log.info("消息发送成功: {}", correlationData.getId());
        } else {
            log.error("消息发送失败: {}, 原因: {}", correlationData.getId(), cause);
        }
    }
    
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        log.error("消息返回: exchange={}, routingKey={}, replyCode={}, replyText={}", 
                  exchange, routingKey, replyCode, replyText);
    }
}
```

## 监控配置

### 监控指标

Spring RabbitMQ 模块提供以下监控指标：

#### 连接和通道指标
```properties
# RabbitMQ 连接指标
rabbitmq_connections                      # 当前连接数
rabbitmq_channels                         # 当前通道数
rabbitmq_connection_created_total         # 连接创建总数
rabbitmq_connection_closed_total          # 连接关闭总数
```

#### 消息发布指标
```properties
# 消息发布指标
rabbitmq_published_total                  # 发布消息总数
rabbitmq_acknowledged_published_total     # 已确认发布消息总数
rabbitmq_not_acknowledged_published_total # 未确认发布消息总数
rabbitmq_failed_to_publish_total          # 发布失败消息总数
rabbitmq_unrouted_published_total         # 未路由消息总数
```

#### 消息消费指标
```properties
# 消息消费指标
rabbitmq_consumed_total                   # 消费消息总数
rabbitmq_acknowledged_total               # 已确认消费消息总数
rabbitmq_rejected_total                   # 拒绝消息总数
```

### 健康检查

```java
@Component
public class RabbitMQHealthIndicator implements HealthIndicator {
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @Override
    public Health health() {
        try {
            // 执行简单的 RabbitMQ 操作来检查连接
            String testMessage = "health-check-" + System.currentTimeMillis();
            rabbitTemplate.convertAndSend("health-check-queue", testMessage);
            
            return Health.up()
                .withDetail("rabbitmq", "Available")
                .withDetail("timestamp", System.currentTimeMillis())
                .build();
        } catch (Exception e) {
            return Health.down()
                .withDetail("rabbitmq", "Unavailable")
                .withDetail("error", e.getMessage())
                .build();
        }
    }
}
```

### 监控端点访问

```bash
# 查看 RabbitMQ 健康状态
curl http://localhost:8338/api/actuator/health

# 查看连接指标
curl http://localhost:8338/api/actuator/metrics/rabbitmq.connections

# 查看消息发布指标
curl http://localhost:8338/api/actuator/metrics/rabbitmq.published.total

# 查看所有 Prometheus 指标
curl http://localhost:8338/api/actuator/prometheus | grep rabbitmq
```

### 告警配置建议

```yaml
# RabbitMQ 告警规则
groups:
  - name: rabbitmq-monitoring
    rules:
      # RabbitMQ 连接数过高告警
      - alert: HighRabbitMQConnections
        expr: rabbitmq_connections > 100
        for: 2m
        labels:
          severity: warning
          service: sprival
          component: rabbitmq
        annotations:
          summary: "RabbitMQ 连接数过高"
          description: "当前连接数 {{ $value }} 超过100个"
          
      # 消息发布失败率过高告警
      - alert: HighRabbitMQPublishFailureRate
        expr: (rabbitmq_failed_to_publish_total / rabbitmq_published_total) * 100 > 5
        for: 1m
        labels:
          severity: critical
          service: sprival
          component: rabbitmq
        annotations:
          summary: "RabbitMQ 消息发布失败率过高"
          description: "发布失败率 {{ $value }}% 超过5%阈值"
          
      # 消息消费延迟告警
      - alert: RabbitMQConsumerLag
        expr: rabbitmq_consumed_total - rabbitmq_acknowledged_total > 1000
        for: 5m
        labels:
          severity: warning
          service: sprival
          component: rabbitmq
        annotations:
          summary: "RabbitMQ 消息消费延迟"
          description: "未确认消息数 {{ $value }} 超过1000个"
```

## 常见问题

### Q1: 如何选择消息确认模式？
A: 根据业务需求选择：
- **none**: 不确认，性能最好但可能丢失消息
- **auto**: 自动确认，处理成功自动确认，失败则拒绝
- **manual**: 手动确认，需要业务代码显式确认，最可靠

### Q2: 如何处理消息重复消费？
A: 
- 使用幂等性设计
- 在消息中添加唯一ID
- 使用数据库唯一约束
- 实现分布式锁

### Q3: 如何优化 RabbitMQ 性能？
A: 
- 合理配置连接池大小
- 使用批量确认
- 避免大消息
- 合理设置TTL
- 使用集群模式

### Q4: 死信队列的作用？
A: 
- 处理无法路由的消息
- 处理被拒绝的消息
- 处理过期的消息
- 实现消息重试机制

### Q5: 如何监控 RabbitMQ 性能？
A: 
- 监控连接数和通道数
- 监控消息发布和消费速率
- 监控队列长度
- 监控消息确认率
- 设置合理的告警阈值

## 最佳实践

### 1. 消息设计原则
- 消息体尽量小，避免大对象
- 使用JSON格式，便于调试
- 添加消息版本号，支持兼容性
- 设置合理的TTL

### 2. 队列设计原则
- 按业务功能划分队列
- 使用有意义的队列名称
- 合理设置队列参数
- 考虑死信队列

### 3. 交换器选择
- **Direct**: 精确匹配路由键
- **Topic**: 模式匹配路由键
- **Fanout**: 广播到所有绑定队列
- **Headers**: 基于消息头匹配

### 4. 错误处理策略
- 实现重试机制
- 使用死信队列
- 记录错误日志
- 发送告警通知

## 故障排查

### 连接问题
```bash
# 检查 RabbitMQ 连接状态
rabbitmqctl status

# 查看连接列表
rabbitmqctl list_connections

# 查看通道列表
rabbitmqctl list_channels
```

### 队列问题
```bash
# 查看队列列表
rabbitmqctl list_queues

# 查看队列详情
rabbitmqctl list_queues name messages consumers

# 清空队列
rabbitmqctl purge_queue queue_name
```

### 性能问题
```bash
# 查看交换器列表
rabbitmqctl list_exchanges

# 查看绑定关系
rabbitmqctl list_bindings

# 查看消费者列表
rabbitmqctl list_consumers
```

## 参考文档

- [Spring AMQP 官方文档](https://spring.io/projects/spring-amqp)
- [RabbitMQ 官方文档](https://www.rabbitmq.com/documentation.html)
- [Spring Boot AMQP 文档](https://docs.spring.io/spring-boot/docs/current/reference/html/messaging.html#messaging.amqp)

---

*本模块提供了企业级的 RabbitMQ 消息队列解决方案，确保高可靠性、高性能和易用性。*