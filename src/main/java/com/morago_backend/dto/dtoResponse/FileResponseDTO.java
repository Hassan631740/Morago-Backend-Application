package com.morago_backend.dto.dtoResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "Response DTO for File")
public class FileResponseDTO {

    @Schema(description = "File ID", example = "1")
    private Long id;

    @Schema(description = "Original title", example = "avatar.png")
    private String originalTitle;

    @Schema(description = "File path", example = "/uploads/2025/09/avatar.png")
    private String path;

    @Schema(description = "MIME type or category", example = "image/png")
    private String type;

    @Schema(description = "Related theme ID", example = "3")
    private Long themeId;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;

}


