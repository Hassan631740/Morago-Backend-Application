package com.morago_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "files")
public class FileResource extends BaseEntity {

    @Column(name = "origin_type", length = 255)
    private String originType;

    @Column(name = "path", length = 255)
    private String path;

    @Column(name = "type", length = 100)
    private String type;

}