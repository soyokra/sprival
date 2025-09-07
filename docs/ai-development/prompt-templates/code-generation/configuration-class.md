# 配置类生成模板

## 模板信息
- **版本**: 1.0
- **适用场景**: 生成Spring Boot配置类
- **更新时间**: 2024-01-01

## 基础模板

### 标准配置类生成
```markdown
请为Sprival项目生成一个{COMPONENT_NAME}的配置类，具体要求如下：

## 项目上下文
- 项目名称: Sprival
- Spring Boot版本: 2.7.18
- Java版本: 1.8
- 基础包名: com.soyokra.sprival

## 组件信息
- 组件名称: {COMPONENT_NAME}
- 组件版本: {COMPONENT_VERSION}
- 主要功能: {COMPONENT_FUNCTION}
- 依赖组件: {DEPENDENCIES}

## 配置要求
- 配置类名: {CONFIG_CLASS_NAME}
- 配置前缀: {CONFIG_PREFIX}
- 需要的Bean: {REQUIRED_BEANS}
- 条件装配: {CONDITIONAL_ON}

## 代码规范
- 使用@Configuration注解
- 添加@EnableConfigurationProperties如果需要
- 使用@ConditionalOnProperty进行条件装配
- 添加完整的Javadoc注释
- 使用Lombok注解简化代码
- 遵循Google Java代码规范

## 输出要求
1. 完整的Java配置类代码
2. 相关的Properties配置类（如果需要）
3. application.properties配置示例
4. 使用说明和注意事项

请确保生成的代码可以直接在项目中使用，无需额外修改。
```

## 专用模板

### MyBatis-Plus配置类
```markdown
请为Sprival项目生成MyBatis-Plus的配置类，具体要求如下：

## 项目上下文
- 项目: Sprival Spring Boot组件集成模板
- MyBatis-Plus版本: 3.5.12
- 数据源: Dynamic-Datasource + HikariCP
- 监控: P6Spy SQL监控

## 功能需求
- 多数据源支持（master主库，sprival业务库）
- 分页插件配置
- SQL监控集成
- 自动填充配置
- 逻辑删除支持

## 技术要求
- 配置类: SprivalMybatisPlusConfiguration
- 包路径: com.soyokra.sprival.config.mysql
- 支持@DS注解切换数据源
- 集成P6Spy进行SQL监控
- 配置分页插件和逻辑删除插件

## 代码规范
- 使用@Configuration + @MapperScan
- 添加完整的Bean配置方法
- 包含异常处理和日志记录
- 提供详细的Javadoc文档

请生成完整的配置类代码和相关配置文件示例。
```

### Redis配置类
```markdown
请为Sprival项目生成Redis的配置类，具体要求如下：

## 项目上下文
- 项目: Sprival
- Redis客户端: Redisson 3.19.3
- Spring组件: Spring Cache + Spring Data Redis
- 缓存策略: 多级缓存支持

## 功能需求
- Redisson客户端配置
- Spring Cache集成
- 序列化配置（JSON格式）
- 缓存过期策略
- 分布式锁支持

## 技术要求
- 配置类: SprivalRedisConfiguration  
- Redisson配置优先级最高
- 支持多种缓存注解
- 提供RedisTemplate和StringRedisTemplate
- 集成分布式锁工具类

## 特殊说明
根据requirement.md，Redisson自动装配类会优先注册RedisConnectionFactory，
这意味着Spring Cache和Spring Data Redis都会使用Redisson客户端。

请生成相应的配置类和使用示例。
```

### Jetty配置类
```markdown
请为Sprival项目生成Jetty Web服务器的配置类，具体要求如下：

## 项目上下文
- 项目: Sprival
- Web服务器: Jetty（替代默认Tomcat）
- 限流组件: Guava RateLimiter
- 监控: 集成Actuator

## 功能需求
- Jetty服务器自定义配置
- 接口限流集成
- 访问日志配置
- 性能优化配置
- 健康检查端点

## 技术要求
- 配置类: SprivalJettyConfiguration
- 自定义Jetty配置: SprivalJettyCustomizer
- 集成RateLimiter限流
- 支持访问日志记录
- 配置线程池和连接池

## 代码要求
- 实现JettyServletWebServerFactoryCustomizer
- 添加限流Filter或Interceptor
- 配置访问日志格式
- 提供性能调优参数

请生成完整的Jetty配置代码。
```

## 变量说明

### 通用变量
- `{COMPONENT_NAME}`: 组件名称，如"MyBatis-Plus"、"Redis"等
- `{COMPONENT_VERSION}`: 组件版本号
- `{COMPONENT_FUNCTION}`: 组件主要功能描述
- `{DEPENDENCIES}`: 依赖的其他组件列表
- `{CONFIG_CLASS_NAME}`: 配置类名称
- `{CONFIG_PREFIX}`: 配置属性前缀
- `{REQUIRED_BEANS}`: 需要创建的Bean列表
- `{CONDITIONAL_ON}`: 条件装配条件

### 特定组件变量
- `{DATASOURCE_NAMES}`: 数据源名称列表（用于数据库配置）
- `{CACHE_NAMES}`: 缓存名称列表（用于缓存配置）
- `{QUEUE_NAMES}`: 队列名称列表（用于消息队列配置）

## 使用示例

### 生成ClickHouse配置类
```markdown
替换变量值：
- COMPONENT_NAME: "ClickHouse"
- COMPONENT_VERSION: "0.3.2-patch11"
- COMPONENT_FUNCTION: "数据分析和日志存储"
- CONFIG_CLASS_NAME: "SprivalClickHouseConfiguration"
- CONFIG_PREFIX: "sprival.clickhouse"
- DEPENDENCIES: "MyBatis-Plus, Dynamic-Datasource"

使用基础模板生成提示词...
```

## 输出验证

生成的配置类应该满足：
1. **编译通过**: 无语法错误，依赖正确
2. **功能完整**: 包含所需的所有Bean配置
3. **规范合规**: 遵循项目编码规范
4. **文档齐全**: 包含完整的注释和使用说明
5. **可扩展性**: 易于后续功能扩展

## 常见问题

### Q: 如何处理配置类之间的依赖关系？
A: 使用@DependsOn注解或@Order注解控制加载顺序，在模板中明确指定依赖关系。

### Q: 配置类过于复杂时如何处理？
A: 可以拆分为多个配置类，使用@Import注解组合，或创建配置类的配置类。

### Q: 如何确保配置的环境兼容性？
A: 使用@Profile注解区分不同环境，在模板中包含环境相关的配置示例。

---

*模板版本: 1.0 | 最后更新: 2024-01-01*
