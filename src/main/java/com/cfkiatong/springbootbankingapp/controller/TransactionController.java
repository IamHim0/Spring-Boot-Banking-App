package com.cfkiatong.springbootbankingapp.controller;

import com.cfkiatong.springbootbankingapp.dto.*;
import com.cfkiatong.springbootbankingapp.exception.business.NoFieldUpdatedException;
import com.cfkiatong.springbootbankingapp.exception.business.NoTargetAccountException;
import com.cfkiatong.springbootbankingapp.services.TransactionService;
import io.jsonwebtoken.Jwt;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/accounts")
public class TransactionController {

    private final TransactionService services;

    public TransactionController (TransactionService services) {
        this.services = services;
    }

    //Create Account
    @PostMapping
    public ResponseEntity<ViewAccountResponse> addAccount(@Valid @RequestBody CreateAccountRequest createAccountRequest) {
        ViewAccountResponse accountResponse = services.addAccount(createAccountRequest);

        return ResponseEntity.created(URI.create("/api/v1/accounts/" + accountResponse.getId())).body(accountResponse);
    }



    //ID BASED MAPPING:
    @GetMapping("/me")
    public ResponseEntity<ViewAccountResponse> getAccount(@AuthenticationPrincipal UserDetails userDetails) {
        ViewAccountResponse accountResponse = services.getAccount(UUID.fromString(userDetails.getUsername()));

        return ResponseEntity.ok(accountResponse);
    }

    @PatchMapping("/me")
    public ResponseEntity<ViewAccountResponse> updateAccount(@AuthenticationPrincipal UserDetails userDetails, @Validated @RequestBody UpdateAccountRequest updateAccountRequest) {
        if (!updateAccountRequest.oneFieldPresent()) {
            throw new NoFieldUpdatedException();
        }

        ViewAccountResponse updatedAccount = services.updateAccount(UUID.fromString(userDetails.getUsername()), updateAccountRequest);

        return ResponseEntity.ok(updatedAccount);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteAccount(@AuthenticationPrincipal UserDetails userDetails) {
        services.deleteAccount(UUID.fromString(userDetails.getUsername()));

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/balance")
    public ResponseEntity<ViewBalanceResponse> viewBalance(@AuthenticationPrincipal UserDetails userDetails) {
        ViewBalanceResponse balanceResponse = services.viewBalance(UUID.fromString(userDetails.getUsername()));

        return ResponseEntity.ok(balanceResponse);
    }

    @PostMapping("/me/transactions")
    public ResponseEntity<ViewBalanceResponse> makeTransaction(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody TransactionRequest transactionRequest) {
        if (transactionRequest.getType() == TransactionType.TRANSFER && transactionRequest.getTargetAccountUsername() == null) {
            throw new NoTargetAccountException();
        }

        ViewBalanceResponse transactionResponse = services.makeTransaction(UUID.fromString(userDetails.getUsername()), transactionRequest);

        return ResponseEntity.ok(transactionResponse);
    }

}