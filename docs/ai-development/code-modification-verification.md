# 代码修改验证规范

## 概述

本文档定义了Sprival项目中代码修改后的验证规范，确保每次代码修改都能及时发现和解决问题，避免将错误代码提交到版本控制系统。

## 🎯 验证原则

### 1. 及时验证原则
- **每次修改后立即验证**: 不要积累多个修改后再验证
- **小步快跑**: 每次只做小的修改，然后立即验证
- **快速反馈**: 通过重启应用快速获得反馈

### 2. 全面验证原则
- **启动验证**: 确保应用能够正常启动
- **功能验证**: 验证修改的功能是否正常工作
- **健康验证**: 检查各组件健康状态
- **性能验证**: 确保修改不影响应用性能

### 3. 问题追踪原则
- **记录问题**: 详细记录发现的问题和解决方案
- **问题分类**: 将问题按类型分类，便于后续处理
- **经验积累**: 将常见问题和解决方案整理成文档

## 📋 验证检查清单

### 代码修改后必须执行的验证步骤

#### 1. 编译验证
```powershell
# 清理并重新编译
mvn clean compile

# 检查编译结果
echo "编译状态: $LASTEXITCODE"
```

**检查点**:
- [ ] 编译无错误
- [ ] 编译无警告（或警告已确认可接受）
- [ ] 所有依赖正确解析

#### 2. 应用启动验证
```powershell
# 启动应用
mvn spring-boot:run

# 等待启动完成（约30-60秒）
Start-Sleep -Seconds 30

# 检查应用是否成功启动
netstat -an | findstr 8338
```

**检查点**:
- [ ] 应用成功启动
- [ ] 端口8338正常监听
- [ ] 启动日志无错误
- [ ] 启动时间在合理范围内（<60秒）

#### 3. 健康检查验证
```powershell
# 检查应用健康状态
curl http://localhost:8338/api/actuator/health

# 检查详细健康信息
curl http://localhost:8338/api/actuator/health/db
curl http://localhost:8338/api/actuator/health/redis
curl http://localhost:8338/api/actuator/health/mongo
```

**检查点**:
- [ ] 整体健康状态为UP
- [ ] 数据库连接正常
- [ ] Redis连接正常
- [ ] MongoDB连接正常
- [ ] 其他组件状态正常

#### 4. 监控指标验证
```powershell
# 检查监控指标
curl http://localhost:8338/api/actuator/prometheus

# 检查应用信息
curl http://localhost:8338/api/actuator/info
```

**检查点**:
- [ ] 监控指标正常输出
- [ ] 应用信息正确显示
- [ ] 关键指标在正常范围内

#### 5. 功能验证
```powershell
# 测试相关功能端点
curl http://localhost:8338/api/actuator
curl http://localhost:8338/api/actuator/metrics
```

**检查点**:
- [ ] 修改的功能正常工作
- [ ] 相关API端点响应正常
- [ ] 业务逻辑执行正确

## 🔧 自动化验证脚本

### 创建验证脚本
```powershell
# 创建验证脚本
New-Item -Path "scripts\verify-code-changes.ps1" -ItemType File -Force
```

### 验证脚本内容
```powershell
# scripts/verify-code-changes.ps1
param(
    [switch]$SkipCompile = $false,
    [switch]$SkipStartup = $false,
    [switch]$SkipHealth = $false
)

Write-Host "🔍 开始代码修改验证..." -ForegroundColor Green

# 1. 编译验证
if (-not $SkipCompile) {
    Write-Host "📦 执行编译验证..." -ForegroundColor Yellow
    mvn clean compile
    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ 编译失败，请检查代码错误" -ForegroundColor Red
        exit 1
    }
    Write-Host "✅ 编译验证通过" -ForegroundColor Green
}

# 2. 启动验证
if (-not $SkipStartup) {
    Write-Host "🚀 执行启动验证..." -ForegroundColor Yellow
    Start-Process -FilePath "mvn" -ArgumentList "spring-boot:run" -WindowStyle Hidden
    Start-Sleep -Seconds 30
    
    $portCheck = netstat -an | findstr 8338
    if (-not $portCheck) {
        Write-Host "❌ 应用启动失败，端口8338未监听" -ForegroundColor Red
        exit 1
    }
    Write-Host "✅ 启动验证通过" -ForegroundColor Green
}

# 3. 健康检查验证
if (-not $SkipHealth) {
    Write-Host "💚 执行健康检查验证..." -ForegroundColor Yellow
    $healthResponse = curl -s http://localhost:8338/api/actuator/health
    if ($healthResponse -notmatch '"status":"UP"') {
        Write-Host "❌ 健康检查失败: $healthResponse" -ForegroundColor Red
        exit 1
    }
    Write-Host "✅ 健康检查验证通过" -ForegroundColor Green
}

Write-Host "🎉 所有验证通过！代码修改安全" -ForegroundColor Green
```

## 📊 验证结果记录

### 验证记录模板
```markdown
## 代码修改验证记录

**修改时间**: 2025-09-09 10:20:00
**修改内容**: [描述修改的具体内容]
**修改文件**: [列出修改的文件列表]

### 验证结果

#### 编译验证
- [ ] 通过
- [ ] 失败
- **错误信息**: [如有错误，记录具体信息]

#### 启动验证
- [ ] 通过
- [ ] 失败
- **启动时间**: [记录启动时间]
- **错误信息**: [如有错误，记录具体信息]

#### 健康检查验证
- [ ] 通过
- [ ] 失败
- **健康状态**: [记录健康检查结果]
- **错误信息**: [如有错误，记录具体信息]

#### 功能验证
- [ ] 通过
- [ ] 失败
- **测试结果**: [记录功能测试结果]
- **错误信息**: [如有错误，记录具体信息]

### 问题记录
[记录发现的问题和解决方案]

### 验证结论
- [ ] 验证通过，可以提交代码
- [ ] 验证失败，需要修复问题
```

## 🚨 常见问题及解决方案

### 1. 编译失败
**常见原因**:
- 语法错误
- 依赖缺失
- 版本冲突

**解决方案**:
```powershell
# 检查编译错误
mvn clean compile -X

# 检查依赖
mvn dependency:tree

# 解决版本冲突
mvn dependency:resolve
```

### 2. 启动失败
**常见原因**:
- 配置错误
- 端口占用
- 依赖服务不可用

**解决方案**:
```powershell
# 检查端口占用
netstat -an | findstr 8338

# 检查配置
mvn spring-boot:run -Dspring-boot.run.arguments="--debug"

# 检查依赖服务
# MySQL: netstat -an | findstr 3306
# Redis: netstat -an | findstr 6379
# MongoDB: netstat -an | findstr 27017
```

### 3. 健康检查失败
**常见原因**:
- 数据库连接失败
- Redis连接失败
- 外部服务不可用

**解决方案**:
```powershell
# 检查详细健康信息
curl http://localhost:8338/api/actuator/health/db
curl http://localhost:8338/api/actuator/health/redis

# 检查服务状态
# 启动MySQL、Redis、MongoDB等服务
```

## 📈 验证效率优化

### 1. 并行验证
- 编译和启动可以并行进行
- 健康检查和功能验证可以并行进行

### 2. 增量验证
- 只验证修改相关的组件
- 使用缓存减少重复验证

### 3. 自动化集成
- 集成到IDE中，修改后自动验证
- 集成到CI/CD流程中

## 🎯 最佳实践

### 1. 开发习惯
- 每次修改后立即验证
- 保持验证记录
- 及时解决问题

### 2. 团队协作
- 统一验证标准
- 共享验证脚本
- 定期回顾验证流程

### 3. 持续改进
- 根据项目发展调整验证标准
- 优化验证脚本
- 积累问题解决经验

---

*此规范将根据项目发展和使用反馈持续优化*
