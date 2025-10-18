package com.morago_backend.dto.dtoRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter

/**
 * DTO for Rating request operations
 */
@Schema(description = "Rating request data")
public class RatingRequestDTO {

    @Schema(description = "User ID who is giving the rating", example = "1")
    @NotNull(message = "Who user ID is required")
    private Long whoUserId;

    @Schema(description = "User ID who is receiving the rating", example = "2")
    @NotNull(message = "To whom user ID is required")
    private Long toWhomUserId;

    @Schema(description = "Rating grade (1.0 to 5.0)", example = "4.5")
    @NotNull(message = "Rating grade is required")
    @DecimalMin(value = "1.0", message = "Rating must be at least 1.0")
    @DecimalMax(value = "5.0", message = "Rating cannot exceed 5.0")
    private BigDecimal grade;

    // Constructors
    public RatingRequestDTO() {}

    public RatingRequestDTO(Long whoUserId, Long toWhomUserId, BigDecimal grade) {
        this.whoUserId = whoUserId;
        this.toWhomUserId = toWhomUserId;
        this.grade = grade;
    }
}
