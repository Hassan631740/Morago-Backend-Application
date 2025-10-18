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
@Table(name = "ratings")
public class Rating extends BaseEntity {

    @Column(name = "who_user_id_bigint")
    private Long whoUserId;

    @Column(name = "to_whom_user_id_bigint")
    private Long toWhomUserId;

    @Column(name = "grade_decimal15_2", precision = 15, scale = 2)
    private BigDecimal grade;

}
