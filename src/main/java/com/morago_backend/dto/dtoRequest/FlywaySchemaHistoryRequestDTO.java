package com.morago_backend.dto.dtoRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request DTO for Flyway schema history row")
public class FlywaySchemaHistoryRequestDTO {

    public interface Create {}
    public interface Update {}

    @Schema(description = "Version string", example = "1")
    @NotBlank(message = "Version is required", groups = Create.class)
    private String version;

    @Schema(description = "Description", example = "create_tables")
    @NotBlank(message = "Description is required", groups = Create.class)
    private String description;

    @Schema(description = "Type", example = "SQL")
    @NotBlank(message = "Type is required", groups = Create.class)
    private String type;

    @Schema(description = "Script file name", example = "V1__create_tables.sql")
    @NotBlank(message = "Script is required", groups = Create.class)
    private String script;

    @Schema(description = "Checksum", example = "123456789")
    private Integer checksum;

    @Schema(description = "Installed by user", example = "postgres")
    private String installedBy;

    @Schema(description = "Execution time in ms", example = "45")
    @PositiveOrZero(message = "Execution time cannot be negative")
    private Integer executionTime;

    @Schema(description = "Success flag", example = "true")
    private Boolean success;

}


