## 配置说明

配置源码在ServerProperties.Jetty

| 配置项                            | 默认值 | 说明             |
| --------------------------------- | ------ | ---------------- |
| server.jetty.threads.max          | 200    | 最大线程数       |
| server.jetty.threads.min          | 8      | 最小线程数       |
| server.jetty.threads.idle-timeout | 60000  | 线程空闲超时时间 |

## 监控预警

#### 监控指标

| 指标名称                 | 类型  | 说明               |
| ------------------------ | ----- |------------------|
| jetty_threads_busy       | gauge | 正在处理请求的活跃线程数     |
| jetty_threads_config_max | gauge | 线程池配置的最大线程数      |
| jetty_threads_config_min | gauge | 线程池配置的最小线程数      |
| jetty_threads_current    | gauge | 当前实际拥有的线程数       |
| jetty_threads_idle       | gauge | 空闲等待请求的线程数       |
| jetty_threads_jobs       | gauge | 线程池任务队列中等待处理的请求数 |

#### Grafana配置

```plaintext
sum(jetty_threads_busy) by (job)
sum(jetty_threads_config_max) by (job)
sum(jetty_threads_config_min) by (job)
sum(jetty_threads_current) by (job)
sum(jetty_threads_idle) by (job)
sum(jetty_threads_jobs) by (job)
```


## 源码分析

#### 调用链路

```plaintext
AbstractApplicationContext.onRefresh
=> ServletWebServerApplicationContext.onRefresh
=> ServletWebServerApplicationContext.createWebServer
=> ServletWebServerFactory.getWebServer
=> JettyServletWebServerFactory.getWebServer
```

#### 自动装配相关类

```plaintext
ServletWebServerFactoryAutoConfiguration
ServletWebServerFactoryConfiguration
```
