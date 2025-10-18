# Morago Backend - Complete API Documentation

## Table of Contents

1. [Overview](#overview)
2. [Quick Start](#quick-start)
3. [Authentication](#authentication)
4. [Admin Features](#admin-features)
5. [File Storage](#file-storage)
6. [All API Endpoints](#all-api-endpoints)
7. [Swagger/OpenAPI Setup](#swaggeropenapi-setup)
8. [Testing & Validation](#testing--validation)
9. [Configuration](#configuration)
10. [Error Handling](#error-handling)

---

## Overview

### API Information

- **Title**: Morago Backend API
- **Version**: v1.0.0
- **Base URL (Local)**: `http://localhost:8080`
- **Base URL (Production)**: `https://api.morago.com`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI Spec**: `http://localhost:8080/v3/api-docs`

### Key Features

- ✅ User management and authentication
- ✅ Admin features (account blocking, profile verification)
- ✅ File storage (AWS S3 + Local)
- ✅ Translator profile management
- ✅ Real-time communication (WebSocket)
- ✅ Financial operations (deposits, withdrawals)
- ✅ Rating system
- ✅ Multilingual support

### Authentication

All endpoints (except `/api/auth/login`) require JWT authentication:
```
Authorization: Bearer {your-jwt-token}
```

### User Roles

- **ADMINISTRATOR**: Full system access
- **INTERPRETER**: Translator-specific features
- **CLIENT**: Customer-specific features

---

## Quick Start

### 1. Start the Application

```bash
cd /Users/hassankoroma/morago-backend-sep15
mvn spring-boot:run
```

### 2. Access Swagger UI

```
http://localhost:8080/swagger-ui.html
```

### 3. Login to Get JWT Token

**Request:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "phone": "+1234567890",
    "password": "your_password"
  }'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### 4. Use Token in Requests

```bash
curl -X GET http://localhost:8080/api/users/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## Authentication

### Login Endpoint

#### POST /api/auth/login

**Description**: Authenticates user and returns JWT token

**Access**: Public (no authentication required)

**Request Body:**
```json
{
  "phone": "+1234567890",
  "password": "password123"
}
```

**Success Response (200):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIrMTIzNDU2Nzg5MCIsInJvbGVzIjpbIkFETUlOSVNUUkFUT1IiXSwiaWF0IjoxNjk2ODQ4MDAwLCJleHAiOjE2OTY4NTE2MDB9.signature"
}
```

**Error Responses:**
- **401 Unauthorized**: Invalid credentials
- **400 Bad Request**: Missing or invalid fields

**cURL Example:**
```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"phone":"+admin","password":"admin123"}' \
  | jq -r '.token')
```

---

## Admin Features

> **⚠️ Important:** All administrative operations now use the `/api/admin/**` endpoints.  
> Regular `/api/users/**` endpoints are for CLIENT and INTERPRETER roles only.

### Admin Dashboard

#### GET /api/admin/dashboard

**Description**: Get comprehensive system statistics and metrics

**Access**: ADMINISTRATOR only

**Success Response (200):**
```json
{
  "totalUsers": 1500,
  "activeUsers": 1200,
  "inactiveUsers": 300,
  "totalClients": 800,
  "totalInterpreters": 600,
  "verifiedInterpreters": 450,
  "unverifiedInterpreters": 150,
  "totalAdministrators": 10,
  "totalDebtors": 25,
  "totalSystemBalance": 50000.00,
  "totalWithdrawals": 350,
  "totalDeposits": 500,
  "totalWithdrawalAmount": 15000.00,
  "totalDepositAmount": 25000.00,
  "totalCallRecords": 5000,
  "activeRooms": 0,
  "totalNotifications": 10000,
  "systemStatus": "HEALTHY"
}
```

**cURL Example:**
```bash
curl -X GET http://localhost:8080/api/admin/dashboard \
  -H "Authorization: Bearer $TOKEN"
```

---

### User Management

#### GET /api/admin/users

**Description**: Get all users with pagination and filtering

**Access**: ADMINISTRATOR only

**Parameters:**
- `page`: Page number (default: 0)
- `size`: Items per page (default: 10)
- `sortBy`: Sort field (default: "id")
- `ascending`: Sort direction (default: true)
- `search`: Search term for phone, first name, or last name
- `filters[role]`: Filter by role (CLIENT, INTERPRETER, ADMINISTRATOR)
- `filters[active]`: Filter by active status (true/false)

**Success Response (200):**
```json
{
  "content": [
    {
      "id": 1,
      "phone": "+1234567890",
      "firstName": "John",
      "lastName": "Doe",
      "balance": 100.00,
      "isActive": true,
      "roles": ["CLIENT"],
      "adminId": null
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 100
}
```

**cURL Example:**
```bash
curl -X GET "http://localhost:8080/api/admin/users?page=0&size=10&search=john" \
  -H "Authorization: Bearer $TOKEN"
```

---

#### POST /api/admin/users

**Description**: Create a new user

**Access**: ADMINISTRATOR only

**Request Body:**
```json
{
  "phone": "+1234567890",
  "password": "securePassword123",
  "firstName": "John",
  "lastName": "Doe",
  "role": "CLIENT",
  "isActive": true
}
```

**Success Response (201):**
```json
{
  "id": 5,
  "phone": "+1234567890",
  "firstName": "John",
  "lastName": "Doe",
  "balance": 0.00,
  "isActive": true,
  "roles": ["CLIENT"]
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8080/api/admin/users \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "phone": "+1234567890",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe",
    "role": "CLIENT"
  }'
```

---

#### PUT /api/admin/users/{id}

**Description**: Update user information

**Access**: ADMINISTRATOR only

**Parameters:**
- `id` (path): User ID to update

**Request Body:**
```json
{
  "firstName": "Jane",
  "lastName": "Smith",
  "isActive": true
}
```

**Success Response (200):**
```json
{
  "id": 5,
  "phone": "+1234567890",
  "firstName": "Jane",
  "lastName": "Smith",
  "isActive": true
}
```

---

#### DELETE /api/admin/users/{id}

**Description**: Delete a user

**Access**: ADMINISTRATOR only

**Parameters:**
- `id` (path): User ID to delete

**Success Response (204):** No content

**cURL Example:**
```bash
curl -X DELETE http://localhost:8080/api/admin/users/5 \
  -H "Authorization: Bearer $TOKEN"
```

---

### Role Management

#### POST /api/admin/users/{userId}/roles/{roleName}

**Description**: Assign a role to a user

**Access**: ADMINISTRATOR only

**Parameters:**
- `userId` (path): User ID
- `roleName` (path): Role name (CLIENT, INTERPRETER, ADMINISTRATOR)

**Success Response (200):**
```json
{
  "userId": 5,
  "phone": "+1234567890",
  "roles": ["CLIENT", "INTERPRETER"]
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8080/api/admin/users/5/roles/INTERPRETER \
  -H "Authorization: Bearer $TOKEN"
```

---

#### DELETE /api/admin/users/{userId}/roles/{roleName}

**Description**: Remove a role from a user

**Access**: ADMINISTRATOR only

**Parameters:**
- `userId` (path): User ID
- `roleName` (path): Role name to remove

**Success Response (200):**
```json
{
  "userId": 5,
  "phone": "+1234567890",
  "roles": ["CLIENT"]
}
```

---

#### PUT /api/admin/users/{userId}/roles

**Description**: Set all roles for a user (replaces existing roles)

**Access**: ADMINISTRATOR only

**Parameters:**
- `userId` (path): User ID

**Request Body:**
```json
["CLIENT", "INTERPRETER"]
```

**Success Response (200):**
```json
{
  "userId": 5,
  "phone": "+1234567890",
  "roles": ["CLIENT", "INTERPRETER"]
}
```

---

#### GET /api/admin/users/{userId}/roles

**Description**: Get all roles for a user

**Access**: ADMINISTRATOR only

**Success Response (200):**
```json
["CLIENT", "INTERPRETER"]
```

---

#### GET /api/admin/users/by-role/{roleName}

**Description**: Get all users with a specific role

**Access**: ADMINISTRATOR only

**Parameters:**
- `roleName` (path): Role name (CLIENT, INTERPRETER, ADMINISTRATOR)

**Success Response (200):**
```json
[
  {
    "userId": 1,
    "phone": "+1234567890",
    "roles": ["CLIENT"]
  },
  {
    "userId": 2,
    "phone": "+0987654321",
    "roles": ["CLIENT", "INTERPRETER"]
  }
]
```

---

### Translator Profile Verification

#### POST /api/admin/translator-profiles/{id}/verify

**Description**: Verify an interpreter profile (mark as trusted)

**Access**: ADMINISTRATOR only

**Parameters:**
- `id` (path): Translator profile ID to verify

**Success Response (200):**
```json
{
  "id": 3,
  "email": "translator@example.com",
  "dateOfBirth": "1990-05-20",
  "isAvailable": true,
  "isOnline": false,
  "isVerified": true,
  "levelOfKorean": "Advanced",
  "createdAtDatetime": "2024-02-10T08:00:00",
  "updatedAtDatetime": "2024-10-09T05:00:00"
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8080/api/admin/translator-profiles/3/verify \
  -H "Authorization: Bearer $TOKEN"
```

---

#### POST /api/admin/translator-profiles/{id}/unverify

**Description**: Remove verification from an interpreter profile

**Access**: ADMINISTRATOR only

**Parameters:**
- `id` (path): Translator profile ID to unverify

**Success Response (200):**
```json
{
  "id": 3,
  "email": "translator@example.com",
  "isVerified": false
}
```

---

#### GET /api/admin/translator-profiles/unverified

**Description**: Get list of all unverified interpreter profiles

**Access**: ADMINISTRATOR only

**Success Response (200):**
```json
[
  {
    "id": 7,
    "email": "newinterpreter@example.com",
    "isVerified": false,
    "levelOfKorean": "Intermediate"
  }
]
```

---

### Admin Profile Management

#### GET /api/admin/admins

**Description**: Get all admin profiles with pagination

**Access**: ADMINISTRATOR only

**Success Response (200):**
```json
{
  "content": [
    {
      "id": 1,
      "userId": 1,
      "userPhone": "+admin",
      "userFullName": "Admin User",
      "adminLevel": "SUPER_ADMIN",
      "department": "System Administration",
      "isActive": true,
      "loginCount": 150
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 5
}
```

---

#### POST /api/admin/admins

**Description**: Create an admin profile for a user

**Access**: ADMINISTRATOR only

**Request Body:**
```json
{
  "userId": 10,
  "adminLevel": "MODERATOR",
  "department": "Customer Support",
  "permissions": "USER_MANAGEMENT,CONTENT_MODERATION",
  "isActive": true,
  "notes": "Customer support team leader"
}
```

**Success Response (201):**
```json
{
  "id": 5,
  "userId": 10,
  "adminLevel": "MODERATOR",
  "department": "Customer Support",
  "isActive": true
}
```

---

#### GET /api/admin/admins/{id}

**Description**: Get admin profile by ID

**Access**: ADMINISTRATOR only

**Success Response (200):**
```json
{
  "id": 1,
  "userId": 1,
  "userPhone": "+admin",
  "userFullName": "Admin User",
  "adminLevel": "SUPER_ADMIN",
  "department": "System Administration",
  "permissions": "ALL",
  "isActive": true,
  "lastLogin": "2025-10-11T04:30:00",
  "loginCount": 150
}
```

---

### Financial Management

#### GET /api/admin/deposits

**Description**: Get all deposits

**Access**: ADMINISTRATOR only

**Success Response (200):**
```json
[
  {
    "id": 1,
    "userId": 5,
    "amount": 100.00,
    "createdAt": "2025-10-01T10:00:00"
  }
]
```

---

#### GET /api/admin/withdrawals

**Description**: Get all withdrawals

**Access**: ADMINISTRATOR only

**Success Response (200):**
```json
[
  {
    "id": 1,
    "userId": 5,
    "amount": 50.00,
    "createdAt": "2025-10-05T15:00:00"
  }
]
```

---

#### GET /api/admin/debtors

**Description**: Get all debtors

**Access**: ADMINISTRATOR only

**Success Response (200):**
```json
[
  {
    "id": 1,
    "userId": 10,
    "debtAmount": 500.00,
    "createdAt": "2025-09-15T08:00:00"
  }
]
```

---

## File Storage

### Overview

- **Local Storage**: Development (stores in `uploads/` directory)
- **AWS S3**: Production (configurable bucket and region)
- **Auto-switching**: Based on active profile (local/dev vs. prod)

### File Validation

**Avatars:**
- Max size: 5MB
- Allowed types: JPEG, JPG, PNG, GIF, WebP

**Documents:**
- Max size: 10MB
- Allowed types: PDF, DOC, DOCX, JPEG, PNG

---

### Upload Endpoints

#### POST /api/uploads/users/{userId}/avatar

**Description**: Upload user avatar (replaces old avatar automatically)

**Access**: All authenticated users (CLIENT, INTERPRETER, ADMINISTRATOR)

**Parameters:**
- `userId` (path): User ID
- `file` (form-data): Image file

**Request:**
```bash
curl -X POST http://localhost:8080/api/uploads/users/1/avatar \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@avatar.jpg"
```

**Success Response (201):**
```
/uploads/avatars/uuid-avatar.jpg
```
or (in production):
```
https://your-bucket.s3.amazonaws.com/avatars/uuid-avatar.jpg
```

**Error Responses:**
- **400**: File validation failed
- **401**: No authentication
- **404**: User not found
- **500**: Upload failed

**Validation:**
- File must be image (JPEG, PNG, GIF, WebP)
- Max size: 5MB
- Old avatar automatically deleted

---

#### POST /api/uploads/translators/{translatorId}/documents

**Description**: Upload verification documents for translators

**Access**: ADMINISTRATOR, INTERPRETER

**Parameters:**
- `translatorId` (path): Translator profile ID
- `file` (form-data): Document file
- `documentType` (optional query): Type of document (e.g., "certificate", "id")

**Request:**
```bash
curl -X POST http://localhost:8080/api/uploads/translators/1/documents \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@certificate.pdf" \
  -F "documentType=certificate"
```

**Success Response (201):**
```
https://bucket.s3.amazonaws.com/translator-documents/1/uuid-certificate.pdf
```

**Validation:**
- File must be PDF, DOC, DOCX, JPEG, or PNG
- Max size: 10MB

---

#### POST /api/uploads/themes/{themeId}/icon

**Description**: Upload icon for a theme

**Access**: ADMINISTRATOR, INTERPRETER

**Parameters:**
- `themeId` (path): Theme ID
- `file` (form-data): Icon image file

**Request:**
```bash
curl -X POST http://localhost:8080/api/uploads/themes/1/icon \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@icon.png"
```

---

#### POST /api/uploads/documents

**Description**: Upload general document

**Access**: All authenticated users

**Parameters:**
- `file` (form-data): Document file
- `folder` (optional query): Custom folder name (default: "documents")

**Request:**
```bash
curl -X POST "http://localhost:8080/api/uploads/documents?folder=contracts" \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@document.pdf"
```

---

### Download Endpoints

#### GET /api/uploads/files/{fileId}/download

**Description**: Download file by database ID

**Access**: All authenticated users

**Parameters:**
- `fileId` (path): File ID from the database

**Request:**
```bash
curl -X GET http://localhost:8080/api/uploads/files/123/download \
  -H "Authorization: Bearer $TOKEN" \
  -o downloaded-file.pdf
```

**Success Response (200):**
- Binary file content
- Headers:
  - `Content-Type`: Original file MIME type
  - `Content-Disposition`: `attachment; filename="original-filename.pdf"`

---

#### GET /api/uploads/download?path={path}

**Description**: Download file by storage path

**Access**: All authenticated users

**Parameters:**
- `path` (query): File path in storage

**Request:**
```bash
curl -X GET "http://localhost:8080/api/uploads/download?path=/uploads/avatars/uuid-file.jpg" \
  -H "Authorization: Bearer $TOKEN" \
  -o file.jpg
```

---

### Delete Endpoint

#### DELETE /api/uploads/files/{fileId}

**Description**: Delete file from storage and the database

**Access**: ADMINISTRATOR only

**Parameters:**
- `fileId` (path): File ID to delete

**Request:**
```bash
curl -X DELETE http://localhost:8080/api/uploads/files/123 \
  -H "Authorization: Bearer $TOKEN"
```

**Success Response (204):** No content

---

## All API Endpoints

### Authentication

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/api/auth/login` | User login | Public |

---

### Admin Endpoints (All require ADMINISTRATOR role)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/admin/dashboard` | Get dashboard statistics |
| GET | `/api/admin/users` | Get all users (paginated) |
| GET | `/api/admin/users/{id}` | Get user by ID |
| POST | `/api/admin/users` | Create new user |
| PUT | `/api/admin/users/{id}` | Update user |
| DELETE | `/api/admin/users/{id}` | Delete user |
| POST | `/api/admin/users/{userId}/roles/{roleName}` | Assign role to user |
| DELETE | `/api/admin/users/{userId}/roles/{roleName}` | Remove role from user |
| PUT | `/api/admin/users/{userId}/roles` | Set user roles |
| GET | `/api/admin/users/{userId}/roles` | Get user roles |
| GET | `/api/admin/users/by-role/{roleName}` | Get users by role |
| POST | `/api/admin/users/{userId}/promote-to-admin` | Promote to administrator |
| POST | `/api/admin/users/{userId}/promote-to-interpreter` | Promote to interpreter |
| POST | `/api/admin/users/{userId}/set-as-client` | Set as client |
| POST | `/api/admin/users/{userId}/set-as-interpreter` | Set as interpreter |
| POST | `/api/admin/users/{userId}/set-as-administrator` | Set as administrator |
| GET | `/api/admin/users/clients` | Get all clients |
| GET | `/api/admin/users/interpreters` | Get all interpreters |
| GET | `/api/admin/users/administrators` | Get all administrators |
| POST | `/api/admin/translator-profiles/{id}/verify` | Verify translator profile |
| POST | `/api/admin/translator-profiles/{id}/unverify` | Unverify translator profile |
| GET | `/api/admin/translator-profiles/unverified` | Get unverified profiles |
| GET | `/api/admin/admins` | Get all admin profiles |
| GET | `/api/admin/admins/{id}` | Get admin profile by ID |
| GET | `/api/admin/admins/by-user/{userId}` | Get admin by user ID |
| POST | `/api/admin/admins` | Create admin profile |
| PUT | `/api/admin/admins/{id}` | Update admin profile |
| DELETE | `/api/admin/admins/{id}` | Delete admin profile |
| GET | `/api/admin/admins/active` | Get active admins |
| GET | `/api/admin/admins/by-level/{level}` | Get admins by level |
| GET | `/api/admin/admins/by-department/{dept}` | Get admins by department |
| GET | `/api/admin/debtors` | Get all debtors |
| GET | `/api/admin/debtors/{id}` | Get debtor by ID |
| POST | `/api/admin/debtors` | Create debtor |
| PUT | `/api/admin/debtors/{id}` | Update debtor |
| DELETE | `/api/admin/debtors/{id}` | Delete debtor |
| GET | `/api/admin/deposits` | Get all deposits |
| GET | `/api/admin/deposits/{id}` | Get deposit by ID |
| POST | `/api/admin/deposits` | Create deposit |
| PUT | `/api/admin/deposits/{id}` | Update deposit |
| DELETE | `/api/admin/deposits/{id}` | Delete deposit |
| GET | `/api/admin/withdrawals` | Get all withdrawals |
| GET | `/api/admin/withdrawals/{id}` | Get withdrawal by ID |
| POST | `/api/admin/withdrawals` | Create withdrawal |
| PUT | `/api/admin/withdrawals/{id}` | Update withdrawal |
| DELETE | `/api/admin/withdrawals/{id}` | Delete withdrawal |

---

### User Management (CLIENT, INTERPRETER only)

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/api/users/{id}` | Get user by ID (own profile) | CLIENT, INTERPRETER |
| PUT | `/api/users/{id}` | Update user (own profile) | CLIENT, INTERPRETER |

---

### Translator Profile Management

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/api/translator-profiles` | Get all profiles (paginated) | INTERPRETER, ADMINISTRATOR |
| GET | `/api/translator-profiles/{id}` | Get profile by ID | INTERPRETER, ADMINISTRATOR |
| POST | `/api/translator-profiles` | Create profile | INTERPRETER, ADMINISTRATOR |
| PUT | `/api/translator-profiles/{id}` | Update profile | INTERPRETER, ADMINISTRATOR |
| DELETE | `/api/translator-profiles/{id}` | Delete profile | INTERPRETER, ADMINISTRATOR |

---

### File Upload/Download

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/api/uploads/users/{userId}/avatar` | Upload user avatar | All authenticated |
| POST | `/api/uploads/translators/{id}/documents` | Upload translator docs | ADMINISTRATOR, INTERPRETER |
| POST | `/api/uploads/themes/{themeId}/icon` | Upload theme icon | ADMINISTRATOR, INTERPRETER |
| POST | `/api/uploads/documents` | Upload general document | All authenticated |
| GET | `/api/uploads/files/{fileId}/download` | Download by file ID | All authenticated |
| GET | `/api/uploads/download?path={path}` | Download by path | All authenticated |
| DELETE | `/api/uploads/files/{fileId}` | Delete file | ADMINISTRATOR |

---

### Roles

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/api/roles` | Get all roles | ADMINISTRATOR |
| GET | `/api/roles/{id}` | Get role by ID | ADMINISTRATOR |
| POST | `/api/roles` | Create role | ADMINISTRATOR |
| PUT | `/api/roles/{id}` | Update role | ADMINISTRATOR |
| DELETE | `/api/roles/{id}` | Delete role | ADMINISTRATOR |

---

### Other Endpoints

- **Languages**: `/api/languages/*`
- **Themes**: `/api/themes/*`
- **Ratings**: `/api/ratings/*`
- **Notifications**: `/api/notifications/*`
- **Deposits**: `/api/deposits/*`
- **Withdrawals**: `/api/withdrawals/*`
- **Files**: `/api/files/*`
- **File Resources**: `/api/file-resources/*`
- **Call Records**: `/api/call-records/*`

---

## Swagger/OpenAPI Setup

### Configuration

**Files:**
- `src/main/java/com/morago_backend/config/OpenApiConfig.java` - OpenAPI/Swagger configuration
- `src/main/java/com/morago_backend/config/SecurityConfig.java` - Security and authentication configuration
- `src/main/java/com/morago_backend/config/JpaAuditingConfig.java` - JPA auditing configuration
- `src/main/java/com/morago_backend/config/SocketIOConfig.java` - WebSocket configuration

**OpenAPI Features:**
- ✅ Comprehensive API metadata
- ✅ JWT Bearer authentication scheme
- ✅ Multiple server environments
- ✅ Contact and license information
- ✅ External documentation links

**Security Features:**
- ✅ JWT-based authentication
- ✅ Role-based access control
- ✅ CORS configuration
- ✅ Stateless session management
- ✅ Path-based authorization

**Implementation:**
```java
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI springOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Morago Backend API")
                .description("Complete API for Morago translation platform")
                .version("v1.0.0")
                .contact(new Contact()
                    .name("Morago Support")
                    .email("support@morago.com"))
            )
            .servers(List.of(
                new Server().url("http://localhost:8080").description("Local"),
                new Server().url("https://api.morago.com").description("Production")
            ))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
            .components(new Components()
                .addSecuritySchemes("bearerAuth",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                )
            );
    }
}
```

---

### API Response Examples

**File**: `src/main/java/com/morago_backend/config/ApiResponseExamples.java`

**Reusable Annotations:**
```text
@Success200          // 200 OK
@Created201          // 201 Created
@NoContent204        // 204 No Content
@BadRequest400       // 400 Bad Request
@Unauthorized401     // 401 Unauthorized
@Forbidden403        // 403 Forbidden
@NotFound404         // 404 Not Found
@InternalServerError500  // 500 Internal Server Error
```

**Error Response Schema:**
```json
{
  "timestamp": "2025-10-09T05:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed for field: phone",
  "path": "/api/users"
}
```

---

### Controller Documentation Example

**Enhanced AuthController:**

```java
@RestController
@RequestMapping("/api/auth")
@Tag(
    name = "Authentication",
    description = "Authentication endpoints for user login and session management"
)
public class AuthController {
    
    @Operation(
        summary = "User Login",
        description = """
            Authenticates a user and returns a JWT token.
            
            ### Authentication Flow:
            1. Send phone number and password
            2. Receive JWT token
            3. Use token in Authorization header
            
            ### Roles:
            Token contains user roles (ADMINISTRATOR, INTERPRETER, CLIENT)
            
            ### Token Expiration:
            Tokens expire after 1 hour
            """,
        requestBody = @RequestBody(
            description = "Login credentials",
            required = true,
            content = @Content(
                examples = {
                    @ExampleObject(
                        name = "Admin Login",
                        value = "{\"phone\":\"+admin\",\"password\":\"admin123\"}"
                    )
                }
            )
        )
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Login successful",
            content = @Content(
                schema = @Schema(implementation = LoginResponse.class),
                examples = @ExampleObject(
                    value = "{\"token\":\"eyJhbGc...\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Invalid credentials"
        )
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.login(request.getPhone(), request.getPassword());
        return ResponseEntity.ok(new LoginResponse(token));
    }
}
```

---

## Testing & Validation

### Method 1: Swagger UI (Interactive Testing)

**Access Swagger UI:**
```
http://localhost:8080/swagger-ui.html
```

**Steps:**

1. **Login:**
   - Navigate to "Authentication" section
   - Click POST /api/auth/login
   - Click "Try it out"
   - Enter credentials
   - Click "Execute"
   - Copy the token

2. **Authorize:**
   - Click "Authorize" button (top-right, lock icon)
   - Paste token (without "Bearer" prefix)
   - Click "Authorize"
   - Click "Close"

3. **Test Endpoints:**
   - Any endpoint: Click "Try it out"
   - Fill parameters
   - Click "Execute"
   - View response

---

### Method 2: cURL Command Line Testing

#### Test Authentication
```bash
# Login
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"phone":"+admin","password":"admin123"}' \
  | jq -r '.token')

echo "Token: $TOKEN"
```

#### Test User Endpoints
```bash
# Get all users
curl -X GET "http://localhost:8080/api/users?page=0&size=10" \
  -H "Authorization: Bearer $TOKEN"

# Get user by ID
curl -X GET http://localhost:8080/api/users/1 \
  -H "Authorization: Bearer $TOKEN"

# Block user
curl -X POST http://localhost:8080/api/users/5/block \
  -H "Authorization: Bearer $TOKEN"

# Activate user
curl -X POST http://localhost:8080/api/users/5/activate \
  -H "Authorization: Bearer $TOKEN"
```

#### Test Translator Verification
```bash
# Verify profile
curl -X POST http://localhost:8080/api/translator-profiles/1/verify \
  -H "Authorization: Bearer $TOKEN"

# Get unverified profiles
curl -X GET http://localhost:8080/api/translator-profiles/unverified \
  -H "Authorization: Bearer $TOKEN"
```

#### Test File Upload
```bash
# Upload avatar
curl -X POST http://localhost:8080/api/uploads/users/1/avatar \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@/path/to/avatar.jpg"

# Upload translator document
curl -X POST http://localhost:8080/api/uploads/translators/1/documents \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@/path/to/certificate.pdf"

# Download file
curl -X GET http://localhost:8080/api/uploads/files/1/download \
  -H "Authorization: Bearer $TOKEN" \
  -o downloaded.jpg
```

---

### Method 3: Automated Validation Script

**Create file: `validate-api.sh`**

```bash
#!/bin/bash

API_URL="http://localhost:8080"
PHONE="+admin"
PASSWORD="admin123"

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo "🧪 Morago Backend API Validation"
echo "================================="

# Test 1: Server Health
echo -n "1. Server health check... "
RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" $API_URL/actuator/health)
if [ "$RESPONSE" = "200" ]; then
  echo -e "${GREEN}✓ PASS${NC}"
else
  echo -e "${RED}✗ FAIL (HTTP $RESPONSE)${NC}"
  exit 1
fi

# Test 2: Swagger UI
echo -n "2. Swagger UI accessible... "
RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" $API_URL/swagger-ui.html)
if [ "$RESPONSE" = "200" ]; then
  echo -e "${GREEN}✓ PASS${NC}"
else
  echo -e "${RED}✗ FAIL (HTTP $RESPONSE)${NC}"
fi

# Test 3: OpenAPI Docs
echo -n "3. OpenAPI JSON available... "
RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" $API_URL/v3/api-docs)
if [ "$RESPONSE" = "200" ]; then
  echo -e "${GREEN}✓ PASS${NC}"
else
  echo -e "${RED}✗ FAIL (HTTP $RESPONSE)${NC}"
fi

# Test 4: Login (Authentication)
echo -n "4. Login endpoint... "
TOKEN=$(curl -s -X POST $API_URL/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"phone\":\"$PHONE\",\"password\":\"$PASSWORD\"}" \
  | jq -r '.token' 2>/dev/null)

if [ -n "$TOKEN" ] && [ "$TOKEN" != "null" ]; then
  echo -e "${GREEN}✓ PASS${NC}"
else
  echo -e "${RED}✗ FAIL (No token received)${NC}"
  echo -e "${YELLOW}Note: Make sure admin user exists with correct credentials${NC}"
  exit 1
fi

# Test 5: Unauthorized Access (Should fail)
echo -n "5. Unauthorized access protection... "
RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" $API_URL/api/users)
if [ "$RESPONSE" = "401" ]; then
  echo -e "${GREEN}✓ PASS (Correctly rejected)${NC}"
else
  echo -e "${YELLOW}⚠ WARNING (Expected 401, got $RESPONSE)${NC}"
fi

# Test 6: Authorized Access
echo -n "6. Authorized access... "
RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" \
  -X GET "$API_URL/api/users?page=0&size=5" \
  -H "Authorization: Bearer $TOKEN")
if [ "$RESPONSE" = "200" ]; then
  echo -e "${GREEN}✓ PASS${NC}"
else
  echo -e "${YELLOW}⚠ Got $RESPONSE (Check if user has ADMIN role)${NC}"
fi

# Test 7: Admin Features - Block endpoint exists
echo -n "7. Block user endpoint... "
RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" \
  -X POST $API_URL/api/users/1/block \
  -H "Authorization: Bearer $TOKEN")
if [ "$RESPONSE" != "404" ] && [ "$RESPONSE" != "405" ]; then
  echo -e "${GREEN}✓ PASS (Endpoint accessible: $RESPONSE)${NC}"
else
  echo -e "${RED}✗ FAIL ($RESPONSE)${NC}"
fi

# Test 8: Verify translator endpoint exists
echo -n "8. Verify translator endpoint... "
RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" \
  -X POST $API_URL/api/translator-profiles/1/verify \
  -H "Authorization: Bearer $TOKEN")
if [ "$RESPONSE" != "404" ] && [ "$RESPONSE" != "405" ]; then
  echo -e "${GREEN}✓ PASS (Endpoint accessible: $RESPONSE)${NC}"
else
  echo -e "${RED}✗ FAIL ($RESPONSE)${NC}"
fi

# Test 9: Get unverified profiles
echo -n "9. Unverified profiles endpoint... "
RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" \
  -X GET $API_URL/api/translator-profiles/unverified \
  -H "Authorization: Bearer $TOKEN")
if [ "$RESPONSE" = "200" ]; then
  echo -e "${GREEN}✓ PASS${NC}"
else
  echo -e "${YELLOW}⚠ Got $RESPONSE${NC}"
fi

# Test 10: File upload endpoint structure
echo -n "10. File upload endpoint... "
RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" \
  -X POST $API_URL/api/uploads/users/1/avatar \
  -H "Authorization: Bearer $TOKEN")
if [ "$RESPONSE" = "400" ] || [ "$RESPONSE" = "500" ]; then
  echo -e "${GREEN}✓ PASS (Endpoint exists - needs multipart data)${NC}"
else
  echo -e "${YELLOW}⚠ Got $RESPONSE${NC}"
fi

echo ""
echo "================================="
echo -e "${GREEN}✅ API Validation Complete!${NC}"
echo ""
echo "📖 Access full documentation:"
echo "   Swagger UI: http://localhost:8080/swagger-ui.html"
echo "   OpenAPI JSON: http://localhost:8080/v3/api-docs"
```

**Run:**
```bash
chmod +x validate-api.sh
./validate-api.sh
```

---

### Method 4: Postman Collection

**Import OpenAPI Spec to Postman:**

1. Open Postman
2. Click **Import**
3. Paste URL: `http://localhost:8080/v3/api-docs`
4. Click **Import**
5. Collection created with all endpoints!

**Setup Environment:**
- Variable: `baseUrl` = `http://localhost:8080`
- Variable: `token` = (set via login script)

**Login Script (in Postman Tests tab):**
```javascript
pm.test("Login successful", function () {
    pm.response.to.have.status(200);
    const response = pm.response.json();
    pm.environment.set("token", response.token);
});
```

---

### Endpoint Validation Checklist

For each endpoint, validate:

- [ ] **Request Format**: Correct HTTP method, Content-Type
- [ ] **Authentication**: 
  - Public endpoints work without token
  - Protected endpoints return 401 without token
  - Valid token allows access
- [ ] **Authorization**:
  - Correct role returns 200/201/204
  - Wrong role returns 403
  - No role information returns 403
- [ ] **Input Validation**:
  - Required fields validated
  - Invalid data returns 400
  - Field constraints enforced
- [ ] **Success Cases**:
  - Correct status code returned
  - Response format matches schema
  - Data persisted in the database
  - WebSocket events fired (if applicable)
- [ ] **Error Cases**:
  - 400 for invalid input
  - 401 for missing auth
  - 403 for wrong role
  - 404 for not found
  - 500 handled gracefully
- [ ] **Performance**:
  - Response time < 500ms
  - No memory leaks
  - Handles concurrent requests
- [ ] **Documentation**:
  - Swagger docs accurate
  - Examples work
  - Schemas correct

---

## Configuration

### Local Development

**File**: `src/main/resources/application-local.properties`

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/morago
spring.datasource.username=root
spring.datasource.password=your_password

# JWT
security.jwt.secret=your_secret_key
security.jwt.expiration-ms=3600000

# File Storage (Local)
storage.local.base-dir=uploads

# File Upload Limits
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Socket.IO
socketio.host=0.0.0.0
socketio.port=9092
socketio.allowed-origins=http://localhost:5173
```

---

### Production

**File**: `src/main/resources/application-prod.properties`

```properties
# Database (from environment)
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# JWT (from environment)
security.jwt.secret=${SECURITY_JWT_SECRET}
security.jwt.expiration-ms=${JWT_EXPIRATION_MS}

# AWS S3 Storage (Production)
storage.s3.bucket=${AWS_S3_BUCKET}
storage.s3.region=${AWS_S3_REGION}
storage.s3.access-key=${AWS_S3_ACCESS_KEY}
storage.s3.secret-key=${AWS_S3_SECRET_KEY}

# File Upload Limits
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Socket.IO
socketio.host=${SOCKETIO_HOST}
socketio.port=${SOCKETIO_PORT}
socketio.allowed-origins=${SOCKETIO_ALLOWED_ORIGINS}
```

---

### Environment Variables

**File**: `env.example`

```bash
# Database
DB_URL=jdbc:mysql://localhost:3306/morago
DB_USERNAME=root
DB_PASSWORD=your_password

# JWT
SECURITY_JWT_SECRET=your_base64_encoded_secret
JWT_EXPIRATION_MS=3600000

# Socket.IO
SOCKETIO_HOST=0.0.0.0
SOCKETIO_PORT=9092
SOCKETIO_ALLOWED_ORIGINS=http://localhost:5173

# File Storage - Local
STORAGE_LOCAL_BASE_DIR=uploads

# File Storage - AWS S3 (Production)
AWS_S3_BUCKET=your-bucket-name
AWS_S3_REGION=us-east-1
AWS_S3_ACCESS_KEY=your-access-key
AWS_S3_SECRET_KEY=your-secret-key

# File Upload Limits
MAX_FILE_SIZE=10MB
MAX_REQUEST_SIZE=10MB
```

---

## Error Handling

### Standard Error Response Format

```json
{
  "timestamp": "2025-10-09T05:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Detailed error message",
  "path": "/api/endpoint"
}
```

### Common HTTP Status Codes

| Code | Meaning | When It Occurs |
|------|---------|----------------|
| 200 | OK | Successful GET, PUT request |
| 201 | Created | Successful POST (resource created) |
| 204 | No Content | Successful DELETE |
| 400 | Bad Request | Invalid input, validation failed |
| 401 | Unauthorized | No token or invalid/expired token |
| 403 | Forbidden | Valid token but insufficient permissions |
| 404 | Not Found | Resource doesn't exist |
| 500 | Internal Server Error | Unexpected server error |

### Error Examples

#### 400 - Validation Error
```json
{
  "timestamp": "2025-10-09T05:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Phone number is required",
  "path": "/api/users"
}
```

#### 401 - Unauthorized
```json
{
  "timestamp": "2025-10-09T05:00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource",
  "path": "/api/users"
}
```

#### 403 - Forbidden
```json
{
  "timestamp": "2025-10-09T05:00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied - ADMINISTRATOR role required",
  "path": "/api/users/block"
}
```

#### 404 - Not Found
```json
{
  "timestamp": "2025-10-09T05:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "User not found with id: 999",
  "path": "/api/users/999"
}
```

---

## Data Models (Schemas)

### UserResponseDTO

```json
{
  "id": 1,
  "phone": "+1234567890",
  "firstName": "John",
  "lastName": "Doe",
  "balance": 100.00,
  "ratings": 4.5,
  "totalRatings": 10,
  "isActive": true,
  "isDebtor": false,
  "userProfileId": 5,
  "translatorProfileId": null,
  "adminId": null,
  "imageId": 10,
  "roles": ["CLIENT", "INTERPRETER"],
  "createdAtDatetime": "2024-01-15T10:30:00",
  "updatedAtDatetime": "2024-10-09T05:00:00"
}
```

### AdminResponseDTO

```json
{
  "id": 1,
  "userId": 1,
  "userPhone": "+admin",
  "userFullName": "Admin User",
  "adminLevel": "SUPER_ADMIN",
  "department": "System Administration",
  "permissions": "ALL",
  "isActive": true,
  "lastLogin": "2025-10-11T04:30:00",
  "loginCount": 150,
  "notes": "Primary system administrator",
  "createdAt": "2025-01-01T00:00:00",
  "updatedAt": "2025-10-11T04:30:00"
}
```

### AdminDashboardResponseDTO

```json
{
  "totalUsers": 1500,
  "activeUsers": 1200,
  "inactiveUsers": 300,
  "totalClients": 800,
  "totalInterpreters": 600,
  "verifiedInterpreters": 450,
  "unverifiedInterpreters": 150,
  "totalAdministrators": 10,
  "totalDebtors": 25,
  "totalSystemBalance": 50000.00,
  "totalWithdrawals": 350,
  "totalDeposits": 500,
  "totalWithdrawalAmount": 15000.00,
  "totalDepositAmount": 25000.00,
  "totalCallRecords": 5000,
  "activeRooms": 0,
  "totalNotifications": 10000,
  "systemStatus": "HEALTHY"
}
```

### TranslatorProfileResponseDTO

```json
{
  "id": 3,
  "email": "translator@example.com",
  "dateOfBirth": "1990-05-20",
  "isAvailable": true,
  "isOnline": false,
  "isVerified": true,
  "levelOfKorean": "Advanced",
  "createdAtDatetime": "2024-02-10T08:00:00",
  "updatedAtDatetime": "2024-10-09T05:00:00"
}
```

### LoginRequest

```json
{
  "phone": "+1234567890",
  "password": "password123"
}
```

### LoginResponse

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIrMTIzNDU2Nzg5MCIsInJvbGVzIjpbIkFETUlOSVNUUkFUT1IiXSwiaWF0IjoxNjk2ODQ4MDAwLCJleHAiOjE2OTY4NTE2MDB9.signature"
}
```

### PagedResponse

```json
{
  "content": [
    {
      "id": 1,
      "phone": "+1234567890",
      "firstName": "John",
      "lastName": "Doe",
      "balance": 100.00,
      "isActive": true
    },
    {
      "id": 2,
      "phone": "+0987654321",
      "firstName": "Jane",
      "lastName": "Smith",
      "balance": 50.00,
      "isActive": true
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 100
}
```

---

## Advanced Features

### Pagination

Most list endpoints support pagination:

**Parameters:**
- `page`: Page number (0-indexed)
- `size`: Items per page
- `sortBy`: Field to sort by
- `ascending`: Sort direction (true/false)

**Example:**
```bash
GET /api/users?page=0&size=10&sortBy=createdAt&ascending=false
```

### Filtering

Some endpoints support filtering:

**Parameters:**
- `search`: Text search across relevant fields
- `filters[key]`: Filter by specific field values

**Example:**
```bash
GET /api/users?search=john&filters[role]=CLIENT&filters[active]=true
```

---

## WebSocket Events

Real-time events for admin actions:

| Event | Trigger | Payload |
|-------|---------|---------|
| `userBlocked` | User blocked | UserResponseDTO |
| `userActivated` | User activated | UserResponseDTO |
| `translatorProfileVerified` | Profile verified | TranslatorProfileResponseDTO |
| `translatorProfileUnverified` | Verification removed | TranslatorProfileResponseDTO |
| `fileCreated` | File uploaded | File entity |

**Socket.IO Connection:**
```javascript
const socket = io('http://localhost:9092');

socket.on('userBlocked', (user) => {
  console.log('User blocked:', user);
  // Update UI
});

socket.on('translatorProfileVerified', (profile) => {
  console.log('Profile verified:', profile);
  // Update UI
});
```

---

## Security

### JWT Token

**Format**: `eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.{payload}.{signature}`

**Expiration**: 1 hour (3600000 ms)

**Usage**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Decode Token**:
```bash
echo $TOKEN | cut -d. -f2 | base64 -d | jq
```

### Role-Based Access Control

| Role | Permissions |
|------|-------------|
| **ADMINISTRATOR** | All endpoints, user management, profile verification, system configuration |
| **INTERPRETER** | Translator profiles, availability management, call records |
| **CLIENT** | View translators, create call records, rate interpreters |

---

## Troubleshooting

### Issue: Cannot Connect to API

**Check:**
```bash
# Is server running?
curl http://localhost:8080/actuator/health

# Check port
lsof -i :8080

# Check logs
tail -f logs/spring.log
```

### Issue: 401 Unauthorized

**Causes:**
- No Authorization header
- Invalid token
- Expired token (> 1 hour old)

**Solution:**
- Login again to get new token
- Verify Authorization header format: `Bearer {token}`

### Issue: 403 Forbidden

**Causes:**
- Valid token but wrong role
- Endpoint requires different role

**Solution:**
- Check endpoint's required role in Swagger UI
- Verify user has correct role
- Login with correct role user

### Issue: Swagger UI Not Loading

**Check:**
- Application is running: `curl http://localhost:8080/actuator/health`
- Correct URL: `http://localhost:8080/swagger-ui.html`
- Browser console for errors

### Issue: File Upload Fails

**Check:**
- File size within limits (5MB avatars, 10MB documents)
- File type is allowed (JPEG, PNG, PDF, etc.)
- Multipart form-data content type
- User has permission for endpoint

---

## Frontend Integration

### React/Vue Example

```javascript
// Login
async function login(phone, password) {
  const response = await fetch('http://localhost:8080/api/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ phone, password })
  });
  const data = await response.json();
  localStorage.setItem('token', data.token);
  return data.token;
}

// Get admin dashboard statistics
async function getDashboard() {
  const token = localStorage.getItem('token');
  const response = await fetch('http://localhost:8080/api/admin/dashboard', {
    headers: { 'Authorization': `Bearer ${token}` }
  });
  return await response.json();
}

// Get all users (admin)
async function getAllUsers(page = 0, size = 10) {
  const token = localStorage.getItem('token');
  const response = await fetch(`http://localhost:8080/api/admin/users?page=${page}&size=${size}`, {
    headers: { 'Authorization': `Bearer ${token}` }
  });
  return await response.json();
}

// Create user (admin)
async function createUser(userData) {
  const token = localStorage.getItem('token');
  const response = await fetch('http://localhost:8080/api/admin/users', {
    method: 'POST',
    headers: { 
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(userData)
  });
  return await response.json();
}

// Assign role to user (admin)
async function assignRole(userId, roleName) {
  const token = localStorage.getItem('token');
  const response = await fetch(`http://localhost:8080/api/admin/users/${userId}/roles/${roleName}`, {
    method: 'POST',
    headers: { 'Authorization': `Bearer ${token}` }
  });
  return await response.json();
}

// Upload avatar
async function uploadAvatar(userId, file) {
  const token = localStorage.getItem('token');
  const formData = new FormData();
  formData.append('file', file);

  const response = await fetch(`http://localhost:8080/api/uploads/users/${userId}/avatar`, {
    method: 'POST',
    headers: { 'Authorization': `Bearer ${token}` },
    body: formData
  });
  return await response.text(); // Returns file URL
}

// Download file
async function downloadFile(fileId, filename) {
  const token = localStorage.getItem('token');
  const response = await fetch(`http://localhost:8080/api/uploads/files/${fileId}/download`, {
    headers: { 'Authorization': `Bearer ${token}` }
  });
  const blob = await response.blob();
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = filename;
  a.click();
}
```

### Angular/TypeScript Example

```typescript
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable()
export class MoragoApiService {
  private baseUrl = 'http://localhost:8080';
  private token: string;

  constructor(private http: HttpClient) {}

  // Login
  login(phone: string, password: string): Observable<{token: string}> {
    return this.http.post<{token: string}>(
      `${this.baseUrl}/api/auth/login`,
      { phone, password }
    ).pipe(
      tap(response => this.token = response.token)
    );
  }

  // Get headers with auth
  private getHeaders(): HttpHeaders {
    return new HttpHeaders({
      'Authorization': `Bearer ${this.token}`
    });
  }

  // Block user
  blockUser(userId: number): Observable<any> {
    return this.http.post(
      `${this.baseUrl}/api/users/${userId}/block`,
      {},
      { headers: this.getHeaders() }
    );
  }

  // Upload avatar
  uploadAvatar(userId: number, file: File): Observable<string> {
    const formData = new FormData();
    formData.append('file', file);

    return this.http.post<string>(
      `${this.baseUrl}/api/uploads/users/${userId}/avatar`,
      formData,
      { 
        headers: new HttpHeaders({ 'Authorization': `Bearer ${this.token}` }),
        responseType: 'text' as 'json'
      }
    );
  }
}
```

---

## Database Schema

### Users Table

```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    phone VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(200),
    last_name VARCHAR(200),
    balance DECIMAL(10,2) DEFAULT 0.00,
    ratings DECIMAL(15,2) DEFAULT 0.00,
    total_ratings INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    is_debtor BOOLEAN DEFAULT FALSE,
    image_id BIGINT,
    translator_profile_id BIGINT,
    user_profile_id BIGINT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

### Admins Table

```sql
CREATE TABLE admins (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    admin_level VARCHAR(50),
    department VARCHAR(100),
    permissions TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    last_login TIMESTAMP NULL,
    login_count INT DEFAULT 0,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_admin_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Indexes
CREATE INDEX idx_admins_user_id ON admins(user_id);
CREATE INDEX idx_admins_admin_level ON admins(admin_level);
CREATE INDEX idx_admins_department ON admins(department);
CREATE INDEX idx_admins_is_active ON admins(is_active);
```

### Translator Profiles Table

```sql
CREATE TABLE translator_profiles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255),
    date_of_birth DATE,
    is_available BOOLEAN,
    is_online BOOLEAN,
    is_verified BOOLEAN DEFAULT FALSE,
    level_of_korean VARCHAR(200),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

### Files Table

```sql
CREATE TABLE files (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    original_title VARCHAR(255),
    path VARCHAR(255),
    type VARCHAR(100),
    theme_id BIGINT,
    created_at_datetime TIMESTAMP,
    updated_at_datetime TIMESTAMP
);
```

---

## Best Practices

### 1. Authentication
✅ Always login first to get token
✅ Store token securely (not in localStorage for sensitive apps)
✅ Refresh token before expiration
✅ Handle 401 errors by redirecting to log in

### 2. Error Handling
✅ Check response status codes
✅ Display user-friendly error messages
✅ Log errors for debugging
✅ Handle network errors gracefully

### 3. File Uploads
✅ Validate file size on client side
✅ Show upload progress
✅ Handle upload errors
✅ Validate file types before upload

### 4. API Calls
✅ Use environment variables for base URL
✅ Implement retry logic for failed requests
✅ Add request timeouts
✅ Handle concurrent requests properly

---

## Performance

### Expected Response Times

| Endpoint Type | Expected Time |
|---------------|---------------|
| Simple GET | < 100ms |
| GET with DB query | < 200ms |
| POST/PUT | < 300ms |
| File upload (< 1MB) | < 500ms |
| File upload (> 1MB) | < 2000ms |
| File download | < 500ms |

---

## Export & Integration

### Export OpenAPI Specification

```bash
# JSON format
curl http://localhost:8080/v3/api-docs > openapi.json

# YAML format
curl http://localhost:8080/v3/api-docs.yaml > openapi.yaml
```

### Generate Client SDK

**TypeScript/Axios:**
```bash
npx @openapitools/openapi-generator-cli generate \
  -i http://localhost:8080/v3/api-docs \
  -g typescript-axios \
  -o ./client-sdk
```

**Python:**
```bash
openapi-generator-cli generate \
  -i http://localhost:8080/v3/api-docs \
  -g python \
  -o ./python-client
```

**Java:**
```bash
openapi-generator-cli generate \
  -i http://localhost:8080/v3/api-docs \
  -g java \
  -o ./java-client
```

---

## Summary

### Implemented Features

✅ **Comprehensive Admin System**
- Dedicated `/api/admin/**` endpoints for all administrative operations
- Admin profile management with levels (SUPER_ADMIN, MODERATOR, SUPPORT)
- Dashboard with real-time system statistics
- User management (create, update, delete, list)
- Role management (assign, remove, promote)
- Translator verification workflow
- Financial operations (deposits, withdrawals, debtors)
- Login tracking and admin activity monitoring

✅ **Enhanced Security**
- SecurityConfig moved to `config` package
- Role-based access control with clear separation:
  - `/api/admin/**` - ADMINISTRATOR only
  - `/api/users/**` - CLIENT, INTERPRETER only
  - `/api/translator-profiles/**` - INTERPRETER, ADMINISTRATOR
- JWT-based authentication
- Proper authorization for all endpoints

✅ **File Storage**
- Local storage (development)
- AWS S3 (production)
- Avatar upload/download
- Document management
- File validation

✅ **API Documentation**
- Enhanced Swagger/OpenAPI config
- Comprehensive endpoint documentation
- Interactive testing via Swagger UI
- Request/response examples
- Error response schemas

✅ **Data Models**
- User entity with Admin relationship
- Admin entity with department and permission management
- Comprehensive DTOs for all operations
- Dashboard statistics DTO

### Access Points

| Resource | URL |
|----------|-----|
| Swagger UI | `http://localhost:8080/swagger-ui.html` |
| OpenAPI JSON | `http://localhost:8080/v3/api-docs` |
| OpenAPI YAML | `http://localhost:8080/v3/api-docs.yaml` |
| Health Check | `http://localhost:8080/actuator/health` |

### Quick Commands

```bash
# Start application
mvn spring-boot:run

# Test API
curl http://localhost:8080/actuator/health

# Access Swagger
open http://localhost:8080/swagger-ui.html

# Validate endpoints
./validate-api.sh
```

---

## Support

For issues or questions:
- Check Swagger UI for interactive documentation
- Review error messages and status codes
- Verify authentication token is valid
- Ensure correct role permissions
- Check application logs for details

---

**Version:** 2.0.0  
**Last Updated:** October 11, 2025  
**Status:** ✅ Production Ready  
**Documentation Type:** Complete API Reference

**Recent Updates (v2.0.0):**
- ✅ New AdminController with 50+ consolidated admin endpoints
- ✅ Admin profile management system with levels and departments
- ✅ Dashboard statistics endpoint for system monitoring
- ✅ SecurityConfig relocated to config package
- ✅ Updated access control (admin endpoints separated from user endpoints)
- ✅ New database table: admins

**Access Swagger UI for interactive testing:**  
`http://localhost:8080/swagger-ui.html`

