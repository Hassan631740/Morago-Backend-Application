package com.morago_backend.dto.dtoResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "Response DTO for FileResource")
public class FileResourceResponseDTO {

    @Schema(description = "FileResource ID", example = "1")
    private Long id;

    @Schema(description = "Origin type", example = "USER_UPLOAD")
    private String originType;

    @Schema(description = "File path", example = "/files/sample.pdf")
    private String path;

    @Schema(description = "File type", example = "pdf")
    private String type;

    @Schema(description = "Created timestamp")
    private LocalDateTime createdAtDatetime;

    @Schema(description = "Updated timestamp")
    private LocalDateTime updatedAtDatetime;
}
