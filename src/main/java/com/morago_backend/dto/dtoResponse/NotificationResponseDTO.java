package com.morago_backend.dto.dtoResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@Schema(description = "Response DTO for Notification")
public class NotificationResponseDTO {

    @Schema(description = "Notification ID", example = "1")
    private Long id;

    @Schema(description = "Title", example = "Welcome")
    private String title;

    @Schema(description = "Text body", example = "Thanks for joining Morago!")
    private String text;

    @Schema(description = "Date", example = "2025-09-30")
    private LocalDate date;

    @Schema(description = "Time", example = "14:30:00")
    private LocalTime time;

    @Schema(description = "User ID", example = "42")
    private Long userId;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAtDatetime;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAtDatetime;

}


