package com.morago_backend.dto.dtoResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter

/**
 * DTO for Deposit response data
 */
@Schema(description = "Deposit response data")
public class DepositResponseDTO {

    @Schema(description = "Deposit ID", example = "1")
    private Long id;

    @Schema(description = "Account holder name", example = "John Doe")
    private String accountHolder;

    @Schema(description = "Name of the bank", example = "Chase Bank")
    private String bankName;

    @Schema(description = "Deposit amount", example = "1000.00")
    private BigDecimal sum;

    @Schema(description = "Deposit status", example = "PENDING")
    private String status;

    @Schema(description = "User ID making the deposit", example = "1")
    private Long userId;

    @Schema(description = "Deposit creation timestamp")
    private LocalDateTime createdAtDatetime;

    @Schema(description = "Deposit last update timestamp")
    private LocalDateTime updatedAtDatetime;

    // Constructors
    public DepositResponseDTO() {}

    public DepositResponseDTO(Long id, String accountHolder, String bankName, BigDecimal sum,
                              String status, Long userId, LocalDateTime createdAtDatetime,
                              LocalDateTime updatedAtDatetime) {
        this.id = id;
        this.accountHolder = accountHolder;
        this.bankName = bankName;
        this.sum = sum;
        this.status = status;
        this.userId = userId;
        this.createdAtDatetime = createdAtDatetime;
        this.updatedAtDatetime = updatedAtDatetime;
    }


}
