## 开发环境信息

### 项目结构
`
sprival/
├── src/main/java/com/soyokra/sprival/
│   ├── SprivalApplication.java          # 主应用类
│   ├── config/                          # 配置类目录
│   │   ├── http/                        # HTTP客户端配置
│   │   ├── redis/                       # Redis配置
│   │   ├── mysql/                       # MySQL配置
│   │   ├── jetty/                       # Jetty配置
│   │   ├── kafka/                       # Kafka配置
│   │   ├── mongodb/                     # MongoDB配置
│   │   ├── rabbit/                      # RabbitMQ配置
│   │   ├── clickhouse/                  # ClickHouse配置
│   │   └── ratelimiter/                 # 限流器配置
│   ├── client/                          # Feign客户端
│   ├── service/                         # 业务服务
│   └── controller/                      # 控制器
├── src/main/resources/
│   ├── application.properties           # 应用配置
│   ├── redisson.yml                     # Redisson配置
│   ├── spy.properties                   # P6Spy配置
│   └── mapper/                          # MyBatis映射文件
├── dockers/                             # Docker配置
├── docs/                                # 项目文档
└── scripts/                             # 脚本文件
`

### 启动方式
1. **Maven启动**: mvn spring-boot:run
2. **脚本启动**: start-utf8.bat (Windows)
3. **Docker启动**: docker-compose up

### 监控端点
- **健康检查**: http://localhost:8338/api/actuator/health
- **应用信息**: http://localhost:8338/api/actuator/info
- **指标监控**: http://localhost:8338/api/actuator/metrics
- **Prometheus**: http://localhost:8338/api/actuator/prometheus

