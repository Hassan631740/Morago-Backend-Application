package com.morago_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "themes")
public class Theme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(length = 200)
    private String name;

    @Column(name = "korean_title", length = 200)
    private String koreanTitle;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "night_price", precision = 10, scale = 2)
    private BigDecimal nightPrice;

    @Column(length = 500)
    private String description;

    @Column(name = "is_popular")
    private Boolean isPopular;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "icon_id")
    private Long iconId;

    @Column(name = "is_active")
    private Boolean isActive;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "theme")
    private Set<File> files = new HashSet<>();

    //Back-reference to translators
    @ManyToMany(mappedBy = "themes")
    private Set<TranslatorProfile> translatorProfiles = new HashSet<>();
}
