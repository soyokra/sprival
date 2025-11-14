# Sprival 参考文档

## 概述

本目录包含 Sprival 项目的参考文档，提供功能特性的完整说明和使用指南，类似于 [Spring Boot Reference Documentation](https://docs.spring.io/spring-boot/index.html)。

参考文档面向研发人员，帮助开发者快速理解和使用 Sprival 框架的各项功能。



## 文档导航

### 🔧 [组件集成](components/README.md)
了解如何集成和使用各种技术组件：

- **HTTP 服务器**: [Jetty 服务器集成](components/http-server/jetty/README.md)
- **HTTP 客户端**: [HTTP 客户端配置](components/http-client/README.md)
- **数据库**: 
  - [MySQL 集成](components/mysql/README.md)
  - [MongoDB 集成](components/mongodb/README.md)
  - [ClickHouse 集成](components/clickhouse/README.md)
- **缓存**: [Redis/Redisson 集成](components/redis/README.md)
- **消息队列**:
  - [RabbitMQ 集成](components/rabbitmq/README.md)
  - [Kafka 集成](components/kafka/README.md)
- **搜索引擎**: [Elasticsearch 集成](components/elasticsearch/README.md)

### 📊 [监控集成](monitoring/README.md)
了解如何配置和使用监控系统：

- Prometheus 指标采集
- Grafana 可视化面板
- 自定义指标暴露
- 健康检查配置

### 📝 [日志集成](logging/)
了解日志系统的配置和使用：

- Logback 配置
- 日志输出格式
- 日志级别管理
- 日志文件策略

### 🚀 [部署指南](deployment/)
了解如何在不同环境中部署 Sprival 应用：

- Docker 容器化部署
- Kubernetes 集群部署
- 环境配置管理

### 📐 [项目规范](DIRECTORY-STRUCTURE.md)
了解 Sprival 项目的目录结构和编码规范。

## 快速开始

### 1. 项目初始化

```bash
# 克隆项目
git clone https://github.com/soyokra/sprival.git
cd sprival

# 启动 Docker 环境
cd dockers
docker-compose up -d

# 编译项目
mvn clean package

# 运行应用
java -jar target/sprival-*.jar
```

### 2. 配置组件

在 `application.properties` 中配置需要使用的组件：

```properties
# MySQL 配置
spring.datasource.url=jdbc:mysql://localhost:3306/sprival
spring.datasource.username=root
spring.datasource.password=root

# Redis 配置
spring.redis.host=localhost
spring.redis.port=6379

# Jetty 配置
server.port=8080
```

### 3. 编写业务代码

参考各组件的集成文档，快速开始业务开发。

## 文档结构

```
docs/reference/
├── README.md                       # 参考文档索引（本文档）
├── components/                     # 组件集成指南
│   ├── README.md                   # 组件集成总览
│   ├── http-server/                # HTTP 服务器组件
│   ├── http-client/                # HTTP 客户端组件
│   ├── mysql/                      # MySQL 数据库组件
│   ├── redis/                      # Redis 缓存组件
│   ├── clickhouse/                 # ClickHouse 分析数据库
│   ├── mongodb/                    # MongoDB 文档数据库
│   ├── rabbitmq/                   # RabbitMQ 消息队列
│   ├── kafka/                      # Kafka 消息队列
│   └── elasticsearch/              # Elasticsearch 搜索引擎
├── deployment/                     # 部署指南
├── logging/                        # 日志指南
├── monitoring/                     # 监控指南
└── DIRECTORY-STRUCTURE.md          # 目录结构规范
```

## 相关文档

- [API 文档](../api/README.md) - 代码级别的 API 说明文档
- [项目 README](../../README.md) - 项目总体介绍

## 文档原则

本参考文档遵循以下原则：

1. **技术导向**: 面向研发人员，避免营销化描述
2. **实用优先**: 重点说明如何配置和使用，而非理论介绍
3. **示例丰富**: 提供完整可运行的代码示例
4. **问题驱动**: 包含常见问题和解决方案
5. **持续更新**: 随代码变更及时更新文档

## 贡献指南

欢迎贡献文档改进：

1. 发现错误或缺失内容时提交 Issue
2. 提交 Pull Request 改进文档
3. 遵循项目的文档编写规范
4. 保持文档的技术导向性

---

*注: 参考文档持续更新中，如有疑问请参考 [API 文档](../api/README.md) 或提交 Issue。*

