package com.cfkiatong.springbootbankingapp.dto.response;

import com.cfkiatong.springbootbankingapp.dto.Role;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public record UserResponse(
        String username,
        String email,
        String firstName,
        String lastName,
        List<UUID> accountIds) {
}