package com.cfkiatong.springbootbankingapp.controller;

import com.cfkiatong.springbootbankingapp.account.Account;
import com.cfkiatong.springbootbankingapp.services.Services;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/sbbankingapp")
public class Controller {

    private final Services services;

    public Controller(Services services) {
        this.services = services;
    }

    @PostMapping
    public void addAccount(@RequestBody Account account) {
        services.addAccount(account);
    }

    @GetMapping("/id/{id}")
    public Account getAccountById(@PathVariable UUID id) {
        return services.getAccountById(id);
    }

    @GetMapping("/username/{username}")
    public Account getAccountByUsername(@PathVariable String username) {
        return services.getAccountByUsername(username);
    }

    @DeleteMapping("/deletebyid/{id}")
    public void deleteAccount(@PathVariable UUID id) {
        services.deleteAccountById(id);
    }

    @DeleteMapping("/deletebyusername/{username}")
    public void deleteAccountByUsername(@PathVariable String username) {
        System.out.println("Method called");
        services.deleteAccountByUsername(username);
    }

    @PatchMapping("/updateaccount/{username}")
    public void updateAccount(@PathVariable String username, @RequestBody Map<String, String> body) {
        services.updateAccount(username, body.get("newFirstName"), body.get("newLastName"), body.get("newUsername"), body.get("newPassword"));
    }

}
