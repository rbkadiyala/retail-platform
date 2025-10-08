# Stage 1: Build
FROM maven:3.9.2-eclipse-temurin-17 AS build
WORKDIR /app

# Copy full project for multi-module build
COPY . .

# Download dependencies for all modules
RUN mvn dependency:go-offline -B

# Build only user-service
RUN mvn clean package -pl user-service -am -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/user-service/target/user-service-1.0.0.jar user-service.jar
EXPOSE 9083
ENTRYPOINT ["java", "-jar", "user-service.jar"]
