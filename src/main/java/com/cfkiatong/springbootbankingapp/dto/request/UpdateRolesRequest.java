package com.cfkiatong.springbootbankingapp.dto.request;

import com.cfkiatong.springbootbankingapp.dto.Role;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record UpdateRolesRequest(@NotNull(message = "Must add or delete a role.") String action,
                                 @NotNull(message = "Roles field cannot be empty.") Set<Role> roles) {
}