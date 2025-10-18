package com.morago_backend.dto.dtoResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Schema(description = "Response DTO for User")
public class UserResponseDTO {

    @Schema(description = "User ID", example = "1")
    private Long id;

    @Schema(description = "Phone number", example = "+1234567890")
    private String phone;

    @Schema(description = "First name", example = "John")
    private String firstName;

    @Schema(description = "Last name", example = "Doe")
    private String lastName;

    @Schema(description = "Account balance", example = "100.50")
    private BigDecimal balance;

    @Schema(description = "Average ratings", example = "4.5")
    private BigDecimal ratings;

    @Schema(description = "Total number of ratings", example = "25")
    private Integer totalRatings;

    @Schema(description = "Is user active", example = "true")
    private Boolean isActive;

    @Schema(description = "Is user a debtor", example = "false")
    private Boolean isDebtor;

    @Schema(description = "User profile ID", example = "1")
    private Long userProfileId;

    @Schema(description = "Translator profile ID", example = "1")
    private Long translatorProfileId;

    @Schema(description = "Admin profile ID (if user is an administrator)", example = "1")
    private Long adminId;

    @Schema(description = "Profile image ID", example = "1")
    private Long imageId;

    @Schema(description = "Roles assigned to the user", example = "[\"CLIENT\", \"ADMIN\"]")
    private Set<String> roles;

    @Schema(description = "Category creation timestamp")
    private LocalDateTime createdAtDatetime;

    @Schema(description = "Category last update timestamp")
    private LocalDateTime updatedAtDatetime;

}
