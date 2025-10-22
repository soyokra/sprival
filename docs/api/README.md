# Sprival API 文档

## 概述

本目录包含 Sprival 项目的 API 级别文档，提供代码级别的详细说明，类似于 [Spring Boot API Documentation](https://docs.spring.io/spring-boot/api/java/index.html)。

## 文档结构

API 文档按照 Java 包结构组织，每个包、类、接口都有对应的说明文档。

```
docs/api/
├── README.md                          # API 文档索引（本文档）
└── com/
    └── soyokra/
        └── sprival/
            ├── package-summary.md     # 根包概述
            ├── config/                # 配置类 API
            ├── app/                   # 应用类 API
            └── support/               # 支持类 API
```

## 核心包说明

### com.soyokra.sprival.config
配置类包，包含各组件的配置类、属性类和自动配置类。

**主要类**:
- Redis 配置类
- MySQL 配置类  
- Kafka 配置类
- MongoDB 配置类
- RabbitMQ 配置类
- ClickHouse 配置类
- Elasticsearch 配置类
- Jetty HTTP Server 配置类

### com.soyokra.sprival.app
应用业务代码包，包含 HTTP 请求处理、业务逻辑、数据访问等。

**子包**:
- `app.http` - HTTP 请求相关（Controller、Request、Response、Middleware）
- `app.model` - 数据模型和实体类
- `app.service` - 业务服务层
- `app.repository` - 数据仓库层（数据库、ES等）
- `app.exception` - 异常处理
- `app.aspect` - 切面类
- `app.client` - HTTP 客户端

### com.soyokra.sprival.support
支持类包，提供日志、工具类等基础支持功能。

**子包**:
- `support.logging` - 日志基础类

## 阅读建议

1. **查找类**: 根据包结构快速定位到目标类的文档
2. **了解用途**: 阅读类的概述了解其职责和使用场景
3. **查看方法**: 查看方法签名、参数、返回值和异常说明
4. **参考示例**: 通过代码示例了解具体用法
5. **交叉引用**: 跟随相关类链接深入了解

## 相关文档

- [参考文档](../reference/README.md) - 功能特性的完整使用指南
- [组件集成指南](../reference/components/README.md) - 各组件的集成说明
- [目录结构规范](../reference/DIRECTORY-STRUCTURE.md) - 项目目录结构说明

## 文档约定

本 API 文档遵循以下约定：

- **包名**: 采用全小写，用点分隔
- **类名**: 采用大驼峰命名法（PascalCase）
- **方法名**: 采用小驼峰命名法（camelCase）
- **参数说明**: 包含参数名、类型、是否必需、默认值、说明
- **返回值说明**: 包含返回类型、返回值含义
- **异常说明**: 包含可能抛出的异常类型和抛出条件
- **代码示例**: 提供完整可运行的示例代码

---

*注: API 文档持续更新中，如发现错误或缺失，请提交 Issue 或 PR。*

