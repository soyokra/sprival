# 多阶段构建 Dockerfile
# 第一阶段：Maven 构建
FROM maven:3.8-openjdk-8 AS builder

# 设置工作目录
WORKDIR /app

# 复制 pom.xml 和下载依赖（利用 Docker 缓存）
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 复制源代码
COPY src ./src

# 构建应用（跳过测试以加快构建速度）
RUN mvn clean package -DskipTests -B

# 第二阶段：运行环境
FROM openjdk:8-jre-alpine

# 设置工作目录
WORKDIR /app

# 安装时区数据和健康检查工具
RUN apk add --no-cache tzdata curl

# 设置时区（可选，根据实际需求修改）
ENV TZ=Asia/Shanghai

# 创建非 root 用户
RUN addgroup -S spring && adduser -S spring -G spring

# 从构建阶段复制 JAR 文件
COPY --from=builder /app/target/*.jar app.jar

# 修改文件所有者
RUN chown spring:spring app.jar

# 切换到非 root 用户
USER spring:spring

# 暴露端口（根据应用配置修改）
EXPOSE 8080

# JVM 参数配置
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]

