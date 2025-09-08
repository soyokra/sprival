# Sprival AI开发启动脚本
# 在AI编程前自动生成项目上下文

param(
    [switch]$SkipContext = $false,
    [switch]$OpenContext = $false
)

Write-Host "🚀 Sprival AI开发环境启动..." -ForegroundColor Green

# 1. 生成项目上下文（除非跳过）
if (-not $SkipContext) {
    Write-Host "📋 生成项目上下文..." -ForegroundColor Yellow
    
    # 检查脚本是否存在
    $contextScript = ".\scripts\generate-project-context.ps1"
    if (Test-Path $contextScript) {
        & $contextScript
        Write-Host "✅ 项目上下文生成完成" -ForegroundColor Green
    } else {
        Write-Host "⚠️ 上下文生成脚本不存在，跳过上下文生成" -ForegroundColor Yellow
    }
}

# 1.5. 验证项目结构
Write-Host "🔍 验证项目结构..." -ForegroundColor Yellow
$validateScript = ".\scripts\validate-project-structure.ps1"
if (Test-Path $validateScript) {
    & $validateScript
    Write-Host "✅ 项目结构验证完成" -ForegroundColor Green
} else {
    Write-Host "⚠️ 结构验证脚本不存在，跳过结构验证" -ForegroundColor Yellow
}

# 2. 显示项目状态
Write-Host "`n📊 项目状态概览:" -ForegroundColor Cyan
Write-Host "   - 项目名称: Sprival" -ForegroundColor White
Write-Host "   - 技术栈: Spring Boot 2.7.18 + Java 8" -ForegroundColor White
Write-Host "   - 已完成组件: 8个 (HTTP Server, MySQL, Redis, ClickHouse, MongoDB, RabbitMQ, Kafka, HTTP Client)" -ForegroundColor White
Write-Host "   - 编码格式: UTF-8" -ForegroundColor White
Write-Host "   - 启动端口: 8338" -ForegroundColor White

# 3. 显示上下文文件位置
Write-Host "`n📁 上下文文件位置:" -ForegroundColor Cyan
$contextDir = "docs\ai-development\context"
if (Test-Path $contextDir) {
    $contextFiles = Get-ChildItem $contextDir -Name "*-latest.*"
    foreach ($file in $contextFiles) {
        Write-Host "   - $contextDir\$file" -ForegroundColor White
    }
} else {
    Write-Host "   - 上下文目录不存在: $contextDir" -ForegroundColor Yellow
}

# 4. 显示快速命令
Write-Host "`n⚡ 快速命令:" -ForegroundColor Cyan
Write-Host "   - 启动应用: mvn spring-boot:run" -ForegroundColor White
Write-Host "   - 启动应用(脚本): .\start-utf8.bat" -ForegroundColor White
Write-Host "   - 健康检查: http://localhost:8338/api/actuator/health" -ForegroundColor White
Write-Host "   - 监控指标: http://localhost:8338/api/actuator/metrics" -ForegroundColor White
Write-Host "   - 重新生成上下文: .\scripts\generate-project-context.ps1" -ForegroundColor White

# 5. 显示AI编程指导
Write-Host "`n🤖 AI编程指导:" -ForegroundColor Cyan
Write-Host "   1. 查看项目上下文: Get-Content docs\ai-development\context\sprival-ai-context-latest.md" -ForegroundColor White
Write-Host "   2. 查看组件状态: Get-Content docs\ai-development\context\component-status-latest.md" -ForegroundColor White
Write-Host "   3. 查看AI指导: Get-Content docs\ai-development\context\ai-guidance-latest.md" -ForegroundColor White
Write-Host "   4. 使用上下文模板: docs\ai-development\project-context-template.md" -ForegroundColor White
Write-Host "   5. 查看开发规范: docs\ai-development\development-standards.md" -ForegroundColor White
Write-Host "   6. 查看项目结构: docs\PROJECT-STRUCTURE.md" -ForegroundColor White

# 6. 可选：打开上下文文件
if ($OpenContext) {
    $contextFile = "docs\ai-development\context\sprival-ai-context-latest.md"
    if (Test-Path $contextFile) {
        Write-Host "`n📖 打开上下文文件..." -ForegroundColor Yellow
        Start-Process notepad.exe $contextFile
    } else {
        Write-Host "⚠️ 上下文文件不存在: $contextFile" -ForegroundColor Yellow
    }
}

# 7. 显示下一步建议
Write-Host "`n🎯 下一步建议:" -ForegroundColor Cyan
Write-Host "   1. 查看项目上下文了解现状" -ForegroundColor White
Write-Host "   2. 查看项目结构规范" -ForegroundColor White
Write-Host "   3. 根据需求选择合适的组件" -ForegroundColor White
Write-Host "   4. 使用标准模板与AI交互" -ForegroundColor White
Write-Host "   5. 遵循项目开发规范" -ForegroundColor White
Write-Host "   6. 运行结构验证确保规范性" -ForegroundColor White

Write-Host "`n✅ AI开发环境准备完成！" -ForegroundColor Green
Write-Host "💡 提示: 使用 -OpenContext 参数可以自动打开上下文文件" -ForegroundColor Yellow
