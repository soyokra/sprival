# Spring HTTP Client

## 组件说明

### 技术选型

采用「声明式客户端 + 容错机制 + 高性能传输 + 可观测性工具」的组合方案，覆盖 HTTP 调用全场景需求。

| 组件 | 选型理由 |
|------|---------|
| [Feign](https://github.com/OpenFeign/feign) | Spring Cloud 官方声明式 HTTP 客户端，与 Spring 生态完美集成，简化 HTTP 调用开发，支持注解式接口定义 |
| [Resilience4j](https://resilience4j.readme.io/) | 现代容错库，提供熔断器、重试器、限流器、隔板等完整容错能力，轻量级且功能强大 |
| [OkHttp](https://square.github.io/okhttp/) | 高性能 HTTP 客户端，提供连接池、HTTP/2 支持、拦截器等企业级特性，性能优于 JDK 默认实现 |
| [Micrometer](https://micrometer.io/) | 统一指标收集和监控，支持 Prometheus、Grafana 等多种监控系统，与 Spring Boot 深度集成 |
| [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html) | 健康检查和监控端点，提供运维友好的管理接口，支持多种监控协议 |

### 架构设计

采用分层架构设计，各层职责清晰，组件协作紧密：

```
应用层
  ↓
声明式客户端层（Feign）
  ↓
容错层（Resilience4j）
  ↓
HTTP传输层（OkHttp）
  ↓
目标服务（服务端负载均衡）
  ↑
监控与治理层（Micrometer + Actuator）
```

**架构层次说明：**

- **声明式客户端层**：Feign 提供基于注解的声明式 HTTP 客户端，通过接口定义简化远程调用，支持请求/响应编解码、拦截器等扩展能力
- **容错层**：Resilience4j 提供熔断器、重试器、限流器等容错机制，保障服务调用的稳定性和可用性，防止级联故障
- **HTTP传输层**：OkHttp 提供高性能 HTTP 传输能力，支持连接池复用、HTTP/2 协议、请求/响应拦截等特性，提升传输效率
- **监控与治理层**：Micrometer 统一收集指标数据，Actuator 提供健康检查和监控端点，支持 Prometheus、Grafana 等监控系统集成

**组件协作关系：**

- Feign 作为声明式客户端，通过注解定义接口，自动生成 HTTP 调用代码
- Resilience4j 与 Feign 集成，在 Feign 调用前后提供容错保护，支持熔断、重试等机制
- OkHttp 作为 Feign 的底层 HTTP 客户端，提供高性能的 HTTP 传输能力，替代 JDK 默认实现
- Micrometer 自动收集 Feign 和 Resilience4j 的指标数据，通过 Actuator 端点暴露，支持监控系统集成


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


## 监控指标

### Feign HTTP 客户端指标

| 指标名称 | 类型 | 描述 | 监控建议 |
|---------|------|------|---------|
| feign_Client_http_response_code_total | Counter | HTTP 响应码统计，包含 method、uri、status 标签 | 按 status 分组统计错误率；识别 4xx/5xx 错误；按 uri 分组识别问题接口 |
| feign_Client_seconds_count | Counter | Feign 客户端请求总次数，包含 method、uri、status 标签 | 计算 QPS；按 uri 分组识别热点接口；按 status 分组统计成功率 |
| feign_Client_seconds_sum | Summary | Feign 客户端请求处理总耗时（秒），包含 method、uri、status 标签 | 配合 count 计算平均响应时间：sum / count；按 uri 分组识别慢接口 |
| feign_Client_seconds_max | Gauge | Feign 客户端最近一段时间的最大请求延迟（秒） | 识别慢请求尖刺，超过阈值（如 3s）需告警；监控 P99/P95 延迟 |
| feign_Feign_exception_seconds_count | Counter | Feign 异常发生次数，包含 exception、method、uri 标签 | 监控异常频率；按 exception 类型分组识别常见异常；异常率上升需告警 |
| feign_Feign_exception_seconds_sum | Summary | Feign 异常处理总耗时（秒），包含 exception、method、uri 标签 | 评估异常对性能的影响；识别异常处理耗时较长的场景 |
| feign_Feign_exception_seconds_max | Gauge | Feign 异常处理的最大耗时（秒） | 识别异常处理性能瓶颈 |

### Resilience4j 熔断器指标

| 指标名称 | 类型 | 描述 | 监控建议 |
|---------|------|------|---------|
| resilience4j_circuitbreaker_state | Gauge | 熔断器状态（0=关闭，1=打开，2=半开） | 监控熔断器状态变化；状态为 OPEN 时需立即告警；半开状态需关注恢复情况 |
| resilience4j_circuitbreaker_failure_rate | Gauge | 熔断器失败率（百分比） | 失败率超过阈值（如 50%）需告警；监控失败率趋势，评估服务健康度 |
| resilience4j_circuitbreaker_slow_call_rate | Gauge | 熔断器慢调用率（百分比） | 慢调用率持续高位说明服务响应慢，需要优化或扩容 |
| resilience4j_circuitbreaker_slow_calls | Gauge | 熔断器慢调用数量 | 监控慢调用数量趋势；结合 slow_call_rate 评估服务性能 |
| resilience4j_circuitbreaker_buffered_calls | Gauge | 熔断器缓冲的调用数量 | 监控缓冲调用数，评估熔断器是否正常工作 |
| resilience4j_circuitbreaker_calls_seconds_count | Counter | 熔断器调用总次数，包含 state 标签 | 统计通过熔断器的调用量；按 state 分组分析熔断器行为 |
| resilience4j_circuitbreaker_calls_seconds_sum | Summary | 熔断器调用总耗时（秒），包含 state 标签 | 配合 count 计算平均调用时间；评估熔断器对性能的影响 |
| resilience4j_circuitbreaker_calls_seconds_max | Gauge | 熔断器调用的最大耗时（秒） | 识别通过熔断器的慢调用 |
| resilience4j_circuitbreaker_not_permitted_calls_total | Counter | 熔断器拒绝的调用总数（OPEN 状态时） | 监控被拒绝的调用数；持续增长说明服务异常，需要排查上游服务 |

### Resilience4j 重试指标

| 指标名称 | 类型 | 描述 | 监控建议 |
|---------|------|------|---------|
| resilience4j_retry_calls_total | Counter | 重试调用总次数，包含 result、retry_name 标签 | 监控重试频率；重试次数过多说明服务不稳定，需要优化或排查问题；按 result 分组分析重试成功率 |
