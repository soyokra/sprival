#!/usr/bin/env bash
set -euo pipefail

# 默认参数
REGISTRY_HOST=${REGISTRY_HOST:-localhost:5000}
IMAGE_NAMESPACE=${IMAGE_NAMESPACE:-sprival}
IMAGE_NAME=${1:-app}
IMAGE_TAG=${2:-$(date +%Y%m%d%H%M%S)}
BUILD_CONTEXT=${3:-/workspace/project}

FULL_IMAGE_TAG="${REGISTRY_HOST}/${IMAGE_NAMESPACE}/${IMAGE_NAME}:${IMAGE_TAG}"

echo "[build] building image: ${FULL_IMAGE_TAG}"
docker build -t "${FULL_IMAGE_TAG}" "${BUILD_CONTEXT}"

echo "[build] pushing image: ${FULL_IMAGE_TAG}"
docker push "${FULL_IMAGE_TAG}"

echo "[build] done"


