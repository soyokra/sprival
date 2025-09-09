# Sprival项目测试体系

## 概述

本文档介绍了Sprival项目的测试体系，包括单元测试、集成测试、测试配置和测试工具。

## 测试结构

```
src/test/
├── java/com/soyokra/sprival/
│   ├── config/                    # 测试配置类
│   │   └── TestConfiguration.java
│   ├── integration/               # 集成测试
│   │   ├── UserServiceIntegrationTest.java
│   │   ├── RedisIntegrationTest.java
│   │   └── DatabaseIntegrationTest.java
│   ├── service/                   # 服务层单元测试
│   │   └── UserServiceTest.java
│   ├── controller/                # 控制器单元测试
│   │   └── HealthTestControllerTest.java
│   ├── util/                      # 测试工具类
│   │   ├── TestDataBuilder.java
│   │   ├── TestConstants.java
│   │   ├── TestRedisConfig.java
│   │   └── TestMongoConfig.java
│   ├── TestApplication.java       # 测试应用启动类
│   └── TestSuite.java            # 测试套件
└── resources/
    ├── application-test.properties # 测试环境配置
    ├── test-schema.sql            # 测试数据库表结构
    ├── test-data.sql              # 测试数据
    └── logback-test.xml           # 测试日志配置
```

## 测试类型

### 1. 单元测试 (Unit Tests)

- **位置**: `src/test/java/com/soyokra/sprival/service/`, `src/test/java/com/soyokra/sprival/controller/`
- **特点**: 使用Mock对象，测试单个类或方法
- **工具**: JUnit 5, Mockito, AssertJ
- **命名**: `*Test.java`

### 2. 集成测试 (Integration Tests)

- **位置**: `src/test/java/com/soyokra/sprival/integration/`
- **特点**: 测试多个组件之间的交互
- **工具**: Spring Boot Test, TestContainers, 嵌入式数据库
- **命名**: `*IntegrationTest.java`

### 3. 配置测试 (Configuration Tests)

- **位置**: `src/test/java/com/soyokra/sprival/config/`
- **特点**: 测试Spring配置类
- **工具**: Spring Boot Test, 嵌入式服务
- **命名**: `*ConfigurationTest.java`

## 测试环境

### 测试配置

- **配置文件**: `application-test.properties`
- **数据库**: H2内存数据库
- **Redis**: 嵌入式Redis服务器
- **MongoDB**: 嵌入式MongoDB服务器
- **日志**: 独立的测试日志配置

### 测试数据

- **表结构**: `test-schema.sql`
- **测试数据**: `test-data.sql`
- **数据构建器**: `TestDataBuilder.java`
- **测试常量**: `TestConstants.java`

## 运行测试

### 运行所有测试

```bash
# 运行所有测试
mvn test

# 运行单元测试
mvn test -Dtest="*Test"

# 运行集成测试
mvn test -Dtest="*IntegrationTest"

# 运行测试套件
mvn test -Dtest="TestSuite"
```

### 运行特定测试

```bash
# 运行特定测试类
mvn test -Dtest="UserServiceTest"

# 运行特定测试方法
mvn test -Dtest="UserServiceTest#testGetUserById_Success"

# 运行特定包下的测试
mvn test -Dtest="com.soyokra.sprival.service.*"
```

### 生成测试报告

```bash
# 生成测试报告
mvn test jacoco:report

# 查看测试覆盖率报告
open target/site/jacoco/index.html
```

## 测试工具

### 1. 测试数据构建器

```java
// 构建用户响应对象
UserServiceClient.UserResponse user = TestDataBuilder.buildUserResponse(1L, "testuser", "test@example.com");

// 构建用户列表
List<UserServiceClient.UserResponse> users = TestDataBuilder.buildUserList(3);
```

### 2. 测试常量

```java
// 使用测试常量
String username = TestConstants.TEST_USERNAME;
String email = TestConstants.TEST_EMAIL;
Long userId = TestConstants.TEST_USER_ID;
```

### 3. 测试配置

```java
// 使用测试配置
@ActiveProfiles("test")
@SpringBootTest
class MyTest {
    // 测试代码
}
```

## 测试最佳实践

### 1. 测试命名

- 测试方法名应该描述测试场景和预期结果
- 使用`@DisplayName`注解提供更清晰的测试描述
- 格式: `test[MethodName]_[Scenario]_[ExpectedResult]`

### 2. 测试结构

- 使用Given-When-Then模式组织测试代码
- 每个测试方法只测试一个场景
- 使用`@BeforeEach`和`@AfterEach`进行测试准备和清理

### 3. 断言

- 使用AssertJ进行流畅的断言
- 提供清晰的错误消息
- 测试边界条件和异常情况

### 4. Mock使用

- 只Mock外部依赖，不Mock被测试的类
- 使用`@MockBean`替代`@Mock`进行Spring集成测试
- 验证Mock对象的调用

### 5. 测试数据

- 使用测试数据构建器创建测试数据
- 避免硬编码测试数据
- 确保测试数据的独立性

## 测试覆盖率

### 覆盖率目标

- **行覆盖率**: ≥ 80%
- **分支覆盖率**: ≥ 70%
- **方法覆盖率**: ≥ 90%

### 覆盖率报告

- 使用JaCoCo生成覆盖率报告
- 排除配置类、DTO类和实体类
- 定期检查覆盖率报告

## 持续集成

### CI/CD配置

```yaml
# GitHub Actions示例
name: Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Set up JDK 8
        uses: actions/setup-java@v2
        with:
          java-version: '8'
          distribution: 'adopt'
      - name: Run tests
        run: mvn test
      - name: Generate coverage report
        run: mvn jacoco:report
```

## 故障排除

### 常见问题

1. **测试数据库连接失败**
   - 检查H2数据库配置
   - 确保测试数据脚本正确

2. **Redis连接失败**
   - 检查嵌入式Redis配置
   - 确保端口没有被占用

3. **MongoDB连接失败**
   - 检查嵌入式MongoDB配置
   - 确保MongoDB版本兼容

4. **测试超时**
   - 检查测试超时配置
   - 优化测试性能

### 调试技巧

- 使用`@DirtiesContext`重新加载Spring上下文
- 使用`@Transactional`回滚测试数据
- 使用`@MockBean`替换Spring Bean
- 使用`@TestPropertySource`覆盖配置

## 扩展测试

### 添加新测试

1. 创建测试类
2. 添加测试方法
3. 更新测试套件
4. 运行测试验证

### 测试模板

```java
@ExtendWith(MockitoExtension.class)
@DisplayName("测试类描述")
class MyTest {

    @Mock
    private MyDependency myDependency;

    @InjectMocks
    private MyService myService;

    @Test
    @DisplayName("测试方法描述")
    void testMethod_Success() {
        // Given
        // 准备测试数据

        // When
        // 执行被测试的方法

        // Then
        // 验证结果
    }
}
```

## 总结

Sprival项目的测试体系提供了完整的测试解决方案，包括单元测试、集成测试、测试工具和测试配置。通过遵循测试最佳实践，可以确保代码质量和系统稳定性。
