package com.cfkiatong.springbootbankingapp.controller;

import com.cfkiatong.springbootbankingapp.dto.ChangeAccountTypeRequest;
import com.cfkiatong.springbootbankingapp.dto.CreateAccountRequest;
import com.cfkiatong.springbootbankingapp.dto.UpdateAccountRequest;
import com.cfkiatong.springbootbankingapp.dto.ViewAccountResponse;
import com.cfkiatong.springbootbankingapp.repository.AccountRepository;
import com.cfkiatong.springbootbankingapp.services.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.View;
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
    public ResponseEntity<ViewAccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest createAccountRequest) {
        ViewAccountResponse accountResponse = accountService.createAccount(createAccountRequest);

        return ResponseEntity.created(URI.create("/api/v1/me/accounts/" + accountResponse.getAccountId())).body(accountResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ViewAccountResponse> getAccount(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID accountId) {
        return ResponseEntity.ok(accountService.getAccount(userDetails, accountId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ViewAccountResponse> changeAccountType(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID accountId,
            @Valid @RequestBody ChangeAccountTypeRequest changeAccountTypeRequest) {
        return ResponseEntity.ok(accountService.changeAccountType(userDetails, accountId, changeAccountTypeRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID accountId) {
        accountService.deleteAccount(userDetails, accountId);

        return ResponseEntity.noContent().build();
    }

}