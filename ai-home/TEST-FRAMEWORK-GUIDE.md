# 测试框架开发指南

> AI 开发测试代码时必须遵循的规范和指南

## 📋 目录

- [测试框架概述](#测试框架概述)
- [测试目录结构](#测试目录结构)
- [测试基类使用](#测试基类使用)
- [单元测试编写规范](#单元测试编写规范)
- [集成测试编写规范](#集成测试编写规范)
- [性能测试编写规范](#性能测试编写规范)
- [压力测试编写规范](#压力测试编写规范)
- [工具类和Fixture使用](#工具类和fixture使用)
- [测试配置说明](#测试配置说明)
- [运行测试](#运行测试)

---

## 测试框架概述

项目已建立完整的测试框架，支持四种类型的测试：

1. **单元测试** (`unit/`) - 快速、隔离，使用Mock对象
2. **集成测试** (`integration/`) - 测试组件协作，使用真实环境
3. **性能测试** (`performance/`) - JMH基准测试
4. **压力测试** (`loadtest/`) - HTTP接口压力测试，使用Apache HttpClient

### 技术栈

- **JUnit 5**: 测试框架
- **Mockito**: Mock框架
- **AssertJ**: 断言库
- **Testcontainers**: 容器化测试（可选）
- **Rest Assured**: API测试
- **JMH**: 性能基准测试
- **Apache HttpClient**: HTTP压力测试
- **JaCoCo**: 代码覆盖率

---

## 测试目录结构

```
src/test/
├── java/com/soyokra/sprival/
│   ├── base/                          # 测试基类 ⭐
│   │   ├── BaseUnitTest.java         # 单元测试基类
│   │   ├── BaseIntegrationTest.java  # 集成测试基类
│   │   ├── BasePerformanceTest.java  # 性能测试基类
│   │   └── BaseLoadTest.java         # 压力测试基类
│   ├── config/                        # 测试配置
│   │   └── TestContainersConfig.java  # Testcontainers配置
│   ├── fixture/                       # 测试数据构造器 ⭐
│   │   ├── OrderTblFixture.java      # Order测试数据
│   │   └── TestDataBuilder.java     # 通用数据构造器
│   ├── util/                          # 测试工具类 ⭐
│   │   ├── TestAssertUtils.java      # 断言工具
│   │   ├── HttpLoadTestExecutor.java # HTTP压力测试执行器
│   │   ├── LoadTestConfig.java       # 压力测试配置
│   │   ├── LoadTestResult.java       # 压力测试结果
│   │   └── LoadTestStatistics.java   # 压力测试统计
│   ├── unit/                          # 单元测试 ⭐
│   │   ├── controller/
│   │   ├── service/
│   │   └── repository/
│   ├── integration/                  # 集成测试 ⭐
│   │   ├── api/
│   │   ├── database/
│   │   └── cache/
│   ├── performance/                   # 性能测试 ⭐
│   └── loadtest/                      # 压力测试 ⭐
│       └── api/
└── resources/
    └── application.properties         # 测试配置文件
```

### 命名规范

- **单元测试**: `XxxTest.java`
- **集成测试**: `XxxIntegrationTest.java`
- **性能测试**: `XxxBenchmark.java`
- **压力测试**: `XxxLoadTest.java`

---

## 测试基类使用

### BaseUnitTest - 单元测试基类

**用途**: 所有单元测试必须继承此类

**功能**:
- 自动初始化Mockito注解（`@Mock`, `@InjectMocks`）
- 统一日志配置

**示例**:
```java
package com.soyokra.sprival.unit.service;

import com.soyokra.sprival.base.BaseUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

@DisplayName("OrderService单元测试")
class OrderServiceTest extends BaseUnitTest {

    @Mock
    private OrderTblProvider orderTblProvider;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("测试根据订单ID获取订单")
    void testGetOrder() {
        // 测试代码
    }
}
```

### BaseIntegrationTest - 集成测试基类

**用途**: 所有集成测试必须继承此类

**功能**:
- Spring Boot测试上下文加载
- 事务自动回滚（默认）
- 测试Profile配置

**关键注解**:
- `@SpringBootTest`: 加载完整Spring上下文
- `@ActiveProfiles("test")`: 使用test profile
- `@Transactional`: 默认开启，测试后回滚

**示例**:
```java
package com.soyokra.sprival.integration.database;

import com.soyokra.sprival.base.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

@DisplayName("Order Repository集成测试")
class OrderRepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private OrderTblProvider orderTblProvider;

    @Test
    @DisplayName("测试保存订单")
    @Rollback  // 显式声明回滚（默认已开启）
    void testSave() {
        // 测试代码
    }

    @Test
    @DisplayName("测试提交事务")
    @Rollback(false)  // 不回滚，提交事务
    void testCommitTransaction() {
        // 测试代码
    }
}
```

### BasePerformanceTest - 性能测试基类

**用途**: 性能基准测试基类

**功能**:
- JMH基准测试配置
- 默认测试模式：吞吐量（Throughput）
- 预热和测试参数配置

**示例**:
```java
package com.soyokra.sprival.performance;

import com.soyokra.sprival.base.BasePerformanceTest;
import org.openjdk.jmh.annotations.*;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
public class OrderServiceBenchmark extends BasePerformanceTest {

    @Benchmark
    public void benchmarkGetOrder() {
        // 性能测试代码
    }
}
```

---

## 单元测试编写规范

### 基本原则

1. **必须继承BaseUnitTest**
2. **使用Mock隔离外部依赖**
3. **保持测试快速执行**（< 1秒）
4. **每个测试方法只测试一个功能点**
5. **使用Given-When-Then结构**

### 测试结构模板

```java
@DisplayName("XxxService单元测试")
class XxxServiceTest extends BaseUnitTest {

    @Mock
    private DependencyProvider dependencyProvider;

    @InjectMocks
    private XxxService xxxService;

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
        // 准备测试数据
    }

    @Test
    @DisplayName("测试功能描述")
    void testFunction() {
        // Given: 准备测试数据和Mock行为
        when(dependencyProvider.method()).thenReturn(result);

        // When: 执行被测试方法
        Result actual = xxxService.method(input);

        // Then: 验证结果
        assertThat(actual).isNotNull();
        assertThat(actual.getValue()).isEqualTo(expected);
        verify(dependencyProvider).method();
    }

    @Test
    @DisplayName("测试异常场景")
    void testFunction_Exception() {
        // Given: Mock抛出异常
        when(dependencyProvider.method()).thenThrow(new RuntimeException());

        // When & Then: 验证异常
        assertThatThrownBy(() -> xxxService.method(input))
                .isInstanceOf(RuntimeException.class);
    }
}
```

### Mock使用规范

**使用@Mock注解**:
```java
@Mock
private OrderTblProvider orderProvider;
```

**Mock行为设置**:
```java
// 返回值
when(orderProvider.getById(orderId)).thenReturn(order);

// 抛出异常
when(orderProvider.getById(orderId)).thenThrow(new RuntimeException());

// 验证调用
verify(orderProvider).getById(orderId);
verify(orderProvider, times(2)).getById(orderId);
```

**使用MockUtils工具类**:
```java
// 创建Mock对象
OrderTblProvider provider = MockUtils.mock(OrderTblProvider.class);

// 创建带返回值的Mock
OrderTblProvider provider = MockUtils.mockWithReturn(
    OrderTblProvider.class, 
    order
);
```

---

## 集成测试编写规范

### 基本原则

1. **必须继承BaseIntegrationTest**
2. **使用真实组件，不Mock**
3. **确保测试隔离**（使用`@Rollback`）
4. **可以测试组件协作**
5. **可以使用Testcontainers**

### API集成测试

**使用RestAssured进行HTTP测试**:
```java
@DisplayName("Order API集成测试")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderApiIntegrationTest extends BaseIntegrationTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
        RestAssured.baseURI = "http://localhost:" + port;
        RestAssured.basePath = "/api/order";
    }

    @Test
    @DisplayName("测试订单插入接口")
    void testInsert() {
        // Given: 准备请求数据
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("orderId", "ORDER001");

        // When: 调用接口
        ResponseUtils<?> response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/insert")
                .then()
                .statusCode(200)
                .extract()
                .as(ResponseUtils.class);

        // Then: 验证响应
        TestAssertUtils.assertSuccess(response);
    }
}
```

### 数据库集成测试

```java
@DisplayName("Order Repository集成测试")
class OrderRepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private OrderTblProvider orderTblProvider;

    @Test
    @DisplayName("测试保存订单")
    @Rollback
    void testSave() {
        // Given: 准备测试数据
        OrderTbl order = OrderTblFixture.create();

        // When: 保存
        boolean result = orderTblProvider.save(order);

        // Then: 验证
        assertThat(result).isTrue();
        assertThat(order.getOrderId()).isNotNull();
    }
}
```

### 使用Testcontainers

**在集成测试中使用Testcontainers**（可选）:
```java
@SpringBootTest
@ContextConfiguration(initializers = TestContainersConfig.class)
class OrderIntegrationTest extends BaseIntegrationTest {
    // 测试代码
}
```

**注意**: Testcontainers需要Docker环境，如不使用可在测试中移除容器配置。

---

## 性能测试编写规范

### 基本原则

1. **继承BasePerformanceTest**
2. **使用JMH注解配置**
3. **使用benchmark profile编译**
4. **关注预热和测量次数**

### 性能测试模板

```java
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class OrderServiceBenchmark extends BasePerformanceTest {

    @Setup(Level.Trial)
    public void setup() {
        // 测试数据准备
    }

    @Benchmark
    public void benchmarkGetOrder() {
        // 性能测试代码
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(OrderServiceBenchmark.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}
```

---

## 压力测试编写规范

### 基本原则

1. **继承BaseLoadTest**
2. **使用Apache HttpClient发送HTTP请求**
3. **数据保留在数据库（不回滚）**
4. **关注基础指标：TPS、响应时间、成功率**
5. **确保生成的数据符合数据库字段长度限制**

### 压力测试模板

```java
@DisplayName("Order Insert API 压力测试")
public class OrderInsertLoadTest extends BaseLoadTest {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 创建订单请求对象
     */
    private String createOrderRequest() {
        OrderInsertRequest request = new OrderInsertRequest();
        
        // 使用TestDataBuilder生成符合数据库长度的唯一ID
        request.setOrderId(TestDataBuilder.generateId("ORDER"));  // 22字符
        request.setTradeId(TestDataBuilder.generateId("TRADE"));  // 20字符
        request.setUserId(TestDataBuilder.generateId("USER"));    // 20字符
        // ... 其他字段
        
        return objectMapper.writeValueAsString(request);
    }
    
    @Test
    @DisplayName("基准压力测试（10并发，60秒）")
    void testInsert_BaselineLoad() {
        LoadTestConfig config = LoadTestConfig.builder()
                .url(baseUrl + "/order/insert")
                .httpMethod("POST")
                .concurrentThreads(10)
                .durationSeconds(60)
                .warmupSeconds(10)
                .requestBodySupplier(this::createOrderRequest)
                .header("Content-Type", "application/json")
                .build();
        
        LoadTestResult result = executor.execute(config);
        result.printReport();
        
        // 验证测试结果
        assertThat(result.getSuccessRate()).isGreaterThanOrEqualTo(99.0);
        assertThat(result.getAvgResponseTimeMs()).isLessThanOrEqualTo(500.0);
    }
}
```

### 压力测试配置

**LoadTestConfig配置项：**
- `url`: 测试目标URL
- `httpMethod`: HTTP方法（POST/GET等）
- `concurrentThreads`: 并发线程数（默认10）
- `durationSeconds`: 测试持续时间（默认60秒）
- `warmupSeconds`: 预热时间（默认10秒）
- `requestBodySupplier`: 请求体生成器
- `headers`: 请求头
- `connectTimeoutMs`: 连接超时（默认5000ms）
- `readTimeoutMs`: 读取超时（默认30000ms）

### 测试结果分析

**LoadTestResult包含的指标：**
- `totalRequests`: 总请求数
- `successRequests`: 成功请求数
- `failedRequests`: 失败请求数
- `avgResponseTimeMs`: 平均响应时间
- `minResponseTimeMs`: 最小响应时间
- `maxResponseTimeMs`: 最大响应时间
- `p50/p90/p95/p99ResponseTimeMs`: 响应时间分位数
- `tps`: 每秒事务数
- `successRate`: 成功率（百分比）
- `errors`: 错误详情列表

### 测试报告示例

```
========================================
Load Test Report
========================================
Test Duration: 60.00 seconds
Concurrent Threads: 10
----------------------------------------
Total Requests: 15,234
Successful Requests: 15,180 (99.6%)
Failed Requests: 54 (0.4%)
----------------------------------------
TPS: 253.90 requests/sec
Avg Response Time: 38.5 ms
Min Response Time: 12 ms
Max Response Time: 523 ms
----------------------------------------
P50 Response Time: 35 ms
P90 Response Time: 68 ms
P95 Response Time: 92 ms
P99 Response Time: 156 ms
========================================
```

### 数据库字段长度注意事项

**重要**: 生成的测试数据必须符合数据库字段长度限制，否则会导致测试失败。

参考 `order_tbl` 表字段限制：
- `order_id`: varchar(22) - 使用 `TestDataBuilder.generateId("ORDER")` 生成22字符
- `trade_id`: varchar(20) - 使用 `TestDataBuilder.generateId("TRADE")` 生成20字符
- `user_id`: varchar(20) - 使用 `TestDataBuilder.generateId("USER")` 生成20字符
- `partner_id`: varchar(50) - 使用 `TestDataBuilder.generateId("PARTNER")` 生成29字符
- `supplier_id`: varchar(64) - 使用 `TestDataBuilder.generateId("SUPPLIER")` 生成30字符
- `idempotent_id`: varchar(50) - 使用 `TestDataBuilder.generateUUID("IDEM")` 生成22字符

`TestDataBuilder` 已经针对这些字段优化，自动生成符合长度限制的唯一ID。

---

## 工具类和Fixture使用

### MockUtils - Mock工具类

**位置**: `com.soyokra.sprival.util.MockUtils`

**常用方法**:
```java
// 创建Mock对象
OrderTblProvider provider = MockUtils.mock(OrderTblProvider.class);

// 创建Spy对象
OrderService service = MockUtils.spy(orderService);

// 创建带返回值的Mock
OrderTblProvider provider = MockUtils.mockWithReturn(
    OrderTblProvider.class, 
    order
);

// 重置Mock
MockUtils.reset(provider);
```

### JsonUtils - JSON工具类

**位置**: `com.soyokra.sprival.util.JsonUtils`

**常用方法**:
```java
// 对象转JSON
String json = JsonUtils.toJson(order);

// JSON转对象
OrderTbl order = JsonUtils.fromJson(json, OrderTbl.class);

// 从资源文件读取JSON
OrderTbl order = JsonUtils.fromResource("test-data/order.json", OrderTbl.class);
```

### TestAssertUtils - 断言工具类

**位置**: `com.soyokra.sprival.util.TestAssertUtils`

**常用方法**:
```java
// 断言成功响应
TestAssertUtils.assertSuccess(response);

// 断言成功响应且包含数据
TestAssertUtils.assertSuccessWithData(response);

// 断言成功响应且数据匹配
TestAssertUtils.assertSuccessWithData(response, expectedData);

// 断言错误响应
TestAssertUtils.assertError(response, errorCode);
TestAssertUtils.assertError(response, errorCode, errorMessage);
```

### OrderTblFixture - Order测试数据构造器

**位置**: `com.soyokra.sprival.fixture.OrderTblFixture`

**使用方法**:
```java
// 使用默认值创建
OrderTbl order = OrderTblFixture.create();

// 覆盖部分字段
OrderTbl order = OrderTblFixture.create(o -> {
    o.setOrderId("ORDER001");
    o.setUserId("USER001");
});

// 创建指定ID的订单
OrderTbl order = OrderTblFixture.createSaved("ORDER001");

// 创建指定用户的订单
OrderTbl order = OrderTblFixture.createForUser("USER001");

// 创建指定状态的订单
OrderTbl order = OrderTblFixture.createWithStatus(2);
```

### TestDataBuilder - 通用数据构造器

**位置**: `com.soyokra.sprival.fixture.TestDataBuilder`

**常用方法**:
```java
// 生成唯一ID
String id = TestDataBuilder.generateId();
String id = TestDataBuilder.generateId("ORDER");

// 获取当前时间
LocalDateTime now = TestDataBuilder.now();

// 生成测试字符串
String str = TestDataBuilder.generateString("TEST");
```

### 创建新的Fixture

**为新的业务实体创建Fixture**:
```java
package com.soyokra.sprival.fixture;

import com.soyokra.sprival.app.model.User;
import java.util.function.Consumer;

public final class UserFixture {

    private UserFixture() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }

    public static User create() {
        return create(null);
    }

    public static User create(Consumer<User> customizer) {
        User user = new User();
        // 设置默认值
        user.setUserId(TestDataBuilder.generateId("USER"));
        user.setUsername("test_user");
        
        // 应用自定义配置
        if (customizer != null) {
            customizer.accept(user);
        }
        
        return user;
    }
}
```

---

## 测试配置说明

### application.properties

**位置**: `src/test/resources/application.properties`

**特点**:
- 继承主应用的`application.properties`配置
- 仅在必要时覆盖测试特定配置
- 通过`@TestPropertySource`加载

**测试环境特定配置**:
```properties
# 日志级别（测试环境使用DEBUG）
logging.level.com.soyokra.sprival = DEBUG

# Jetty访问日志（测试环境关闭）
server.jetty.accesslog.enabled = false

# 禁用监控指标收集（可选）
management.metrics.export.prometheus.enabled = false
```

### TestContainersConfig

**位置**: `com.soyokra.sprival.config.TestContainersConfig`

**功能**:
- 管理测试容器（MySQL等）
- 自动覆盖数据源配置
- 容器生命周期管理

**使用**:
```java
@SpringBootTest
@ContextConfiguration(initializers = TestContainersConfig.class)
class MyIntegrationTest extends BaseIntegrationTest {
    // 测试代码
}
```

**注意**: Testcontainers需要Docker环境。

---

## 运行测试

### 运行单元测试

```bash
# 运行所有单元测试
mvn test

# 运行指定测试类
mvn test -Dtest=OrderServiceTest

# 运行指定包下的测试
mvn test -Dtest=com.soyokra.sprival.unit.service.*
```

### 运行集成测试

```bash
# 运行集成测试（包含单元测试）
mvn verify

# 仅运行集成测试
mvn failsafe:integration-test
```

### 运行性能测试

```bash
# 使用benchmark profile编译和运行
mvn clean test -Pbenchmark

# 直接运行JMH基准测试
mvn exec:java -Dexec.mainClass="com.soyokra.sprival.performance.OrderServiceBenchmark"
```

### 运行压力测试

```bash
# 运行所有压力测试
mvn test -Dtest=*LoadTest

# 运行指定压力测试
mvn test -Dtest=OrderInsertLoadTest

# 运行指定测试方法
mvn test -Dtest=OrderInsertLoadTest#testInsert_QuickLoad

# 快速验证（10并发，10秒）
mvn test -Dtest=OrderInsertLoadTest#testInsert_QuickLoad

# 基准测试（10并发，60秒）
mvn test -Dtest=OrderInsertLoadTest#testInsert_BaselineLoad

# 高并发测试（50并发，60秒）
mvn test -Dtest=OrderInsertLoadTest#testInsert_HighConcurrency

# 持久性测试（10并发，300秒）
mvn test -Dtest=OrderInsertLoadTest#testInsert_Endurance
```

**注意**: 
- 压力测试需要Spring Boot应用启动，测试时间较长
- 测试数据会保留在本地数据库中
- 确保数据库连接正常且有足够空间

### 查看代码覆盖率

```bash
# 运行测试并生成覆盖率报告
mvn test

# 查看覆盖率报告
# 报告位置: target/site/jacoco/index.html
```

### 跳过测试

```bash
# 编译时跳过测试
mvn clean package -DskipTests

# 编译和运行都跳过测试
mvn clean package -Dmaven.test.skip=true
```

---

## 测试编写检查清单

### 单元测试检查清单

- [ ] 继承`BaseUnitTest`
- [ ] 使用`@DisplayName`注解
- [ ] 使用Given-When-Then结构
- [ ] Mock外部依赖，不依赖真实组件
- [ ] 测试方法命名清晰（`testFunction`或`testFunction_Scenario`）
- [ ] 验证所有重要的方法调用（使用`verify`）
- [ ] 覆盖正常场景和异常场景
- [ ] 使用Fixture构造测试数据

### 集成测试检查清单

- [ ] 继承`BaseIntegrationTest`
- [ ] 使用`@DisplayName`注解
- [ ] 使用`@Rollback`明确事务行为
- [ ] 确保测试数据隔离
- [ ] 使用真实的Spring上下文
- [ ] 可以测试组件协作
- [ ] 使用Fixture构造测试数据

### 通用检查清单

- [ ] 测试类命名符合规范（`XxxTest`, `XxxIntegrationTest`, `XxxBenchmark`）
- [ ] 测试方法使用`@DisplayName`描述测试目的
- [ ] 测试代码清晰易读
- [ ] 使用工具类减少重复代码
- [ ] 测试失败时能快速定位问题

---

## 常见问题和最佳实践

### 1. 测试数据隔离

**问题**: 测试之间数据污染

**解决**: 
- 集成测试使用`@Transactional`和`@Rollback`
- 使用唯一ID（`TestDataBuilder.generateId()`）
- 清理测试数据（`@AfterEach`或`@BeforeEach`）

### 2. 测试执行速度

**问题**: 测试执行过慢

**解决**:
- 单元测试不依赖外部服务
- 集成测试使用轻量级Mock或嵌入式服务
- 合理使用Testcontainers（仅必要时）

### 3. Mock对象使用

**问题**: Mock配置复杂

**解决**:
- 使用`MockUtils`工具类
- 使用`@Mock`注解
- 提取Mock配置到`@BeforeEach`方法

### 4. 测试数据构造

**问题**: 测试数据构造重复

**解决**:
- 使用Fixture数据构造器
- 使用`TestDataBuilder`生成通用数据
- 创建领域特定的Fixture

### 5. 断言使用

**问题**: 断言不够清晰

**解决**:
- 使用AssertJ的流式断言
- 使用`TestAssertUtils`针对业务对象断言
- 提供清晰的错误消息

---

## 参考示例

### 完整的单元测试示例

参见: `src/test/java/com/soyokra/sprival/unit/service/OrderServiceTest.java`

### 完整的集成测试示例

参见: `src/test/java/com/soyokra/sprival/integration/api/OrderApiIntegrationTest.java`

### 完整的Fixture示例

参见: `src/test/java/com/soyokra/sprival/fixture/OrderTblFixture.java`

---

## 更新日志

- **2025-11-03**: 重新设计测试框架，新增压力测试支持
  - 新增 `BaseLoadTest` 压力测试基类
  - 新增 `HttpLoadTestExecutor`、`LoadTestConfig`、`LoadTestResult`、`LoadTestStatistics` 压力测试工具类
  - 优化 `TestDataBuilder`，确保生成的ID符合数据库字段长度限制
  - 实现 `OrderInsertLoadTest` 示例压力测试
- **2025-10-25**: 创建测试框架和规范文档
- 后续更新将在此记录

---

## 相关文档

- [FILE-INDEX.md](FILE-INDEX.md) - 文件快速定位
- [QUICK-REFERENCE.md](QUICK-REFERENCE.md) - 快速参考手册
- [ENCODING-STANDARDS.md](ENCODING-STANDARDS.md) - 编码标准

