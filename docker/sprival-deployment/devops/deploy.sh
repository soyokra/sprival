#!/usr/bin/env bash
set -euo pipefail

# kubeconfig 优先顺序：/root/.kube/config（已在 docker-compose 中映射）
# 支持传参指定清单路径

MANIFEST_PATH=${1:-/workspace/project/deploy}
NAMESPACE=${NAMESPACE:-default}

if [ ! -e "$MANIFEST_PATH" ]; then
  echo "[deploy] manifest path not found: $MANIFEST_PATH" >&2
  exit 1
fi

echo "[deploy] applying manifests from: ${MANIFEST_PATH} in namespace ${NAMESPACE}"
kubectl apply -n "${NAMESPACE}" -f "${MANIFEST_PATH}"

echo "[deploy] rollout status (deployments)"
kubectl -n "${NAMESPACE}" get deploy

echo "[deploy] pods"
kubectl -n "${NAMESPACE}" get pods -o wide

echo "[deploy] done"


