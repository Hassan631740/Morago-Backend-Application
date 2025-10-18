package com.morago_backend.dto.dtoResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter

/**
 * DTO for Debtor response data
 */
@Schema(description = "Debtor response data")
public class DebtorResponseDTO {

    @Schema(description = "Debtor ID", example = "1")
    private Long id;

    @Schema(description = "Account holder name", example = "John Doe")
    private String accountHolder;

    @Schema(description = "Name of the bank", example = "Chase Bank")
    private String bankName;

    @Schema(description = "Whether the debt is paid", example = "false")
    private Boolean paid;

    @Schema(description = "User ID associated with this debtor", example = "1")
    private Long userId;

    @Schema(description = "Debt amount owed", example = "100.50")
    private BigDecimal debtAmount;

    @Schema(description = "Debtor creation timestamp")
    private LocalDateTime createdAtDatetime;

    @Schema(description = "Debtor last update timestamp")
    private LocalDateTime updatedAtDatetime;

    // Constructors
    public DebtorResponseDTO() {}

    public DebtorResponseDTO(Long id, String accountHolder, String bankName, Boolean paid,
                             Long userId, BigDecimal debtAmount, LocalDateTime createdAtDatetime, LocalDateTime updatedAtDatetime) {
        this.id = id;
        this.accountHolder = accountHolder;
        this.bankName = bankName;
        this.paid = paid;
        this.userId = userId;
        this.debtAmount = debtAmount;
        this.createdAtDatetime = createdAtDatetime;
        this.updatedAtDatetime = updatedAtDatetime;
    }
}

