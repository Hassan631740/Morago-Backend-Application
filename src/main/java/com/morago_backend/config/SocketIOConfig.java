package com.morago_backend.config;

import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import java.util.List;
import java.util.Map;

@Configuration
public class SocketIOConfig {

    @Value("${socketio.host}")
    private String host;

    @Value("${socketio.port}")
    private String portStr;

    @Value("${socketio.allowed-origins}")
    private String allowedOrigins;

    private final JwtDecoder jwtDecoder;

    public SocketIOConfig(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }
    
    private int getSocketPort() {
        try {
            int port = Integer.parseInt(portStr);
            if (port <= 0 || port > 65535) {
                throw new IllegalArgumentException("Port must be between 1 and 65535, got: " + port);
            }
            return port;
        } catch (NumberFormatException e) {
            System.err.println("ERROR: Invalid SOCKETIO_PORT value. Expected a number but got: " + portStr);
            System.err.println("Please set SOCKETIO_PORT to a valid port number (e.g., 9092) in your Railway environment variables.");
            throw new IllegalArgumentException("SOCKETIO_PORT must be a valid port number. Current value: " + portStr, e);
        }
    }

    @Bean
    public SocketIOServer socketIOServer() {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname(host);
        config.setPort(getSocketPort());
        config.setOrigin(allowedOrigins);

        SocketIOServer server = new SocketIOServer(config);

        server.addConnectListener(client -> {
            System.out.println("New client connecting...");
            // Read token sent via auth
            Map<String, List<String>> authParams = client.getHandshakeData().getUrlParams();
            String token = null;
            if (authParams != null && authParams.containsKey("token")) {
                token = authParams.get("token").get(0);
            }
            System.out.println("JWT received: " + token);

            if (token == null || !isValidToken(token)) {
                System.out.println("JWT invalid or missing. Disconnecting client.");
                client.disconnect();
            } else {
                System.out.println("JWT valid. Client connected.");
            }
        });

        return server;
    }

    private boolean isValidToken(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            return true;
        } catch (JwtException e) {
            System.out.println("JWT decode failed: " + e.getMessage());
            return false;
        }
    }
}
