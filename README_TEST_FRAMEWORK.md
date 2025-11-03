# 测试框架快速开始

## 概述

本项目提供完整的测试框架，支持单元测试、集成测试、性能测试和压力测试。

## 快速运行压力测试

### 1. 确保环境准备

- 数据库已启动（MySQL localhost:33306）
- 应用配置正确（`src/main/resources/application.properties`）

### 2. 运行快速验证测试（推荐首次运行）

```bash
# 快速压力测试：10并发，10秒
mvn test -Dtest=OrderInsertLoadTest#testInsert_QuickLoad
```

### 3. 运行完整压力测试场景

```bash
# 基准压力测试：10并发，60秒
mvn test -Dtest=OrderInsertLoadTest#testInsert_BaselineLoad

# 高并发测试：50并发，60秒
mvn test -Dtest=OrderInsertLoadTest#testInsert_HighConcurrency

# 持久性测试：10并发，300秒（5分钟）
mvn test -Dtest=OrderInsertLoadTest#testInsert_Endurance
```

## 测试报告示例

测试完成后会输出类似以下报告：

```
========================================
Load Test Report
========================================
Test Duration: 10.00 seconds
Concurrent Threads: 10
----------------------------------------
Total Requests: 2,534
Successful Requests: 2,518 (99.4%)
Failed Requests: 16 (0.6%)
----------------------------------------
TPS: 253.40 requests/sec
Avg Response Time: 38.5 ms
Min Response Time: 12 ms
Max Response Time: 156 ms
----------------------------------------
P50 Response Time: 35 ms
P90 Response Time: 68 ms
P95 Response Time: 92 ms
P99 Response Time: 145 ms
========================================
```

## 测试目标

- **成功率**: >= 99%
- **平均响应时间**: <= 500ms
- **P99响应时间**: <= 1000ms

## 测试数据

- 测试数据会自动生成并保存到数据库
- 每次请求生成唯一的订单ID，避免主键冲突
- 所有ID长度符合数据库字段限制

## 目录结构

```
src/test/java/com/soyokra/sprival/
├── base/                    # 测试基类
│   ├── BaseUnitTest.java
│   ├── BaseIntegrationTest.java
│   ├── BasePerformanceTest.java
│   └── BaseLoadTest.java
├── fixture/                 # 测试数据构造器
│   ├── TestDataBuilder.java
│   └── OrderTblFixture.java
├── util/                    # 测试工具类
│   ├── HttpLoadTestExecutor.java
│   ├── LoadTestConfig.java
│   ├── LoadTestResult.java
│   ├── LoadTestStatistics.java
│   └── TestAssertUtils.java
├── loadtest/               # 压力测试
│   └── api/
│       └── OrderInsertLoadTest.java
├── unit/                   # 单元测试
│   └── service/
│       └── OrderServiceTest.java
└── integration/            # 集成测试
    └── database/
        └── OrderRepositoryIntegrationTest.java
```

## 详细文档

完整的测试框架指南请参考: [ai-home/TEST-FRAMEWORK-GUIDE.md](ai-home/TEST-FRAMEWORK-GUIDE.md)

## 常见问题

### 1. 测试失败：数据太长

**问题**: `Data too long for column 'order_id' at row 1`

**解决**: 已在 `TestDataBuilder` 中修复，确保使用最新代码。各字段ID长度：
- order_id: 22字符
- trade_id: 20字符  
- user_id: 20字符
- partner_id: 29字符
- supplier_id: 30字符
- idempotent_id: 22字符

### 2. 数据库连接失败

**检查**:
- MySQL是否启动
- 端口是否正确（33306）
- 用户名密码是否正确

### 3. 测试运行时间过长

**建议**:
- 首次运行使用 `testInsert_QuickLoad`（10秒）
- 验证通过后再运行长时间测试

## 开发新的压力测试

1. 继承 `BaseLoadTest`
2. 使用 `TestDataBuilder` 生成测试数据
3. 配置 `LoadTestConfig`
4. 调用 `executor.execute(config)`
5. 验证 `LoadTestResult`

示例代码请参考 `OrderInsertLoadTest.java`

