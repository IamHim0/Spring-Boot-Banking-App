package com.cfkiatong.springbootbankingapp.controller;

import com.cfkiatong.springbootbankingapp.account.Account;
import com.cfkiatong.springbootbankingapp.dto.*;
import com.cfkiatong.springbootbankingapp.services.Services;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/sbbankingapp")
public class Controller {

    private final Services services;

    public Controller(Services services) {
        this.services = services;
    }

    @PostMapping
    public void addAccount(@Valid @RequestBody CreateAccountRequest createAccountRequest) {
        services.addAccount(createAccountRequest);
    }

    @GetMapping("/id/{id}")
    public ViewAccountResponse getAccountById(@PathVariable UUID id) {
        return services.getAccountById(id);
    }

    @GetMapping("/username/{username}")
    public ViewAccountResponse getAccountByUsername(@PathVariable String username) {
        return services.getAccountByUsername(username);
    }

    @PatchMapping("/updateaccount/{username}")
    public void updateAccount(@PathVariable String username, @Validated @RequestBody UpdateAccountRequest updateAccountRequest) {
        services.updateAccount(username, updateAccountRequest);
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

    @GetMapping("/viewbalance/{username}")
    public ViewBalanceResponse ViewBalance(@PathVariable String username) {
        return services.viewBalance(username);
    }

    @PatchMapping("/deposit/{username}")
    public void makeDeposit(@PathVariable String username, @Valid @RequestBody DepositRequest depositRequest) {
        services.makeDeposit(username, depositRequest);
    }

    @PatchMapping("/withdraw/{username}")
    public void makeWithdrawal(@PathVariable String username, @Valid @RequestBody WithdrawRequest withdrawRequest) {
        services.makeWithdrawal(username, withdrawRequest);
    }
}