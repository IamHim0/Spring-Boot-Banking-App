package com.cfkiatong.springbootbankingapp.controller;

import com.cfkiatong.springbootbankingapp.dto.*;
import com.cfkiatong.springbootbankingapp.dto.request.TransactionRequest;
import com.cfkiatong.springbootbankingapp.dto.response.BalanceResponse;
import com.cfkiatong.springbootbankingapp.dto.response.TransactionResponse;
import com.cfkiatong.springbootbankingapp.exception.business.NoTargetAccountException;
import com.cfkiatong.springbootbankingapp.services.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/users/me/accounts/{accountId}/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<BalanceResponse> getBalance(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID accountId) {
        BalanceResponse balanceResponse = transactionService.getBalance(UUID.fromString(userDetails.getUsername()), accountId);

        return ResponseEntity.ok(balanceResponse);
    }

    @PostMapping
    public ResponseEntity<BalanceResponse> makeTransaction(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID accountId, @Valid @RequestBody TransactionRequest transactionRequest) {
        if (transactionRequest.type() == TransactionType.TRANSFER && transactionRequest.targetAccountId() == null) {
            throw new NoTargetAccountException();
        }

        BalanceResponse transactionResponse = transactionService.makeTransaction(UUID.fromString(userDetails.getUsername()), accountId, transactionRequest);

        return ResponseEntity.ok(transactionResponse);
    }

    @GetMapping("/userhistory")
    public ResponseEntity<List<TransactionResponse>> getUserTransactions(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(transactionService.getUserTransactions(UUID.fromString(userDetails.getUsername())));
    }

    @GetMapping("/accounthistory")
    public ResponseEntity<List<TransactionResponse>> getAccountTransactions(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID accountId) {
        return ResponseEntity.ok(transactionService.getAccountTransactions(UUID.fromString(userDetails.getUsername()), accountId));
    }

}