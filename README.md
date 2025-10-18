# Morago Backend

A Spring Boot application for managing interpretation services with role-based access control.

## 🚀 Quick Start

1. **Set up environment variables:**
   ```bash
   cp env.example .env
   # Edit .env with your actual credentials
   ```

2. **Generate JWT secrets:**
   ```bash
   ./generate-jwt-secrets.sh
   ```

3. **Run the application:**
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=local
   ```

## 🔐 Security Configuration

**IMPORTANT:** This application uses environment variables for all sensitive configuration. Never commit real credentials to version control.

### Required Environment Variables

- `DB_URL` - Database connection URL
- `DB_USERNAME` - Database username
- `DB_PASSWORD` - Database password
- `SECURITY_JWT_SECRET` - JWT signing secret (base64-encoded)
- `jwt.secret` - JWT secret for Spring Security
- `CORS_ALLOWED_ORIGINS` - Allowed CORS origins
- `SOCKETIO_HOST` - Socket.IO server host
- `SOCKETIO_PORT` - Socket.IO server port
- `SOCKETIO_ALLOWED_ORIGINS` - Socket.IO allowed origins

### Security Features

- **Role-Based Access Control (RBAC)** with three roles:
  - `CLIENT` - Users requesting interpretation services
  - `INTERPRETER` - Users providing interpretation services  
  - `ADMINISTRATOR` - System administrators with full access

- **JWT Authentication** with configurable expiration
- **CORS Protection** with configurable allowed origins
- **Environment-based Configuration** for different deployment stages

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/morago_backend/
│   │   ├── auth/           # Authentication services
│   │   ├── config/         # Configuration classes
│   │   ├── controller/     # REST controllers
│   │   ├── dto/           # Data Transfer Objects
│   │   ├── entity/        # JPA entities
│   │   ├── repository/    # Data repositories
│   │   ├── security/      # Security configuration
│   │   ├── service/       # Business logic services
│   │   └── signaling/     # WebRTC signaling
│   └── resources/
│       ├── application*.properties  # Environment-specific configs
│       └── db/migration/           # Database migrations
└── test/                  # Test files
```

## 🛠️ Development

### Prerequisites

- Java 21+
- Maven 3.6+
- MySQL 8.0+
- Node.js (for frontend development)

### Running Tests

```bash
mvn test
```

### Building

```bash
mvn clean package
```

## 🔧 Configuration

### Application Profiles

- `local` - Local development (default)
- `dev` - Development environment
- `prod` - Production environment

### Database Configuration

The application uses Flyway for database migrations. Migrations are located in `src/main/resources/db/migration/`.

### Security Configuration

Security is configured in `SecurityConfig.java` with:
- JWT-based authentication
- Role-based authorization
- CORS configuration
- Method-level security

## 📚 API Documentation

API documentation is available via Swagger UI when the application is running:
- Local: http://localhost:8080/swagger-ui.html

## 🚨 Security Best Practices

1. **Never commit credentials** to version control
2. **Use different secrets** for each environment
3. **Rotate secrets regularly** (especially JWT secrets)
4. **Use strong passwords** for database access
5. **Monitor access logs** for suspicious activity
6. **Keep dependencies updated** for security patches

## 📖 Additional Documentation

- [Environment Setup Guide](ENVIRONMENT_SETUP.md)
- [RBAC Documentation](RBAC_README.md)
- [Pagination & Filtering](PAGINATION_FILTERING_README.md)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Ensure all tests pass
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License.
