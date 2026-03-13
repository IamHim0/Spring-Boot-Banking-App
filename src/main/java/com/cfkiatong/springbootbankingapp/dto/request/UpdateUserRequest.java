package com.cfkiatong.springbootbankingapp.dto.request;

public record UpdateUserRequest(
        String newUsername,
        String newPassword,
        String newEmail,
        String newFirstname,
        String newLastname
) {

}
