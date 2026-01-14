# MongoDB

## 组件说明

采用官方组件[Spring Data MongoDB](https://spring.io/projects/spring-data-mongodb)

## 架构设计
```
MongoDB 服务器
    ↓
MongoClient（原生驱动，连接管理）
    ↓
MongoDbFactory（Spring 工厂，数据库实例管理）
    ↓
MongoTemplate（Spring 核心操作类，提供 CRUD/查询封装）
    ↓
MongoRepository（业务层接口，简化开发）
```

## 配置说明

### 配置方式

MongoDB支持两种配置方式：**Host配置**和**URI配置**。

#### 1. Host配置（推荐用于单点模式）

Host配置适用于单点模式的MongoDB服务，配置简单直观。

```properties
# 基础连接配置
spring.data.mongodb.host = localhost
spring.data.mongodb.port = 27017
spring.data.mongodb.database = sprival

# 认证配置
spring.data.mongodb.username = admin
spring.data.mongodb.password = workdock
spring.data.mongodb.authentication-database = admin

# 高级配置
spring.data.mongodb.auto-index-creation = true
spring.data.mongodb.uuid-representation = javaLegacy
spring.data.mongodb.field-naming-strategy = org.springframework.data.mongodb.core.convert.DefaultFieldNamingStrategy
```

#### 2. URI配置（推荐用于副本集）

URI配置支持副本集和丰富的连接参数，适合生产环境。

```properties
# 副本集配置示例
spring.data.mongodb.uri = mongodb://admin:workdock@localhost:27017/sprival?authSource=admin&maxPoolSize=20&minPoolSize=5&connectTimeoutMS=10000&socketTimeoutMS=30000&serverSelectionTimeoutMS=5000&heartbeatFrequencyMS=10000&retryWrites=true&retryReads=true

# 单点配置示例
spring.data.mongodb.uri = mongodb://admin:workdock@localhost:27017/sprival?authSource=admin&maxPoolSize=10&minPoolSize=2
```

### 标准URI格式

```plaintext
mongodb://[username:password@]host1[:port1][,host2[:port2],...[,hostN[:portN]]][/[database][?options]]
```

## 配置项详解

### Host配置项

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `spring.data.mongodb.host` | String | localhost | MongoDB服务器主机地址 |
| `spring.data.mongodb.port` | Integer | 27017 | MongoDB服务器端口 |
| `spring.data.mongodb.database` | String | test | 数据库名称 |
| `spring.data.mongodb.username` | String | - | 认证用户名 |
| `spring.data.mongodb.password` | String | - | 认证密码 |
| `spring.data.mongodb.authentication-database` | String | admin | 认证数据库 |
| `spring.data.mongodb.replica-set-name` | String | - | 副本集名称 |
| `spring.data.mongodb.auto-index-creation` | Boolean | true | 是否自动创建索引 |
| `spring.data.mongodb.uuid-representation` | String | javaLegacy | UUID表示方式 |
| `spring.data.mongodb.field-naming-strategy` | String | - | 字段命名策略类名 |
| `spring.data.mongodb.grid-fs-database` | String | - | GridFS数据库名称 |

### 配置注意事项

> **重要**: Host配置和URI配置不能同时使用，选择其中一种方式即可。

- **Host配置**: 适用于单点模式，配置简单，但功能有限
- **URI配置**: 适用于副本集和生产环境，功能丰富，支持更多参数
- **认证配置**: 生产环境建议使用认证，开发环境可以跳过
- **连接池**: 生产环境建议配置合适的连接池大小


## URI参数详解

### 连接配置

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `connectTimeoutMS` | Integer | 10000 | 连接超时时间（毫秒） |
| `socketTimeoutMS` | Integer | 0 | Socket超时时间（毫秒） |
| `serverSelectionTimeoutMS` | Integer | 30000 | 服务器选择超时时间（毫秒） |
| `maxIdleTimeMS` | Integer | 0 | 连接最大空闲时间（毫秒） |
| `maxLifeTimeMS` | Integer | 0 | 连接最大生存时间（毫秒） |

### 连接池配置

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `maxPoolSize` | Integer | 100 | 连接池最大连接数 |
| `minPoolSize` | Integer | 0 | 连接池最小连接数 |
| `waitQueueTimeoutMS` | Integer | 120000 | 等待连接超时时间（毫秒） |

### 副本集配置

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `replicaSet` | String | - | 副本集名称 |
| `heartbeatFrequencyMS` | Integer | 10000 | 心跳频率（毫秒） |
| `localThresholdMS` | Integer | 15 | 本地阈值（毫秒） |

### 认证配置

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `authSource` | String | admin | 认证数据库 |
| `authMechanism` | String | - | 认证机制（MONGO-CR/GSSAPI/PLAIN/MONGODB-X509） |
| `authMechanismProperties` | String | - | 认证机制属性 |

### 读写配置

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `readPreference` | String | primary | 读偏好（primary/primaryPreferred/secondary/secondaryPreferred/nearest） |
| `readPreferenceTags` | String | - | 读偏好标签 |
| `maxStalenessSeconds` | Integer | -1 | 最大延迟时间（秒） |
| `w` | String/Integer | 1 | 写关注级别 |
| `wtimeoutMS` | Integer | 0 | 写超时时间（毫秒） |
| `journal` | Boolean | false | 是否等待日志提交 |

### 安全配置

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `ssl` | Boolean | false | 是否使用SSL |
| `tls` | Boolean | false | 是否使用TLS |
| `tlsInsecure` | Boolean | false | 是否允许不安全的TLS连接 |
| `tlsAllowInvalidHostnames` | Boolean | false | 是否允许无效主机名 |

### 重试配置

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `retryWrites` | Boolean | true | 是否重试写操作 |
| `retryReads` | Boolean | true | 是否重试读操作 |

### 应用配置

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `appName` | String | - | 应用程序名称 |
| `compressors` | String | - | 压缩器列表（zlib/snappy/zstd） |
| `zlibCompressionLevel` | Integer | -1 | zlib压缩级别 |
| `uuidRepresentation` | String | javaLegacy | UUID表示方式 |


### 监控指标

MongoDB驱动提供以下关键监控指标：

| 指标名称 | 类型 | 说明 |
|----------|------|------|
| `mongodb_driver_commands_seconds_count` | Counter | 命令执行次数 |
| `mongodb_driver_commands_seconds_max` | Gauge | 命令最大执行时间 |
| `mongodb_driver_commands_seconds_sum` | Gauge | 命令执行时间总和 |
| `mongodb_driver_pool_checkedout` | Gauge | 当前使用中的连接数 |
| `mongodb_driver_pool_size` | Gauge | 连接池总大小 |
| `mongodb_driver_pool_waitqueuesize` | Gauge | 等待连接的请求数量 |
