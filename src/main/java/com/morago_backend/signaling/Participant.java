package com.morago_backend.signaling;

import com.corundumstudio.socketio.SocketIOClient;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents a participant in a WebRTC room.
 * Stores the Socket.IO client and the participant's current status (online/offline).
 */

@Data
@AllArgsConstructor
public class Participant {
    private SocketIOClient client;
    private String status;
}