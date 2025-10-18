package com.morago_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "languages")
public class Language extends BaseEntity {

    @Column(name = "name", length = 200, nullable = false)
    private String name;

    //Back-reference to translators
    @ManyToMany(mappedBy = "languages")
    private Set<TranslatorProfile> translatorProfiles = new HashSet<>();
}
