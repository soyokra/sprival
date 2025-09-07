# 系统环境配置信息

## 概述

本文档记录了Sprival项目的开发环境配置信息，确保所有依赖和工具版本与系统环境兼容。

## 系统基本信息

### 操作系统
- **系统**: Windows 11
- **版本**: 10.0 (build 26100)
- **架构**: amd64 (64位)
- **编码**: GBK (中文环境)
- **时区**: 中国标准时间 (GMT+8)

### 硬件架构
- **处理器架构**: x64
- **系统类型**: 64位操作系统

## Java开发环境

### JDK配置
- **JDK版本**: 1.8.0_333
- **JDK安装路径**: `C:\Program Files\Java\jdk1.8.0_333`
- **JRE版本**: 1.8.0_341
- **JRE路径**: `C:\Program Files\Java\jre1.8.0_341`
- **供应商**: Oracle Corporation
- **虚拟机**: Java HotSpot(TM) 64-Bit Server VM

### 环境变量配置
```bash
# 系统环境变量
JAVA_HOME = C:\Program Files\Java\jdk1.8.0_333
MAVEN_HOME = C:\Program Files\Java\apache-maven-3.8.9

# PATH中的Java路径（按优先级排序）
PATH = %JAVA_HOME%\bin;C:\Program Files (x86)\Common Files\Oracle\Java\javapath;...
```

### Java工具路径
- **java.exe**: `C:\Program Files\Java\jdk1.8.0_333\bin\java.exe`
- **javac.exe**: `C:\Program Files\Java\jdk1.8.0_333\bin\javac.exe`
- **jar.exe**: `C:\Program Files\Java\jdk1.8.0_333\bin\jar.exe`

## Maven构建环境

### Maven配置
- **Maven版本**: Apache Maven 3.8.9
- **Maven安装路径**: `C:\Program Files\Java\apache-maven-3.8.9`
- **构建工具ID**: e26b057cc3a17459358ef53e4d0e2e381bf08a1c
- **使用的Java**: 1.8.0_333 (JDK)
- **默认编码**: UTF-8

### Maven仓库
- **本地仓库**: `C:\Users\soyok\.m2\repository`
- **中央仓库**: https://repo.maven.apache.org/maven2

## 项目兼容性配置

### Java版本约束
- **编译目标**: Java 1.8 (52.0 class file version)
- **最大兼容**: Java 8 LTS
- **不兼容**: Java 11+ 编译的依赖包（class file version 55.0+）

### 关键依赖版本限制

#### Spring Boot生态
- **Spring Boot**: 2.7.18 (Java 8兼容的最后版本)
- **Spring Framework**: 5.3.x 系列
- **Spring Cloud**: 2021.0.8

#### MyBatis-Plus生态
```xml
<!-- ✅ Java 8兼容版本 -->
<mybatis-plus-boot-starter>3.5.7</mybatis-plus-boot-starter>
<mybatis-plus-generator>3.5.7</mybatis-plus-generator>

<!-- ❌ 避免使用（需要Java 11+）-->
<!-- <mybatis-plus-boot-starter>3.5.9+</mybatis-plus-boot-starter> -->
<!-- <mybatis-plus-jsqlparser>3.5.9+</mybatis-plus-jsqlparser> -->
```

#### 数据库驱动
```xml
<!-- ✅ 新坐标（推荐） -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>8.0.33</version>
</dependency>

<!-- ❌ 旧坐标（已废弃） -->
<!-- <groupId>mysql</groupId> -->
<!-- <artifactId>mysql-connector-java</artifactId> -->
```

## 开发工具配置

### PowerShell环境
- **Shell**: PowerShell 7.x
- **执行路径**: `C:\Program Files\PowerShell\7\pwsh.exe`
- **编码**: UTF-8

### IDE建议配置
```properties
# 推荐的IDE设置
file.encoding=UTF-8
project.build.sourceEncoding=UTF-8
maven.compiler.source=1.8
maven.compiler.target=1.8
```

## 验证命令

### 环境验证脚本
```bash
# 验证Java环境
echo "JAVA_HOME: $env:JAVA_HOME"
java -version
javac -version

# 验证Maven环境  
echo "MAVEN_HOME: $env:MAVEN_HOME"
mvn -version

# 验证项目编译
mvn clean compile
```

### 预期输出
```bash
# Java版本输出
java version "1.8.0_333"
Java(TM) SE Runtime Environment (build 1.8.0_333-b02)
Java HotSpot(TM) 64-Bit Server VM (build 25.333-b02, mixed mode)

# javac版本输出
javac 1.8.0_333

# Maven版本输出
Apache Maven 3.8.9 (e26b057cc3a17459358ef53e4d0e2e381bf08a1c)
Maven home: C:\Program Files\Java\apache-maven-3.8.9
Java version: 1.8.0_333, vendor: Oracle Corporation, runtime: C:\Program Files\Java\jdk1.8.0_333\jre
```

## 常见问题和解决方案

### Q1: 编译时提示"类文件具有错误的版本"
**原因**: 依赖包使用了高于Java 8的版本编译
**解决**: 降级到Java 8兼容的版本，参考上述"关键依赖版本限制"

### Q2: Maven提示"No compiler is provided"
**原因**: JAVA_HOME指向JRE而非JDK
**解决**: 确保JAVA_HOME指向JDK路径：`C:\Program Files\Java\jdk1.8.0_333`

### Q3: 字符编码问题
**原因**: Windows默认使用GBK编码
**解决**: 在Maven配置中强制使用UTF-8编码

## 更新历史

| 日期 | 版本 | 更新内容 | 更新人 |
|------|------|----------|--------|
| 2025-01-08 | 1.0 | 初始创建，记录Java 8 + Maven 3.8.9环境 | AI Assistant |

## 注意事项

1. **版本锁定**: 本项目严格限制使用Java 8兼容的依赖版本
2. **环境一致性**: 开发、测试、生产环境应使用相同的Java版本
3. **依赖升级**: 升级任何依赖前，必须验证Java 8兼容性
4. **文档同步**: 环境变更时应及时更新本文档

---

*本文档记录了系统的具体环境配置，请在进行依赖版本选择时参考此文档，确保兼容性。*
