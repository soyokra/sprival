# Kafka 日志测试脚本使用说明

## 概述

`test_kafka_logging.py` 是一个用于测试 Kafka 日志系统的 Python 脚本，可以模拟 Spring Boot 应用向 Kafka 发送各种类型的日志消息。

## 功能特性

- ✅ 连接到 localhost:9092 的 Kafka 服务
- ✅ 向 `application-logs` topic 发送日志消息
- ✅ 支持多种日志类型：基本日志、错误日志、MDC 日志、结构化日志
- ✅ 性能测试和批量发送
- ✅ 详细的测试报告和统计信息
- ✅ 错误处理和重试机制

## 安装依赖

```bash
pip install kafka-python requests
```

## 使用方法

### 1. 启动 Kafka 服务

```bash
cd docker/sprival-logging
docker-compose up -d
```

### 2. 运行测试脚本

```bash
cd ai-home
python test_kafka_logging.py
```

### 3. 查看测试结果

脚本会输出详细的测试报告，包括：
- 各种类型日志的发送结果
- 性能统计信息
- 成功率和吞吐量
- 访问信息

## 测试类型

### 基本日志测试
- 发送 5 条 INFO 级别的普通日志消息
- 包含时间戳、线程名等基本信息

### 错误日志测试
- 发送 3 条 ERROR 级别的错误日志
- 包含异常堆栈信息
- 模拟各种系统错误场景

### MDC 日志测试
- 发送 3 条包含 MDC 上下文的日志
- 包含用户ID、请求ID、会话ID等信息
- 模拟用户操作日志

### 结构化日志测试
- 发送 3 条结构化日志消息
- 包含操作类型、持续时间、资源路径等
- 模拟业务操作日志

### 性能测试
- 发送 50 条消息进行性能测试
- 统计吞吐量和成功率
- 显示详细的性能指标

## 日志消息格式

脚本发送的日志消息采用与 Spring Boot 应用相同的格式：

```json
{
  "timestamp": "2025-01-22T10:30:00.000Z",
  "level": "INFO",
  "loggerName": "com.soyokra.sprival.test",
  "threadName": "thread-1234",
  "message": "测试日志消息",
  "hostname": "test-host",
  "application": "sprival-test",
  "log_source": "kafka",
  "index_prefix": "sprival-logs",
  "throwable": null,
  "mdc": {
    "userId": "user-1234",
    "requestId": "req-1234567890"
  }
}
```

## 验证结果

### 1. 检查 Kafka 主题

```bash
docker exec sprival-logging-kafka-1 /opt/kafka/bin/kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --topic application-logs \
  --from-beginning
```

### 2. 检查 Elasticsearch 索引

```bash
curl "http://localhost:9200/sprival-logs-*/_search?pretty"
```

### 3. 在 Kibana 中查看

1. 访问 http://localhost:5601
2. 创建索引模式：`sprival-logs-*`
3. 在 Discover 中查看日志数据

## 故障排除

### 连接问题
- 确保 Kafka 服务正在运行
- 检查端口 9092 是否可访问
- 验证 Docker 容器状态

### 消息发送失败
- 检查网络连接
- 验证 topic 是否存在
- 查看 Kafka 日志

### 数据未出现在 Elasticsearch
- 检查 Logstash 配置
- 验证 Elasticsearch 连接
- 查看 Logstash 日志

## 自定义配置

可以通过修改脚本中的参数来自定义测试：

```python
# 修改 Kafka 地址
tester = KafkaLogTester(bootstrap_servers="localhost:9092", topic="application-logs")

# 修改测试数量
tester.test_basic_logs(count=10)
tester.test_performance(count=100)
```

## 扩展功能

脚本支持以下扩展：
- 添加新的日志类型
- 自定义消息格式
- 集成其他监控工具
- 添加告警功能

## 注意事项

1. 确保有足够的系统资源运行 Kafka 和 ELK 服务
2. 测试过程中会产生大量日志数据，注意磁盘空间
3. 生产环境中请调整性能参数
4. 定期清理测试数据

---

**创建时间**: 2025-01-22  
**作者**: AI Assistant  
**版本**: 1.0.0
