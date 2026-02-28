package com.cfkiatong.springbootbankingapp.dto.response;

import com.cfkiatong.springbootbankingapp.dto.Role;

import java.util.Set;

public record UserRolesResponse(String username, Set<Role> roles) {
}
