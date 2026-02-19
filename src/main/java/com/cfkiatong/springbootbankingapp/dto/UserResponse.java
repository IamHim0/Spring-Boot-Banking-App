package com.cfkiatong.springbootbankingapp.dto;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class UserResponse {

    private String email;
    private String username;
    private String firstName;
    private String lastName;
    private Set<Role> roles;
    private List<UUID> accountIds;

    public UserResponse(String email, String username, String firstName, String lastName, Set<Role> roles, List<UUID> accountIds) {
        this.email = email;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roles = roles;
        this.accountIds = accountIds;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public List<UUID> getAccountIds() {
        return accountIds;
    }
}