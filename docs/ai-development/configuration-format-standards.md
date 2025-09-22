# Sprival 配置格式规范

## 概述

本文档定义了Sprival项目中配置文件的格式规范，确保配置的可读性、可维护性和一致性。

## 🎯 配置格式原则

### 1. 扁平化优先
- **优先使用**: 扁平化Properties格式
- **配置清晰**: 每个配置项都有明确的路径
- **易于查找**: 可以通过IDE快速搜索和定位
- **避免缩进**: 不需要考虑YAML的缩进问题

### 2. 配置集中
- **统一位置**: 所有配置都在`application.properties`中
- **避免分散**: 不使用外部配置文件（除非特殊情况）
- **便于管理**: 减少文件依赖和部署复杂度

### 3. 格式一致
- **命名规范**: 使用点分隔的层次结构
- **注释完整**: 每个配置段都有清晰的注释
- **分组明确**: 相关配置项放在一起

## 📋 配置格式规范

### 推荐格式（扁平化Properties）

```properties
# ===========================================
# [组件名]配置
# ===========================================

# 基础配置
spring.redis.redisson.config.singleServerConfig.address = redis://localhost:6379
spring.redis.redisson.config.singleServerConfig.password = workdock
spring.redis.redisson.config.singleServerConfig.database = 0

# 连接池配置
spring.redis.redisson.config.singleServerConfig.connectionPoolSize = 20
spring.redis.redisson.config.singleServerConfig.connectionMinimumIdleSize = 5

# 超时配置
spring.redis.redisson.config.singleServerConfig.idleConnectionTimeout = 10000
spring.redis.redisson.config.singleServerConfig.connectTimeout = 10000
spring.redis.redisson.config.singleServerConfig.timeout = 3000

# 重试配置
spring.redis.redisson.config.singleServerConfig.retryAttempts = 3
spring.redis.redisson.config.singleServerConfig.retryInterval = 1500

# 网络配置
spring.redis.redisson.config.singleServerConfig.keepAlive = true
spring.redis.redisson.config.singleServerConfig.tcpKeepAlive = true

# 线程配置
spring.redis.redisson.config.threads = 16
spring.redis.redisson.config.nettyThreads = 32
spring.redis.redisson.config.transportMode = NIO
```

### 避免格式（YAML内联）

```properties
# ❌ 不推荐：YAML内联格式
spring.redis.redisson.config = |
  singleServerConfig:
    address: "redis://localhost:6379"
    password: "workdock"
    database: 0
    connectionPoolSize: 20
    connectionMinimumIdleSize: 5
    idleConnectionTimeout: 10000
    connectTimeout: 10000
    timeout: 3000
    retryAttempts: 3
    retryInterval: 1500
    keepAlive: true
    tcpKeepAlive: true
  threads: 16
  nettyThreads: 32
  transportMode: "NIO"
```

## 🔧 配置格式优势

### 1. 扁平化格式优势
- **配置项明确**: 每个配置项都有完整的路径
- **易于搜索**: 可以通过IDE或编辑器快速搜索特定配置项
- **避免缩进错误**: 不需要考虑YAML的缩进问题
- **IDE支持更好**: 大多数IDE对Properties格式的支持更完善
- **配置验证**: 更容易进行配置项的验证和检查

### 2. 集中配置优势
- **文件数量减少**: 从多个配置文件减少到1个
- **配置集中**: 所有配置都在`application.properties`中
- **维护简化**: 不需要管理外部YAML文件
- **部署简化**: 减少文件依赖

## 📝 配置命名规范

### 1. 层次结构
```
spring.[组件名].config.[子配置].[具体配置项] = 值
```

### 2. 命名示例
```properties
# Redis配置
spring.redis.redisson.config.singleServerConfig.address
spring.redis.redisson.config.singleServerConfig.password

# 数据库配置
spring.datasource.master.url
spring.datasource.master.username
spring.datasource.slave.url
spring.datasource.slave.username

# 缓存配置
spring.cache.redis.key-prefix
spring.cache.redis.use-key-prefix
spring.cache.redis.enable-statistics
```

### 3. 注释规范
```properties
# ===========================================
# 配置段标题
# ===========================================

# 配置项说明
spring.redis.redisson.config.singleServerConfig.address = redis://localhost:6379
```

## 🚀 配置迁移指南

### 1. 从YAML内联格式迁移

**迁移前**:
```properties
spring.redis.redisson.config = |
  singleServerConfig:
    address: "redis://localhost:6379"
    password: "workdock"
```

**迁移后**:
```properties
spring.redis.redisson.config.singleServerConfig.address = redis://localhost:6379
spring.redis.redisson.config.singleServerConfig.password = workdock
```

### 2. 从外部文件迁移

**迁移前**:
```yaml
# redisson.yml
singleServerConfig:
  address: "redis://localhost:6379"
  password: "workdock"
```

**迁移后**:
```properties
# application.properties
spring.redis.redisson.config.singleServerConfig.address = redis://localhost:6379
spring.redis.redisson.config.singleServerConfig.password = workdock
```

## 🔍 配置验证

### 1. 格式验证
```powershell
# 检查配置文件语法
.\scripts\validate-config-format.ps1

# 检查配置项完整性
.\scripts\validate-config-completeness.ps1
```

### 2. 功能验证
```powershell
# 启动应用验证配置
mvn spring-boot:run

# 检查健康状态
curl http://localhost:8338/api/actuator/health

# 检查特定组件状态
curl http://localhost:8338/api/actuator/health/redis
```

## 📊 配置管理最佳实践

### 1. 配置组织
- **按组件分组**: 相关配置项放在一起
- **使用注释**: 每个配置段都有清晰的注释
- **逻辑顺序**: 按照配置的逻辑关系排序

### 2. 配置维护
- **版本控制**: 配置文件纳入版本控制
- **变更记录**: 记录配置变更的原因和影响
- **测试验证**: 配置变更后必须进行测试验证

### 3. 配置安全
- **敏感信息**: 敏感配置使用环境变量或加密
- **访问控制**: 限制配置文件的访问权限
- **审计日志**: 记录配置变更的审计日志

## 🎯 特殊情况处理

### 1. 复杂配置
对于特别复杂的配置，可以考虑以下方案：
- **分阶段迁移**: 逐步将复杂配置扁平化
- **保留注释**: 在扁平化配置中添加详细注释
- **文档说明**: 在文档中说明复杂配置的结构

### 2. 第三方组件
对于第三方组件的配置：
- **优先扁平化**: 尽量使用扁平化格式
- **保持兼容**: 确保与第三方组件的兼容性
- **文档参考**: 参考官方文档的推荐格式

## 📚 参考资源

- **Spring Boot配置**: [Spring Boot Configuration Properties](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)
- **Properties格式**: [Java Properties Format](https://docs.oracle.com/javase/8/docs/api/java/util/Properties.html)
- **配置最佳实践**: [Spring Boot Best Practices](https://spring.io/guides/topicals/spring-boot-best-practices/)

## 🔄 持续改进

### 1. 规范更新
- 根据项目发展更新配置格式规范
- 收集使用反馈，持续优化规范
- 定期审查配置格式的一致性

### 2. 工具增强
- 开发配置格式验证工具
- 提供配置迁移脚本
- 完善配置文档生成工具

---

*此规范将根据项目发展和使用反馈持续优化*
