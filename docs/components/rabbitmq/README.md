# RabbitMQ 模块

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

- [spring-boot-starter-amqp](https://spring.io/projects/spring-amqp) - Spring AMQP 集成
- [spring-rabbit](https://github.com/spring-projects/spring-amqp) - RabbitMQ 客户端实现
- [rabbitmq-java-client](https://github.com/rabbitmq/rabbitmq-java-client) - RabbitMQ Java 客户端

## 配置说明
```properties
spring.rabbitmq.host = localhost
spring.rabbitmq.port = 5672
spring.rabbitmq.username = guest
spring.rabbitmq.password = guest
spring.rabbitmq.virtual-host = /
# RabbitMQ 连接池配置
spring.rabbitmq.cache.connection.mode = connection
spring.rabbitmq.cache.connection.size = 10
spring.rabbitmq.cache.channel.size = 25
spring.rabbitmq.cache.channel.checkout-timeout = 30000
spring.rabbitmq.connection-timeout = 15000
# RabbitMQ 消息确认配置
spring.rabbitmq.publisher-confirm-type = correlated
spring.rabbitmq.publisher-returns = true
spring.rabbitmq.listener.simple.acknowledge-mode = manual
# RabbitMQ 重试配置
spring.rabbitmq.listener.simple.retry.enabled = true
spring.rabbitmq.listener.simple.retry.max-attempts = 3
spring.rabbitmq.listener.simple.retry.initial-interval = 1000
spring.rabbitmq.listener.simple.retry.multiplier = 2
spring.rabbitmq.listener.simple.retry.max-interval = 10000
```

## 监控指标

|指标名称|类型|描述|监控建议|
|-------|---|------|------|
|rabbitmq_acknowledged_published_total|Counter|已确认发布的消息总次数，表示成功发送到RabbitMQ经纪人的消息数量（使用publisher confirms）。这是一个累积计数器，可用于计算发布确认率（acknowledged / published）。|监控发布可靠性：查询 rate(rabbitmq_acknowledged_published_total[5m]) / rate(rabbitmq_published_total[5m]) 计算 5 分钟确认率。如果确认率 < 99%，检查网络或经纪人配置。|
|rabbitmq_acknowledged_total|Counter|消费者确认的消息总次数，表示成功处理的消费消息数量（使用consumer acks）。这是一个累积计数器，可用于计算消费确认率（acknowledged / consumed）。|监控消费可靠性：查询 rate(rabbitmq_acknowledged_total[5m]) / rate(rabbitmq_consumed_total[5m]) 计算 5 分钟确认率。如果确认率 < 95%，调查消费者延迟或错误处理。|
|rabbitmq_channels|Gauge|当前活动的RabbitMQ通道数，每个通道代表一个逻辑连接，用于发布或消费。|监控资源使用：如果 channels > 预期阈值（如 1000），优化通道复用或检查连接泄漏。高值可能导致性能下降。|
|rabbitmq_connections|Gauge|当前活动的RabbitMQ连接数，每个连接可支持多个通道。|监控连接健康：如果 connections 突然增加，检查客户端重连逻辑。保持在合理范围内（如 < 500）以避免资源耗尽。|
|rabbitmq_consumed_total|Counter|从RabbitMQ消费的消息总次数，包括所有接收到的消息。|跟踪消费负载：高 consumed 率表示高流量，结合 acknowledged 分析处理效率。如果 consumed 率 > 处理能力，考虑水平扩展消费者。|
|rabbitmq_failed_to_publish_total|Counter|发布消息失败的总次数，通常由于经纪人不可用或配置错误。|诊断发布问题：监控失败率 rate(rabbitmq_failed_to_publish_total[5m]) / rate(rabbitmq_published_total[5m])。如果 > 1%，实施重试机制或警报经纪人状态。|
|rabbitmq_not_acknowledged_published_total|Counter|未确认发布的消息总次数，表示可能丢失或延迟确认的消息。|监控发布确认：如果 not_acknowledged 持续增加，检查 publisher confirms 配置。目标：保持为 0 或很低。|
|rabbitmq_published_total|Counter|向RabbitMQ发布的消息总次数，包括所有尝试发送的消息。|跟踪发布流量：使用 rate(rabbitmq_published_total[5m]) 监控发布速率。高峰期结合 failed 分析瓶颈。|
|rabbitmq_rejected_total|Counter|被消费者拒绝的消息总次数，通常由于负确认（nack）或异常。|监控拒绝率：查询 rate(rabbitmq_rejected_total[5m]) / rate(rabbitmq_consumed_total[5m])。如果 > 5%，优化消费者逻辑以减少无效处理。|
|rabbitmq_unrouted_published_total|Counter|无法路由到队列的发布消息总次数，通常由于交换器/路由键问题。|诊断路由问题：如果 unrouted 率 rate(rabbitmq_unrouted_published_total[5m]) / rate(rabbitmq_published_total[5m]) > 0.1%，验证交换器绑定和路由键配置。|



