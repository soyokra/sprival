# 性能测试组件

## 概述

Sprival 项目提供了完整的性能测试框架，支持微基准测试（JMH）和压力测试（Load Test），帮助开发者快速评估系统性能、发现瓶颈。

## 功能特性

### ✅ 支持的测试类型

1. **微基准测试（Benchmark）**
   - 使用 JMH (Java Microbenchmark Harness)
   - 测试代码片段的性能（纳秒/微秒级别）
   - 适合方法级性能优化

2. **压力测试（Load Test）**
   - HTTP 接口压力测试：测试完整请求链路性能
   - 数据库直接压力测试：测试纯数据库层性能
   - 自定义多线程并发框架
   - 测试系统在高并发下的表现

3. **性能分析工具**
   - 自动统计响应时间（最小/最大/平均）
   - 计算 TPS（每秒事务数）
   - 计算成功率
   - 生成性能测试报告

## 快速开始

### 1. 运行 HTTP 接口压力测试

#### Windows 环境
```powershell
# 使用默认配置（100并发，60秒）
.\scripts\run-performance-test.ps1

# 自定义并发和时长
.\scripts\run-performance-test.ps1 -ConcurrentUsers 200 -DurationSeconds 120

# 运行特定测试方法
.\scripts\run-performance-test.ps1 -TestMethod testOrderInsertPeakLoad
```

#### Linux/Mac 环境
```bash
# 使用默认配置
./scripts/run-performance-test.sh

# 自定义参数（测试类 测试方法 并发数 持续时间）
./scripts/run-performance-test.sh OrderInsertLoadTest "" 200 120

# 运行特定测试方法
./scripts/run-performance-test.sh OrderInsertLoadTest testOrderInsertPeakLoad 100 60
```

> 📖 **详细说明**：查看 [HTTP 接口压力测试文档](QUICK-START.md)

### 2. 运行数据库直接压力测试 ⭐ 新增

测试数据库层的纯插入性能，不包含 HTTP 开销。

#### Windows 环境
```powershell
# 运行数据库插入压力测试
.\scripts\run-performance-test.ps1 -TestClass OrderDatabaseInsertLoadTest

# 运行批量插入测试（推荐）
.\scripts\run-performance-test.ps1 `
  -TestClass OrderDatabaseInsertLoadTest `
  -TestMethod testOrderDatabaseBatchInsert
```

#### Linux/Mac 环境
```bash
# 运行数据库插入压力测试
./scripts/run-performance-test.sh OrderDatabaseInsertLoadTest "" 100 60

# 运行批量插入测试
./scripts/run-performance-test.sh OrderDatabaseInsertLoadTest testOrderDatabaseBatchInsert
```

> 📖 **详细说明**：查看 [数据库压力测试文档](DATABASE-LOAD-TEST.md)

### 3. 使用 Maven 直接运行

```bash
# 运行所有压力测试
mvn test -Dtest=*LoadTest

# 运行 HTTP 接口测试
mvn test -Dtest=OrderInsertLoadTest

# 运行数据库直接测试
mvn test -Dtest=OrderDatabaseInsertLoadTest

# 运行特定测试方法
mvn test -Dtest=OrderInsertLoadTest#testOrderInsertWithFixedConcurrency
```

### 4. 运行微基准测试

```bash
# 运行 JMH 基准测试
mvn test -Dtest=OrderServiceBenchmark
```

## 目录结构

```
src/test/java/com/soyokra/sprival/performance/
├── benchmark/                           # 微基准测试
│   └── OrderServiceBenchmark.java      # 订单服务基准测试
├── loadtest/                            # 压力测试
│   ├── OrderInsertLoadTest.java        # HTTP 接口压力测试
│   └── OrderDatabaseInsertLoadTest.java # 数据库直接压力测试 ⭐ 新增
├── config/                              # 配置类
│   └── PerformanceTestConfig.java      # 性能测试配置
├── util/                                # 工具类
│   ├── PerformanceTestUtils.java       # 性能测试工具
│   ├── DataLengthValidationTest.java   # 数据长度验证测试
│   └── SimpleDataLengthValidator.java  # 简单数据长度验证器
└── README.md                            # 详细文档

src/test/resources/performance/
└── application-performance.properties   # 性能测试配置文件

scripts/
├── run-performance-test.ps1            # Windows 压测脚本
└── run-performance-test.sh             # Linux/Mac 压测脚本

target/performance-reports/              # 性能测试报告目录
└── OrderInsert_FixedConcurrency_100_20251015_180000.txt
```

## 配置说明

### 压力测试配置

配置文件: `src/test/resources/performance/application-performance.properties`

```properties
# 基础配置
performance.test.base-url=http://localhost:8338
performance.test.concurrent-users=100      # 并发用户数
performance.test.duration-seconds=60       # 测试持续时间（秒）
performance.test.warmup-seconds=10         # 预热时间（秒）
performance.test.target-tps=1000          # 目标TPS
performance.test.verbose-logging=false     # 详细日志
performance.test.report-output-dir=target/performance-reports
```

### 运行时覆盖配置

```bash
# 通过系统属性覆盖配置
mvn test -Dtest=OrderInsertLoadTest \
  -Dperformance.test.concurrent-users=200 \
  -Dperformance.test.duration-seconds=120
```

## 压力测试模式

### 1. 固定并发测试
**场景**: 模拟固定数量的并发用户持续访问系统  
**目的**: 评估系统在稳定负载下的性能表现  
**配置**:
- 并发数: 100（可配置）
- 持续时间: 60秒（可配置）

```java
@Test
public void testOrderInsertWithFixedConcurrency() throws Exception
```

### 2. 递增并发测试
**场景**: 逐步增加并发用户数  
**目的**: 找出系统的性能拐点和极限  
**配置**:
- 并发级别: 10 → 50 → 100 → 200 → 500
- 每级持续: 30秒

```java
@Test
public void testOrderInsertWithIncrementalLoad() throws Exception
```

### 3. 峰值压力测试
**场景**: 短时间内的极高并发  
**目的**: 测试系统在突发流量下的表现  
**配置**:
- 并发数: 1000
- 持续时间: 10秒

```java
@Test
public void testOrderInsertPeakLoad() throws Exception
```

## 性能指标说明

### 1. TPS (每秒事务数)
衡量系统吞吐量的关键指标

- **优秀**: > 1000 TPS
- **良好**: 500-1000 TPS
- **需优化**: < 500 TPS

### 2. 响应时间
接口处理单个请求的耗时

- **优秀**: < 50ms
- **良好**: 50-200ms
- **需优化**: > 200ms

### 3. 成功率
成功请求占总请求的百分比

- **优秀**: > 99.9%
- **良好**: 99%-99.9%
- **需优化**: < 99%

## 测试报告示例

### 控制台输出
```
========================================
性能测试结果: OrderInsert_FixedConcurrency_100
========================================
总请求数: 50000
成功请求: 49800
失败请求: 200
成功率: 99.60%
----------------------------------------
最小响应时间: 5 ms
最大响应时间: 120 ms
平均响应时间: 15.50 ms
----------------------------------------
TPS (每秒事务数): 833.33
总耗时: 60000 ms (60 s)
========================================
```

### 文件报告
报告保存位置: `target/performance-reports/`

```
========================================
性能测试报告: OrderInsert_FixedConcurrency_100
测试时间: 2025-10-15 18:00:00
========================================

测试结果:
  总请求数: 50000
  成功请求: 49800
  失败请求: 200
  成功率: 99.60%

响应时间:
  最小: 5 ms
  最大: 120 ms
  平均: 15.50 ms

吞吐量:
  TPS: 833.33 请求/秒
  总耗时: 60000 ms (60.00 s)
```

## 编写自定义压力测试

### 步骤1: 创建测试类

在 `src/test/java/com/soyokra/sprival/performance/loadtest/` 下创建新的测试类：

```java
package com.soyokra.sprival.performance.loadtest;

import com.soyokra.sprival.performance.config.PerformanceTestConfig;
import com.soyokra.sprival.performance.util.PerformanceTestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("performance")
public class YourApiLoadTest {
    
    @Autowired
    private PerformanceTestConfig config;
    
    @Test
    public void testYourApi() throws Exception {
        // 实现压力测试逻辑
    }
}
```

### 步骤2: 参考示例实现

参考 `OrderInsertLoadTest.java` 的完整实现

### 步骤3: 运行测试

```bash
# Windows
.\scripts\run-performance-test.ps1 -TestClass YourApiLoadTest

# Linux/Mac
./scripts/run-performance-test.sh YourApiLoadTest
```

## 技术栈

| 组件 | 版本 | 说明 |
|------|------|------|
| JMH | 1.35 | 微基准测试框架 |
| Apache HttpClient | 4.5.14 | HTTP 客户端 |
| REST Assured | - | REST API 测试库 |
| Spring Boot Test | 2.7.18 | Spring 测试支持 |

## 性能优化建议

### 数据库层面
1. ✅ 添加索引
2. ✅ 优化 SQL 查询
3. ✅ 使用数据库连接池
4. ✅ 读写分离

### 应用层面
1. ✅ 添加缓存（Redis）
2. ✅ 异步处理
3. ✅ 批量操作
4. ✅ 减少锁竞争

### 架构层面
1. ✅ 负载均衡
2. ✅ 水平扩展
3. ✅ 使用消息队列
4. ✅ 微服务拆分

## 注意事项

1. ⚠️ **不要在生产环境运行压力测试**
2. ⚠️ 压力测试前确保有足够的系统资源
3. ⚠️ 测试前关闭不必要的日志输出
4. ⚠️ 建议使用独立的测试环境
5. ⚠️ 测试数据应该真实但要脱敏
6. ⚠️ 注意监控系统资源（CPU、内存、网络）

## 常见问题

### Q1: 压力测试失败，连接被拒绝？
**解决**: 
1. 确保应用已启动：`mvn spring-boot:run`
2. 检查端口是否正确：默认 8338
3. 检查防火墙设置

### Q2: 测试报告在哪里？
**解决**: 
测试报告保存在 `target/performance-reports/` 目录下

### Q3: 如何提高测试并发数？
**解决**: 
```bash
# 修改配置文件或使用系统属性
-Dperformance.test.concurrent-users=500
```

### Q4: 测试时出现 OOM 错误？
**解决**: 
增加 JVM 内存：
```bash
export MAVEN_OPTS="-Xmx2g -Xms2g"
mvn test -Dtest=OrderInsertLoadTest
```

## 参考资料

### 📖 本项目文档
- [快速开始指南](QUICK-START.md) - 5分钟快速入门
- [数据库压力测试](DATABASE-LOAD-TEST.md) - 数据库层性能测试 ⭐ 新增
- [数据长度修复说明](DATA-LENGTH-FIX.md) - 数据库字段长度问题修复
- [性能测试详细文档](../../test/java/com/soyokra/sprival/performance/README.md)

### 🔗 外部资源
- [JMH 官方文档](https://github.com/openjdk/jmh)
- [Apache HttpClient 文档](https://hc.apache.org/httpcomponents-client-ga/)
- [性能测试最佳实践](https://martinfowler.com/articles/practical-test-pyramid.html)

---

**维护者**: Sprival Team  
**最后更新**: 2025-10-19  
**版本**: 1.1

