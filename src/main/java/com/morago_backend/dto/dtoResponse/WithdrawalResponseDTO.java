package com.morago_backend.dto.dtoResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Getter
@Setter

/**
 * DTO for Withdrawal response data
 */
@Schema(description = "Withdrawal response data")
public class WithdrawalResponseDTO {

    @Schema(description = "Withdrawal ID", example = "1")
    private Long id;

    @Schema(description = "Account number", example = "1234567890")
    private String accountNumber;

    @Schema(description = "Account holder name", example = "John Doe")
    private String accountHolder;

    @Schema(description = "Name of the bank", example = "Chase Bank")
    private String bankName;

    @Schema(description = "Withdrawal amount", example = "500.00")
    private BigDecimal sum;

    @Schema(description = "Withdrawal status", example = "PENDING")
    private String status;

    @Schema(description = "User ID making the withdrawal", example = "1")
    private Long userId;

    @Schema(description = "Withdrawal creation timestamp")
    private LocalDateTime createdAtDatetime;

    @Schema(description = "Withdrawal last update timestamp")
    private LocalDateTime updatedAtDatetime;

    // Constructors
    public WithdrawalResponseDTO() {}

    public WithdrawalResponseDTO(Long id, String accountNumber, String accountHolder, String bankName,
                                 BigDecimal sum, String status, Long userId, LocalDateTime createdAtDatetime,
                                 LocalDateTime updatedAtDatetime) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.bankName = bankName;
        this.sum = sum;
        this.status = status;
        this.userId = userId;
        this.createdAtDatetime = createdAtDatetime;
        this.updatedAtDatetime = updatedAtDatetime;
    }

}
