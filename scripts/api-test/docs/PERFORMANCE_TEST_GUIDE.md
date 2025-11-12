# Hikari 连接池性能测试指南

本指南介绍如何使用 api-test 框架测试数据库性能并在 Grafana 中观察 Hikari 连接池监控指标。

## 📋 测试准备

### 1. 数据库表创建

确保已创建测试表（位于 `docs/data/mysql/test_order.sql`）：
- `test_order` - 订单主表
- `test_order_detail` - 订单明细表

### 2. 应用启动

确保 Spring Boot 应用已启动并运行在 `http://127.0.0.1:8338`

### 3. 监控确认

确认 Grafana 和 Prometheus 已配置好 Hikari 连接池监控。

## 🎯 测试场景

### 场景一：数据预填充

在进行性能测试前，需要先填充测试数据。

```bash
# 方式1：使用 curl 命令
curl -X POST http://127.0.0.1:8338/api/test/order/batchInsert \
  -H "Content-Type: application/json" \
  -d '{
    "batchSize": 1000,
    "batchCount": 100,
    "startUserId": 1
  }'

# 方式2：使用 Postman 或其他 API 工具
# 填充 10 万条数据（1000 * 100）
```

**建议**：
- 首次测试：填充 1 万条数据（`batchSize: 100, batchCount: 100`）
- 压力测试：填充 10 万条数据（`batchSize: 1000, batchCount: 100`）
- 极限测试：填充 100 万条数据（`batchSize: 1000, batchCount: 1000`）

### 场景二：单条插入性能测试

测试单条记录插入性能，观察写操作对连接池的压力。

```bash
cd api-test
python main.py --config configs/test_order_insert.json
```

**测试参数**：
- 并发线程：20
- 持续时间：60秒
- 预期 QPS：根据硬件配置不同，通常 500-2000 QPS

**关注指标**：
- `hikaricp_connections_active` - 活跃连接数应稳定
- `hikaricp_connections_creation_seconds` - 连接创建时间
- 响应时间 P95 - 应在 50ms 以内

### 场景三：主键查询性能测试

测试主键索引查询性能，这是最快的查询方式。

```bash
python main.py --config configs/test_order_query_pk.json
```

**测试参数**：
- 并发线程：50
- 持续时间：60秒
- 预期 QPS：5000-10000 QPS

**关注指标**：
- 响应时间应该非常快（P99 < 10ms）
- 连接池使用率
- 数据库 CPU 使用率

### 场景四：分页查询性能测试

测试分页查询性能，模拟列表查询场景。

```bash
python main.py --config configs/test_order_query_page.json
```

**测试参数**：
- 并发线程：30
- 持续时间：60秒
- 预期 QPS：1000-3000 QPS

**关注指标**：
- 响应时间 P95 应在 100ms 以内
- 注意深度分页（大页码）性能下降

### 场景五：更新操作性能测试

测试更新操作性能。

```bash
python main.py --config configs/test_order_update.json
```

**测试参数**：
- 并发线程：20
- 持续时间：60秒

**关注指标**：
- 更新操作比插入稍快
- 观察锁等待情况

### 场景六：混合操作测试

模拟真实业务场景（70%读 + 30%写）。

```bash
python main.py --config configs/test_order_mixed.json
```

**测试参数**：
- 并发线程：40
- 持续时间：180秒（3分钟）

**关注指标**：
- 连接池在读写混合场景下的表现
- 整体系统吞吐量

### 场景七：渐进式压测

逐步增加负载，找到系统性能拐点。

```bash
python main.py --config configs/test_order_ramp_up.json
```

**测试参数**：
- 初始线程：10
- 目标线程：100
- 渐进时长：120秒
- 保持时长：180秒

**关注指标**：
- 观察在哪个并发量下响应时间开始显著增长
- 连接池是否出现等待
- 数据库 CPU、内存使用率

### 场景八：峰值冲击测试

测试系统应对突发流量的能力。

```bash
python main.py --config configs/test_order_spike.json
```

**测试参数**：
- 基础线程：10
- 峰值线程：200
- 峰值持续：15秒

**关注指标**：
- 峰值时的响应时间
- 连接池是否能快速响应
- 是否出现连接超时

## 📊 Grafana 监控指标说明

### Hikari 核心指标

#### 1. 连接数指标

```
hikaricp_connections_active
```
- **含义**：当前活跃连接数（正在使用的连接）
- **观察点**：应该在合理范围内波动，不应持续接近最大值
- **异常**：如果持续等于最大连接数，说明连接池不足

```
hikaricp_connections_idle
```
- **含义**：当前空闲连接数
- **观察点**：空闲连接数 + 活跃连接数 = 总连接数
- **异常**：如果空闲连接数长期为 0，可能需要增加连接池大小

```
hikaricp_connections_pending
```
- **含义**：等待获取连接的线程数
- **观察点**：应该保持为 0 或很小的值
- **异常**：如果持续有等待，说明连接池不足或有连接泄漏

#### 2. 连接创建指标

```
hikaricp_connections_creation_seconds
```
- **含义**：创建新连接的耗时
- **观察点**：通常在 10-100ms
- **异常**：如果超过 1 秒，需要检查数据库连接性能

#### 3. 连接使用指标

```
hikaricp_connections_usage_seconds
```
- **含义**：连接的使用时长
- **观察点**：大部分请求应该很快归还连接（< 100ms）
- **异常**：长时间占用连接可能导致连接池耗尽

#### 4. 连接超时指标

```
hikaricp_connections_timeout_total
```
- **含义**：连接获取超时的总次数
- **观察点**：应该保持为 0
- **异常**：出现超时说明连接池配置不合理或存在性能问题

### 应用性能指标

从 api-test 报告中关注：
- **QPS**：每秒查询数
- **响应时间**：P50、P90、P95、P99
- **成功率**：应该保持 100%（或接近）
- **错误率**：应该接近 0%

### 数据库指标

在 Grafana 中关注：
- **MySQL QPS**：查询速率
- **MySQL TPS**：事务速率
- **慢查询数**：应该为 0
- **锁等待**：应该很少
- **CPU 使用率**：不应持续 100%
- **磁盘 I/O**：观察读写压力

## 📈 性能基准参考

基于中等硬件配置（4核CPU，8GB内存，SSD）：

| 操作类型 | 预期 QPS | P95 响应时间 | 推荐并发数 |
|---------|---------|-------------|-----------|
| 主键查询 | 5000+ | < 10ms | 50-100 |
| 索引查询 | 3000+ | < 20ms | 30-50 |
| 分页查询 | 1000+ | < 50ms | 20-40 |
| 单条插入 | 1000+ | < 30ms | 10-30 |
| 批量插入 | 500+ | < 100ms | 5-20 |
| 更新操作 | 1500+ | < 20ms | 10-30 |
| 混合操作 | 2000+ | < 40ms | 30-50 |

## 🎬 完整测试流程

### 步骤 1：数据预填充

```bash
# 填充 10 万条测试数据
curl -X POST http://127.0.0.1:8338/api/test/order/batchInsert \
  -H "Content-Type: application/json" \
  -d '{"batchSize": 1000, "batchCount": 100, "startUserId": 1}'
```

### 步骤 2：基准测试

运行各种测试场景建立性能基准：

```bash
# 1. 插入性能
python main.py --config configs/test_order_insert.json

# 2. 主键查询性能
python main.py --config configs/test_order_query_pk.json

# 3. 分页查询性能
python main.py --config configs/test_order_query_page.json

# 4. 更新性能
python main.py --config configs/test_order_update.json
```

### 步骤 3：压力测试

逐步增加压力，找到系统瓶颈：

```bash
# 渐进式压测
python main.py --config configs/test_order_ramp_up.json
```

在 Grafana 中实时观察：
- 在哪个并发量下响应时间开始明显增长？
- 连接池是否出现等待？
- 数据库资源使用情况如何？

### 步骤 4：极限测试

测试系统在极限负载下的表现：

```bash
# 峰值冲击测试
python main.py --config configs/test_order_spike.json
```

观察：
- 系统能否顶住突发流量？
- 恢复正常需要多长时间？
- 是否有请求失败？

### 步骤 5：稳定性测试

长时间运行测试系统稳定性：

```bash
# 混合操作 - 运行 3 分钟
python main.py --config configs/test_order_mixed.json
```

观察：
- 连接池是否稳定？
- 是否有内存泄漏？
- 响应时间是否稳定？

## 🔍 问题诊断

### 问题1：响应时间过长

**可能原因**：
- 数据库慢查询
- 索引缺失或未使用
- 连接池配置不当

**诊断方法**：
1. 查看 api-test 报告中的 P95、P99 响应时间
2. 在 Grafana 中查看数据库慢查询日志
3. 检查是否有索引未使用

### 问题2：连接池等待

**现象**：`hikaricp_connections_pending` > 0

**可能原因**：
- 连接池太小
- 数据库连接数限制
- 存在连接泄漏（未正确归还）

**解决方法**：
1. 增大连接池配置（`maximum-pool-size`）
2. 检查数据库最大连接数配置
3. 检查代码是否正确关闭连接

### 问题3：连接创建过慢

**现象**：`hikaricp_connections_creation_seconds` > 1秒

**可能原因**：
- 数据库响应慢
- 网络延迟高
- 数据库连接数已达上限

**解决方法**：
1. 优化数据库配置
2. 检查网络连接
3. 使用连接池预热

## 📝 测试报告解读

### api-test HTML 报告

报告位于 `api-test/reports/` 目录，包含：

1. **测试概览**
   - 总请求数
   - 成功/失败请求
   - 成功率
   - QPS

2. **响应时间统计**
   - 最小值、最大值、平均值
   - P90、P95、P99 百分位
   - 标准差

3. **状态码分布**
   - 200：成功
   - 500：服务器错误
   - 其他：需要关注

4. **错误统计**
   - 详细的错误信息和出现次数

### Grafana 仪表板

推荐创建包含以下面板的仪表板：

1. **Hikari 连接池概览**
   - 活跃连接数
   - 空闲连接数
   - 等待连接数
   - 总连接数

2. **Hikari 性能指标**
   - 连接创建时间
   - 连接使用时长
   - 连接超时次数

3. **应用性能指标**
   - API 响应时间（从 api-test 报告导入）
   - API QPS
   - API 成功率

4. **数据库指标**
   - MySQL QPS/TPS
   - 慢查询数量
   - 锁等待
   - CPU/内存使用率

## 🎓 最佳实践

### 1. Hikari 连接池配置建议

```yaml
spring:
  datasource:
    hikari:
      # 最小空闲连接数
      minimum-idle: 10
      
      # 最大连接池大小（根据实际情况调整）
      # 公式：((核心数 * 2) + 有效磁盘数)
      maximum-pool-size: 20
      
      # 连接超时时间
      connection-timeout: 30000
      
      # 连接最大生命周期
      max-lifetime: 1800000
      
      # 连接空闲超时
      idle-timeout: 600000
      
      # 连接测试查询
      connection-test-query: SELECT 1
```

### 2. 测试策略

**渐进式测试**：
1. 从小负载开始（10 线程）
2. 逐步增加负载（20、30、50、100）
3. 观察每个阶段的性能表现
4. 找到性能拐点

**场景组合**：
1. 先测试单一操作（插入、查询、更新）
2. 再测试混合操作
3. 最后进行极限测试

### 3. 数据管理

**定期清理**：
```sql
-- 清空测试表
TRUNCATE TABLE test_order;
TRUNCATE TABLE test_order_detail;

-- 或者保留最近的数据
DELETE FROM test_order WHERE create_time < DATE_SUB(NOW(), INTERVAL 1 DAY);
```

**数据隔离**：
- 使用独立的测试数据库
- 不要在生产环境运行性能测试

## 📖 扩展测试

### 自定义测试场景

创建自己的配置文件：

```json
{
  "test_name": "My Custom Test",
  "http": {
    "base_url": "http://127.0.0.1:8338"
  },
  "scenario": {
    "type": "constant",
    "threads": 30,
    "duration": 120
  },
  "api": {
    "endpoint": "/api/test/order/query",
    "method": "GET",
    "params": {
      "userId": "${mock:int:1:10000}",
      "status": "${mock:int:0:4}"
    }
  }
}
```

### 命令行快速测试

不使用配置文件，直接命令行测试：

```bash
python main.py \
  --url http://127.0.0.1:8338 \
  --endpoint /api/test/order/get/12345 \
  --scenario constant \
  --threads 50 \
  --duration 30 \
  --reports console,html
```

## 🚨 注意事项

1. **数据库连接数限制**
   - 确保数据库最大连接数 > Hikari 最大连接池大小
   - MySQL 默认 151，可能需要调整

2. **测试环境隔离**
   - 使用独立的测试环境
   - 避免影响生产数据

3. **资源监控**
   - 同时监控应用和数据库资源
   - 注意磁盘空间（日志文件）

4. **测试时机**
   - 避免在业务高峰期测试
   - 测试前确认系统状态正常

## 📚 参考资料

- [HikariCP GitHub](https://github.com/brettwooldridge/HikariCP)
- [Spring Boot Actuator Metrics](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.metrics)
- [MySQL Performance Tuning](https://dev.mysql.com/doc/refman/8.0/en/optimization.html)

