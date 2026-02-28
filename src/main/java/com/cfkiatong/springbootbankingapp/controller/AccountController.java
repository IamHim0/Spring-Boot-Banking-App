package com.cfkiatong.springbootbankingapp.controller;

import com.cfkiatong.springbootbankingapp.dto.response.AccountResponse;
import com.cfkiatong.springbootbankingapp.dto.request.ChangeAccountOwnerRequest;
import com.cfkiatong.springbootbankingapp.services.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/users/me/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@AuthenticationPrincipal UserDetails userDetails) {
        AccountResponse accountResponse = accountService.createAccount(UUID.fromString(userDetails.getUsername()));

        return ResponseEntity.created(URI.create("/api/v1/users/me/accounts/" + accountResponse.getId())).body(accountResponse);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponse> getAccount(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID accountId) {
        AccountResponse accountResponse = accountService.getAccount(UUID.fromString(userDetails.getUsername()), accountId);

        return ResponseEntity.ok(accountResponse);
    }

    @PatchMapping("/{accountId}")
    public ResponseEntity<AccountResponse> changeAccountOwner(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID accountId, @RequestBody ChangeAccountOwnerRequest changeAccountOwnerRequest) {
        return ResponseEntity.ok(accountService.changeAccountOwner(UUID.fromString(userDetails.getUsername()), accountId, changeAccountOwnerRequest));
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> deleteAccount(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID accountId) {
        accountService.deleteAccount(UUID.fromString(userDetails.getUsername()), accountId);

        return ResponseEntity.noContent().build();
    }

}