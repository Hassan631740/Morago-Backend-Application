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
    echo '# Build JDBC URL and credentials for Flyway' >> /app/start.sh && \
    echo 'FLYWAY_USER="${DB_USERNAME:-${MYSQLUSER:-}}"' >> /app/start.sh && \
    echo 'FLYWAY_PASSWORD="${DB_PASSWORD:-${MYSQLPASSWORD:-}}"' >> /app/start.sh && \
    echo 'if [ -n "$DATABASE_URL" ]; then' >> /app/start.sh && \
    echo '  RAW_URL="$DATABASE_URL"' >> /app/start.sh && \
    echo '  # Normalize to mysql:// if jdbc given for parsing' >> /app/start.sh && \
    echo '  TMP_URL="$RAW_URL"' >> /app/start.sh && \
    echo '  TMP_URL="${TMP_URL#jdbc:}"' >> /app/start.sh && \
    echo '  # Extract creds and host parts: mysql://user:pass@host:port/db' >> /app/start.sh && \
    echo '  PROTO_REMOVED="${TMP_URL#mysql://}"' >> /app/start.sh && \
    echo '  CREDS_PART="${PROTO_REMOVED%@*}"' >> /app/start.sh && \
    echo '  HOSTPATH_PART="${PROTO_REMOVED#*@}"' >> /app/start.sh && \
    echo '  if [[ "$RAW_URL" == *"@"* ]]; then' >> /app/start.sh && \
    echo '    FLYWAY_USER="${FLYWAY_USER:-${CREDS_PART%%:*}}"' >> /app/start.sh && \
    echo '    FLYWAY_PASSWORD="${FLYWAY_PASSWORD:-${CREDS_PART#*:}}"' >> /app/start.sh && \
    echo '  fi' >> /app/start.sh && \
    echo '  # Split host:port/db' >> /app/start.sh && \
    echo '  HOSTPORT="${HOSTPATH_PART%%/*}"' >> /app/start.sh && \
    echo '  DBNAME="${HOSTPATH_PART#*/}"' >> /app/start.sh && \
    echo '  HOSTNAME="${HOSTPORT%%:*}"' >> /app/start.sh && \
    echo '  PORTPART="${HOSTPORT#*:}"' >> /app/start.sh && \
    echo '  if [ "$HOSTPORT" = "$PORTPART" ]; then PORTPART="3306"; fi' >> /app/start.sh && \
    echo '  DB_URL="jdbc:mysql://${HOSTNAME}:${PORTPART}/${DBNAME}"' >> /app/start.sh && \
    echo 'else' >> /app/start.sh && \
    echo '  DB_URL="jdbc:mysql://${MYSQLHOST}:${MYSQLPORT:-3306}/${MYSQLDATABASE}"' >> /app/start.sh && \
    echo 'fi' >> /app/start.sh && \
    echo '' >> /app/start.sh && \
    echo 'echo "Running Flyway repair against: $DB_URL"' >> /app/start.sh && \
    echo '/app/flyway/flyway -url="$DB_URL" -user="$FLYWAY_USER" -password="$FLYWAY_PASSWORD" -locations=filesystem:/app/migrations repair || true' >> /app/start.sh && \
    echo '' >> /app/start.sh && \
    echo '# Start the Spring Boot application' >> /app/start.sh && \
    echo 'java -Dserver.port=$PORT -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE -jar app.jar' >> /app/start.sh && \
    chmod +x /app/start.sh

# Run the entrypoint script
ENTRYPOINT ["/app/start.sh"]
