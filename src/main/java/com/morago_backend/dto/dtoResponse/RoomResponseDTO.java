package com.morago_backend.dto.dtoResponse;

import lombok.Data;
import java.util.Set;

@Data
public class RoomResponseDTO {
    private String id;
    private String name;
    private String createdBy;
    private Set<String> participants;
}