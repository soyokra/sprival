# Sprival项目结构验证脚本
# 用于验证项目文件目录结构是否符合规范

param(
    [switch]$Fix = $false,
    [switch]$Verbose = $false
)

Write-Host "🔍 开始验证Sprival项目结构..." -ForegroundColor Green

# 定义标准目录结构
$standardStructure = @{
    "docs" = @{
        "ai-development" = @("context", "prompt-templates")
        "components" = @("http-server", "http-client", "mysql", "redis", "clickhouse", "mongodb", "rabbitmq", "kafka", "elasticsearch", "monitoring")
        "deployment" = @()
        "development" = @()
    }
    "scripts" = @{
        "build" = @()
        "deployment" = @()
        "development" = @()
        "testing" = @()
    }
    "src" = @{
        "main" = @{
            "java" = @{
                "com" = @{
                    "soyokra" = @{
                        "sprival" = @("config", "client", "controller", "service", "repository", "entity", "dto", "exception", "util", "aspect")
                    }
                }
            }
            "resources" = @("config", "mapper", "static", "templates")
        }
        "test" = @{
            "java" = @()
            "resources" = @()
        }
    }
    "dockers" = @("clickhouse", "elasticsearch", "grafana", "kafka", "mongodb", "prometheus", "rabbitmq", "redis")
    "tools" = @{
        "ide" = @()
        "maven" = @()
        "scripts" = @()
    }
    "tests" = @("integration", "performance", "data")
}

# 定义根目录允许的文件
$allowedRootFiles = @(
    "README.md",
    "LICENSE",
    "pom.xml",
    "start-utf8.bat",
    ".gitignore",
    ".editorconfig"
)

# 定义不允许在根目录的文件
$forbiddenRootFiles = @(
    "encoding-test.ps1",
    "IDEA-编码配置指南.md",
    "sprvial.iml",
    "start-utf8.ps1"
)

# 定义Java包结构规范
$javaPackageStructure = @{
    "config" = @("http", "redis", "mysql", "kafka", "mongodb", "rabbitmq", "clickhouse", "elasticsearch", "jetty", "ratelimiter", "monitoring")
    "client" = @()
    "controller" = @()
    "service" = @()
    "repository" = @()
    "entity" = @()
    "dto" = @()
    "exception" = @()
    "util" = @()
    "aspect" = @()
}

# 验证结果
$validationResults = @{
    "errors" = @()
    "warnings" = @()
    "suggestions" = @()
}

# 验证根目录文件
Write-Host "📁 验证根目录文件..." -ForegroundColor Yellow
$rootFiles = Get-ChildItem -Path "." -File | Where-Object { $_.Name -notlike ".*" }
foreach ($file in $rootFiles) {
    if ($file.Name -in $forbiddenRootFiles) {
        $validationResults.errors += "根目录不应包含文件: $($file.Name)"
    } elseif ($file.Name -notin $allowedRootFiles) {
        $validationResults.warnings += "根目录文件可能需要重新组织: $($file.Name)"
    }
}

# 验证目录结构
Write-Host "📂 验证目录结构..." -ForegroundColor Yellow
function Validate-DirectoryStructure {
    param(
        [string]$Path,
        [hashtable]$Structure,
        [string]$CurrentPath = ""
    )
    
    if (-not (Test-Path $Path)) {
        $validationResults.warnings += "缺少目录: $CurrentPath"
        return
    }
    
    $items = Get-ChildItem -Path $Path -Directory
    foreach ($item in $items) {
        $itemPath = if ($CurrentPath) { "$CurrentPath/$($item.Name)" } else { $item.Name }
        
        if ($Structure.ContainsKey($item.Name)) {
            if ($Structure[$item.Name] -is [hashtable]) {
                Validate-DirectoryStructure -Path $item.FullName -Structure $Structure[$item.Name] -CurrentPath $itemPath
            } elseif ($Structure[$item.Name] -is [array]) {
                # 验证子目录
                foreach ($subDir in $Structure[$item.Name]) {
                    $subPath = Join-Path $item.FullName $subDir
                    if (-not (Test-Path $subPath)) {
                        $validationResults.warnings += "缺少子目录: $itemPath/$subDir"
                    }
                }
            }
        } else {
            $validationResults.warnings += "未定义的目录: $itemPath"
        }
    }
}

# 验证Java包结构
Write-Host "☕ 验证Java包结构..." -ForegroundColor Yellow
$javaPath = "src\main\java\com\soyokra\sprival"
if (Test-Path $javaPath) {
    $javaDirs = Get-ChildItem -Path $javaPath -Directory
    foreach ($dir in $javaDirs) {
        if ($javaPackageStructure.ContainsKey($dir.Name)) {
            # 验证子包
            $subDirs = Get-ChildItem -Path $dir.FullName -Directory
            foreach ($subDir in $subDirs) {
                if ($subDir.Name -notin $javaPackageStructure[$dir.Name]) {
                    $validationResults.warnings += "Java包结构不规范: $($dir.Name).$($subDir.Name)"
                }
            }
        } else {
            $validationResults.warnings += "未定义的Java包: $($dir.Name)"
        }
    }
} else {
    $validationResults.errors += "缺少Java源码目录: $javaPath"
}

# 验证配置文件
Write-Host "⚙️ 验证配置文件..." -ForegroundColor Yellow
$configFiles = @(
    "src\main\resources\application.properties",
    "src\main\resources\config\redisson.yml",
    "src\main\resources\config\spy.properties"
)

foreach ($configFile in $configFiles) {
    if (-not (Test-Path $configFile)) {
        $validationResults.warnings += "缺少配置文件: $configFile"
    }
}

# 验证文档结构
Write-Host "📚 验证文档结构..." -ForegroundColor Yellow
$docsPath = "docs"
if (Test-Path $docsPath) {
    # 检查组件文档命名一致性
    $componentDirs = Get-ChildItem -Path $docsPath -Directory | Where-Object { $_.Name -like "spring-*" }
    foreach ($dir in $componentDirs) {
        $expectedName = $dir.Name -replace "spring-", ""
        $validationResults.suggestions += "建议重命名文档目录: $($dir.Name) -> components/$expectedName"
    }
} else {
    $validationResults.errors += "缺少文档目录: $docsPath"
}

# 验证Docker配置
Write-Host "🐳 验证Docker配置..." -ForegroundColor Yellow
$dockerPath = "dockers"
if (Test-Path $dockerPath) {
    $dockerCompose = Join-Path $dockerPath "docker-compose.yml"
    if (-not (Test-Path $dockerCompose)) {
        $validationResults.warnings += "缺少Docker Compose文件: $dockerCompose"
    }
} else {
    $validationResults.warnings += "缺少Docker配置目录: $dockerPath"
}

# 验证脚本文件
Write-Host "🔧 验证脚本文件..." -ForegroundColor Yellow
$scriptsPath = "scripts"
if (Test-Path $scriptsPath) {
    $requiredScripts = @("ai-dev-start.ps1", "generate-project-context.ps1")
    foreach ($script in $requiredScripts) {
        $scriptPath = Join-Path $scriptsPath $script
        if (-not (Test-Path $scriptPath)) {
            $validationResults.warnings += "缺少脚本文件: $scriptPath"
        }
    }
} else {
    $validationResults.errors += "缺少脚本目录: $scriptsPath"
}

# 输出验证结果
Write-Host "`n📊 验证结果:" -ForegroundColor Cyan

if ($validationResults.errors.Count -gt 0) {
    Write-Host "`n❌ 错误 ($($validationResults.errors.Count)):" -ForegroundColor Red
    foreach ($error in $validationResults.errors) {
        Write-Host "   - $error" -ForegroundColor Red
    }
}

if ($validationResults.warnings.Count -gt 0) {
    Write-Host "`n⚠️ 警告 ($($validationResults.warnings.Count)):" -ForegroundColor Yellow
    foreach ($warning in $validationResults.warnings) {
        Write-Host "   - $warning" -ForegroundColor Yellow
    }
}

if ($validationResults.suggestions.Count -gt 0) {
    Write-Host "`n💡 建议 ($($validationResults.suggestions.Count)):" -ForegroundColor Blue
    foreach ($suggestion in $validationResults.suggestions) {
        Write-Host "   - $suggestion" -ForegroundColor Blue
    }
}

# 生成修复建议
if ($Fix) {
    Write-Host "`n🔧 生成修复建议..." -ForegroundColor Green
    
    # 创建修复脚本
    $fixScript = @"
# Sprival项目结构修复脚本
# 自动生成于 $(Get-Date)

Write-Host "🔧 开始修复项目结构..." -ForegroundColor Green

"@
    
    # 添加修复命令
    foreach ($error in $validationResults.errors) {
        if ($error -like "*缺少目录*") {
            $dirName = $error -replace "缺少目录: ", ""
            $fixScript += "`n# 创建目录: $dirName`n"
            $fixScript += "New-Item -ItemType Directory -Path `"$dirName`" -Force | Out-Null`n"
        }
    }
    
    foreach ($warning in $validationResults.warnings) {
        if ($warning -like "*缺少文件*") {
            $fileName = $warning -replace "缺少文件: ", ""
            $fixScript += "`n# 创建文件: $fileName`n"
            $fixScript += "New-Item -ItemType File -Path `"$fileName`" -Force | Out-Null`n"
        }
    }
    
    $fixScript += "`nWrite-Host '✅ 项目结构修复完成' -ForegroundColor Green`n"
    
    $fixScriptPath = "scripts\fix-project-structure.ps1"
    $fixScript | Out-File -FilePath $fixScriptPath -Encoding UTF8
    Write-Host "📝 修复脚本已生成: $fixScriptPath" -ForegroundColor Cyan
}

# 生成结构报告
$reportPath = "docs\ai-development\context\project-structure-report-$(Get-Date -Format 'yyyy-MM-dd-HH-mm').md"
$report = @"
# Sprival项目结构验证报告

**生成时间**: $(Get-Date)  
**验证结果**: $($validationResults.errors.Count) 错误, $($validationResults.warnings.Count) 警告, $($validationResults.suggestions.Count) 建议

## 错误 ($($validationResults.errors.Count))
"@

foreach ($error in $validationResults.errors) {
    $report += "`n- $error"
}

$report += "`n`n## 警告 ($($validationResults.warnings.Count))"
foreach ($warning in $validationResults.warnings) {
    $report += "`n- $warning"
}

$report += "`n`n## 建议 ($($validationResults.suggestions.Count))"
foreach ($suggestion in $validationResults.suggestions) {
    $report += "`n- $suggestion"
}

$report += "`n`n## 修复建议`n`n1. 运行修复脚本: .\scripts\fix-project-structure.ps1`n2. 手动调整不符合规范的文件和目录`n3. 更新相关配置和文档引用`n4. 重新运行验证脚本确认修复结果`n"

$report | Out-File -FilePath $reportPath -Encoding UTF8

# 总结
$totalIssues = $validationResults.errors.Count + $validationResults.warnings.Count + $validationResults.suggestions.Count
if ($totalIssues -eq 0) {
    Write-Host "`n✅ 项目结构验证通过！" -ForegroundColor Green
} else {
    Write-Host "`n📋 发现 $totalIssues 个问题需要处理" -ForegroundColor Yellow
    Write-Host "📄 详细报告: $reportPath" -ForegroundColor Cyan
    if ($Fix) {
        Write-Host "🔧 修复脚本: scripts\fix-project-structure.ps1" -ForegroundColor Cyan
    }
}

Write-Host "`n💡 提示: 使用 -Fix 参数生成修复脚本" -ForegroundColor Yellow
