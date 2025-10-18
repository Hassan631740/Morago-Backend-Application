package com.morago_backend.signaling.dto;

import lombok.Data;

@Data
public class JoinRoomRequest {
    private String roomId;
    private String userId;
}
