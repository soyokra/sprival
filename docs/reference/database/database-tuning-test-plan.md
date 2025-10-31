# 数据库调优测试方案

## 📋 方案概述

本文档描述了通过测试和分析监控数据进行数据库调优的完整方案。方案涵盖SQL性能测试、连接池调优、查询优化测试和监控数据分析等维度。

## 🎯 测试目标

1. **SQL性能分析**: 识别慢查询和性能瓶颈
2. **连接池优化**: 评估和优化HikariCP连接池配置
3. **查询优化验证**: 验证索引、查询计划等优化效果
4. **并发性能评估**: 测试数据库在高并发场景下的表现
5. **监控数据收集**: 收集Prometheus指标用于性能分析

## 📊 测试策略

### 1. SQL性能测试

#### 1.1 慢查询识别测试
- **目标**: 识别执行时间超过阈值的SQL语句
- **工具**: P6Spy + 自定义监控
- **方法**: 
  - 启用P6Spy SQL日志
  - 分析SQL执行时间分布
  - 识别Top N慢查询

#### 1.2 SQL执行计划分析测试
- **目标**: 分析查询执行计划，识别全表扫描、索引失效等问题
- **方法**: 
  - 使用EXPLAIN分析查询计划
  - 验证索引使用情况
  - 检测潜在的性能问题

#### 1.3 批量操作性能测试
- **目标**: 测试批量插入、更新、删除操作的性能
- **场景**: 
  - 不同批次大小的性能对比
  - 批量操作的TPS和响应时间

### 2. 连接池性能测试

#### 2.1 连接池配置压力测试
- **目标**: 找出最优的连接池配置参数
- **测试维度**:
  - `max-pool-size`: 最大连接数（10, 20, 50, 100）
  - `min-idle`: 最小空闲连接数（5, 10, 20）
  - `connection-timeout`: 连接超时时间
  - `max-lifetime`: 连接最大生命周期

#### 2.2 连接池指标监控测试
- **监控指标** (HikariCP Metrics):
  - `hikaricp.connections.active`: 活跃连接数
  - `hikaricp.connections.idle`: 空闲连接数
  - `hikaricp.connections.pending`: 等待连接的线程数
  - `hikaricp.connections.timeout`: 连接超时次数
  - `hikaricp.connections.acquire`: 获取连接耗时

### 3. 并发性能测试

#### 3.1 多并发场景测试
- **场景**:
  - 低并发 (10-50): 基准性能
  - 中并发 (100-200): 正常负载
  - 高并发 (500-1000): 峰值负载
  - 极限并发 (1000+): 压力测试

#### 3.2 读写分离测试
- **目标**: 验证主从库读写分离的效果
- **测试点**:
  - 读请求路由到从库
  - 写请求路由到主库
  - 主从延迟影响

### 4. 数据库监控数据收集

#### 4.1 Prometheus指标收集
- **应用层指标**:
  - HikariCP连接池指标
  - SQL执行时间指标
  - 事务处理指标

- **数据库层指标** (通过MySQL Exporter):
  - `mysql_global_status_queries`: 总查询数
  - `mysql_global_status_slow_queries`: 慢查询数
  - `mysql_global_status_threads_connected`: 当前连接数
  - `mysql_global_status_threads_running`: 运行线程数
  - `mysql_global_status_created_tmp_tables`: 临时表创建数
  - `mysql_global_status_table_locks_waited`: 表锁等待数

#### 4.2 P6Spy SQL日志分析
- **收集数据**:
  - SQL语句文本
  - 执行时间
  - 参数值
  - 调用堆栈

## 🔧 测试实现方案

### 测试类结构

```
src/test/java/com/soyokra/sprival/performance/database/
├── SqlPerformanceTest.java              # SQL性能测试
├── ConnectionPoolPerformanceTest.java   # 连接池性能测试
├── QueryOptimizationTest.java           # 查询优化测试
├── ConcurrentDatabaseTest.java          # 并发性能测试
├── DatabaseMonitoringTest.java          # 监控数据收集测试
└── DatabaseTuningAnalysis.java          # 调优分析工具
```

### 测试工具类

```
src/test/java/com/soyokra/sprival/performance/database/util/
├── SqlMetricsCollector.java             # SQL指标收集器
├── ConnectionPoolMetricsCollector.java  # 连接池指标收集器
├── PrometheusMetricsCollector.java      # Prometheus指标收集器
└── DatabasePerformanceAnalyzer.java    # 数据库性能分析器
```

## 📈 测试执行流程

### 阶段1: 基准测试
1. 使用当前配置进行基准性能测试
2. 收集基准性能数据
3. 建立性能基线

### 阶段2: 问题识别
1. 运行慢查询识别测试
2. 分析SQL执行计划
3. 检查连接池状态
4. 收集监控数据

### 阶段3: 调优验证
1. 应用优化措施（如添加索引、调整连接池配置）
2. 重新运行性能测试
3. 对比优化前后的性能指标
4. 验证优化效果

### 阶段4: 持续监控
1. 建立长期监控机制
2. 设置性能告警阈值
3. 定期性能回归测试

## 📊 关键性能指标

### SQL性能指标
- **平均响应时间**: < 50ms (优秀), 50-200ms (良好), > 200ms (需优化)
- **P95响应时间**: < 100ms (优秀), 100-500ms (良好), > 500ms (需优化)
- **P99响应时间**: < 200ms (优秀), 200-1000ms (良好), > 1000ms (需优化)
- **慢查询比例**: < 1% (优秀), 1-5% (良好), > 5% (需优化)

### 连接池指标
- **活跃连接数**: 保持在max-pool-size的50-80%
- **连接获取时间**: < 10ms (优秀), 10-50ms (良好), > 50ms (需优化)
- **连接超时次数**: 0 (优秀), < 10次/小时 (良好), > 10次/小时 (需优化)
- **等待连接数**: 0 (优秀), < 5 (良好), > 5 (需优化)

### 并发性能指标
- **TPS (每秒事务数)**: > 1000 (优秀), 500-1000 (良好), < 500 (需优化)
- **成功率**: > 99.9% (优秀), 99%-99.9% (良好), < 99% (需优化)
- **错误率**: < 0.1% (优秀), 0.1-1% (良好), > 1% (需优化)

## 🎯 调优建议流程

### 1. 问题定位
1. 查看慢查询日志
2. 分析P6Spy SQL日志
3. 检查Prometheus监控指标
4. 识别性能瓶颈

### 2. 优化措施
1. **SQL优化**:
   - 添加缺失的索引
   - 优化查询语句（避免SELECT *）
   - 使用批量操作替代循环操作
   - 优化JOIN查询

2. **连接池优化**:
   - 调整连接池大小
   - 优化连接超时配置
   - 调整连接生命周期

3. **架构优化**:
   - 读写分离
   - 分库分表
   - 缓存策略

### 3. 效果验证
1. 重新运行性能测试
2. 对比优化前后的指标
3. 验证是否达到性能目标

## 📝 测试报告模板

### 测试报告包含内容
1. **测试环境**: 数据库版本、配置、硬件资源
2. **测试场景**: 测试类型、并发数、数据量
3. **测试结果**: 
   - 性能指标对比
   - 问题识别结果
   - 优化建议
4. **优化效果**: 优化前后的性能对比
5. **后续建议**: 进一步优化方向

## 🛠️ 工具和依赖

### 必要工具
- **JMeter / Gatling**: 压力测试工具（可选）
- **Prometheus**: 指标收集
- **Grafana**: 指标可视化
- **P6Spy**: SQL监控
- **JConsole / VisualVM**: JVM监控（可选）

### 测试依赖
- JUnit 5
- Spring Boot Test
- Micrometer (Prometheus集成)
- HikariCP Metrics

## ⚠️ 注意事项

1. **测试环境隔离**: 确保测试不影响生产环境
2. **数据准备**: 使用真实但脱敏的测试数据
3. **资源监控**: 测试过程中监控系统资源（CPU、内存、磁盘IO）
4. **结果验证**: 多次运行测试，取平均值确保结果稳定
5. **渐进式优化**: 一次只优化一个方面，便于定位问题

## 🔄 持续集成

可以将数据库性能测试集成到CI/CD流程：

```yaml
# .gitlab-ci.yml 示例
database-performance-test:
  stage: test
  script:
    - mvn clean test -Dtest=*DatabasePerformanceTest
  artifacts:
    paths:
      - target/performance-reports/
    expire_in: 1 week
  only:
    - develop
    - master
```

---

**维护者**: Sprival Team  
**创建日期**: 2025-01-08  
**版本**: 1.0

