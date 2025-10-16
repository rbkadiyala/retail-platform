# ----------------------
# Stage 1: Build
# ----------------------
FROM maven:3.9.2-eclipse-temurin-17 AS build
WORKDIR /app

# Copy entire repo (parent POM must be visible)
COPY . .

# Build only user-service module
RUN mvn clean package -pl user-service -am -DskipTests

# ----------------------
# Stage 2: Runtime
# ----------------------
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copy the built JAR
COPY --from=build /app/user-service/target/user-service-1.0.0.jar user-service.jar

# Create logs directory (hardcoded)
RUN mkdir -p /app/logs && chmod -R 777 /app/logs

# Expose service port
ARG USER_SERVICE_PORT=9083
EXPOSE ${USER_SERVICE_PORT}

# Environment variables for Spring Boot (without log path)
ENV SERVER_PORT=${USER_SERVICE_PORT}
ENV SPRING_PROFILES_ACTIVE=default

# Run the application
ENTRYPOINT ["java", "-jar", "user-service.jar"]
