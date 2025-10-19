# 性能测试运行脚本 (Windows PowerShell)
# 用途：快速运行性能测试

param(
    [string]$TestClass = "OrderInsertLoadTest",
    [string]$TestMethod = "",
    [int]$ConcurrentUsers = 100,
    [int]$DurationSeconds = 60
)

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Sprival 性能测试运行脚本" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 检查 Java 环境
Write-Host "【1】检查 Java 环境..." -ForegroundColor Green
try {
    $javaVersion = java -version 2>&1
    Write-Host "✅ Java 环境正常" -ForegroundColor Green
} catch {
    Write-Host "❌ Java 环境未配置，请先安装 Java" -ForegroundColor Red
    exit 1
}

# 检查 Maven 环境
Write-Host ""
Write-Host "【2】检查 Maven 环境..." -ForegroundColor Green
try {
    $mavenVersion = mvn -version 2>&1
    Write-Host "✅ Maven 环境正常" -ForegroundColor Green
} catch {
    Write-Host "❌ Maven 环境未配置，请先安装 Maven" -ForegroundColor Red
    exit 1
}

# 清理旧的测试报告
Write-Host ""
Write-Host "【3】清理旧的测试报告..." -ForegroundColor Green
if (Test-Path "target\performance-reports") {
    $fileCount = (Get-ChildItem "target\performance-reports" -File).Count
    if ($fileCount -gt 0) {
        Write-Host "清理 $fileCount 个旧报告文件..." -ForegroundColor Yellow
        Remove-Item "target\performance-reports\*" -Force
    }
}

# 设置性能测试参数
Write-Host ""
Write-Host "【4】配置性能测试参数..." -ForegroundColor Green
Write-Host "  测试类: $TestClass" -ForegroundColor White
Write-Host "  并发用户数: $ConcurrentUsers" -ForegroundColor White
Write-Host "  持续时间: $DurationSeconds 秒" -ForegroundColor White

# 构建测试命令
$testCommand = "mvn clean test"
if ($TestMethod) {
    $testCommand += " -Dtest=$TestClass#$TestMethod"
    Write-Host "  测试方法: $TestMethod" -ForegroundColor White
} else {
    $testCommand += " -Dtest=$TestClass"
}

# 添加系统属性
$testCommand += " -Dperformance.test.concurrent-users=$ConcurrentUsers"
$testCommand += " -Dperformance.test.duration-seconds=$DurationSeconds"
$testCommand += " -Dspring.profiles.active=performance"

# 启动性能测试
Write-Host ""
Write-Host "【5】启动性能测试..." -ForegroundColor Green
Write-Host "执行命令: $testCommand" -ForegroundColor Yellow
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 执行测试
Invoke-Expression $testCommand

# 检查测试结果
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  测试完成" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ 性能测试执行成功" -ForegroundColor Green
    
    # 查看测试报告
    if (Test-Path "target\performance-reports") {
        $reports = Get-ChildItem "target\performance-reports" -File | Sort-Object LastWriteTime -Descending
        if ($reports.Count -gt 0) {
            Write-Host ""
            Write-Host "📊 生成的测试报告:" -ForegroundColor Cyan
            foreach ($report in $reports) {
                Write-Host "  - $($report.Name)" -ForegroundColor White
            }
            
            # 显示最新报告的内容
            Write-Host ""
            Write-Host "📈 最新测试报告内容:" -ForegroundColor Cyan
            Write-Host "----------------------------------------" -ForegroundColor Gray
            Get-Content $reports[0].FullName | Write-Host
            Write-Host "----------------------------------------" -ForegroundColor Gray
        }
    }
} else {
    Write-Host "❌ 性能测试执行失败" -ForegroundColor Red
    Write-Host "请查看上方日志获取详细错误信息" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "测试报告目录: target\performance-reports\" -ForegroundColor Yellow
Write-Host ""

# 使用示例
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  使用示例" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "# 运行所有下单接口压力测试" -ForegroundColor Gray
Write-Host ".\scripts\run-performance-test.ps1" -ForegroundColor White
Write-Host ""
Write-Host "# 运行固定并发测试，100并发，持续60秒" -ForegroundColor Gray
Write-Host ".\scripts\run-performance-test.ps1 -ConcurrentUsers 100 -DurationSeconds 60" -ForegroundColor White
Write-Host ""
Write-Host "# 运行特定测试方法" -ForegroundColor Gray
Write-Host ".\scripts\run-performance-test.ps1 -TestMethod testOrderInsertWithFixedConcurrency" -ForegroundColor White
Write-Host ""
Write-Host "# 运行其他测试类" -ForegroundColor Gray
Write-Host ".\scripts\run-performance-test.ps1 -TestClass YourLoadTest" -ForegroundColor White
Write-Host ""

