# Sprival 应用 Docker 构建脚本 (PowerShell)

param(
    [string]$Version = "latest"
)

$ErrorActionPreference = "Stop"

Write-Host "==========================================" -ForegroundColor Green
Write-Host "Sprival 应用 Docker 构建" -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Green

# 设置变量
$ImageName = "sprival"
$Tag = "${ImageName}:${Version}"

Write-Host "`n构建信息:" -ForegroundColor Yellow
Write-Host "  镜像名称: $ImageName"
Write-Host "  版本标签: $Version"
Write-Host "  完整标签: $Tag"
Write-Host ""

# 构建镜像
Write-Host "开始构建 Docker 镜像..." -ForegroundColor Green
docker build `
    --tag $Tag `
    --progress=plain `
    .

if ($LASTEXITCODE -eq 0) {
    Write-Host "`n✅ 构建成功！" -ForegroundColor Green
    Write-Host ""
    Write-Host "使用以下命令运行:" -ForegroundColor Yellow
    Write-Host "  docker run -d -p 8080:8080 --name sprival $Tag"
    Write-Host ""
    Write-Host "查看镜像:"
    Write-Host "  docker images | Select-String sprival"
} else {
    Write-Host "`n❌ 构建失败！" -ForegroundColor Red
    exit 1
}

