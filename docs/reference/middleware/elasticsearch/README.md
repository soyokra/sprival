# Elasticsearch

## 组件说明

官方组件Spring Data Elasticsearch
- [Spring Data Elasticsearch](https://spring.io/projects/spring-data-elasticsearch)

## 配置说明

### 配置方式

Elasticsearch支持两种配置方式：**Host配置**和**URI配置**。

#### 1. Host配置（推荐用于单点模式）

Host配置适用于单点模式的Elasticsearch服务，配置简单直观。

```properties
# 基础连接配置
spring.elasticsearch.uris = http://localhost:8200
spring.elasticsearch.username = 
spring.elasticsearch.password = 

# 超时配置
spring.elasticsearch.connection-timeout = 5s
spring.elasticsearch.socket-timeout = 30s

# 高级配置
spring.elasticsearch.path-prefix = 
spring.data.elasticsearch.repositories.enabled = true
```

#### 2. URI配置（推荐用于集群模式）

URI配置支持集群模式和丰富的连接参数，适合生产环境。

```properties
# 单点配置示例
spring.elasticsearch.uris = http://localhost:8200

# 集群配置示例
spring.elasticsearch.uris = http://node1:9200,http://node2:9200,http://node3:9200
spring.elasticsearch.username = elastic
spring.elasticsearch.password = workdock
spring.elasticsearch.connection-timeout = 5s
spring.elasticsearch.socket-timeout = 30s
```

### 标准URI格式

```plaintext
http://[username:password@]host[:port][/path-prefix]
https://[username:password@]host[:port][/path-prefix]  (SSL连接)
```

## 配置项详解

### Host配置项

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `spring.elasticsearch.uris` | List<String> | http://localhost:9200 | Elasticsearch服务器URI列表，支持多个节点（逗号分隔） |
| `spring.elasticsearch.username` | String | - | 认证用户名 |
| `spring.elasticsearch.password` | String | - | 认证密码 |
| `spring.elasticsearch.connection-timeout` | Duration | 1s | 连接超时时间 |
| `spring.elasticsearch.socket-timeout` | Duration | 30s | Socket超时时间 |
| `spring.elasticsearch.path-prefix` | String | - | 路径前缀 |
| `spring.data.elasticsearch.repositories.enabled` | Boolean | true | 是否启用Elasticsearch Repository |
| `spring.data.elasticsearch.client.reactive.endpoints` | List<String> | - | 响应式客户端端点列表 |
| `spring.data.elasticsearch.client.reactive.use-ssl` | Boolean | false | 是否使用SSL连接 |

### 配置注意事项

> **重要**: Host配置和URI配置不能同时使用，选择其中一种方式即可。

- **Host配置**: 适用于单点模式，配置简单，但功能有限
- **URI配置**: 适用于集群模式和生产环境，功能丰富，支持更多参数
- **认证配置**: 生产环境建议使用认证，开发环境可以跳过
- **超时配置**: 生产环境建议根据实际网络情况配置合适的超时时间
- **集群配置**: 生产环境建议配置多个节点，提高可用性和性能

## URI参数详解

### 连接配置

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `connection-timeout` | Duration | 1s | 连接超时时间 |
| `socket-timeout` | Duration | 30s | Socket超时时间 |

### 安全配置

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `username` | String | - | 认证用户名（在URI中通过@符号前指定） |
| `password` | String | - | 认证密码（在URI中通过@符号前指定） |
| `use-ssl` | Boolean | false | 是否使用SSL连接（https://协议） |

### 路径配置

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `path-prefix` | String | - | 路径前缀（在URI中通过/符号后指定） |

## 监控指标

Elasticsearch驱动提供以下关键监控指标：

| 指标名称 | 类型 | 说明 |
|----------|------|------|
| `elasticsearch_client_requests_total` | Counter | 客户端请求总数 |
| `elasticsearch_client_requests_seconds` | Histogram | 客户端请求耗时分布 |
| `elasticsearch_client_requests_seconds_max` | Gauge | 客户端请求最大耗时 |
| `elasticsearch_client_requests_seconds_sum` | Counter | 客户端请求耗时总和 |
| `elasticsearch_client_connections_active` | Gauge | 当前活跃连接数 |
| `elasticsearch_client_connections_idle` | Gauge | 当前空闲连接数 |
| `elasticsearch_client_connections_total` | Gauge | 连接池总连接数 |