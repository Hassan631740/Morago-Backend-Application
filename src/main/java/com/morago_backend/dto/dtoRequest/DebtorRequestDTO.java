package com.morago_backend.dto.dtoRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter

/**
 * DTO for Debtor request operations
 */
@Schema(description = "Debtor request data")
public class DebtorRequestDTO {

    @Schema(description = "Account holder name", example = "John Doe")
    @NotBlank(message = "Account holder name is required")
    @Size(max = 200, message = "Account holder name cannot exceed 200 characters")
    private String accountHolder;

    @Schema(description = "Name of the bank", example = "Chase Bank")
    @NotBlank(message = "Bank name is required")
    @Size(max = 200, message = "Bank name cannot exceed 200 characters")
    private String bankName;

    @Schema(description = "Whether the debt is paid", example = "false")
    private Boolean paid;

    @Schema(description = "User ID associated with this debtor", example = "1")
    @NotNull(message = "User ID is required")
    private Long userId;

    @Schema(description = "Debt amount owed", example = "100.50")
    private BigDecimal debtAmount;

    // Constructors
    public DebtorRequestDTO() {}

    public DebtorRequestDTO(String accountHolder, String bankName, Boolean paid, Long userId, BigDecimal debtAmount) {
        this.accountHolder = accountHolder;
        this.bankName = bankName;
        this.paid = paid;
        this.userId = userId;
        this.debtAmount = debtAmount;
    }
}
