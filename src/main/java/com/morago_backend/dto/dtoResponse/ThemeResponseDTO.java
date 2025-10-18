package com.morago_backend.dto.dtoResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response DTO for Theme")
public class ThemeResponseDTO {

    @Schema(description = "Theme ID", example = "1")
    private Long id;

    @Schema(description = "Theme name", example = "Medical Translation")
    private String name;

    @Schema(description = "Korean title", example = "의료 번역")
    private String koreanTitle;

    @Schema(description = "Price", example = "1000.00")
    private BigDecimal price;

    @Schema(description = "Night price", example = "1200.00")
    private BigDecimal nightPrice;

    @Schema(description = "Description", example = "Translation service for medical documents")
    private String description;

    @Schema(description = "Is popular", example = "true")
    private Boolean isPopular;

    @Schema(description = "Icon ID", example = "1")
    private Long iconId;

    @Schema(description = "Is active", example = "true")
    private Boolean isActive;

    @Schema(description = "Category ID", example = "1")
    private Long categoryId;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAtDatetime;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAtDatetime;

}
