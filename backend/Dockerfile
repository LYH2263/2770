# 构建阶段 (使用层缓存下载依赖，并配置阿里云镜像加速)
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# 1. 先拷贝配置和 pom.xml
COPY settings.xml .
COPY pom.xml .

# 2. 预下载依赖 (只要 pom.xml 不变，这一层就会被 Docker 缓存)
# 添加重试机制，处理网络不稳定问题
RUN mvn -s settings.xml dependency:go-offline -B || \
    mvn -s settings.xml dependency:go-offline -B || \
    mvn -s settings.xml dependency:resolve -B

# 3. 拷贝源码并打包
COPY src ./src
RUN mvn -s settings.xml package -DskipTests

# 运行阶段
FROM eclipse-temurin:17-jre
WORKDIR /app

# 拷贝jar包
COPY --from=build /app/target/*.jar app.jar

# 添加等待脚本
RUN sed -i 's/http:\/\/ports.ubuntu.com/https:\/\/mirrors.aliyun.com/g' /etc/apt/sources.list.d/ubuntu.sources || \
    sed -i 's/http:\/\/archive.ubuntu.com/https:\/\/mirrors.aliyun.com/g' /etc/apt/sources.list || \
    sed -i 's/http:\/\/security.ubuntu.com/https:\/\/mirrors.aliyun.com/g' /etc/apt/sources.list
RUN apt-get update && apt-get install -y netcat-openbsd && rm -rf /var/lib/apt/lists/*

EXPOSE 8270

# 启动应用
CMD ["java", "-jar", "app.jar"]
