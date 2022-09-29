# 指定基础镜像
FROM java:11
# 维护者信息
MAINTAINER ztx
# 用于指定持久化目录
VOLUME /tmp
# 将本地文件添加到容器中（这时是打包到本地项目中的target目录下，因为jar有个前缀是target）
ADD target/chat.jar chat.jar
#ADD target/config config
#COPY target/lib
#ADD target/conf
# 指定于外界交互的端口
EXPOSE 8881
#设置时区 run后面
RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone

# 配置容器，使其可执行化
#ENTRYPOINT exec java -Xms512m -Xmx512m -jar /app.jar
ENTRYPOINT exec java $JAVA_OPTS -jar -Duser.timezone=GMT+08 /chat.jar