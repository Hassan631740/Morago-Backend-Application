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

# Download Flyway CLI for runtime
RUN mkdir -p /app/flyway && \
    wget https://repo1.maven.org/maven2/org/flywaydb/flyway-commandline/10.16.0/flyway-commandline-10.16.0.tar.gz && \
    tar -xzf flyway-commandline-10.16.0.tar.gz -C /app/flyway --strip-components=1 && \
    rm flyway-commandline-10.16.0.tar.gz

# ===== Stage 2: Runtime =====
FROM eclipse-temurin:21-jdk-alpine

# Install necessary packages for Flyway CLI
RUN apk add --no-cache bash wget mysql-client curl

# Set working directory
WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/morago-backend-0.0.1-SNAPSHOT.jar app.jar

# Copy Flyway CLI from build stage
COPY --from=build /app/flyway /app/flyway

# Copy migration files
COPY src/main/resources/db/migration /app/migrations

# Make Flyway executable
RUN chmod +x /app/flyway/flyway

# Expose default port (Railway overrides $PORT)
EXPOSE 8080

# Set Spring profile to railway for Railway deployment
ENV SPRING_PROFILES_ACTIVE=railway

# Create entrypoint script that runs Flyway repair before starting the app
RUN echo '#!/bin/bash' > /app/start.sh && \
    echo 'set -e' >> /app/start.sh && \
    echo '' >> /app/start.sh && \
    echo '# Extract database credentials from environment variables' >> /app/start.sh && \
    echo 'if [ -n "$DATABASE_URL" ]; then' >> /app/start.sh && \
    echo '  DB_URL="$DATABASE_URL"' >> /app/start.sh && \
    echo 'else' >> /app/start.sh && \
    echo '  DB_URL="jdbc:mysql://${MYSQLHOST}:${MYSQLPORT:-3306}/${MYSQLDATABASE}"' >> /app/start.sh && \
    echo 'fi' >> /app/start.sh && \
    echo '' >> /app/start.sh && \
    echo '# Run Flyway repair to fix checksum mismatches' >> /app/start.sh && \
    echo '/app/flyway/flyway -url="$DB_URL" -user="${DB_USERNAME:-$MYSQLUSER}" -password="${DB_PASSWORD:-$MYSQLPASSWORD}" -locations=filesystem:/app/migrations repair || true' >> /app/start.sh && \
    echo '' >> /app/start.sh && \
    echo '# Start the Spring Boot application' >> /app/start.sh && \
    echo 'java -Dserver.port=$PORT -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE -jar app.jar' >> /app/start.sh && \
    chmod +x /app/start.sh

# Run the entrypoint script
ENTRYPOINT ["/app/start.sh"]
