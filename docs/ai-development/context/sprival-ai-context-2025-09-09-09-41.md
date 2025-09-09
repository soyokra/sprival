# Sprival项目AI编程上下文

**生成时间**: 2025-09-09 09:41:49  
**版本**: 0.0.1  
**用途**: AI编程前项目现状了解

## 🎯 项目概述

Sprival是一个Spring Boot组件集成模板项目，提供了完整的微服务开发基础设施，包括数据访问、缓存、消息队列、HTTP客户端、监控等组件。

## 📊 项目状态总览

### 技术栈
- **框架**: Spring Boot 2.7.18 + Java 8
- **云原生**: Spring Cloud 2021.0.8
- **构建**: Maven
- **服务器**: Jetty
- **编码**: UTF-8

### 组件完成度
- ✅ HTTP Server (Jetty + 限流)
- ✅ MySQL (MyBatis-Plus + 多数据源)
- ✅ Redis (Spring Cache + Redisson)
- ✅ ClickHouse (分析数据库)
- ✅ MongoDB (文档数据库)
- ✅ RabbitMQ (消息队列)
- ✅ Kafka (消息队列)
- ✅ HTTP Client (Feign + 容错)

### 架构特点
- **组件化**: 每个组件独立配置和健康检查
- **监控友好**: 集成Actuator + Micrometer + Prometheus
- **容器化**: 完整的Docker支持
- **跨平台**: UTF-8编码支持

## 🚀 快速开始

### 启动项目
`ash
# 方式1: Maven启动
mvn spring-boot:run

# 方式2: 脚本启动 (Windows)
start-utf8.bat

# 方式3: Docker启动
docker-compose up
`

### 访问端点
- **应用**: http://localhost:8338/api
- **健康检查**: http://localhost:8338/api/actuator/health
- **监控指标**: http://localhost:8338/api/actuator/metrics

## 📁 项目结构

`
sprival/
├── src/main/java/com/soyokra/sprival/
│   ├── SprivalApplication.java          # 主应用类
│   ├── config/                          # 配置类 (8个组件)
│   ├── client/                          # Feign客户端
│   ├── service/                         # 业务服务
│   └── controller/                      # 控制器
├── src/main/resources/
│   ├── application.properties           # 应用配置
│   ├── redisson.yml                     # Redisson配置
│   └── spy.properties                   # P6Spy配置
├── dockers/                             # Docker配置
├── docs/                                # 项目文档
└── scripts/                             # 脚本文件
`

## 🔧 开发规范

### 命名规范
- **配置类**: Sprival + 组件名 + Configuration
- **属性类**: Sprival + 组件名 + Properties
- **健康检查**: Sprival + 组件名 + HealthIndicator

### 配置模式
- **配置类**: @Configuration + @Bean
- **属性绑定**: @ConfigurationProperties
- **条件配置**: @ConditionalOnClass/@ConditionalOnBean

### 健康检查
- 所有组件都实现HealthIndicator
- 统一的异常处理和降级策略
- 集成到Spring Boot Actuator

## 📚 文档资源

- **项目文档**: docs/README.md
- **AI开发规范**: docs/ai-development/
- **组件文档**: docs/spring-*/
- **系统环境**: docs/SYSTEM-ENVIRONMENT.md
- **编码规范**: docs/ENCODING-STANDARDS.md

## ⚠️ 注意事项

1. **版本兼容**: 确保依赖版本与Spring Boot 2.7.18兼容
2. **编码问题**: 所有文件使用UTF-8编码
3. **配置冲突**: 注意组件间配置冲突
4. **性能优化**: 合理配置连接池和超时参数
5. **安全考虑**: 敏感配置使用环境变量

## 🎯 AI编程建议

1. **理解架构**: 先了解整体架构和组件关系
2. **遵循规范**: 按照项目命名和配置规范开发
3. **健康检查**: 新组件必须实现健康检查
4. **监控集成**: 考虑监控和指标收集
5. **文档更新**: 及时更新相关文档

---

*此文档由脚本自动生成，请定期更新以保持准确性*
