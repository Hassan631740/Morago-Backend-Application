package com.morago_backend.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "Response DTO for user registration")
public class SignupResponse {

    @Schema(description = "JWT token for authentication", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "User ID", example = "1")
    private Long userId;

    @Schema(description = "User's phone number", example = "+1234567890")
    private String phone;

    @Schema(description = "User's first name", example = "John")
    private String firstName;

    @Schema(description = "User's last name", example = "Doe")
    private String lastName;

    @Schema(description = "User's roles", example = "[\"CLIENT\"]")
    private Set<String> roles;
}

