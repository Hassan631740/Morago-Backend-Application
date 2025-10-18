package com.morago_backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    private String id;
    private String name;
    private String createdBy;
    private Set<String> participants = new HashSet<>();
}
