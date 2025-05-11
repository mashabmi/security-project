package com.example.security.payload.request;

import com.example.security.enums.UserRole;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    private UserRole role;

    private String captchaResponse;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getCaptchaResponse() {
        return captchaResponse;
    }

    public void setCaptcha(String captcha) {
        this.captchaResponse = captcha;
    }
}
