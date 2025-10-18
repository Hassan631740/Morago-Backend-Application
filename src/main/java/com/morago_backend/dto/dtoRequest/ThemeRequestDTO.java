package com.morago_backend.dto.dtoRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Schema(description = "Request DTO for creating or updating a Theme")
public class ThemeRequestDTO {

    @Schema(description = "Theme name", example = "Medical Translation")
    @NotBlank(message = "Theme name is required")
    @Size(max = 200)
    private String name;

    @Schema(description = "Korean title", example = "의료 번역")
    @Size(max = 200)
    private String koreanTitle;

    @Schema(description = "Price", example = "1000.00")
    @NotNull(message = "Price is required")
    private BigDecimal price;

    @Schema(description = "Night price", example = "1200.00")
    private BigDecimal nightPrice;

    @Schema(description = "Description")
    @Size(max = 500)
    private String description;

    @Schema(description = "Is popular")
    private Boolean isPopular;

    @Schema(description = "Icon ID")
    private Long iconId;

    @Schema(description = "Is active")
    private Boolean isActive;

    @Schema(description = "Category ID", example = "1")
    @NotNull(message = "Category ID is required")
    private Long categoryId;

}
