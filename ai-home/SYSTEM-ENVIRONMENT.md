# 系统环境配置

> 记录项目开发环境要求和配置注意事项

## 环境要求

### 必需环境
- **JDK**: 1.8+ (建议 1.8.0_341+)
- **Maven**: 3.6+ (建议 3.8.9+)
- **Docker**: 20.10+ (用于运行中间件)
- **Docker Compose**: 1.29+

### 推荐环境
- **IDE**: IntelliJ IDEA / Eclipse
- **Git**: 2.0+
- **Shell**: PowerShell 7+ (Windows) / Bash (Linux/Mac)

## Java 环境配置

### JDK 安装
- **版本**: JDK 1.8
- **供应商**: Oracle JDK / OpenJDK 均可
- **架构**: x64 (64位)

### 环境变量配置

#### Windows
```powershell
# 系统环境变量
JAVA_HOME = C:\Program Files\Java\jdk1.8.0_xxx
MAVEN_HOME = C:\Program Files\Java\apache-maven-3.8.9

# 添加到 PATH
%JAVA_HOME%\bin
%MAVEN_HOME%\bin
```

#### Linux/Mac
```bash
# ~/.bashrc 或 ~/.zshrc
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
export MAVEN_HOME=/usr/share/maven
export PATH=$JAVA_HOME/bin:$MAVEN_HOME/bin:$PATH
```

### 验证安装
```bash
# 查看 Java 版本
java -version

# 查看 Maven 版本
mvn -version

# 查看 Docker 版本
docker --version
docker-compose --version
```

## Maven 配置

### settings.xml 配置

#### 本地仓库路径
```xml
<!-- Windows -->
<localRepository>C:/Users/{用户名}/.m2/repository</localRepository>

<!-- Linux/Mac -->
<localRepository>/home/{用户名}/.m2/repository</localRepository>
```

#### 镜像配置（可选，加速下载）
```xml
<mirrors>
  <mirror>
    <id>aliyun</id>
    <mirrorOf>central</mirrorOf>
    <url>https://maven.aliyun.com/repository/public</url>
  </mirror>
</mirrors>
```

## 编码配置

### 重要：统一使用 UTF-8

#### Maven 编码配置
在项目 `pom.xml` 中已配置：
```xml
<properties>
  <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
  <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
  <maven.compiler.encoding>UTF-8</maven.compiler.encoding>
</properties>
```

#### IDE 编码配置
- **IntelliJ IDEA**: Settings → Editor → File Encodings → UTF-8
- **Eclipse**: Preferences → General → Workspace → Text file encoding → UTF-8

#### Maven 命令行编码（Windows）
```powershell
# PowerShell 7 默认 UTF-8
# 如果使用旧版 PowerShell，设置编码
[Console]::OutputEncoding = [System.Text.Encoding]::UTF-8

# Maven 命令添加编码参数
mvn clean package -Dfile.encoding=UTF-8
```

## Docker 配置

### Docker Desktop (Windows/Mac)
- **内存**: 建议分配 4GB+
- **CPU**: 建议分配 2 核+
- **磁盘**: 建议预留 20GB+

### Docker Compose
- **版本**: 1.29.0+
- **文件位置**: `docker/sprival-middleware/docker-compose.yml`

### 验证 Docker
```bash
# 检查 Docker 状态
docker ps

# 检查 Docker Compose
docker-compose version
```

## 常见问题

### Windows 编码问题

#### 问题：Maven 编译出现中文乱码
**原因**: Windows 默认使用 GBK 编码  
**解决**:
```powershell
# 方案 1: 使用 PowerShell 7 (推荐)
# 下载: https://github.com/PowerShell/PowerShell/releases

# 方案 2: Maven 命令添加编码参数
mvn clean package -Dfile.encoding=UTF-8

# 方案 3: 设置 MAVEN_OPTS
$env:MAVEN_OPTS="-Dfile.encoding=UTF-8"
```

#### 问题：日志文件中文乱码
**解决**: 确保以下配置：
1. `application.properties` 中配置 UTF-8
2. `logback-kafka.xml` 中配置 UTF-8
3. 项目 `pom.xml` 中配置 UTF-8

### Docker 相关问题

#### 问题：Docker 容器启动失败
**检查步骤**:
```bash
# 1. 查看容器状态
docker ps -a

# 2. 查看容器日志
docker logs <container-id>

# 3. 检查端口占用
# Windows
netstat -ano | findstr "3306"

# Linux/Mac
lsof -i :3306
```

#### 问题：Docker Compose 启动慢
**优化建议**:
- 增加 Docker Desktop 内存分配
- 使用国内镜像源
- 预先拉取镜像

### Java 版本问题

#### 问题：项目要求 Java 8，但系统安装了多个版本
**解决**: 确保 `JAVA_HOME` 指向 JDK 1.8
```bash
# 验证当前使用的 Java 版本
java -version
echo $JAVA_HOME  # Linux/Mac
echo %JAVA_HOME% # Windows
```

## 项目特定配置

### 配置位置
- **主配置**: `src/main/resources/application.properties`
- **日志配置**: `src/main/resources/logback-kafka.xml`
- **测试配置**: `src/test/resources/application-test.properties`

### 配置原则（来自项目记忆）
1. ✅ 配置直接写在 `application.properties` 中
2. ✅ 不加载外部配置文件
3. ✅ 使用扁平化的 Redis/Redisson 配置格式
4. ✅ 每次修改代码后必须重启应用并检查错误

## 开发工具推荐

### IntelliJ IDEA 插件
- **Lombok**: 减少样板代码
- **MyBatisX**: MyBatis 增强
- **Docker**: Docker 集成
- **Rainbow Brackets**: 彩虹括号

### VS Code 扩展（可选）
- **Java Extension Pack**: Java 开发套件
- **Spring Boot Extension Pack**: Spring Boot 支持

---

**最后更新**: 2025-10-22  
**参考**: 详细配置参见项目文档 `docs/reference/`
