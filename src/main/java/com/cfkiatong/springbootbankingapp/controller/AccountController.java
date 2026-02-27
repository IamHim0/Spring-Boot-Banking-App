package com.cfkiatong.springbootbankingapp.controller;

import com.cfkiatong.springbootbankingapp.dto.response.GetAccountResponse;
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
    public ResponseEntity<GetAccountResponse> createAccount(@AuthenticationPrincipal UserDetails userDetails) {
        GetAccountResponse accountResponse = accountService.createAccount(UUID.fromString(userDetails.getUsername()));

        return ResponseEntity.created(URI.create("/api/v1/users/me/accounts/" + accountResponse.getId())).body(accountResponse);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<GetAccountResponse> getAccount(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID accountId) {
        GetAccountResponse accountResponse = accountService.getAccount(UUID.fromString(userDetails.getUsername()), accountId);

        return ResponseEntity.ok(accountResponse);
    }

    @PatchMapping("/{accountId}")
    public ResponseEntity<GetAccountResponse> changeAccountOwner(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID accountId, @RequestBody ChangeAccountOwnerRequest changeAccountOwnerRequest) {
        return ResponseEntity.ok(accountService.changeAccountOwner(UUID.fromString(userDetails.getUsername()), accountId, changeAccountOwnerRequest));
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> deleteAccount(@AuthenticationPrincipal UserDetails userDetails) {
        accountService.deleteAccount(UUID.fromString(userDetails.getUsername()));

        return ResponseEntity.noContent().build();
    }

}