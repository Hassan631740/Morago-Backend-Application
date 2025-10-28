package com.morago_backend.dto.dtoRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@Schema(description = "Request DTO for creating or updating a User")
public class UserRequestDTO {

    public interface Create {}
    public interface Update {}

    @Schema(description = "User's phone number", example = "+1234567890")
    @NotBlank(message = "Phone number is required", groups = Create.class)
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String phone;

    @Schema(description = "User's password", example = "securePassword123")
    @NotBlank(message = "Password is required", groups = Create.class)
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;
    
    @Override
    public String toString() {
        return "UserRequestDTO{" +
                "phone='" + phone + '\'' +
                ", password='***REDACTED***'" +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", balance=" + balance +
                ", isActive=" + isActive +
                ", role='" + role + '\'' +
                ", translatorProfileId=" + translatorProfileId +
                ", userProfileId=" + userProfileId +
                ", roleIds=" + roleIds +
                '}';
    }

    @Schema(description = "User's first name", example = "John")
    @Size(max = 200, message = "First name cannot exceed 200 characters")
    private String firstName;

    @Schema(description = "User's last name", example = "Doe")
    @Size(max = 200, message = "Last name cannot exceed 200 characters")
    private String lastName;

    @Schema(description = "User's account balance", example = "100.50")
    private BigDecimal balance;

    @Schema(description = "Whether the user is active", example = "true")
    private Boolean isActive;

    @Schema(description = "Role(s) assigned to the user", example = "CLIENT")
    private String role;

    @Schema(description = "Translator profile ID (if any)", example = "1")
    private Long translatorProfileId;

    @Schema(description = "User profile ID (if any)", example = "1")
    private Long userProfileId;

    @Schema(description = "Set of role IDs (alternative to role string)", example = "[1,2]")
    private Set<Long> roleIds;

    public UserRequestDTO() {}
}
