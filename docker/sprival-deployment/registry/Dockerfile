# Docker Registry 自定义镜像
# 基于官方 registry 镜像，可以根据需要定制
FROM registry:3.0.0

# 安装工具用于健康检查
RUN apk add --no-cache wget

# 暴露默认端口
EXPOSE 5000

# 可以在这里添加自定义配置或脚本
# 例如：安装额外工具、设置环境变量等

# 示例：如果需要认证，可以添加 htpasswd 工具
# RUN apk add --no-cache apache2-utils

# 设置工作目录
WORKDIR /var/lib/registry
