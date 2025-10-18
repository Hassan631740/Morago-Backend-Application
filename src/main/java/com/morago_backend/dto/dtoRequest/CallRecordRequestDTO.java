package com.morago_backend.dto.dtoRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Schema(description = "Request DTO for creating or updating a CallRecord")
public class CallRecordRequestDTO {

    public interface Create {}
    public interface Update {}

    @Schema(description = "Duration of the call in seconds", example = "300")
    @NotNull(message = "Duration is required", groups = Create.class)
    @Min(value = 0, message = "Duration cannot be negative")
    private Integer durationSeconds;

    @Schema(description = "Status of the call", example = "true")
    private Boolean status;

    @Schema(description = "Total sum for the call", example = "50.00")
    @DecimalMin(value = "0.0", inclusive = true, message = "Sum must be positive")
    private BigDecimal sum;

    @Schema(description = "Commission applied to the call", example = "5.00")
    @DecimalMin(value = "0.0", inclusive = true, message = "Commission must be positive")
    private BigDecimal commission;

    @Schema(description = "Whether translator rated the call", example = "true")
    private Boolean translatorHasRated;

    @Schema(description = "Whether user rated the call", example = "false")
    private Boolean userHasRated;

    @Schema(description = "Caller user ID", example = "10")
    @NotNull(message = "Caller user ID is required", groups = Create.class)
    private Long callerUserId;

    @Schema(description = "Recipient user ID", example = "20")
    @NotNull(message = "Recipient user ID is required", groups = Create.class)
    private Long recipientUserId;

    @Schema(description = "Theme ID", example = "2")
    private Long themeId;

    @Schema(description = "Channel name", example = "room-123")
    @Size(max = 50, message = "Channel name cannot exceed 50 characters")
    private String channelName;

    @Schema(description = "Call status text", example = "COMPLETED")
    @Size(max = 50, message = "Call status cannot exceed 50 characters")
    private String callStatus;

    @Schema(description = "Is end call", example = "true")
    private Boolean endCall;

}


