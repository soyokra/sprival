# 监控组件

## 简介

监控组件为Sprival项目提供完整的应用监控解决方案，基于Spring Boot Actuator、Micrometer、Prometheus和Grafana构建现代化的监控体系，实现应用性能监控、健康检查、告警通知和可视化展示。

## 功能特性

- **应用监控**: 基于Spring Boot Actuator的应用健康检查和指标暴露
- **指标收集**: 使用Micrometer统一收集JVM、HTTP、数据库等各类指标
- **时序存储**: 基于Prometheus的指标存储和查询
- **可视化展示**: 通过Grafana实现监控数据的可视化展示
- **智能告警**: 基于AlertManager的告警路由和通知
- **日志监控**: 结构化日志收集和分析
- **容器化部署**: 支持Docker和Kubernetes部署

## 环境要求

- **Java版本**: 1.8+
- **Spring Boot版本**: 2.7.18
- **Prometheus版本**: 2.40+
- **Grafana版本**: 9.0+
- **AlertManager版本**: 0.25+

## 快速开始

### 安装步骤
1. 项目已配置所需依赖，无需额外添加
2. 启动Prometheus、Grafana、AlertManager服务
3. 配置application.properties中的监控相关配置
4. 启动Spring Boot应用
5. 访问监控面板查看指标

### 基础配置
```properties
# 启用所有监控端点
management.endpoints.web.exposure.include = *

# 启用Prometheus指标导出
management.metrics.export.prometheus.enabled = true

# 自定义Actuator端点路径
management.endpoints.web.base-path = /api/actuator

# 健康检查详细信息
management.endpoint.health.show-details = always
management.endpoint.health.show-components = always

# 应用信息配置
management.info.env.enabled = true
management.info.build.enabled = true
management.info.git.enabled = true
```

### 基础使用
```java
// 自定义业务指标
@Component
public class BusinessMetrics {
    
    private final MeterRegistry meterRegistry;
    private final Counter requestCounter;
    
    public BusinessMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.requestCounter = Counter.builder("business.requests.total")
            .description("Total number of business requests")
            .register(meterRegistry);
    }
    
    public void recordRequest() {
        requestCounter.increment();
    }
}
```

## 配置说明

### 配置参数
| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `management.endpoints.web.exposure.include` | String | health,info | 暴露的监控端点 |
| `management.metrics.export.prometheus.enabled` | Boolean | false | 是否启用Prometheus指标导出 |
| `management.endpoint.health.show-details` | String | never | 健康检查详情显示策略 |
| `management.metrics.tags.application` | String | - | 应用标签 |
| `management.metrics.tags.environment` | String | - | 环境标签 |

### 高级配置
```properties
# JVM指标配置
management.metrics.enable.jvm = true
management.metrics.enable.process = true
management.metrics.enable.system = true

# Web指标配置
management.metrics.web.server.request.autotime.enabled = true
management.metrics.web.client.request.autotime.enabled = true

# 数据源指标配置
management.metrics.enable.hikaricp = true
management.metrics.enable.jdbc = true

# 自定义指标标签
management.metrics.tags.application = sprival
management.metrics.tags.environment = ${spring.profiles.active:dev}
management.metrics.tags.version = @project.version@
```

## 使用示例

### 基本用法
```java
// 自定义健康检查
@Component
public class CustomHealthIndicator implements HealthIndicator {
    
    @Override
    public Health health() {
        // 检查外部服务连接
        if (checkExternalService()) {
            return Health.up()
                .withDetail("external-service", "Available")
                .build();
        } else {
            return Health.down()
                .withDetail("external-service", "Unavailable")
                .build();
        }
    }
    
    private boolean checkExternalService() {
        // 实际的健康检查逻辑
        return true;
    }
}
```

### 高级用法
```java
// 自定义指标收集
@Service
public class MetricsService {
    
    private final MeterRegistry meterRegistry;
    private final Timer businessTimer;
    private final Counter errorCounter;
    
    public MetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.businessTimer = Timer.builder("business.operation.duration")
            .description("Business operation duration")
            .register(meterRegistry);
        this.errorCounter = Counter.builder("business.errors.total")
            .description("Total business errors")
            .register(meterRegistry);
    }
    
    public void recordOperation(Runnable operation) {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            operation.run();
        } catch (Exception e) {
            errorCounter.increment();
            throw e;
        } finally {
            sample.stop(businessTimer);
        }
    }
}
```

## 监控

### 健康检查
```bash
# 查看应用健康状态
curl http://localhost:8338/api/actuator/health

# 查看所有监控端点
curl http://localhost:8338/api/actuator

# 查看Prometheus指标
curl http://localhost:8338/api/actuator/prometheus
```

### 监控指标
| 指标名称 | 类型 | 说明 |
|----------|------|------|
| `jvm_memory_used_bytes` | Gauge | JVM内存使用量 |
| `jvm_gc_pause_seconds` | Timer | GC暂停时间 |
| `http_server_requests_seconds` | Timer | HTTP请求处理时间 |
| `hikaricp_connections_active` | Gauge | 数据库连接池活跃连接数 |
| `system_cpu_usage` | Gauge | 系统CPU使用率 |
| `process_uptime_seconds` | Gauge | 应用运行时间 |

## 常见问题

**Q: 如何自定义监控指标？**
A: 通过注入MeterRegistry，使用Counter、Timer、Gauge等指标类型收集自定义业务指标。

**Q: 监控数据如何存储？**
A: 使用Prometheus作为时序数据库存储监控数据，支持高效查询和聚合。

**Q: 如何配置告警规则？**
A: 在Prometheus中配置告警规则文件，通过AlertManager处理告警通知。

**Q: 监控数据保留多长时间？**
A: 默认保留15天，可通过Prometheus配置调整保留策略。

**Q: 如何优化监控性能？**
A: 合理设置指标收集频率，避免收集过多不必要的指标，使用采样减少数据量。

## 参考文档

- [Spring Boot Actuator官方文档](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Micrometer官方文档](https://micrometer.io/docs)
- [Prometheus官方文档](https://prometheus.io/docs/)
- [Grafana官方文档](https://grafana.com/docs/)