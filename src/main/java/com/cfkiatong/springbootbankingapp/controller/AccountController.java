package com.cfkiatong.springbootbankingapp.controller;

import com.cfkiatong.springbootbankingapp.dto.CreateAccountRequest;
import com.cfkiatong.springbootbankingapp.dto.UpdateAccountRequest;
import com.cfkiatong.springbootbankingapp.dto.ViewAccountResponse;
import com.cfkiatong.springbootbankingapp.exception.business.NoFieldUpdatedException;
import com.cfkiatong.springbootbankingapp.services.AccountService;
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
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    //Create Account
    @PostMapping
    public ResponseEntity<ViewAccountResponse> addAccount(@Valid @RequestBody CreateAccountRequest createAccountRequest) {
        ViewAccountResponse accountResponse = accountService.addAccount(createAccountRequest);

        return ResponseEntity.created(URI.create("/api/v1/accounts/" + accountResponse.getId())).body(accountResponse);
    }

    @GetMapping("/get")
    public ResponseEntity<ViewAccountResponse> getAccount(@AuthenticationPrincipal UserDetails userDetails) {
        ViewAccountResponse accountResponse = accountService.getAccount(UUID.fromString(userDetails.getUsername()));

        return ResponseEntity.ok(accountResponse);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteAccount(@AuthenticationPrincipal UserDetails userDetails) {
        accountService.deleteAccount(UUID.fromString(userDetails.getUsername()));

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/update")
    public ResponseEntity<ViewAccountResponse> updateAccount(@AuthenticationPrincipal UserDetails userDetails, @Validated @RequestBody UpdateAccountRequest updateAccountRequest) {
        if (!updateAccountRequest.oneFieldPresent()) {
            throw new NoFieldUpdatedException();
        }

        ViewAccountResponse updatedAccount = accountService.updateAccount(UUID.fromString(userDetails.getUsername()), updateAccountRequest);

        return ResponseEntity.ok(updatedAccount);
    }

}