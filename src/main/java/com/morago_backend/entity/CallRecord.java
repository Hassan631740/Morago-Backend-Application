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
@Table(name = "calls")
public class CallRecord extends BaseEntity {

    @Column(name = "duration_int")
    private Integer durationSeconds;

    @Column(name = "status_bit")
    private Boolean status;

    @Column(name = "sum_decimal10_2", precision = 12, scale = 2)
    private BigDecimal sum;

    @Column(name = "commission_decimal10_2", precision = 12, scale = 2)
    private BigDecimal commission;

    @Column(name = "translator_has_rated_bit")
    private Boolean translatorHasRated;

    @Column(name = "user_has_rated_bit")
    private Boolean userHasRated;

    @Column(name = "caller_id_bigint")
    private Long callerUserId;

    @Column(name = "recipient_id_bigint")
    private Long recipientUserId;

    @Column(name = "theme_id_bigint")
    private Long themeId;

    @Column(name = "channel_name_varchar50", length = 50)
    private String channelName;

    @Column(name = "call_status_varchar50", length = 50)
    private String callStatus;

    @Column(name = "is_end_call_bit")
    private Boolean endCall;

}