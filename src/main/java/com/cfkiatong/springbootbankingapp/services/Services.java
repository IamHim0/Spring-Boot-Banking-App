package com.cfkiatong.springbootbankingapp.services;

import com.cfkiatong.springbootbankingapp.account.Account;
import com.cfkiatong.springbootbankingapp.dto.*;
import com.cfkiatong.springbootbankingapp.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class Services {

    private final AccountRepository accountRepository;

    public Services(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    private Account findAccount(String username) {
        return accountRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Account with username: " + username + " not found"));
    }

    private Account findAccount(UUID id) {
        return accountRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Account with id: " + id + " not found"));
    }

    //Write methods (save, delete, deleteById, etc.) are @Transactional by default
    public void addAccount(CreateAccountRequest createAccountRequest) {
        if (accountRepository.findByUsername(createAccountRequest.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username is already taken");
        }

        Account account = new Account(createAccountRequest.getFirstName(), createAccountRequest.getLastName(), createAccountRequest.getUsername(), createAccountRequest.getPassword(), createAccountRequest.getInitialBalance());
        accountRepository.save(account);
    }

    public ViewAccountResponse getAccountById(UUID id) {
        return mapToViewAccountResponse(findAccount(id));
    }

    public ViewAccountResponse getAccountByUsername(String username) {
        return mapToViewAccountResponse(findAccount(username));
    }

    public ViewBalanceResponse viewBalance(String username) {
        return mapToViewBalanceResponse(findAccount(username));
    }

    @Transactional
    public void updateAccount(String username, UpdateAccountRequest updateAccountRequest) {
        if (updateAccountRequest.getNewFirstName() == null && updateAccountRequest.getNewLastName() == null && updateAccountRequest.getNewUsername() == null && updateAccountRequest.getNewPassword() == null) {
            throw new IllegalArgumentException("Must update at least one field of the account");
        }

        Account account = findAccount(username);

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

    //Write methods (save, delete, deleteById, etc.) are @Transactional by default
    public void deleteAccountById(UUID id) {
        if (findAccount(id) != null) {
            accountRepository.deleteById(id);
        }
    }

    @Transactional
    public void deleteAccountByUsername(String username) {
        if (findAccount(username) != null) {
            accountRepository.deleteByUsername(username);
        }
    }

    @Transactional
    public void makeDeposit(String username, DepositRequest depositRequest) {
        Account account = findAccount(username);

        account.setBalance(account.getBalance().add(depositRequest.getDeposit()));
    }

    @Transactional
    public void makeWithdrawal(String username, WithdrawRequest withdrawRequest) {
        Account account = findAccount(username);

        if (account.getBalance().compareTo(withdrawRequest.getWithdrawal()) < 0) {
            throw new IllegalArgumentException("Balance is insufficient for this transaction");
        }

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