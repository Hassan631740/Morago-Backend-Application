package com.morago_backend.dto.dtoRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter

/**
 * DTO for Withdrawal request operations
 */
@Schema(description = "Withdrawal request data")
public class WithdrawalRequestDTO {

    @Schema(description = "Account number", example = "1234567890")
    @NotBlank(message = "Account number is required")
    @Size(max = 200, message = "Account number cannot exceed 200 characters")
    private String accountNumber;

    @Schema(description = "Account holder name", example = "John Doe")
    @NotBlank(message = "Account holder name is required")
    @Size(max = 200, message = "Account holder name cannot exceed 200 characters")
    private String accountHolder;

    @Schema(description = "Name of the bank", example = "Chase Bank")
    @NotBlank(message = "Bank name is required")
    @Size(max = 200, message = "Bank name cannot exceed 200 characters")
    private String bankName;

    @Schema(description = "Withdrawal amount", example = "500.00")
    @NotNull(message = "Withdrawal amount is required")
    @DecimalMin(value = "0.01", message = "Withdrawal amount must be greater than 0")
    private BigDecimal sum;

    @Schema(description = "Withdrawal status", example = "PENDING", allowableValues = {"PENDING", "APPROVED", "REJECTED"})
    @Size(max = 50, message = "Status cannot exceed 50 characters")
    private String status;

    @Schema(description = "User ID making the withdrawal", example = "1")
    @NotNull(message = "User ID is required")
    private Long userId;

    // Constructors
    public WithdrawalRequestDTO() {}

    public WithdrawalRequestDTO(String accountNumber, String accountHolder, String bankName,
                                BigDecimal sum, String status, Long userId) {
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.bankName = bankName;
        this.sum = sum;
        this.status = status;
        this.userId = userId;
    }
}
