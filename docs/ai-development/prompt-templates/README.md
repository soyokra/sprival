# Sprival 提示词模板库

## 概述

本模板库为Sprival项目的AI辅助开发提供标准化的提示词模板，涵盖代码生成、问题诊断、文档编写、代码审查等各个开发环节。

## 模板分类

### 1. 代码生成类模板
- [配置类生成](./code-generation/configuration-class.md)
- [服务类生成](./code-generation/service-class.md)
- [控制器生成](./code-generation/controller-class.md)
- [实体类生成](./code-generation/entity-class.md)
- [测试类生成](./code-generation/test-class.md)

### 2. 问题诊断类模板
- [错误分析](./diagnostics/error-analysis.md)
- [性能优化](./diagnostics/performance-optimization.md)
- [配置检查](./diagnostics/configuration-check.md)
- [依赖冲突](./diagnostics/dependency-conflict.md)

### 3. 文档生成类模板
- [API文档](./documentation/api-documentation.md)
- [配置说明](./documentation/configuration-guide.md)
- [使用指南](./documentation/user-guide.md)
- [部署文档](./documentation/deployment-guide.md)

### 4. 代码审查类模板
- [代码规范检查](./code-review/coding-standards.md)
- [安全性审查](./code-review/security-review.md)
- [性能评估](./code-review/performance-review.md)
- [架构合理性](./code-review/architecture-review.md)

## 使用方法

### 1. 模板选择
根据具体需求选择对应的模板类别和具体模板。

### 2. 上下文准备
使用模板前，请先准备好相关的上下文信息：
- 项目基本信息
- 相关组件状态
- 具体需求描述
- 技术约束条件

### 3. 模板应用
将准备好的上下文信息填入模板中的占位符，形成完整的提示词。

### 4. 结果验证
对AI生成的结果进行验证，确保符合项目要求和技术规范。

## 通用模板变量

所有模板都支持以下通用变量：

```yaml
# 项目信息
PROJECT_NAME: "Sprival"
PROJECT_VERSION: "0.0.1"
JAVA_VERSION: "1.8"
SPRING_BOOT_VERSION: "2.7.18"
PACKAGE_BASE: "com.soyokra.sprival"

# 技术栈
WEB_SERVER: "Jetty"
DATABASE: "MySQL + ClickHouse"
CACHE: "Redis (Redisson)"
MESSAGING: "Kafka + RabbitMQ"

# 开发规范
CODING_STYLE: "Google Java Style"
TEST_FRAMEWORK: "JUnit 5"
DOCUMENTATION_FORMAT: "Markdown"
```

## 快速开始示例

### 示例1：生成MyBatis-Plus配置类
```markdown
使用模板: code-generation/configuration-class.md

上下文信息:
- 组件: MyBatis-Plus
- 版本: 3.5.12  
- 功能需求: 多数据源配置
- 数据源: MySQL主库 + ClickHouse从库

生成的提示词:
请为Sprival项目生成MyBatis-Plus的多数据源配置类...
```

### 示例2：诊断Redis连接问题
```markdown
使用模板: diagnostics/error-analysis.md

上下文信息:
- 错误类型: Redis连接超时
- 环境: 开发环境
- 错误日志: [具体错误信息]
- 配置信息: [Redis配置]

生成的提示词:
请分析Sprival项目中的Redis连接超时问题...
```

## 模板维护

### 版本控制
- 每个模板都有版本号标识
- 重要更新需要更新版本号
- 保持向下兼容性

### 更新流程
1. 识别需要更新的模板
2. 更新模板内容
3. 测试模板效果
4. 更新版本号和变更日志
5. 提交代码审查

### 质量标准
- 模板应该清晰、具体、可操作
- 包含足够的上下文信息
- 提供示例和使用说明
- 考虑边界情况和异常处理

---

*请查看各子目录中的具体模板文件获取详细的使用说明*
