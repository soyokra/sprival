# Sprival

Sprival 是一个 Spring Boot 集成框架，提供标准项目构建配置、组件集成、中间件集成、ELK 日志集成、监控集成和云原生部署方案。

## 核心特性

- **组件集成**: HTTP Server (Jetty)、HTTP Client (Feign + Resilience4j)
- **中间件集成**: MySQL、Redis、RabbitMQ、Kafka、ClickHouse、MongoDB、Elasticsearch
- **ELK 日志**: Kafka Appender + ELK 集成
- **监控集成**: Prometheus + Grafana
- **测试工具**: API 测试框架
- **云原生**: Docker、GitLab CI/CD、Kubernetes

## 技术栈

Spring Boot 2.7、Java 8、MyBatis Plus、Spring Cloud OpenFeign、Redisson

## 快速开始

```bash
git clone https://github.com/soyokra/sprival.git
cd sprival && mvn clean package
java -jar target/sprival-*.jar
```

详细步骤请参考 [快速开始指南](docs/reference/README.md#快速开始)。

## 文档导航

- **[参考文档](docs/reference/README.md)**: 包含详细的使用指南和快速开始步骤
- **[API 文档](docs/api/README.md)**: 代码级别的 API 说明文档
- **[组件集成指南](docs/reference/components/)**: 各组件的详细集成文档
- **[部署指南](docs/reference/deployment/)**: Docker 和 Kubernetes 部署方案

## 项目结构

```
sprival/
├── src/                    # 源代码
├── docs/                   # 文档目录
│   ├── api/               # API 文档
│   └── reference/         # 参考文档
├── docker/                # Docker 配置
└── scripts/               # 脚本工具
```

更多详情请参考 [目录结构规范](docs/reference/DIRECTORY-STRUCTURE.md)。
