package com.cfkiatong.springbootbankingapp.dto.request;

import jakarta.validation.constraints.NotBlank;

public class AuthenticationRequest {

    @NotBlank(message = "username field cannot be empty")
    private String username;
    @NotBlank(message = "password field cannot be empty")
    private String password;

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
}
