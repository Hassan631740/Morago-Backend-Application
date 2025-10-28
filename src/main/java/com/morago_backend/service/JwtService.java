package com.morago_backend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.expiration-ms:3600000}")
    private String expirationMsStr;
    
    private long getExpirationMs() {
        try {
            long expirationMs = Long.parseLong(expirationMsStr);
            if (expirationMs <= 0) {
                throw new IllegalArgumentException("JWT expiration must be greater than 0, got: " + expirationMs);
            }
            return expirationMs;
        } catch (NumberFormatException e) {
            System.err.println("ERROR: Invalid security.jwt.expiration-ms value. Expected a number but got: " + expirationMsStr);
            System.err.println("Please set JWT_EXPIRATION_MS to a valid number (e.g., 3600000) in your Railway environment variables.");
            throw new IllegalArgumentException("JWT_EXPIRATION_MS must be a valid number. Current value: " + expirationMsStr, e);
        }
    }

    public String extractUsername(String token) {

        return extractAllClaims(token).getSubject();
    }

    public String generateToken(UserDetails userDetails) {

        return generateToken(Map.of(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + getExpirationMs());
        return Jwts.builder()
            .claims(extraClaims)
            .subject(userDetails.getUsername())
            .issuedAt(now)
            .expiration(expiry)
            .signWith(getSignInKey(), Jwts.SIG.HS256)
            .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {

        return extractAllClaims(token).getExpiration().before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
            .verifyWith(getSignInKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}


