# Build stage
FROM maven:3.9.2-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/target/auth-service-1.0.0.jar auth-service.jar
EXPOSE 9082
ENTRYPOINT ["java","-jar","auth-service.jar"]