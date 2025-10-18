package com.morago_backend.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request DTO for client user registration")
public class ClientSignupRequest {

    @Schema(description = "User's phone number", example = "+1234567890", required = true)
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String phone;

    @Schema(description = "User's password", example = "securePassword123", required = true)
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @Schema(description = "User's first name", example = "John")
    @Size(max = 200, message = "First name cannot exceed 200 characters")
    private String firstName;

    @Schema(description = "User's last name", example = "Doe")
    @Size(max = 200, message = "Last name cannot exceed 200 characters")
    private String lastName;
}

