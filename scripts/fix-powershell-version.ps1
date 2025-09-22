# PowerShell版本修复脚本
# 解决PowerShell 7.2.24版本过旧导致的Cursor扩展启动失败问题

Write-Host "🔧 PowerShell版本修复脚本" -ForegroundColor Green
Write-Host "当前问题: PowerShell 7.2.24已到达生命周期结束，导致Cursor扩展无法正常启动" -ForegroundColor Yellow

# 检查当前PowerShell版本
Write-Host "`n📊 当前PowerShell版本信息:" -ForegroundColor Cyan
try {
    $currentVersion = pwsh --version
    Write-Host "   版本: $currentVersion" -ForegroundColor White
    
    if ($currentVersion -like "*7.2.*") {
        Write-Host "   ⚠️ 警告: 当前版本已不再受支持，需要更新到7.4.x或更高版本" -ForegroundColor Red
    } else {
        Write-Host "   ✅ 版本正常" -ForegroundColor Green
    }
} catch {
    Write-Host "   ❌ 无法获取PowerShell版本信息" -ForegroundColor Red
}

# 检查PowerShell安装位置
Write-Host "`n📁 PowerShell安装位置:" -ForegroundColor Cyan
$pwshPath = (Get-Command pwsh).Source
Write-Host "   路径: $pwshPath" -ForegroundColor White

# 提供解决方案
Write-Host "`n🛠️ 解决方案:" -ForegroundColor Cyan
Write-Host "1. 手动下载并安装最新版本PowerShell:" -ForegroundColor White
Write-Host "   - 访问: https://github.com/PowerShell/PowerShell/releases" -ForegroundColor Gray
Write-Host "   - 下载: PowerShell-7.4.12-win-x64.msi" -ForegroundColor Gray
Write-Host "   - 运行安装程序" -ForegroundColor Gray

Write-Host "`n2. 使用Winget安装（如果可用）:" -ForegroundColor White
Write-Host "   winget install Microsoft.PowerShell" -ForegroundColor Gray

Write-Host "`n3. 使用Chocolatey安装（如果已安装）:" -ForegroundColor White
Write-Host "   choco upgrade powershell-core" -ForegroundColor Gray

Write-Host "`n4. 临时解决方案 - 配置Cursor使用Windows PowerShell:" -ForegroundColor White
Write-Host "   - 在Cursor设置中搜索 'terminal.integrated.defaultProfile.windows'" -ForegroundColor Gray
Write-Host "   - 设置为 'Windows PowerShell' 而不是 'PowerShell'" -ForegroundColor Gray

# 检查是否有Windows PowerShell
Write-Host "`n🔍 检查Windows PowerShell可用性:" -ForegroundColor Cyan
try {
    $windowsPS = Get-Command powershell -ErrorAction Stop
    Write-Host "   ✅ Windows PowerShell可用: $($windowsPS.Source)" -ForegroundColor Green
    Write-Host "   💡 可以临时使用Windows PowerShell作为替代" -ForegroundColor Yellow
} catch {
    Write-Host "   ❌ Windows PowerShell不可用" -ForegroundColor Red
}

# 提供Cursor配置建议
Write-Host "`n⚙️ Cursor配置建议:" -ForegroundColor Cyan
Write-Host "1. 打开Cursor设置 (Ctrl+,)" -ForegroundColor White
Write-Host "2. 搜索 'powershell'" -ForegroundColor White
Write-Host "3. 找到 'PowerShell: Use Windows PowerShell'" -ForegroundColor White
Write-Host "4. 勾选此选项以使用Windows PowerShell" -ForegroundColor White
Write-Host "5. 或者设置 'Terminal › Integrated › Default Profile: Windows' 为 'Windows PowerShell'" -ForegroundColor White

Write-Host "`n📋 验证步骤:" -ForegroundColor Cyan
Write-Host "1. 更新PowerShell后，重启Cursor" -ForegroundColor White
Write-Host "2. 打开新的PowerShell终端" -ForegroundColor White
Write-Host "3. 检查是否还有错误提示" -ForegroundColor White
Write-Host "4. 运行项目脚本测试功能" -ForegroundColor White

Write-Host "`n✅ 修复脚本执行完成！" -ForegroundColor Green
Write-Host "💡 建议优先使用方案1手动安装最新版本PowerShell" -ForegroundColor Yellow

