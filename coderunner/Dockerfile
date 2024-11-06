#FROM openjdk:17-jdk
#ARG JAR_FILE=target/*.jar
#COPY ${JAR_FILE} app.jar
#EXPOSE 8081
#ENTRYPOINT ["java", "-jar", "/app.jar"]
FROM openjdk:17-jdk-slim

# Install Docker CLI
RUN apt-get update && apt-get install -y docker.io

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app.jar"]
