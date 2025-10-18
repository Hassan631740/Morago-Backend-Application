# ===== Stage 1: Build =====
FROM eclipse-temurin:21-jdk-alpine AS build

# Install Maven
RUN apk add --no-cache maven bash

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies first (cache optimization)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the jar
RUN mvn clean package -DskipTests

# ===== Stage 2: Runtime =====
FROM eclipse-temurin:21-jdk-alpine

# Set working directory
WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/morago-backend-0.0.1-SNAPSHOT.jar app.jar

# Expose default port (Railway overrides $PORT)
EXPOSE 8080

# Set Spring profile to railway for Railway deployment
ENV SPRING_PROFILES_ACTIVE=railway

# Run Spring Boot jar
ENTRYPOINT ["sh", "-c", "java -Dserver.port=$PORT -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE -jar app.jar"]
