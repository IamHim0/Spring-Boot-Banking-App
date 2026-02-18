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
@RequestMapping("api/v1/me/accounts/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController (TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("{id}/balance")
    public ResponseEntity<ViewBalanceResponse> viewBalance(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID accountId) {
        return ResponseEntity.ok(transactionService.viewBalance(userDetails, accountId));
    }

    @GetMapping("/{id}/transactions")
    public ResponseEntity<TransactionHistoryResponse> getTransactions(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID accountId){
        return ResponseEntity.ok(transactionService.getTransactions(userDetails, accountId));
    }

    @PostMapping("/{id}}/transactions")
    public ResponseEntity<ViewBalanceResponse> makeTransaction(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID accountId, @Valid @RequestBody TransactionRequest transactionRequest) {
        if (transactionRequest.getType() == TransactionType.TRANSFER && transactionRequest.getTargetAccountUsername() == null) {
            throw new NoTargetAccountException();
        }

        return ResponseEntity.ok(transactionService.makeTransaction(userDetails, accountId, transactionRequest));
    }

    //ADMIN ENDPOINTS
    @GetMapping("/me/transactionhistory")
    public ResponseEntity<AdminTransactionHistoryResponse>  getTransactionHistory(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(transactionService.getTransactionHistory());
    }

}