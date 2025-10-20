# Sprival 项目文档

## 项目概述
Sprival是Spring Boot组件集成模板，提供一份全面的pom.xml组件引入，各个组件配置说明，监控指标，以及后续k8s部署方案。 
- 组件集成 
  - Http Server
  - Http Client
  - MySQL
  - Redis
  - RabbitMQ/Kafka
  - Mongodb
  - Clickhouse
  - ElasticSearch
- 日志 
  - KafkaAppender
  - ELK
- 监控
  - Spring Actuator Prometheus
  - Grafana
  - Prometheus
- 云原生部署
  - gitlab, gitlab-ci
  - docker, docker-hub
  - k8s

## 文档结构

### 🔧 系统环境
- [系统环境配置 (Linux)](SYSTEM-ENVIRONMENT.md) - **重要**: Linux开发环境配置信息，依赖版本兼容性说明
- [系统环境配置 (Windows)](SYSTEM-ENVIRONMENT-WINDOWS.md) - **重要**: Windows开发环境配置信息，依赖版本兼容性说明
- [编码规范](ENCODING-STANDARDS.md) - **重要**: 跨平台编码兼容性规范，避免中文乱码问题

### 🤖 AI辅助开发
- [AI辅助开发规范](ai-development/README.md) - AI辅助开发文档规范
- [上下文管理策略](ai-development/context-management.md) - 项目上下文管理
- [提示词模板库](ai-development/prompt-templates/) - 标准化提示词模板
- [开发工作流程](ai-development/workflow.md) - AI辅助开发流程

### 📝 文档规范
- [技术文档规范标准](components/DOCUMENTATION-STANDARDS.md) - 组件文档编写规范
- [文档模板](components/TEMPLATE.md) - 标准化文档模板
- [文档改造总结](components/DOCUMENTATION-REFACTOR-SUMMARY.md) - 文档规范化改造说明

## 目录规范
- [项目目录规范](PROJECT-STRUCTURE.md)

### 📦 组件文档
- [MySQL组件](components/mysql/README.md) - 数据库集成方案
- [Redis组件](components/redis/README.md) - 缓存集成方案  
- [HTTP Server组件](components/http-server/README.md) - Web服务器配置
- [HTTP Client组件](components/http-client/README.md) - HTTP客户端集成
- [Kafka组件](components/kafka/README.md) - 消息队列集成
- [RabbitMQ组件](components/rabbitmq/README.md) - 消息队列集成
- [MongoDB组件](components/mongodb/README.md) - 文档数据库集成
- [ClickHouse组件](components/clickhouse/README.md) - 分析数据库集成
- [Elasticsearch组件](components/elasticsearch/README.md) - 搜索引擎集成
- [监控组件](components/monitoring/README.md) - 全栈监控解决方案
- [性能测试组件](components/performance-testing/README.md) - 性能测试和压测方案

## 快速开始

1. **环境准备**: 请先阅读系统环境配置文档确保环境兼容性
   - [Linux环境配置](SYSTEM-ENVIRONMENT.md)
   - [Windows环境配置](SYSTEM-ENVIRONMENT-WINDOWS.md)
2. **项目构建**: `mvn clean compile`
3. **运行项目**: `mvn spring-boot:run`
4. **查看监控**: http://localhost:8338/api/actuator

### Linux环境快速启动
```bash
# 检查环境
java -version
mvn -version

# 构建并运行
mvn clean compile
mvn spring-boot:run

# 或者使用项目提供的启动脚本
./scripts/ai-dev-start.sh
```

### Windows环境快速启动
```powershell
# 1. 一键检测开发环境（推荐首次运行）
.\scripts\check-windows-environment.ps1

# 2. 检查环境
java -version
mvn -version

# 3. 构建并运行
mvn clean compile
mvn spring-boot:run

# 或者使用项目提供的启动脚本
.\scripts\ai-dev-start.ps1
```

> 💡 **提示**: 首次使用建议先运行环境检测脚本，它会自动检查Java、Maven、Docker等配置，并给出优化建议。

