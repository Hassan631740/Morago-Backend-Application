package com.morago_backend.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AuthDtos {

    public static class RegisterRequest {
        @Email
        @NotBlank
        public String email;
        @NotBlank
        public String password;
    }

    public static class LoginRequest {
        @Email
        @NotBlank
        public String email;
        @NotBlank
        public String password;
    }

    public static class AuthResponse {
        public String token;

        public AuthResponse(String token) {
            this.token = token;
        }
    }
}


