package com.morago_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "notifications")
public class Notification extends BaseEntity {

    @Column(name = "title_varchar200", length = 200)
    private String title;

    @Column(name = "text_varchar1000", length = 1000)
    private String text;

    @Column(name = "date_date")
    private java.time.LocalDate date;

    @Column(name = "time_time")
    private java.time.LocalTime time;

    @Column(name = "user_id_bigint")
    private Long userId;

}