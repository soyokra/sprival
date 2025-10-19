# 系统环境配置信息 (Windows)

## 概述

本文档记录了Sprival项目在Windows开发环境下的配置信息，确保所有依赖和工具版本与系统环境兼容。

## 当前系统实际环境

> **检测时间**: 2025-10-15  
> **说明**: 以下是当前开发环境的实际配置信息

### 操作系统信息
- **系统**: Microsoft Windows 11 家庭中文版
- **版本**: 10.0.26100 Build 26100
- **架构**: x64-based PC (64位)
- **处理器**: Intel(R) Core(TM) i7-9750H CPU @ 2.60GHz
- **制造商**: Dell Inc.

### Java环境实际配置
- **Java运行时版本**: 1.8.0_341
  ```
  Java(TM) SE Runtime Environment (build 1.8.0_341-b10)
  Java HotSpot(TM) 64-Bit Server VM (build 25.341-b10, mixed mode)
  ```
- **Java编译器版本**: javac 1.8.0_333
- **JAVA_HOME**: `C:\Program Files\Java\jdk1.8.0_333`
- **供应商**: Oracle Corporation

### Maven环境实际配置
- **Maven版本**: Apache Maven 3.8.9
- **Maven Home**: `C:\Program Files\Java\apache-maven-3.8.9`
- **使用的Java**: 1.8.0_333
- **默认编码**: GBK ⚠️ (需要配置为UTF-8)
- **本地仓库**: `C:\Users\{用户名}\.m2\repository`

### Shell环境实际配置
- **PowerShell版本**: 7.5.3
- **PowerShell路径**: `C:\Program Files\PowerShell\7\pwsh.exe`
- **系统编码**: UTF-8 (PowerShell 7默认)
- **Maven平台编码**: GBK ⚠️ (需要特别注意)

### 关键问题提示
1. ⚠️ **编码问题**: Maven默认使用GBK编码，可能导致中文乱码，需要配置UTF-8
2. ✅ **Java版本**: 符合项目要求（Java 8）
3. ✅ **Maven版本**: 符合项目要求（3.8.9）
4. ✅ **PowerShell**: 使用PowerShell 7，UTF-8支持良好

---

## 系统基本信息

### 操作系统
- **系统**: Windows 10/11 (Build 26100+)
- **版本**: Windows 10 Pro/Enterprise 或 Windows 11
- **架构**: x86_64 (64位)
- **编码**: UTF-8 (推荐配置)
- **时区**: 根据系统设置自动识别
- **主机名**: 根据系统配置

### 硬件架构
- **处理器架构**: x86_64 / AMD64
- **系统类型**: 64位操作系统
- **虚拟化**: Hyper-V / VMware / 物理机

## Java开发环境

### JDK配置
- **JDK版本**: 1.8.0_xxx (推荐使用 JDK 8u202 或更高版本)
- **推荐发行版**: 
  - Oracle JDK 8 (需要许可证)
  - OpenJDK 8 (推荐使用 Adoptium/Temurin)
  - Amazon Corretto 8
- **JDK安装路径示例**: `C:\Program Files\Java\jdk1.8.0_xxx`
- **JRE路径示例**: `C:\Program Files\Java\jdk1.8.0_xxx\jre`
- **虚拟机**: HotSpot 64-Bit Server VM

### 环境变量配置
```powershell
# 系统环境变量设置（通过"系统属性"-"高级"-"环境变量"配置）

# JAVA_HOME 变量
JAVA_HOME = C:\Program Files\Java\jdk1.8.0_xxx

# MAVEN_HOME 变量（如果单独安装Maven）
MAVEN_HOME = C:\Program Files\Apache\maven

# PATH 变量（追加以下路径）
%JAVA_HOME%\bin
%MAVEN_HOME%\bin
```

### 环境变量配置步骤
1. 右键点击"此电脑"→"属性"
2. 点击"高级系统设置"→"环境变量"
3. 在"系统变量"中新建 `JAVA_HOME`，值为JDK安装路径
4. 在"系统变量"中找到 `Path`，添加 `%JAVA_HOME%\bin`
5. 点击"确定"保存，重启命令行窗口

### Java工具路径
- **java**: `%JAVA_HOME%\bin\java.exe`
- **javac**: `%JAVA_HOME%\bin\javac.exe`
- **jar**: `%JAVA_HOME%\bin\jar.exe`

## Maven构建环境

### Maven配置
- **Maven版本**: Apache Maven 3.8.7 或 3.8.8 (推荐)
- **Maven安装方式**: 
  - 方式1: 单独下载安装（推荐）
  - 方式2: 使用IDE内置Maven
- **Maven安装路径示例**: `C:\Program Files\Apache\maven`
- **使用的Java**: 1.8.0_xxx (JDK)
- **默认编码**: UTF-8 (需要配置)
- **平台**: Windows x86_64

### Maven仓库
- **本地仓库**: 
  - 默认: `C:\Users\{用户名}\.m2\repository`
  - 推荐自定义: `D:\.m2\repository` (避免C盘空间不足)
- **中央仓库**: https://repo.maven.apache.org/maven2
- **国内镜像**: https://maven.aliyun.com/repository/public (推荐)

### Maven配置文件 (settings.xml)
```xml
<!-- 位置: C:\Users\{用户名}\.m2\settings.xml -->
<settings>
  <!-- 自定义本地仓库路径 -->
  <localRepository>D:\.m2\repository</localRepository>
  
  <!-- 配置阿里云镜像加速 -->
  <mirrors>
    <mirror>
      <id>aliyun-maven</id>
      <mirrorOf>central</mirrorOf>
      <name>阿里云公共仓库</name>
      <url>https://maven.aliyun.com/repository/public</url>
    </mirror>
  </mirrors>
  
  <!-- 配置编码 -->
  <profiles>
    <profile>
      <id>default</id>
      <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
      </properties>
    </profile>
  </profiles>
</settings>
```

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

### Shell环境
- **PowerShell**: 
  - PowerShell 7.x (推荐) - `C:\Program Files\PowerShell\7\pwsh.exe`
  - Windows PowerShell 5.1 (系统自带) - `C:\Windows\System32\WindowsPowerShell\v1.0\powershell.exe`
- **命令提示符**: `cmd.exe`
- **编码**: 
  - PowerShell 7: 默认 UTF-8
  - PowerShell 5.1: 需要配置 `[Console]::OutputEncoding = [System.Text.Encoding]::UTF8`
  - CMD: 需要执行 `chcp 65001` 切换到 UTF-8

### 编码配置（重要）
```powershell
# PowerShell 配置文件
# 位置: $PROFILE (通常是 C:\Users\{用户名}\Documents\PowerShell\Microsoft.PowerShell_profile.ps1)

# 设置控制台编码为UTF-8
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$PSDefaultParameterValues['*:Encoding'] = 'utf8'

# 设置环境变量
$env:JAVA_TOOL_OPTIONS = "-Dfile.encoding=UTF-8"
```

### Docker环境
- **Docker Desktop for Windows**: 4.x 或更高版本
- **Docker Compose**: 包含在 Docker Desktop 中
- **WSL 2**: 推荐启用（性能更好）
- **状态**: 用于运行中间件服务

### IDE建议配置

#### IntelliJ IDEA
```properties
# File > Settings > Editor > File Encodings
file.encoding=UTF-8
IDE Encoding=UTF-8
Project Encoding=UTF-8

# File > Settings > Build, Execution, Deployment > Build Tools > Maven
Maven home directory: C:\Program Files\Apache\maven
User settings file: C:\Users\{用户名}\.m2\settings.xml

# File > Settings > Build, Execution, Deployment > Compiler > Java Compiler
Project bytecode version: 8
Per-module bytecode version: 8
```

#### VS Code
```json
// settings.json
{
  "files.encoding": "utf8",
  "java.configuration.runtimes": [
    {
      "name": "JavaSE-1.8",
      "path": "C:\\Program Files\\Java\\jdk1.8.0_xxx"
    }
  ],
  "maven.executable.path": "C:\\Program Files\\Apache\\maven\\bin\\mvn.cmd"
}
```

## 验证命令

### 自动环境检测脚本（推荐）

项目提供了自动化环境检测脚本，可以一键检测所有配置：

```powershell
# 运行环境检测脚本
.\scripts\check-windows-environment.ps1
```

该脚本会自动检测：
- ✅ 操作系统信息
- ✅ PowerShell版本和编码
- ✅ Java环境（版本、JAVA_HOME、javac）
- ✅ Maven环境（版本、编码配置）
- ✅ Docker环境
- ✅ 项目兼容性
- ✅ 自动生成检测报告到 `logs/` 目录

### 环境验证脚本 (PowerShell)
```powershell
# 验证Java环境
Write-Host "JAVA_HOME: $env:JAVA_HOME"
java -version
javac -version

# 验证Maven环境  
Write-Host "MAVEN_HOME: $env:MAVEN_HOME"
mvn -version

# 验证项目编译
mvn clean compile
```

### 环境验证脚本 (CMD)
```batch
@echo off
echo JAVA_HOME: %JAVA_HOME%
java -version
javac -version

echo MAVEN_HOME: %MAVEN_HOME%
mvn -version

mvn clean compile
```

### 预期输出

#### 当前系统实际输出
```powershell
# Java版本输出
java version "1.8.0_341"
Java(TM) SE Runtime Environment (build 1.8.0_341-b10)
Java HotSpot(TM) 64-Bit Server VM (build 25.341-b10, mixed mode)

# javac版本输出
javac 1.8.0_333

# Maven版本输出
Apache Maven 3.8.9 (e26b057cc3a17459358ef53e4d0e2e381bf08a1c)
Maven home: C:\Program Files\Java\apache-maven-3.8.9
Java version: 1.8.0_333, vendor: Oracle Corporation, runtime: C:\Program Files\Java\jdk1.8.0_333\jre
Default locale: zh_CN, platform encoding: GBK
OS name: "windows 11", version: "10.0", arch: "amd64", family: "windows"
```

#### 标准输出格式（参考）
```powershell
# Java版本输出
java version "1.8.0_xxx"
Java(TM) SE Runtime Environment (build 1.8.0_xxx-bxx)
Java HotSpot(TM) 64-Bit Server VM (build 25.xxx-bxx, mixed mode)

# javac版本输出
javac 1.8.0_xxx

# Maven版本输出
Apache Maven 3.8.7 (或 3.8.8/3.8.9)
Maven home: C:\Program Files\Apache\maven
Java version: 1.8.0_xxx, vendor: Oracle Corporation, runtime: C:\Program Files\Java\jdk1.8.0_xxx\jre
Default locale: zh_CN, platform encoding: UTF-8
OS name: "windows 10/11", version: "10.0", arch: "amd64", family: "windows"
```

> ⚠️ **注意**: 如果Maven输出显示 `platform encoding: GBK`，说明编码配置不正确，需要参考下方"Q3: 中文乱码问题"进行配置。

## 常见问题和解决方案

### Q1: 编译时提示"类文件具有错误的版本"
**原因**: 依赖包使用了高于Java 8的版本编译  
**解决**: 降级到Java 8兼容的版本，参考上述"关键依赖版本限制"

### Q2: Maven提示"JAVA_HOME not found"
**原因**: JAVA_HOME环境变量未正确配置  
**解决**: 
1. 检查JAVA_HOME是否指向JDK路径（不是JRE）
2. 重启命令行窗口使环境变量生效
3. 使用管理员权限设置系统环境变量

### Q3: 中文乱码问题（当前系统存在此问题 ⚠️）
**原因**: Windows默认使用GBK编码，与项目UTF-8不一致。当前系统Maven检测到使用GBK编码  

**解决方案**:

**方法1: 设置系统环境变量（推荐，永久生效）**
```powershell
# 1. 打开系统环境变量设置
# 右键"此电脑" → "属性" → "高级系统设置" → "环境变量"

# 2. 在"系统变量"中新建以下变量：
变量名: JAVA_TOOL_OPTIONS
变量值: -Dfile.encoding=UTF-8

# 3. 在"系统变量"中新建：
变量名: MAVEN_OPTS
变量值: -Dfile.encoding=UTF-8

# 4. 重启PowerShell验证
mvn -version
# 应该显示: platform encoding: UTF-8
```

**方法2: 配置PowerShell配置文件（临时方案）**
```powershell
# 编辑PowerShell配置文件
notepad $PROFILE

# 添加以下内容：
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$env:JAVA_TOOL_OPTIONS = "-Dfile.encoding=UTF-8"
$env:MAVEN_OPTS = "-Dfile.encoding=UTF-8"

# 重新加载配置
. $PROFILE
```

**方法3: 使用Maven的settings.xml配置**
```xml
<!-- C:\Users\{用户名}\.m2\settings.xml -->
<settings>
  <profiles>
    <profile>
      <id>default</id>
      <activation>
        <activeByDefault>true</activeByDefault>
      </activation>
      <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <maven.compiler.encoding>UTF-8</maven.compiler.encoding>
      </properties>
    </profile>
  </profiles>
</settings>
```

**验证编码配置**:
```powershell
# 验证Java工具选项
echo $env:JAVA_TOOL_OPTIONS

# 验证Maven输出（应该显示UTF-8）
mvn -version
```

参考 [编码规范](ENCODING-STANDARDS.md) 进行全面配置

### Q4: PowerShell脚本无法执行
**原因**: PowerShell执行策略限制  
**解决**: 
```powershell
# 以管理员身份运行PowerShell，执行：
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

### Q5: Maven下载依赖慢
**原因**: 访问国外中央仓库速度慢  
**解决**: 配置阿里云镜像，参考上述"Maven配置文件"

### Q6: 路径中包含空格导致问题
**原因**: Windows路径常包含空格（如"Program Files"）  
**解决**: 
- 使用双引号包裹路径：`"C:\Program Files\Java\jdk1.8.0_xxx"`
- 或安装到无空格路径：`C:\Java\jdk1.8.0_xxx`

## 快速启动脚本

### PowerShell版本
项目提供了Windows快速启动脚本：
```powershell
# 使用项目提供的启动脚本
.\scripts\ai-dev-start.ps1

# 或手动执行
mvn clean compile
mvn spring-boot:run
```

## 更新历史

| 日期 | 版本 | 更新内容 | 更新人 |
|------|------|----------|--------|
| 2025-10-15 | 1.0 | 创建Windows环境配置文档 | AI Assistant |
| 2025-10-15 | 2.0 | 添加当前系统实际环境检测信息，更新实际输出示例 | AI Assistant |

## 注意事项

1. **版本锁定**: 本项目严格限制使用Java 8兼容的依赖版本
2. **环境一致性**: 开发、测试、生产环境应使用相同的Java版本
3. **依赖升级**: 升级任何依赖前，必须验证Java 8兼容性
4. **文档同步**: 环境变更时应及时更新本文档
5. **编码统一**: Windows环境特别注意UTF-8编码配置，避免中文乱码
6. **路径规范**: Windows路径使用反斜杠`\`，注意转义和空格处理
7. **权限问题**: 某些操作需要管理员权限，建议以管理员身份运行命令行

## 推荐工具

### 终端工具
- **Windows Terminal**: 微软官方现代化终端，支持多标签页
- **PowerShell 7**: 跨平台PowerShell，UTF-8默认支持更好

### 包管理工具
- **Chocolatey**: Windows包管理器，可快速安装JDK、Maven等
  ```powershell
  # 使用Chocolatey安装Java和Maven
  choco install openjdk8
  choco install maven
  ```
- **Scoop**: 另一个轻量级包管理器
  ```powershell
  scoop install openjdk8
  scoop install maven
  ```

---

*本文档记录了Windows系统的具体环境配置，请在进行依赖版本选择时参考此文档，确保兼容性。*

