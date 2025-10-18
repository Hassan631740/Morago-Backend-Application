package com.morago_backend.dto.dtoRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Schema(description = "Request DTO for creating or updating a Translator Profile")
public class TranslatorProfileRequestDTO {

    @Schema(description = "Email of the translator", example = "translator@example.com")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    private String email;

    @Schema(description = "Date of birth", example = "1990-01-15")
    private LocalDate dateOfBirth;

    @Schema(description = "Is translator currently available", example = "true")
    private Boolean isAvailable;

    @Schema(description = "Is translator currently online", example = "false")
    private Boolean isOnline;

    @Schema(description = "Is translator profile verified by admin", example = "false")
    private Boolean isVerified;

    @Schema(description = "Level of Korean proficiency", example = "Intermediate")
    @Size(max = 200, message = "Level of Korean cannot exceed 200 characters")
    private String levelOfKorean;

    public TranslatorProfileRequestDTO() {}

    public TranslatorProfileRequestDTO(String email, LocalDate dateOfBirth, Boolean isAvailable,
                                       Boolean isOnline, String levelOfKorean) {
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.isAvailable = isAvailable;
        this.isOnline = isOnline;
        this.levelOfKorean = levelOfKorean;
    }
}
