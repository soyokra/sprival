# Sprival日志清理脚本
# 用于清理旧的日志文件

param(
    [int]$RetainDays = 7,
    [switch]$Force = $false,
    [switch]$Verbose = $false
)

Write-Host "🧹 开始清理日志文件..." -ForegroundColor Green
Write-Host "📅 保留天数: $RetainDays" -ForegroundColor Cyan

$LogsDir = "logs"
$CutoffDate = (Get-Date).AddDays(-$RetainDays)

if (-not (Test-Path $LogsDir)) {
    Write-Host "⚠️ 日志目录不存在: $LogsDir" -ForegroundColor Yellow
    exit 0
}

# 获取所有日志文件
$LogFiles = Get-ChildItem -Path $LogsDir -Filter "*.log*" -File

if ($LogFiles.Count -eq 0) {
    Write-Host "✅ 没有找到需要清理的日志文件" -ForegroundColor Green
    exit 0
}

Write-Host "📊 找到 $($LogFiles.Count) 个日志文件" -ForegroundColor Cyan

$DeletedCount = 0
$KeptCount = 0
$TotalSize = 0

foreach ($LogFile in $LogFiles) {
    $FileSize = $LogFile.Length
    $TotalSize += $FileSize
    
    if ($LogFile.LastWriteTime -lt $CutoffDate) {
        if ($Verbose) {
            Write-Host "🗑️ 删除旧日志: $($LogFile.Name) (大小: $([math]::Round($FileSize/1KB, 2))KB, 修改时间: $($LogFile.LastWriteTime))" -ForegroundColor Yellow
        }
        
        try {
            Remove-Item $LogFile.FullName -Force
            $DeletedCount++
        } catch {
            Write-Host "❌ 删除失败: $($LogFile.Name) - $($_.Exception.Message)" -ForegroundColor Red
        }
    } else {
        if ($Verbose) {
            Write-Host "✅ 保留日志: $($LogFile.Name) (大小: $([math]::Round($FileSize/1KB, 2))KB, 修改时间: $($LogFile.LastWriteTime))" -ForegroundColor Green
        }
        $KeptCount++
    }
}

# 输出清理结果
Write-Host "`n📋 清理结果:" -ForegroundColor Cyan
Write-Host "   总文件数: $($LogFiles.Count)" -ForegroundColor White
Write-Host "   删除文件: $DeletedCount" -ForegroundColor White
Write-Host "   保留文件: $KeptCount" -ForegroundColor White
Write-Host "   总大小: $([math]::Round($TotalSize/1MB, 2))MB" -ForegroundColor White

if ($DeletedCount -gt 0) {
    Write-Host "`n🎉 日志清理完成！删除了 $DeletedCount 个旧日志文件" -ForegroundColor Green
} else {
    Write-Host "`n✅ 没有需要清理的旧日志文件" -ForegroundColor Green
}

# 显示当前日志目录状态
Write-Host "`n📁 当前日志目录状态:" -ForegroundColor Cyan
$CurrentLogs = Get-ChildItem -Path $LogsDir -Filter "*.log*" -File
if ($CurrentLogs.Count -gt 0) {
    foreach ($Log in $CurrentLogs) {
        $SizeKB = [math]::Round($Log.Length / 1KB, 2)
        Write-Host "   - $($Log.Name): $SizeKB KB (修改时间: $($Log.LastWriteTime))" -ForegroundColor White
    }
} else {
    Write-Host "   - 没有日志文件" -ForegroundColor White
}
