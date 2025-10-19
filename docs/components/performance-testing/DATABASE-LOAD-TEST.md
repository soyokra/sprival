# 数据库插入压力测试说明

## 📋 概述

本文档介绍如何进行数据库层的直接插入压力测试。与 HTTP 接口测试不同，这种测试绕过了 HTTP 层、序列化等开销，直接测试数据库层的纯插入性能。

## 🎯 测试目标

1. **测试数据库层的纯插入性能**：不包含 HTTP、序列化等开销
2. **验证数据生成的正确性**：确保生成的数据符合数据库字段长度限制
3. **测试高并发写入性能**：验证数据库在高并发下的表现
4. **对比分析各层开销**：与 HTTP 接口测试对比，分析性能瓶颈

## 🆚 HTTP 测试 vs 数据库测试

| 特性 | HTTP 接口测试 | 数据库直接测试 |
|------|--------------|---------------|
| **测试范围** | HTTP → Controller → Service → Database | Service → Database |
| **网络开销** | 包含 HTTP 网络开销 | 无网络开销 |
| **序列化** | 包含 JSON 序列化/反序列化 | 无序列化开销 |
| **验证层** | 包含参数验证、请求验证 | 仅业务验证 |
| **真实性** | 更接近真实用户请求 | 更接近数据库真实性能 |
| **适用场景** | 整体性能评估 | 数据库性能瓶颈分析 |

## 🚀 快速开始

### 方式1: 使用脚本运行

#### Windows
```powershell
# 运行数据库插入压力测试（默认100并发，60秒）
.\scripts\run-performance-test.ps1 -TestClass OrderDatabaseInsertLoadTest

# 自定义参数
.\scripts\run-performance-test.ps1 `
  -TestClass OrderDatabaseInsertLoadTest `
  -ConcurrentUsers 200 `
  -DurationSeconds 120
```

#### Linux/Mac
```bash
# 运行数据库插入压力测试
./scripts/run-performance-test.sh OrderDatabaseInsertLoadTest "" 100 60
```

### 方式2: 使用 Maven 直接运行

```bash
# 运行所有数据库压力测试
mvn test -Dtest=OrderDatabaseInsertLoadTest

# 运行特定测试方法
mvn test -Dtest=OrderDatabaseInsertLoadTest#testOrderDatabaseInsertWithFixedConcurrency
```

## 📊 测试类型说明

### 1. 固定并发测试 (testOrderDatabaseInsertWithFixedConcurrency)

**测试目标**：验证系统在稳定并发下的数据库插入性能

```java
@Test
public void testOrderDatabaseInsertWithFixedConcurrency() throws Exception
```

**默认配置**：
- 并发数：100
- 持续时间：60秒
- 插入方式：单条插入

**运行方式**：
```bash
# Windows
.\scripts\run-performance-test.ps1 `
  -TestClass OrderDatabaseInsertLoadTest `
  -TestMethod testOrderDatabaseInsertWithFixedConcurrency

# Linux/Mac
./scripts/run-performance-test.sh OrderDatabaseInsertLoadTest testOrderDatabaseInsertWithFixedConcurrency
```

### 2. 递增并发测试 (testOrderDatabaseInsertWithIncrementalLoad)

**测试目标**：找出数据库性能的拐点

```java
@Test
public void testOrderDatabaseInsertWithIncrementalLoad() throws Exception
```

**并发级别**：10 → 50 → 100 → 200 → 500

**运行方式**：
```bash
# Windows
.\scripts\run-performance-test.ps1 `
  -TestClass OrderDatabaseInsertLoadTest `
  -TestMethod testOrderDatabaseInsertWithIncrementalLoad

# Maven
mvn test -Dtest=OrderDatabaseInsertLoadTest#testOrderDatabaseInsertWithIncrementalLoad
```

### 3. 峰值压力测试 (testOrderDatabaseInsertPeakLoad)

**测试目标**：测试数据库在极限情况下的表现

```java
@Test
public void testOrderDatabaseInsertPeakLoad() throws Exception
```

**配置**：
- 并发数：1000
- 持续时间：10秒

**运行方式**：
```bash
# Windows
.\scripts\run-performance-test.ps1 `
  -TestClass OrderDatabaseInsertLoadTest `
  -TestMethod testOrderDatabaseInsertPeakLoad
```

### 4. 批量插入测试 (testOrderDatabaseBatchInsert) ⭐ 推荐

**测试目标**：测试批量插入的性能提升效果

```java
@Test
public void testOrderDatabaseBatchInsert() throws Exception
```

**配置**：
- 批量大小：100条/批
- 总批次：100批
- 并发批次：10个线程

**运行方式**：
```bash
# Windows
.\scripts\run-performance-test.ps1 `
  -TestClass OrderDatabaseInsertLoadTest `
  -TestMethod testOrderDatabaseBatchInsert

# Maven
mvn test -Dtest=OrderDatabaseInsertLoadTest#testOrderDatabaseBatchInsert
```

## 📈 性能报告示例

### 单条插入性能报告

```
========================================
性能测试报告: OrderDatabaseInsert_FixedConcurrency_100
测试时间: 2025-10-19 20:30:00
========================================

测试结果:
  总请求数: 60000
  成功请求: 59950
  失败请求: 50
  成功率: 99.92%

响应时间:
  最小: 2 ms
  最大: 50 ms
  平均: 8.5 ms

吞吐量:
  TPS: 999.17 请求/秒
  总耗时: 60100 ms (60.10 s)
```

### 批量插入性能报告

```
========================================
性能测试报告: OrderDatabaseInsert_Batch_100
测试时间: 2025-10-19 20:35:00
========================================

测试结果:
  总批次: 100
  成功批次: 100
  失败批次: 0
  成功率: 100.00%

响应时间:
  最小: 50 ms
  最大: 200 ms
  平均: 120 ms

吞吐量:
  批次TPS: 8.33 批次/秒
  记录TPS: 833.33 记录/秒
  总耗时: 12000 ms (12.00 s)

总插入记录数: 10000
```

## 🔍 性能对比分析

### 单条插入 vs 批量插入

假设测试结果如下：

| 指标 | 单条插入 | 批量插入(100) | 性能提升 |
|------|---------|--------------|---------|
| TPS (记录/秒) | 999 | 833 × 批次数 | - |
| 平均响应时间 | 8.5ms | 120ms/批 | - |
| 每条记录耗时 | 8.5ms | 1.2ms | **7倍** |
| CPU 使用率 | 较高 | 较低 | 更高效 |
| 数据库连接 | 频繁 | 较少 | 更少开销 |

**结论**：批量插入在大量数据写入场景下性能更优。

### HTTP 接口 vs 数据库直接测试

假设测试结果如下：

| 指标 | HTTP 接口测试 | 数据库直接测试 | 差异 |
|------|--------------|---------------|------|
| TPS | 833 | 999 | +20% |
| 平均响应时间 | 15.5ms | 8.5ms | -45% |
| P99 响应时间 | 120ms | 50ms | -58% |

**分析**：
- HTTP 层增加了约 **7ms** 的延迟
- 包括网络传输、JSON 序列化、参数验证等
- 数据库层是主要性能瓶颈

## ⚙️ 配置说明

### 测试配置文件

配置文件：`src/test/resources/performance/application-performance.properties`

```properties
# 并发用户数
performance.test.concurrent-users=100

# 测试持续时间（秒）
performance.test.duration-seconds=60

# 预热时间（秒）
performance.test.warmup-seconds=10

# 详细日志
performance.test.verbose-logging=false

# 报告输出目录
performance.test.report-output-dir=target/performance-reports
```

### 数据库配置

确保测试环境数据库配置正确：

```properties
# application-test.properties
spring.datasource.url=jdbc:mysql://localhost:3306/test_db
spring.datasource.username=root
spring.datasource.password=password

# 连接池配置（重要！）
spring.datasource.hikari.maximum-pool-size=200
spring.datasource.hikari.minimum-idle=50
spring.datasource.hikari.connection-timeout=30000
```

## 💡 优化建议

### 1. 数据库层优化

#### 索引优化
```sql
-- 查看索引使用情况
SHOW INDEX FROM order_tbl;

-- 分析慢查询
SHOW VARIABLES LIKE 'slow_query%';
```

#### 连接池优化
```properties
# 增加连接池大小（根据并发数调整）
spring.datasource.hikari.maximum-pool-size=200

# 设置合理的超时时间
spring.datasource.hikari.connection-timeout=30000
```

### 2. 批量操作优化

使用批量插入代替单条插入：

```java
// ❌ 不推荐：循环单条插入
for (OrderTbl order : orders) {
    orderProvider.save(order);
}

// ✅ 推荐：批量插入
orderProvider.saveBatch(orders);
```

**性能提升**：约 **5-10倍**

### 3. 事务优化

合理使用事务批次：

```java
@Transactional(rollbackFor = Exception.class)
public void batchInsertWithTransaction(List<OrderTbl> orders) {
    // 批量插入
    orderProvider.saveBatch(orders);
}
```

### 4. 异步插入

对于非实时性要求高的场景，使用异步插入：

```java
@Async
public CompletableFuture<Void> asyncBatchInsert(List<OrderTbl> orders) {
    orderProvider.saveBatch(orders);
    return CompletableFuture.completedFuture(null);
}
```

## 📊 性能基准参考

### 硬件环境

- CPU: 4核 2.5GHz
- 内存: 8GB
- 磁盘: SSD
- 数据库: MySQL 8.0

### 性能指标

| 操作类型 | TPS | 平均响应时间 | P99响应时间 | 评价 |
|---------|-----|-------------|------------|------|
| 单条插入 | 800-1200 | 5-15ms | 30-50ms | 良好 |
| 批量插入(100) | 50-100批/秒 | 100-200ms | 300-500ms | 优秀 |
| 并发500 | 500-800 | 20-50ms | 100-200ms | 可接受 |

## ⚠️ 注意事项

1. **测试环境隔离**
   - 不要在生产环境运行
   - 使用独立的测试数据库
   - 测试前备份数据

2. **数据清理**
   ```sql
   -- 测试后清理数据
   TRUNCATE TABLE order_tbl;
   ```

3. **监控数据库资源**
   - CPU 使用率
   - 内存使用情况
   - 磁盘 IO
   - 连接数

4. **避免死锁**
   - 控制并发数
   - 合理设置事务隔离级别
   - 监控锁等待情况

5. **数据长度验证**
   ```bash
   # 运行前先验证数据格式
   .\scripts\validate-data-length.ps1
   ```

## 🔧 故障排除

### 问题1: 连接池耗尽

**错误信息**：
```
Connection is not available, request timed out after 30000ms
```

**解决方案**：
```properties
# 增加连接池大小
spring.datasource.hikari.maximum-pool-size=200

# 或降低并发数
performance.test.concurrent-users=50
```

### 问题2: 死锁

**错误信息**：
```
Deadlock found when trying to get lock
```

**解决方案**：
- 降低并发数
- 检查索引是否合理
- 调整事务隔离级别

### 问题3: 数据长度超出

**错误信息**：
```
Data too long for column 'order_id'
```

**解决方案**：
查看 [数据长度修复文档](DATA-LENGTH-FIX.md)

## 📚 相关文档

- [HTTP 接口压力测试](README.md)
- [数据长度修复说明](DATA-LENGTH-FIX.md)
- [快速开始指南](QUICK-START.md)
- [性能优化建议](README.md#性能优化建议)

## 🔗 测试代码

- 测试类：[OrderDatabaseInsertLoadTest.java](../../../src/test/java/com/soyokra/sprival/performance/loadtest/OrderDatabaseInsertLoadTest.java)
- 数据模型：[OrderTbl.java](../../../src/main/java/com/soyokra/sprival/app/repository/db/shop/model/OrderTbl.java)
- 数据提供者：[OrderTblProvider.java](../../../src/main/java/com/soyokra/sprival/app/repository/db/shop/provider/OrderTblProvider.java)

---

**维护者**: Sprival Team  
**最后更新**: 2025-10-19

