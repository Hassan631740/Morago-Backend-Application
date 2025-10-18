package com.morago_backend.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Schema(description = "Request DTO for translator registration")
public class TranslatorSignupRequest {

    @Schema(description = "Translator's phone number", example = "+1234567890", required = true)
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String phone;

    @Schema(description = "Translator's password", example = "securePassword123", required = true)
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @Schema(description = "Translator's first name", example = "Jane")
    @Size(max = 200, message = "First name cannot exceed 200 characters")
    private String firstName;

    @Schema(description = "Translator's last name", example = "Smith")
    @Size(max = 200, message = "Last name cannot exceed 200 characters")
    private String lastName;

    @Schema(description = "Translator's email address", example = "translator@example.com", required = true)
    @NotBlank(message = "Email is required for translators")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    private String email;

    @Schema(description = "Translator's date of birth", example = "1990-01-15")
    private LocalDate dateOfBirth;

    @Schema(description = "Translator's Korean language proficiency level", example = "Advanced")
    @Size(max = 200, message = "Level of Korean cannot exceed 200 characters")
    private String levelOfKorean;
}

