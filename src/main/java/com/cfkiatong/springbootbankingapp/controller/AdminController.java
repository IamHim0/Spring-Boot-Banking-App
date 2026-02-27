package com.cfkiatong.springbootbankingapp.controller;

import com.cfkiatong.springbootbankingapp.dto.request.UpdateUserRequest;
import com.cfkiatong.springbootbankingapp.entity.Account;
import com.cfkiatong.springbootbankingapp.entity.Transaction;
import com.cfkiatong.springbootbankingapp.entity.UserEntity;
import com.cfkiatong.springbootbankingapp.services.AdminService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;

@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    public List<UserEntity> getAllUsers() {
        return adminService.getAllUsers();

    }

    public List<Account> getAllAccounts() {
        return adminService.getAllAccounts();
    }

    public List<Transaction> getAllTransactions() {
        return adminService.getAllTransactions();
    }

    public UserEntity getUser(String username) {
        return adminService.getUser(username);
    }

    public Account getAccount(UUID accountId) {
        return adminService.getAccount(accountId);
    }

    public Transaction getTransaction(UUID transactionId) {
        return adminService.getTransaction(transactionId);
    }

    public UserEntity updateUser(String username, UpdateUserRequest updateUserRequest) {
        return adminService.getUser(username);
    }

}