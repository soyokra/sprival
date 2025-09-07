# Sprival 项目文档

## 项目概述
Sprival是Spring Boot组件集成模板，提供一份全面的pom.xml组件引入，各个组件配置使用说明，监控指标方案，以及后续k8s部署方案。

## 文档结构

### 🔧 系统环境
- [系统环境配置](SYSTEM-ENVIRONMENT.md) - **重要**: 开发环境配置信息，依赖版本兼容性说明
- [编码规范](ENCODING-STANDARDS.md) - **重要**: 跨平台编码兼容性规范，避免中文乱码问题

### 🤖 AI辅助开发
- [AI辅助开发规范](ai-development/README.md) - AI辅助开发文档规范
- [上下文管理策略](ai-development/context-management.md) - 项目上下文管理
- [提示词模板库](ai-development/prompt-templates/) - 标准化提示词模板
- [开发工作流程](ai-development/workflow.md) - AI辅助开发流程

### 📦 组件文档
- [Spring MySQL](spring-mysql/README.md) - 数据库集成方案
- [Spring Redis](spring-redis/README.md) - 缓存集成方案  
- [Spring HTTP Server](spring-http-server/README.md) - Web服务器配置
- [Spring HTTP Client](spring-http-client/README.md) - HTTP客户端集成
- [Spring Kafka](spring-kafka/README.md) - 消息队列集成
- [Spring RabbitMQ](spring-rabbit/README.md) - 消息队列集成
- [Spring MongoDB](spring-mongo/README.md) - 文档数据库集成
- [Spring ClickHouse](spring-clickhouse/README.md) - 分析数据库集成
- [Spring Elasticsearch](spring-elasticsearch/README.md) - 搜索引擎集成

### 📊 监控运维
- [Spring监控模块](spring-monitoring/README.md) - 全栈监控解决方案

## 快速开始

1. **环境准备**: 请先阅读 [系统环境配置](SYSTEM-ENVIRONMENT.md) 确保环境兼容性
2. **项目构建**: `mvn clean compile`
3. **运行项目**: `mvn spring-boot:run`
4. **查看监控**: http://localhost:8338/api/actuator

