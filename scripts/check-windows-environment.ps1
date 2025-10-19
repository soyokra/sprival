# Windows 开发环境检测脚本
# 用途：检测当前Windows系统的开发环境配置
# 使用：.\scripts\check-windows-environment.ps1

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Sprival Windows 开发环境检测工具" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 检测时间
$detectionTime = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
Write-Host "检测时间: $detectionTime" -ForegroundColor Yellow
Write-Host ""

# 1. 操作系统信息
Write-Host "【1】操作系统信息" -ForegroundColor Green
Write-Host "----------------------------------------"
try {
    $osInfo = Get-ComputerInfo | Select-Object OsName, OsVersion, OsArchitecture, CsSystemType, CsProcessors
    Write-Host "系统: $($osInfo.OsName)"
    Write-Host "版本: $($osInfo.OsVersion)"
    Write-Host "架构: $($osInfo.OsArchitecture)"
    Write-Host "类型: $($osInfo.CsSystemType)"
    Write-Host "处理器: $($osInfo.CsProcessors[0].Name)"
} catch {
    Write-Host "无法获取操作系统详细信息" -ForegroundColor Red
}
Write-Host ""

# 2. PowerShell信息
Write-Host "【2】PowerShell环境" -ForegroundColor Green
Write-Host "----------------------------------------"
Write-Host "PowerShell版本: $($PSVersionTable.PSVersion)"
Write-Host "PowerShell路径: $PSHOME"
$encoding = [System.Text.Encoding]::Default.EncodingName
Write-Host "系统默认编码: $encoding"
Write-Host ""

# 3. Java环境检测
Write-Host "【3】Java开发环境" -ForegroundColor Green
Write-Host "----------------------------------------"
$javaInstalled = $false
try {
    $javaVersion = java -version 2>&1
    Write-Host "✅ Java已安装" -ForegroundColor Green
    Write-Host $javaVersion
    $javaInstalled = $true
} catch {
    Write-Host "❌ Java未安装或未配置到PATH" -ForegroundColor Red
}

if ($env:JAVA_HOME) {
    Write-Host "JAVA_HOME: $env:JAVA_HOME" -ForegroundColor Green
    if (Test-Path "$env:JAVA_HOME\bin\javac.exe") {
        Write-Host "✅ JAVA_HOME配置正确（指向JDK）" -ForegroundColor Green
    } else {
        Write-Host "⚠️ JAVA_HOME可能指向JRE而非JDK" -ForegroundColor Yellow
    }
} else {
    Write-Host "❌ JAVA_HOME环境变量未设置" -ForegroundColor Red
}

# 检测javac
try {
    $javacVersion = javac -version 2>&1
    Write-Host "javac版本: $javacVersion" -ForegroundColor Green
} catch {
    Write-Host "⚠️ javac不可用（可能未安装JDK）" -ForegroundColor Yellow
}

# 检查JAVA_TOOL_OPTIONS
if ($env:JAVA_TOOL_OPTIONS) {
    Write-Host "JAVA_TOOL_OPTIONS: $env:JAVA_TOOL_OPTIONS" -ForegroundColor Green
} else {
    Write-Host "⚠️ JAVA_TOOL_OPTIONS未设置（建议设置为 -Dfile.encoding=UTF-8）" -ForegroundColor Yellow
}
Write-Host ""

# 4. Maven环境检测
Write-Host "【4】Maven构建环境" -ForegroundColor Green
Write-Host "----------------------------------------"
try {
    $mavenVersion = mvn -version 2>&1
    Write-Host "✅ Maven已安装" -ForegroundColor Green
    Write-Host $mavenVersion
    
    # 检查编码
    if ($mavenVersion -match "platform encoding: UTF-8") {
        Write-Host "✅ Maven编码配置正确（UTF-8）" -ForegroundColor Green
    } elseif ($mavenVersion -match "platform encoding: GBK") {
        Write-Host "⚠️ Maven使用GBK编码，建议配置为UTF-8" -ForegroundColor Yellow
        Write-Host "   解决方法：设置环境变量 JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8" -ForegroundColor Yellow
    }
} catch {
    Write-Host "❌ Maven未安装或未配置到PATH" -ForegroundColor Red
}

if ($env:MAVEN_HOME) {
    Write-Host "MAVEN_HOME: $env:MAVEN_HOME" -ForegroundColor Green
} else {
    Write-Host "⚠️ MAVEN_HOME环境变量未设置（非必需，但建议设置）" -ForegroundColor Yellow
}

# 检查settings.xml
$settingsFile = "$env:USERPROFILE\.m2\settings.xml"
if (Test-Path $settingsFile) {
    Write-Host "✅ Maven配置文件存在: $settingsFile" -ForegroundColor Green
} else {
    Write-Host "⚠️ Maven配置文件不存在，建议创建并配置阿里云镜像" -ForegroundColor Yellow
}
Write-Host ""

# 5. Docker环境检测
Write-Host "【5】Docker环境" -ForegroundColor Green
Write-Host "----------------------------------------"
try {
    $dockerVersion = docker --version 2>&1
    Write-Host "✅ Docker已安装: $dockerVersion" -ForegroundColor Green
    
    $dockerComposeVersion = docker-compose --version 2>&1
    Write-Host "✅ Docker Compose已安装: $dockerComposeVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ Docker未安装" -ForegroundColor Yellow
    Write-Host "   建议安装Docker Desktop for Windows" -ForegroundColor Yellow
}
Write-Host ""

# 6. 项目兼容性检查
Write-Host "【6】项目兼容性检查" -ForegroundColor Green
Write-Host "----------------------------------------"
$compatible = $true

# 检查Java版本是否为1.8
if ($javaInstalled -and $javaVersion -match "1\.8\.0") {
    Write-Host "✅ Java版本符合要求（Java 8）" -ForegroundColor Green
} elseif ($javaInstalled) {
    Write-Host "❌ Java版本不符合要求（需要Java 8）" -ForegroundColor Red
    $compatible = $false
} else {
    Write-Host "❌ 无法检测Java版本" -ForegroundColor Red
    $compatible = $false
}

Write-Host ""

# 7. 总结和建议
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  检测总结" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

if ($compatible) {
    Write-Host "✅ 开发环境基本配置正确，可以开始开发" -ForegroundColor Green
} else {
    Write-Host "⚠️ 开发环境存在问题，请根据上述提示进行修复" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "建议操作：" -ForegroundColor Yellow
Write-Host "1. 如果Maven显示GBK编码，请设置环境变量解决中文乱码问题"
Write-Host "2. 配置Maven settings.xml使用阿里云镜像加速依赖下载"
Write-Host "3. 确保JAVA_HOME指向JDK而非JRE"
Write-Host "4. 安装Docker Desktop用于运行中间件服务"
Write-Host ""
Write-Host "详细配置说明请参考："
Write-Host "  docs/SYSTEM-ENVIRONMENT-WINDOWS.md"
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan

# 导出报告
$reportPath = "logs\environment-check-$(Get-Date -Format 'yyyyMMdd-HHmmss').txt"
if (!(Test-Path "logs")) {
    New-Item -ItemType Directory -Path "logs" -Force | Out-Null
}

$reportContent = @"
Sprival Windows 开发环境检测报告
========================================
检测时间: $detectionTime

操作系统: $($osInfo.OsName)
系统版本: $($osInfo.OsVersion)
处理器: $($osInfo.CsProcessors[0].Name)

PowerShell版本: $($PSVersionTable.PSVersion)
系统编码: $encoding

Java环境:
$javaVersion

Maven环境:
$mavenVersion

"@

$reportContent | Out-File -FilePath $reportPath -Encoding UTF8
Write-Host "检测报告已保存到: $reportPath" -ForegroundColor Green

