package com.cfkiatong.springbootbankingapp.controller;

import com.cfkiatong.springbootbankingapp.dto.*;
import com.cfkiatong.springbootbankingapp.entity.UserEntity;
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
@RequestMapping("api/v1/me/transactions")
public class TransactionController {

    private final TransactionService services;

    public TransactionController(TransactionService services) {
        this.services = services;
    }

    @GetMapping
    public ResponseEntity<ViewBalanceResponse> viewBalance(@AuthenticationPrincipal UserDetails userDetails) {
        ViewBalanceResponse balanceResponse = services.viewBalance(UUID.fromString(userDetails.getUsername()));

        return ResponseEntity.ok(balanceResponse);
    }

    @PostMapping
    public ResponseEntity<ViewBalanceResponse> makeTransaction(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody TransactionRequest transactionRequest) {
        if (transactionRequest.getType() == TransactionType.TRANSFER && transactionRequest.getTargetAccountUsername() == null) {
            throw new NoTargetAccountException();
        }

        ViewBalanceResponse transactionResponse = services.makeTransaction(UUID.fromString(userDetails.getUsername()), transactionRequest);

        return ResponseEntity.ok(transactionResponse);
    }

}