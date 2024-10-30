FROM --platform=linux/amd64 openjdk:17-jdk-alpine

ARG JAR_FILE=aiku-main.jar
COPY /build/libs/${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]
