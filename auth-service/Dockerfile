# ----------------------
# Stage 1: Build
# ----------------------
FROM maven:3.9.2-eclipse-temurin-17 AS build
WORKDIR /app

# Use a dedicated Maven cache volume
VOLUME /root/.m2

# ----------------------
# 1️⃣ Copy only POM files to leverage Docker cache
# ----------------------
COPY pom.xml ./ 
COPY auth-service/pom.xml auth-service/
COPY user-service/pom.xml user-service/
# Add other modules if any

# ----------------------
# 2️⃣ Pre-fetch dependencies (cached if POMs unchanged)
# ----------------------
RUN mvn dependency:go-offline -B

# ----------------------
# 3️⃣ Copy full source code
# ----------------------
COPY . .

# ----------------------
# 4️⃣ Build only auth-service module
# ----------------------
RUN mvn clean package -pl auth-service -am -DskipTests

# ----------------------
# Stage 2: Runtime
# ----------------------
FROM eclipse-temurin:17-jdk
WORKDIR /app

# ----------------------
# Copy built JAR from build stage
# ----------------------
COPY --from=build /app/auth-service/target/auth-service-1.0.0.jar auth-service.jar

# ----------------------
# Create logs directory
# ----------------------
RUN mkdir -p /app/logs && chmod -R 777 /app/logs

EXPOSE 9082
ENTRYPOINT ["java", "-jar", "auth-service.jar"]
