# 监控数据测试方案

## 1. 测试目标

设计一套测试接口和压测方案，通过不同的测试场景来触发和观测各个监控面板的数据，验证监控系统的有效性。

### 监控面板覆盖

本方案将完整验证以下 5 个监控面板：

1. **HikariCP 连接池监控** - 数据库连接池性能
2. **Jetty HTTP Server 监控** - Web 服务器性能
3. **RabbitMQ 消息队列监控** - 消息发布和消费
4. **JVM 性能监控** - 内存、GC、线程等
5. **Lettuce Redis 客户端监控** - Redis 命令和缓存性能

---

## 2. 测试接口设计

### 2.1 数据库连接池测试（HikariCP）

#### 接口1：批量查询
```http
GET /api/monitor-test/db/query?count=100&delay=0
```
- **功能**：批量执行数据库查询
- **参数**：
  - `count`: 查询次数（默认 100）
  - `delay`: 每次查询间隔毫秒数（默认 0）
- **触发指标**：
  - `hikaricp_connections_active` - 活动连接数
  - `hikaricp_connections_acquire_seconds` - 获取连接延迟
  - `hikaricp_connections_usage_seconds` - 连接使用时长

#### 接口2：慢查询测试
```http
GET /api/monitor-test/db/slow-query?timeout=5000
```
- **功能**：执行慢查询（模拟长时间占用连接）
- **参数**：
  - `timeout`: 查询耗时毫秒数（默认 5000）
- **触发指标**：
  - `hikaricp_connections_pending` - 等待连接的线程数
  - `hikaricp_connections_timeout_total` - 连接超时计数
  - 连接池利用率接近 100%

#### 接口3：连接泄露模拟
```http
GET /api/monitor-test/db/connection-leak
```
- **功能**：模拟连接泄露（获取但不释放连接）
- **触发指标**：
  - `hikaricp_connections_active` 持续增长
  - `hikaricp_connections_idle` 减少到 0
  - 连接池健康度下降

---

### 2.2 HTTP Server 测试（Jetty）

#### 接口4：快速响应
```http
GET /api/monitor-test/http/fast
```
- **功能**：快速响应接口（<10ms）
- **触发指标**：
  - `http_server_requests_seconds` - 请求处理时间
  - `jetty_threads_busy` - 活动线程数
  - `jetty_connections_current_connections` - 当前连接数

#### 接口5：慢响应
```http
GET /api/monitor-test/http/slow?delay=2000
```
- **功能**：慢响应接口
- **参数**：
  - `delay`: 响应延迟毫秒数（默认 2000）
- **触发指标**：
  - `http_server_requests_seconds_max` - 最大请求延迟
  - `jetty_threads_jobs` - 等待任务队列
  - 线程池利用率上升

#### 接口6：HTTP 错误
```http
GET /api/monitor-test/http/error?type=400
GET /api/monitor-test/http/error?type=500
```
- **功能**：触发 HTTP 错误
- **参数**：
  - `type`: 错误状态码（400 或 500）
- **触发指标**：
  - `http_server_requests_seconds{status="4xx"}` - 客户端错误
  - `http_server_requests_seconds{status="5xx"}` - 服务端错误

#### 接口7：大数据传输
```http
POST /api/monitor-test/http/large-payload
Content-Type: application/json
Body: { "data": "large data..." }
```
- **功能**：大请求体测试（触发 HTTP 压缩）
- **触发指标**：
  - `jetty_connections_bytes_in_bytes` - 入站字节数
  - `jetty_connections_bytes_out_bytes` - 出站字节数
  - 评估压缩效果

---

### 2.3 RabbitMQ 消息队列测试

#### 接口8：批量发布消息
```http
POST /api/monitor-test/mq/publish?count=100&routingKey=test.queue
```
- **功能**：批量发布消息到 RabbitMQ
- **参数**：
  - `count`: 消息数量（默认 100）
  - `routingKey`: 路由键（默认 test.queue）
- **触发指标**：
  - `rabbitmq_published_total` - 发布消息总数
  - `rabbitmq_acknowledged_published_total` - 确认发布总数
  - 发布确认率

#### 接口9：发布失败模拟
```http
GET /api/monitor-test/mq/publish-fail
```
- **功能**：模拟发布失败（使用错误的 exchange 或路由键）
- **触发指标**：
  - `rabbitmq_failed_to_publish_total` - 发布失败总数
  - `rabbitmq_unrouted_published_total` - 无法路由的消息
  - 发布失败率上升

#### 接口10：消费统计
```http
GET /api/monitor-test/mq/consume-stats
```
- **功能**：查看消费统计信息
- **触发指标**：
  - `rabbitmq_consumed_total` - 消费消息总数
  - `rabbitmq_acknowledged_total` - 确认消费总数
  - `rabbitmq_rejected_total` - 拒绝消息总数
  - 消费确认率

---

### 2.4 JVM 性能测试

#### 接口11：内存分配
```http
GET /api/monitor-test/jvm/memory-allocation?size=10MB&count=100
```
- **功能**：内存分配测试
- **参数**：
  - `size`: 每次分配大小（支持 KB/MB，默认 10MB）
  - `count`: 分配次数（默认 100）
- **触发指标**：
  - `jvm_memory_used_bytes` - 已使用内存
  - `jvm_gc_memory_allocated_bytes_total` - 已分配内存总量
  - 堆内存使用率上升

#### 接口12：触发 GC
```http
GET /api/monitor-test/jvm/trigger-gc
```
- **功能**：主动触发垃圾回收
- **触发指标**：
  - `jvm_gc_pause_seconds` - GC 暂停时间
  - `jvm_gc_overhead_percent` - GC 开销百分比
  - `jvm_gc_pause_seconds_count` - GC 次数增加

#### 接口13：创建线程
```http
GET /api/monitor-test/jvm/create-threads?count=50&duration=60000
```
- **功能**：创建线程
- **参数**：
  - `count`: 线程数量（默认 50）
  - `duration`: 线程存活时间毫秒数（默认 60000）
- **触发指标**：
  - `jvm_threads_live_threads` - 活动线程数
  - `jvm_threads_states_threads` - 各状态线程分布
  - `jvm_threads_peak_threads` - 峰值线程数

#### 接口14：CPU 密集计算
```http
GET /api/monitor-test/jvm/cpu-intensive?duration=5000
```
- **功能**：CPU 密集型计算
- **参数**：
  - `duration`: 持续时间毫秒数（默认 5000）
- **触发指标**：
  - `process_cpu_usage` - 进程 CPU 使用率
  - `system_cpu_usage` - 系统 CPU 使用率
  - CPU 使用率峰值

---

### 2.5 Redis/Cache 缓存测试

#### 接口15：缓存读取
```http
GET /api/monitor-test/cache/read?key=test&cacheName=user
```
- **功能**：从缓存读取数据
- **参数**：
  - `key`: 缓存键
  - `cacheName`: 缓存名称（默认 user）
- **触发指标**：
  - `cache_gets_total{result="hit"}` - 缓存命中
  - `cache_gets_total{result="miss"}` - 缓存未命中
  - 缓存命中率

#### 接口16：缓存写入
```http
POST /api/monitor-test/cache/write?key=test&value=data&cacheName=user
```
- **功能**：向缓存写入数据
- **参数**：
  - `key`: 缓存键
  - `value`: 缓存值
  - `cacheName`: 缓存名称（默认 user）
- **触发指标**：
  - `cache_puts_total` - 缓存写入总数

#### 接口17：缓存清除
```http
DELETE /api/monitor-test/cache/evict?key=test&cacheName=user
```
- **功能**：清除指定缓存
- **参数**：
  - `key`: 缓存键
  - `cacheName`: 缓存名称
- **触发指标**：
  - `cache_removals_total` - 缓存移除总数

#### 接口18：Redis 命令测试
```http
GET /api/monitor-test/redis/commands?command=GET&count=1000
GET /api/monitor-test/redis/commands?command=SET&count=1000
GET /api/monitor-test/redis/commands?command=HGET&count=1000
```
- **功能**：批量执行 Redis 命令
- **参数**：
  - `command`: 命令类型（GET/SET/HGET/HSET/ZADD 等）
  - `count`: 执行次数（默认 1000）
- **触发指标**：
  - `lettuce_command_completion_seconds` - 命令完成延迟
  - `lettuce_command_firstresponse_seconds` - 首次响应延迟
  - 按命令类型分组的吞吐量

---

## 3. 压测场景设计

### 场景1：基础健康检查 ✅

**目标**：验证所有指标正常采集，轻量级压测

```bash
#!/bin/bash
# 场景1：基础健康检查
BASE_URL="http://localhost:8338/api/monitor-test"

echo "=== 场景1：基础健康检查 ==="
for i in {1..10}; do
  echo "Round $i..."
  curl -s "$BASE_URL/http/fast" > /dev/null
  curl -s "$BASE_URL/db/query?count=5" > /dev/null
  curl -s "$BASE_URL/cache/read?key=test$i" > /dev/null
  sleep 1
done

echo "✅ 基础健康检查完成，请查看 Grafana 各面板是否有数据"
```

**预期结果**：
- 所有 5 个监控面板都有数据显示
- 各项指标在健康范围内（绿色）

---

### 场景2：高并发压测 🔥

**目标**：测试 HTTP 服务器高并发处理能力

```bash
#!/bin/bash
# 场景2：高并发压测
echo "=== 场景2：HTTP 高并发压测 ==="

# 使用 Apache Bench
ab -n 10000 -c 100 \
   http://localhost:8338/api/monitor-test/http/fast

# 或使用 wrk
# wrk -t 10 -c 100 -d 60s \
#     http://localhost:8338/api/monitor-test/http/fast
```

**观测指标**：
- `jetty_threads_busy` - 活动线程数应该上升
- `http_server_requests_seconds_count` - QPS 统计
- `jetty_connections_current_connections` - 当前连接数
- 平均响应延迟应保持 <50ms

**预期结果**：
- QPS 达到 1000+ (取决于硬件)
- 线程池利用率 <80%
- 无 HTTP 5xx 错误

---

### 场景3：连接池压力测试 💥

**目标**：测试数据库连接池在压力下的表现

```bash
#!/bin/bash
# 场景3：连接池压力测试
echo "=== 场景3：连接池压力测试 ==="

# 并发触发慢查询，耗尽连接池
ab -n 50 -c 30 \
   "http://localhost:8338/api/monitor-test/db/slow-query?timeout=3000"
```

**观测指标**：
- `hikaricp_connections_pending` - 等待线程数应该 >0
- `hikaricp_connections_active` - 接近 max (20)
- 连接池利用率应该 >90%（红色告警）
- `hikaricp_connections_acquire_seconds` - 获取连接延迟增加

**预期结果**：
- 连接池健康度指标变为黄色或红色
- 部分请求可能超时
- 压测结束后连接池恢复正常

---

### 场景4：内存压力测试 🧠

**目标**：触发内存分配和 GC，观测 JVM 性能

```bash
#!/bin/bash
# 场景4：内存压力测试
echo "=== 场景4：JVM 内存压力测试 ==="

for i in {1..20}; do
  echo "Memory allocation round $i..."
  curl -s "http://localhost:8338/api/monitor-test/jvm/memory-allocation?size=50MB&count=10" > /dev/null
  sleep 1
done

echo "触发 GC..."
curl -s "http://localhost:8338/api/monitor-test/jvm/trigger-gc" > /dev/null
```

**观测指标**：
- `jvm_memory_used_bytes` - 堆内存使用量上升
- `jvm_gc_pause_seconds` - GC 暂停时间
- `jvm_gc_pause_seconds_count` - GC 次数增加
- `jvm_gc_overhead_percent` - GC 开销百分比
- 堆内存使用率应该 >70%（黄色）

**预期结果**：
- 内存使用率上升到 70-85%
- 触发多次 GC
- GC 后内存使用率下降
- 无 OutOfMemoryError

---

### 场景5：缓存性能测试 📦

**目标**：测试缓存命中率和性能

```bash
#!/bin/bash
# 场景5：缓存性能测试
BASE_URL="http://localhost:8338/api/monitor-test/cache"

echo "=== 场景5：缓存性能测试 ==="

# 步骤1：写入缓存
echo "步骤1：写入 100 个缓存..."
for i in {1..100}; do
  curl -s -X POST "$BASE_URL/write?key=key$i&value=value$i&cacheName=user" > /dev/null
done

# 步骤2：读取缓存（应该全部命中）
echo "步骤2：读取缓存（命中测试）..."
for i in {1..100}; do
  curl -s "$BASE_URL/read?key=key$i&cacheName=user" > /dev/null
done

# 步骤3：读取不存在的缓存（应该全部未命中）
echo "步骤3：读取不存在缓存（未命中测试）..."
for i in {101..200}; do
  curl -s "$BASE_URL/read?key=key$i&cacheName=user" > /dev/null
done

echo "✅ 缓存测试完成"
```

**观测指标**：
- `cache_gets_total{result="hit"}` - 命中次数应为 100
- `cache_gets_total{result="miss"}` - 未命中次数应为 100
- 缓存命中率应为 50%
- `cache_puts_total` - 写入次数应为 100

**预期结果**：
- 步骤2 命中率 100%
- 步骤3 命中率 0%
- 总体命中率 50%

---

### 场景6：消息队列压测 📨

**目标**：测试 RabbitMQ 消息发布和消费

```bash
#!/bin/bash
# 场景6：消息队列压测
BASE_URL="http://localhost:8338/api/monitor-test/mq"

echo "=== 场景6：RabbitMQ 消息队列压测 ==="

# 批量发布消息
echo "发布 1000 条消息..."
curl -s -X POST "$BASE_URL/publish?count=1000&routingKey=test.queue" > /dev/null

# 等待消费
echo "等待消费..."
sleep 5

# 查看消费统计
curl -s "$BASE_URL/consume-stats"

# 测试发布失败
echo "测试发布失败..."
curl -s "$BASE_URL/publish-fail" > /dev/null
```

**观测指标**：
- `rabbitmq_published_total` - 发布消息数应增加 1000
- `rabbitmq_acknowledged_published_total` - 确认数应接近 1000
- 发布确认率应 >99%（绿色）
- `rabbitmq_consumed_total` - 消费消息数
- `rabbitmq_failed_to_publish_total` - 发布失败应增加

**预期结果**：
- 发布成功率 >99%
- 消费成功率 >95%
- 发布失败测试时失败率上升

---

## 4. 测试工具准备

### 4.1 压测工具

#### Apache Bench (ab)
```bash
# 安装（Ubuntu/Debian）
sudo apt-get install apache2-utils

# 安装（macOS）
brew install ab

# 基本用法
ab -n 10000 -c 100 http://localhost:8338/api/monitor-test/http/fast
# -n: 总请求数
# -c: 并发数
```

#### wrk
```bash
# 安装（Ubuntu/Debian）
sudo apt-get install wrk

# 安装（macOS）
brew install wrk

# 基本用法
wrk -t 10 -c 100 -d 60s http://localhost:8338/api/monitor-test/http/fast
# -t: 线程数
# -c: 连接数
# -d: 持续时间
```

#### JMeter
- 下载：https://jmeter.apache.org/download_jmeter.cgi
- GUI 界面配置测试计划
- 支持复杂场景和结果分析

#### Gatling
- 下载：https://gatling.io/open-source/
- Scala DSL 编写测试脚本
- 强大的报告功能

### 4.2 监控观测工具

#### Grafana Dashboard
- 访问：http://localhost:3000
- 导入已创建的 5 个监控面板
- 实时观测指标变化

#### Prometheus 查询
- 访问：http://localhost:9090
- 直接查询指标验证数据
- 示例查询：
  ```promql
  rate(http_server_requests_seconds_count[1m])
  hikaricp_connections_active
  cache_gets_total
  ```

#### 应用日志
- 查看应用日志辅助分析
- 日志位置：`logs/sprival.log`

---

## 5. 验证清单

### 5.1 HikariCP 连接池监控 ✅

- [ ] **健康度指标**
  - 连接池健康度显示正常（绿色）
  - 活动连接数在合理范围（<80%）
  
- [ ] **连接使用**
  - 活动连接数随压测变化
  - 空闲连接数正确显示
  - 总连接数 = 活动 + 空闲
  
- [ ] **连接获取**
  - 平均获取延迟 <10ms
  - P95/P99 延迟在合理范围
  - 慢查询时 pending 数量增加
  
- [ ] **性能指标**
  - 连接获取速率统计准确
  - 连接创建速率显示正常
  - 利用率计算正确

---

### 5.2 Jetty HTTP Server 监控 ✅

- [ ] **健康度指标**
  - 服务器健康度正常（绿色）
  - 线程利用率 <70%
  
- [ ] **请求统计**
  - QPS 统计准确
  - 按状态码分组正确
  - 错误率计算准确
  
- [ ] **线程池**
  - 活动线程数正确
  - 空闲线程数显示
  - 等待任务数监控
  
- [ ] **延迟监控**
  - 平均响应延迟 <100ms
  - 最大延迟识别尖刺
  - 慢接口时延迟上升
  
- [ ] **连接监控**
  - 当前连接数统计
  - 字节流量监控
  - 压缩效果可见

---

### 5.3 RabbitMQ 消息队列监控 ✅

- [ ] **健康度指标**
  - 连接数显示正常
  - 通道数统计正确
  
- [ ] **发布监控**
  - 发布确认率 >99%（绿色）
  - 发布速率统计准确
  - 发布失败时失败率上升
  - 无法路由消息监控
  
- [ ] **消费监控**
  - 消费确认率 >95%（绿色）
  - 消费速率统计准确
  - 拒绝消息监控
  
- [ ] **错误监控**
  - 发布失败数量
  - 未确认发布数量
  - 消息拒绝数量

---

### 5.4 JVM 性能监控 ✅

- [ ] **内存监控**
  - 堆内存使用率正常
  - 已使用/已提交/最大值显示
  - GC 后内存使用率监控
  - 内存泄露识别
  
- [ ] **GC 监控**
  - GC 暂停时间合理 (<200ms)
  - GC 频率统计准确
  - GC 开销百分比监控
  - 内存分配速率
  - 晋升速率
  
- [ ] **线程监控**
  - 活动线程数统计
  - 守护线程数显示
  - 峰值线程数记录
  - 线程状态分布
  
- [ ] **CPU 监控**
  - 进程 CPU 使用率
  - 系统 CPU 使用率
  - 进程运行时间
  
- [ ] **类加载**
  - 已加载类数
  - 类卸载速率
  
- [ ] **Buffer**
  - Buffer 数量
  - Buffer 内存使用

---

### 5.5 Lettuce Redis 监控 ✅

- [ ] **健康度指标**
  - 命令吞吐量统计
  - 平均延迟 <10ms（绿色）
  - 最大延迟监控
  
- [ ] **延迟分布**
  - P50/P95/P99 延迟显示
  - 按命令类型分组
  - 首次响应延迟
  - 命令完成延迟
  
- [ ] **命令分析**
  - 各命令类型吞吐量
  - 各命令类型延迟对比
  - 热点命令识别
  - 慢命令识别
  
- [ ] **缓存监控**
  - 缓存命中率 >80%（绿色）
  - 命中/未命中速率
  - Gets/Puts/Removals 统计
  - 按缓存名称分组
  
- [ ] **连接监控**
  - 按端点延迟分析
  - 网络延迟 vs 处理延迟

---

## 6. 实施步骤

### 步骤1：环境准备
1. 确保 Spring Boot 应用正常运行
2. 确保 Prometheus 正常采集指标
3. 确保 Grafana 可以访问并导入 Dashboard
4. 安装压测工具（ab 或 wrk）

### 步骤2：创建测试接口
1. 创建 `MonitorTestController.java`
2. 实现所有 18 个测试接口
3. 启动应用并验证接口可访问
   ```bash
   curl http://localhost:8338/api/monitor-test/http/fast
   ```

### 步骤3：配置监控
1. 导入 5 个 Grafana Dashboard JSON 文件
2. 验证所有面板可以访问
3. 检查数据源配置正确

### 步骤4：执行基础测试
1. 运行场景1（基础健康检查）
2. 在 Grafana 中验证所有面板都有数据
3. 确认指标在健康范围内

### 步骤5：执行压测场景
1. 按顺序执行场景2-6
2. 实时观测 Grafana 面板
3. 记录关键指标数值
4. 验证告警阈值是否合理

### 步骤6：结果分析
1. 检查所有验证清单项
2. 识别性能瓶颈
3. 调整告警阈值
4. 优化配置参数

### 步骤7：文档记录
1. 记录测试结果
2. 记录性能基线
3. 更新配置文档
4. 编写问题和改进建议

---

## 7. 预期性能基线

基于典型配置的预期指标：

| 指标类别 | 指标名称 | 预期值 | 告警阈值 |
|---------|---------|--------|---------|
| **HikariCP** | 连接池利用率 | <70% | >90% |
| | 平均获取延迟 | <5ms | >50ms |
| | 等待线程数 | 0 | >0 |
| **Jetty** | 线程池利用率 | <70% | >90% |
| | 平均响应延迟 | <50ms | >500ms |
| | QPS | 1000+ | - |
| **RabbitMQ** | 发布确认率 | >99% | <95% |
| | 消费确认率 | >95% | <90% |
| | 发布失败率 | 0% | >1% |
| **JVM** | 堆内存使用率 | <70% | >85% |
| | GC 平均暂停 | <50ms | >200ms |
| | 进程 CPU | <70% | >90% |
| **Lettuce** | 命令平均延迟 | <5ms | >50ms |
| | 缓存命中率 | >80% | <60% |

---

## 8. 常见问题

### Q1: 测试接口响应 404
**原因**：接口未实现或路径错误  
**解决**：检查 Controller 是否正确创建，确认 context-path 配置

### Q2: Grafana 面板无数据
**原因**：Prometheus 未采集到指标  
**解决**：
1. 访问 `/actuator/prometheus` 确认指标暴露
2. 检查 Prometheus 配置和 targets 状态
3. 验证指标名称是否正确

### Q3: 压测时应用崩溃
**原因**：资源不足或配置不当  
**解决**：
1. 降低并发数
2. 增加 JVM 堆内存
3. 调整连接池大小
4. 检查错误日志

### Q4: 缓存命中率为 0
**原因**：缓存未启用或配置错误  
**解决**：
1. 确认 `spring.cache.type=redis`
2. 检查 Redis 连接
3. 验证 `@Cacheable` 注解使用

### Q5: RabbitMQ 指标不更新
**原因**：消息未被消费或监听器未启动  
**解决**：
1. 确认消费者监听器正常运行
2. 检查队列绑定关系
3. 验证 RabbitMQ 连接状态

---

## 9. 总结

本测试方案提供了：
- ✅ 18 个测试接口，覆盖 5 大监控面板
- ✅ 6 个压测场景，从基础到高级
- ✅ 完整的验证清单，确保监控有效
- ✅ 性能基线参考，便于对比分析

通过执行本方案，可以：
1. 验证监控系统完整性和准确性
2. 建立性能基线数据
3. 识别系统瓶颈和优化点
4. 调整告警阈值配置
5. 为生产环境监控提供参考

---

**文档版本**: 1.0  
**创建日期**: 2025-11-03  
**更新日期**: 2025-11-03

