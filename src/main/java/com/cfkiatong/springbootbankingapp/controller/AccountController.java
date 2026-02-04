package com.cfkiatong.springbootbankingapp.controller;

import com.cfkiatong.springbootbankingapp.dto.*;
import com.cfkiatong.springbootbankingapp.exception.business.NoFieldUpdatedException;
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

    //Create Account
    @PostMapping
    public void addAccount(@Valid @RequestBody CreateAccountRequest createAccountRequest) {
        services.addAccount(createAccountRequest);
    }

    //ID BASED MAPPING:
    @GetMapping("/{id}")
    public ViewAccountResponse getAccount(@PathVariable UUID id) {
        return services.getAccount(id);
    }

    @PatchMapping("/{id}")
    public void updateAccount(@PathVariable UUID id, @Validated @RequestBody UpdateAccountRequest updateAccountRequest) {
        if (!updateAccountRequest.oneFieldPresent()) {
            throw new NoFieldUpdatedException();
        }

        services.updateAccount(id, updateAccountRequest);
    }

    @DeleteMapping("/{id}")
    public void deleteAccount(@PathVariable UUID id) {
        services.deleteAccount(id);
    }

    @GetMapping("/{id}/balance")
    public ViewBalanceResponse viewBalance(@PathVariable UUID id) {
        return services.viewBalance(id);
    }

    @PostMapping("/{id}/transactions")
    public void makeTransaction(@PathVariable UUID id, @Valid @RequestBody TransactionRequest transactionRequest) {
        services.makeTransaction(id, transactionRequest);
    }


    //USERNAME BASED MAPPING:
    @GetMapping
    public ViewAccountResponse getAccountByUsername(@RequestParam String username) {
        return services.getAccountByUsername(username);
    }

    @PatchMapping
    public void updateAccountByUsername(@RequestParam String username, @RequestBody UpdateAccountRequest updateAccountRequest) {
        services.updateAccountByUsername(username, updateAccountRequest);
    }

    @DeleteMapping
    public void deleteAccountByUsername(@RequestParam String username) {
        services.deleteAccountByUsername(username);
    }

    @GetMapping("/balance")
    public ViewBalanceResponse viewBalanceByUsername(@RequestParam String username) {
        return services.viewBalanceByUsername(username);
    }

    @PostMapping("/transactions")
    public void makeTransactionByUsername(@RequestParam String username, @Valid @RequestBody TransactionRequest transactionRequest) {
        services.makeTransactionByUsername(username, transactionRequest);
    }

}