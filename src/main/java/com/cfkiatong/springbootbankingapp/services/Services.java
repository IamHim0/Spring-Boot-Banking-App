package com.cfkiatong.springbootbankingapp.services;

import com.cfkiatong.springbootbankingapp.account.Account;
import com.cfkiatong.springbootbankingapp.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class Services {

    private final AccountRepository accountRepository;

    public Services(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public void addAccount(Account account) {
        accountRepository.save(account);
    }

    public void deleteAccountById(UUID id) {
        accountRepository.deleteById(id);
    }

    @Transactional
    public void deleteAccountByUsername(String username) {
        accountRepository.deleteByUsername(username);
    }

    public Account getAccountById(UUID id) {
        return accountRepository.findById(id).orElseThrow(() -> new IllegalStateException(id + "not found"));
    }

    public Account getAccountByUsername(String username) {
        return accountRepository.findByUsername(username).orElseThrow(() -> new RuntimeException(("Account not found")));
    }

    @Transactional
    public void updateAccount(String username, String newFirstName, String newLastName, String newUsername, String newPassword) {
        Account account = accountRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Account not found"));

        if (newFirstName != null) {
            account.setFirstName(newFirstName);
        }
        if (newLastName != null) {
            account.setLastName(newLastName);
        }
        if (newUsername != null) {
            account.setUsername(newUsername);
        }
        if (newPassword != null) {
            account.setPassword(newPassword);
        }
    }
}