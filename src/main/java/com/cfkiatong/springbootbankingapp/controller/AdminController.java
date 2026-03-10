package com.cfkiatong.springbootbankingapp.controller;

import com.cfkiatong.springbootbankingapp.dto.request.UpdateRolesRequest;
import com.cfkiatong.springbootbankingapp.dto.request.UpdateUserRequest;
import com.cfkiatong.springbootbankingapp.dto.request.UpdateUserStatusRequest;
import com.cfkiatong.springbootbankingapp.dto.response.AccountResponse;
import com.cfkiatong.springbootbankingapp.dto.response.TransactionResponse;
import com.cfkiatong.springbootbankingapp.dto.response.UserResponse;
import com.cfkiatong.springbootbankingapp.dto.response.UserRolesResponse;
import com.cfkiatong.springbootbankingapp.services.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("api/v1/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        return ResponseEntity.ok(adminService.getAllAccounts());
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionResponse>> getAllTransactions() {
        return ResponseEntity.ok(adminService.getAllTransactions());
    }

    @GetMapping("/users/{username}")
    public ResponseEntity<UserResponse> getUser(@PathVariable String username) {
        return ResponseEntity.ok(adminService.getUser(username));
    }

    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable UUID accountId) {
        return ResponseEntity.ok(adminService.getAccount(accountId));
    }

    @GetMapping("/transactions/{transactionId}")
    public ResponseEntity<TransactionResponse> getTransaction(@PathVariable UUID transactionId) {
        return ResponseEntity.ok(adminService.getTransaction(transactionId));
    }

    @PatchMapping("/users/{username}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable String username, @RequestBody UpdateUserRequest updateUserRequest) {
        return ResponseEntity.ok(adminService.updateUser(username, updateUserRequest));
    }

    @PatchMapping("/users/{username}/status")
    public ResponseEntity<UserResponse> updateUserStatus(@PathVariable String username, @RequestBody UpdateUserStatusRequest updateUserStatusRequest) {
        return ResponseEntity.ok(adminService.updateUserStatus(username, updateUserStatusRequest));
    }

    @DeleteMapping("/users/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable String username) {
        adminService.deleteUser(username);

        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/users/{username}/roles")
    public ResponseEntity<UserRolesResponse> updateUserRoles(@PathVariable String username, @RequestBody UpdateRolesRequest updateRolesRequest) {
        return ResponseEntity.ok(adminService.updateUserRoles(username, updateRolesRequest));
    }

}