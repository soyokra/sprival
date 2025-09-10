# Sprival项目文件目录规范

## 概述

本文档定义了Sprival项目的标准文件目录结构，确保项目组织清晰、易于维护，并为AI开发提供明确的规范指导。
AI写文档必须在docs目录框架下

## 📁 标准目录结构

```
sprival/
├── 📁 docs/                          # 项目文档
│   ├── 📁 ai-development/            # AI辅助开发文档
│   │   ├── 📁 context/               # 自动生成的上下文文件
│   │   ├── 📁 prompt-templates/      # 提示词模板库
│   │   ├── README.md                 # AI开发规范
│   │   ├── context-management.md     # 上下文管理策略
│   │   ├── project-context-template.md # 项目上下文模板
│   │   └── workflow.md               # 开发工作流程
│   ├── 📁 components/                # 组件文档（重命名自spring-*）
│   │   ├── 📁 http-server/           # HTTP服务器组件
│   │   ├── 📁 http-client/           # HTTP客户端组件
│   │   ├── 📁 mysql/                 # MySQL数据库组件
│   │   ├── 📁 redis/                 # Redis缓存组件
│   │   ├── 📁 clickhouse/            # ClickHouse分析数据库
│   │   ├── 📁 mongodb/               # MongoDB文档数据库
│   │   ├── 📁 rabbitmq/              # RabbitMQ消息队列
│   │   ├── 📁 kafka/                 # Kafka消息队列
│   │   ├── 📁 elasticsearch/         # Elasticsearch搜索引擎
│   │   └── 📁 monitoring/            # 监控组件
│   ├── 📁 deployment/                # 部署相关文档
│   │   ├── docker.md                 # Docker部署指南
│   │   ├── kubernetes.md             # K8s部署指南
│   │   └── environment.md            # 环境配置指南
│   ├── 📁 development/               # 开发相关文档
│   │   ├── coding-standards.md       # 编码规范
│   │   ├── encoding-standards.md     # 编码标准
│   │   ├── system-environment.md     # 系统环境配置
│   │   └── ide-setup.md              # IDE配置指南
│   └── README.md                     # 项目文档索引
├── 📁 scripts/                       # 脚本文件
│   ├── 📁 build/                     # 构建脚本
│   ├── 📁 deployment/                # 部署脚本
│   ├── 📁 development/               # 开发脚本
│   │   ├── ai-dev-start.ps1          # AI开发启动脚本
│   │   └── generate-project-context.ps1 # 项目上下文生成脚本
│   └── 📁 testing/                   # 测试脚本
├── 📁 src/                           # 源代码
│   ├── 📁 main/                      # 主代码
│   │   ├── 📁 java/                  # Java源码
│   │   │   └── 📁 com/soyokra/sprival/
│   │   │       ├── SprivalApplication.java # 主应用类
│   │   │       ├── 📁 config/        # 配置类
│   │   │       │   ├── 📁 http/      # HTTP相关配置
│   │   │       │   ├── 📁 redis/     # Redis配置
│   │   │       │   ├── 📁 mysql/     # MySQL配置
│   │   │       │   ├── 📁 kafka/     # Kafka配置
│   │   │       │   ├── 📁 mongodb/   # MongoDB配置
│   │   │       │   ├── 📁 rabbitmq/  # RabbitMQ配置
│   │   │       │   ├── 📁 clickhouse/ # ClickHouse配置
│   │   │       │   ├── 📁 elasticsearch/ # Elasticsearch配置
│   │   │       │   ├── 📁 jetty/     # Jetty配置
│   │   │       │   ├── 📁 ratelimiter/ # 限流器配置
│   │   │       │   └── 📁 monitoring/ # 监控配置
│   │   │       ├── 📁 app/           # 应用业务代码
│   │   │       │   ├── 📁 http/      # HTTP请求相关
│   │   │       │   │   ├── 📁 controller/ # 控制器
│   │   │       │   │   ├── 📁 request/ # 请求体
│   │   │       │   │   ├── 📁 response/ # 响应体
│   │   │       │   │   └── 📁 middleware/ # 过滤器、拦截器
│   │   │       │   ├── 📁 model/     # 实体类
│   │   │       │   ├── 📁 service/   # 业务服务
│   │   │       │   ├── 📁 repository/ # 数据仓库
│   │   │       │   │   ├── 📁 db/    # 数据库相关
│   │   │       │   │   │   └── 📁 dbname/ # 数据库名称
│   │   │       │   │   │       ├── 📁 mapper/ # 数据库映射类
│   │   │       │   │   │       ├── 📁 entity/ # 数据库表实体类
│   │   │       │   │   │       └── 📁 service/ # 数据库操作服务类
│   │   │       │   │   └── 📁 es/    # Elasticsearch相关
│   │   │       │   ├── 📁 exception/ # 异常处理
│   │   │       │   ├── 📁 aspect/    # 切面类
│   │   │       │   └── 📁 client/    # HTTP请求客户端类
│   │   │       └── 📁 support/       # 支持类
│   │   │           ├── 📁 util/      # 工具类
│   │   │           ├── 📁 health/    # 健康检查基础类
│   │   │           ├── 📁 monitor/   # 监控基础类
│   │   │           └── 📁 logging/   # 日志基础类
│   │   └── 📁 resources/             # 资源文件
│   │       ├── application.properties # 主配置文件
│   │       ├── 📁 config/            # 配置文件目录
│   │       │   ├── redisson.yml      # Redisson配置
│   │       │   ├── spy.properties    # P6Spy配置
│   │       │   └── 📁 profiles/      # 环境配置
│   │       │       ├── dev.properties
│   │       │       ├── test.properties
│   │       │       └── prod.properties
│   │       ├── 📁 mapper/            # MyBatis映射文件
│   │       │   ├── 📁 master/        # 主数据源映射
│   │       │   └── 📁 sprival/       # 业务数据源映射
│   │       ├── 📁 static/            # 静态资源
│   │       └── 📁 templates/         # 模板文件
│   └── 📁 test/                      # 测试代码
│       ├── 📁 java/                  # 测试Java代码
│       └── 📁 resources/             # 测试资源
├── 📁 dockers/                       # Docker配置
│   ├── docker-compose.yml            # 容器编排
│   ├── 📁 clickhouse/                # ClickHouse容器
│   ├── 📁 elasticsearch/             # Elasticsearch容器
│   ├── 📁 grafana/                   # Grafana容器
│   ├── 📁 kafka/                     # Kafka容器
│   ├── 📁 mongodb/                   # MongoDB容器
│   ├── 📁 prometheus/                # Prometheus容器
│   ├── 📁 rabbitmq/                  # RabbitMQ容器
│   └── 📁 redis/                     # Redis容器
├── 📁 tools/                         # 工具和配置
│   ├── 📁 ide/                       # IDE配置
│   │   ├── idea-settings.xml         # IntelliJ IDEA配置
│   │   └── vscode-settings.json      # VS Code配置
│   ├── 📁 maven/                     # Maven配置
│   └── 📁 scripts/                   # 工具脚本
├── 📁 tests/                         # 测试相关
│   ├── 📁 integration/               # 集成测试
│   ├── 📁 performance/               # 性能测试
│   └── 📁 data/                      # 测试数据
├── pom.xml                           # Maven配置
├── README.md                         # 项目说明
├── LICENSE                           # 许可证
├── .gitignore                        # Git忽略文件
├── .editorconfig                     # 编辑器配置
└── start-utf8.bat                    # 启动脚本（保留在根目录）
```

## 📋 目录规范说明

### 1. 根目录文件
- **README.md**: 项目主要说明文档
- **LICENSE**: 开源许可证
- **pom.xml**: Maven项目配置
- **start-utf8.bat**: 主要启动脚本（UTF-8编码）
- **.gitignore**: Git忽略规则
- **.editorconfig**: 编辑器统一配置

### 2. docs/ 文档目录
- **ai-development/**: AI辅助开发相关文档
- **components/**: 各组件详细文档（重命名自spring-*）
- **deployment/**: 部署相关文档
- **development/**: 开发环境配置文档

### 3. scripts/ 脚本目录
- **build/**: 构建相关脚本
- **deployment/**: 部署脚本
- **development/**: 开发辅助脚本
- **testing/**: 测试相关脚本

### 4. src/ 源码目录
- **main/java/**: 主代码，按功能分包
- **main/resources/**: 资源文件，按类型组织
- **test/**: 测试代码

### 5. dockers/ 容器目录
- 按组件组织Docker配置
- 每个组件独立的Dockerfile和配置

### 6. tools/ 工具目录
- **ide/**: IDE配置文件
- **maven/**: Maven相关配置
- **scripts/**: 开发工具脚本

## 🎯 包结构规范

### Java包命名规范
```
com.soyokra.sprival
├── config          # 配置类
│   ├── http        # HTTP相关配置
│   ├── redis       # Redis配置
│   ├── mysql       # MySQL配置
│   └── ...
├── app             # 应用业务代码
│   ├── http        # HTTP请求相关
│   │   ├── controller    # 控制器
│   │   ├── request      # 请求体
│   │   ├── response     # 响应体
│   │   └── middleware   # 过滤器、拦截器
│   ├── model       # 实体类
│   ├── service     # 业务服务
│   ├── repository  # 数据仓库
│   │   ├── db      # 数据库相关
│   │   │   └── dbname  # 数据库名称
│   │   │       ├── mapper   # 数据库映射类
│   │   │       ├── entity  # 数据库表实体类
│   │   │       └── service # 数据库操作服务类
│   │   └── es      # Elasticsearch相关
│   ├── exception   # 异常处理
│   ├── aspect      # 切面类
│   └── client      # HTTP请求客户端类
└── support         # 支持类
    ├── util        # 工具类
    ├── health      # 健康检查基础类
    ├── monitor     # 监控基础类
    └── logging     # 日志基础类
```

### 配置文件组织规范
```
src/main/resources/
├── application.properties    # 主配置文件
├── config/                  # 配置文件目录
│   ├── redisson.yml         # Redisson配置
│   ├── spy.properties       # P6Spy配置
│   └── profiles/            # 环境配置
│       ├── dev.properties   # 开发环境
│       ├── test.properties  # 测试环境
│       └── prod.properties  # 生产环境
└── mapper/                  # MyBatis映射文件
    ├── master/              # 主数据源
    └── sprival/             # 业务数据源
```

## 📝 文件命名规范

### 1. Java类命名
- **配置类**: `Sprival + 组件名 + Configuration`
- **属性类**: `Sprival + 组件名 + Properties`
- **健康检查**: `Sprival + 组件名 + HealthIndicator`
- **自动配置**: `Sprival + 组件名 + AutoConfiguration`
- **控制器**: `功能名 + Controller`
- **请求体**: `功能名 + Request`
- **响应体**: `功能名 + Response`
- **服务类**: `功能名 + Service`
- **实体类**: `功能名 + Entity` 或 `功能名 + Model`
- **映射类**: `功能名 + Mapper`
- **异常类**: `功能名 + Exception`

### 2. 配置文件命名
- **主配置**: `application.properties`
- **组件配置**: `组件名.yml` 或 `组件名.properties`
- **环境配置**: `环境名.properties`

### 3. 文档文件命名
- **组件文档**: `README.md`
- **配置文档**: `配置名.md`
- **部署文档**: `部署方式.md`

## 🎯 新目录结构优势

### 1. 业务代码集中管理
- **app/ 目录**: 所有业务相关代码统一管理
- **清晰分层**: HTTP层、业务层、数据层分离明确
- **模块化**: 按功能模块组织，便于维护和扩展

### 2. HTTP请求处理优化
- **request/**: 统一管理请求体，便于参数验证
- **response/**: 统一管理响应体，便于数据格式化
- **middleware/**: 过滤器、拦截器集中管理

### 3. 数据访问层细化
- **按数据库类型组织**: `db/` 和 `es/` 分离
- **按数据库实例组织**: `dbname/` 便于多数据源管理
- **职责明确**: mapper、entity、service 各司其职

### 4. 基础功能模块化
- **support/ 目录**: 基础功能模块统一管理
- **可复用性**: 工具类、监控、日志等基础功能
- **扩展性**: 便于添加新的基础功能模块

## 🔧 实施指南

### 1. 迁移步骤
1. 创建新的目录结构（app/ 和 support/ 目录）
2. 移动现有文件到对应位置：
   - `controller/` → `app/http/controller/`
   - `service/` → `app/service/`
   - `repository/` → `app/repository/db/`
   - `entity/` → `app/model/` 或 `app/repository/db/dbname/entity/`
   - `dto/` → `app/http/request/` 和 `app/http/response/`
   - `exception/` → `app/exception/`
   - `aspect/` → `app/aspect/`
   - `client/` → `app/client/`
   - `support/` 目录保持不变
3. 更新import路径和配置引用
4. 更新文档中的路径引用
5. 测试确保功能正常

### 2. 维护规范
1. 新文件必须放在对应目录
2. 定期检查目录结构规范性
3. 使用工具自动检查目录结构
4. 在CI/CD中集成结构检查

### 3. AI开发指导
1. 严格按照目录规范创建文件
2. 使用标准命名规范
3. 遵循包结构约定
4. 及时更新相关文档

---

*此规范将根据项目发展持续更新和完善*
