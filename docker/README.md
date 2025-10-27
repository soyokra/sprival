# Sprival 应用 Docker 部署

## 概述

本目录包含 Sprival 应用的 Docker 部署相关文件。

## 快速开始

### 方式一：使用脚本构建

**Linux/Mac:**
```bash
chmod +x ../docker-build.sh
../docker-build.sh
```

**Windows PowerShell:**
```powershell
.\..\docker-build.ps1
```

### 方式二：使用 Docker 命令

```bash
# 在项目根目录执行
docker build -t sprival:latest .
```

### 方式三：使用 Docker Compose

```bash
# 构建并运行
cd docker
docker-compose -f deploy.yml up -d

# 查看日志
docker-compose -f deploy.yml logs -f

# 停止
docker-compose -f deploy.yml down
```

## 运行容器

### 基础运行

```bash
docker run -d \
  --name sprival \
  -p 8080:8080 \
  sprival:latest
```

### 自定义配置

```bash
docker run -d \
  --name sprival \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e JAVA_OPTS="-Xms1024m -Xmx2048m" \
  -v $(pwd)/logs:/app/logs \
  sprival:latest
```

### 连接到现有网络

```bash
# 连接到 sprival 网络（如果存在）
docker run -d \
  --name sprival \
  -p 8080:8080 \
  --network sprival-network \
  sprival:latest
```

## 验证部署

### 检查容器状态

```bash
docker ps | grep sprival
```

### 查看日志

```bash
# 查看所有日志
docker logs sprival

# 实时查看日志
docker logs -f sprival

# 查看最近 100 行
docker logs --tail 100 sprival
```

### 健康检查

```bash
# 使用 curl
curl http://localhost:8080/actuator/health

# 或使用 wget
wget -qO- http://localhost:8080/actuator/health
```

## 镜像管理

### 查看镜像

```bash
docker images | grep sprival
```

### 打标签并推送到仓库

```bash
# 标记镜像
docker tag sprival:latest localhost:5000/sprival:1.0.0

# 推送到本地仓库（需要先启动 registry）
docker push localhost:5000/sprival:1.0.0

# 推送到远程仓库
docker tag sprival:latest your-registry.com/sprival:1.0.0
docker push your-registry.com/sprival:1.0.0
```

### 删除镜像

```bash
docker rmi sprival:latest
```

## 目录结构

```
.
├── Dockerfile          # 应用 Dockerfile
├── deploy.yml         # Docker Compose 部署配置
├── README.md          # 本文档
└── Dockerfile         # 参考版本（与根目录相同）
```

## 环境变量

| 变量名 | 说明 | 默认值 |
|-------|------|--------|
| `SPRING_PROFILES_ACTIVE` | Spring Profile | `prod` |
| `JAVA_OPTS` | JVM 参数 | `-Xms512m -Xmx1024m` |
| `TZ` | 时区 | `Asia/Shanghai` |

## 端口映射

- **应用端口**: 8080
- **健康检查**: `/actuator/health`

## 数据持久化

### 日志目录

```bash
# 挂载日志目录
docker run -v /host/path/logs:/app/logs sprival:latest
```

### 配置文件

如果需要在容器外管理配置：

```bash
docker run -v /host/path/config:/app/config sprival:latest
```

## 与 Registry 集成

### 推送到本地 Registry

```bash
# 1. 确保本地 Registry 运行
cd ../sprival-deployment
docker-compose up -d

# 2. 标记镜像
docker tag sprival:latest localhost:5000/sprival:1.0.0

# 3. 推送镜像
docker push localhost:5000/sprival:1.0.0

# 4. 在浏览器中查看
# http://localhost:8080 (Registry UI)
```

## 故障排查

### 容器无法启动

```bash
# 查看容器日志
docker logs sprival

# 查看容器状态
docker ps -a | grep sprival

# 进入容器调试
docker exec -it sprival sh
```

### 端口被占用

```bash
# 检查端口占用
netstat -an | grep 8080

# 使用其他端口
docker run -p 9000:8080 sprival:latest
```

### 健康检查失败

```bash
# 检查应用是否运行
docker exec sprival wget -O- http://localhost:8080/actuator/health

# 检查 JVM 参数
docker exec sprival ps aux | grep java
```

## 生产环境建议

1. **资源限制**: 设置合适的 CPU 和内存限制
2. **健康检查**: 配置健康检查端点
3. **日志收集**: 集成日志收集系统（ELK）
4. **监控告警**: 配置 Prometheus + Grafana
5. **安全**: 使用非 root 用户运行（已配置）
6. **备份**: 定期备份数据和配置

## 相关文档

- [项目 README](../README.md)
- [部署指南](../docs/reference/deployment/)
- [Registry 使用](../docker/sprival-deployment/)
