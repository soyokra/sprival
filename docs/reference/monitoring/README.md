# 监控

> 基于 Prometheus + Grafana 构建的监控系统，使用 Python 进行初始化(setup)，方便维护和升级。

## 快速开始

启动monitoring服务

```
cd docker/sprival-monitoring
docker compose up setup
docker compose up -d
```

访问服务：
- Grafana：http://localhost:3000/
- Prometheus：http://localhost:9090/

默认登录信息：
- Grafana 用户名：`admin`
- Grafana 密码：`workdock`


## 整体架构

```
┌─────────────────────┐
│  Spring Boot App    │
│  (Actuator)         │
│  /api/actuator/     │
│  prometheus         │
└──────────┬──────────┘
           │ 暴露指标端点
           ↓
┌─────────────────────┐
│    Prometheus       │
│  - 抓取指标         │
│  - 时序存储         │
│  - 查询引擎         │
└──────────┬──────────┘
           │ 查询数据
           ↓
┌─────────────────────┐
│      Grafana        │
│  - Dashboard        │
│  - Alerting         │
│  - Explore          │
└─────────────────────┘
           ↑
           │ Setup 初始化
           │
┌─────────────────────┐
│   Setup Service     │
│  - 添加数据源        │
│  - 导入 Dashboard   │
└─────────────────────┘
```

## 数据流

1. **指标暴露**: Spring Boot 应用通过 Actuator 暴露 Prometheus 格式的指标
2. **指标抓取**: Prometheus 定期抓取应用指标并存储到时序数据库
3. **数据查询**: Grafana 从 Prometheus 查询数据
4. **可视化展示**: Grafana 通过 Dashboard 提供丰富的监控可视化

## 预配置 Dashboard

系统已预配置以下监控面板：

- **JVM Dashboard**: JVM 内存、GC、线程等监控
- **Feign Dashboard**: Feign 客户端调用监控
- **Jetty Dashboard**: Jetty 服务器连接和请求监控
- **HikariCP Dashboard**: 数据库连接池监控
- **Lettuce Dashboard**: Redis 客户端监控
- **RabbitMQ Dashboard**: RabbitMQ 消息队列监控
