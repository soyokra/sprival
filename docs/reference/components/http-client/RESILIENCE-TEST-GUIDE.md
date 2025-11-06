# Feign 容错机制测试指南

本指南说明如何测试 Feign 客户端的熔断、重试、降级等容错机制。

## 测试环境

- **服务地址**: `http://127.0.0.1:8338/api`
- **Feign 客户端**: 调用本地服务接口，测试容错机制

## 架构说明

```
客户端接口 → ResilienceClientController
    ↓
服务层（容错） → ResilienceTestService (@CircuitBreaker, @Retry)
    ↓
Feign 客户端 → SprivalClient
    ↓
服务端接口 → ResilienceTestController（模拟失败场景）
```

## 测试接口说明

### 1. 熔断器测试

**客户端接口**: `GET /api/client/test/circuit-breaker`

**参数**:
- `shouldFail`: 是否失败（默认 false）
- `delay`: 延迟时间（毫秒，默认 0）

**测试步骤**:

1. **触发熔断器打开**
   ```bash
   # 连续调用 5-10 次失败场景
   curl "http://127.0.0.1:8338/api/client/test/circuit-breaker?shouldFail=true"
   ```

2. **观察熔断器状态**
   - 前几次：正常调用服务，返回异常
   - 失败率达到 50% 后：熔断器打开，直接返回降级响应
   - 降级响应的 `fallback` 字段为 `true`

3. **等待熔断器恢复**
   - 等待 30 秒（配置的 `wait-duration-in-open-state`）
   - 熔断器进入半开状态，允许少量请求尝试
   - 如果请求成功，熔断器关闭；否则继续打开

4. **测试成功场景**
   ```bash
   # 调用成功场景，熔断器关闭后正常工作
   curl "http://127.0.0.1:8338/api/client/test/circuit-breaker?shouldFail=false"
   ```

### 2. 重试测试

**客户端接口**: `GET /api/client/test/retry`

**参数**:
- `shouldFail`: 是否失败（默认 false）
- `failTimes`: 失败次数，前 N 次失败，第 N+1 次成功（默认 2）

**测试步骤**:

1. **测试重试成功**
   ```bash
   # 前 2 次失败，第 3 次成功
   curl "http://127.0.0.1:8338/api/client/test/retry?shouldFail=true&failTimes=2"
   ```
   - 观察日志，可以看到 3 次调用记录
   - 最终返回成功响应，`attemptCount=3`

2. **重置计数器**
   ```bash
   # 重置服务端的重试计数器
   curl "http://127.0.0.1:8338/api/test/resilience/reset"
   ```

3. **测试重试失败（触发降级）**
   ```bash
   # 失败次数 > 重试次数，触发降级
   curl "http://127.0.0.1:8338/api/client/test/retry?shouldFail=true&failTimes=5"
   ```
   - 重试 3 次后仍失败
   - 触发降级逻辑，返回 `fallback=true`

### 3. 超时测试

**客户端接口**: `GET /api/client/test/timeout`

**参数**:
- `delay`: 延迟时间（毫秒，默认 0）

**测试步骤**:

1. **测试正常响应（未超时）**
   ```bash
   # 延迟 2 秒，未超过读取超时（5 秒）
   curl "http://127.0.0.1:8338/api/client/test/timeout?delay=2000"
   ```

2. **测试超时降级**
   ```bash
   # 延迟 6 秒，超过读取超时（5 秒）
   curl "http://127.0.0.1:8338/api/client/test/timeout?delay=6000"
   ```
   - 请求超时后触发降级逻辑
   - 返回 `fallback=true`

### 4. 组合测试

**客户端接口**: `GET /api/client/test/combined`

**参数**:
- `shouldFail`: 是否失败（默认 true）

**测试说明**:
- 同时测试重试和熔断器的协作
- 先触发重试机制，重试失败后计入熔断器统计
- 观察多个容错机制如何协同工作

## 配置说明

### Feign 客户端配置

```properties
# 针对 sprival-service 的专用配置
feign.client.config.sprival-service.connect-timeout=2000
feign.client.config.sprival-service.read-timeout=5000
feign.client.config.sprival-service.logger-level=FULL
```

### Resilience4j 配置

**熔断器配置**:
```properties
# 失败率阈值 50%
resilience4j.circuitbreaker.instances.feign.failure-rate-threshold=50
# 熔断器打开后等待 30 秒
resilience4j.circuitbreaker.instances.feign.wait-duration-in-open-state=30s
# 滑动窗口大小 10
resilience4j.circuitbreaker.instances.feign.sliding-window-size=10
# 最小调用次数 5（统计失败率前需要至少 5 次调用）
resilience4j.circuitbreaker.instances.feign.minimum-number-of-calls=5
# 半开状态允许 3 次调用
resilience4j.circuitbreaker.instances.feign.permitted-number-of-calls-in-half-open-state=3
```

**重试配置**:
```properties
# 最大重试 3 次
resilience4j.retry.instances.feign.max-attempts=3
# 重试间隔 1 秒
resilience4j.retry.instances.feign.wait-duration=1s
```

## 监控指标

访问 Actuator 端点查看容错指标：

```bash
# 查看熔断器指标
curl http://127.0.0.1:8338/api/actuator/metrics/resilience4j.circuitbreaker.calls

# 查看重试指标
curl http://127.0.0.1:8338/api/actuator/metrics/resilience4j.retry.calls

# 查看 Feign 客户端指标
curl http://127.0.0.1:8338/api/actuator/metrics/feign.Client.requests
```

## 日志观察

启动应用后，观察日志输出：

```
# 重试日志
INFO  - 调用重试测试接口 - shouldFail: true, failTimes: 2
WARN  - 重试测试 - 第 1 次尝试失败（共需失败 2 次）
INFO  - 调用重试测试接口 - shouldFail: true, failTimes: 2
WARN  - 重试测试 - 第 2 次尝试失败（共需失败 2 次）
INFO  - 调用重试测试接口 - shouldFail: true, failTimes: 2
INFO  - 重试测试 - 第 3 次尝试成功，重置计数器

# 熔断器日志
ERROR - 熔断器降级 - 原因: 服务器错误: 熔断器测试：模拟服务异常
WARN  - 熔断器测试 - 触发降级逻辑（Fallback）

# Feign 调用日志
ERROR - Feign 调用失败 - 方法: SprivalClient#testCircuitBreaker(boolean,long), 状态码: 500, 原因: Server Error
```

## 常见问题

### 1. 熔断器不触发

**问题**: 调用多次失败后，熔断器仍未打开

**原因**:
- 调用次数未达到 `minimum-number-of-calls`（最小 5 次）
- 失败率未达到阈值（50%）

**解决**: 确保至少调用 5 次，且失败率超过 50%

### 2. 重试不生效

**问题**: 只调用了一次，没有重试

**原因**:
- 服务端的重试计数器没有重置
- `failTimes` 参数设置为 0

**解决**: 调用 `/api/test/resilience/reset` 重置计数器

### 3. 降级逻辑不执行

**问题**: 调用失败但没有返回降级响应

**原因**:
- Fallback 类没有被 Spring 管理（缺少 `@Component`）
- 配置中没有指定 `fallback` 参数

**解决**: 检查 `SprivalClientFallback` 是否有 `@Component` 注解

## 最佳实践

1. **测试前重置状态**
   - 调用 `/api/test/resilience/reset` 重置重试计数器
   - 等待熔断器关闭（30 秒后）

2. **循环测试熔断器**
   ```bash
   # 连续调用 10 次观察熔断器状态变化
   for i in {1..10}; do 
     echo "请求 $i:"
     curl -s "http://127.0.0.1:8338/api/client/test/circuit-breaker?shouldFail=true" | jq .
     sleep 1
   done
   ```

3. **观察日志**
   - 开启 DEBUG 日志级别：`logging.level.com.soyokra.sprival=DEBUG`
   - 查看 Feign 完整日志：`feign.client.config.sprival-service.logger-level=FULL`

4. **监控指标**
   - 使用 Prometheus + Grafana 可视化监控
   - 配置告警规则，熔断器打开时通知

## 参考资料

- [Resilience4j 官方文档](https://resilience4j.readme.io/docs)
- [Spring Cloud OpenFeign 文档](https://docs.spring.io/spring-cloud-openfeign/docs/current/reference/html/)
- [HTTP Client 模块文档](./README.md)

