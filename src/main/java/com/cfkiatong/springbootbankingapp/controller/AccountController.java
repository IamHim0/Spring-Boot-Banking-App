package com.cfkiatong.springbootbankingapp.controller;

import com.cfkiatong.springbootbankingapp.dto.*;
import com.cfkiatong.springbootbankingapp.services.Services;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/accounts")
public class AccountController {

    private final Services services;

    public AccountController(Services services) {
        this.services = services;
    }

    @PostMapping
    public void addAccount(@Valid @RequestBody CreateAccountRequest createAccountRequest) {
        services.addAccount(createAccountRequest);
    }

    @GetMapping
    public ViewAccountResponse getAccountByUsername(@RequestParam String username) {
        return services.getAccountByUsername(username);
    }

    @GetMapping("/{id}")
    public ViewAccountResponse getAccountById(@PathVariable UUID id) {
        return services.getAccountById(id);
    }

    @PatchMapping
    public void updateAccount(@RequestParam String username, @Validated @RequestBody UpdateAccountRequest updateAccountRequest) {
        services.updateAccount(username, updateAccountRequest);
    }

    @PatchMapping("/{id}")
    public void updateAccount(@PathVariable UUID id, @Validated @RequestBody UpdateAccountRequest updateAccountRequest) {
        services.updateAccount(id, updateAccountRequest);
    }

    @DeleteMapping
    public void deleteAccountByUsername(@RequestParam String username) {
        services.deleteAccountByUsername(username);
    }

    @DeleteMapping("/{id}")
    public void deleteAccount(@PathVariable UUID id) {
        services.deleteAccountById(id);
    }

    @GetMapping("/balance")
    public ViewBalanceResponse viewBalanceByUsername(@RequestParam String username) {
        return services.viewBalanceByUsername(username);
    }

    @GetMapping("/{id}/balance")
    public ViewBalanceResponse viewBalance(@PathVariable UUID id) {
        return services.viewBalance(id);
    }

    @PostMapping("/transactions")
    public void makeTransactionByUsername(@RequestParam String username, @Valid @RequestBody TransactionRequest transactionRequest) {
        services.makeTransactionByUsername(username, transactionRequest);
    }

    @PostMapping("/{id}/transactions")
    public void makeTransaction(@PathVariable UUID id, @Valid @RequestBody TransactionRequest transactionRequest) {
        services.makeTransaction(id, transactionRequest);
    }
}