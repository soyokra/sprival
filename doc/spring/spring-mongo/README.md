# mongo

## 配置说明

mongodb有两种配置方式，一种是分项配置，这种方式只支持单点模式的mongodb服务。如下示例：

```properties
spring.data.mongodb.host = host
spring.data.mongodb.port = port
spring.data.mongodb.username = username
spring.data.mongodb.username = password
spring.data.mongodb.database = database
spring.data.mongodb.authentication-database = authenticationDatabase
spring.data.mongodb.replica-set-name = replicaSetName
```

一种是uri的方式配置，这种方式可以支持副本集的mongodb服务，并且支持通过参数控制客户端线程池相关参数。

标准的mongodb uri 如下所示：

```plaintext
mongodb://[username:password@]host1[:port1][,host2[:port2],...[,hostN[:portN]]][/[database][?options]]
```

配置示例如下：

```properties
spring.data.mongodb.uri = mongodb://username:password@10.1.100.101:27000,10.1.100.102:27000,10.1.100.103:27000/admin?replicaSet=test&authSource=admin&maxpoolsize=10&minpoolsize=2
```

### 配置项说明

对应的java类是MongoProperties

| 属性                                          | 示例值       | 说明                                                                                   |
| ------------------------------------------- | --------- | ------------------------------------------------------------------------------------ |
| spring.data.mongodb.host                    | localhost | Mongo server host. Cannot be set with URI.                                           |
| spring.data.mongodb.port                    | 27017     | Mongo server port. Cannot be set with URI.                                           |
| spring.data.mongodb.uri                     |           | Mongo database URI. Cannot be set with host, port, credentials and replica set name. |
| spring.data.mongodb.database                | test      | Database name.                                                                       |
| spring.data.mongodb.authentication-database | admin     | Authentication database name.                                                        |
| spring.data.mongodb.grid-fs-database        |           | GridFS database name.                                                                |
| spring.data.mongodb.username                | admin     | Login user of the mongo server. Cannot be set with URI.                              |
| spring.data.mongodb.password                | workdock  | Login password of the mongo server. Cannot be set with URI.                          |
| spring.data.mongodb.replica-set-name        | test      | Required replica set name for the cluster. Cannot be set with URI.                   |
| spring.data.mongodb.field-naming-strategy   |           | Fully qualified name of the FieldNamingStrategy to use.                              |

### 连接参数说明

对应的java类是ConnectionString

| 属性                 | 默认值       | 说明                                                                                                 |
| ------------------ | --------- | -------------------------------------------------------------------------------------------------- |
| replicaSet         | test      | 指定副本集名称（连接副本集时必选）                                                                                  |
| authSource         | admin     | 指定认证数据库（默认与 database 一致，若用户在其他数据库创建，需显式指定，如 admin）                                                 |
| ssl                |           | 是否启用 TLS/SSL 加密（true 表示启用）                                                                         |
| readPreference     | secondary | 读偏好（如 primary、secondary、nearest 等）                                                                 |
| writeConcern       | majority  | 写关注（如 1、majority 等）                                                                                |
| maxPoolSize        | 100       | The maximum number of connections in the connection pool                                           |
| minPoolSize        | 0         |                                                                                                    |
| maxIdleTimeMs      | 0         | Maximum idle time of a pooled connection. A connection that exceeds this limit will be closed      |
| maxLifeTimeMs      | 0         | Maximum life time of a pooled connection. A connection that exceeds this limit will be closed      |
| waitQueueTimeoutMs | 0         | The maximum wait time in milliseconds that a thread may wait for a connection to become available. |
| connectTimeoutMs   | 0         | How long a connection can take to be opened before timing out                                      |
| socketTimeoutMs    | 0         | How long a send or receive on a socket can take before timing out                                  |

## 监控预警

| 属性                                    | 类型  | 说明                                                 |
| ------------------------------------- | --- | -------------------------------------------------- |
| mongodb.driver.pool.checkedout        |     | the count of connections that are currently in use |
| mongodb.driver.pool.size              |     |                                                    |
| mongodb.driver.pool.waitqueuesize     |     |                                                    |
| mongodb_driver_commands_seconds_count |     |                                                    |
| mongodb_driver_commands_seconds_max   |     |                                                    |
| mongodb_driver_commands_seconds_sum   |     |                                                    |

## 源码分析

TODO
