# Sprival AI开发规范执行机制

## 概述

本文档定义了Sprival项目中AI开发的标准规范和执行机制，确保AI生成的代码符合项目结构和编码规范。

## 🎯 开发规范原则

### 1. 目录结构规范
- **严格遵循**: 必须按照 `docs/PROJECT-STRUCTURE.md` 中定义的目录结构
- **包命名规范**: 使用 `com.soyokra.sprival` 作为根包
- **文件组织**: 按功能模块组织文件，避免文件散乱

### 2. 命名规范
- **类命名**: 使用 `Sprival` 前缀 + 组件名 + 类型
- **包命名**: 使用小写字母，按功能分层
- **文件命名**: 使用驼峰命名法，与类名保持一致

### 3. 代码规范
- **编码格式**: 统一使用UTF-8编码
- **代码风格**: 遵循Java标准编码规范
- **注释规范**: 使用JavaDoc标准注释格式

## 📋 AI开发检查清单

### 开发前检查
- [ ] 确认目标目录结构符合规范
- [ ] 检查包名是否正确
- [ ] 验证类名是否符合命名规范
- [ ] 确认依赖关系正确

### 开发中检查
- [ ] 代码结构清晰，职责单一
- [ ] 配置类使用正确的注解
- [ ] 健康检查类实现HealthIndicator接口
- [ ] 异常处理机制完善

### 开发后检查
- [ ] 代码能够正常编译
- [ ] 配置文件正确且完整
- [ ] 文档更新及时
- [ ] 测试用例覆盖充分

## 🔧 自动化验证工具

### 1. 项目结构验证
```powershell
# 验证项目结构
.\scripts\validate-project-structure.ps1

# 验证并生成修复脚本
.\scripts\validate-project-structure.ps1 -Fix
```

### 2. 代码质量检查
```powershell
# 编译检查
mvn clean compile

# 代码规范检查
mvn checkstyle:check

# 测试覆盖率检查
mvn jacoco:report
```

### 3. 文档一致性检查
```powershell
# 检查文档链接
.\scripts\check-documentation.ps1

# 更新文档索引
.\scripts\update-doc-index.ps1
```

## 📝 标准模板

### 1. 配置类模板
```java
package com.soyokra.sprival.config.[组件名];

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConditionalOnClass;
import lombok.Data;

/**
 * [组件名]配置类
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Configuration
@EnableConfigurationProperties([组件名]Properties.class)
public class Sprival[组件名]Configuration {

    /**
     * 配置[组件名]Bean
     */
    @Bean
    @ConditionalOnClass([组件名]Class.class)
    public [组件名]Bean [组件名]Bean([组件名]Properties properties) {
        return new [组件名]Bean(properties);
    }
}
```

### 2. 属性类模板
```java
package com.soyokra.sprival.config.[组件名];

import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Data;

/**
 * [组件名]配置属性
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Data
@ConfigurationProperties(prefix = "sprival.[组件名]")
public class Sprival[组件名]Properties {
    
    private String property1;
    private Integer property2;
    private Boolean property3;
}
```

### 3. 健康检查模板
```java
package com.soyokra.sprival.config.[组件名];

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * [组件名]健康检查指示器
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Slf4j
@Component
public class Sprival[组件名]HealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        try {
            // 健康检查逻辑
            return Health.up()
                .withDetail("status", "Available")
                .withDetail("timestamp", System.currentTimeMillis())
                .build();
        } catch (Exception e) {
            log.error("[组件名]健康检查失败", e);
            return Health.down()
                .withDetail("error", e.getMessage())
                .withDetail("timestamp", System.currentTimeMillis())
                .build();
        }
    }
}
```

### 4. 服务类模板
```java
package com.soyokra.sprival.service;

import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

/**
 * [功能]服务类
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Slf4j
@Service
public class [功能]Service {

    /**
     * [功能描述]
     */
    public [返回类型] [方法名]([参数列表]) {
        try {
            // 业务逻辑
            return result;
        } catch (Exception e) {
            log.error("[功能]执行失败", e);
            throw new [异常类型]("[错误信息]", e);
        }
    }
}
```

### 5. 控制器模板
```java
package com.soyokra.sprival.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

/**
 * [功能]控制器
 * 
 * @author Sprival Team
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/[功能]")
public class [功能]Controller {

    /**
     * [功能描述]
     */
    @GetMapping("/{id}")
    public ResponseEntity<[返回类型]> [方法名](@PathVariable [参数类型] id) {
        try {
            // 业务逻辑
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("[功能]查询失败", e);
            return ResponseEntity.status(500).build();
        }
    }
}
```

## 🚀 AI开发工作流程

### 1. 需求分析阶段
```markdown
## 任务描述
[详细描述要完成的任务]

## 涉及组件
[列出相关的组件]

## 目录结构
[确认文件放置的目录结构]

## 命名规范
[确认类名、包名等命名规范]
```

### 2. 代码生成阶段
```markdown
## 生成要求
- 严格按照项目目录结构规范
- 使用标准命名规范
- 遵循代码模板格式
- 包含完整的注释和文档
```

### 3. 验证阶段
```markdown
## 验证步骤
1. 运行项目结构验证脚本
2. 检查代码编译是否通过
3. 验证配置文件是否正确
4. 确认文档更新完整
```

## 📊 质量保证机制

### 1. 自动化检查
- **结构验证**: 每次开发后运行结构验证脚本
- **编译检查**: 确保代码能够正常编译
- **测试验证**: 运行相关测试用例
- **文档检查**: 验证文档一致性

### 2. 人工审查
- **代码审查**: 检查代码质量和规范性
- **架构审查**: 验证架构设计合理性
- **文档审查**: 确认文档完整性和准确性

### 3. 持续改进
- **规范更新**: 根据项目发展更新规范
- **模板优化**: 持续改进代码模板
- **工具增强**: 完善自动化验证工具

## 🔍 常见问题解决

### 1. 目录结构问题
**问题**: 文件放置位置不正确
**解决**: 参考 `docs/PROJECT-STRUCTURE.md` 调整文件位置

### 2. 命名规范问题
**问题**: 类名或包名不符合规范
**解决**: 使用标准命名规范，添加Sprival前缀

### 3. 配置问题
**问题**: 配置文件不正确或缺失
**解决**: 检查配置文件格式和内容，确保完整性

### 4. 依赖问题
**问题**: 依赖关系不正确
**解决**: 检查pom.xml中的依赖配置

## 📚 参考资源

- **项目结构规范**: `docs/PROJECT-STRUCTURE.md`
- **编码标准**: `docs/development/encoding-standards.md`
- **系统环境**: `docs/development/system-environment.md`
- **IDE配置**: `docs/development/ide-setup.md`

## 🎯 最佳实践

### 1. 开发前准备
- 仔细阅读项目结构规范
- 了解相关组件的现有实现
- 确认开发环境和工具配置

### 2. 开发过程中
- 严格按照规范进行开发
- 及时运行验证脚本
- 保持代码和文档同步更新

### 3. 开发完成后
- 运行完整的验证流程
- 更新相关文档
- 提交代码前进行最终检查

---

*此规范将根据项目发展和使用反馈持续优化*
