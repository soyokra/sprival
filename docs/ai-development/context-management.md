# Sprival 上下文管理策略

## 概述

本文档定义了Sprival项目中AI辅助开发的上下文管理策略，确保AI助手能够准确理解项目状态、组件关系和开发需求。

## 上下文层级结构

### 1. 全局项目上下文 (Global Context)

#### 项目元信息
```yaml
project:
  name: "Sprival"
  description: "Spring Boot组件集成模板"
  version: "0.0.1"
  java_version: "1.8"
  spring_boot_version: "2.7.18"
  spring_cloud_version: "2021.0.8"
  
architecture:
  pattern: "组件集成模板"
  web_server: "Jetty"
  build_tool: "Maven"
  package_structure: "com.soyokra.sprival"
```

#### 技术栈清单
```yaml
core_dependencies:
  - spring-boot-starter-web
  - spring-boot-starter-actuator
  - spring-boot-starter-jetty
  
data_layer:
  - mybatis-plus-boot-starter: "3.5.12"
  - dynamic-datasource-spring-boot-starter: "4.3.1"
  - mysql-connector-java
  - clickhouse-jdbc: "0.3.2-patch11"
  
cache_layer:
  - spring-boot-starter-cache
  - spring-boot-starter-data-redis  
  - redisson-spring-boot-starter: "3.19.3"
  
messaging:
  - spring-boot-starter-amqp
  - spring-kafka
  
monitoring:
  - micrometer-registry-prometheus
  - p6spy: "3.9.1"
```

### 2. 组件级上下文 (Component Context)

#### 组件状态矩阵
```yaml
components:
  http-server:
    status: "已完成"
    technologies: ["Jetty", "Guava RateLimiter"]
    config_files: ["jetty配置"]
    documentation: "docs/spring-http-server/"
    
  http-client:
    status: "研究中"
    planned_technologies: ["Retrofit", "Feign", "Hystrix", "Resilience4j"]
    documentation: "docs/spring-http-client/"
    
  spring-mysql:
    status: "已完成"
    technologies: ["MyBatis-Plus", "Dynamic-Datasource", "HikariCP", "P6Spy"]
    config_files: ["application.properties"]
    documentation: "docs/spring-mysql/"
    
  spring-redis:
    status: "已完成"
    technologies: ["Spring Cache", "Spring Data Redis", "Redisson"]
    config_files: ["redis配置"]
    documentation: "docs/spring-redis/"
    notes: "Redisson优先级最高，其他组件使用Redisson客户端"
    
  spring-clickhouse:
    status: "已完成"
    technologies: ["ClickHouse JDBC", "MyBatis-Plus集成"]
    config_files: ["ClickHouse数据源配置"]
    documentation: "docs/spring-clickhouse/"
    
  spring-data:
    status: "已完成"
    technologies: ["Kafka", "MongoDB", "RabbitMQ", "Elasticsearch"]
    config_files: ["各组件配置"]
    documentation: "docs/spring-*/"
    notes: "使用Spring官方组件"
```

#### 组件依赖关系图
```yaml
dependencies:
  spring-mysql:
    depends_on: []
    used_by: ["spring-clickhouse", "业务层"]
    
  spring-redis:
    depends_on: []
    used_by: ["spring-cache", "session管理"]
    priority: "Redisson > Spring Data Redis > Jedis/Lettuce"
    
  spring-clickhouse:
    depends_on: ["spring-mysql"]
    used_by: ["数据分析", "日志存储"]
    
  http-server:
    depends_on: ["spring-web"]
    integrates_with: ["监控", "限流"]
    
  messaging:
    kafka:
      depends_on: []
      used_by: ["事件驱动架构"]
    rabbitmq:
      depends_on: []
      used_by: ["任务队列"]
```

### 3. 开发任务上下文 (Task Context)

#### 任务分类模板
```yaml
task_types:
  feature_development:
    context_required:
      - 相关组件状态
      - 依赖组件配置
      - 业务需求描述
      - 技术约束条件
      
  bug_fixing:
    context_required:
      - 错误现象描述
      - 相关日志信息
      - 环境配置信息
      - 重现步骤
      
  configuration:
    context_required:
      - 目标组件信息
      - 环境要求
      - 性能要求
      - 安全要求
      
  documentation:
    context_required:
      - 目标受众
      - 文档类型
      - 现有文档状态
      - 更新范围
```

## 上下文提供规范

### 1. 标准上下文模板

#### 新功能开发
```markdown
## 任务上下文
**任务类型**: 新功能开发
**涉及组件**: [组件名称]
**优先级**: [高/中/低]

## 技术上下文
**当前Spring Boot版本**: 2.7.18
**Java版本**: 1.8
**相关依赖**: [Maven依赖列表]

## 业务上下文
**功能描述**: [详细描述]
**用户场景**: [使用场景]
**性能要求**: [性能指标]
**安全要求**: [安全规范]

## 开发约束
**代码规范**: [编码标准]
**测试要求**: [测试覆盖率]
**文档要求**: [文档更新要求]

## 相关文件
**配置文件**: [配置文件路径]
**源码文件**: [Java类路径]
**测试文件**: [测试类路径]
**文档文件**: [文档路径]
```

#### 问题诊断
```markdown
## 问题上下文
**问题类型**: [错误/性能/配置]
**发生时间**: [时间]
**影响范围**: [影响的功能模块]

## 技术环境
**运行环境**: [开发/测试/生产]
**JVM参数**: [JVM配置]
**数据库版本**: [数据库信息]
**中间件版本**: [Redis/Kafka/等版本]

## 错误信息
**错误日志**: [完整错误堆栈]
**相关配置**: [相关配置内容]
**重现步骤**: [详细步骤]

## 排查历史
**已尝试方案**: [已执行的排查步骤]
**排查结果**: [每个步骤的结果]
```

### 2. 动态上下文更新

#### 上下文版本控制
```yaml
context_versioning:
  format: "YYYY-MM-DD-HH-mm"
  update_triggers:
    - 组件版本升级
    - 配置文件修改
    - 架构调整
    - 新功能添加
    
  retention_policy:
    keep_latest: 10
    archive_older: true
```

#### 上下文同步机制
```yaml
sync_strategy:
  auto_update:
    - pom.xml变更时更新依赖信息
    - 配置文件变更时更新配置上下文
    - 代码结构变更时更新架构上下文
    
  manual_update:
    - 业务需求变更
    - 架构决策调整
    - 开发规范更新
```

## 上下文使用指南

### 1. AI助手使用规范

#### 上下文查询优先级
1. **项目全局上下文** - 了解整体架构和技术栈
2. **组件特定上下文** - 理解具体组件的状态和配置
3. **任务相关上下文** - 获取当前任务的具体要求
4. **历史上下文** - 参考类似任务的处理经验

#### 上下文验证检查点
```yaml
validation_checklist:
  project_level:
    - 技术栈版本是否匹配
    - 架构模式是否一致
    - 编码规范是否符合
    
  component_level:
    - 组件状态是否最新
    - 依赖关系是否正确
    - 配置参数是否有效
    
  task_level:
    - 需求描述是否完整
    - 约束条件是否明确
    - 预期结果是否可测量
```

### 2. 开发者使用规范

#### 上下文准备清单
```markdown
## 开发前准备
- [ ] 确认项目当前版本和状态
- [ ] 检查相关组件的最新配置
- [ ] 了解任务的业务背景和技术要求
- [ ] 准备必要的测试数据和环境

## 与AI交互前准备
- [ ] 整理完整的问题描述
- [ ] 收集相关的错误日志或配置信息
- [ ] 明确期望的输出格式和质量要求
- [ ] 准备验证结果的标准和方法
```

## 上下文管理工具

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
./scripts/generate-context.sh

# 输出格式
docs/ai-development/context/
├── project-context-$(date).md
├── component-status.yaml
└── dependency-graph.json
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

---

*本文档将根据项目发展和使用反馈持续优化*
