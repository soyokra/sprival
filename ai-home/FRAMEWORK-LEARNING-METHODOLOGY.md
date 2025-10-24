# 框架学习方法论

## 学习新框架的步骤

### 1. 框架概览
- [ ] 阅读官方文档的架构部分
- [ ] 理解框架的核心概念和设计理念
- [ ] 了解框架的主要组件和它们的关系

### 2. 源码阅读策略
- [ ] 从入口类开始阅读
- [ ] 理解核心抽象类和接口
- [ ] 研究关键实现类的设计模式
- [ ] 关注异常处理和生命周期管理

### 3. 实践验证
- [ ] 创建简单的示例项目
- [ ] 测试框架的基本功能
- [ ] 验证对框架机制的理解
- [ ] 尝试扩展和自定义

## 具体到 Logback 框架

### 核心组件理解
1. **AppenderBase**: 提供基础功能
   - 状态管理 (`started` 字段)
   - 异常处理 (`doAppend()` 方法)
   - 过滤器支持
   - 重入保护 (`guard` 机制)

2. **Appender 接口**: 定义契约
   - `start()` / `stop()` 生命周期
   - `append()` 核心功能
   - `isStarted()` 状态查询

3. **ContextAwareBase**: 上下文支持
   - 日志记录 (`addError()`, `addInfo()`)
   - 状态管理
   - 配置支持

### 实现自定义 Appender 的检查点
- [ ] 是否理解了 `AppenderBase.doAppend()` 的机制？
- [ ] 是否避免了重复实现状态管理？
- [ ] 是否正确处理了异常？
- [ ] 是否遵循了生命周期约定？

## 避免常见错误

### 1. 重复实现框架功能
```java
// ❌ 错误：重复实现状态管理
private final AtomicBoolean started = new AtomicBoolean(false);

// ✅ 正确：使用框架提供的状态管理
if (isStarted()) { ... }
```

### 2. 忽略框架的异常处理
```java
// ❌ 错误：在 append() 中抛出异常
protected void append(ILoggingEvent event) {
    throw new RuntimeException("Error"); // 会被 AppenderBase 捕获
}

// ✅ 正确：记录错误，让框架处理
protected void append(ILoggingEvent event) {
    try {
        // 业务逻辑
    } catch (Exception e) {
        addError("Error message", e); // 记录错误，不抛出
    }
}
```

### 3. 不遵循生命周期约定
```java
// ❌ 错误：在 start() 中设置自定义状态
public void start() {
    this.customStarted = true; // 与框架状态不同步
}

// ✅ 正确：调用父类方法
public void start() {
    super.start(); // 设置框架状态
    // 自定义初始化逻辑
}
```
