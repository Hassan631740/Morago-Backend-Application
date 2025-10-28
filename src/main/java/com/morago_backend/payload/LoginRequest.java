package com.morago_backend.payload;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequest {
    private String phone;
    private String password;
    
    @Override
    public String toString() {
        return "LoginRequest{" +
                "phone='" + phone + '\'' +
                ", password='***REDACTED***'" +
                '}';
    }
}
