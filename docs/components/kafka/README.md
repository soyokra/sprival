# Spring Kafka 模块

## 概述

Spring Kafka 模块提供了完整的 Apache Kafka 消息队列集成解决方案，包括消息生产消费、分区管理、事务支持、监控指标等功能。该模块基于 Spring Kafka 框架，为 Sprival 项目提供高性能、高可靠的消息传递服务。

## 核心特性

- ✅ **消息生产消费**: 支持同步和异步消息发送接收
- ✅ **分区管理**: 支持自定义分区策略和负载均衡
- ✅ **事务支持**: 支持 Kafka 事务和 Spring 事务集成
- ✅ **监控集成**: 与 Micrometer + Prometheus 无缝集成
- ✅ **健康检查**: 实时监控 Kafka 连接状态
- ✅ **序列化支持**: 支持多种序列化方式（JSON、Avro、Protobuf）
- ✅ **错误处理**: 完善的错误处理和重试机制
- ✅ **批量处理**: 支持批量消息生产和消费

## 组件清单

### 核心组件
- [spring-kafka](https://spring.io/projects/spring-kafka) - Spring Kafka 集成框架
- [kafka-clients](https://kafka.apache.org/documentation/) - Kafka Java 客户端（通过 spring-kafka 引入）
- [spring-kafka-test](https://spring.io/projects/spring-kafka) - Kafka 测试支持

### 功能组件
- **消息模板**: KafkaTemplate 消息发送
- **消息监听**: @KafkaListener 消息消费
- **监控指标**: Micrometer 指标收集
- **健康检查**: Spring Boot Actuator 集成
- **序列化器**: 多种序列化方案支持

## 快速开始

### 1. 添加依赖

项目已在 `pom.xml` 中配置了所需依赖：

```xml
<!-- Spring Kafka -->
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>

<!-- Spring Kafka Test -->
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka-test</artifactId>
    <scope>test</scope>
</dependency>
```

### 2. Kafka 服务准备

```bash
# 启动 Kafka 服务（Docker 方式）
docker run -d --name kafka -p 9092:9092 apache/kafka:latest

# 或使用项目提供的 Docker Compose
cd dockers && docker-compose up kafka
```

### 3. 基础配置

```properties
# Kafka 连接配置
spring.kafka.bootstrap-servers = localhost:9092

# 生产者配置
spring.kafka.producer.key-serializer = org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer = org.springframework.kafka.support.serializer.JsonSerializer
spring.kafka.producer.acks = all
spring.kafka.producer.retries = 3
spring.kafka.producer.batch-size = 16384
spring.kafka.producer.linger-ms = 1
spring.kafka.producer.buffer-memory = 33554432

# 消费者配置
spring.kafka.consumer.group-id = sprival-group
spring.kafka.consumer.key-deserializer = org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer = org.springframework.kafka.support.serializer.JsonDeserializer
spring.kafka.consumer.auto-offset-reset = earliest
spring.kafka.consumer.enable-auto-commit = false
spring.kafka.consumer.max-poll-records = 500
```

## 配置详解

### Kafka 连接配置

```properties
# 基础连接配置
spring.kafka.bootstrap-servers = localhost:9092                    # Kafka 服务器地址
spring.kafka.client-id = sprival-client                            # 客户端ID
spring.kafka.properties.security.protocol = PLAINTEXT             # 安全协议
spring.kafka.properties.sasl.mechanism = PLAIN                    # SASL 机制（可选）

# SSL 配置（可选）
spring.kafka.properties.ssl.keystore.location = classpath:keystore.jks
spring.kafka.properties.ssl.keystore.password = password
spring.kafka.properties.ssl.truststore.location = classpath:truststore.jks
spring.kafka.properties.ssl.truststore.password = password
```

### 生产者配置

```properties
# 序列化配置
spring.kafka.producer.key-serializer = org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer = org.springframework.kafka.support.serializer.JsonSerializer

# 可靠性配置
spring.kafka.producer.acks = all                                   # 确认级别：0, 1, all
spring.kafka.producer.retries = 3                                  # 重试次数
spring.kafka.producer.retry-backoff-ms = 100                       # 重试间隔

# 性能配置
spring.kafka.producer.batch-size = 16384                           # 批量大小
spring.kafka.producer.linger-ms = 1                                # 等待时间
spring.kafka.producer.buffer-memory = 33554432                     # 缓冲区大小
spring.kafka.producer.compression-type = gzip                      # 压缩类型

# 事务配置
spring.kafka.producer.transaction-id-prefix = sprival-tx-           # 事务ID前缀
spring.kafka.producer.enable-idempotence = true                    # 启用幂等性
```

### 消费者配置

```properties
# 反序列化配置
spring.kafka.consumer.key-deserializer = org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer = org.springframework.kafka.support.serializer.JsonDeserializer

# 消费者组配置
spring.kafka.consumer.group-id = sprival-group                     # 消费者组ID
spring.kafka.consumer.client-id = sprival-consumer                 # 客户端ID
spring.kafka.consumer.auto-offset-reset = earliest                 # 偏移量重置策略

# 提交配置
spring.kafka.consumer.enable-auto-commit = false                   # 禁用自动提交
spring.kafka.consumer.auto-commit-interval-ms = 1000               # 自动提交间隔

# 性能配置
spring.kafka.consumer.max-poll-records = 500                       # 单次拉取最大记录数
spring.kafka.consumer.max-poll-interval-ms = 300000                # 最大轮询间隔
spring.kafka.consumer.session-timeout-ms = 30000                   # 会话超时时间
spring.kafka.consumer.heartbeat-interval-ms = 3000                 # 心跳间隔

# 分区配置
spring.kafka.consumer.partition-assignment-strategy = org.apache.kafka.clients.consumer.RoundRobinAssignor
```

### 监听器配置

```properties
# 监听器容器配置
spring.kafka.listener.ack-mode = manual_immediate                  # 确认模式
spring.kafka.listener.ack-count = 10                               # 批量确认数量
spring.kafka.listener.ack-time = 1000                              # 批量确认时间

# 并发配置
spring.kafka.listener.concurrency = 3                              # 并发消费者数量
spring.kafka.listener.poll-timeout = 3000                          # 轮询超时时间

# 错误处理配置
spring.kafka.listener.auto-startup = true                          # 自动启动
spring.kafka.listener.missing-topics-fatal = false                 # 缺失主题不致命
```

## 使用示例

### 1. 消息发送示例

```java
@Service
@Slf4j
public class KafkaProducerService {
    
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    /**
     * 发送简单消息
     */
    public void sendMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);
        log.info("发送消息到主题 {}: {}", topic, message);
    }
    
    /**
     * 发送对象消息
     */
    public void sendObjectMessage(String topic, Object data) {
        kafkaTemplate.send(topic, data);
        log.info("发送对象消息到主题 {}: {}", topic, data);
    }
    
    /**
     * 发送到指定分区
     */
    public void sendToPartition(String topic, int partition, String key, Object data) {
        kafkaTemplate.send(topic, partition, key, data);
        log.info("发送消息到主题 {} 分区 {}: {}", topic, partition, data);
    }
    
    /**
     * 发送消息并等待确认
     */
    public void sendWithCallback(String topic, Object data) {
        ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, data);
        
        future.addCallback(
            result -> log.info("消息发送成功: {}", result.getRecordMetadata()),
            failure -> log.error("消息发送失败: {}", failure.getMessage())
        );
    }
    
    /**
     * 批量发送消息
     */
    public void sendBatchMessages(String topic, List<Object> messages) {
        for (Object message : messages) {
            kafkaTemplate.send(topic, message);
        }
        log.info("批量发送 {} 条消息到主题 {}", messages.size(), topic);
    }
}
```

### 2. 消息消费示例

```java
@Component
@Slf4j
public class KafkaConsumerService {
    
    /**
     * 简单消息消费
     */
    @KafkaListener(topics = "simple-topic")
    public void handleSimpleMessage(String message) {
        log.info("接收到消息: {}", message);
        // 处理业务逻辑
    }
    
    /**
     * 对象消息消费
     */
    @KafkaListener(topics = "user-topic")
    public void handleUserMessage(User user) {
        log.info("接收到用户消息: {}", user);
        // 处理用户相关业务
    }
    
    /**
     * 手动确认消息
     */
    @KafkaListener(topics = "manual-topic")
    public void handleManualMessage(String message, Acknowledgment ack) {
        try {
            log.info("接收到消息: {}", message);
            // 处理业务逻辑
            
            // 手动确认消息
            ack.acknowledge();
        } catch (Exception e) {
            log.error("处理消息失败: {}", e.getMessage());
            // 不确认消息，让消息重新消费
        }
    }
    
    /**
     * 批量消费消息
     */
    @KafkaListener(topics = "batch-topic", containerFactory = "batchKafkaListenerContainerFactory")
    public void handleBatchMessages(List<String> messages) {
        log.info("接收到批量消息，数量: {}", messages.size());
        for (String message : messages) {
            // 处理每条消息
            log.info("处理消息: {}", message);
        }
    }
    
    /**
     * 指定分区消费
     */
    @KafkaListener(topicPartitions = @TopicPartition(topic = "partitioned-topic", partitions = {"0", "1"}))
    public void handlePartitionedMessage(String message, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
        log.info("从分区 {} 接收到消息: {}", partition, message);
    }
    
    /**
     * 带错误处理的消息消费
     */
    @KafkaListener(topics = "error-topic", errorHandler = "kafkaErrorHandler")
    public void handleMessageWithError(String message) {
        log.info("接收到消息: {}", message);
        // 模拟可能出错的处理逻辑
        if (message.contains("error")) {
            throw new RuntimeException("处理消息时发生错误");
        }
    }
}
```

### 3. 配置类示例

```java
@Configuration
@EnableKafka
public class KafkaConfiguration {
    
    /**
     * 生产者配置
     */
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        configProps.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        
        return new DefaultKafkaProducerFactory<>(configProps);
    }
    
    /**
     * KafkaTemplate 配置
     */
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
    
    /**
     * 消费者配置
     */
    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "sprival-group");
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        
        return new DefaultKafkaConsumerFactory<>(configProps);
    }
    
    /**
     * 监听器容器工厂配置
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = 
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(3);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }
    
    /**
     * 批量监听器容器工厂配置
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> batchKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = 
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setBatchListener(true);
        factory.setConcurrency(3);
        return factory;
    }
    
    /**
     * 错误处理器配置
     */
    @Bean
    public KafkaListenerErrorHandler kafkaErrorHandler() {
        return (message, exception) -> {
            log.error("Kafka 消息处理失败: {}", exception.getMessage());
            // 可以在这里实现错误处理逻辑，如发送到死信队列
            return null;
        };
    }
}
```

### 4. 事务支持示例

```java
@Service
@Transactional
public class KafkaTransactionService {
    
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    @Autowired
    private UserMapper userMapper;
    
    /**
     * 数据库和 Kafka 事务集成
     */
    @Transactional
    public void createUserWithMessage(User user) {
        // 数据库操作
        userMapper.insert(user);
        
        // Kafka 消息发送（在同一事务中）
        kafkaTemplate.send("user-created", user);
        
        // 如果后续操作失败，数据库回滚，Kafka 消息也不会发送
    }
    
    /**
     * 纯 Kafka 事务
     */
    @Transactional
    public void sendTransactionalMessage(String topic, Object data) {
        kafkaTemplate.executeInTransaction(operations -> {
            operations.send(topic, data);
            operations.send(topic + "-backup", data);
            return null;
        });
    }
}
```

## 监控配置

### 监控指标

Spring Kafka 模块提供以下监控指标：

#### 生产者指标
```properties
# Kafka 生产者指标
kafka_producer_record_send_total                    # 发送记录总数
kafka_producer_record_send_rate                     # 发送记录速率
kafka_producer_record_error_total                   # 发送错误总数
kafka_producer_byte_rate                            # 发送字节速率
kafka_producer_compression_rate                     # 压缩率
kafka_producer_batch_size_avg                       # 平均批量大小
kafka_producer_batch_size_max                       # 最大批量大小
```

#### 消费者指标
```properties
# Kafka 消费者指标
kafka_consumer_records_consumed_total               # 消费记录总数
kafka_consumer_records_consumed_rate                # 消费记录速率
kafka_consumer_fetch_rate                           # 拉取速率
kafka_consumer_fetch_latency_avg                    # 平均拉取延迟
kafka_consumer_fetch_latency_max                    # 最大拉取延迟
kafka_consumer_commit_rate                          # 提交速率
kafka_consumer_commit_latency_avg                   # 平均提交延迟
```

#### 连接指标
```properties
# Kafka 连接指标
kafka_network_io_total                              # 网络IO总数
kafka_network_io_rate                               # 网络IO速率
kafka_connection_count                              # 连接数
kafka_connection_creation_rate                      # 连接创建速率
kafka_connection_close_rate                         # 连接关闭速率
```

### 健康检查

```java
@Component
public class KafkaHealthIndicator implements HealthIndicator {
    
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    @Override
    public Health health() {
        try {
            // 执行简单的 Kafka 操作来检查连接
            String testTopic = "health-check-topic";
            String testMessage = "health-check-" + System.currentTimeMillis();
            
            kafkaTemplate.send(testTopic, testMessage);
            
            return Health.up()
                .withDetail("kafka", "Available")
                .withDetail("bootstrap-servers", "localhost:9092")
                .withDetail("timestamp", System.currentTimeMillis())
                .build();
        } catch (Exception e) {
            return Health.down()
                .withDetail("kafka", "Unavailable")
                .withDetail("error", e.getMessage())
                .build();
        }
    }
}
```

### 监控端点访问

```bash
# 查看 Kafka 健康状态
curl http://localhost:8338/api/actuator/health

# 查看生产者指标
curl http://localhost:8338/api/actuator/metrics/kafka.producer.record.send.total

# 查看消费者指标
curl http://localhost:8338/api/actuator/metrics/kafka.consumer.records.consumed.total

# 查看所有 Prometheus 指标
curl http://localhost:8338/api/actuator/prometheus | grep kafka
```

### 告警配置建议

```yaml
# Kafka 告警规则
groups:
  - name: kafka-monitoring
    rules:
      # Kafka 生产者发送失败率过高告警
      - alert: HighKafkaProducerErrorRate
        expr: (kafka_producer_record_error_total / kafka_producer_record_send_total) * 100 > 5
        for: 2m
        labels:
          severity: warning
          service: sprival
          component: kafka
        annotations:
          summary: "Kafka 生产者发送失败率过高"
          description: "生产者发送失败率 {{ $value }}% 超过5%阈值"
          
      # Kafka 消费者延迟过高告警
      - alert: HighKafkaConsumerLag
        expr: kafka_consumer_fetch_latency_avg > 1000
        for: 1m
        labels:
          severity: critical
          service: sprival
          component: kafka
        annotations:
          summary: "Kafka 消费者延迟过高"
          description: "消费者平均拉取延迟 {{ $value }}ms 超过1000ms阈值"
          
      # Kafka 连接数异常告警
      - alert: KafkaConnectionAnomaly
        expr: kafka_connection_count == 0
        for: 30s
        labels:
          severity: critical
          service: sprival
          component: kafka
        annotations:
          summary: "Kafka 连接异常"
          description: "Kafka 连接数为0，可能存在连接问题"
```

## 常见问题

### Q1: 如何选择序列化方式？
A: 根据数据特点选择：
- **StringSerializer**: 简单字符串消息
- **JsonSerializer**: 复杂对象，可读性好
- **AvroSerializer**: 高性能，需要Schema注册中心
- **ProtobufSerializer**: 高性能，跨语言兼容

### Q2: 如何处理消息重复消费？
A: 
- 使用幂等性设计
- 在消息中添加唯一ID
- 使用数据库唯一约束
- 实现分布式锁

### Q3: 如何优化 Kafka 性能？
A: 
- 合理配置批量大小和等待时间
- 使用压缩减少网络传输
- 调整分区数量提高并行度
- 优化消费者组配置

### Q4: 事务和幂等性的区别？
A: 
- **事务**: 保证多个操作的原子性
- **幂等性**: 保证重复操作的结果一致
- 事务可以包含多个操作，幂等性针对单个操作

### Q5: 如何监控 Kafka 性能？
A: 
- 监控生产者发送速率和错误率
- 监控消费者消费延迟和吞吐量
- 监控连接数和网络IO
- 设置合理的告警阈值

## 最佳实践

### 1. 消息设计原则
- 消息体尽量小，避免大对象
- 使用JSON格式，便于调试
- 添加消息版本号，支持兼容性
- 设置合理的TTL

### 2. 分区设计原则
- 按业务功能划分主题
- 使用有意义的主题名称
- 合理设置分区数量
- 考虑消息顺序性要求

### 3. 消费者组设计
- 按业务功能划分消费者组
- 避免消费者组内分区不均衡
- 合理设置并发消费者数量
- 考虑消息处理能力

### 4. 错误处理策略
- 实现重试机制
- 使用死信队列
- 记录错误日志
- 发送告警通知

## 故障排查

### 连接问题
```bash
# 检查 Kafka 连接状态
kafka-topics.sh --bootstrap-server localhost:9092 --list

# 查看主题列表
kafka-topics.sh --bootstrap-server localhost:9092 --list

# 查看主题详情
kafka-topics.sh --bootstrap-server localhost:9092 --describe --topic test-topic
```

### 消费者问题
```bash
# 查看消费者组列表
kafka-consumer-groups.sh --bootstrap-server localhost:9092 --list

# 查看消费者组详情
kafka-consumer-groups.sh --bootstrap-server localhost:9092 --describe --group sprival-group

# 重置消费者组偏移量
kafka-consumer-groups.sh --bootstrap-server localhost:9092 --reset-offsets --to-earliest --group sprival-group --topic test-topic --execute
```

### 性能问题
```bash
# 查看生产者性能
kafka-producer-perf-test.sh --topic test-topic --num-records 1000 --record-size 1000 --throughput 100 --producer-props bootstrap.servers=localhost:9092

# 查看消费者性能
kafka-consumer-perf-test.sh --topic test-topic --bootstrap-server localhost:9092 --messages 1000
```

## 参考文档

- [Spring Kafka 官方文档](https://spring.io/projects/spring-kafka)
- [Apache Kafka 官方文档](https://kafka.apache.org/documentation/)
- [Spring Boot Kafka 文档](https://docs.spring.io/spring-boot/docs/current/reference/html/messaging.html#messaging.kafka)

---

*本模块提供了企业级的 Kafka 消息队列解决方案，确保高可靠性、高性能和易用性。*
