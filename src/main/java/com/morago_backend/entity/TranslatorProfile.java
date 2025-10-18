package com.morago_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "translator_profiles")
public class TranslatorProfile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "date_of_birth", length = 50)
    private LocalDate dateOfBirth;

    @Column(length = 255)
    private String email;

    @Column(name = "is_available")
    private Boolean isAvailable;

    @Column(name = "is_online")
    private Boolean isOnline;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Column(name = "level_of_korean", length = 200)
    private String levelOfKorean;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "translatorProfile")
    private User user;

    //Many-to-Many relationship with Language
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "translator_languages",
            joinColumns = @JoinColumn(name = "translator_profile_id"),
            inverseJoinColumns = @JoinColumn(name = "language_id")
    )
    private Set<Language> languages = new HashSet<>();

    //Many-to-Many relationship with Theme
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "translator_themes",
            joinColumns = @JoinColumn(name = "translator_profile_id"),
            inverseJoinColumns = @JoinColumn(name = "theme_id")
    )
    private Set<Theme> themes = new HashSet<>();

}
