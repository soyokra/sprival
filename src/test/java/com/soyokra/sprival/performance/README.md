# 性能测试框架

## 📋 目录结构

```
performance/
├── benchmark/              # 微基准测试（JMH）
│   └── OrderServiceBenchmark.java
├── loadtest/               # 压力测试/负载测试
│   └── OrderInsertLoadTest.java
├── config/                 # 性能测试配置
│   └── PerformanceTestConfig.java
├── util/                   # 性能测试工具类
│   └── PerformanceTestUtils.java
└── README.md              # 本文档
```

## 🎯 测试类型说明

### 1. 微基准测试（Benchmark）
**目的**: 测试代码片段或方法的性能（纳秒/微秒级别）  
**工具**: JMH (Java Microbenchmark Harness)  
**位置**: `benchmark/`  
**使用场景**:
- 测试单个方法的性能
- 对比不同实现方式的性能差异
- 测试算法效率

### 2. 压力测试/负载测试（Load Test）
**目的**: 测试系统在高并发下的表现  
**工具**: 自定义多线程框架 + Apache HttpClient  
**位置**: `loadtest/`  
**使用场景**:
- 测试接口的 TPS（每秒事务数）
- 测试响应时间分布
- 发现系统瓶颈
- 验证系统稳定性

## 🚀 快速开始

### 方式1: 运行压力测试

#### 1.1 启动应用
```bash
# 使用性能测试配置启动应用
mvn spring-boot:run -Dspring-boot.run.profiles=performance
```

#### 1.2 运行压力测试
```bash
# 运行下单接口压力测试
mvn test -Dtest=OrderInsertLoadTest

# 运行特定测试方法
mvn test -Dtest=OrderInsertLoadTest#testOrderInsertWithFixedConcurrency
```

#### 1.3 查看测试报告
```bash
# 测试报告保存在
target/performance-reports/
```

### 方式2: 运行微基准测试

```bash
# 运行 JMH 基准测试
mvn test -Dtest=OrderServiceBenchmark
```

## 📊 压力测试说明

### 下单接口压力测试（OrderInsertLoadTest）

提供了三种压力测试模式：

#### 1. 固定并发测试 (testOrderInsertWithFixedConcurrency)
- **并发数**: 100（可配置）
- **持续时间**: 60秒（可配置）
- **目的**: 验证系统在稳定并发下的性能

```java
@Test
public void testOrderInsertWithFixedConcurrency() throws Exception
```

#### 2. 递增并发测试 (testOrderInsertWithIncrementalLoad)
- **并发级别**: 10 → 50 → 100 → 200 → 500
- **每级持续**: 30秒
- **目的**: 找出系统的性能拐点

```java
@Test
public void testOrderInsertWithIncrementalLoad() throws Exception
```

#### 3. 峰值压力测试 (testOrderInsertPeakLoad)
- **并发数**: 1000
- **持续时间**: 10秒
- **目的**: 测试系统在极限情况下的表现

```java
@Test
public void testOrderInsertPeakLoad() throws Exception
```

## ⚙️ 配置说明

### 压力测试配置

配置文件: `src/test/resources/performance/application-performance.properties`

```properties
# 基础配置
performance.test.base-url=http://localhost:8338
performance.test.concurrent-users=100      # 并发用户数
performance.test.duration-seconds=60       # 测试持续时间（秒）
performance.test.warmup-seconds=10         # 预热时间（秒）
performance.test.target-tps=1000          # 目标TPS
performance.test.verbose-logging=false     # 详细日志
performance.test.report-output-dir=target/performance-reports
```

### 自定义测试参数

在测试类中可以通过 `@Autowired` 注入配置：

```java
@Autowired
private PerformanceTestConfig config;

// 修改配置
config.setConcurrentUsers(200);
config.setDurationSeconds(120);
```

## 📝 编写新的压力测试

### 步骤1: 创建测试类

在 `loadtest/` 目录下创建新的测试类：

```java
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("performance")
public class YourApiLoadTest {
    
    @Autowired
    private PerformanceTestConfig config;
    
    private CloseableHttpClient httpClient;
    
    @BeforeEach
    public void setup() {
        // 初始化 HTTP 客户端
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(config.getConcurrentUsers() * 2);
        
        httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .build();
    }
    
    @Test
    public void testYourApi() throws Exception {
        // 实现压力测试逻辑
    }
}
```

### 步骤2: 实现压力测试逻辑

参考 `OrderInsertLoadTest` 的实现方式：

1. 使用 `ExecutorService` 创建线程池
2. 使用 `CountDownLatch` 控制并发启动
3. 使用 `CopyOnWriteArrayList` 收集响应时间
4. 使用 `AtomicLong` 统计失败次数
5. 使用 `PerformanceTestUtils` 计算和输出结果

### 步骤3: 发送HTTP请求

```java
private boolean sendRequest() {
    HttpPost httpPost = new HttpPost(apiUrl);
    httpPost.setHeader("Content-Type", "application/json");
    
    try {
        Map<String, Object> data = new HashMap<>();
        data.put("key", "value");
        
        String jsonBody = objectMapper.writeValueAsString(data);
        httpPost.setEntity(new StringEntity(jsonBody, "UTF-8"));
        
        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            int statusCode = response.getStatusLine().getStatusCode();
            return statusCode >= 200 && statusCode < 300;
        }
    } catch (Exception e) {
        log.error("请求失败", e);
        return false;
    }
}
```

## 📈 性能测试报告

### 报告内容

压力测试完成后，会生成以下信息：

1. **控制台输出**: 实时测试结果
2. **文件报告**: `target/performance-reports/{TestName}_{Timestamp}.txt`

### 报告示例

```
========================================
性能测试报告: OrderInsert_FixedConcurrency_100
测试时间: 2025-10-15 18:00:00
========================================

测试结果:
  总请求数: 50000
  成功请求: 49800
  失败请求: 200
  成功率: 99.60%

响应时间:
  最小: 5 ms
  最大: 120 ms
  平均: 15.5 ms

吞吐量:
  TPS: 833.33 请求/秒
  总耗时: 60000 ms (60.00 s)
```

## 🔍 性能分析建议

### 关键指标

1. **TPS（每秒事务数）**: 系统吞吐量
   - 优秀: > 1000 TPS
   - 良好: 500-1000 TPS
   - 需优化: < 500 TPS

2. **响应时间**:
   - 优秀: < 50ms
   - 良好: 50-200ms
   - 需优化: > 200ms

3. **成功率**:
   - 优秀: > 99.9%
   - 良好: 99%-99.9%
   - 需优化: < 99%

### 性能优化方向

如果性能不佳，可以从以下方向优化：

1. **数据库层面**:
   - 添加索引
   - 优化SQL查询
   - 使用连接池
   - 读写分离

2. **应用层面**:
   - 添加缓存（Redis）
   - 异步处理
   - 批量操作
   - 减少锁竞争

3. **架构层面**:
   - 负载均衡
   - 水平扩展
   - 消息队列
   - 微服务拆分

## 🛠️ 工具类说明

### PerformanceTestUtils

提供了性能测试的常用工具方法：

```java
// 计算测试结果
PerformanceResult result = PerformanceTestUtils.calculateResult(
    testName, responseTimes, totalDuration, failedCount
);

// 打印结果到控制台
PerformanceTestUtils.printResult(result);

// 保存报告到文件
PerformanceTestUtils.saveReport(result, outputDir);
```

## 📚 参考资料

- [JMH 官方文档](https://github.com/openjdk/jmh)
- [Apache HttpClient 文档](https://hc.apache.org/httpcomponents-client-ga/)
- [性能测试最佳实践](https://martinfowler.com/articles/practical-test-pyramid.html)

## ⚠️ 注意事项

1. **不要在生产环境运行压力测试**
2. 压力测试前确保有足够的系统资源
3. 测试前关闭不必要的日志输出
4. 建议使用独立的测试环境
5. 测试数据应该真实但要脱敏
6. 注意监控系统资源（CPU、内存、网络）

## 🔄 持续集成

可以将性能测试集成到 CI/CD 流程：

```yaml
# .gitlab-ci.yml 示例
performance-test:
  stage: test
  script:
    - mvn clean test -Dtest=*LoadTest
  artifacts:
    paths:
      - target/performance-reports/
    expire_in: 1 week
  only:
    - develop
    - master
```

---

**维护者**: Sprival Team  
**最后更新**: 2025-10-15

