package com.morago_backend.dto.dtoRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request DTO for creating or updating a User Profile")
public class UserProfileRequestDTO {

    public interface Create {}
    public interface Update {}

    @Schema(description = "Has the user made a free call", example = "true")
    private Boolean isFreeCallMade;

    @Schema(description = "Related user ID", example = "42")
    @NotNull(message = "User ID is required", groups = Create.class)
    private Long userId;

}


