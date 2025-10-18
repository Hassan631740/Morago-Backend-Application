package com.morago_backend.dto.dtoResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Schema(description = "Response DTO for returning a TranslatorProfile")
public class TranslatorProfileResponseDTO {

    @Schema(description = "ID of the translator", example = "1")
    private Long id;

    @Schema(description = "Email of the translator", example = "translator@example.com")
    private String email;

    @Schema(description = "Date of birth", example = "1990-05-20")
    private LocalDate dateOfBirth;

    @Schema(description = "Is translator currently available", example = "true")
    private Boolean isAvailable;

    @Schema(description = "Is translator currently online", example = "false")
    private Boolean isOnline;

    @Schema(description = "Is translator profile verified by admin", example = "false")
    private Boolean isVerified;

    @Schema(description = "Level of Korean proficiency", example = "Intermediate")
    private String levelOfKorean;

    @Schema(description = "List of themes the translator specializes in")
    private List<String> themes;

    @Schema(description = "Category creation timestamp")
    private LocalDateTime createdAtDatetime;

    @Schema(description = "Category last update timestamp")
    private LocalDateTime updatedAtDatetime;

}
