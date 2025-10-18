package com.morago_backend.dto.dtoResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "Response DTO for CallRecord")
public class CallRecordResponseDTO {

    @Schema(description = "Call ID", example = "1")
    private Long id;

    @Schema(description = "Duration of the call in seconds", example = "300")
    private Integer durationSeconds;

    @Schema(description = "Status of the call", example = "true")
    private Boolean status;

    @Schema(description = "Total sum for the call", example = "50.00")
    private BigDecimal sum;

    @Schema(description = "Commission applied to the call", example = "5.00")
    private BigDecimal commission;

    @Schema(description = "Whether translator rated the call", example = "true")
    private Boolean translatorHasRated;

    @Schema(description = "Whether user rated the call", example = "false")
    private Boolean userHasRated;

    @Schema(description = "Caller user ID", example = "10")
    private Long callerUserId;

    @Schema(description = "Recipient user ID", example = "20")
    private Long recipientUserId;

    @Schema(description = "Theme ID", example = "2")
    private Long themeId;

    @Schema(description = "Channel name", example = "room-123")
    private String channelName;

    @Schema(description = "Call status text", example = "COMPLETED")
    private String callStatus;

    @Schema(description = "Is end call", example = "true")
    private Boolean endCall;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAtDatetime;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAtDatetime;

}


