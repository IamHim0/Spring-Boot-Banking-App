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

import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

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
    public ViewAccountResponse addAccount(CreateAccountRequest createAccountRequest) {
        if (accountRepository.findByUsername(createAccountRequest.getUsername()).isPresent()) {
            throw new UsernameUnavailableException(createAccountRequest.getUsername());
        }

        String hashedPassword = new BCryptPasswordEncoder().encode(createAccountRequest.getPassword());

        Account account = new Account(
                createAccountRequest.getFirstName(),
                createAccountRequest.getLastName(),
                createAccountRequest.getUsername(),
                hashedPassword,
                createAccountRequest.getInitialDeposit());
        accountRepository.save(account);

        return mapToViewAccountResponse(account);
    }

    //ID BASED SERVICES
    public ViewAccountResponse getAccount(UUID id) {
        return mapToViewAccountResponse(findAccount(id));
    }

    @Transactional
    public ViewAccountResponse updateAccount(UUID id, UpdateAccountRequest updateAccountRequest) {
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

        return mapToViewAccountResponse(account);
    }

    //Write methods (save, delete, deleteById, etc.) are @Transactional by default
    public void deleteAccount(UUID id) {
        if (findAccount(id) != null) {
            accountRepository.deleteById(id);
        }
    }

    public ViewBalanceResponse viewBalance(UUID id) {
        return mapToViewBalanceResponse(findAccount(id));
    }

    @Transactional
    public ViewBalanceResponse makeTransaction(UUID id, TransactionRequest transactionRequest) {
        Account account = findAccount(id);

        Consumer<BigDecimal> withdraw = amount -> {
            if (account.getBalance().compareTo(transactionRequest.getAmount()) < 0) {
                throw new InsufficientBalanceException();
            }

            account.setBalance(account.getBalance().subtract(transactionRequest.getAmount()));
        };

        BiConsumer<BigDecimal, Account> depositTo = (amount, targetAccount) -> {
            targetAccount.setBalance(targetAccount.getBalance().add(transactionRequest.getAmount()));
        };

        switch (transactionRequest.getType()) {
            case WITHDRAWAL:
                withdraw.accept(transactionRequest.getAmount());

                break;
            case DEPOSIT:
                depositTo.accept(transactionRequest.getAmount(), account);

                break;
            case TRANSFER:
                withdraw.accept(transactionRequest.getAmount());

                Account targetAccount = findAccount(transactionRequest.getTargetAccountUsername());

                depositTo.accept(transactionRequest.getAmount(), targetAccount);

                break;
        }

        return mapToViewBalanceResponse(account);
    }

    //DTO MAPPING
    private ViewAccountResponse mapToViewAccountResponse(Account account) {
        ViewAccountResponse accDTO = new ViewAccountResponse();

        accDTO.setId(account.getId());
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


    //USERNAME BASED SERVICES
    public ViewAccountResponse getAccountByUsername(String username) {
        return mapToViewAccountResponse(findAccount(username));
    }

//
//    @Transactional
//    public void updateAccountByUsername(String username, UpdateAccountRequest updateAccountRequest) {
//
//        Account account = findAccount(username);
//
//        if (updateAccountRequest.getNewFirstName() != null) {
//            account.setFirstName(updateAccountRequest.getNewFirstName());
//        }
//        if (updateAccountRequest.getNewLastName() != null) {
//            account.setLastName(updateAccountRequest.getNewLastName());
//        }
//        if (updateAccountRequest.getNewUsername() != null) {
//            account.setUsername(updateAccountRequest.getNewUsername());
//        }
//        if (updateAccountRequest.getNewPassword() != null) {
//            account.setPassword(updateAccountRequest.getNewPassword());
//        }
//    }
//
//    @Transactional
//    public void deleteAccountByUsername(String username) {
//        if (findAccount(username) != null) {
//            accountRepository.deleteByUsername(username);
//        }
//    }
//
//    public ViewBalanceResponse viewBalanceByUsername(String username) {
//        return mapToViewBalanceResponse(findAccount(username));
//    }
//
//    @Transactional
//    public void makeTransactionByUsername(String username, TransactionRequest transactionRequest) {
//        Account account = findAccount(username);
//
//        switch (transactionRequest.getType()) {
//            case WITHDRAWAL:
//                if (account.getBalance().compareTo(transactionRequest.getAmount()) < 0) {
//                    throw new InsufficientBalanceException();
//                }
//
//                account.setBalance(account.getBalance().subtract(transactionRequest.getAmount()));
//
//                break;
//            case DEPOSIT:
//                account.setBalance(account.getBalance().add(transactionRequest.getAmount()));
//                break;
//        }
//    }
}