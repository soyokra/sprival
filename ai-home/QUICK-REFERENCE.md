# Sprival 快速参考手册

> AI 开发时的快速查询手册

## 项目基本信息

- **项目**: Sprival (Spring Component Integration Framework)
- **版本**: 0.0.1
- **技术栈**: Spring Boot 2.7.18 + Java 8
- **主类**: `com.soyokra.sprival.SprivalApplication`
- **端口**: 8338
- **基础路径**: `/api`

## 目录快速索引

### 关键文件
| 文件 | 路径 |
|------|------|
| 主配置 | `src/main/resources/application.properties` ⭐ |
| 日志配置 | `src/main/resources/logback-kafka.xml` ⭐ |
| Maven POM | `pom.xml` ⭐ |
| Docker Compose | `docker/sprival-middleware/docker-compose.yml` ⭐ |

### 源代码结构
```
src/main/java/com/soyokra/sprival/
├── SprivalApplication.java    # 主应用类
├── config/                    # 配置类（按组件分包）
├── app/                       # 业务代码
│   ├── http/                  # HTTP 层
│   ├── service/               # 业务服务
│   ├── repository/db/         # 数据访问
│   ├── model/                 # 业务模型
│   └── util/                  # 工具类
├── support/logging/           # 日志支持
└── database/sql/              # SQL 脚本
```

### 文档索引
```
docs/
├── api/                           # API 文档
└── reference/                     # 参考文档
    ├── components/                # 组件集成指南
    ├── logging/README.md          # 日志系统 ⭐
    ├── monitoring/README.md       # 监控系统 ⭐
    └── DIRECTORY-STRUCTURE.md     # 目录结构规范 ⭐
```

## 组件连接信息

### 数据库
| 组件 | 端口 | 用户名 | 密码 | 数据库 |
|------|------|--------|------|--------|
| MySQL | 3336 | root | workdock | sprival |
| MongoDB | 27017 | admin | workdock | sprival |
| ClickHouse | 8123 | default | - | sprival |

### 缓存
| 组件 | 端口 | 密码 | 数据库 |
|------|------|------|--------|
| Redis | 6379 | workdock | 0 |

### 消息队列
| 组件 | 端口 | 用户名 | 密码 |
|------|------|--------|------|
| RabbitMQ | 5672 | guest | guest |
| Kafka | 9092 | - | - |

### 搜索引擎
| 组件 | 端口 | 用户名 | 密码 |
|------|------|--------|------|
| Elasticsearch | 9201 | - | - |

### ELK 和监控
| 组件 | 端口 | 访问地址 |
|------|------|---------|
| Kibana | 5601 | http://localhost:5601 |
| Prometheus | 9090 | http://localhost:9090 |
| Grafana | 3001 | http://localhost:3001 |

## 应用端点

| 端点 | URL | 说明 |
|------|-----|------|
| 健康检查 | `http://localhost:8338/api/actuator/health` | 应用健康状态 |
| 存活探针 | `http://localhost:8338/api/actuator/health/liveness` | 容器存活检查 |
| 就绪探针 | `http://localhost:8338/api/actuator/health/readiness` | 容器就绪检查 |
| Prometheus | `http://localhost:8338/api/actuator/prometheus` | 指标数据 |

## 常用命令

### Maven
```bash
# 编译
mvn clean compile

# 打包（跳过测试）
mvn clean package -DskipTests

# 运行测试
mvn test
```

### Docker
```bash
# 启动所有服务
cd docker/sprival-middleware && docker-compose up -d

# 停止所有服务
cd docker/sprival-middleware && docker-compose down

# 查看服务状态
cd docker/sprival-middleware && docker-compose ps
```

### 应用运行
```bash
# 默认环境运行
java -jar target/sprival-0.0.1.jar

# 指定环境运行
java -jar target/sprival-0.0.1.jar --spring.profiles.active=test
```

## 核心配置快查

### 日志输出目标
```properties
# 可选值: file, kafka, both
sprival.logging.application.output-target = kafka
sprival.logging.jetty-access.output-target = kafka
```

### 健康检查分组
```properties
# 存活探针: 容器DOWN时重启
management.endpoint.health.group.liveness.include = livenessState, diskSpace

# 就绪探针: 容器DOWN时不接受流量
management.endpoint.health.group.readiness.include = db

# 告警探针: 容器DOWN时告警
management.endpoint.health.group.alertness.include = *
```

## 技术栈版本

| 技术 | 版本 |
|------|------|
| Spring Boot | 2.7.18 |
| Spring Cloud | 2021.0.8 |
| Java | 1.8 |
| MyBatis Plus | 3.5.7 |
| Redisson | 3.23.4 |
| Resilience4j | 1.7.1 |

---

**最后更新**: 2025-10-22
