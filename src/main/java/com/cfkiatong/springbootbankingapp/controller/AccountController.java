package com.cfkiatong.springbootbankingapp.controller;

import com.cfkiatong.springbootbankingapp.dto.*;
import com.cfkiatong.springbootbankingapp.exception.business.NoFieldUpdatedException;
import com.cfkiatong.springbootbankingapp.exception.business.NoTargetAccountException;
import com.cfkiatong.springbootbankingapp.services.Services;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.View;
import java.net.URI;
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
    public ResponseEntity<ViewAccountResponse> addAccount(@Valid @RequestBody CreateAccountRequest createAccountRequest) {
        ViewAccountResponse accountResponse = services.addAccount(createAccountRequest);

        return ResponseEntity.created(URI.create("/api/v1/accounts/" + accountResponse.getId())).body(accountResponse);
    }

    //ID BASED MAPPING:
    @GetMapping("/{id}")
    public ResponseEntity<ViewAccountResponse> getAccount(@PathVariable UUID id) {
        ViewAccountResponse accountResponse = services.getAccount(id);

        return ResponseEntity.ok(accountResponse);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ViewAccountResponse> updateAccount(@PathVariable UUID id, @Validated @RequestBody UpdateAccountRequest updateAccountRequest) {
        if (!updateAccountRequest.oneFieldPresent()) {
            throw new NoFieldUpdatedException();
        }

        ViewAccountResponse updatedAccount = services.updateAccount(id, updateAccountRequest);

        return ResponseEntity.ok(updatedAccount);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable UUID id) {
        services.deleteAccount(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<ViewBalanceResponse> viewBalance(@PathVariable UUID id) {
        ViewBalanceResponse balanceResponse = services.viewBalance(id);

        return ResponseEntity.ok(balanceResponse);
    }

    @PostMapping("/{id}/transactions")
    public ResponseEntity<ViewBalanceResponse> makeTransaction(@PathVariable UUID id, @Valid @RequestBody TransactionRequest transactionRequest) {
        if (transactionRequest.getType() == TransactionType.TRANSFER && transactionRequest.getTargetAccountUsername() == null) {
            throw new NoTargetAccountException();
        }

        ViewBalanceResponse transactionResponse = services.makeTransaction(id, transactionRequest);

        return ResponseEntity.ok(transactionResponse);
    }

}