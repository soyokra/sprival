# 独立压力测试工具使用指南

## 概述

独立压力测试工具允许您在应用已启动的情况下，通过命令行执行各种流量场景的压力测试。与Spring Boot集成测试不同，这种方式可以：

- ✅ 观察真实的Actuator/Prometheus监控数据
- ✅ 不干扰应用运行
- ✅ 更接近真实压测场景
- ✅ 支持多种复杂流量模式
- ✅ 可用于生产环境验证

## 前提条件

1. **应用已启动**
   ```bash
   # 启动应用
   mvn spring-boot:run
   # 或
   java -jar target/sprival-0.0.1.jar
   ```

2. **数据库已连接**
   - MySQL运行在 localhost:33306
   - 数据库sprival已创建
   - order_tbl表已存在

## 快速开始

### 方式1：稳定流量测试（最常用）

```bash
mvn exec:java -Dexec.mainClass="com.soyokra.sprival.loadtest.standalone.StandaloneLoadTester" \
  -Dexec.args="--url http://localhost:8338/api/order/insert --scenario steady --threads 20 --duration 120"
```

### 方式2：突发流量测试（秒杀场景）

```bash
mvn exec:java -Dexec.mainClass="com.soyokra.sprival.loadtest.standalone.StandaloneLoadTester" \
  -Dexec.args="--url http://localhost:8338/api/order/insert --scenario burst --peak-threads 100 --burst-duration 60"
```

### 方式3：渐进增长测试（寻找临界点）

```bash
mvn exec:java -Dexec.mainClass="com.soyokra.sprival.loadtest.standalone.StandaloneLoadTester" \
  -Dexec.args="--url http://localhost:8338/api/order/insert --scenario rampup --max-threads 100 --step 10 --step-duration 30"
```

## 测试场景详解

### 1. 稳定流量 (Steady Traffic)

**特点**：固定并发数，持续稳定发送请求

**适用场景**：
- 基准性能测试
- 系统容量评估
- 对比测试

**参数**：
```bash
--scenario steady
--threads <NUM>          # 并发线程数（默认：10）
--duration <SECONDS>     # 测试持续时间（默认：60）
--warmup <SECONDS>       # 预热时间（默认：10）
```

**示例**：
```bash
# 20并发，持续5分钟
mvn exec:java -Dexec.mainClass="com.soyokra.sprival.loadtest.standalone.StandaloneLoadTester" \
  -Dexec.args="--url http://localhost:8338/api/order/insert --scenario steady --threads 20 --duration 300"
```

---

### 2. 突发流量 (Burst Traffic)

**特点**：短时间快速达到峰值并发，模拟秒杀

**适用场景**：
- 秒杀活动测试
- 促销高峰测试
- 瞬时峰值压力测试

**参数**：
```bash
--scenario burst
--peak-threads <NUM>      # 峰值并发数（默认：100）
--burst-duration <SEC>    # 突发持续时间（默认：30）
--ramp-up-time <SEC>      # 达到峰值的时间（默认：5）
```

**示例**：
```bash
# 200并发突发流量，持续1分钟
mvn exec:java -Dexec.mainClass="com.soyokra.sprival.loadtest.standalone.StandaloneLoadTester" \
  -Dexec.args="--url http://localhost:8338/api/order/insert --scenario burst --peak-threads 200 --burst-duration 60"
```

---

### 3. 渐进增长 (Ramp Up Traffic)

**特点**：并发数逐步增加，找到性能临界点

**适用场景**：
- 寻找系统性能临界点
- 容量规划
- 性能瓶颈分析

**参数**：
```bash
--scenario rampup
--start-threads <NUM>     # 起始并发数（默认：1）
--max-threads <NUM>       # 最大并发数（默认：100）
--step <NUM>              # 每次增加线程数（默认：10）
--step-duration <SEC>     # 每阶段持续时间（默认：30）
```

**示例**：
```bash
# 从10并发逐步增加到200，每次增加20，每阶段1分钟
mvn exec:java -Dexec.mainClass="com.soyokra.sprival.loadtest.standalone.StandaloneLoadTester" \
  -Dexec.args="--url http://localhost:8338/api/order/insert --scenario rampup --start-threads 10 --max-threads 200 --step 20 --step-duration 60"
```

---

### 4. 脉冲流量 (Spike Traffic)

**特点**：高低并发交替，测试快速恢复能力

**适用场景**：
- 测试系统弹性恢复能力
- 验证自动扩缩容
- 不稳定流量场景

**参数**：
```bash
--scenario spike
--high-threads <NUM>      # 高并发数（默认：50）
--low-threads <NUM>       # 低并发数（默认：5）
--spike-duration <SEC>    # 每次脉冲持续时间（默认：20）
--cycles <NUM>            # 脉冲次数（默认：5）
```

**示例**：
```bash
# 高并发100与低并发10交替，每次30秒，共10个周期
mvn exec:java -Dexec.mainClass="com.soyokra.sprival.loadtest.standalone.StandaloneLoadTester" \
  -Dexec.args="--url http://localhost:8338/api/order/insert --scenario spike --high-threads 100 --low-threads 10 --spike-duration 30 --cycles 10"
```

---

### 5. 持久性测试 (Endurance Traffic)

**特点**：中等并发长时间运行，发现内存泄漏

**适用场景**：
- 稳定性测试
- 内存泄漏检测
- 长期运行验证

**参数**：
```bash
--scenario endurance
--threads <NUM>           # 并发线程数（默认：10）
--duration <SECONDS>      # 测试持续时间（默认：3600，即1小时）
```

**示例**：
```bash
# 20并发，持续运行3小时
mvn exec:java -Dexec.mainClass="com.soyokra.sprival.loadtest.standalone.StandaloneLoadTester" \
  -Dexec.args="--url http://localhost:8338/api/order/insert --scenario endurance --threads 20 --duration 10800"
```

---

## 命令行参数完整列表

### 必填参数

| 参数 | 说明 | 示例 |
|------|------|------|
| `--url` | 目标URL | `http://localhost:8338/api/order/insert` |
| `--scenario` | 测试场景 | `steady`, `burst`, `rampup`, `spike`, `endurance` |

### 通用可选参数

| 参数 | 说明 | 默认值 |
|------|------|--------|
| `--threads` | 并发线程数 | 10 |
| `--duration` | 测试持续时间（秒） | 60 |
| `--warmup` | 预热时间（秒） | 10 |
| `--report-file` | 报告输出文件路径 | 无 |
| `--interval` | 实时报告输出间隔（秒） | 10 |

### 场景特定参数

**Burst场景**：
- `--peak-threads`：峰值并发数（默认：100）
- `--burst-duration`：突发持续时间（默认：30）
- `--ramp-up-time`：达到峰值时间（默认：5）

**RampUp场景**：
- `--start-threads`：起始并发数（默认：1）
- `--max-threads`：最大并发数（默认：100）
- `--step`：每次增加线程数（默认：10）
- `--step-duration`：每阶段持续时间（默认：30）

**Spike场景**：
- `--high-threads`：高并发数（默认：50）
- `--low-threads`：低并发数（默认：5）
- `--spike-duration`：脉冲持续时间（默认：20）
- `--cycles`：脉冲次数（默认：5）

---

## 测试报告

### 控制台输出

测试完成后会输出类似以下报告：

```
========================================
Load Test Report
========================================
Test Duration: 120.00 seconds
Concurrent Threads: 20
----------------------------------------
Total Requests: 30,456
Successful Requests: 30,401 (99.8%)
Failed Requests: 55 (0.2%)
----------------------------------------
TPS: 253.80 requests/sec
Avg Response Time: 38.5 ms
Min Response Time: 12 ms
Max Response Time: 523 ms
----------------------------------------
P50 Response Time: 35 ms
P90 Response Time: 68 ms
P95 Response Time: 92 ms
P99 Response Time: 156 ms
========================================
```

### 导出到文件

使用 `--report-file` 参数将报告导出为JSON：

```bash
mvn exec:java -Dexec.mainClass="com.soyokra.sprival.loadtest.standalone.StandaloneLoadTester" \
  -Dexec.args="--url http://localhost:8338/api/order/insert --scenario steady --threads 20 --duration 120 --report-file result.json"
```

---

## 配合监控使用

### 1. 启动应用并访问监控端点

```bash
# 查看Prometheus metrics
curl http://localhost:8338/actuator/prometheus

# 查看应用health
curl http://localhost:8338/actuator/health

# 查看metrics列表
curl http://localhost:8338/actuator/metrics
```

### 2. 在另一个终端运行压力测试

```bash
mvn exec:java -Dexec.mainClass="com.soyokra.sprival.loadtest.standalone.StandaloneLoadTester" \
  -Dexec.args="--url http://localhost:8338/api/order/insert --scenario steady --threads 20 --duration 300"
```

### 3. 实时观察监控指标

在测试运行期间，可以实时查询：

```bash
# 查看HTTP请求指标
curl http://localhost:8338/actuator/metrics/http.server.requests

# 查看JVM内存
curl http://localhost:8338/actuator/metrics/jvm.memory.used

# 查看数据库连接池
curl http://localhost:8338/actuator/metrics/hikaricp.connections.active
```

---

## 最佳实践

### 1. 测试前准备

- ✅ 确保数据库有足够空间
- ✅ 清理旧的测试数据（可选）
- ✅ 重启应用获得干净的初始状态
- ✅ 验证应用健康状态

### 2. 测试策略

1. **先稳定流量，后复杂场景**
   - 第一步：10并发，稳定流量60秒
   - 第二步：20并发，稳定流量120秒
   - 第三步：尝试突发或渐进增长

2. **逐步增加压力**
   - 不要一次性测试过高并发
   - 观察每个阶段的系统表现
   - 找到合适的性能基线

3. **关注关键指标**
   - TPS（吞吐量）
   - 平均响应时间
   - P99响应时间
   - 成功率
   - 系统资源使用率

### 3. 测试后分析

- 📊 对比不同场景的测试结果
- 📈 观察监控曲线找到瓶颈
- 🔍 分析失败请求的原因
- 💡 根据结果优化系统配置

---

## 常见问题

### Q: 测试失败，连接被拒绝

**A**: 检查应用是否已启动，端口是否正确

```bash
curl http://localhost:8338/actuator/health
```

### Q: 大量请求失败

**A**: 可能并发数过高，尝试：
1. 降低并发数
2. 检查数据库连接池配置
3. 检查应用日志

### Q: 如何停止长时间测试

**A**: 按 `Ctrl+C` 中断测试

### Q: 测试数据如何清理

**A**: 测试数据会保留，手动清理：

```sql
-- 清理测试数据（谨慎操作）
DELETE FROM order_tbl WHERE order_id LIKE '17306%';
```

---

## 与Spring Boot集成测试对比

| 特性 | 独立测试 | Spring Boot集成测试 |
|------|----------|---------------------|
| 应用启动 | 手动 | 自动 |
| 监控数据 | ✅ 可观察 | ❌ 不可观察 |
| 测试时长 | 不限 | 建议<5分钟 |
| CI/CD集成 | ❌ | ✅ |
| 生产验证 | ✅ | ❌ |
| 流量场景 | 5种 | 基础 |

---

## 相关文档

- [测试框架完整指南](ai-home/TEST-FRAMEWORK-GUIDE.md)
- [Spring Boot集成测试](README_TEST_FRAMEWORK.md)
- [API文档](docs/api/README.md)

