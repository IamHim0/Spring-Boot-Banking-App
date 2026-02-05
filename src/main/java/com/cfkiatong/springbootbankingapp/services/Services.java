package com.cfkiatong.springbootbankingapp.services;

import com.cfkiatong.springbootbankingapp.account.Account;
import com.cfkiatong.springbootbankingapp.dto.*;
import com.cfkiatong.springbootbankingapp.exception.business.AccountNotFoundException;
import com.cfkiatong.springbootbankingapp.exception.business.InsufficientBalanceException;
import com.cfkiatong.springbootbankingapp.exception.business.UsernameUnavailableException;
import com.cfkiatong.springbootbankingapp.repository.AccountRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
        return accountRepository.findByUsername(username).orElseThrow(() -> new AccountNotFoundException(username));
    }

    private Account findAccount(UUID id) {
        return accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException(id));
    }

    //Write methods (save, delete, deleteById, etc.) are @Transactional by default
    public void addAccount(CreateAccountRequest createAccountRequest) {
        if (accountRepository.findByUsername(createAccountRequest.getUsername()).isPresent()) {
            throw new UsernameUnavailableException(createAccountRequest.getUsername());
        }

        String hashedPassword = new BCryptPasswordEncoder().encode(createAccountRequest.getPassword());

        Account account = new Account(
                createAccountRequest.getFirstName(),
                createAccountRequest.getLastName(),
                createAccountRequest.getUsername(),
                hashedPassword,
                createAccountRequest.getInitialBalance());
        accountRepository.save(account);
    }

    //ID BASED SERVICES
    public ViewAccountResponse getAccount(UUID id) {
        return mapToViewAccountResponse(findAccount(id));
    }

    @Transactional
    public void updateAccount(UUID id, UpdateAccountRequest updateAccountRequest) {
        Account account = findAccount(id);

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
            String hashedNewPassword = new BCryptPasswordEncoder().encode(updateAccountRequest.getNewPassword());
            
            account.setPassword(hashedNewPassword);
        }
    }

    //Write methods (save, delete, deleteById, etc.) are @Transactional by default
    public void deleteAccount(UUID id) {
        if (findAccount(id) != null) {
            accountRepository.deleteById(id);
        }
    }

    @Transactional
    public void makeTransaction(UUID id, TransactionRequest transactionRequest) {
        Account account = findAccount(id);

        switch (transactionRequest.getType()) {
            case WITHDRAWAL:
                if (account.getBalance().compareTo(transactionRequest.getAmount()) < 0) {
                    throw new InsufficientBalanceException();
                }

                account.setBalance(account.getBalance().subtract(transactionRequest.getAmount()));

                break;
            case DEPOSIT:
                account.setBalance(account.getBalance().add(transactionRequest.getAmount()));
                break;
        }
    }

    //USERNAME BASED SERVICES
    public ViewAccountResponse getAccountByUsername(String username) {
        return mapToViewAccountResponse(findAccount(username));
    }

    @Transactional
    public void updateAccountByUsername(String username, UpdateAccountRequest updateAccountRequest) {

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

    @Transactional
    public void deleteAccountByUsername(String username) {
        if (findAccount(username) != null) {
            accountRepository.deleteByUsername(username);
        }
    }

    public ViewBalanceResponse viewBalanceByUsername(String username) {
        return mapToViewBalanceResponse(findAccount(username));
    }

    public ViewBalanceResponse viewBalance(UUID id) {
        return mapToViewBalanceResponse(findAccount(id));
    }

    @Transactional
    public void makeTransactionByUsername(String username, TransactionRequest transactionRequest) {
        Account account = findAccount(username);

        switch (transactionRequest.getType()) {
            case WITHDRAWAL:
                if (account.getBalance().compareTo(transactionRequest.getAmount()) < 0) {
                    throw new InsufficientBalanceException();
                }

                account.setBalance(account.getBalance().subtract(transactionRequest.getAmount()));

                break;
            case DEPOSIT:
                account.setBalance(account.getBalance().add(transactionRequest.getAmount()));
                break;
        }
    }

    //DTO MAPPING
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