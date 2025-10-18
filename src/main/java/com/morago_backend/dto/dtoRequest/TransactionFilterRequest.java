package com.morago_backend.dto.dtoRequest;

import com.morago_backend.entity.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionFilterRequest {
    private TransactionType transactionType;
    private String status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer page = 0;
    private Integer size = 20;
}

