# Spring HTTP Server 模块

## 概述

Spring HTTP Server 模块提供了高性能的Web服务器解决方案，基于Jetty服务器，集成了接口限流、安全防护、性能监控等企业级功能。该模块专为高并发、低延迟的Web应用而设计。

## 核心特性

- ✅ **高性能服务器**: 基于 Jetty 9.4.x 提供卓越的并发处理能力
- ✅ **接口限流**: 集成 Guava RateLimiter 实现智能流量控制
- ✅ **安全防护**: HTTP安全头、XSS防护、CSRF保护
- ✅ **访问日志**: 详细的请求日志记录和分析
- ✅ **性能监控**: 与 Prometheus + Grafana 无缝集成
- ✅ **HTTP/2支持**: 现代化的HTTP协议支持
- ✅ **CORS配置**: 完整的跨域资源共享支持

## 组件清单

### 核心组件
- [jetty 9.4.53.v20231009](https://github.com/jetty/jetty.project) - 高性能Web服务器
- [guava 31.1-jre](https://github.com/google/guava) - Google工具库，提供RateLimiter限流功能
- [micrometer-registry-prometheus](https://micrometer.io/) - 指标收集和导出
- [spring-boot-starter-actuator](https://spring.io/projects/spring-boot) - 监控和管理端点

### 功能组件
- **限流控制**: Guava RateLimiter + 自定义拦截器
- **安全防护**: 自定义安全头 + CORS配置
- **访问日志**: Jetty AccessLog + 结构化日志
- **性能监控**: Micrometer指标 + 健康检查
- **错误处理**: JSON格式错误响应

## 快速开始

### 1. 依赖配置

项目已在 `pom.xml` 中配置了所需依赖：
```xml
<!-- Web服务器（排除默认Tomcat） -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
        </exclusion>
    </exclusions>
</dependency>

<!-- Jetty服务器 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jetty</artifactId>
</dependency>

<!-- 限流组件 -->
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.1-jre</version>
</dependency>
```

### 2. 基础配置

```properties
# 服务器基础配置
server.port = 8338
server.servlet.context-path = /api
server.shutdown = graceful

# 编码配置
server.servlet.encoding.charset = UTF-8
server.servlet.encoding.enabled = true
server.servlet.encoding.force = true

# HTTP优化配置
server.max-http-header-size = 16KB
server.max-http-form-post-size = 2MB

# Jetty线程池配置
server.jetty.threads.max = 200
server.jetty.threads.min = 10
server.jetty.threads.idle-timeout = 60s
server.jetty.connection-idle-timeout = 60s

# 访问日志配置
server.jetty.accesslog.enabled = true
server.jetty.accesslog.filename = jetty-access.log
server.jetty.accesslog.format = EXTENDED_NCSA
server.jetty.accesslog.retain-days = 30
server.jetty.accesslog.ignore-paths = /actuator/health,/favicon.ico

# HTTP/2支持
server.http2.enabled = true

# 压缩配置
server.compression.enabled = true
server.compression.mime-types = text/html,text/xml,text/plain,text/css,application/json,application/javascript
server.compression.min-response-size = 2KB
```

## 限流配置

### 限流策略设计

| 限流类型 | 速率限制 | 适用场景 | 配置说明 |
|---------|----------|----------|----------|
| 全局限流 | 100 req/s | 整体流量控制 | 防止系统过载 |
| API限流 | 50 req/s | API接口保护 | 保护核心业务接口 |
| 用户操作限流 | 10 req/s | 用户行为限制 | 防止恶意操作 |
| 登录限流 | 5 req/s | 登录接口保护 | 防止暴力破解 |

### 限流配置类

已实现的限流配置：
- `RateLimiterConfiguration`: 定义不同场景的限流器
- `RateLimitInterceptor`: 智能限流拦截器，根据URI和HTTP方法选择合适的限流策略
- `WebMvcConfiguration`: 注册限流拦截器到Spring MVC

### 使用示例

```java
// 在Controller中使用限流
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    
    @Autowired
    @Qualifier("apiRateLimiter")
    private RateLimiter rateLimiter;
    
    @GetMapping
    public ResponseEntity<List<User>> getUsers() {
        // 手动限流检查（可选，拦截器已自动处理）
        if (!rateLimiter.tryAcquire(1, TimeUnit.SECONDS)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(Collections.emptyList());
        }
        
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
```

## 安全配置

### HTTP安全头

当前配置的安全头：
```http
X-Frame-Options: DENY
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Content-Security-Policy: default-src 'self'; script-src 'self' 'unsafe-inline'
Strict-Transport-Security: max-age=31536000; includeSubDomains; preload
Server: Sprival/1.0
Cache-Control: no-cache, no-store, must-revalidate
Permissions-Policy: camera=(), microphone=(), geolocation=()
Referrer-Policy: strict-origin-when-cross-origin
```

### CORS配置

支持跨域资源共享：
- 允许所有源（生产环境应限制具体域名）
- 支持标准HTTP方法：GET, POST, PUT, DELETE, OPTIONS, HEAD
- 预检请求缓存1小时
- 支持携带认证信息

## 性能优化

### Jetty性能配置

已实现的性能优化：
- **线程池优化**: 最大200线程，最小10线程，60秒空闲超时
- **连接器优化**: 30秒连接超时，128个接受队列大小
- **HTTP配置优化**: 16KB请求/响应头，32KB输出缓冲
- **统计处理器**: 启用性能指标收集
- **优雅停机**: 30秒停机超时

### 性能调优建议

| 场景 | max_threads | min_threads | 说明 |
|------|-------------|-------------|------|
| 开发环境 | 50 | 5 | 资源节约型配置 |
| 测试环境 | 100 | 10 | 模拟生产负载 |
| 生产环境 | 200-500 | 20-50 | 高性能配置 |

## HTTP服务器监控

### 监控指标

HTTP服务器模块提供以下监控指标：

#### Jetty服务器指标
```properties
# 线程池指标
jetty.threads.current         # 当前线程数
jetty.threads.idle            # 空闲线程数
jetty.threads.busy            # 繁忙线程数
jetty.threads.jobs            # 队列中的任务数

# HTTP请求指标
http.server.requests          # HTTP请求统计
http.server.requests.active   # 当前活跃请求数
http.requests.custom.total    # 自定义HTTP请求计数

# 限流指标
rate_limit.requests.total     # 限流请求总数
rate_limit.rejected.total     # 限流拒绝总数
rate_limit.wait.duration      # 限流等待时间
```

### 健康检查

```bash
# 查看HTTP服务器健康状态
curl http://localhost:8338/api/actuator/health

# 响应示例
{
  "status": "UP",
  "components": {
    "jetty": {
      "status": "UP",
      "details": {
        "server": "Jetty",
        "version": "9.4.53.v20231009",
        "port": 8338,
        "contextPath": "/api",
        "status": "RUNNING"
      }
    }
  }
}
```

### 监控端点

```bash
# 查看HTTP请求指标
curl http://localhost:8338/api/actuator/metrics/http.server.requests

# 查看Jetty线程池指标
curl http://localhost:8338/api/actuator/metrics/jetty.threads.current

# 查看限流指标
curl http://localhost:8338/api/actuator/metrics/rate_limit.requests.total

# 查看访问日志
tail -f jetty-access.log
```

> **📊 完整监控方案**: 详细的监控架构、Prometheus配置、Grafana面板等请参考 [Spring监控模块文档](../spring-monitoring/README.md)

## 常见问题

### Q1: 为什么选择Jetty而不是Tomcat？
A: Jetty在高并发场景下表现更好：
- 内存占用更少
- 启动速度更快
- 更好的NIO支持
- 更适合微服务架构

### Q2: 如何调整线程池大小？
A: 根据应用负载和CPU核数调整：
```properties
# CPU核数 * 2 到 CPU核数 * 4
server.jetty.threads.max = 200
server.jetty.threads.min = 10
```

### Q3: 限流策略如何选择？
A: 建议分层限流：
- **全局限流**: 防止系统过载
- **API限流**: 保护核心接口  
- **登录限流**: 防止暴力破解
- **用户限流**: 防止恶意行为

### Q4: 如何启用HTTPS？
A: 配置SSL证书：
```properties
server.ssl.enabled = true
server.ssl.key-store = classpath:keystore.p12
server.ssl.key-store-password = your-password
server.ssl.key-store-type = PKCS12
```

### Q5: 访问日志格式说明？
A: 使用EXTENDED_NCSA格式，包含：
- 客户端IP
- 请求时间
- HTTP方法和URI
- 响应状态码
- 响应大小
- User-Agent
- Referer

## 最佳实践

### 1. 性能优化原则
- **线程池配置**: 根据CPU核数和负载特征调整
- **缓冲区大小**: 根据请求响应大小优化
- **连接超时**: 防止连接泄漏和资源浪费
- **压缩配置**: 减少网络传输，提高响应速度

### 2. 安全防护原则
- **最小权限**: 只开放必要的端口和路径
- **深度防御**: 多层安全防护机制
- **定期更新**: 及时更新安全补丁
- **监控告警**: 实时监控安全事件

### 3. 限流策略原则
- **分层限流**: 全局→API→用户操作的层次化限流
- **动态调整**: 根据系统负载动态调整限流参数
- **友好提示**: 限流时返回友好的错误信息
- **监控分析**: 分析限流数据优化策略

## 故障排查

### 常见问题诊断

```bash
# 检查端口占用
netstat -ano | findstr :8338

# 检查线程池状态
curl http://localhost:8338/api/actuator/metrics/jetty.threads.busy

# 检查限流状态
curl http://localhost:8338/api/actuator/metrics/rate_limit.rejected.total

# 分析访问日志
tail -f jetty-access.log | grep "HTTP/1.1\" 5"

# 查看错误日志
tail -f logs/sprival.log | grep ERROR
```

### 性能问题排查

```bash
# 查看当前活跃请求数
curl http://localhost:8338/api/actuator/metrics/http.server.requests.active

# 查看响应时间分布
curl http://localhost:8338/api/actuator/metrics/http.server.requests | grep duration

# 查看JVM线程状态
curl http://localhost:8338/api/actuator/metrics/jvm.threads.states
```

## 参考文档

- [Jetty配置详细说明](jetty.md) - Jetty的完整配置选项和高级用法
- [Spring监控模块](../spring-monitoring/README.md) - 完整的监控解决方案

---

*本模块提供了企业级的HTTP服务器解决方案，确保高性能、高安全性和高可用性。*