package com.cfkiatong.springbootbankingapp.dto;

public class ChangeAccountOwnerRequest {

    private String newAccountOwner;

    public String getNewAccountOwner() {
        return newAccountOwner;
    }

    public void setNewAccountOwner(String newAccountOwner) {
        this.newAccountOwner = newAccountOwner;
    }
}
