## AI编程指导

### 项目特点
1. **组件化架构**: 每个组件都有独立的配置类和健康检查
2. **统一命名规范**: 所有配置类都以"Sprival"开头
3. **健康检查集成**: 所有组件都集成了Spring Boot Actuator健康检查
4. **监控友好**: 集成了Micrometer和Prometheus监控
5. **UTF-8编码**: 支持跨平台编码兼容

### 开发规范
1. **包结构**: 按功能模块分包，配置类放在config包下
2. **命名规范**: 类名使用Sprival前缀，方法名使用驼峰命名
3. **配置管理**: 使用@ConfigurationProperties进行配置绑定
4. **健康检查**: 每个组件都要实现HealthIndicator
5. **异常处理**: 统一的异常处理和日志记录

### 常用模式
1. **配置类模式**: @Configuration + @Bean
2. **属性绑定模式**: @ConfigurationProperties
3. **健康检查模式**: 继承或实现HealthIndicator
4. **自动配置模式**: @ConditionalOnClass + @ConditionalOnBean

### 注意事项
1. **版本兼容**: 确保所有依赖版本与Spring Boot 2.7.18兼容
2. **编码问题**: 所有文件使用UTF-8编码
3. **配置冲突**: 注意不同组件间的配置冲突
4. **性能考虑**: 合理配置连接池和超时参数
5. **安全考虑**: 敏感配置使用环境变量或加密

### 快速开始模板
`java
// 1. 配置类模板
@Configuration
@EnableConfigurationProperties(YourProperties.class)
public class YourConfiguration {
    
    @Bean
    @ConditionalOnClass(YourClass.class)
    public YourBean yourBean(YourProperties properties) {
        return new YourBean(properties);
    }
}

// 2. 属性类模板
@ConfigurationProperties(prefix = "your.prefix")
@Data
public class YourProperties {
    private String property1;
    private Integer property2;
}

// 3. 健康检查模板
@Component
public class YourHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        // 健康检查逻辑
        return Health.up().build();
    }
}
`

