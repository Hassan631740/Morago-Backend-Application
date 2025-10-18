package com.morago_backend.signaling.dto;

import lombok.Data;

@Data
public class IceCandidateMessage {
    private String roomId;
    private String candidate;
    private String sdpMid;
    private int sdpMLineIndex;
}
