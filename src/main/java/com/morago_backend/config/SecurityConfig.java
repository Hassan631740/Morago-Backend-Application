package com.morago_backend.config;

import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfig = new org.springframework.web.cors.CorsConfiguration();
                    corsConfig.setAllowedOrigins(List.of("http://localhost:5173",
                            "https://healthcheck.railway.app")); // frontend URL
                    corsConfig.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
                    corsConfig.setAllowedHeaders(List.of("*"));
                    corsConfig.setAllowCredentials(true);
                    return corsConfig;
                }))

                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Allow all Swagger/OpenAPI endpoints without authentication
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/swagger-ui/index.html").permitAll()
                        .requestMatchers("/v3/api-docs", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-resources/**", "/configuration/**", "/webjars/**", "/actuator/**","/h2-console/**").permitAll()
                        // Allow other public endpoints
                        .requestMatchers("/", "/api/auth/**", "/api/tokens/**", "/api/password-resets/**", "/webrtc-test.html", "/socket.io/**", "/actuator/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMINISTRATOR")
                        .requestMatchers("/api/roles/**").hasRole("ADMINISTRATOR")
                        // Allow authenticated users to access their own profile and change password
                        .requestMatchers("/api/users/deposit").hasRole("CLIENT")
                        .requestMatchers("/api/users/me", "/api/users/password", "/api/users/balance").hasAnyRole("CLIENT", "INTERPRETER", "ADMINISTRATOR")
                        .requestMatchers("/api/users/all/**").hasRole("ADMINISTRATOR") // Only list/all endpoints are admin-only
                        .requestMatchers("/api/users/**").authenticated() // All other user endpoints accessible to authenticated users
                        .requestMatchers("/api/translator-profiles/**").hasAnyRole("INTERPRETER", "ADMINISTRATOR")
                        .requestMatchers("/api/calls/**").hasAnyRole("CLIENT", "INTERPRETER", "ADMINISTRATOR")
                        .requestMatchers("/api/ratings/**").hasAnyRole("CLIENT", "INTERPRETER", "ADMINISTRATOR")
                        .requestMatchers("/api/deposits/**", "/api/withdrawals/**").hasRole("ADMINISTRATOR")
                        .requestMatchers("/api/categories/**", "/api/languages/**", "/api/themes/**").hasRole("ADMINISTRATOR")
                        .requestMatchers("/api/files/**", "/api/file-resources/**").hasAnyRole("CLIENT", "INTERPRETER", "ADMINISTRATOR")
                        .requestMatchers("/api/uploads/themes/**").hasRole("ADMINISTRATOR")
                        .requestMatchers("/api/uploads/**").authenticated()
                        .requestMatchers("/api/notifications/**").hasAnyRole("CLIENT", "INTERPRETER", "ADMINISTRATOR")
                        .anyRequest().authenticated()
                )
                .userDetailsService(userDetailsService)
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(customJwtAuthenticationConverter()))
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public JwtDecoder jwtDecoder(@Value("${jwt.secret}") String base64Secret) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret));
        return NimbusJwtDecoder.withSecretKey(key).build();
    }

    @Bean
    public JwtAuthenticationConverter customJwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            List<String> roles = jwt.getClaimAsStringList("roles");
            if (roles == null) roles = List.of();
            return roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toList());
        });
        return converter;
    }
}