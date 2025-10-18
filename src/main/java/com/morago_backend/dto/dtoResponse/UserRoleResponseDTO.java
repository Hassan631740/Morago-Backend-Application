package com.morago_backend.dto.dtoResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Schema(description = "User data with roles")
public class UserRoleResponseDTO {

    @Schema(description = "User ID", example = "1")
    private Long id;

    @Schema(description = "User phone number", example = "+1234567890")
    private String phone;

    @Schema(description = "User first name", example = "John")
    private String firstName;

    @Schema(description = "User last name", example = "Doe")
    private String lastName;

    @Schema(description = "Roles assigned to the user", example = "[\"CLIENT\", \"ADMINISTRATOR\"]")
    private Set<String> roles;
}
