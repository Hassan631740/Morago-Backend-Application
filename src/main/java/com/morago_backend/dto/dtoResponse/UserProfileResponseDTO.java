package com.morago_backend.dto.dtoResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "Response DTO for User Profile")
public class UserProfileResponseDTO {

    @Schema(description = "User profile ID", example = "1")
    private Long id;

    @Schema(description = "Has the user made a free call", example = "true")
    private Boolean isFreeCallMade;

    @Schema(description = "Related user ID", example = "42")
    private Long userId;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;

}


