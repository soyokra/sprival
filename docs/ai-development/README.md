# Sprival AI辅助开发文档规范

## 概述

本文档规范旨在为sprival项目的AI辅助开发提供标准化的文档结构、上下文管理策略和提示词模板，确保AI助手能够高效、准确地理解项目需求并提供开发支持。

## 🚀 快速开始

### 1. AI编程前准备

在开始AI编程前，请先运行以下命令生成项目上下文：

```powershell
# 方式1: 使用AI开发启动脚本（推荐）
.\scripts\ai-dev-start.ps1

# 方式2: 仅生成上下文
.\scripts\generate-project-context.ps1

# 方式3: 生成并打开上下文文件
.\scripts\ai-dev-start.ps1 -OpenContext
```

### 2. 查看项目上下文

```powershell
# 查看综合项目上下文
Get-Content docs\ai-development\context\sprival-ai-context-latest.md

# 查看组件状态
Get-Content docs\ai-development\context\component-status-latest.md

# 查看AI编程指导
Get-Content docs\ai-development\context\ai-guidance-latest.md
```

### 3. 验证项目结构

```powershell
# 验证项目结构规范性
.\scripts\validate-project-structure.ps1

# 验证并生成修复脚本
.\scripts\validate-project-structure.ps1 -Fix
```

### 4. 使用上下文模板

参考 `docs/ai-development/project-context-template.md` 中的标准模板，为AI提供结构化的项目信息。

## 项目背景

Sprival是一个Spring Boot组件集成模板项目，提供：
- 全面的pom.xml组件引入
- 各个组件配置使用说明
- 监控指标方案
- K8s部署方案
- ELK日志集成方案
- Prometheus监控集成方案

## 核心组件模块

### 已集成组件 ✅
1. **http-server**: Jetty Web服务器 + Guava RateLimiter接口限流
2. **spring-mysql**: MyBatis-Plus + Dynamic-Datasource + HikariCP + P6Spy
3. **spring-redis**: Spring Cache + Spring Data Redis + Redisson
4. **spring-clickhouse**: ClickHouse集成到MyBatis-Plus
5. **spring-mongo**: Spring Data MongoDB
6. **spring-rabbit**: Spring AMQP
7. **spring-kafka**: Spring Kafka
8. **spring-http-client**: Feign + OkHttp + Resilience4j + LoadBalancer + Micrometer

### 项目特点
- **组件化架构**: 每个组件都有独立的配置类和健康检查
- **统一命名规范**: 所有配置类都以"Sprival"开头
- **健康检查集成**: 所有组件都集成了Spring Boot Actuator健康检查
- **监控友好**: 集成了Micrometer和Prometheus监控
- **UTF-8编码**: 支持跨平台编码兼容

## 文档规范结构

### 1. 目录结构
```
docs/
├── ai-development/           # AI辅助开发相关文档
│   ├── README.md            # 本文档
│   ├── context-management.md # 上下文管理策略
│   ├── project-context-template.md # 项目上下文模板
│   ├── workflow.md          # 开发工作流程
│   ├── prompt-templates/    # 提示词模板库
│   └── context/             # 自动生成的上下文文件
│       ├── sprival-ai-context-latest.md
│       ├── component-status-latest.md
│       ├── ai-guidance-latest.md
│       └── sprival-context-latest.json
├── spring-*/                # 各组件文档
└── deployment/              # 部署相关文档
```

### 2. 文档标准格式

每个组件文档应包含：

#### 基本信息
- **组件名称**
- **版本信息**
- **依赖关系**
- **配置参数**

#### 使用说明
- **快速开始**
- **配置示例**
- **API接口**
- **最佳实践**

#### AI辅助信息
- **关键词标签** (用于AI搜索和理解)
- **常见问题** (FAQ格式)
- **代码模板** (可复用的代码片段)
- **相关组件** (依赖和关联关系)

## 上下文管理策略

### 1. 自动化上下文生成

使用 `scripts/generate-project-context.ps1` 脚本自动生成项目上下文：

```powershell
# 生成完整项目上下文
.\scripts\generate-project-context.ps1

# 输出文件
docs/ai-development/context/
├── sprival-ai-context-latest.md      # 综合项目上下文
├── component-status-latest.md        # 组件状态详情
├── config-status-latest.md           # 配置文件状态
├── dev-environment-latest.md         # 开发环境信息
├── ai-guidance-latest.md             # AI编程指导
└── sprival-context-latest.json       # JSON格式上下文
```

### 2. 项目层级上下文
```markdown
## 项目基本信息
- 项目名称: Sprival
- 技术栈: Spring Boot 2.7.18 + Java 8
- 架构模式: 组件集成模板
- 主要功能: [具体功能列表]

## 当前开发状态
- 已完成模块: [模块列表]
- 开发中模块: [模块列表]  
- 待开发模块: [模块列表]
```

### 3. 组件层级上下文
```markdown
## 组件: [组件名称]
- 版本: [版本号]
- 状态: [开发状态]
- 依赖: [依赖组件列表]
- 配置文件: [相关配置文件路径]
- 源码路径: [Java包路径]
```

### 4. 任务层级上下文
```markdown
## 当前任务
- 任务描述: [具体任务]
- 涉及文件: [文件列表]
- 预期结果: [期望输出]
- 注意事项: [特殊要求]
```

## 提示词模板分类

### 1. 代码生成类
- 配置类生成
- 服务类生成  
- 控制器生成
- 测试类生成

### 2. 问题诊断类
- 错误分析
- 性能优化
- 配置检查
- 依赖冲突

### 3. 文档生成类
- API文档
- 配置说明
- 使用指南
- 部署文档

### 4. 代码审查类
- 代码规范检查
- 安全性审查
- 性能评估
- 架构合理性

## AI交互规范

### 1. 信息提供原则
- **完整性**: 提供足够的上下文信息
- **准确性**: 确保信息的正确性和时效性
- **结构化**: 使用标准化的格式提供信息
- **可追溯**: 记录信息来源和更新时间

### 2. 问题描述规范
```markdown
## 问题描述
[具体问题或需求]

## 相关组件
[涉及的组件或模块]

## 当前状态
[现状描述]

## 期望结果
[期望达到的目标]

## 约束条件
[技术限制或业务限制]
```

### 3. 代码请求规范
```markdown
## 代码需求
[具体的代码生成需求]

## 技术要求
- 框架版本: [版本信息]
- 代码规范: [编码标准]
- 设计模式: [使用的设计模式]

## 输入参数
[相关的输入信息]

## 输出格式
[期望的输出格式]
```

## 开发规范

### 1. 命名规范
- **配置类**: Sprival + 组件名 + Configuration
- **属性类**: Sprival + 组件名 + Properties
- **健康检查**: Sprival + 组件名 + HealthIndicator
- **包结构**: com.soyokra.sprival.config.[组件名]

### 2. 配置模式
- **配置类**: @Configuration + @Bean
- **属性绑定**: @ConfigurationProperties
- **条件配置**: @ConditionalOnClass/@ConditionalOnBean

### 3. 健康检查
- 所有组件都实现HealthIndicator
- 统一的异常处理和降级策略
- 集成到Spring Boot Actuator

## 版本管理

### 文档版本控制
- 使用Git进行版本控制
- 重要更新需要更新版本号
- 保持与代码版本的同步

### 更新日志
记录每次文档更新的内容和原因，便于追踪变更历史。

## 使用指南

### 开发者使用流程
1. 阅读本规范文档
2. 运行上下文生成脚本
3. 根据任务类型选择合适的模板
4. 提供完整的上下文信息
5. 使用标准化的问题描述格式
6. 验证AI输出结果
7. 更新相关文档

### AI助手使用建议
1. 优先查阅项目文档获取上下文
2. 理解组件间的依赖关系
3. 遵循项目的编码规范和架构模式
4. 提供可执行的代码和配置
5. 及时更新相关文档

## 工具集成

### 1. 自动化工具集成

#### Maven集成
```xml
<!-- 上下文信息提取插件 -->
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <configuration>
        <mainClass>com.soyokra.sprival.SprivalApplication</mainClass>
        <!-- 自动生成组件依赖图 -->
        <generateDependencyGraph>true</generateDependencyGraph>
    </configuration>
</plugin>
```

#### Spring Boot Actuator集成
```yaml
management:
  endpoints:
    web:
      exposure:
        include: ["info", "health", "beans", "configprops"]
  info:
    build:
      enabled: true
    git:
      enabled: true
```

### 2. 文档生成自动化

#### 上下文文档模板
```bash
# 自动生成当前项目上下文
.\scripts\generate-project-context.ps1

# 输出格式
docs/ai-development/context/
├── sprival-ai-context-latest.md
├── component-status-latest.md
└── sprival-context-latest.json
```

## 最佳实践

### 1. 上下文维护原则
- **及时性**: 配置和代码变更后立即更新上下文
- **准确性**: 确保上下文信息与实际状态一致
- **完整性**: 提供足够详细的信息支持AI理解
- **结构化**: 使用标准格式便于解析和查询

### 2. 问题处理流程
1. **问题分类** - 根据问题类型选择对应的上下文模板
2. **信息收集** - 按照模板收集完整的上下文信息
3. **上下文验证** - 确认信息的准确性和完整性
4. **AI交互** - 使用结构化的方式提供上下文
5. **结果验证** - 验证AI输出是否符合上下文要求
6. **上下文更新** - 根据处理结果更新相关上下文

### 3. 性能优化建议
- 使用分层的上下文结构，避免信息冗余
- 建立上下文索引，提高查询效率
- 定期清理过期的上下文信息
- 使用缓存机制减少重复计算

## 常见问题

### 1. 上下文信息不足
**问题**: AI输出质量不高
**解决**: 使用脚本生成完整上下文，提供更多项目信息

### 2. 组件状态不明确
**问题**: 不了解组件当前状态
**解决**: 查看component-status-latest.md文件

### 3. 配置冲突
**问题**: 新配置与现有配置冲突
**解决**: 检查config-status-latest.md，了解现有配置

### 4. 编码问题
**问题**: 中文乱码或编码不兼容
**解决**: 确保使用UTF-8编码，参考ENCODING-STANDARDS.md

## 后续计划

1. **完善提示词模板库** - 根据实际开发需求逐步补充
2. **建立知识库** - 积累项目特有的问题解决方案
3. **优化工作流程** - 基于使用反馈持续改进
4. **集成开发工具** - 与IDE和CI/CD流程集成

## 📚 参考资源

- **项目结构规范**: `docs/PROJECT-STRUCTURE.md`
- **开发规范执行机制**: `docs/ai-development/development-standards.md`
- **编码标准**: `docs/ENCODING-STANDARDS.md`
- **系统环境**: `docs/SYSTEM-ENVIRONMENT.md`
- **IDE配置**: `IDEA-编码配置指南.md`

## 🔧 工具集成

### 自动化验证工具
- **项目结构验证**: `scripts/validate-project-structure.ps1`
- **上下文生成**: `scripts/generate-project-context.ps1`
- **AI开发启动**: `scripts/ai-dev-start.ps1`

### 开发规范检查
- **结构规范性**: 自动检查目录和文件组织
- **命名规范性**: 验证类名、包名等命名规范
- **配置完整性**: 检查配置文件是否正确

---

*本文档将随着项目发展持续更新和完善*