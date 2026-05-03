# Build Stage
FROM maven:3.8.3-openjdk-17-slim AS build
WORKDIR /home/app
COPY src ./src
COPY pom.xml .
RUN mvn clean package -DskipTests

# Package stage
FROM openjdk:17.0.2-jdk-slim
WORKDIR /app
COPY --from=build /home/app/target/*.jar spring_rest_docker.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","spring_rest_docker.jar"]