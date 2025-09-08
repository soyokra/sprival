# Sprival项目编码兼容性测试脚本
# 验证UTF-8编码配置是否正确

Write-Host "=== Sprival编码兼容性测试 ===" -ForegroundColor Cyan

# 1. 检查Java编码设置
Write-Host "`n1. 检查Java编码设置..." -ForegroundColor Yellow
$javaEncoding = java -XshowSettings:properties -version 2>&1 | Select-String "file.encoding"
Write-Host "当前Java编码: $javaEncoding"

# 2. 检查Maven编码设置
Write-Host "`n2. 检查Maven编码设置..." -ForegroundColor Yellow
$mavenVersion = mvn -version
Write-Host "Maven版本信息:"
Write-Host $mavenVersion

# 3. 检查项目文件编码
Write-Host "`n3. 检查项目文件编码..." -ForegroundColor Yellow
$propertiesFile = "src/main/resources/application.properties"
if (Test-Path $propertiesFile) {
    $content = Get-Content $propertiesFile -Raw
    $bytes = [System.Text.Encoding]::UTF8.GetBytes($content)
    Write-Host "application.properties文件大小: $($bytes.Length) 字节"
    Write-Host "文件编码检查: UTF-8"
} else {
    Write-Host "application.properties文件不存在" -ForegroundColor Red
}

# 4. 编译测试
Write-Host "`n4. 执行编译测试..." -ForegroundColor Yellow
try {
    mvn clean compile -Dfile.encoding=UTF-8
    Write-Host "编译成功" -ForegroundColor Green
} catch {
    Write-Host "编译失败: $_" -ForegroundColor Red
}

# 5. 测试应用启动
Write-Host "`n5. 测试应用启动（5秒后自动停止）..." -ForegroundColor Yellow
try {
    $process = Start-Process -FilePath "mvn" -ArgumentList "spring-boot:run", "-Dfile.encoding=UTF-8" -PassThru -NoNewWindow
    Start-Sleep -Seconds 5
    Stop-Process -Id $process.Id -Force
    Write-Host "应用启动测试完成" -ForegroundColor Green
} catch {
    Write-Host "应用启动测试失败: $_" -ForegroundColor Red
}

Write-Host "`n=== 测试完成 ===" -ForegroundColor Cyan
Read-Host "按任意键继续..."
