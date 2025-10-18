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
@Table(name = "debtors")
public class Debtor extends BaseEntity {

    @Column(name = "account_holder_varchar200", length = 200)
    private String accountHolder;

    @Column(name = "name_of_bank_varchar200", length = 200)
    private String bankName;

    @Column(name = "is_paid_bit")
    private Boolean paid;

    @Column(name = "user_id_bigint")
    private Long userId;

    @Column(name = "debt_amount", precision = 12, scale = 2)
    private BigDecimal debtAmount;

}