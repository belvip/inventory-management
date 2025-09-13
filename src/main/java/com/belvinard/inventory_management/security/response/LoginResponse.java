package com.belvinard.inventory_management.security.response;

import java.util.List;

public class LoginResponse {
    private String jwtToken;
    private String refreshToken;
    private String username;
    private List<String> roles;

    public LoginResponse(String username, List<String> roles, String jwtToken, String refreshToken) {
        this.username = username;
        this.roles = roles;
        this.jwtToken = jwtToken;
        this.refreshToken = refreshToken;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
