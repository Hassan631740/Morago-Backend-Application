package com.morago_backend.dto.dtoResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "Response DTO for Flyway schema history row")
public class FlywaySchemaHistoryResponseDTO {

    @Schema(description = "Primary key", example = "1")
    private Long installedRank;

    @Schema(description = "Version string", example = "1")
    private String version;

    @Schema(description = "Description", example = "create_tables")
    private String description;

    @Schema(description = "Type", example = "SQL")
    private String type;

    @Schema(description = "Script file name", example = "V1__create_tables.sql")
    private String script;

    @Schema(description = "Checksum", example = "123456789")
    private Integer checksum;

    @Schema(description = "Installed by user", example = "postgres")
    private String installedBy;

    @Schema(description = "Installed on timestamp")
    private LocalDateTime installedOn;

    @Schema(description = "Execution time in ms", example = "45")
    private Integer executionTime;

    @Schema(description = "Success flag", example = "true")
    private Boolean success;

}


