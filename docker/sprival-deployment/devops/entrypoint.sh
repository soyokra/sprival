#!/usr/bin/env sh
set -e

if [ -d "/workspace/devops" ]; then
  chmod +x /workspace/devops/*.sh 2>/dev/null || true
fi

# 如果挂载了宿主机 kubeconfig，则复制到可写目录并做适配
if [ -f "/root/.kube/config" ]; then
  mkdir -p /root/.kube-w
  cp /root/.kube/config /root/.kube-w/config || true
  # 将 127.0.0.1 替换为 host.docker.internal，保留端口
  sed -i 's#https://127.0.0.1:#https://host.docker.internal:#g' /root/.kube-w/config 2>/dev/null || true
  # 关闭证书校验以避开主机名不匹配问题（仅本地环境）
  if command -v kubectl >/dev/null 2>&1; then
    KUBECONFIG=/root/.kube-w/config
    ctx=$(KUBECONFIG=$KUBECONFIG kubectl config current-context 2>/dev/null || true)
    if [ -n "$ctx" ]; then
      clu=$(KUBECONFIG=$KUBECONFIG kubectl config view -o jsonpath="{.contexts[?(@.name=='$ctx')].context.cluster}" 2>/dev/null || true)
      if [ -n "$clu" ]; then
        KUBECONFIG=$KUBECONFIG kubectl config set-cluster "$clu" --insecure-skip-tls-verify=true >/dev/null 2>&1 || true
      fi
    fi
    export KUBECONFIG=/root/.kube-w/config
  fi
fi

exec "$@"


