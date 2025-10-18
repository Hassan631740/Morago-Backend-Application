package com.morago_backend.dto.dtoRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request DTO for creating/updating a Category")
public class CategoryRequestDTO {

    @Schema(description = "Category name", example = "Translation")
    private String name;

    @Schema(description = "Is category active?", example = "true")
    private Boolean isActive;
}
