package com.kinedical.dto;

import com.kinedical.model.User.Role;

public class AuthResponse {

    private String token;
    private String username;
    private String email;
    private Role role;
    private long expiresAt;

    public AuthResponse() {
    }

    public AuthResponse(String token, String username, String email, Role role, long expiresAt) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.role = role;
        this.expiresAt = expiresAt;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public long getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(long expiresAt) {
        this.expiresAt = expiresAt;
    }
}
