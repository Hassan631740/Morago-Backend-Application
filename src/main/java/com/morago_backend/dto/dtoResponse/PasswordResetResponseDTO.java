package com.morago_backend.dto.dtoResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "Response DTO for Password Reset")
public class PasswordResetResponseDTO {

    @Schema(description = "Password reset ID", example = "1")
    private Long id;

    @Schema(description = "Phone number", example = "+1234567890")
    private String phone;

    @Schema(description = "Reset code", example = "1234")
    private Integer resetCode;

    @Schema(description = "Status message", example = "Code verified")
    private String message;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAtDatetime;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAtDatetime;
}
