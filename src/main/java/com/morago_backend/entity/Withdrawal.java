package com.morago_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@Entity
@Table(name = "withdrawals")
public class Withdrawal extends BaseEntity {

    @Column(name = "account_number_varchar200", length = 200)
    private String accountNumber;

    @Column(name = "account_holder_varchar200", length = 200)
    private String accountHolder;

    @Column(name = "name_of_bank_varchar200", length = 200)
    private String bankName;

    @Column(name = "sum_decimal12_2", precision = 12, scale = 2)
    private BigDecimal sum;

    @Column(name = "status_varchar50", length = 50)
    private String status;  // PENDING, APPROVED, REJECTED

    @Column(name = "user_id_bigint")
    private Long userId;

}
