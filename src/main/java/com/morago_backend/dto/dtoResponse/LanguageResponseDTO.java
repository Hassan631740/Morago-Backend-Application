package com.morago_backend.dto.dtoResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "Response DTO for Language")
public class LanguageResponseDTO {

    @Schema(description = "Language ID", example = "1")
    private Long id;

    @Schema(description = "Language name", example = "English")
    private String name;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAtDatetime;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAtDatetime;

}


