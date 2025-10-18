# Railway Environment Variables Setup Guide

## Required Environment Variables for Railway Deployment

### Database Configuration
- `DATABASE_URL`: Your Railway MySQL database URL (automatically provided by Railway when you add MySQL service)
- `DB_USERNAME`: Database username (usually provided by Railway)
- `DB_PASSWORD`: Database password (usually provided by Railway)
- `DB_DRIVER`: Set to `com.mysql.cj.jdbc.Driver` for MySQL

### JWT Configuration
- `SECURITY_JWT_SECRET`: Your JWT secret key (generate a strong random string)
- `JWT_EXPIRATION`: JWT expiration time in milliseconds (e.g., 86400000 for 24 hours)
- `JWT_EXPIRATION_MS`: Same as JWT_EXPIRATION

### Socket.IO Configuration
- `SOCKETIO_HOST`: Set to `0.0.0.0`
- `SOCKETIO_PORT`: Set to `9092`
- `SOCKETIO_ALLOWED_ORIGINS`: Set to `*` or your frontend domain

### AWS S3 Configuration (if using file uploads)
- `AWS_S3_BUCKET`: Your S3 bucket name
- `AWS_S3_REGION`: Your S3 region (e.g., ap-northeast-2)
- `AWS_S3_ACCESS_KEY`: Your AWS access key
- `AWS_S3_SECRET_KEY`: Your AWS secret key
- `AWS_S3_ENDPOINT`: S3 endpoint URL
- `AWS_S3_BASE_URL`: Base URL for your S3 bucket

### File Upload Configuration
- `MAX_FILE_SIZE`: Maximum file size (e.g., 10MB)
- `MAX_REQUEST_SIZE`: Maximum request size (e.g., 10MB)

### Spring Profile
- `SPRING_PROFILES_ACTIVE`: Set to `railway`

## How to Set Environment Variables in Railway

1. Go to your Railway project dashboard
2. Click on your service
3. Go to the "Variables" tab
4. Add each environment variable with its value
5. Click "Deploy" to apply changes

## Quick Setup Commands

You can use these commands to generate JWT secrets:

```bash
# Generate JWT secret (32 characters)
openssl rand -base64 32

# Or use this script if available
./generate-jwt-secrets.sh
```

## Database Setup

1. Add MySQL service to your Railway project
2. Railway will automatically provide DATABASE_URL, DB_USERNAME, and DB_PASSWORD
3. Set DB_DRIVER to `com.mysql.cj.jdbc.Driver`

## Testing Health Check

After deployment, you can test the health check endpoint:
- `/actuator/health` - Spring Boot actuator health check
- `/api/health` - Custom health check endpoint

Both should return status "UP" when the application is running correctly.
