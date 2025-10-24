# Sprival 文件索引

> 用于快速定位项目中的关键文件

## 核心文件

### 主配置
| 文件 | 路径 |
|------|------|
| 主配置文件 | `src/main/resources/application.properties` ⭐ |
| 日志配置 | `src/main/resources/logback-kafka.xml` ⭐ |
| Maven POM | `pom.xml` ⭐ |
| Docker Compose | `docker/sprival-middleware/docker-compose.yml` ⭐ |

### 主应用
| 文件 | 路径 |
|------|------|
| 启动类 | `src/main/java/com/soyokra/sprival/SprivalApplication.java` |

## 源代码结构

### 配置类 (config/)
```
src/main/java/com/soyokra/sprival/config/
├── jetty/              # Jetty 容器配置
├── mysql/              # MyBatis Plus 配置
├── clickhouse/         # ClickHouse 数据源配置
├── kafka/              # Kafka 生产者/消费者配置
└── ...                 # 其他组件配置
```

### 业务代码 (app/)
```
src/main/java/com/soyokra/sprival/app/
├── http/
│   ├── controller/     # REST 控制器
│   ├── request/        # 请求对象
│   ├── response/       # 响应对象
│   └── middleware/     # 拦截器、限流器
├── service/            # 业务服务层
├── repository/db/shop/ # 数据访问层
├── model/              # 业务模型
└── util/               # 工具类
```

### 支持类 (support/)
```
src/main/java/com/soyokra/sprival/support/
└── logging/            # 日志支持（Kafka Appender、Jetty RequestLog）
```

### 数据库相关 (database/)
```
src/main/java/com/soyokra/sprival/database/
├── SprivalShopGenerator.java  # MyBatis Plus 代码生成器
└── sql/                        # 数据库脚本（建表、初始化）
```

## 文档索引

### 项目文档
| 文件 | 路径 | 说明 |
|------|------|------|
| 项目 README | `README.md` | 项目总体介绍 |
| 目录结构规范 | `docs/reference/DIRECTORY-STRUCTURE.md` | 项目结构规范 ⭐ |
| 参考文档索引 | `docs/reference/README.md` | 参考文档入口 |

### 组件文档
| 组件 | 路径 |
|------|------|
| MySQL | `docs/reference/components/mysql/README.md` |
| Redis | `docs/reference/components/redis/README.md` |
| MongoDB | `docs/reference/components/mongodb/README.md` |
| ClickHouse | `docs/reference/components/clickhouse/README.md` |
| Kafka | `docs/reference/components/kafka/README.md` |
| RabbitMQ | `docs/reference/components/rabbitmq/README.md` |
| Elasticsearch | `docs/reference/components/elasticsearch/README.md` |
| HTTP Server (Jetty) | `docs/reference/components/http-server/jetty/README.md` |
| HTTP Client (Feign) | `docs/reference/components/http-client/README.md` |

### 功能文档
| 功能 | 路径 | 说明 |
|------|------|------|
| 日志系统 | `docs/reference/logging/README.md` | 日志功能完整指南 ⭐ |
| ELK-Kafka 集成 | `docs/reference/logging/elk-kafka-integration.md` | ELK-Kafka 集成方案 |
| 监控系统 | `docs/reference/monitoring/README.md` | 监控功能完整指南 ⭐ |

### API 文档
| 文件 | 路径 |
|------|------|
| API 文档索引 | `docs/api/README.md` |

## Docker 配置

### 中间件
| 文件 | 路径 |
|------|------|
| Docker Compose | `docker/sprival-middleware/docker-compose.yml` ⭐ |
| 各组件 Dockerfile | `docker/sprival-middleware/{mysql,redis,mongodb,...}/` |

### 日志系统
| 文件 | 路径 |
|------|------|
| ELK Docker Compose | `docker/sprival-logging/docker-compose.yml` ⭐ |
| Logstash Pipeline | `docker/sprival-logging/logstash/pipeline/logstash.conf` |
| ELK 文档 | `docker/sprival-logging/README.md` |

### 监控系统
| 文件 | 路径 |
|------|------|
| 监控 Docker Compose | `docker/sprival-monitoring/docker-compose.yml` ⭐ |
| Prometheus 配置 | `docker/sprival-monitoring/prometheus/prometheus.yml` |

## AI 工作区 (ai-home/)

| 文件 | 说明 |
|------|------|
| `INDEX.md` | AI 工作区总索引 ⭐ |
| `FILE-INDEX.md` | 文件快速定位（本文档）⭐ |
| `TECH-STACK.md` | 技术栈概览 ⭐ |
| `QUICK-REFERENCE.md` | 快速参考手册 ⭐ |
| `DEVELOPMENT-CHECKLIST.md` | 开发检查清单 |
| `SYSTEM-ENVIRONMENT.md` | 系统环境配置 |
| `ENCODING-STANDARDS.md` | 编码标准 |
| `test_kafka_logging.py` | Kafka 日志测试脚本 ⭐ |
| `quick_kafka_test.py` | 快速 Kafka 测试脚本 |
| `README-KAFKA-TEST.md` | Kafka 测试使用说明 |

## 使用说明

### 快速定位
- **查找配置**: 查看"核心文件"部分
- **查找代码**: 查看"源代码结构"部分
- **查找文档**: 查看"文档索引"部分
- **查找 Docker**: 查看"Docker 配置"部分

### 标记说明
- ⭐ 重要文件，建议优先查看

### 路径规则
- 所有路径相对于项目根目录
- 使用正斜杠 `/` 作为分隔符

---

**最后更新**: 2025-10-22
