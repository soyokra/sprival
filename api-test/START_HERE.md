# 🚀 从这里开始 - Hikari 连接池性能测试

## 📋 前置条件检查

- [x] Spring Boot 应用已启动（端口 8338）
- [x] 数据库已创建测试表（test_order、test_order_detail）
- [x] Grafana 和 Prometheus 已配置
- [x] Python 依赖已安装（`pip install -r requirements.txt`）

## 🎯 3分钟快速测试

### 1️⃣ 预填充测试数据（30秒）

打开新的命令行窗口，执行：

```bash
curl -X POST http://127.0.0.1:8338/api/test/order/batchInsert ^
  -H "Content-Type: application/json" ^
  -d "{\"batchSize\": 1000, \"batchCount\": 10, \"startUserId\": 1}"
```

**说明**：这将填充 1 万条测试数据（建议首次测试用这个量）

### 2️⃣ 运行性能测试（60秒）

```bash
cd api-test
python main.py --config configs/test_order_insert.json
```

### 3️⃣ 查看结果

测试完成后：
1. 打开 `api-test/reports/` 目录中的最新 HTML 报告
2. 打开 Grafana 仪表板查看 Hikari 连接池指标

## 📊 推荐测试流程

### 阶段一：基准测试（建立性能基线）

```bash
# 1. 插入性能
python main.py --config configs/test_order_insert.json

# 2. 主键查询性能  
python main.py --config configs/test_order_query_pk.json

# 3. 分页查询性能
python main.py --config configs/test_order_query_page.json
```

**预期**：
- 插入 QPS：500-2000
- 主键查询 QPS：5000-10000  
- 分页查询 QPS：1000-3000

### 阶段二：压力测试（找到系统瓶颈）

```bash
# 渐进式压测 - 找到性能拐点
python main.py --config configs/test_order_ramp_up.json
```

**观察**：
- 在哪个并发数下响应时间开始明显增长？
- 连接池是否出现等待（pending > 0）？
- 数据库 CPU 使用率如何？

### 阶段三：极限测试（测试系统上限）

```bash
# 峰值冲击测试
python main.py --config configs/test_order_spike.json
```

**观察**：
- 系统能否顶住 200 并发？
- 是否有请求超时或失败？
- 恢复正常需要多长时间？

### 阶段四：稳定性测试（长期运行）

```bash
# 混合操作测试 - 3 分钟
python main.py --config configs/test_order_mixed.json
```

**观察**：
- 连接池是否稳定？
- 响应时间是否稳定？
- 是否有内存泄漏迹象？

## 🎨 Grafana 仪表板

### 必看指标

打开 Grafana，创建或查看包含以下指标的仪表板：

#### 1. Hikari 连接池面板

```
面板1：活跃连接数 vs 空闲连接数
  - hikaricp_connections_active
  - hikaricp_connections_idle

面板2：等待连接数（应该为0）
  - hikaricp_connections_pending

面板3：连接性能
  - hikaricp_connections_creation_seconds
  - hikaricp_connections_usage_seconds

面板4：连接超时（应该为0）
  - hikaricp_connections_timeout_total
```

#### 2. 应用性能面板

从 api-test 报告中获取数据手动添加：
  - QPS
  - 响应时间（P95、P99）
  - 成功率

#### 3. 数据库性能面板

  - MySQL QPS/TPS
  - 慢查询数量
  - 连接数
  - CPU/内存使用率

## 🔍 结果判读

### ✅ 健康状态

```
活跃连接数    < 最大连接数 (有余量)
等待连接数    = 0
连接超时      = 0
响应时间 P95  < 50ms (查询)
响应时间 P95  < 100ms (插入)
成功率        > 99%
```

### ⚠️ 警告状态

```
活跃连接数    接近最大值 (需要关注)
等待连接数    > 0 偶尔出现
响应时间 P95  50-200ms
成功率        95-99%
```

### ❌ 异常状态

```
活跃连接数    = 最大连接数 (连接池不足)
等待连接数    持续 > 0
连接超时      > 0
响应时间 P95  > 500ms
成功率        < 95%
```

## 💡 常见问题

### Q1: 测试失败怎么办？

**检查清单**：
- [ ] 应用是否启动？`curl http://127.0.0.1:8338/api/test/order/statistics`
- [ ] 数据库表是否创建？
- [ ] 是否预填充了数据？

### Q2: 响应时间太长？

**排查步骤**：
1. 查看 api-test HTML 报告 → 错误统计
2. 查看应用日志 → 是否有异常
3. 查看 Grafana → 数据库是否有慢查询

### Q3: 连接池出现等待？

**优化方向**：
1. 增加 `maximum-pool-size`
2. 优化慢查询
3. 检查是否有连接泄漏

### Q4: 如何调整测试参数？

编辑配置文件中的 `scenario` 部分：

```json
{
  "scenario": {
    "type": "constant",
    "threads": 30,      // 调整并发数
    "duration": 120     // 调整测试时长
  }
}
```

## 📖 详细文档

- [性能测试详细指南](docs/PERFORMANCE_TEST_GUIDE.md) - 完整测试流程
- [快速参考手册](TESTING_QUICK_REFERENCE.md) - 常用命令速查
- [测试场景说明](README_TEST_SCENARIOS.md) - 所有场景详解
- [api-test 框架文档](README.md) - 框架使用说明

## 🎉 立即开始

现在就开始你的第一个性能测试：

```bash
# Windows PowerShell
cd api-test
python main.py quick smoke_test --url http://127.0.0.1:8338 --endpoint /api/test/order/statistics
```

这将运行一个 30 秒的快速测试，验证系统是否正常。

**Happy Testing!** 🚀

