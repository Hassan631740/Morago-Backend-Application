package com.morago_backend.dto.dtoResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Schema(description = "Response DTO for Category")
public class CategoryResponseDTO {

    @Schema(description = "Category ID", example = "1")
    private Long id;

    @Schema(description = "Category name", example = "Medical Translation")
    private String name;

    @Schema(description = "Is category active", example = "true")
    private Boolean isActive;

    @Schema(description = "Category creation timestamp")
    private LocalDateTime createdAtDatetime;

    @Schema(description = "Category last update timestamp")
    private LocalDateTime updatedAtDatetime;

    public CategoryResponseDTO() {}

    public CategoryResponseDTO(Long id, String name, Boolean isActive,
                               LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.isActive = isActive;
        this.createdAtDatetime = createdAt;
        this.updatedAtDatetime = updatedAt;
    }
}
