# Stage 1: Build
FROM maven:3.9.2-eclipse-temurin-17 AS build
WORKDIR /app

# Copy everything to handle multi-module builds
COPY . .

# Download dependencies for all modules
RUN mvn dependency:go-offline -B

# Build only auth-service
RUN mvn clean package -pl auth-service -am -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/auth-service/target/auth-service-1.0.0.jar auth-service.jar
EXPOSE 9082
ENTRYPOINT ["java", "-jar", "auth-service.jar"]
