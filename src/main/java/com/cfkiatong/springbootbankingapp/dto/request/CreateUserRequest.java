package com.cfkiatong.springbootbankingapp.dto.request;

import com.cfkiatong.springbootbankingapp.dto.Role;

import java.util.Set;

public class CreateUserRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String password;
    private Set<Role> roles;

    public CreateUserRequest(String firstName, String lastName, String email, String username, String password, Set<Role> roles) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.roles = roles;
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

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Set<Role> getRoles() {
        return roles;
    }
}
