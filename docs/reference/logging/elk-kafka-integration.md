# ELK + Kafka 日志集成指南

## 概述

本文档介绍如何使用 ELK（Elasticsearch, Logstash, Kibana）+ Kafka 构建完整的日志收集、处理和可视化系统。

## 架构

### 整体架构

```
┌─────────────────────┐
│  Spring Boot App    │
│  (KafkaAppender)    │
└──────────┬──────────┘
           │ 异步发送 JSON 日志
           ↓
┌─────────────────────┐
│      Kafka          │
│  - application-logs │
│  - access-logs      │
└──────────┬──────────┘
           │ Logstash 消费
           ↓
┌─────────────────────┐
│     Logstash        │
│  - 解析 JSON        │
│  - 添加字段         │
│  - 数据转换         │
└──────────┬──────────┘
           │ 写入索引
           ↓
┌─────────────────────┐
│  Elasticsearch      │
│  - application-logs-*│
│  - access-logs-*    │
└──────────┬──────────┘
           │ 查询和聚合
           ↓
┌─────────────────────┐
│      Kibana         │
│  - Discover         │
│  - Dashboard        │
│  - Visualize        │
└─────────────────────┘
```

### 数据流

1. **日志生成**: Spring Boot 应用使用 KafkaAppender 发送日志
2. **消息队列**: Kafka 作为缓冲，提供削峰填谷能力
3. **日志处理**: Logstash 消费、解析和转换日志
4. **日志存储**: Elasticsearch 存储和索引日志数据
5. **日志查询**: Kibana 提供可视化查询界面

## 部署步骤

### 前置条件

- Docker 和 Docker Compose 已安装
- 至少 4GB 可用内存
- 至少 10GB 可用磁盘空间

### 1. 启动 Docker 环境

```bash
cd dockers

# 创建必要的目录
mkdir -p logstash/{pipeline,config,volumes/data}
mkdir -p kibana/{config,volumes/data}

# 设置权限
chmod -R 777 elasticsearch/volumes
chmod -R 777 logstash/volumes
chmod -R 777 kibana/volumes

# 启动所有服务
docker-compose up -d

# 等待服务启动（约 2-3 分钟）
docker-compose ps
```

### 2. 验证服务状态

```bash
# 检查 Elasticsearch
curl http://localhost:9201/_cluster/health?pretty

# 检查 Logstash
curl http://localhost:9600/?pretty

# 检查 Kibana
curl http://localhost:5601/api/status

# 检查 Kafka
docker-compose exec kafka kafka-topics.sh \
  --bootstrap-server localhost:29092 --list
```

### 3. 初始化索引模板

```bash
# 给脚本添加执行权限
chmod +x elasticsearch-init-templates.sh

# 执行初始化
./elasticsearch-init-templates.sh
```

预期输出：
```
等待 Elasticsearch 启动...
Elasticsearch 已就绪，开始创建索引模板...
创建 application-logs 索引模板...
创建 jetty-access-logs 索引模板...
索引模板创建完成！
```

### 4. 初始化 Kibana

```bash
# 给脚本添加执行权限
chmod +x kibana-init-config.sh

# 执行初始化
./kibana-init-config.sh
```

预期输出：
```
等待 Kibana 启动...
Kibana 已就绪，开始创建配置...
创建 application-logs Index Pattern...
创建 jetty-access-logs Index Pattern...
Kibana 配置完成！
```

### 5. 配置 Spring Boot 应用

在 `application.properties` 中配置 KafkaAppender：

```properties
# 应用日志配置
sprival.logging.application.output-target=kafka
sprival.logging.application.bootstrap-servers=localhost:9092
sprival.logging.application.topic=application-logs
sprival.logging.application.compression-type=gzip

# 访问日志配置
sprival.logging.jetty-access.output-target=kafka
sprival.logging.jetty-access.bootstrap-servers=localhost:9092
sprival.logging.jetty-access.topic=jetty-access-logs
sprival.logging.jetty-access.compression-type=gzip
```

### 6. 启动应用并生成日志

```bash
# 启动 Spring Boot 应用
java -jar sprival-*.jar

# 或使用 Maven
mvn spring-boot:run
```

### 7. 在 Kibana 中查看日志

1. 访问 http://localhost:5601
2. 点击左侧菜单的 "Discover"
3. 选择 Index Pattern: `application-logs-*`
4. 设置时间范围为"Last 15 minutes"
5. 开始查看和搜索日志

## 使用指南

### Kibana Discover 使用

#### 基本搜索

**搜索特定日志级别**:
```
level: ERROR
```

**搜索包含关键词的日志**:
```
message: *exception*
```

**搜索特定Logger的日志**:
```
loggerName: "com.soyokra.sprival.app.service.UserService"
```

#### 高级搜索

**组合条件**:
```
level: ERROR AND loggerName: *UserService*
```

**范围查询**:
```
processingTime > 1000
```

**MDC 字段搜索**:
```
mdc.userId: "12345"
```

**IP 地址搜索**:
```
clientIp: 192.168.1.0/24
```

#### 时间过滤

- **Quick**: 快速选择（Last 15 minutes, Last 1 hour, etc.）
- **Relative**: 相对时间（Last 2 hours）
- **Absolute**: 绝对时间范围
- **Refresh**: 设置自动刷新间隔（实时监控）

### 创建 Dashboard

#### 1. 创建 Visualization

**示例：错误日志趋势图**

1. 进入 "Visualize Library"
2. 点击 "Create visualization"
3. 选择 "Line" 图表类型
4. 选择 Index Pattern: `application-logs-*`
5. 配置：
   - X-axis: Date Histogram (@timestamp)
   - Y-axis: Count
   - Filter: level: ERROR
6. 保存为 "Error Logs Trend"

**示例：HTTP 状态码分布**

1. 选择 "Pie" 图表
2. Index Pattern: `jetty-access-logs-*`
3. 配置：
   - Slice by: Terms (statusCode)
   - Metric: Count
4. 保存为 "HTTP Status Distribution"

#### 2. 组合 Dashboard

1. 进入 "Dashboard"
2. 点击 "Create dashboard"
3. 添加已保存的 Visualizations
4. 调整布局和大小
5. 保存 Dashboard

### 日志分析示例

#### 场景 1: 查找慢请求

在 Discover 中搜索：
```
processingTime > 5000
```

添加字段：
- uri
- statusCode
- processingTime
- clientIp

排序：按 processingTime 降序

#### 场景 2: 分析错误日志

搜索：
```
level: ERROR AND NOT message: *SocketTimeoutException*
```

查看：
- 错误消息
- 异常堆栈
- 发生时间
- 相关上下文（MDC）

#### 场景 3: 用户行为追踪

使用 MDC 中的 userId：
```
mdc.userId: "user123"
```

按时间排序，查看用户的完整操作轨迹。

#### 场景 4: API 性能分析

在 `jetty-access-logs-*` 中：
```
uri: /api/users/* AND processingTime > 1000
```

创建聚合：
- Avg processingTime by uri
- Max processingTime by uri
- 95th percentile

## 性能优化

### Logstash 优化

#### 增加处理能力

编辑 `logstash/config/logstash.yml`:
```yaml
pipeline.workers: 4          # 增加 worker 数量
pipeline.batch.size: 250     # 增加批处理大小
pipeline.batch.delay: 50     # 调整延迟
```

#### 内存优化

编辑 `docker-compose.yml`:
```yaml
logstash:
  environment:
    - "LS_JAVA_OPTS=-Xms1g -Xmx1g"
```

### Elasticsearch 优化

#### 索引刷新间隔

```bash
curl -X PUT "http://localhost:9201/application-logs-*/_settings" \
  -H 'Content-Type: application/json' -d'
{
  "index": {
    "refresh_interval": "30s"
  }
}'
```

#### 合并策略

```bash
curl -X PUT "http://localhost:9201/application-logs-*/_settings" \
  -H 'Content-Type: application/json' -d'
{
  "index": {
    "merge.scheduler.max_thread_count": 1
  }
}'
```

### Kafka 优化

已有配置已优化，建议监控：
- Consumer lag
- Partition 分布
- 磁盘使用

## 监控和维护

### 监控指标

#### Elasticsearch

```bash
# 集群健康
curl http://localhost:9201/_cluster/health?pretty

# 节点统计
curl http://localhost:9201/_nodes/stats?pretty

# 索引统计
curl http://localhost:9201/application-logs-*/_stats?pretty
```

#### Logstash

```bash
# Pipeline 统计
curl http://localhost:9600/_node/stats/pipelines?pretty

# JVM 内存
curl http://localhost:9600/_node/stats/jvm?pretty
```

#### Kafka

```bash
# 进入容器
docker-compose exec kafka bash

# Consumer lag
kafka-consumer-groups.sh --bootstrap-server localhost:29092 \
  --describe --group logstash-consumer-group
```

### 日志保留策略

#### 自动删除旧索引

创建脚本 `cleanup-old-indices.sh`:
```bash
#!/bin/bash
# 删除 30 天前的索引

DAYS_TO_KEEP=30
DATE_CUTOFF=$(date -d "$DAYS_TO_KEEP days ago" +%Y.%m.%d)

curl -X GET "http://localhost:9201/_cat/indices/application-logs-*?h=index" | \
while read index; do
  INDEX_DATE=$(echo $index | grep -oP '\d{4}\.\d{2}\.\d{2}')
  if [[ "$INDEX_DATE" < "$DATE_CUTOFF" ]]; then
    echo "Deleting index: $index"
    curl -X DELETE "http://localhost:9201/$index"
  fi
done
```

添加到 crontab:
```bash
0 2 * * * /path/to/cleanup-old-indices.sh
```

### 备份和恢复

#### 配置快照仓库

```bash
curl -X PUT "http://localhost:9201/_snapshot/my_backup" \
  -H 'Content-Type: application/json' -d'
{
  "type": "fs",
  "settings": {
    "location": "/usr/share/elasticsearch/backups"
  }
}'
```

#### 创建快照

```bash
curl -X PUT "http://localhost:9201/_snapshot/my_backup/snapshot_1" \
  -H 'Content-Type: application/json' -d'
{
  "indices": "application-logs-*,jetty-access-logs-*",
  "ignore_unavailable": true,
  "include_global_state": false
}'
```

#### 恢复快照

```bash
curl -X POST "http://localhost:9201/_snapshot/my_backup/snapshot_1/_restore"
```

## 故障排除

### 问题 1: Logstash 无法消费 Kafka 消息

**症状**: Kibana 中看不到日志

**排查步骤**:

1. 检查 Kafka topic 是否有数据：
```bash
docker-compose exec kafka kafka-console-consumer.sh \
  --bootstrap-server localhost:29092 \
  --topic application-logs --max-messages 10
```

2. 检查 Logstash Pipeline 状态：
```bash
curl http://localhost:9600/_node/stats/pipelines?pretty
```

3. 查看 Logstash 日志：
```bash
docker-compose logs logstash | tail -100
```

**解决方案**:
- 确认 Kafka 地址正确（kafka:29092）
- 检查网络连通性
- 重启 Logstash

### 问题 2: Elasticsearch 内存不足

**症状**: 服务崩溃或响应慢

**解决方案**:

增加 JVM 内存：
```yaml
# docker-compose.yml
elasticsearch:
  environment:
    - "ES_JAVA_OPTS=-Xms2g -Xmx2g"
```

### 问题 3: Kibana 无法连接 Elasticsearch

**症状**: Kibana 显示连接错误

**解决方案**:

1. 检查 Elasticsearch 状态：
```bash
docker-compose ps elasticsearch
curl http://localhost:9201/_cluster/health
```

2. 检查 Kibana 配置：
```bash
docker-compose exec kibana cat /usr/share/kibana/config/kibana.yml
```

3. 重启 Kibana：
```bash
docker-compose restart kibana
```

### 问题 4: 日志时间不正确

**症状**: Kibana 中看到的时间与实际不符

**解决方案**:

检查 Logstash 时区配置：
```conf
# logstash.conf
filter {
  date {
    match => ["timestamp", "UNIX_MS"]
    target => "@timestamp"
    timezone => "Asia/Shanghai"  # 确保时区正确
  }
}
```

## 最佳实践

### 1. 索引命名

使用时间后缀，便于管理和清理：
- `application-logs-2025.10.22`
- `jetty-access-logs-2025.10.22`

### 2. 字段映射

明确定义字段类型，避免动态映射：
- IP 地址使用 `ip` 类型
- 数值使用 `long` 或 `integer`
- 关键词使用 `keyword`
- 全文搜索使用 `text`

### 3. 日志级别

合理使用日志级别：
- ERROR: 严重错误，需要立即处理
- WARN: 警告信息，需要关注
- INFO: 重要信息
- DEBUG: 调试信息（生产环境慎用）

### 4. MDC 使用

在业务代码中使用 MDC 添加上下文：
```java
MDC.put("userId", userId);
MDC.put("requestId", requestId);
MDC.put("action", "createOrder");
```

### 5. 性能监控

定期监控：
- Consumer lag
- 磁盘使用率
- JVM 内存使用
- 查询响应时间

## 安全建议

### 开发环境（当前配置）

- 无认证
- HTTP 明文传输
- 所有端口暴露

### 生产环境建议

1. **启用认证**:
```yaml
elasticsearch:
  environment:
    - xpack.security.enabled=true
    - ELASTIC_PASSWORD=strongpassword
```

2. **使用 HTTPS**:
```yaml
elasticsearch:
  environment:
    - xpack.security.http.ssl.enabled=true
```

3. **网络隔离**:
- 仅暴露 Kibana 端口
- ES 和 Logstash 使用内部网络
- 使用反向代理

4. **访问控制**:
- 配置 Kibana 用户认证
- 使用角色和权限管理

## 相关文档

- [日志集成参考文档](./README.md)
- [Docker 部署 README](../../dockers/README-ELK.md)
- [ELK 架构规划](../../ai-home/elk-kafka-logging-plan.md)

---

**版本**: 1.0.0  
**更新时间**: 2025-10-22

