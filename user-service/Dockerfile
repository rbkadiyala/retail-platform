# Build stage
FROM maven:3.9.2-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/target/user-service-1.0.0.jar user-service.jar
EXPOSE 9083
ENTRYPOINT ["java","-jar","user-service.jar"]
