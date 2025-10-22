# 编码规范和跨平台兼容性

## 问题背景

Sprival项目在Windows GBK环境下开发，存在跨平台编码兼容性问题：
- **开发环境**: Windows 11 + GBK编码 (代码页936)
- **目标环境**: Linux/Mac + UTF-8编码
- **风险**: 中文注释、日志输出、文件名等可能出现乱码

## 编码标准化方案

### 1. Maven项目编码配置

#### 当前配置 ✅
```xml
<properties>
    <java.version>1.8</java.version>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
</properties>
```

#### 增强配置
```xml
<properties>
    <!-- 编码标准化 -->
    <java.version>1.8</java.version>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    <maven.compiler.encoding>UTF-8</maven.compiler.encoding>
    <file.encoding>UTF-8</file.encoding>
</properties>

<build>
    <plugins>
        <!-- Maven编译插件 -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.10.1</version>
            <configuration>
                <source>1.8</source>
                <target>1.8</target>
                <encoding>UTF-8</encoding>
                <compilerArgs>
                    <arg>-Dfile.encoding=UTF-8</arg>
                </compilerArgs>
            </configuration>
        </plugin>
        
        <!-- Maven资源插件 -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-resources-plugin</artifactId>
            <version>3.2.0</version>
            <configuration>
                <encoding>UTF-8</encoding>
            </configuration>
        </plugin>
        
        <!-- Maven Surefire插件 -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>3.0.0-M7</version>
            <configuration>
                <argLine>-Dfile.encoding=UTF-8</argLine>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### 2. Spring Boot应用编码配置

#### application.properties
```properties
# 应用编码配置
spring.application.name = sprival
server.servlet.encoding.charset = UTF-8
server.servlet.encoding.enabled = true
server.servlet.encoding.force = true

# HTTP编码配置
spring.http.encoding.charset = UTF-8
spring.http.encoding.enabled = true
spring.http.encoding.force = true

# 日志编码配置
logging.charset.console = UTF-8
logging.charset.file = UTF-8
```

### 3. JVM运行时编码配置

#### 启动参数
```bash
# 开发环境启动参数
java -Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8 -jar sprival.jar

# Maven运行参数
export MAVEN_OPTS="-Dfile.encoding=UTF-8"
mvn spring-boot:run -Dfile.encoding=UTF-8
```

#### IDE配置
```properties
# IntelliJ IDEA 配置
idea.system.file.encoding = UTF-8
idea.native2ascii.lowercase = true

# Eclipse 配置
-Dfile.encoding=UTF-8
-Dconsole.encoding=UTF-8
```

### 4. 开发规范

#### 4.1 代码注释规范
```java
/**
 * 用户服务类
 * 
 * @author Sprival Team
 * @version 1.0
 * @since 2024-01-01
 */
@Service
public class UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    /**
     * 查询用户信息
     * 
     * @param userId 用户ID
     * @return 用户信息
     */
    public User findUserById(Long userId) {
        logger.info("查询用户信息, userId: {}", userId);
        // 业务逻辑
        return null;
    }
}
```

#### 4.2 日志输出规范
```java
// ✅ 推荐：使用参数化日志，避免中文字符串拼接
logger.info("用户登录成功, username: {}, loginTime: {}", username, loginTime);

// ❌ 避免：直接中文字符串拼接
logger.info("用户" + username + "登录成功");

// ✅ 异常日志
try {
    // 业务逻辑
} catch (Exception e) {
    logger.error("用户操作失败, userId: {}", userId, e);
}
```

#### 4.3 配置文件规范
```yaml
# application.yml - 推荐使用YAML格式
spring:
  application:
    name: sprival
  datasource:
    dynamic:
      primary: master
      datasource:
        master:
          url: jdbc:mysql://localhost:3306/sprival?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
```

### 5. Git配置

#### .gitattributes 文件
```gitattributes
# 文本文件统一使用LF换行符
* text=auto eol=lf

# Java源文件
*.java text eol=lf encoding=UTF-8
*.xml text eol=lf encoding=UTF-8
*.properties text eol=lf encoding=UTF-8
*.yml text eol=lf encoding=UTF-8
*.yaml text eol=lf encoding=UTF-8
*.md text eol=lf encoding=UTF-8

# 脚本文件
*.sh text eol=lf
*.bat text eol=crlf

# 二进制文件
*.jar binary
*.war binary
*.class binary
*.pdf binary
*.png binary
*.jpg binary
*.jpeg binary
*.gif binary
```

#### Git全局配置
```bash
# 配置Git使用UTF-8编码
git config --global core.quotepath false
git config --global gui.encoding utf-8
git config --global i18n.commit.encoding utf-8
git config --global i18n.logoutputencoding utf-8
```

### 6. 跨平台测试

#### 编码验证脚本
```bash
#!/bin/bash
# encoding-test.sh - 编码兼容性测试脚本

echo "=== 编码兼容性测试 ==="

# 1. 检查Java文件编码
echo "检查Java源文件编码..."
find src -name "*.java" -exec file {} \; | grep -v UTF-8 && echo "发现非UTF-8文件" || echo "所有Java文件都是UTF-8编码"

# 2. 检查配置文件编码
echo "检查配置文件编码..."
find src -name "*.properties" -o -name "*.yml" -o -name "*.yaml" | xargs file | grep -v UTF-8 && echo "发现非UTF-8配置文件" || echo "所有配置文件都是UTF-8编码"

# 3. 编译测试
echo "执行编译测试..."
mvn clean compile -Dfile.encoding=UTF-8

# 4. 运行测试
echo "执行单元测试..."
mvn test -Dfile.encoding=UTF-8

echo "=== 测试完成 ==="
```

### 7. Docker环境配置

#### Dockerfile
```dockerfile
FROM openjdk:8-jre-alpine

# 设置环境变量
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8
ENV JAVA_OPTS="-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8"

# 复制应用
COPY target/sprival-*.jar app.jar

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app.jar"]
```

## 问题排查指南

### 常见编码问题

#### 1. 中文注释乱码
**现象**: Linux环境下查看源码时中文注释显示乱码
**原因**: 文件保存时使用了GBK编码
**解决**: 
```bash
# 转换文件编码
iconv -f GBK -t UTF-8 源文件.java > 新文件.java
```

#### 2. 日志输出乱码
**现象**: 应用日志中中文显示为问号或乱码
**解决**: 
```bash
# 启动时指定编码
java -Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8 -jar app.jar
```

#### 3. 数据库中文乱码
**现象**: 数据库中存储的中文显示乱码
**解决**:
```properties
# 数据库连接URL添加编码参数
spring.datasource.url=jdbc:mysql://localhost:3306/sprival?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
```

### 验证方法

#### 1. 本地验证
```bash
# Windows环境验证
mvn clean compile -Dfile.encoding=UTF-8
mvn test -Dfile.encoding=UTF-8

# 检查编译后的class文件
javap -cp target/classes com.soyokra.sprival.SprivalApplication
```

#### 2. 跨平台验证
```bash
# Linux/Mac环境验证
export LANG=en_US.UTF-8
export LC_ALL=en_US.UTF-8
mvn clean compile test
```

## 最佳实践建议

### 1. 开发环境统一
- 统一使用UTF-8编码
- IDE设置统一编码格式
- Git配置统一换行符

### 2. 代码编写规范
- 避免在代码中硬编码中文字符串
- 使用资源文件管理国际化文本
- 日志使用参数化输出

### 3. 持续集成
- CI/CD环境设置UTF-8编码
- 跨平台编译测试
- 自动化编码检查

### 4. 文档管理
- 文档文件统一UTF-8编码
- Markdown文件注意编码格式
- README等说明文件跨平台测试

---

*本文档确保Sprival项目在不同平台间的编码兼容性，避免中文内容出现乱码问题。*
