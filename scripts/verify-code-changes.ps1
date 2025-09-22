# Sprival代码修改验证脚本
# 用于验证代码修改后应用是否正常运行

param(
    [switch]$SkipCompile = $false,
    [switch]$SkipStartup = $false,
    [switch]$SkipHealth = $false,
    [switch]$Verbose = $false
)

Write-Host "🔍 开始代码修改验证..." -ForegroundColor Green
Write-Host "📅 验证时间: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')" -ForegroundColor Cyan

$ErrorCount = 0
$StartTime = Get-Date

# 1. 编译验证
if (-not $SkipCompile) {
    Write-Host "`n📦 执行编译验证..." -ForegroundColor Yellow
    $CompileStart = Get-Date
    
    try {
        if ($Verbose) {
            mvn clean compile
        } else {
            mvn clean compile | Out-Null
        }
        
        if ($LASTEXITCODE -ne 0) {
            Write-Host "❌ 编译失败，请检查代码错误" -ForegroundColor Red
            $ErrorCount++
        } else {
            $CompileTime = (Get-Date) - $CompileStart
            Write-Host "✅ 编译验证通过 (耗时: $($CompileTime.TotalSeconds.ToString('F2'))秒)" -ForegroundColor Green
        }
    } catch {
        Write-Host "❌ 编译过程出现异常: $($_.Exception.Message)" -ForegroundColor Red
        $ErrorCount++
    }
}

# 2. 启动验证
if (-not $SkipStartup) {
    Write-Host "`n🚀 执行启动验证..." -ForegroundColor Yellow
    $StartupStart = Get-Date
    
    try {
        # 检查是否已有应用在运行
        $ExistingProcess = Get-Process -Name "java" -ErrorAction SilentlyContinue | Where-Object { $_.CommandLine -like "*sprival*" }
        if ($ExistingProcess) {
            Write-Host "⚠️ 检测到已有应用在运行，正在停止..." -ForegroundColor Yellow
            $ExistingProcess | Stop-Process -Force
            Start-Sleep -Seconds 5
        }
        
        # 启动应用
        Write-Host "🔄 启动应用..." -ForegroundColor Cyan
        $StartupJob = Start-Job -ScriptBlock { 
            Set-Location $using:PWD
            mvn spring-boot:run 2>&1
        }
        
        # 等待启动
        $MaxWaitTime = 60
        $WaitTime = 0
        $Started = $false
        
        while ($WaitTime -lt $MaxWaitTime) {
            Start-Sleep -Seconds 2
            $WaitTime += 2
            
            $PortCheck = netstat -an | findstr ":8338"
            if ($PortCheck) {
                $Started = $true
                break
            }
            
            if ($Verbose) {
                Write-Host "⏳ 等待应用启动... ($WaitTime/$MaxWaitTime秒)" -ForegroundColor Cyan
            }
        }
        
        if (-not $Started) {
            Write-Host "❌ 应用启动超时，请检查启动日志" -ForegroundColor Red
            Stop-Job $StartupJob -ErrorAction SilentlyContinue
            Remove-Job $StartupJob -ErrorAction SilentlyContinue
            $ErrorCount++
        } else {
            $StartupTime = (Get-Date) - $StartupStart
            Write-Host "✅ 启动验证通过 (耗时: $($StartupTime.TotalSeconds.ToString('F2'))秒)" -ForegroundColor Green
        }
    } catch {
        Write-Host "❌ 启动过程出现异常: $($_.Exception.Message)" -ForegroundColor Red
        $ErrorCount++
    }
}

# 3. 健康检查验证
if (-not $SkipHealth -and $Started) {
    Write-Host "`n💚 执行健康检查验证..." -ForegroundColor Yellow
    $HealthStart = Get-Date
    
    try {
        # 等待应用完全启动
        Start-Sleep -Seconds 5
        
        # 检查整体健康状态
        Write-Host "🔍 检查整体健康状态..." -ForegroundColor Cyan
        $HealthResponse = try {
            Invoke-RestMethod -Uri "http://localhost:8338/api/actuator/health" -TimeoutSec 10
        } catch {
            $null
        }
        
        if (-not $HealthResponse -or $HealthResponse.status -ne "UP") {
            Write-Host "❌ 整体健康检查失败" -ForegroundColor Red
            if ($HealthResponse) {
                Write-Host "   状态: $($HealthResponse.status)" -ForegroundColor Red
            }
            $ErrorCount++
        } else {
            Write-Host "✅ 整体健康状态: $($HealthResponse.status)" -ForegroundColor Green
        }
        
        # 检查各组件健康状态
        $Components = @("db", "redis", "mongo", "rabbit")
        foreach ($Component in $Components) {
            Write-Host "🔍 检查 $Component 组件..." -ForegroundColor Cyan
            $ComponentHealth = try {
                Invoke-RestMethod -Uri "http://localhost:8338/api/actuator/health/$Component" -TimeoutSec 5
            } catch {
                $null
            }
            
            if (-not $ComponentHealth -or $ComponentHealth.status -ne "UP") {
                Write-Host "⚠️ $Component 组件状态异常" -ForegroundColor Yellow
                if ($ComponentHealth) {
                    Write-Host "   状态: $($ComponentHealth.status)" -ForegroundColor Yellow
                }
            } else {
                Write-Host "✅ $Component 组件状态正常" -ForegroundColor Green
            }
        }
        
        $HealthTime = (Get-Date) - $HealthStart
        Write-Host "✅ 健康检查验证完成 (耗时: $($HealthTime.TotalSeconds.ToString('F2'))秒)" -ForegroundColor Green
        
    } catch {
        Write-Host "❌ 健康检查过程出现异常: $($_.Exception.Message)" -ForegroundColor Red
        $ErrorCount++
    }
}

# 4. 监控指标验证
if ($Started) {
    Write-Host "`n📊 执行监控指标验证..." -ForegroundColor Yellow
    
    try {
        # 检查监控端点
        $MetricsResponse = try {
            Invoke-RestMethod -Uri "http://localhost:8338/api/actuator" -TimeoutSec 5
        } catch {
            $null
        }
        
        if ($MetricsResponse) {
            Write-Host "✅ 监控端点正常" -ForegroundColor Green
        } else {
            Write-Host "❌ 监控端点异常" -ForegroundColor Red
            $ErrorCount++
        }
        
        # 检查Prometheus指标
        $PrometheusResponse = try {
            Invoke-WebRequest -Uri "http://localhost:8338/api/actuator/prometheus" -TimeoutSec 5
        } catch {
            $null
        }
        
        if ($PrometheusResponse -and $PrometheusResponse.StatusCode -eq 200) {
            Write-Host "✅ Prometheus指标正常" -ForegroundColor Green
        } else {
            Write-Host "❌ Prometheus指标异常" -ForegroundColor Red
            $ErrorCount++
        }
        
    } catch {
        Write-Host "❌ 监控指标检查过程出现异常: $($_.Exception.Message)" -ForegroundColor Red
        $ErrorCount++
    }
}

# 5. 清理和总结
Write-Host "`n🧹 清理资源..." -ForegroundColor Yellow

# 停止启动的作业
if ($StartupJob) {
    Stop-Job $StartupJob -ErrorAction SilentlyContinue
    Remove-Job $StartupJob -ErrorAction SilentlyContinue
}

$TotalTime = (Get-Date) - $StartTime

# 输出验证结果
Write-Host "`n📋 验证结果总结:" -ForegroundColor Cyan
Write-Host "   总耗时: $($TotalTime.TotalSeconds.ToString('F2'))秒" -ForegroundColor White
Write-Host "   错误数量: $ErrorCount" -ForegroundColor White

if ($ErrorCount -eq 0) {
    Write-Host "`n🎉 所有验证通过！代码修改安全，可以提交" -ForegroundColor Green
    exit 0
} else {
    Write-Host "`n❌ 验证失败，发现 $ErrorCount 个问题，请修复后重新验证" -ForegroundColor Red
    exit 1
}
