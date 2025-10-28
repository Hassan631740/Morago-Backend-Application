package com.morago_backend.dto.dtoRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request DTO for changing password")
public class PasswordChangeRequestDTO {

    @Schema(description = "Current password", example = "currentPassword123", required = true)
    @NotBlank(message = "Current password is required")
    private String currentPassword;

    @Schema(description = "New password", example = "newSecurePassword123", required = true)
    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String newPassword;

    public PasswordChangeRequestDTO() {}
    
    @Override
    public String toString() {
        return "PasswordChangeRequestDTO{" +
                "currentPassword='***REDACTED***'" +
                ", newPassword='***REDACTED***'" +
                '}';
    }
}


