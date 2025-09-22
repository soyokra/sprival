# Sprival项目上下文生成脚本
# 用于AI编程前了解项目现状

param(
    [string]$OutputDir = "docs/ai-development/context",
    [switch]$Force = $false
)

Write-Host "🚀 开始生成Sprival项目上下文..." -ForegroundColor Green

# 创建输出目录
if (!(Test-Path $OutputDir)) {
    New-Item -ItemType Directory -Path $OutputDir -Force | Out-Null
}

# 获取当前时间戳
$timestamp = Get-Date -Format "yyyy-MM-dd-HH-mm"
$date = Get-Date -Format "yyyy-MM-dd HH:mm:ss"

# 1. 生成项目基础信息
Write-Host "📋 生成项目基础信息..." -ForegroundColor Yellow
$projectInfo = @"
# Sprival项目上下文 - $date

## 项目基本信息

### 项目元信息
- **项目名称**: Sprival
- **项目描述**: Spring Boot组件集成模板
- **版本**: 0.0.1
- **Java版本**: 1.8
- **Spring Boot版本**: 2.7.18
- **Spring Cloud版本**: 2021.0.8
- **构建工具**: Maven
- **Web服务器**: Jetty
- **包结构**: com.soyokra.sprival

### 技术栈清单
- **核心框架**: Spring Boot 2.7.18
- **Web层**: Spring MVC + Jetty
- **数据层**: MyBatis-Plus + Dynamic-Datasource + HikariCP
- **缓存层**: Spring Cache + Redis + Redisson
- **消息队列**: RabbitMQ + Kafka
- **文档数据库**: MongoDB
- **分析数据库**: ClickHouse
- **HTTP客户端**: Feign + OkHttp + Resilience4j
- **监控**: Spring Boot Actuator + Micrometer + Prometheus
- **限流**: Guava RateLimiter
- **SQL监控**: P6Spy

### 编码规范
- **文件编码**: UTF-8
- **跨平台兼容**: Windows GBK环境支持
- **启动脚本**: start-utf8.bat, start-utf8.ps1

"@

$projectInfo | Out-File -FilePath "$OutputDir/project-context-$timestamp.md" -Encoding UTF8

# 2. 分析组件状态
Write-Host "🔍 分析组件状态..." -ForegroundColor Yellow

$componentStatus = @"
## 组件状态矩阵

### 已完成组件 ✅

#### 1. HTTP Server (spring-http-server)
- **状态**: 已完成
- **技术栈**: Jetty + Guava RateLimiter
- **配置类**: SprivalJettyCustomizer, SprivalRateLimiterConfiguration
- **功能**: Web服务器 + 接口限流
- **文档**: docs/spring-http-server/README.md

#### 2. MySQL (spring-mysql)
- **状态**: 已完成
- **技术栈**: MyBatis-Plus + Dynamic-Datasource + HikariCP + P6Spy
- **配置类**: SprivalMybatisPlusConfiguration
- **功能**: 数据库访问 + 多数据源 + SQL监控
- **文档**: docs/spring-mysql/README.md

#### 3. Redis (spring-redis)
- **状态**: 已完成
- **技术栈**: Spring Cache + Spring Data Redis + Redisson
- **配置类**: SprivalRedisConfiguration, SprivalRedisHealthIndicator
- **功能**: 缓存 + 分布式锁 + 健康检查
- **文档**: docs/spring-redis/README.md
- **优先级**: Redisson > Spring Data Redis

#### 4. ClickHouse (spring-clickhouse)
- **状态**: 已完成
- **技术栈**: ClickHouse JDBC + MyBatis-Plus集成
- **配置类**: SprivalClickHouseDataSourceCreator
- **功能**: 分析数据库 + 数据源集成
- **文档**: docs/spring-clickhouse/README.md

#### 5. MongoDB (spring-mongo)
- **状态**: 已完成
- **技术栈**: Spring Data MongoDB
- **配置类**: SprivalMongoHealthIndicator
- **功能**: 文档数据库 + 健康检查
- **文档**: docs/spring-mongo/README.md

#### 6. RabbitMQ (spring-rabbit)
- **状态**: 已完成
- **技术栈**: Spring AMQP
- **配置类**: SprivalRabbitHealthIndicator
- **功能**: 消息队列 + 健康检查
- **文档**: docs/spring-rabbit/README.md

#### 7. Kafka (spring-kafka)
- **状态**: 已完成
- **技术栈**: Spring Kafka
- **配置类**: SprivalKafkaProducerCustomizer, SprivalKafkaConsumerCustomizer
- **功能**: 消息队列 + 监控集成
- **文档**: docs/spring-kafka/README.md

#### 8. HTTP Client (spring-http-client)
- **状态**: 已完成
- **技术栈**: Feign + OkHttp + Resilience4j + LoadBalancer + Micrometer
- **配置类**: SprivalHttpClientConfiguration, SprivalHttpClientHealthIndicator
- **功能**: 声明式HTTP客户端 + 容错机制 + 负载均衡 + 监控
- **文档**: docs/spring-http-client/README.md

### 组件依赖关系
- **spring-mysql** → **spring-clickhouse** (数据源基础)
- **spring-redis** → **spring-cache** (缓存基础)
- **http-server** → **ratelimiter** (限流集成)
- **所有组件** → **monitoring** (监控集成)

"@

$componentStatus | Out-File -FilePath "$OutputDir/component-status-$timestamp.md" -Encoding UTF8

# 3. 生成配置文件信息
Write-Host "⚙️ 分析配置文件..." -ForegroundColor Yellow

$configInfo = @"
## 配置文件状态

### 核心配置文件
- **pom.xml**: Maven依赖管理，包含所有组件依赖
- **application.properties**: 应用配置，包含所有组件配置
- **redisson.yml**: Redisson配置
- **spy.properties**: P6Spy SQL监控配置

### Docker配置
- **dockers/docker-compose.yml**: 容器编排配置
- **dockers/*/Dockerfile**: 各组件容器化配置
- **dockers/*/volumes/**: 数据持久化目录

### 启动脚本
- **start-utf8.bat**: Windows UTF-8启动脚本
- **start-utf8.ps1**: PowerShell UTF-8启动脚本

### 文档结构
- **docs/**: 项目文档根目录
- **docs/ai-development/**: AI辅助开发文档
- **docs/spring-*/**: 各组件文档
- **docs/SYSTEM-ENVIRONMENT.md**: 系统环境配置
- **docs/ENCODING-STANDARDS.md**: 编码规范

"@

$configInfo | Out-File -FilePath "$OutputDir/config-status-$timestamp.md" -Encoding UTF8

# 4. 生成开发环境信息
Write-Host "🛠️ 分析开发环境..." -ForegroundColor Yellow

$devInfo = @"
## 开发环境信息

### 项目结构
```
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
```

### 启动方式
1. **Maven启动**: `mvn spring-boot:run`
2. **脚本启动**: `start-utf8.bat` (Windows)
3. **Docker启动**: `docker-compose up`

### 监控端点
- **健康检查**: http://localhost:8338/api/actuator/health
- **应用信息**: http://localhost:8338/api/actuator/info
- **指标监控**: http://localhost:8338/api/actuator/metrics
- **Prometheus**: http://localhost:8338/api/actuator/prometheus

"@

$devInfo | Out-File -FilePath "$OutputDir/dev-environment-$timestamp.md" -Encoding UTF8

# 5. 生成AI编程指导
Write-Host "🤖 生成AI编程指导..." -ForegroundColor Yellow

$aiGuidance = @"
## AI编程指导

### 项目特点
1. **组件化架构**: 每个组件都有独立的配置类和健康检查
2. **统一命名规范**: 所有配置类都以"Sprival"开头
3. **健康检查集成**: 所有组件都集成了Spring Boot Actuator健康检查
4. **监控友好**: 集成了Micrometer和Prometheus监控
5. **UTF-8编码**: 支持跨平台编码兼容

### 开发规范
1. **包结构**: 按功能模块分包，配置类放在config包下
2. **命名规范**: 类名使用Sprival前缀，方法名使用驼峰命名
3. **配置管理**: 使用@ConfigurationProperties进行配置绑定
4. **健康检查**: 每个组件都要实现HealthIndicator
5. **异常处理**: 统一的异常处理和日志记录

### 常用模式
1. **配置类模式**: @Configuration + @Bean
2. **属性绑定模式**: @ConfigurationProperties
3. **健康检查模式**: 继承或实现HealthIndicator
4. **自动配置模式**: @ConditionalOnClass + @ConditionalOnBean

### 注意事项
1. **版本兼容**: 确保所有依赖版本与Spring Boot 2.7.18兼容
2. **编码问题**: 所有文件使用UTF-8编码
3. **配置冲突**: 注意不同组件间的配置冲突
4. **性能考虑**: 合理配置连接池和超时参数
5. **安全考虑**: 敏感配置使用环境变量或加密

### 快速开始模板
```java
// 1. 配置类模板
@Configuration
@EnableConfigurationProperties(YourProperties.class)
public class YourConfiguration {
    
    @Bean
    @ConditionalOnClass(YourClass.class)
    public YourBean yourBean(YourProperties properties) {
        return new YourBean(properties);
    }
}

// 2. 属性类模板
@ConfigurationProperties(prefix = "your.prefix")
@Data
public class YourProperties {
    private String property1;
    private Integer property2;
}

// 3. 健康检查模板
@Component
public class YourHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        // 健康检查逻辑
        return Health.up().build();
    }
}
```

"@

$aiGuidance | Out-File -FilePath "$OutputDir/ai-guidance-$timestamp.md" -Encoding UTF8

# 6. 生成综合上下文文档
Write-Host "📄 生成综合上下文文档..." -ForegroundColor Yellow

$comprehensiveContext = @"
# Sprival项目AI编程上下文

**生成时间**: $date  
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
```bash
# 方式1: Maven启动
mvn spring-boot:run

# 方式2: 脚本启动 (Windows)
start-utf8.bat

# 方式3: Docker启动
docker-compose up
```

### 访问端点
- **应用**: http://localhost:8338/api
- **健康检查**: http://localhost:8338/api/actuator/health
- **监控指标**: http://localhost:8338/api/actuator/metrics

## 📁 项目结构

```
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
```

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
"@

$comprehensiveContext | Out-File -FilePath "$OutputDir/sprival-ai-context-$timestamp.md" -Encoding UTF8

# 7. 生成JSON格式的上下文（供AI工具使用）
Write-Host "📋 生成JSON格式上下文..." -ForegroundColor Yellow

$jsonContext = @{
    project = @{
        name = "Sprival"
        description = "Spring Boot组件集成模板"
        version = "0.0.1"
        javaVersion = "1.8"
        springBootVersion = "2.7.18"
        springCloudVersion = "2021.0.8"
        buildTool = "Maven"
        webServer = "Jetty"
        encoding = "UTF-8"
    }
    components = @{
        httpServer = @{
            status = "completed"
            technologies = @("Jetty", "Guava RateLimiter")
            configClasses = @("SprivalJettyCustomizer", "SprivalRateLimiterConfiguration")
        }
        mysql = @{
            status = "completed"
            technologies = @("MyBatis-Plus", "Dynamic-Datasource", "HikariCP", "P6Spy")
            configClasses = @("SprivalMybatisPlusConfiguration")
        }
        redis = @{
            status = "completed"
            technologies = @("Spring Cache", "Spring Data Redis", "Redisson")
            configClasses = @("SprivalRedisConfiguration", "SprivalRedisHealthIndicator")
        }
        clickhouse = @{
            status = "completed"
            technologies = @("ClickHouse JDBC", "MyBatis-Plus集成")
            configClasses = @("SprivalClickHouseDataSourceCreator")
        }
        mongodb = @{
            status = "completed"
            technologies = @("Spring Data MongoDB")
            configClasses = @("SprivalMongoHealthIndicator")
        }
        rabbitmq = @{
            status = "completed"
            technologies = @("Spring AMQP")
            configClasses = @("SprivalRabbitHealthIndicator")
        }
        kafka = @{
            status = "completed"
            technologies = @("Spring Kafka")
            configClasses = @("SprivalKafkaProducerCustomizer", "SprivalKafkaConsumerCustomizer")
        }
        httpClient = @{
            status = "completed"
            technologies = @("Feign", "OkHttp", "Resilience4j", "LoadBalancer", "Micrometer")
            configClasses = @("SprivalHttpClientConfiguration", "SprivalHttpClientHealthIndicator")
        }
    }
    development = @{
        packageStructure = "com.soyokra.sprival"
        namingConvention = "Sprival前缀"
        healthCheckPattern = "HealthIndicator实现"
        configurationPattern = "@Configuration + @Bean"
        monitoringIntegration = "Actuator + Micrometer + Prometheus"
    }
    endpoints = @{
        application = "http://localhost:8338/api"
        health = "http://localhost:8338/api/actuator/health"
        metrics = "http://localhost:8338/api/actuator/metrics"
        prometheus = "http://localhost:8338/api/actuator/prometheus"
    }
    generatedAt = $date
    version = "1.0"
} | ConvertTo-Json -Depth 10

$jsonContext | Out-File -FilePath "$OutputDir/sprival-context-$timestamp.json" -Encoding UTF8

# 8. 创建最新版本的符号链接
Write-Host "🔗 创建最新版本链接..." -ForegroundColor Yellow

$latestFiles = @(
    "project-context-latest.md",
    "component-status-latest.md", 
    "config-status-latest.md",
    "dev-environment-latest.md",
    "ai-guidance-latest.md",
    "sprival-ai-context-latest.md",
    "sprival-context-latest.json"
)

$timestampFiles = @(
    "project-context-$timestamp.md",
    "component-status-$timestamp.md",
    "config-status-$timestamp.md", 
    "dev-environment-$timestamp.md",
    "ai-guidance-$timestamp.md",
    "sprival-ai-context-$timestamp.md",
    "sprival-context-$timestamp.json"
)

for ($i = 0; $i -lt $latestFiles.Length; $i++) {
    $latestPath = "$OutputDir/$($latestFiles[$i])"
    $timestampPath = "$OutputDir/$($timestampFiles[$i])"
    
    if (Test-Path $latestPath) {
        Remove-Item $latestPath -Force
    }
    
    Copy-Item $timestampPath $latestPath
}

Write-Host "✅ 项目上下文生成完成！" -ForegroundColor Green
Write-Host "📁 输出目录: $OutputDir" -ForegroundColor Cyan
Write-Host "📄 主要文件:" -ForegroundColor Cyan
Write-Host "   - sprival-ai-context-latest.md (综合上下文)" -ForegroundColor White
Write-Host "   - sprival-context-latest.json (JSON格式)" -ForegroundColor White
Write-Host "   - component-status-latest.md (组件状态)" -ForegroundColor White
Write-Host "   - ai-guidance-latest.md (AI编程指导)" -ForegroundColor White
