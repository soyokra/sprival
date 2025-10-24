# Kafka + ELK 日志集成测试指南

## 概述

本指南将帮助您测试完整的日志流程：**Spring Boot → Kafka → Logstash → Elasticsearch → Kibana**

## 架构图

```
Spring Boot Application
        ↓ (Kafka Producer)
    Kafka Topic: application-logs
        ↓ (Kafka Consumer)
    Logstash Pipeline
        ↓ (Index to Elasticsearch)
    Elasticsearch Index: sprival-logs-*
        ↓ (Query & Visualize)
    Kibana Dashboard
```

## 启动步骤

### 1. 启动 ELK + Kafka 服务

```bash
cd docker/sprival-logging
docker-compose up -d
```

### 2. 检查服务状态

```bash
# 检查 Elasticsearch
curl http://localhost:9200

# 检查 Kibana
curl http://localhost:5601

# 检查 Logstash
curl http://localhost:9600

# 检查 Kafka
docker exec sprival-logging-kafka-1 /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --list
```

### 3. 启动 Spring Boot 应用

```bash
cd ../../
mvn spring-boot:run
```

## 测试接口

### 基本日志测试
```bash
curl http://localhost:8338/api/test/logging/basic
```

### 异常日志测试
```bash
curl http://localhost:8338/api/test/logging/exception
```

### MDC 日志测试
```bash
curl "http://localhost:8338/api/test/logging/mdc?userId=test-user-123"
```

### 结构化日志测试
```bash
curl "http://localhost:8338/api/test/logging/structured?action=test-action"
```

### 批量日志测试
```bash
curl "http://localhost:8338/api/test/logging/batch?count=10"
```

### 配置信息查看
```bash
curl http://localhost:8338/api/test/logging/config
```

## 验证步骤

### 1. 检查 Kafka 主题

```bash
# 查看主题列表
docker exec sprival-logging-kafka-1 /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --list

# 查看主题消息
docker exec sprival-logging-kafka-1 /opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic application-logs --from-beginning
```

### 2. 检查 Elasticsearch 索引

```bash
# 查看所有索引
curl "http://localhost:9200/_cat/indices?v"

# 查看 sprival-logs 索引
curl "http://localhost:9200/sprival-logs-*/_search?pretty"

# 查看索引统计
curl "http://localhost:9200/sprival-logs-*/_count"
```

### 3. 在 Kibana 中查看日志

1. 访问 http://localhost:5601
2. 进入 **Stack Management** > **Index Patterns**
3. 创建索引模式：`sprival-logs-*`
4. 选择时间字段：`@timestamp`
5. 进入 **Discover** 查看日志数据

## 日志字段说明

### 基本字段
- `@timestamp`: 日志时间戳
- `timestamp`: 原始时间戳
- `level`: 日志级别 (INFO, DEBUG, WARN, ERROR)
- `loggerName`: 日志记录器名称
- `threadName`: 线程名称
- `message`: 日志消息
- `throwable`: 异常信息（如果有）

### 自定义字段
- `hostname`: 主机名
- `application`: 应用名称
- `log_source`: 日志来源 (kafka)
- `index_prefix`: 索引前缀 (sprival-logs)

### MDC 字段
- `mdc_*`: MDC 上下文信息（如 mdc_userId, mdc_requestId）

## 故障排除

### 1. Kafka 连接问题
- 检查端口配置：应用使用 `localhost:8092`，Kafka 内部使用 `kafka:29092`
- 检查网络连通性：确保应用和 Kafka 在同一网络

### 2. Logstash 处理问题
- 检查 Logstash 日志：`docker logs sprival-logging-logstash-1`
- 检查管道配置：`docker exec sprival-logging-logstash-1 cat /usr/share/logstash/pipeline/logstash.conf`

### 3. Elasticsearch 索引问题
- 检查索引创建：`curl "http://localhost:9200/_cat/indices?v"`
- 检查映射：`curl "http://localhost:9200/sprival-logs-*/_mapping?pretty"`

### 4. 应用日志配置问题
- 检查配置：`curl http://localhost:8338/api/test/logging/config`
- 检查 logback 配置：`src/main/resources/logback-kafka.xml`

## 性能优化建议

### 1. Kafka 配置优化
- 调整批处理大小：`batch.size`
- 调整延迟时间：`linger.ms`
- 启用压缩：`compression.type`

### 2. Logstash 配置优化
- 调整消费者线程数：`consumer_threads`
- 调整批处理大小：`batch_size`

### 3. Elasticsearch 配置优化
- 调整刷新间隔：`refresh_interval`
- 调整分片数量：`number_of_shards`

## 监控指标

### 1. Kafka 监控
- 主题消息数量
- 消费者延迟
- 生产者吞吐量

### 2. Logstash 监控
- 处理事件数量
- 处理延迟
- 错误数量

### 3. Elasticsearch 监控
- 索引大小
- 查询性能
- 集群健康状态

## 扩展功能

### 1. 日志过滤
在 Logstash 中添加过滤器：
```ruby
filter {
  if [level] == "ERROR" {
    # 特殊处理错误日志
  }
}
```

### 2. 日志告警
使用 Elasticsearch Watcher 或 Kibana Alerting 设置日志告警。

### 3. 日志分析
在 Kibana 中创建可视化图表和仪表板。

## 总结

通过以上步骤，您应该能够成功实现：
- Spring Boot 应用日志通过 Kafka 发送
- Logstash 从 Kafka 消费日志并处理
- Elasticsearch 存储和索引日志
- Kibana 可视化和分析日志

如果遇到问题，请检查各个组件的日志输出，并根据错误信息进行相应的调整。
