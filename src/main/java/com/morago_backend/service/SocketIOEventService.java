package com.morago_backend.service;

import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.corundumstudio.socketio.annotation.OnEvent;
import java.util.UUID;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class SocketIOEventService {

    private final SocketIOServer server;
    private SocketIONamespace notificationsNs;
    private SocketIONamespace callsNs;

    public SocketIOEventService(SocketIOServer server) {
        this.server = server;
    }

    @PostConstruct
    public void init() {
        this.notificationsNs = server.addNamespace("/notifications");
        this.callsNs = server.addNamespace("/calls");

        ConnectListener commonConnect = client -> { };
        DisconnectListener commonDisconnect = client -> { };

        notificationsNs.addConnectListener(commonConnect);
        notificationsNs.addDisconnectListener(commonDisconnect);
        callsNs.addConnectListener(commonConnect);
        callsNs.addDisconnectListener(commonDisconnect);
    }

    @OnEvent("notify")
    public void onNotify(com.corundumstudio.socketio.SocketIOClient client, String message) {
        notificationsNs.getBroadcastOperations().sendEvent("notify", message);
    }

    // Call signaling events for WebRTC: offer, answer, candidate, hangup
    @OnEvent("offer")
    public void onOffer(com.corundumstudio.socketio.SocketIOClient client, SignalPayload payload) {
        callsNs.getClient(UUID.fromString(payload.getTo())).sendEvent("offer", payload);
    }

    @OnEvent("answer")
    public void onAnswer(com.corundumstudio.socketio.SocketIOClient client, SignalPayload payload) {
        callsNs.getClient(UUID.fromString(payload.getTo())).sendEvent("answer", payload);
    }

    @OnEvent("candidate")
    public void onCandidate(com.corundumstudio.socketio.SocketIOClient client, SignalPayload payload) {
        callsNs.getClient(UUID.fromString(payload.getTo())).sendEvent("candidate", payload);
    }

    @OnEvent("hangup")
    public void onHangup(com.corundumstudio.socketio.SocketIOClient client, SignalPayload payload) {
        callsNs.getClient(UUID.fromString(payload.getTo())).sendEvent("hangup", payload);
    }

    public static class SignalPayload {
        private String from;
        private String to;
        private Object data;

        public String getFrom() { return from; }
        public void setFrom(String from) { this.from = from; }
        public String getTo() { return to; }
        public void setTo(String to) { this.to = to; }
        public Object getData() { return data; }
        public void setData(Object data) { this.data = data; }
    }
}


