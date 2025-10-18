package com.morago_backend.dto.dtoRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request DTO for creating or updating a Language")
public class LanguageRequestDTO {

    public interface Create {}
    public interface Update {}

    @Schema(description = "Language name", example = "English")
    @NotBlank(message = "Name is required", groups = Create.class)
    @Size(max = 200, message = "Name cannot exceed 200 characters")
    private String name;

}


