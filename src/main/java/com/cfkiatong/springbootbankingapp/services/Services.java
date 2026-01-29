package com.cfkiatong.springbootbankingapp.services;

import com.cfkiatong.springbootbankingapp.account.Account;
import com.cfkiatong.springbootbankingapp.dto.*;
import com.cfkiatong.springbootbankingapp.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.node.StringNode;

import java.math.BigDecimal;
import java.util.List;
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

    public ViewAccountResponse getAccountById(UUID id) {
        return mapToViewAccountResponse(accountRepository.findById(id).orElseThrow(() -> new IllegalStateException(id + "not found")));
    }

    public ViewAccountResponse getAccountByUsername(String username) {
        return mapToViewAccountResponse(accountRepository.findByUsername(username).orElseThrow(() -> new RuntimeException(("Account not found"))));
    }

    public ViewBalanceResponse viewBalance(String username) {
        return mapToViewBalanceResponse(accountRepository.findByUsername(username).orElseThrow(() -> new RuntimeException(("Account not found"))));
    }

    @Transactional
    public void updateAccount(String username, UpdateAccountRequest updateAccountRequest) {
        Account account = accountRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Account not found"));

        if (updateAccountRequest.getNewFirstName() != null) {
            account.setFirstName(updateAccountRequest.getNewFirstName());
        }
        if (updateAccountRequest.getNewLastName() != null) {
            account.setLastName(updateAccountRequest.getNewLastName());
        }
        if (updateAccountRequest.getNewUsername() != null) {
            account.setUsername(updateAccountRequest.getNewUsername());
        }
        if (updateAccountRequest.getNewPassword() != null) {
            account.setPassword(updateAccountRequest.getNewPassword());
        }
    }

    @Transactional
    public void deleteAccountByUsername(String username) {
        accountRepository.deleteByUsername(username);
    }

    @Transactional
    public void makeDeposit(String username, DepositRequest depositRequest) {
        Account account = accountRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Account not found"));

        account.setBalance(account.getBalance().add(depositRequest.getDeposit()));
    }

    @Transactional
    public void makeWithdrawal(String username, WithdrawRequest withdrawRequest) {
        Account account = accountRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Account not found"));

        account.setBalance(account.getBalance().subtract(withdrawRequest.getWithdrawal()));
    }

    private ViewAccountResponse mapToViewAccountResponse(Account account) {
        ViewAccountResponse accDTO = new ViewAccountResponse();

        accDTO.setFirstName(account.getFirstName());
        accDTO.setLastName(account.getLastName());
        accDTO.setUsername(account.getUsername());
        accDTO.setBalance(account.getBalance());

        return accDTO;
    }

    private ViewBalanceResponse mapToViewBalanceResponse(Account account) {
        ViewBalanceResponse balanceDTO = new ViewBalanceResponse();

        balanceDTO.setBalance(account.getBalance());

        return balanceDTO;
    }

}