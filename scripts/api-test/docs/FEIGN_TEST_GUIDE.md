# Feign 容错机制测试指南

本指南说明如何使用 api-test 框架测试 Feign 客户端的熔断、重试、降级等容错机制。

## 测试环境

- **服务地址**: `http://127.0.0.1:8338/api`
- **Feign 客户端**: 调用本地服务接口，测试容错机制

## 测试场景

### 1. 熔断器测试

#### 1.1 触发熔断器打开

**配置文件**: `configs/feign_circuit_breaker.json`

**测试步骤**:

```bash
# 1. 先重置计数器
curl "http://127.0.0.1:8338/api/feign/self-call/reset"

# 2. 运行测试，连续调用失败场景触发熔断
python main.py --config configs/feign_circuit_breaker.json
```

**预期结果**:
- 前几次：正常调用服务，返回异常
- 失败率达到 50% 后：熔断器打开，直接返回降级响应
- 降级响应的 `fallback` 字段为 `true`

**配置说明**:
- `shouldFail: true` - 模拟失败场景
- `duration: 120` - 运行 120 秒，确保有足够时间触发熔断
- `threads: 1` - 单线程，便于观察熔断器状态变化

#### 1.2 测试成功场景（熔断器关闭后）

**配置文件**: `configs/feign_circuit_breaker_success.json`

**测试步骤**:

```bash
# 等待熔断器恢复（30秒后）
# 熔断器进入半开状态，允许少量请求尝试
# 如果请求成功，熔断器关闭；否则继续打开

python main.py --config configs/feign_circuit_breaker_success.json
```

**预期结果**:
- 如果熔断器已关闭：正常调用服务，返回成功响应
- 如果熔断器处于半开状态：允许请求通过，成功后关闭熔断器

### 2. 重试测试

#### 2.1 重试成功场景

**配置文件**: `configs/feign_retry.json`

**测试步骤**:

```bash
# 1. 重置计数器
curl "http://127.0.0.1:8338/api/feign/self-call/reset"

# 2. 运行测试，前 2 次失败，第 3 次成功
python main.py --config configs/feign_retry.json
```

**预期结果**:
- 观察日志，可以看到 3 次调用记录
- 最终返回成功响应，`attemptCount=3`
- `fallback=false`

**配置说明**:
- `shouldFail: true` - 启用失败模式
- `failTimes: 2` - 前 2 次失败，第 3 次成功
- Resilience4j 配置：`max-attempts=3`，所以会重试 3 次

#### 2.2 重试失败场景（触发降级）

**配置文件**: `configs/feign_retry_failure.json`

**测试步骤**:

```bash
# 1. 重置计数器
curl "http://127.0.0.1:8338/api/feign/self-call/reset"

# 2. 运行测试，失败次数 > 重试次数，触发降级
python main.py --config configs/feign_retry_failure.json
```

**预期结果**:
- 重试 3 次后仍失败
- 触发降级逻辑，返回 `fallback=true`
- 响应消息包含"降级"关键字

**配置说明**:
- `failTimes: 5` - 失败次数超过重试次数（3次）
- 重试机制失效后，触发 Fallback 降级

### 3. 超时测试

#### 3.1 正常响应（未超时）

**配置文件**: `configs/feign_timeout.json`

**测试步骤**:

```bash
python main.py --config configs/feign_timeout.json
```

**预期结果**:
- 延迟 2 秒，未超过读取超时（5 秒）
- 返回成功响应
- `fallback=false`

**配置说明**:
- `delay: 2000` - 延迟 2 秒
- Feign 配置：`read-timeout=5000`（5秒）

#### 3.2 超时降级

**配置文件**: `configs/feign_timeout_exceeded.json`

**测试步骤**:

```bash
python main.py --config configs/feign_timeout_exceeded.json
```

**预期结果**:
- 延迟 6 秒，超过读取超时（5 秒）
- 请求超时后触发降级逻辑
- 返回 `fallback=true`

**配置说明**:
- `delay: 6000` - 延迟 6 秒
- 超过 Feign 读取超时时间，触发超时异常
- Resilience4j 捕获超时异常，触发降级

### 4. 组合测试

**配置文件**: `configs/feign_combined.json`

**测试步骤**:

```bash
# 1. 重置计数器
curl "http://127.0.0.1:8338/api/feign/self-call/reset"

# 2. 运行组合测试
python main.py --config configs/feign_combined.json
```

**预期结果**:
- 同时测试重试和熔断器的协作
- 先触发重试机制，重试失败后计入熔断器统计
- 观察多个容错机制如何协同工作

**配置说明**:
- `shouldFail: true` - 启用失败模式
- 内部使用 `failTimes: 5`，确保重试失败
- 多次失败后触发熔断器

## 测试流程建议

### 完整测试流程

1. **重置状态**
   ```bash
   curl "http://127.0.0.1:8338/api/feign/self-call/reset"
   ```

2. **测试重试机制**
   ```bash
   # 重试成功场景
   python main.py --config configs/feign_retry.json
   
   # 重置计数器
   curl "http://127.0.0.1:8338/api/feign/self-call/reset"
   
   # 重试失败场景
   python main.py --config configs/feign_retry_failure.json
   ```

3. **测试熔断器**
   ```bash
   # 重置计数器
   curl "http://127.0.0.1:8338/api/feign/self-call/reset"
   
   # 触发熔断器打开
   python main.py --config configs/feign_circuit_breaker.json
   
   # 等待 30 秒后测试成功场景
   sleep 30
   python main.py --config configs/feign_circuit_breaker_success.json
   ```

4. **测试超时**
   ```bash
   # 正常响应
   python main.py --config configs/feign_timeout.json
   
   # 超时降级
   python main.py --config configs/feign_timeout_exceeded.json
   ```

5. **组合测试**
   ```bash
   # 重置计数器
   curl "http://127.0.0.1:8338/api/feign/self-call/reset"
   
   # 组合测试
   python main.py --config configs/feign_combined.json
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

## 配置说明

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

### Feign 配置

```properties
# 针对 sprival-service 的专用配置
feign.client.config.sprival-service.connect-timeout=2000
feign.client.config.sprival-service.read-timeout=5000
feign.client.config.sprival-service.logger-level=FULL
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

**解决**: 调用 `/api/feign/self-call/reset` 重置计数器

### 3. 降级逻辑不执行

**问题**: 调用失败但没有返回降级响应

**原因**:
- Fallback 类没有被 Spring 管理（缺少 `@Component`）
- 配置中没有指定 `fallback` 参数

**解决**: 检查 `SprivalClientFallback` 是否有 `@Component` 注解

## 参考资料

- [Resilience4j 官方文档](https://resilience4j.readme.io/docs)
- [Spring Cloud OpenFeign 文档](https://docs.spring.io/spring-cloud-openfeign/docs/current/reference/html/)
- [HTTP Client 模块文档](../../docs/reference/components/http-client/README.md)

