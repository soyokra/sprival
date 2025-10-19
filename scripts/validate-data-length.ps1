# 数据长度验证脚本 (Windows PowerShell)
# 用途：验证性能测试生成的数据是否符合数据库字段长度限制

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  数据长度验证测试" -ForegroundColor Cyan
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

# 运行数据长度验证测试
Write-Host ""
Write-Host "【3】运行数据长度验证测试..." -ForegroundColor Green
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 编译验证器
Push-Location src\test\java
javac -encoding UTF-8 com\soyokra\sprival\performance\util\SimpleDataLengthValidator.java

# 执行验证
java com.soyokra.sprival.performance.util.SimpleDataLengthValidator
$validationResult = $LASTEXITCODE
Pop-Location

# 检查测试结果
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  验证完成" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ 数据长度验证通过" -ForegroundColor Green
    Write-Host ""
    Write-Host "验证结果:" -ForegroundColor Cyan
    Write-Host "  ✓ order_id 长度符合 varchar(22) 限制" -ForegroundColor White
    Write-Host "  ✓ trade_id 长度符合 varchar(20) 限制" -ForegroundColor White
    Write-Host "  ✓ idempotent_id 长度符合 varchar(50) 限制" -ForegroundColor White
    Write-Host "  ✓ 数据唯一性验证通过" -ForegroundColor White
    Write-Host ""
} else {
    Write-Host "❌ 数据长度验证失败" -ForegroundColor Red
    Write-Host "请查看上方日志获取详细错误信息" -ForegroundColor Yellow
    Write-Host ""
    exit 1
}

# 显示说明
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  说明" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "此脚本验证以下内容：" -ForegroundColor Gray
Write-Host "1. 生成的 order_id 长度不超过 22 字符" -ForegroundColor White
Write-Host "2. 生成的 trade_id 长度不超过 20 字符" -ForegroundColor White
Write-Host "3. 生成的 idempotent_id 长度不超过 50 字符" -ForegroundColor White
Write-Host "4. 生成的数据具有良好的唯一性" -ForegroundColor White
Write-Host ""
Write-Host "详细文档请查看：" -ForegroundColor Gray
Write-Host "  docs\components\performance-testing\DATA-LENGTH-FIX.md" -ForegroundColor Yellow
Write-Host ""

