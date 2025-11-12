# test_order_detail 表使用说明

## 📋 表的设计目的

`test_order_detail` 表是为了测试以下场景而设计的：

1. **JOIN 查询性能** - 测试主表关联从表的查询性能
2. **事务性能** - 测试同时插入主表和从表的事务性能
3. **一对多关系** - 模拟真实业务中的订单-明细关系
4. **复杂查询** - 测试聚合查询、分组查询等

## 🎯 新增的接口

### 1. 插入订单及明细

**接口**：`POST /api/test/order/insertWithDetails`

**功能**：
- 在一个事务中同时插入订单和明细
- 每个订单随机生成 1-5 个明细
- 用于测试事务性能和为 JOIN 查询准备数据

**使用示例**：
```bash
curl -X POST http://127.0.0.1:8338/api/test/order/insertWithDetails \
  -H "Content-Type: application/json" \
  -d '{
    "orderNo": "ORD20251103001",
    "userId": 1001,
    "productId": 100,
    "productName": "iPhone 15 Pro",
    "quantity": 2,
    "unitPrice": 7999.00,
    "totalAmount": 15998.00,
    "status": 0,
    "paymentMethod": "支付宝",
    "shippingAddress": "北京市朝阳区建国门外大街1号"
  }'
```

### 2. 查询订单及明细（JOIN 查询）

**接口**：`GET /api/test/order/withDetail/{orderId}`

**功能**：
- 查询订单信息
- 关联查询该订单的所有明细
- 用于测试 JOIN 查询性能

**使用示例**：
```bash
curl http://127.0.0.1:8338/api/test/order/withDetail/12345
```

**响应示例**：
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "order": {
      "orderId": 12345,
      "orderNo": "ORD20251103001",
      "userId": 1001,
      "productName": "iPhone 15 Pro",
      "totalAmount": 15998.00,
      ...
    },
    "details": [
      {
        "detailId": 1,
        "orderId": 12345,
        "productId": 100,
        "productName": "iPhone 15 Pro",
        "quantity": 2,
        "unitPrice": 7999.00,
        "subtotal": 15998.00
      }
    ],
    "detailCount": 1
  }
}
```

## 🧪 测试配置

### 1. 事务性能测试

**配置文件**：`configs/test_order_insert_with_details.json`

```bash
cd api-test
python main.py --config configs/test_order_insert_with_details.json
```

**测试说明**：
- 测试同时插入订单和明细的性能
- 每个请求插入 1 个订单 + 1-5 个明细
- 验证事务的正确性和性能

**关注指标**：
- 插入性能应比单表稍慢（因为要插入多个表）
- 成功率应该 100%（事务要么全成功，要么全失败）
- 响应时间 P95 应在 100ms 以内

### 2. JOIN 查询性能测试

**配置文件**：`configs/test_order_join_query.json`

```bash
python main.py --config configs/test_order_join_query.json
```

**测试说明**：
- 测试订单关联明细的查询性能
- 模拟实际业务中查看订单详情的场景
- 验证 JOIN 查询的性能表现

**关注指标**：
- JOIN 查询性能应比单表查询慢
- 响应时间 P95 应在 50ms 以内
- 观察数据库执行计划是否使用了索引

## 📊 完整测试流程

### 步骤 1：预填充主表数据

使用原有的批量插入接口：

```bash
curl -X POST http://127.0.0.1:8338/api/test/order/batchInsert \
  -H "Content-Type: application/json" \
  -d '{"batchSize": 1000, "batchCount": 10, "startUserId": 1}'
```

这将在 `test_order` 表中插入 1 万条数据（没有明细）。

### 步骤 2：插入带明细的订单

使用新接口插入带明细的订单：

```bash
cd api-test
python main.py --config configs/test_order_insert_with_details.json
```

这将插入订单同时插入明细到 `test_order_detail` 表。

### 步骤 3：测试 JOIN 查询

```bash
python main.py --config configs/test_order_join_query.json
```

## 🎯 测试场景对比

| 测试类型 | 涉及表 | 配置文件 | 用途 |
|---------|-------|---------|------|
| 单表插入 | test_order | `test_order_insert.json` | 基础插入性能 |
| 事务插入 | test_order + test_order_detail | `test_order_insert_with_details.json` | 事务性能 |
| 单表查询 | test_order | `test_order_query_pk.json` | 基础查询性能 |
| JOIN 查询 | test_order + test_order_detail | `test_order_join_query.json` | JOIN 性能 |

## 💡 为什么需要这个表？

### 1. 真实业务场景

在实际业务中，订单系统通常有主表和明细表的关系：
- **订单主表**：记录订单基本信息
- **订单明细表**：记录订单中的商品明细

### 2. 性能对比测试

通过对比单表和多表操作性能，可以了解：
- JOIN 查询的性能开销
- 事务操作的性能影响
- 索引在关联查询中的作用

### 3. 监控指标观察

在 JOIN 查询场景下观察：
- **连接池使用情况** - JOIN 查询通常占用连接时间更长
- **数据库负载** - JOIN 查询对数据库 CPU 压力更大
- **响应时间差异** - 对比单表查询和 JOIN 查询的响应时间

## 🔍 SQL 执行计划分析

测试时可以查看 SQL 执行计划：

```sql
-- 查看 JOIN 查询的执行计划
EXPLAIN SELECT o.*, d.* 
FROM test_order o 
LEFT JOIN test_order_detail d ON o.order_id = d.order_id 
WHERE o.order_id = 12345;

-- 确认是否使用了索引
-- type 应该是 eq_ref 或 ref
-- key 应该显示使用的索引名
```

## 📈 性能预期

### 单表操作 vs 多表操作

| 操作类型 | 预期 QPS | 响应时间 P95 |
|---------|---------|-------------|
| 单表插入 | 1000-2000 | < 30ms |
| 多表插入（事务） | 500-1000 | < 100ms |
| 单表查询 | 5000-10000 | < 10ms |
| JOIN 查询 | 2000-5000 | < 30ms |

**结论**：多表操作性能约为单表操作的 50-70%，这是正常的。

## 🎓 测试建议

### 测试顺序

1. **先测试单表操作** - 建立性能基线
2. **再测试多表操作** - 对比性能差异
3. **分析性能差异原因** - 优化配置

### 优化方向

如果 JOIN 查询性能不理想：
1. 检查索引是否正确创建（`idx_order_id`）
2. 考虑使用缓存
3. 评估是否可以冗余部分数据避免 JOIN

## 📞 接口清单更新

现在总共有 **12 个测试接口**：

| 序号 | 接口 | 方法 | 路径 |
|------|------|------|------|
| 1 | 单条插入 | POST | `/api/test/order/insert` |
| 2 | **插入带明细** | POST | `/api/test/order/insertWithDetails` |
| 3 | 批量插入 | POST | `/api/test/order/batchInsert` |
| 4 | 主键查询 | GET | `/api/test/order/get/{id}` |
| 5 | **JOIN 查询** | GET | `/api/test/order/withDetail/{id}` |
| 6 | 订单号查询 | GET | `/api/test/order/getByOrderNo` |
| 7 | 条件查询 | GET | `/api/test/order/query` |
| 8 | 分页查询 | GET | `/api/test/order/page` |
| 9 | 更新操作 | PUT | `/api/test/order/update` |
| 10 | 删除操作 | DELETE | `/api/test/order/delete/{id}` |
| 11 | 统计查询 | GET | `/api/test/order/statistics` |
| 12 | 混合操作 | POST | `/api/test/order/mixedOperation` |

---

**现在 test_order_detail 表已完全集成到测试体系中！** ✅

