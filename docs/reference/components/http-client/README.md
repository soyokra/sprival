# Spring HTTP Client 模块

## 概述
Spring HTTP Client 模块提供了完整的HTTP客户端解决方案，包括高性能传输层、声明式客户端、容错机制和监控集成。该模块旨在为 Sprival 项目提供高效、可靠的HTTP通信能力。负载均衡由服务端控制，客户端专注于高性能通信和容错处理。

## 核心特性
- ✅ **高性能传输**: 采用OkHttp高性能HTTP客户端，支持连接池、HTTP/2
- ✅ **声明式编程**: 基于Feign的声明式HTTP客户端，简化开发
- ✅ **容错机制**: 集成Resilience4j提供熔断、重试、限流等容错能力
- ✅ **监控集成**: 与Micrometer无缝集成，提供完整的性能监控
- ✅ **配置灵活**: 支持细粒度的配置和自定义扩展

## 组件

### 组件架构
```
应用代码 → Feign接口 → Resilience4j容错 → OkHttp传输 → 目标服务（服务端负载均衡）
                ↓
            Micrometer监控 ← Actuator健康检查
```

### 组件说明

#### 1. HTTP传输层
- **OkHttp**: 高性能HTTP客户端，提供连接池、HTTP/2支持、拦截器等企业级特性

#### 2. 声明式客户端层
- **Feign**: Spring Cloud官方声明式HTTP客户端，与Spring生态完美集成，简化HTTP调用开发

#### 3. 容错层
- **Resilience4j**: 现代容错库，提供熔断器、重试器、限流器、隔板等完整容错能力

#### 4. 监控与治理层
- **Micrometer**: 统一指标收集和监控，支持Prometheus、Grafana等多种监控系统
- **Spring Boot Actuator**: 健康检查和监控端点，提供运维友好的管理接口


## 配置说明

### Feign配置

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `feign.okhttp.enabled` | `false` | 启用OkHttp作为底层HTTP客户端 |
| `feign.client.config.default.connect-timeout` | `10000` | 连接超时时间(毫秒) |
| `feign.client.config.default.read-timeout` | `60000` | 读取超时时间(毫秒) |
| `feign.client.config.default.logger-level` | `NONE` | 日志级别 |
| `feign.compression.request.enabled` | `false` | 启用请求压缩 |
| `feign.compression.response.enabled` | `false` | 启用响应压缩 |

### OkHttp配置

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `okhttp.connection-pool.max-idle-connections` | `5` | 最大空闲连接数 |
| `okhttp.connection-pool.keep-alive-duration` | `300s` | 连接保持活跃时间 |
| `okhttp.connect-timeout` | `10s` | 连接超时时间 |
| `okhttp.read-timeout` | `30s` | 读取超时时间 |
| `okhttp.write-timeout` | `30s` | 写入超时时间 |

### Resilience4j配置

#### 熔断器配置

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `resilience4j.circuitbreaker.instances.feign.failure-rate-threshold` | `50` | 失败率阈值(%) |
| `resilience4j.circuitbreaker.instances.feign.wait-duration-in-open-state` | `60s` | 熔断器打开后等待时间 |
| `resilience4j.circuitbreaker.instances.feign.sliding-window-size` | `100` | 滑动窗口大小 |
| `resilience4j.circuitbreaker.instances.feign.minimum-number-of-calls` | `100` | 最小调用次数 |

#### 重试配置

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `resilience4j.retry.instances.feign.max-attempts` | `3` | 最大重试次数 |
| `resilience4j.retry.instances.feign.wait-duration` | `500ms` | 重试间隔时间 |
| `resilience4j.retry.instances.feign.retry-exceptions` | - | 需要重试的异常类型 |


