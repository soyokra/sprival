# 错误分析模板

## 模板信息
- **版本**: 1.0
- **适用场景**: 分析和解决项目中的各类错误
- **更新时间**: 2024-01-01

## 基础错误分析模板

```markdown
请分析Sprival项目中出现的错误，并提供解决方案：

## 项目上下文
- 项目: Sprival Spring Boot组件集成模板
- Spring Boot版本: 2.7.18
- Java版本: 1.8
- 运行环境: {ENVIRONMENT}

## 错误信息
- 错误类型: {ERROR_TYPE}
- 发生时间: {OCCURRENCE_TIME}
- 错误频率: {ERROR_FREQUENCY}
- 影响范围: {IMPACT_SCOPE}

## 详细错误日志
```
{ERROR_LOGS}
```

## 相关配置
- 配置文件: {CONFIG_FILES}
- 相关组件: {RELATED_COMPONENTS}
- 环境变量: {ENVIRONMENT_VARIABLES}

## 重现步骤
1. {STEP_1}
2. {STEP_2}
3. {STEP_3}
...

## 分析要求
1. **根因分析**: 深入分析错误的根本原因
2. **影响评估**: 评估错误对系统的影响程度
3. **解决方案**: 提供具体可行的解决步骤
4. **预防措施**: 建议预防类似错误的措施
5. **监控建议**: 推荐相关的监控和告警配置

## 输出格式
请按以下格式提供分析结果：
- 错误原因分析
- 解决方案（优先级排序）
- 修复步骤（详细操作）
- 验证方法
- 预防建议
```

## 专用错误分析模板

### 数据库连接错误
```markdown
请分析Sprival项目中的数据库连接错误：

## 项目上下文
- 数据库: MySQL + ClickHouse
- 连接池: HikariCP
- ORM框架: MyBatis-Plus 3.5.12
- 多数据源: Dynamic-Datasource 4.3.1

## 错误详情
- 数据源: {DATASOURCE_NAME}
- 连接URL: {DATABASE_URL}
- 错误类型: {CONNECTION_ERROR_TYPE}
- 错误日志:
```
{DATABASE_ERROR_LOGS}
```

## 配置信息
- 数据源配置: {DATASOURCE_CONFIG}
- 连接池配置: {HIKARI_CONFIG}
- 超时设置: {TIMEOUT_CONFIG}

## 分析重点
1. 检查数据库服务状态
2. 验证连接参数正确性
3. 分析连接池配置合理性
4. 检查网络连通性
5. 验证用户权限设置

## 常见原因及解决方案
请针对以下可能原因进行分析：
- 数据库服务未启动
- 连接参数错误
- 连接池耗尽
- 网络连接问题
- 用户权限不足
- 防火墙阻拦
```

### Redis连接错误
```markdown
请分析Sprival项目中的Redis连接错误：

## 项目上下文
- Redis客户端: Redisson 3.19.3
- Spring组件: Spring Cache + Spring Data Redis
- 连接方式: {CONNECTION_MODE}

## 错误详情
- Redis地址: {REDIS_HOST}:{REDIS_PORT}
- 错误类型: {REDIS_ERROR_TYPE}
- 错误日志:
```
{REDIS_ERROR_LOGS}
```

## 配置信息
- Redisson配置: {REDISSON_CONFIG}
- 连接池配置: {CONNECTION_POOL_CONFIG}
- 超时配置: {TIMEOUT_CONFIG}

## 特殊说明
根据项目架构，Redisson优先级最高，Spring Cache和Spring Data Redis
都使用Redisson客户端连接Redis。

## 分析重点
1. Redis服务状态检查
2. Redisson配置验证
3. 网络连通性测试
4. 认证信息确认
5. 集群模式配置检查（如果适用）

请提供针对性的解决方案。
```

### 消息队列错误
```markdown
请分析Sprival项目中的消息队列错误：

## 项目上下文
- Kafka: Spring Kafka
- RabbitMQ: Spring AMQP
- 错误组件: {MQ_COMPONENT}

## 错误详情
- 队列/Topic: {QUEUE_TOPIC_NAME}
- 操作类型: {OPERATION_TYPE} (生产/消费)
- 错误类型: {MQ_ERROR_TYPE}
- 错误日志:
```
{MQ_ERROR_LOGS}
```

## 配置信息
- 连接配置: {MQ_CONNECTION_CONFIG}
- 生产者配置: {PRODUCER_CONFIG}
- 消费者配置: {CONSUMER_CONFIG}

## 分析重点
1. 消息中间件服务状态
2. 连接参数正确性
3. 队列/Topic存在性
4. 权限配置检查
5. 消息格式验证
6. 序列化/反序列化问题

请提供详细的排查步骤和解决方案。
```

### 应用启动错误
```markdown
请分析Sprival项目的启动错误：

## 项目上下文
- 主启动类: com.soyokra.sprival.SprivalApplication
- Web服务器: Jetty
- 自动配置组件: {AUTO_CONFIG_COMPONENTS}

## 错误详情
- 启动阶段: {STARTUP_PHASE}
- 错误类型: {STARTUP_ERROR_TYPE}
- 错误日志:
```
{STARTUP_ERROR_LOGS}
```

## 配置文件
- application.properties内容:
```
{APPLICATION_PROPERTIES}
```

## 依赖信息
- Maven依赖: 见pom.xml
- 可能冲突的依赖: {CONFLICTING_DEPENDENCIES}

## 分析重点
1. 自动配置类加载问题
2. Bean循环依赖
3. 配置属性绑定错误
4. 依赖版本冲突
5. 端口占用问题
6. 类路径资源缺失

请提供启动问题的完整解决方案。
```

## 错误分类处理指南

### 编译时错误
- 依赖缺失或版本冲突
- 语法错误或类型不匹配
- 注解处理器问题
- Maven构建配置问题

### 运行时错误
- 空指针异常
- 类找不到异常
- 方法调用异常
- 资源访问异常

### 配置错误
- 属性绑定失败
- Bean创建失败
- 自动配置条件不满足
- 外部服务连接失败

### 性能问题
- 内存泄漏
- CPU占用过高
- 数据库查询慢
- 网络请求超时

## 变量说明

### 通用变量
- `{ERROR_TYPE}`: 错误类型分类
- `{ENVIRONMENT}`: 运行环境（dev/test/prod）
- `{ERROR_LOGS}`: 完整的错误日志
- `{OCCURRENCE_TIME}`: 错误发生时间
- `{ERROR_FREQUENCY}`: 错误发生频率
- `{IMPACT_SCOPE}`: 影响范围描述
- `{CONFIG_FILES}`: 相关配置文件路径
- `{RELATED_COMPONENTS}`: 相关组件列表

### 数据库相关变量
- `{DATASOURCE_NAME}`: 数据源名称
- `{DATABASE_URL}`: 数据库连接URL
- `{CONNECTION_ERROR_TYPE}`: 连接错误类型
- `{DATASOURCE_CONFIG}`: 数据源配置内容
- `{HIKARI_CONFIG}`: HikariCP配置
- `{TIMEOUT_CONFIG}`: 超时配置

### Redis相关变量
- `{REDIS_HOST}`: Redis主机地址
- `{REDIS_PORT}`: Redis端口
- `{CONNECTION_MODE}`: 连接模式（单机/集群/哨兵）
- `{REDISSON_CONFIG}`: Redisson配置
- `{CONNECTION_POOL_CONFIG}`: 连接池配置

## 使用流程

### 1. 错误信息收集
- 完整的错误堆栈信息
- 相关的配置文件内容
- 环境和版本信息
- 重现步骤和条件

### 2. 模板选择和填充
- 根据错误类型选择合适的模板
- 填充所有必要的变量信息
- 补充特定的上下文信息

### 3. AI分析和解决
- 使用填充好的模板进行AI分析
- 获取详细的解决方案
- 验证解决方案的可行性

### 4. 解决方案实施
- 按照建议的步骤执行修复
- 验证问题是否解决
- 记录解决过程和经验

## 最佳实践

### 错误日志收集
- 收集完整的错误堆栈信息
- 包含上下文日志信息
- 注意敏感信息的脱敏处理

### 问题重现
- 提供详细的重现步骤
- 说明重现的环境条件
- 记录重现的频率和规律

### 解决方案验证
- 在测试环境先验证解决方案
- 评估解决方案的影响范围
- 准备回滚方案

---

*模板版本: 1.0 | 最后更新: 2024-01-01*
