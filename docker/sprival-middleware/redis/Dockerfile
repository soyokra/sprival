FROM redis:7.2-alpine

# 安装必要的工具
RUN apk add --no-cache tzdata

# 设置时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 创建Redis配置目录
RUN mkdir -p /usr/local/etc/redis /data /logs

# 复制配置文件
COPY redis.conf /usr/local/etc/redis/redis.conf

# 设置权限
RUN chown -R redis:redis /data /logs /usr/local/etc/redis

# 暴露端口
EXPOSE 6379

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD redis-cli ping || exit 1

# 启动Redis服务器
CMD ["redis-server", "/usr/local/etc/redis/redis.conf"]
