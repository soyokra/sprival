# 数据库性能测试场景说明

## 🎯 测试目标

通过 api-test 框架测试应用数据库操作性能，在 Grafana 中观察 Hikari 连接池监控指标。

## 📦 已创建内容

### Java 后端代码

#### 1. 实体类（Entity）
- `TestOrder.java` - 订单表实体
- `TestOrderDetail.java` - 订单明细表实体

#### 2. 数据访问层（Repository）
- `TestOrderMapper.java` - 订单 Mapper 接口
- `TestOrderDetailMapper.java` - 订单明细 Mapper 接口
- `TestOrderContract.java` - 订单 Contract 接口
- `TestOrderDetailContract.java` - 订单明细 Contract 接口
- `TestOrderProvider.java` - 订单 Provider 实现
- `TestOrderDetailProvider.java` - 订单明细 Provider 实现
- `TestBaseProvider.java` - 测试数据源基类

#### 3. 请求/响应对象（DTO）
- `TestOrderInsertRequest.java` - 单条插入请求
- `TestOrderBatchInsertRequest.java` - 批量插入请求（数据预填充）
- `TestOrderUpdateRequest.java` - 更新请求
- `TestOrderQueryRequest.java` - 查询请求
- `TestOrderStatisticsResponse.java` - 统计响应

#### 4. 业务逻辑层（Service）
- `TestOrderService.java` - 订单服务类
  - 单条插入
  - 批量插入
  - 主键查询
  - 订单号查询
  - 条件查询
  - 分页查询
  - 更新操作
  - 删除操作
  - 统计查询

#### 5. 控制器层（Controller）
- `TestOrderController.java` - 订单测试控制器
  - `POST /api/test/order/insert` - 单条插入
  - `POST /api/test/order/batchInsert` - 批量插入
  - `GET /api/test/order/get/{orderId}` - 主键查询
  - `GET /api/test/order/getByOrderNo` - 订单号查询
  - `GET /api/test/order/query` - 条件查询
  - `GET /api/test/order/page` - 分页查询
  - `PUT /api/test/order/update` - 更新操作
  - `DELETE /api/test/order/delete/{orderId}` - 删除操作
  - `GET /api/test/order/statistics` - 统计查询
  - `POST /api/test/order/mixedOperation` - 混合操作

#### 6. 工具类
- `TestDataGenerator.java` - 测试数据生成工具

### api-test 配置文件

#### 7. 测试场景配置（configs/）
- `test_order_insert.json` - 单表插入性能测试
- `test_order_insert_with_details.json` - **事务插入性能测试（含明细）**
- `test_order_query_pk.json` - 主键查询性能测试
- `test_order_query_page.json` - 分页查询性能测试
- `test_order_join_query.json` - **JOIN 查询性能测试**
- `test_order_update.json` - 更新性能测试
- `test_order_mixed.json` - 混合操作测试
- `test_order_ramp_up.json` - 渐进式压测
- `test_order_spike.json` - 峰值冲击测试

### 文档

#### 8. 测试指南
- `docs/PERFORMANCE_TEST_GUIDE.md` - 详细性能测试指南
- `TESTING_QUICK_REFERENCE.md` - 快速参考手册

## ⚡ 快速测试（3步走）

### 第1步：数据预填充

```bash
curl -X POST http://127.0.0.1:8338/api/test/order/batchInsert \
  -H "Content-Type: application/json" \
  -d '{"batchSize": 1000, "batchCount": 100, "startUserId": 1}'
```

这将填充 10 万条测试数据。

### 第2步：运行性能测试

```bash
cd api-test

# 选择一个测试场景运行
python main.py --config configs/test_order_insert.json
```

### 第3步：查看结果

1. **查看 api-test 报告**：打开 `api-test/reports/` 下的 HTML 报告
2. **查看 Grafana 仪表板**：观察 Hikari 连接池指标

## 📊 测试场景详解

### 1. 单表插入性能测试
```bash
python main.py --config configs/test_order_insert.json
```
- **并发**：20 线程
- **时长**：60 秒
- **操作**：单条订单插入（仅主表）
- **目的**：测试单表写入性能基准

### 2. 事务插入性能测试（NEW）
```bash
python main.py --config configs/test_order_insert_with_details.json
```
- **并发**：15 线程
- **时长**：60 秒
- **操作**：插入订单 + 明细（1-5条）
- **目的**：测试事务性能和多表插入
- **涉及表**：test_order + test_order_detail

### 3. 主键查询测试
```bash
python main.py --config configs/test_order_query_pk.json
```
- **并发**：50 线程
- **时长**：60 秒
- **操作**：根据 ID 查询（单表）
- **目的**：测试最快查询性能

### 4. JOIN 查询测试（NEW）
```bash
python main.py --config configs/test_order_join_query.json
```
- **并发**：30 线程
- **时长**：60 秒
- **操作**：查询订单及明细（关联查询）
- **目的**：测试 JOIN 查询性能
- **涉及表**：test_order + test_order_detail

### 5. 分页查询测试
```bash
python main.py --config configs/test_order_query_page.json
```
- **并发**：30 线程
- **时长**：60 秒
- **操作**：分页查询订单列表
- **目的**：测试列表查询性能

### 6. 更新性能测试
```bash
python main.py --config configs/test_order_update.json
```
- **并发**：20 线程
- **时长**：60 秒
- **操作**：更新订单状态
- **目的**：测试更新操作性能

### 7. 混合操作测试
```bash
python main.py --config configs/test_order_mixed.json
```
- **并发**：40 线程
- **时长**：180 秒
- **操作**：70%读 + 30%写
- **目的**：模拟真实业务场景

### 8. 渐进式压测
```bash
python main.py --config configs/test_order_ramp_up.json
```
- **并发**：10→100 线程
- **时长**：300 秒
- **操作**：订单插入
- **目的**：找到性能拐点

### 9. 峰值冲击测试
```bash
python main.py --config configs/test_order_spike.json
```
- **并发**：10→200 线程
- **时长**：45 秒
- **操作**：分页查询
- **目的**：测试突发流量处理能力

## 🎓 测试策略建议

### 初次测试
1. 数据预填充：1 万条（`batchSize: 100, batchCount: 100`）
2. 运行基础测试：插入、主键查询、分页查询
3. 观察 Grafana 指标是否正常

### 压力测试
1. 数据预填充：10 万条（`batchSize: 1000, batchCount: 100`）
2. 运行渐进式压测找到性能拐点
3. 运行峰值测试验证极限性能

### 稳定性测试
1. 数据预填充：10 万条
2. 运行混合操作测试 30 分钟
3. 观察连接池和内存是否稳定

## 📈 期望结果

基于中等配置硬件：

| 测试场景 | 预期 QPS | 响应时间 P95 | 成功率 |
|---------|---------|-------------|--------|
| 单条插入 | 500-2000 | < 50ms | > 99% |
| 主键查询 | 5000-10000 | < 10ms | 100% |
| 分页查询 | 1000-3000 | < 50ms | 100% |
| 更新操作 | 1000-3000 | < 30ms | > 99% |
| 混合操作 | 2000-5000 | < 40ms | > 99% |

## 🔗 相关文档

- [详细测试指南](docs/PERFORMANCE_TEST_GUIDE.md)
- [快速参考手册](TESTING_QUICK_REFERENCE.md)
- [api-test 框架文档](README.md)
- [SQL 脚本](../docs/data/mysql/test_order.sql)

## ⚠️ 注意事项

1. 确保应用已启动（端口 8338）
2. 确保数据库表已创建
3. 先进行数据预填充再做性能测试
4. 测试时实时观察 Grafana 监控
5. 保存测试报告以便对比分析

---

**开始测试，观察 Hikari 连接池表现！** 🚀

