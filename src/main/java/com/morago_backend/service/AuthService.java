package com.morago_backend.service;

import com.morago_backend.entity.*;
import com.morago_backend.payload.ClientSignupRequest;
import com.morago_backend.payload.SignupResponse;
import com.morago_backend.payload.TranslatorSignupRequest;
import com.morago_backend.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecretKey jwtSecret;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       @Value("${security.jwt.secret}") String base64Secret) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret));
    }

    public String login(String phone, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(phone, password));

        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<String> roles = user.getRoles().stream()
                .map(Enum::name)
                .toList();

        long now = System.currentTimeMillis();
        long exp = now + 3600000;

        return Jwts.builder()
                .setSubject(phone)
                .claim("roles", roles)
                .claim("id", user.getId()) // Add numeric ID for frontend validation
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(exp))
                .signWith(jwtSecret)
                .compact();

    }

    public SignupResponse signupClient(ClientSignupRequest request) {
        // Check if phone already exists
        if (userRepository.findByPhone(request.getPhone()).isPresent()) {
            throw new IllegalArgumentException("Phone number already registered");
        }

        // Create user
        User user = new User();
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setIsActive(true);
        user.setIsDebtor(false);
        
        // Add CLIENT role
        Set<UserRole> roles = new HashSet<>();
        roles.add(UserRole.CLIENT);
        user.setRoles(roles);

        // Create user profile
        UserProfile userProfile = new UserProfile();
        userProfile.setCreatedAt(LocalDateTime.now());
        userProfile.setUpdatedAt(LocalDateTime.now());
        userProfile.setIsFreeCallMade(false);
        userProfile.setUser(user);
        user.setUserProfile(userProfile);

        // Save user
        User savedUser = userRepository.save(user);

        // Generate JWT token
        String token = generateToken(savedUser);

        // Return response
        return new SignupResponse(
                token,
                savedUser.getId(),
                savedUser.getPhone(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getRoles().stream().map(Enum::name).collect(Collectors.toSet())
        );
    }

    public SignupResponse signupTranslator(TranslatorSignupRequest request) {
        // Check if phone already exists
        if (userRepository.findByPhone(request.getPhone()).isPresent()) {
            throw new IllegalArgumentException("Phone number already registered");
        }

        // Create user
        User user = new User();
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setIsActive(true);
        user.setIsDebtor(false);
        
        // Add INTERPRETER role
        Set<UserRole> roles = new HashSet<>();
        roles.add(UserRole.INTERPRETER);
        user.setRoles(roles);

        // Create translator profile
        TranslatorProfile translatorProfile = new TranslatorProfile();
        translatorProfile.setEmail(request.getEmail());
        translatorProfile.setDateOfBirth(request.getDateOfBirth());
        translatorProfile.setLevelOfKorean(request.getLevelOfKorean());
        translatorProfile.setCreatedAt(LocalDateTime.now());
        translatorProfile.setUpdatedAt(LocalDateTime.now());
        translatorProfile.setIsAvailable(false);
        translatorProfile.setIsOnline(false);
        translatorProfile.setIsVerified(false); // Translator needs to be verified by admin
        translatorProfile.setUser(user);
        user.setTranslatorProfile(translatorProfile);

        // Save user
        User savedUser = userRepository.save(user);

        // Generate JWT token
        String token = generateToken(savedUser);

        // Return response
        return new SignupResponse(
                token,
                savedUser.getId(),
                savedUser.getPhone(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getRoles().stream().map(Enum::name).collect(Collectors.toSet())
        );
    }

    private String generateToken(User user) {
        List<String> roles = user.getRoles().stream()
                .map(Enum::name)
                .toList();

        long now = System.currentTimeMillis();
        long exp = now + 3600000; // 1 hour

        return Jwts.builder()
                .setSubject(user.getPhone())
                .claim("roles", roles)
                .claim("id", user.getId())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(exp))
                .signWith(jwtSecret)
                .compact();
    }
}
