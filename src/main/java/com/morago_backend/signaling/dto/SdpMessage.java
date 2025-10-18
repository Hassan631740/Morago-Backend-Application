package com.morago_backend.signaling.dto;

import lombok.Data;

@Data
public class SdpMessage {
    private String roomId;
    private String sdp;   // session description
    private String type;  // "offer" or "answer"
}
