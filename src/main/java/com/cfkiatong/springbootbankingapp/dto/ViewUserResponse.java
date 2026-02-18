package com.cfkiatong.springbootbankingapp.dto;

import java.util.List;
import java.util.UUID;

public class ViewUserResponse {

    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private List<UUID> accounts;

    public ViewUserResponse(String username, String password, String email, String firstName, String lastName, List<UUID> accounts) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.accounts = accounts;
    }
}
