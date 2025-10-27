# Docker Registry 私有镜像仓库

## 概述

本项目使用 Docker Registry 作为私有镜像仓库，用于存储和分发 Docker 镜像。

## 功能特性

- ✅ 本地私有镜像仓库
- ✅ 持久化存储
- ✅ HTTP 访问（开发环境）
- ✅ 健康检查支持
- ✅ 镜像删除功能
- ✅ Web UI 管理界面
- ✅ 基础认证支持（可扩展）

## 快速开始

### 启动所有服务（包括 UI）

```bash
cd docker/sprival-deployment
docker-compose up -d
```

### 单独启动 Registry

```bash
docker-compose up -d registry
```

### 启动 Web UI

```bash
docker-compose up -d registry-ui
```

### 访问 Web 界面

浏览器访问：

- **Web UI**: http://localhost:8080
- **Registry API**: http://localhost:5000

### 验证服务

```bash
# 检查 Registry 健康状态
curl http://localhost:5000/v2/

# 查看仓库列表
curl http://localhost:5000/v2/_catalog
```

## 使用镜像

### 配置客户端

由于使用 HTTP（非 HTTPS），需要配置 Docker 客户端允许不安全连接。

**Linux / macOS:**

编辑或创建文件 `~/.docker/daemon.json`:

```json
{
  "insecure-registries": ["localhost:5000"]
}
```

**Windows:**

编辑或创建文件 `%USERPROFILE%\.docker\daemon.json`:

```json
{
  "insecure-registries": ["localhost:5000"]
}
```

配置完成后，需要重启 Docker 服务：

```bash
# Linux
sudo systemctl restart docker

# macOS / Windows
# 重启 Docker Desktop
```

### 使用示例

```bash
# 标记镜像
docker tag my-app:latest localhost:5000/sprival/my-app:latest

# 推送镜像
docker push localhost:5000/sprival/my-app:latest

# 拉取镜像
docker pull localhost:5000/sprival/my-app:latest
```

## 配置说明

### 配置文件位置

配置文件位于 `registry/config.yml`，包含以下配置：

- **存储配置**: 使用文件系统存储，数据持久化到卷 `registry-data`
- **HTTP 配置**: 监听 5000 端口，配置了安全响应头
- **日志配置**: 日志级别为 info
- **健康检查**: 存储驱动健康检查，每 10 秒检查一次
- **删除功能**: 启用镜像删除功能

### 数据持久化

Registry 数据存储在 Docker 卷 `registry-data` 中，即使容器重启或删除，数据也不会丢失。

查看卷信息：

```bash
docker volume inspect sprival-deployment_registry-data
```

### 安全增强（可选）

#### 添加基础认证

如果需要添加认证，可以创建用户凭证：

```bash
# 生成密码文件
docker run --rm -it -v ${PWD}/registry:/auth \
  alpine/htpasswd -Bbn username password > auth/htpasswd

# 修改 config.yml 添加认证配置
# auth:
#   htpasswd:
#     realm: basic-realm
#     path: /auth/htpasswd
```

然后更新 docker-compose.yml：

```yaml
volumes:
  - registry-data:/var/lib/registry
  - ./registry/config.yml:/etc/docker/registry/config.yml:ro
  - ./registry/auth:/auth
```

#### 使用 HTTPS（生产环境推荐）

生产环境应使用 HTTPS。修改 docker-compose.yml：

```yaml
environment:
  REGISTRY_HTTP_TLS_ENABLED: "true"
  REGISTRY_HTTP_TLS_CERTIFICATE: /certs/server.crt
  REGISTRY_HTTP_TLS_KEY: /certs/server.key
volumes:
  - ./registry/certs:/certs
```

## 维护命令

### 查看日志

```bash
docker-compose logs -f registry
```

### 查看存储使用

```bash
# 进入容器查看
docker exec -it sprival-registry du -sh /var/lib/registry

# 或直接在宿主机查看卷
docker run --rm -v sprival-deployment_registry-data:/data \
  alpine du -sh /data
```

### 清理旧镜像（需要启用垃圾回收）

编辑 `config.yml` 添加：

```yaml
storage:
  filesystem:
    rootdirectory: /var/lib/registry
  delete:
    enabled: true
  maintenance:
    uploadpurging:
      enabled: true
      age: 168h
      dryrun: false
```

然后重启服务：

```bash
docker-compose restart registry
```

### 备份数据

```bash
docker run --rm -v sprival-deployment_registry-data:/data \
  -v ${PWD}/backup:/backup \
  alpine tar czf /backup/registry-backup-$(date +%Y%m%d).tar.gz -C /data .
```

### 恢复数据

```bash
docker run --rm -v sprival-deployment_registry-data:/data \
  -v ${PWD}/backup:/backup \
  alpine sh -c "rm -rf /data/* && tar xzf /backup/registry-backup-YYYYMMDD.tar.gz -C /data"
```

## 故障排查

### 常见问题

1. **无法推送到仓库**

   检查是否配置了 `insecure-registries`，并重启 Docker。

2. **端口被占用**

   修改 docker-compose.yml 中的端口映射。

3. **存储空间不足**

   定期清理无用镜像或扩大存储卷容量。

### 调试命令

```bash
# 检查容器状态
docker ps -a | grep sprival-registry

# 检查网络
docker network inspect sprival-deployment_frontend

# 查看容器日志
docker-compose logs --tail=100 registry
```

## 生产环境建议

1. **使用 HTTPS**: 配置 TLS 证书
2. **启用认证**: 使用 htpasswd 或 JWT
3. **配置代理**: 使用 Nginx 作为反向代理
4. **监控告警**: 集成 Prometheus 监控
5. **备份策略**: 定期备份 registry 数据
6. **资源限制**: 设置合理的 CPU 和内存限制

## 相关文档

- [Docker Registry 官方文档](https://docs.docker.com/registry/)
- [Registry 配置参考](https://docs.docker.com/registry/configuration/)
- [私有仓库最佳实践](https://docs.docker.com/registry/deploying/)
- [版本兼容性说明](../VERSION-COMPATIBILITY.md)
- [Registry UI 使用指南](../registry-ui/README.md)

