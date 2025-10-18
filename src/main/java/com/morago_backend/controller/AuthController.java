package com.morago_backend.controller;

import com.morago_backend.dto.dtoResponse.ErrorResponse;
import com.morago_backend.payload.*;
import com.morago_backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "User Sign up")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(
        summary = "User Login",
        description = """
            Authenticates a user and returns a JWT token for subsequent API calls.
            
            ### Authentication Flow:
            1. Send phone number and password
            2. Receive JWT token on successful authentication
            3. Use token in Authorization header for protected endpoints: `Authorization: Bearer {token}`
            
            ### Roles:
            The token contains user roles (ADMINISTRATOR, INTERPRETER, or CLIENT)
            which determine access to different endpoints.
            
            ### Token Expiration:
            Tokens expire after 1 hour. Request a new token when expired.
            """,
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Login credentials",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginRequest.class),
                examples = {
                    @ExampleObject(
                        name = "Admin Login",
                        description = "Login as administrator",
                        value = """
                        {
                          "phone": "+1234567890",
                          "password": "admin123"
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "Client Login",
                        description = "Login as regular client",
                        value = """
                        {
                          "phone": "+0987654321",
                          "password": "client123"
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "Interpreter Login",
                        description = "Login as interpreter",
                        value = """
                        {
                          "phone": "+1122334455",
                          "password": "interpreter123"
                        }
                        """
                    )
                }
            )
        )
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Login successful - JWT token returned",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginResponse.class),
                examples = @ExampleObject(
                    name = "Successful Login",
                    value = """
                    {
                      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication failed - Invalid credentials",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    name = "Invalid Credentials",
                    value = """
                    {
                      "timestamp": "2025-10-09T04:30:00",
                      "status": 401,
                      "error": "Unauthorized",
                      "message": "Invalid phone number or password",
                      "path": "/api/auth/login"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request - Validation failed",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    name = "Validation Error",
                    value = """
                    {
                      "timestamp": "2025-10-09T04:30:00",
                      "status": 400,
                      "error": "Bad Request",
                      "message": "Phone number is required",
                      "path": "/api/auth/login"
                    }
                    """
                )
            )
        )
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.login(request.getPhone(), request.getPassword());
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @Operation(
        summary = "Client Registration",
        description = """
            Registers a new client user and returns a JWT token for immediate authentication.
            
            ### Registration Flow:
            1. Provide phone number, password, and optional personal information
            2. System creates user account with CLIENT role
            3. Receive JWT token for immediate access
            
            ### Profile Creation:
            - A user profile is automatically created for the client
            - Client receives one free call upon registration
            
            ### Phone Number Format:
            - Must be in international format (e.g., +1234567890)
            - Must be unique across all users
            
            ### Password Requirements:
            - Minimum 8 characters
            """,
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Client registration information",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ClientSignupRequest.class),
                examples = @ExampleObject(
                    name = "Client Registration",
                    description = "Register a new client",
                    value = """
                    {
                      "phone": "+1234567890",
                      "password": "securePassword123",
                      "firstName": "John",
                      "lastName": "Doe"
                    }
                    """
                )
            )
        )
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Registration successful - JWT token and user details returned",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SignupResponse.class),
                examples = @ExampleObject(
                    name = "Successful Registration",
                    value = """
                    {
                      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                      "userId": 123,
                      "phone": "+1234567890",
                      "firstName": "John",
                      "lastName": "Doe",
                      "roles": ["CLIENT"]
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request - Validation failed or phone already registered",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = {
                    @ExampleObject(
                        name = "Validation Error",
                        value = """
                        {
                          "timestamp": "2025-10-11T10:30:00",
                          "status": 400,
                          "error": "Bad Request",
                          "message": "Phone number is required",
                          "path": "/api/auth/signup/client"
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "Phone Already Registered",
                        value = """
                        {
                          "timestamp": "2025-10-11T10:30:00",
                          "status": 400,
                          "error": "Bad Request",
                          "message": "Phone number already registered",
                          "path": "/api/auth/signup/client"
                        }
                        """
                    )
                }
            )
        )
    })
    @PostMapping("/signup/client")
    public ResponseEntity<SignupResponse> signupClient(@Valid @RequestBody ClientSignupRequest request) {
        SignupResponse response = authService.signupClient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
        summary = "Translator Registration",
        description = """
            Registers a new translator (interpreter) and returns a JWT token for immediate authentication.
            
            ### Registration Flow:
            1. Provide phone number, password, email, and professional information
            2. System creates user account with INTERPRETER role
            3. Receive JWT token for immediate access
            
            ### Translator Profile:
            - A translator profile is automatically created
            - Initial status: unverified, offline, and unavailable
            - Admin verification required before translator can accept calls
            
            ### Required Information:
            - Email address (for professional communication)
            - Korean language proficiency level
            - Date of birth (optional)
            
            ### Verification Process:
            - New translators start with `isVerified: false`
            - Admin must verify translator credentials
            - Only verified translators can receive translation requests
            """,
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Translator registration information",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TranslatorSignupRequest.class),
                examples = @ExampleObject(
                    name = "Translator Registration",
                    description = "Register a new translator",
                    value = """
                    {
                      "phone": "+1122334455",
                      "password": "securePassword123",
                      "firstName": "Jane",
                      "lastName": "Smith",
                      "email": "jane.smith@example.com",
                      "dateOfBirth": "1990-01-15",
                      "levelOfKorean": "Advanced"
                    }
                    """
                )
            )
        )
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Registration successful - JWT token and user details returned",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SignupResponse.class),
                examples = @ExampleObject(
                    name = "Successful Registration",
                    value = """
                    {
                      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                      "userId": 456,
                      "phone": "+1122334455",
                      "firstName": "Jane",
                      "lastName": "Smith",
                      "roles": ["INTERPRETER"]
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request - Validation failed or phone already registered",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = {
                    @ExampleObject(
                        name = "Validation Error",
                        value = """
                        {
                          "timestamp": "2025-10-11T10:30:00",
                          "status": 400,
                          "error": "Bad Request",
                          "message": "Email is required for translators",
                          "path": "/api/auth/signup/translator"
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "Phone Already Registered",
                        value = """
                        {
                          "timestamp": "2025-10-11T10:30:00",
                          "status": 400,
                          "error": "Bad Request",
                          "message": "Phone number already registered",
                          "path": "/api/auth/signup/translator"
                        }
                        """
                    )
                }
            )
        )
    })
    @PostMapping("/signup/translator")
    public ResponseEntity<SignupResponse> signupTranslator(@Valid @RequestBody TranslatorSignupRequest request) {
        SignupResponse response = authService.signupTranslator(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
