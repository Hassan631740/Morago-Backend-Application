package com.morago_backend.dto.dtoResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for Rating response data
 */
@Getter
@Setter
@Schema(description = "Rating response data")
public class RatingResponseDTO {

    @Schema(description = "Rating ID", example = "1")
    private Long id;

    @Schema(description = "User ID who gave the rating", example = "1")
    private Long whoUserId;

    @Schema(description = "User ID who received the rating", example = "2")
    private Long toWhomUserId;

    @Schema(description = "Rating grade (1.0 to 5.0)", example = "4.5")
    private BigDecimal grade;

    @Schema(description = "Rating creation timestamp")
    private LocalDateTime createdAtDatetime;

    @Schema(description = "Rating last update timestamp")
    private LocalDateTime updatedAtDatetime;

    // Constructors
    public RatingResponseDTO() {}

    public RatingResponseDTO(Long id, Long whoUserId, Long toWhomUserId, BigDecimal grade,
                             LocalDateTime createdAtDatetime, LocalDateTime updatedAtDatetime) {
        this.id = id;
        this.whoUserId = whoUserId;
        this.toWhomUserId = toWhomUserId;
        this.grade = grade;
        this.createdAtDatetime = createdAtDatetime;
        this.updatedAtDatetime = updatedAtDatetime;
    }


}
