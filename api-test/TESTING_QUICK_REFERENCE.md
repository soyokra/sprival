# 性能测试快速参考

## 🚀 快速开始

### 1. 数据预填充（必需）

```bash
curl -X POST http://127.0.0.1:8338/api/test/order/batchInsert \
  -H "Content-Type: application/json" \
  -d '{"batchSize": 1000, "batchCount": 100, "startUserId": 1}'
```

### 2. 运行测试

```bash
cd api-test

# 插入性能测试
python main.py --config configs/test_order_insert.json

# 查询性能测试
python main.py --config configs/test_order_query_pk.json

# 分页查询测试
python main.py --config configs/test_order_query_page.json

# 混合操作测试
python main.py --config configs/test_order_mixed.json

# 渐进式压测
python main.py --config configs/test_order_ramp_up.json

# 峰值冲击测试
python main.py --config configs/test_order_spike.json
```

### 3. 查看报告

HTML 报告位于：`api-test/reports/`

## 📊 测试配置对照表

| 配置文件 | 测试类型 | 并发数 | 时长 | 用途 |
|---------|---------|-------|------|------|
| `test_order_insert.json` | 单条插入 | 20 | 60s | 写入性能基准 |
| `test_order_query_pk.json` | 主键查询 | 50 | 60s | 读取性能基准 |
| `test_order_query_page.json` | 分页查询 | 30 | 60s | 分页性能 |
| `test_order_update.json` | 更新操作 | 20 | 60s | 更新性能 |
| `test_order_mixed.json` | 混合操作 | 40 | 180s | 真实场景 |
| `test_order_ramp_up.json` | 渐进压测 | 10→100 | 300s | 寻找拐点 |
| `test_order_spike.json` | 峰值冲击 | 10→200 | 45s | 突发流量 |

## 🎯 关键指标参考

### Hikari 连接池

```
活跃连接数（Active）    < 最大连接数
空闲连接数（Idle）      > 0
等待连接数（Pending）   = 0
连接创建时间            < 100ms
连接使用时长            < 100ms
连接超时次数            = 0
```

### API 性能

```
QPS（查询）             > 3000
QPS（插入）             > 500
响应时间 P95（查询）    < 20ms
响应时间 P95（插入）    < 50ms
成功率                  > 99%
```

## 🛠️ 常用命令

### 查看最新报告

```bash
# Windows
start reports\*.html

# Linux/Mac
open reports/*.html
```

### 调整测试参数

```bash
# 快速测试（小负载，短时间）
python main.py \
  --url http://127.0.0.1:8338 \
  --endpoint /api/test/order/page \
  --scenario constant \
  --threads 10 \
  --duration 30

# 压力测试（大负载，长时间）
python main.py \
  --url http://127.0.0.1:8338 \
  --endpoint /api/test/order/insert \
  --scenario constant \
  --threads 50 \
  --duration 300
```

## 🔧 故障排查

### 连接池满了

**症状**：`hikaricp_connections_pending` > 0

**解决**：
1. 增加 `maximum-pool-size`
2. 优化查询性能
3. 检查是否有慢查询

### 响应时间过长

**症状**：P95 > 100ms

**排查**：
1. 查看 Grafana 数据库慢查询
2. 检查索引是否生效
3. 查看数据库 CPU 使用率

### 测试失败率高

**症状**：成功率 < 95%

**排查**：
1. 查看 api-test 错误统计
2. 检查应用日志
3. 检查数据库连接数是否达到上限

## 💡 优化建议

### 连接池优化

```yaml
# 调整前先做基准测试
# 然后逐步调整参数，观察效果

spring:
  datasource:
    hikari:
      maximum-pool-size: 30  # 增加连接数
      minimum-idle: 15       # 增加最小空闲
      connection-timeout: 60000  # 增加超时时间
```

### 数据库优化

```sql
-- 检查索引使用情况
EXPLAIN SELECT * FROM test_order WHERE user_id = 123;

-- 查看慢查询
SHOW FULL PROCESSLIST;

-- 查看表统计信息
SHOW TABLE STATUS LIKE 'test_order';
```

### 应用优化

1. **批量操作**：使用 `batchInsert` 而不是循环单条插入
2. **合理分页**：避免深度分页
3. **缓存使用**：对热点数据使用缓存
4. **异步处理**：非关键路径使用异步

## 📞 支持

遇到问题？
- 查看详细文档：`docs/PERFORMANCE_TEST_GUIDE.md`
- 查看测试示例：`tests/`
- 查看配置示例：`configs/`

