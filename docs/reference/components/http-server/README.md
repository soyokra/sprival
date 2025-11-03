# HTTP Server 模块

## 概述

Sprival HTTP Server 模块基于 Jetty 嵌入式服务器，提供高性能、轻量级的 Web 服务能力。Jetty 以其低资源占用、高并发处理能力和灵活的可定制性著称，是构建现代微服务应用的理想选择。

## 核心特性

- **高性能 Jetty 服务器**: 轻量级、低内存占用、高并发处理能力，适合云原生部署
- **访问日志管理**: 支持 EXTENDED_NCSA 格式访问日志，自动按天轮转，可配置忽略路径
- **安全头配置**: 内置 HTTP 安全头，防止 XSS、点击劫持、MIME 嗅探等常见 Web 安全漏洞
- **线程池优化**: 灵活的线程池配置，支持动态调整最大/最小线程数和空闲超时
- **HTTP 压缩**: 自动压缩 JSON、HTML、CSS 等响应内容，减少网络传输
- **优雅关闭**: 支持服务平滑下线，完成处理中的请求后再关闭
- **监控集成**: 与 Prometheus + Grafana 无缝集成，提供详细的性能指标

## 组件清单

- [Eclipse Jetty](https://www.eclipse.org/jetty/) - 嵌入式 Web 服务器和 Servlet 容器
- [Spring Boot Jetty Starter](https://docs.spring.io/spring-boot/docs/2.7.18/reference/html/web.html#web.servlet.embedded-container.jetty) - Spring Boot Jetty 集成

## 配置说明

```properties
# ===========================================
# HTTP Server 基础配置
# ===========================================
# 服务端口
server.port = 8338
# 上下文路径，所有接口前缀
server.servlet.context-path = /api

# 编码配置
server.servlet.encoding.charset = UTF-8
server.servlet.encoding.enabled = true
server.servlet.encoding.force = true

# ===========================================
# Jetty 线程池配置
# ===========================================
# 最大工作线程数，根据业务负载调整
server.jetty.threads.max = 200
# 最小工作线程数，保持一定空闲线程快速响应
server.jetty.threads.min = 20
# 线程空闲超时时间
server.jetty.threads.idle-timeout = 60s
# 连接空闲超时时间
server.jetty.connection-idle-timeout = 60s

# ===========================================
# Jetty 访问日志配置
# ===========================================
# 启用访问日志
server.jetty.accesslog.enabled = true
# 日志文件路径
server.jetty.accesslog.filename = logs/jetty-access.log
# 日志格式：EXTENDED_NCSA (包含详细请求信息)
server.jetty.accesslog.format = EXTENDED_NCSA
# 日志保留天数
server.jetty.accesslog.retain-days = 7
# 忽略的路径（健康检查、监控端点）
server.jetty.accesslog.ignore-paths = /actuator/health,/actuator/prometheus,/favicon.ico
# 追加模式
server.jetty.accesslog.append = true

# ===========================================
# HTTP 优化配置
# ===========================================
# HTTP 请求头最大大小
server.max-http-header-size = 16KB
# HTTP 表单提交最大大小
server.max-http-form-post-size = 2MB
# 优雅关闭：完成处理中的请求后再关闭
server.shutdown = graceful

# ===========================================
# HTTP 压缩配置
# ===========================================
# 启用 HTTP 响应压缩
server.compression.enabled = true
# 需要压缩的 MIME 类型
server.compression.mime-types = text/html,text/xml,text/plain,text/css,application/json,application/javascript
# 启用压缩的最小响应大小
server.compression.min-response-size = 2KB
```

## 安全配置

本模块通过 `SprivalJettySecurityHeaderCustomizer` 自动配置以下 HTTP 安全头：

| 安全头 | 配置值 | 作用 |
|-------|--------|------|
| X-Frame-Options | DENY | 防止点击劫持攻击 |
| X-Content-Type-Options | nosniff | 防止 MIME 类型嗅探 |
| X-XSS-Protection | 1; mode=block | XSS 防护 |
| Content-Security-Policy | 限制资源加载来源 | 防止内容注入攻击 |
| Strict-Transport-Security | max-age=31536000 (仅 HTTPS) | 强制 HTTPS 访问 |
| Server | Sprival/1.0 | 隐藏服务器真实信息 |
| Cache-Control | no-cache (API 接口) | 防止缓存敏感信息 |
| Permissions-Policy | 禁用敏感设备权限 | 限制浏览器功能访问 |
| Referrer-Policy | strict-origin-when-cross-origin | 控制 Referer 信息泄露 |

## 监控指标

### Jetty 线程池指标

| 指标名称 | 类型 | 描述 | 监控建议 |
|---------|------|------|---------|
| jetty_threads_config_max | Gauge | 配置的最大线程数 | 对比实际使用情况，评估是否需要调整 |
| jetty_threads_config_min | Gauge | 配置的最小线程数 | 确保最小线程数满足基础负载 |
| jetty_threads_current | Gauge | 当前线程池中的线程总数 | 监控线程池规模变化趋势 |
| jetty_threads_idle | Gauge | 当前空闲线程数 | 持续为 0 说明线程池不足，需要增加 max |
| jetty_threads_busy | Gauge | 当前正在处理请求的线程数 | 接近 max 时需要告警，表示负载高 |
| jetty_threads_jobs | Gauge | 队列中等待执行的任务数 | 持续 >0 说明线程不足，请求在排队 |

### HTTP 请求指标

| 指标名称 | 类型 | 描述 | 监控建议 |
|---------|------|------|---------|
| http_server_requests_seconds_count | Counter | HTTP 请求总数，包含 uri、method、status、exception 标签 | 计算 QPS；按 status 分组统计错误率；按 uri 分组识别热点接口 |
| http_server_requests_seconds_sum | Summary | HTTP 请求处理总耗时（秒） | 配合 count 计算平均响应时间：sum / count |
| http_server_requests_seconds_max | Gauge | 最近一段时间的最大请求延迟（秒） | 识别慢请求尖刺，超过阈值（如 5s）需告警 |

### Jetty 连接指标

| 指标名称 | 类型 | 描述 | 监控建议 |
|---------|------|------|---------|
| jetty_connections_current_connections | Gauge | 当前活动 TCP 连接数 | 监控连接数趋势，异常增长需要排查；持续高位可能是连接泄露 |
| jetty_connections_max_connections | Gauge | 历史最大连接数 | 用于容量规划，评估峰值负载 |
| jetty_connections_messages_in_messages_total | Counter | 接收的 HTTP 消息总数 | 结合 messages_out 识别流量模式；异常比例需排查 |
| jetty_connections_messages_out_messages_total | Counter | 发送的 HTTP 消息总数 | 监控响应吞吐量；正常情况应与 messages_in 接近 |
| jetty_connections_bytes_in_bytes_count | Counter | 接收的请求次数（用于计算字节统计） | 配合 sum 计算平均请求大小 |
| jetty_connections_bytes_in_bytes_sum | Summary | 接收的字节总数 | 监控入站流量；平均请求大小 = sum / count；识别大请求攻击 |
| jetty_connections_bytes_in_bytes_max | Gauge | 最近一段时间接收的最大字节数 | 识别异常大请求，可能是上传攻击或误用 |
| jetty_connections_bytes_out_bytes_count | Counter | 发送的响应次数（用于计算字节统计） | 配合 sum 计算平均响应大小 |
| jetty_connections_bytes_out_bytes_sum | Summary | 发送的字节总数 | 监控出站流量；评估压缩效果；计算带宽使用 |
| jetty_connections_bytes_out_bytes_max | Gauge | 最近一段时间发送的最大字节数 | 识别大响应接口，优化序列化或分页 |
| jetty_connections_request_seconds_count | Counter | 连接级别请求处理次数 | 与 http_server_requests_seconds_count 对比，识别连接复用情况 |
| jetty_connections_request_seconds_sum | Summary | 连接级别请求处理总耗时 | 监控连接层面的性能，包含网络传输时间 |
| jetty_connections_request_seconds_max | Gauge | 连接级别最大请求处理时间 | 识别网络层面的慢请求 |