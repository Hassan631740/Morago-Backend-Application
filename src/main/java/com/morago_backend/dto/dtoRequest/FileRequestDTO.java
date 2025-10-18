package com.morago_backend.dto.dtoRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Schema(description = "Request DTO for creating or updating a File")
public class FileRequestDTO {

    public interface Create {}
    public interface Update {}

    @Schema(description = "Original title", example = "avatar.png")
    @NotBlank(message = "Original title is required", groups = Create.class)
    @Size(max = 255, message = "Original title cannot exceed 255 characters")
    private String originalTitle;

    @Schema(description = "File path, auto-generated when uploading")
    @Size(max = 255, message = "Path cannot exceed 255 characters")
    private String path;

    @Schema(description = "MIME type or category", example = "image/png")
    @NotBlank(message = "Type is required", groups = Create.class)
    @Size(max = 100, message = "Type cannot exceed 100 characters")
    private String type;

    @Schema(description = "Related theme ID", example = "3")
    private Long themeId;

    @Schema(description = "File to be uploaded")
    private MultipartFile file;
}
