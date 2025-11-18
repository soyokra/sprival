# ELK + Kafka日志集成

## 概述

本文档介绍如何使用 ELK（Elasticsearch, Logstash, Kibana）+ Kafka 构建完整的日志收集、处理和可视化系统。

## 架构

### 整体架构

```
┌─────────────────────┐
│  Spring Boot App    │
│  (KafkaAppender)    │
└──────────┬──────────┘
           │ 异步发送 JSON 日志
           ↓
┌─────────────────────┐
│      Kafka          │
│  - application-logs │
│  - access-logs      │
└──────────┬──────────┘
           │ Logstash 消费
           ↓
┌─────────────────────┐
│     Logstash        │
│  - 解析 JSON        │
│  - 添加字段         │
│  - 数据转换         │
└──────────┬──────────┘
           │ 写入索引
           ↓
┌─────────────────────┐
│  Elasticsearch      │
│  - application-logs-*│
│  - access-logs-*    │
└──────────┬──────────┘
           │ 查询和聚合
           ↓
┌─────────────────────┐
│      Kibana         │
│  - Discover         │
│  - Dashboard        │
│  - Visualize        │
└─────────────────────┘
```

### 数据流

1. **日志生成**: Spring Boot 应用使用 KafkaAppender 发送日志
2. **消息队列**: Kafka 作为缓冲，提供削峰填谷能力
3. **日志处理**: Logstash 消费、解析和转换日志
4. **日志存储**: Elasticsearch 存储和索引日志数据
5. **日志查询**: Kibana 提供可视化查询界面
