# Sprival Support 模块

## 概述

`support` 目录是 Sprival 项目的基础功能模块，包含项目中通用的基础功能组件。这些组件为整个项目提供核心的基础服务支持。

## 目录结构

```
support/
├── health/                    # 健康检查模块
│   ├── example/              # 健康检查示例
│   ├── SprivalBaseHealthIndicator.java
│   ├── SprivalHealthConfiguration.java
│   ├── SprivalHealthDependencyMode.java
│   ├── SprivalHealthManager.java
│   └── SprivalHealthProperties.java
└── README.md                 # 本文件
```

## 模块说明

### 1. Health 健康检查模块

健康检查模块提供了完整的应用健康状态监控功能，包括：

#### 核心组件
- **SprivalBaseHealthIndicator** - 基础健康检查抽象类
- **SprivalHealthManager** - 健康检查管理器
- **SprivalHealthProperties** - 健康检查配置属性
- **SprivalHealthDependencyMode** - 依赖模式枚举
- **SprivalHealthConfiguration** - 健康检查配置类

#### 主要特性
- **强依赖/弱依赖模式** - 支持配置中间件的依赖模式
- **默认不运行** - 所有健康检查默认禁用，需要明确配置
- **统一基础类** - 提供统一的基础健康检查框架
- **灵活配置** - 支持组件级别的启用/禁用控制

#### 使用示例
```java
@Component
@ConditionalOnProperty(name = "your.component.enabled", havingValue = "true")
public class YourComponentHealthIndicator extends SprivalBaseHealthIndicator {
    
    @Override
    protected String getComponentName() {
        return "your-component";
    }
    
    @Override
    protected Health doHealthCheck() {
        // 实现具体的健康检查逻辑
        return createUpHealth(getHealthCheckDetails());
    }
}
```

## 设计原则

### 1. 模块化设计
- 每个功能模块独立封装
- 清晰的模块边界和职责
- 便于维护和扩展

### 2. 可配置性
- 支持外部配置
- 提供合理的默认值
- 支持运行时动态配置

### 3. 可扩展性
- 提供基础抽象类
- 支持自定义实现
- 遵循开闭原则

### 4. 性能优化
- 避免不必要的资源消耗
- 支持按需启用
- 提供性能监控

## 配置说明

### 健康检查配置
```properties
# 全局健康检查开关
sprival.health.enabled = true

# 各组件健康检查启用配置（默认不启用）
sprival.health.component-enabled.redis = true
sprival.health.component-enabled.mongodb = true
sprival.health.component-enabled.kafka = false

# 中间件依赖模式配置
sprival.health.dependency-modes.redis = strong
sprival.health.dependency-modes.kafka = weak
```

## 扩展指南

### 添加新的基础功能模块

1. **创建模块目录**
   ```bash
   mkdir -p src/main/java/com/soyokra/sprival/support/your-module
   ```

2. **定义模块接口**
   ```java
   public interface YourModuleService {
       // 定义模块接口
   }
   ```

3. **实现基础抽象类**
   ```java
   public abstract class YourBaseService implements YourModuleService {
       // 实现通用逻辑
   }
   ```

4. **创建配置类**
   ```java
   @Configuration
   @EnableConfigurationProperties(YourModuleProperties.class)
   public class YourModuleConfiguration {
       // 配置Bean
   }
   ```

5. **添加配置属性**
   ```java
   @Data
   @Component
   @ConfigurationProperties(prefix = "sprival.your-module")
   public class YourModuleProperties {
       // 配置属性
   }
   ```

### 添加新的健康检查指示器

1. **继承基础类**
   ```java
   @Component
   public class YourHealthIndicator extends SprivalBaseHealthIndicator {
       // 实现抽象方法
   }
   ```

2. **配置启用条件**
   ```java
   @ConditionalOnProperty(name = "sprival.health.component-enabled.your-component", havingValue = "true")
   ```

3. **添加配置**
   ```properties
   sprival.health.component-enabled.your-component = true
   sprival.health.dependency-modes.your-component = strong
   ```

## 最佳实践

### 1. 命名规范
- 类名以 `Sprival` 开头
- 包名使用小写字母和下划线
- 方法名使用驼峰命名

### 2. 文档规范
- 每个类都要有完整的JavaDoc
- 提供使用示例
- 说明配置参数

### 3. 测试规范
- 提供单元测试
- 提供集成测试
- 测试覆盖率要求

### 4. 配置规范
- 提供合理的默认值
- 支持外部配置
- 配置项要有说明

## 版本管理

### 当前版本
- **Health模块**: v1.0
- **Support模块**: v1.0

### 版本兼容性
- 向后兼容性保证
- 废弃功能提前通知
- 平滑升级路径

## 贡献指南

### 1. 代码提交
- 遵循项目代码规范
- 提供完整的测试用例
- 更新相关文档

### 2. 功能扩展
- 先讨论设计方案
- 提供详细的设计文档
- 考虑向后兼容性

### 3. 问题反馈
- 使用Issue跟踪问题
- 提供详细的复现步骤
- 提供环境信息

## 联系方式

- **项目维护者**: Sprival Team
- **邮箱**: sprival@soyokra.com
- **文档**: [项目文档](docs/)

---

*最后更新: 2024年9月10日*
