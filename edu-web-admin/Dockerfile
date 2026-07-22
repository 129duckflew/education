# 构建新的基础镜像
FROM openjdk:11-jre-slim

# 设置工作目录
WORKDIR /app

# 复制JAR文件到容器中
COPY target/edu-web-admin-1.0.jar .
# 暴露web应用程序使用的端口
EXPOSE 8081

CMD    java -Dspring.profiles.active=prod -jar edu-web-admin-1.0.jar