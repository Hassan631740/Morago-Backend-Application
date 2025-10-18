package com.morago_backend.dto.dtoRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request DTO for creating or updating a FileResource")
public class FileResourceRequestDTO {

    @Schema(description = "Origin type of the file", example = "UPLOAD")
    @NotBlank(message = "Origin type is required")
    @Size(max = 255)
    private String originType;

    @Schema(description = "Path of the file", example = "/uploads/file.pdf")
    @NotBlank(message = "Path is required")
    @Size(max = 255)
    private String path;

    @Schema(description = "File type", example = "PDF")
    @NotBlank(message = "Type is required")
    @Size(max = 100)
    private String type;
}
