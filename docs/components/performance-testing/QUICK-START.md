# 性能测试快速入门

## 🚀 5分钟快速开始

### 步骤0: 验证数据格式（可选但推荐）

在运行压力测试前，建议先验证测试数据格式是否符合数据库字段限制：

```powershell
# Windows
.\scripts\validate-data-length.ps1

# Linux/Mac
chmod +x scripts/validate-data-length.sh
./scripts/validate-data-length.sh
```

> 💡 **提示**: 此步骤验证生成的测试数据不会因长度问题导致数据库插入失败

### 步骤1: 启动应用（可选）

如果要测试真实接口，需要先启动应用：

```bash
# 使用性能测试配置启动
mvn spring-boot:run -Dspring-boot.run.profiles=performance
```

> 💡 **提示**: 也可以直接运行压力测试，测试代码会模拟请求

### 步骤2: 运行压力测试

#### Windows 环境
```powershell
# 运行下单接口压力测试（默认100并发，60秒）
.\scripts\run-performance-test.ps1
```

#### Linux/Mac 环境
```bash
# 添加执行权限
chmod +x scripts/run-performance-test.sh

# 运行压力测试
./scripts/run-performance-test.sh
```

### 步骤3: 查看测试报告

测试完成后，查看生成的报告：

```powershell
# Windows
Get-Content target\performance-reports\*.txt | Select-Object -Last 1

# Linux/Mac
cat target/performance-reports/$(ls -t target/performance-reports | head -1)
```

## 📊 测试结果示例

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

## 🎯 常用测试场景

### 场景1: 基础性能测试
适合日常性能验证

```powershell
# Windows
.\scripts\run-performance-test.ps1 -ConcurrentUsers 50 -DurationSeconds 30

# Linux/Mac
./scripts/run-performance-test.sh OrderInsertLoadTest "" 50 30
```

### 场景2: 高并发压力测试
测试系统极限

```powershell
# Windows
.\scripts\run-performance-test.ps1 -TestMethod testOrderInsertPeakLoad

# Linux/Mac
./scripts/run-performance-test.sh OrderInsertLoadTest testOrderInsertPeakLoad
```

### 场景3: 递增负载测试
找出性能拐点

```powershell
# Windows
.\scripts\run-performance-test.ps1 -TestMethod testOrderInsertWithIncrementalLoad

# Linux/Mac
./scripts/run-performance-test.sh OrderInsertLoadTest testOrderInsertWithIncrementalLoad
```

## 🔧 自定义参数

### 调整并发数
```bash
# 200 并发用户
-ConcurrentUsers 200
```

### 调整测试时长
```bash
# 测试 120 秒
-DurationSeconds 120
```

### 组合使用
```powershell
# Windows: 200并发，持续120秒
.\scripts\run-performance-test.ps1 -ConcurrentUsers 200 -DurationSeconds 120

# Linux/Mac
./scripts/run-performance-test.sh OrderInsertLoadTest "" 200 120
```

## 📈 性能指标解读

### TPS (每秒事务数)
- ✅ **优秀**: > 1000 TPS
- 👍 **良好**: 500-1000 TPS  
- ⚠️ **需优化**: < 500 TPS

### 响应时间
- ✅ **优秀**: < 50ms
- 👍 **良好**: 50-200ms
- ⚠️ **需优化**: > 200ms

### 成功率
- ✅ **优秀**: > 99.9%
- 👍 **良好**: 99%-99.9%
- ⚠️ **需优化**: < 99%

## 💡 测试技巧

### 技巧1: 先预热再测试
```bash
# 使用较小并发预热 10 秒
.\scripts\run-performance-test.ps1 -ConcurrentUsers 10 -DurationSeconds 10

# 然后进行正式测试
.\scripts\run-performance-test.ps1 -ConcurrentUsers 100 -DurationSeconds 60
```

### 技巧2: 对比测试
```bash
# 优化前测试
.\scripts\run-performance-test.ps1 > before.txt

# 进行代码优化...

# 优化后测试
.\scripts\run-performance-test.ps1 > after.txt

# 对比结果
```

### 技巧3: 监控系统资源
在压测时，使用系统监控工具：

```bash
# Windows: 任务管理器 -> 性能
# Linux: top, htop
# 监控: CPU、内存、网络IO
```

## ⚠️ 注意事项

1. **不要在生产环境运行**
2. 确保测试环境资源充足
3. 关闭不必要的日志输出
4. 测试前备份数据库
5. 注意监控系统资源

## 🆘 常见问题

### Q: 测试一直连接失败？
**A**: 检查应用是否启动，端口是否正确（默认8338）

### Q: 测试很慢，没有反应？
**A**: 检查并发数是否过高，降低并发数重试

### Q: 找不到测试报告？
**A**: 报告在 `target/performance-reports/` 目录下

### Q: 想测试其他接口怎么办？
**A**: 参考 [完整文档](README.md#编写自定义压力测试) 编写新的测试类

### Q: 数据库报错 "Data too long for column"？
**A**: 测试数据长度超出数据库字段限制，请查看 [数据长度修复文档](DATA-LENGTH-FIX.md)

## 📚 进一步学习

- [完整性能测试文档](README.md)
- [性能测试框架详细说明](../../test/java/com/soyokra/sprival/performance/README.md)
- [性能优化建议](README.md#性能优化建议)

---

**快速开始有问题？** 查看 [完整文档](README.md) 或提交 Issue

