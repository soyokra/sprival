# Sprival项目文件目录规范

## 概述

本文档定义了Sprival项目的标准文件目录结构，确保项目组织清晰、易于维护，并为AI协同开发提供明确的规范指导。
AI写文档必须在docs目录框架下

## 📁 标准目录结构

```
sprival/
├── 📁 docs/                          # 项目文档
│   ├── 📁 api/                       # API文档目录（类文件说明）
│   │   └── ...                       # JavaDoc风格的类、接口、方法说明
│   ├── 📁 reference/                 # 参考文档目录
│   │   ├── 📁 components/            # 组件集成
│   │   │   ├── 📁 http-server/       # HTTP服务器组件
│   │   │   ├── 📁 http-client/       # HTTP客户端组件
│   │   │   ├── 📁 mysql/             # MySQL数据库组件
│   │   │   ├── 📁 redis/             # Redis缓存组件
│   │   │   ├── 📁 clickhouse/        # ClickHouse分析数据库
│   │   │   ├── 📁 mongodb/           # MongoDB文档数据库
│   │   │   ├── 📁 rabbitmq/          # RabbitMQ消息队列
│   │   │   ├── 📁 kafka/             # Kafka消息队列
│   │   │   ├── 📁 elasticsearch/     # Elasticsearch搜索引擎
│   │   │   └── README.md             # 组件集成索引
│   │   ├── 📁 deployment/            # 云原生部署
│   │   │   ├── docker.md             # Docker部署指南
│   │   │   ├── kubernetes.md         # K8s部署指南
│   │   │   └── environment.md        # 环境配置指南
│   │   ├── 📁 logging/               # 日志集成
│   │   ├── 📁 monitoring/            # 监控集成
│   │   ├── DIRECTORY-STRUCTURE.md    # 目录结构规范
│   │   └── README.md                 # 参考文档索引
├── 📁 src/                           # 源代码
│   ├── 📁 main/                      # 主代码
│   │   ├── 📁 java/                  # Java源码
│   │   │   └── 📁 com/soyokra/sprival/
│   │   │       ├── SprivalApplication.java # 主应用类
│   │   │       ├── 📁 config/        # 配置类
│   │   │       │   ├── 📁 redis/     # Redis配置
│   │   │       │   ├── 📁 mysql/     # MySQL配置
│   │   │       │   ├── 📁 kafka/     # Kafka配置
│   │   │       │   ├── 📁 mongodb/   # MongoDB配置
│   │   │       │   ├── 📁 rabbitmq/  # RabbitMQ配置
│   │   │       │   ├── 📁 clickhouse/ # ClickHouse配置
│   │   │       │   ├── 📁 elasticsearch/ # Elasticsearch配置
│   │   │       │   ├── 📁 jetty/     # Jetty配置
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
│   │   │           └── 📁 logging/   # 日志基础类
│   │   └── 📁 resources/             # 资源文件
│   │       ├── application.properties # 主配置文件
│   │       ├── 📁 config/            # 配置文件目录
│   │       │   ├── spy.properties    # P6Spy配置
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
├── pom.xml                           # Maven配置
├── README.md                         # 项目说明
├── LICENSE                           # 许可证
├── .gitignore                        # Git忽略文件
├── .editorconfig                     # 编辑器配置
```

## 📋 目录规范说明

### 1. 根目录文件
- **README.md**: 项目主要说明文档
- **LICENSE**: 开源许可证
- **pom.xml**: Maven项目配置
- **.gitignore**: Git忽略规则
- **.editorconfig**: 编辑器统一配置

### 2. docs/ 文档目录

#### docs/api/ - API文档目录
存放类文件的API说明文档，类似于 [Spring Boot API Documentation](https://docs.spring.io/spring-boot/api/java/index.html)
- 采用JavaDoc风格
- 包含类、接口、方法的详细说明
- 提供完整的方法签名、参数说明、返回值说明
- 包括使用示例和注意事项

#### docs/reference/ - 参考文档目录
存放学习和使用时的参考文档，类似于 [Spring Boot Reference Documentation](https://docs.spring.io/spring-boot/index.html)
- **components/**: 各组件集成指南和最佳实践
- **deployment/**: 部署相关文档（Docker、Kubernetes等）
- **logging/**: 日志系统配置和使用指南
- **monitoring/**: 监控系统配置和使用指南
- **DIRECTORY-STRUCTURE.md**: 项目目录结构规范
- **README.md**: 参考文档索引

### 3. src/ 源码目录
- **main/java/**: 主代码，按功能分包
- **main/resources/**: 资源文件，按类型组织
- **test/**: 测试代码

### 4. dockers/ 容器目录
- 按组件组织Docker配置
- 每个组件独立的Dockerfile和配置


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

## 📚 文档组织规范

### API文档 (docs/api/)
**用途**: 提供代码级别的API说明，供开发者查阅具体类、接口和方法的使用

**内容要求**:
- 每个包的概述说明
- 类和接口的详细描述
- 方法签名、参数、返回值、异常说明
- 代码示例和使用场景
- 相关类的交叉引用

**组织方式**:
```
docs/api/
├── index.md                    # API文档总索引
├── com/
│   └── soyokra/
│       └── sprival/
│           ├── package-summary.md      # 包概述
│           ├── SprivalApplication.md   # 应用主类
│           ├── config/                 # 配置类API
│           │   ├── package-summary.md
│           │   ├── redis/
│           │   ├── mysql/
│           │   └── ...
│           ├── app/                    # 应用类API
│           │   ├── package-summary.md
│           │   ├── http/
│           │   ├── model/
│           │   ├── service/
│           │   └── ...
│           └── support/                # 支持类API
│               └── ...
```

### 参考文档 (docs/reference/)
**用途**: 提供功能特性的完整说明和使用指南，供开发者学习和参考

**内容要求**:
- 功能特性的概念说明
- 配置方式和参数说明
- 使用示例和最佳实践
- 常见问题和解决方案
- 注意事项和限制说明

**组织方式**:
```
docs/reference/
├── README.md                   # 参考文档总索引
├── components/                 # 组件集成指南
│   ├── README.md               # 组件集成总览
│   ├── redis/
│   │   └── README.md           # Redis集成完整指南
│   ├── mysql/
│   │   └── README.md           # MySQL集成完整指南
│   └── ...
├── deployment/                 # 部署指南
│   ├── docker.md               # Docker部署
│   ├── kubernetes.md           # K8s部署
│   └── environment.md          # 环境配置
├── logging/                    # 日志指南
│   └── README.md
├── monitoring/                 # 监控指南
│   └── README.md
└── DIRECTORY-STRUCTURE.md      # 目录结构规范（本文档）
```

### 文档编写原则
1. **API文档**: 关注"是什么"（What）- 类的职责、方法的功能
2. **参考文档**: 关注"怎么用"（How）- 如何配置、如何使用、如何解决问题
3. **技术导向**: 面向研发人员，避免营销化描述
4. **结构清晰**: 使用标题层级、代码块、列表等提升可读性
5. **示例丰富**: 提供完整可运行的代码示例
6. **持续更新**: 代码变更时同步更新文档
