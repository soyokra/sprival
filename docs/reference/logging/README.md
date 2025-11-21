# 日志

> 基于[docker-elk](https://github.com/deviantony/docker-elk)，改造成使用python进行初始化(setup)，方便维护和升级，并且集成了kafka服务。

## 快速开始

启动logging服务

```
cd sprival-logging
docker compose up setup
docker compose up -d
```


访问服务：
- Kibana：http://localhost:5601/
- ElasticSearch：http://localhost:9200/

默认登录信息：
- Kibana 用户名：`elastic`
- Kibana 密码：`workdock`

## 整体架构

```
┌─────────────────────┐
│  Spring Boot App    │
│  (KafkaAppender)    │
└──────────┬──────────┘
           │ 异步发送 JSON 日志
           ↓
┌─────────────────────┐
│      Kafka          │
└──────────┬──────────┘
           │ Logstash 消费
           ↓
┌─────────────────────┐
│     Logstash        │
│  - 解析 JSON        │
│  - 添加字段          │
│  - 数据转换          │
└──────────┬──────────┘
           │ 写入索引
           ↓
┌─────────────────────┐
│  Elasticsearch      │
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

## 数据流

1. **日志生成**: Spring Boot 应用使用 KafkaAppender 发送日志
2. **消息队列**: Kafka 作为缓冲，提供削峰填谷能力
3. **日志处理**: Logstash 消费、解析和转换日志
4. **日志存储**: Elasticsearch 存储和索引日志数据
5. **日志查询**: Kibana 提供可视化查询界面
