# API文档生成模板

## 模板信息
- **版本**: 1.0
- **适用场景**: 生成REST API文档和接口说明
- **更新时间**: 2024-01-01

## 基础API文档模板

```markdown
请为Sprival项目生成API文档，具体要求如下：

## 项目上下文
- 项目: Sprival Spring Boot组件集成模板
- Web框架: Spring Boot Web + Jetty
- API版本: {API_VERSION}
- 基础路径: {BASE_PATH}

## API信息
- 控制器类: {CONTROLLER_CLASS}
- 业务模块: {BUSINESS_MODULE}
- 功能描述: {FUNCTION_DESCRIPTION}
- 权限要求: {PERMISSION_REQUIREMENTS}

## 接口列表
{API_ENDPOINTS_LIST}

## 文档要求
1. **接口概览**: 包含所有接口的基本信息表格
2. **详细说明**: 每个接口的详细参数和返回值说明
3. **请求示例**: 提供完整的请求示例（curl和代码）
4. **响应示例**: 提供成功和错误的响应示例
5. **状态码说明**: 列出所有可能的HTTP状态码及含义
6. **数据模型**: 相关的实体类和DTO说明
7. **错误处理**: 统一的错误响应格式说明

## 输出格式
使用Markdown格式，包含：
- 接口概览表格
- 详细的接口文档
- 请求/响应示例
- 数据模型定义
- 错误码对照表

请确保文档内容完整、准确、易于理解。
```

## 专用API文档模板

### REST CRUD接口文档
```markdown
请为Sprival项目的{ENTITY_NAME}实体生成完整的CRUD API文档：

## 项目上下文
- 实体类: {ENTITY_CLASS}
- 控制器: {CONTROLLER_CLASS}
- 服务类: {SERVICE_CLASS}
- 数据表: {TABLE_NAME}

## 接口规范
- 基础路径: /api/v1/{entity_path}
- 认证方式: {AUTH_METHOD}
- 内容类型: application/json
- 字符编码: UTF-8

## CRUD操作接口
1. **创建**: POST /{entity_path}
2. **查询单个**: GET /{entity_path}/{id}
3. **查询列表**: GET /{entity_path}
4. **分页查询**: GET /{entity_path}/page
5. **更新**: PUT /{entity_path}/{id}
6. **部分更新**: PATCH /{entity_path}/{id}
7. **删除**: DELETE /{entity_path}/{id}
8. **批量删除**: DELETE /{entity_path}/batch

## 特殊要求
- 包含分页查询参数说明
- 支持条件筛选和排序
- 统一的响应格式（使用ResponseUtils）
- 完整的参数验证说明
- MyBatis-Plus分页插件集成

请生成完整的API文档，包含所有接口的详细说明。
```

### 业务接口文档模板
```markdown
请为Sprival项目的{BUSINESS_MODULE}业务模块生成API文档：

## 业务上下文
- 业务模块: {BUSINESS_MODULE}
- 业务描述: {BUSINESS_DESCRIPTION}
- 主要功能: {MAIN_FUNCTIONS}
- 相关实体: {RELATED_ENTITIES}

## 技术上下文
- 控制器包: com.soyokra.sprival.controller.{module}
- 服务包: com.soyokra.sprival.service.{module}
- 数据访问: MyBatis-Plus
- 缓存策略: Redis (Redisson)

## 接口分组
### {GROUP_1_NAME}
- {ENDPOINT_1}
- {ENDPOINT_2}
- ...

### {GROUP_2_NAME}
- {ENDPOINT_3}
- {ENDPOINT_4}
- ...

## 业务规则
- 业务约束: {BUSINESS_CONSTRAINTS}
- 数据验证: {VALIDATION_RULES}
- 权限控制: {PERMISSION_RULES}
- 事务处理: {TRANSACTION_RULES}

## 集成组件
- 数据库: {DATABASE_INTEGRATION}
- 缓存: {CACHE_INTEGRATION}
- 消息队列: {MQ_INTEGRATION}
- 外部服务: {EXTERNAL_SERVICES}

请生成业务导向的API文档，突出业务逻辑和使用场景。
```

### 监控和管理接口文档
```markdown
请为Sprival项目的监控和管理接口生成文档：

## 项目上下文
- 监控组件: Spring Boot Actuator
- 指标收集: Micrometer + Prometheus
- 健康检查: 自定义健康指示器
- 管理端点: /actuator/*

## 监控接口
### 健康检查
- /actuator/health - 应用健康状态
- /actuator/health/mysql - MySQL健康状态
- /actuator/health/redis - Redis健康状态
- /actuator/health/mongodb - MongoDB健康状态
- /actuator/health/rabbit - RabbitMQ健康状态

### 指标监控
- /actuator/metrics - 应用指标
- /actuator/prometheus - Prometheus格式指标
- /actuator/info - 应用信息

### 配置管理
- /actuator/configprops - 配置属性
- /actuator/env - 环境信息
- /actuator/beans - Spring Bean信息

## 自定义端点
{CUSTOM_ENDPOINTS}

## 安全配置
- 端点访问控制
- 敏感信息脱敏
- 监控数据安全

请生成完整的监控接口文档，包含使用说明和安全注意事项。
```

## 文档结构规范

### 1. 接口概览表格
```markdown
| 接口路径 | HTTP方法 | 功能描述 | 认证要求 | 状态 |
|---------|----------|----------|----------|------|
| /api/v1/users | GET | 获取用户列表 | 是 | 已实现 |
| /api/v1/users/{id} | GET | 获取用户详情 | 是 | 已实现 |
| /api/v1/users | POST | 创建用户 | 是 | 已实现 |
```

### 2. 详细接口说明
```markdown
## 获取用户列表

### 基本信息
- **接口路径**: `/api/v1/users`
- **请求方法**: `GET`
- **功能描述**: 获取系统中的用户列表，支持分页和条件筛选
- **认证要求**: 需要登录

### 请求参数
#### Query参数
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|-------|------|------|--------|------|
| page | Integer | 否 | 1 | 页码，从1开始 |
| size | Integer | 否 | 20 | 每页大小，最大100 |
| name | String | 否 | - | 用户名模糊查询 |
| status | String | 否 | - | 用户状态：ACTIVE, INACTIVE |

### 响应结果
#### 成功响应 (200)
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [...],
    "total": 100,
    "size": 20,
    "current": 1,
    "pages": 5
  },
  "timestamp": "2024-01-01T12:00:00Z"
}
```

#### 错误响应 (400)
```json
{
  "code": 400,
  "message": "参数验证失败",
  "data": null,
  "timestamp": "2024-01-01T12:00:00Z"
}
```

### 请求示例
#### cURL
```bash
curl -X GET "http://localhost:8080/api/v1/users?page=1&size=10&name=john" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json"
```

#### Java (RestTemplate)
```java
// Java代码示例
```

#### JavaScript (Axios)
```javascript
// JavaScript代码示例
```
```

### 3. 数据模型定义
```markdown
## 数据模型

### User 用户实体
```json
{
  "id": "Long - 用户ID",
  "username": "String - 用户名",
  "email": "String - 邮箱地址",
  "status": "String - 状态：ACTIVE, INACTIVE",
  "createTime": "LocalDateTime - 创建时间",
  "updateTime": "LocalDateTime - 更新时间"
}
```

### UserDTO 用户传输对象
```json
{
  "username": "String - 用户名（必填）",
  "email": "String - 邮箱地址（必填）",
  "password": "String - 密码（必填，创建时）"
}
```
```

### 4. 错误码对照表
```markdown
## 错误码说明

| 错误码 | HTTP状态码 | 错误信息 | 说明 |
|-------|-----------|----------|------|
| 200 | 200 | success | 请求成功 |
| 400 | 400 | 参数验证失败 | 请求参数不正确 |
| 401 | 401 | 未授权访问 | 需要登录认证 |
| 403 | 403 | 权限不足 | 没有访问权限 |
| 404 | 404 | 资源不存在 | 请求的资源未找到 |
| 500 | 500 | 服务器内部错误 | 系统异常 |
```

## 变量说明

### 通用变量
- `{API_VERSION}`: API版本号，如"v1"
- `{BASE_PATH}`: API基础路径，如"/api/v1"
- `{CONTROLLER_CLASS}`: 控制器类名
- `{BUSINESS_MODULE}`: 业务模块名称
- `{FUNCTION_DESCRIPTION}`: 功能描述
- `{PERMISSION_REQUIREMENTS}`: 权限要求说明

### 实体相关变量
- `{ENTITY_NAME}`: 实体名称，如"User"
- `{ENTITY_CLASS}`: 实体类全名
- `{TABLE_NAME}`: 对应的数据表名
- `{entity_path}`: 实体的REST路径，如"users"

### 接口相关变量
- `{API_ENDPOINTS_LIST}`: 接口端点列表
- `{AUTH_METHOD}`: 认证方式
- `{CUSTOM_ENDPOINTS}`: 自定义端点列表

## 使用指南

### 1. 准备工作
- 确认API的完整功能需求
- 收集相关的实体类和DTO
- 了解业务规则和约束条件
- 准备测试数据和示例

### 2. 文档生成流程
1. 选择合适的模板
2. 填充所有必要的变量
3. 生成初始文档
4. 验证文档的准确性
5. 补充示例和说明
6. 进行文档审查

### 3. 文档维护
- API变更时及时更新文档
- 定期检查文档的准确性
- 收集用户反馈并改进
- 保持文档版本与代码同步

## 质量检查清单

### 完整性检查
- [ ] 所有接口都有完整说明
- [ ] 参数和返回值定义清晰
- [ ] 包含成功和错误示例
- [ ] 数据模型定义完整

### 准确性检查
- [ ] 接口路径和方法正确
- [ ] 参数类型和约束准确
- [ ] 响应格式与实际一致
- [ ] 错误码对照表完整

### 可用性检查
- [ ] 文档结构清晰易读
- [ ] 示例代码可直接使用
- [ ] 包含足够的使用说明
- [ ] 提供了常见问题解答

---

*模板版本: 1.0 | 最后更新: 2024-01-01*
