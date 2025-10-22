# Sprival 技术栈概览

> 快速了解项目的技术架构和组件集成

## 项目信息

- **项目名称**: Sprival (Spring Component Integration Framework)
- **当前版本**: 0.0.1
- **核心技术**: Spring Boot 2.7.18 + Java 8
- **构建工具**: Maven
- **主包名**: com.soyokra.sprival

## 技术架构

### 核心框架
- **Spring Boot**: 2.7.18
- **Spring Cloud**: 2021.0.8
- **Java**: 1.8
- **Maven**: 构建管理

### HTTP 层

#### HTTP Server - Jetty
- 端口: 8338
- 基础路径: `/api`
- 特性: 访问日志、GZIP 压缩、优雅关闭、安全头

#### HTTP Client - OpenFeign
- 客户端: OpenFeign + OkHttp
- 弹性机制: Resilience4j（断路器、重试）
- 负载均衡: Spring Cloud LoadBalancer
- 监控: Micrometer 集成

### 数据存储

#### MySQL
- ORM: MyBatis Plus 3.5.7
- 连接池: HikariCP
- 特性: 动态数据源、SQL 日志（P6Spy）、代码生成器

#### MongoDB
- 驱动: Spring Data MongoDB
- 特性: 自动索引、连接池管理

#### ClickHouse
- 驱动: ClickHouse JDBC 0.3.2
- 用途: 数据分析和大数据查询

#### Elasticsearch
- 驱动: Spring Data Elasticsearch
- 端口: 9201
- 用途: 全文搜索、日志存储

### 缓存层

#### Redis
- 客户端: Lettuce（连接池）
- 高级功能: Redisson 3.23.4
- 特性: Spring Cache、分布式锁、对象存储

### 消息队列

#### RabbitMQ
- 端口: 5672
- 特性: 发布确认、手动 ACK、重试机制

#### Kafka
- 端口: 9092
- 用途: 日志传输、业务消息
- 特性: 事务支持、幂等性、手动提交

## 核心功能

### 日志系统
- **框架**: Logback + SLF4J
- **扩展**: 自定义 KafkaAppender
- **日志类型**: 应用日志、访问日志
- **输出目标**: file / kafka / both
- **ELK 集成**: Logstash → Elasticsearch → Kibana

### 监控系统
- **组件**: Actuator + Micrometer + Prometheus + Grafana
- **健康检查**:
  - liveness（存活探针）: diskSpace, livenessState
  - readiness（就绪探针）: db
  - alertness（告警探针）: 所有组件
- **监控端点**: `/api/actuator/*`

### 限流功能
- **实现**: Redisson RateLimiter（令牌桶算法）
- **特性**: 基于 Redis 的分布式限流

## 代码组织

```
com.soyokra.sprival/
├── SprivalApplication          # 主应用类
├── config/                     # 配置类（按组件分包）
│   ├── clickhouse/
│   ├── jetty/
│   ├── kafka/
│   └── mysql/
├── app/                        # 业务代码
│   ├── http/                   # HTTP 层（controller、request、response、middleware）
│   ├── service/                # 业务服务层
│   ├── repository/db/          # 数据访问层（按数据库分包）
│   ├── model/                  # 业务模型
│   └── util/                   # 工具类
├── support/                    # 支持类
│   └── logging/                # 日志支持（Kafka Appender、RequestLog）
└── database/                   # 数据库相关
    ├── SprivalShopGenerator    # 代码生成器
    └── sql/                    # SQL 脚本
```

## 资源文件

```
src/main/resources/
├── application.properties      # ⭐ 主配置文件（所有组件配置）
├── logback-kafka.xml          # ⭐ 日志配置
├── config/spy.properties       # P6Spy SQL 日志配置
└── mapper/shop/               # MyBatis XML 映射
```

## 测试体系

### 单元测试
- 框架: JUnit 5 + Mockito
- 覆盖率: JaCoCo
- 断言: AssertJ

### 集成测试
- 框架: Spring Boot Test + Testcontainers
- 工具: RestAssured
- 容器: MySQL, Elasticsearch

### 性能测试
- 框架: JMH（Java Microbenchmark Harness）
- 负载测试: 自定义测试工具

## Docker 环境

### 中间件
```
docker/sprival-middleware/docker-compose.yml
├── MySQL
├── Redis
├── MongoDB
├── ClickHouse
├── RabbitMQ
├── Kafka
└── Elasticsearch
```

### 日志系统
```
docker/sprival-logging/docker-compose.yml
├── Elasticsearch
├── Logstash
└── Kibana
```

### 监控系统
```
docker/sprival-monitoring/docker-compose.yml
├── Prometheus
└── Grafana
```

## 配置管理

### 配置原则
- 所有配置写在 `application.properties`（不加载外部配置文件）
- 使用扁平化的 Redis/Redisson 配置格式
- 按组件分组组织配置

### 环境配置
- **dev**: 开发环境（默认）
- **test**: 测试环境
- **prod**: 生产环境
- **benchmark**: 性能测试环境

## 文档结构

```
docs/
├── api/                        # API 文档（JavaDoc 风格）
└── reference/                  # 参考文档
    ├── components/             # 组件集成指南（MySQL、Redis、Kafka 等）
    ├── logging/                # 日志系统完整指南 ⭐
    ├── monitoring/             # 监控系统完整指南 ⭐
    ├── deployment/             # 部署指南
    └── DIRECTORY-STRUCTURE.md  # 目录结构规范 ⭐
```

## 依赖版本

### Spring 生态
- spring-boot: 2.7.18
- spring-cloud: 2021.0.8

### 数据访问
- mybatis-plus: 3.5.7
- dynamic-datasource: 4.3.1
- mysql-connector: 8.0.33
- clickhouse-jdbc: 0.3.2-patch11

### 缓存
- redisson: 3.23.4

### HTTP 客户端
- resilience4j: 1.7.1

### 测试
- testcontainers: 1.17.6
- jmh: 1.35

---

**最后更新**: 2025-10-22  
**详细文档**: 参见 `docs/reference/` 目录

