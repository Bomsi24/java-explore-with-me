FROM eclipse-temurin:21-jre-jammy
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar"]
RUN apt-get update && apt-get install -y tzdata

RUN ln -snf /usr/share/zoneinfo/Asia/Krasnoyarsk /etc/localtime && echo 'Asia/Krasnoyarsk' > /etc/timezone