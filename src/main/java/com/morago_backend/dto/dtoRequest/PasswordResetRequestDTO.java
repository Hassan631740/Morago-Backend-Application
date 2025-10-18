package com.morago_backend.dto.dtoRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request DTO for creating a Password Reset request")
public class PasswordResetRequestDTO {

    public interface Create {}
    public interface Verify {}

    @Schema(description = "Phone number", example = "+1234567890")
    @NotBlank(message = "Phone is required", groups = {Create.class, Verify.class})
    @Pattern(
            regexp = "^\\+?[1-9]\\d{1,14}$",
            message = "Invalid phone number format",
            groups = {Create.class, Verify.class}
    )
    private String phone;

    @Schema(description = "Reset code", example = "123456")
    @NotNull(message = "Reset code is required", groups = Verify.class)
    @Min(value = 0, message = "Reset code must be non-negative")
    private Integer resetCode;

    @Schema(description = "New password (only for reset step)", example = "MyNewPassword123")
    @NotBlank(message = "New password is required", groups = Verify.class)
    private String newPassword;
}


