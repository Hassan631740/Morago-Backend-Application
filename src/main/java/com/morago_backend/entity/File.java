package com.morago_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "files")
public class File extends BaseEntity {
    @Column(name = "original_title", length = 255)
    private String originalTitle;

    @Column(length = 255)
    private String path;

    @Column(length = 100)
    private String type;

    @ManyToOne
    @JoinColumn(name = "theme_id")
    private Theme theme;

}
