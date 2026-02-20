package com.cfkiatong.springbootbankingapp.dto;

public class UpdateUserRequest {

    private String newUsername;
    private String newPassword;
    private String newEmail;
    private String newFirstName;
    private String newLastName;

    public boolean oneFieldPresent() {
        return newUsername != null || newPassword != null || newEmail != null || newFirstName != null || newLastName != null;
    }

    public String getNewUsername() {
        return newUsername;
    }

    public void setNewUsername(String newUsername) {
        this.newUsername = newUsername;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewEmail() {
        return newEmail;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }

    public String getNewFirstName() {
        return newFirstName;
    }

    public void setNewFirstName(String newFirstName) {
        this.newFirstName = newFirstName;
    }

    public String getNewLastName() {
        return newLastName;
    }

    public void setNewLastName(String newLastName) {
        this.newLastName = newLastName;
    }
}
