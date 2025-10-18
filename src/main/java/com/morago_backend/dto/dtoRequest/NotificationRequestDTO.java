package com.morago_backend.dto.dtoRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Schema(description = "Request DTO for creating or updating a Notification")
public class NotificationRequestDTO {

    public interface Create {}
    public interface Update {}

    @Schema(description = "Title", example = "Welcome")
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;

    @Schema(description = "Text body", example = "Thanks for joining Morago!")
    @Size(max = 1000, message = "Text cannot exceed 1000 characters")
    private String text;

    @Schema(description = "Date", example = "2025-09-30")
    private LocalDate date;

    @Schema(description = "Time", example = "14:30:00")
    private LocalTime time;

    @Schema(description = "User ID", example = "42")
    @NotNull(message = "User ID is required", groups = Create.class)
    private Long userId;

}


