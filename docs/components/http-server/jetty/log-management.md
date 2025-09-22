# Jetty访问日志管理

## 概述

本文档描述了Sprival项目中Jetty访问日志的配置和管理策略，确保日志文件得到有效管理，避免磁盘空间浪费。

## 📋 当前配置

### 日志配置参数
```properties
# Jetty访问日志配置
server.jetty.accesslog.enabled=true
server.jetty.accesslog.filename=logs/jetty-access.log
server.jetty.accesslog.format=EXTENDED_NCSA
server.jetty.accesslog.retain-days=7
server.jetty.accesslog.ignore-paths=/actuator/health,/actuator/prometheus,/favicon.ico
server.jetty.accesslog.append=true
server.jetty.accesslog.extended-format=true
server.jetty.accesslog.log-cookies=false
server.jetty.accesslog.log-latency=true
server.jetty.accesslog.log-server=true
```

### 配置说明

| 参数 | 值 | 说明 |
|------|-----|------|
| `enabled` | `true` | 启用访问日志 |
| `filename` | `logs/jetty-access.log` | 日志文件路径（相对于项目根目录） |
| `format` | `EXTENDED_NCSA` | 使用扩展的NCSA格式 |
| `retain-days` | `7` | 保留7天的日志文件 |
| `ignore-paths` | `/actuator/health,/actuator/prometheus,/favicon.ico` | 忽略健康检查和监控端点的访问日志 |
| `append` | `true` | 追加模式，不覆盖现有日志 |
| `extended-format` | `true` | 使用扩展格式，包含更多信息 |
| `log-cookies` | `false` | 不记录Cookie信息 |
| `log-latency` | `true` | 记录请求延迟 |
| `log-server` | `true` | 记录服务器信息 |

## 🗂️ 日志文件结构

### 目录结构
```
sprival/
├── logs/                          # 日志目录
│   ├── jetty-access.log          # 当前访问日志
│   ├── jetty-access.log.xxxxx    # 历史访问日志（自动轮转）
│   └── ...
├── .gitignore                    # 忽略logs/目录
└── scripts/
    └── cleanup-logs.ps1          # 日志清理脚本
```

### 日志格式示例
```
127.0.0.1 - - [09/九月/2025:02:20:42 +0000] "GET /api/actuator/prometheus HTTP/1.1" 200 18833 "-" "Prometheus/3.5.0"
[0:0:0:0:0:0:0:1] - - [09/九月/2025:02:20:57 +0000] "GET /api/actuator/health HTTP/1.1" 200 15 "-" "curl/8.14.1"
```

## 🧹 日志管理工具

### 1. 日志清理脚本
**文件**: `scripts/cleanup-logs.ps1`

**功能**:
- 自动清理超过指定天数的日志文件
- 显示清理统计信息
- 支持详细输出模式

**使用方法**:
```powershell
# 清理7天前的日志（默认）
.\scripts\cleanup-logs.ps1

# 清理3天前的日志
.\scripts\cleanup-logs.ps1 -RetainDays 3

# 详细输出
.\scripts\cleanup-logs.ps1 -Verbose

# 强制清理（谨慎使用）
.\scripts\cleanup-logs.ps1 -Force
```

### 2. 日志监控
**监控指标**:
- 日志文件大小
- 日志文件数量
- 磁盘使用情况

**监控命令**:
```powershell
# 查看日志目录状态
Get-ChildItem logs/ -Recurse | Measure-Object -Property Length -Sum

# 查看最新日志
Get-Content logs/jetty-access.log -Tail 10

# 查看日志文件统计
.\scripts\cleanup-logs.ps1 -Verbose
```

## ⚙️ 配置优化

### 1. 性能优化
- **忽略监控端点**: 减少不必要的日志记录
- **使用追加模式**: 避免文件锁竞争
- **合理设置保留天数**: 平衡存储空间和调试需求

### 2. 存储优化
- **集中管理**: 所有日志文件放在logs/目录
- **自动清理**: 定期清理旧日志文件
- **版本控制**: 通过.gitignore忽略日志文件

### 3. 安全考虑
- **不记录敏感信息**: 禁用Cookie记录
- **路径过滤**: 忽略健康检查等内部请求
- **访问控制**: 确保日志目录权限正确

## 🔧 故障排除

### 常见问题

#### 1. 日志文件过多
**症状**: 磁盘空间不足，大量日志文件
**解决**:
```powershell
# 立即清理旧日志
.\scripts\cleanup-logs.ps1 -RetainDays 1

# 检查磁盘使用
Get-ChildItem logs/ -Recurse | Measure-Object -Property Length -Sum
```

#### 2. 日志文件被占用
**症状**: 无法删除或移动日志文件
**解决**:
```powershell
# 停止应用
Get-Process -Name "java" | Stop-Process -Force

# 清理日志
.\scripts\cleanup-logs.ps1
```

#### 3. 日志格式异常
**症状**: 日志内容格式不正确
**解决**:
- 检查application.properties中的format配置
- 确认Jetty版本兼容性
- 重启应用使配置生效

## 📊 监控和告警

### 日志监控指标
- **文件大小**: 单个日志文件不应超过100MB
- **文件数量**: 日志文件总数不应超过50个
- **磁盘使用**: logs/目录不应超过1GB

### 告警阈值
- 日志文件超过100MB
- 日志文件数量超过50个
- 磁盘使用率超过80%

## 🎯 最佳实践

### 1. 开发环境
- 保留3-7天的日志
- 忽略监控端点访问
- 定期清理旧日志

### 2. 生产环境
- 保留7-30天的日志
- 启用日志轮转
- 配置日志监控告警

### 3. 维护建议
- 每周运行日志清理脚本
- 监控磁盘使用情况
- 定期检查日志格式和内容

## 📚 相关文档

- **Jetty配置**: `src/main/resources/application.properties`
- **日志清理脚本**: `scripts/cleanup-logs.ps1`
- **项目结构**: `docs/PROJECT-STRUCTURE.md`
- **开发规范**: `docs/ai-development/development-standards.md`

---

*此配置将根据项目发展和使用反馈持续优化*
