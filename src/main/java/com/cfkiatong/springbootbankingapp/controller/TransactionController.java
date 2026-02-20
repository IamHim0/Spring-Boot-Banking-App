package com.cfkiatong.springbootbankingapp.controller;

import com.cfkiatong.springbootbankingapp.dto.*;
import com.cfkiatong.springbootbankingapp.exception.business.NoTargetAccountException;
import com.cfkiatong.springbootbankingapp.services.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/me/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<GetBalanceResponse> getBalance(@AuthenticationPrincipal UserDetails userDetails) {
        GetBalanceResponse balanceResponse = transactionService.getBalance(UUID.fromString(userDetails.getUsername()));

        return ResponseEntity.ok(balanceResponse);
    }

    @PostMapping
    public ResponseEntity<GetBalanceResponse> makeTransaction(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody TransactionRequest transactionRequest) {
        if (transactionRequest.getType() == TransactionType.TRANSFER && transactionRequest.getTargetAccountId() == null) {
            throw new NoTargetAccountException();
        }

        GetBalanceResponse transactionResponse = transactionService.makeTransaction(UUID.fromString(userDetails.getUsername()), transactionRequest);

        return ResponseEntity.ok(transactionResponse);
    }

}