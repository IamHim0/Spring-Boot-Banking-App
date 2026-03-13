package com.cfkiatong.springbootbankingapp.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AuthenticationRequest(
        @NotBlank(message = "username field cannot be empty")
        String username,
        @NotBlank(message = "password field cannot be empty")
        String password) {
    
}