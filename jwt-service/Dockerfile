# ----------------------
# Stage 1: Build
# ----------------------
FROM maven:3.9.2-eclipse-temurin-17 AS build
WORKDIR /app

# Copy entire repo (parent POM must be visible)
COPY . .

# Build only jwt-service module
RUN mvn clean package -pl jwt-service -am -DskipTests

# ----------------------
# Stage 2: Runtime
# ----------------------
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copy the built JAR
COPY --from=build /app/jwt-service/target/jwt-service-1.0.0.jar jwt-service.jar

# Create logs directory
RUN mkdir -p /app/logs && chmod -R 777 /app/logs

# Expose service port
ARG JWT_SERVICE_PORT=9081
EXPOSE ${JWT_SERVICE_PORT}

# Environment variables for Spring Boot
ENV SERVER_PORT=${JWT_SERVICE_PORT}
ENV SPRING_PROFILES_ACTIVE=default

# Run the application
ENTRYPOINT ["java", "-jar", "jwt-service.jar"]
