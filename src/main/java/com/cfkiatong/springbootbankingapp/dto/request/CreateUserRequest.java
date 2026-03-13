package com.cfkiatong.springbootbankingapp.dto.request;

import com.cfkiatong.springbootbankingapp.dto.Role;

import java.util.Set;

public record CreateUserRequest(
        String firstName,
        String lastName,
        String email,
        String username,
        String password,
        Set<Role> roles
) {

}