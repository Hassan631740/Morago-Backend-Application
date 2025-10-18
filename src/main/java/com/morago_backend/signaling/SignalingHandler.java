package com.morago_backend.signaling;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.morago_backend.signaling.dto.IceCandidateMessage;
import com.morago_backend.signaling.dto.JoinRoomRequest;
import com.morago_backend.signaling.dto.SdpMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class SignalingHandler {

    // Mapping of roomId -> Map of userId -> Participant (client + status)
    private final ConcurrentHashMap<String, Map<String, Participant>> rooms = new ConcurrentHashMap<>();

    // Called when a client connects to the server
    @OnConnect
    public void onConnect(SocketIOClient client) {
        log.info("Client connected: {}", client.getSessionId());
    }

    // Called when a client disconnects from the server
    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        log.info("Client disconnected: {}", client.getSessionId());

        // Remove participant from all rooms
        for (String roomId : rooms.keySet()) {
            Map<String, Participant> participants = rooms.get(roomId);
            if (participants != null) {
                boolean removed = participants.values().removeIf(p ->
                        p.getClient().getSessionId().equals(client.getSessionId())
                );
                if (removed) {
                    broadcastStatus(roomId); // Notify remaining participants
                }
            }
        }
    }

    // Event when a user joins a room
    @OnEvent("join")
    public void onJoin(SocketIOClient client, JoinRoomRequest request) {
        rooms.putIfAbsent(request.getRoomId(), new ConcurrentHashMap<>());
        rooms.get(request.getRoomId())
                .put(request.getUserId(), new Participant(client, "online"));

        log.info("User {} joined room {}", request.getUserId(), request.getRoomId());

        // Broadcast updated participant status to all in the room
        broadcastStatus(request.getRoomId());
    }

    // Event when an SDP offer is received
    @OnEvent("offer")
    public void onOffer(SocketIOClient client, SdpMessage offer) {
        log.info("Received SDP offer in room {} from type {}", offer.getRoomId(), offer.getType());
        broadcastToRoomExceptSender(client, offer.getRoomId(), "offer", offer);
    }

    // Event when an SDP answer is received
    @OnEvent("answer")
    public void onAnswer(SocketIOClient client, SdpMessage answer) {
        log.info("Received SDP answer in room {} from type {}", answer.getRoomId(), answer.getType());
        broadcastToRoomExceptSender(client, answer.getRoomId(), "answer", answer);
    }

    // Event when an ICE candidate is received
    @OnEvent("candidate")
    public void onCandidate(SocketIOClient client, IceCandidateMessage candidate) {
        log.info("Received ICE candidate in room {}", candidate.getRoomId());
        broadcastToRoomExceptSender(client, candidate.getRoomId(), "candidate", candidate);
    }

    // Broadcast a message to all participants in the room except the sender
    private void broadcastToRoomExceptSender(SocketIOClient sender, String roomId, String event, Object data) {
        Map<String, Participant> participants = rooms.get(roomId);
        if (participants != null) {
            participants.values().forEach(p -> {
                if (!p.getClient().getSessionId().equals(sender.getSessionId())) {
                    p.getClient().sendEvent(event, data);
                }
            });
        }
    }

    // Broadcast the participant status map to all clients in the room
    private void broadcastStatus(String roomId) {
        Map<String, Participant> participants = rooms.get(roomId);
        if (participants != null) {
            Map<String, String> statusMap = new HashMap<>();
            participants.forEach((userId, participant) -> statusMap.put(userId, participant.getStatus()));

            participants.values().forEach(p -> p.getClient().sendEvent("participant-status", statusMap));
        }
    }
}
