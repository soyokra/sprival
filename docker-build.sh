#!/bin/bash

# Sprival 应用 Docker 构建脚本

set -e

# 颜色输出
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}=========================================="
echo "Sprival 应用 Docker 构建"
echo "==========================================${NC}"

# 设置变量
IMAGE_NAME="sprival"
VERSION="${1:-latest}"
TAG="${IMAGE_NAME}:${VERSION}"

echo -e "${YELLOW}构建信息:${NC}"
echo "  镜像名称: ${IMAGE_NAME}"
echo "  版本标签: ${VERSION}"
echo "  完整标签: ${TAG}"
echo ""

# 构建镜像
echo -e "${GREEN}开始构建 Docker 镜像...${NC}"
docker build \
  --tag ${TAG} \
  --progress=plain \
  .

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ 构建成功！${NC}"
    echo ""
    echo "使用以下命令运行:"
    echo "  docker run -d -p 8080:8080 --name sprival ${TAG}"
    echo ""
    echo "查看镜像:"
    echo "  docker images | grep ${IMAGE_NAME}"
else
    echo -e "${RED}❌ 构建失败！${NC}"
    exit 1
fi

