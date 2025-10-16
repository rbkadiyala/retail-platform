# ----------------------
# Stage 1: Build
# ----------------------
FROM maven:3.9.2-eclipse-temurin-17 AS build
WORKDIR /app

# Copy entire repo to resolve parent POM dependencies
COPY ../ . 

# Build only jwt-service module
RUN mvn clean package -pl jwt-service -am -DskipTests

# ----------------------
# Stage 2: Runtime
# ----------------------
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /app/jwt-service/target/jwt-service-1.0.0.jar jwt-service.jar

# Create logs directory
RUN mkdir -p /app/logs && chmod -R 777 /app/logs

EXPOSE 9084
ENTRYPOINT ["java", "-jar", "jwt-service.jar"]
