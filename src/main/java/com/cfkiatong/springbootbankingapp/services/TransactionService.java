package com.cfkiatong.springbootbankingapp.services;

import com.cfkiatong.springbootbankingapp.entity.Account;
import com.cfkiatong.springbootbankingapp.dto.*;
import com.cfkiatong.springbootbankingapp.entity.Transaction;
import com.cfkiatong.springbootbankingapp.exception.business.AccountNotFoundException;
import com.cfkiatong.springbootbankingapp.exception.business.InsufficientBalanceException;
import com.cfkiatong.springbootbankingapp.exception.business.UsernameUnavailableException;
import com.cfkiatong.springbootbankingapp.repository.AccountRepository;
import com.cfkiatong.springbootbankingapp.repository.TransactionRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@Service
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
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

        Account account = new Account(
                createAccountRequest.getFirstName(),
                createAccountRequest.getLastName(),
                createAccountRequest.getUsername(),
                new BCryptPasswordEncoder().encode(createAccountRequest.getPassword()),
                createAccountRequest.getInitialDeposit());
        accountRepository.save(account);

        return mapToViewAccountResponse(account);
    }

    public ViewAccountResponse getAccount(UUID id) {
        return mapToViewAccountResponse(findAccount(id));
    }

    public TransactionHistoryResponse getTransactions(UUID id){
        return mapToTransactionHistoryReponse(findAccount(id));
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
        accountRepository.deleteById(id);
    }

    public ViewBalanceResponse viewBalance(UUID id) {
        return mapToViewBalanceResponse(findAccount(id));
    }

    @Transactional
    public ViewBalanceResponse makeTransaction(UUID id, TransactionRequest transactionRequest) {
        TransactionType type = transactionRequest.getType();

        Account account = findAccount(id);
        UUID targetAccId = null;

        BigDecimal sourceBalanceBefore = account.getBalance();

        BigDecimal targetBalanceBefore = null;
        BigDecimal targetBalanceAfter = null;

        Consumer<BigDecimal> withdraw = amount -> {
            if (account.getBalance().compareTo(transactionRequest.getAmount()) < 0) {
                throw new InsufficientBalanceException();
            }

            account.setBalance(account.getBalance().subtract(transactionRequest.getAmount()));
        };

        Consumer<Account> depositTo = (targetAccount) -> {
            targetAccount.setBalance(targetAccount.getBalance().add(transactionRequest.getAmount()));
        };

        switch (type) {
            case WITHDRAWAL:
                withdraw.accept(transactionRequest.getAmount());

                break;
            case DEPOSIT:
                depositTo.accept(account);

                break;
            case TRANSFER:
                withdraw.accept(transactionRequest.getAmount());

                Account targetAccount = findAccount(transactionRequest.getTargetAccountUsername());
                targetAccId = targetAccount.getId();
                targetBalanceBefore = targetAccount.getBalance();

                depositTo.accept(targetAccount);
                targetBalanceAfter = targetAccount.getBalance();

                break;
        }

        Transaction transaction = new Transaction(
                LocalDateTime.now(),
                type,
                account.getId(),
                targetAccId,
                transactionRequest.getAmount(),
                sourceBalanceBefore,
                account.getBalance(),
                targetBalanceBefore,
                targetBalanceAfter);

        transactionRepository.save(transaction);

        return mapToViewBalanceResponse(account);
    }

    //ADMIN METHODS
    public  AdminTransactionHistoryResponse getTransactionHistory(){
        List<Transaction> transactions = transactionRepository.findAll();

        List<AdminTransactionDTO> dtos = transactions.stream()
                .map(transaction ->
                        new AdminTransactionDTO(
                                transaction.getTimestamp(),
                                transaction.getTransactionId(),
                                transaction.getType(),
                                transaction.getSourceAccount(),
                                transaction.getTargetAccount(),
                                transaction.getTransactionAmount(),
                                transaction.getSourceBalanceBefore(),
                                transaction.getSourceBalanceAfter(),
                                transaction.getTargetBalanceBefore(),
                                transaction.getTargetBalanceAfter()

                        ))
                .toList();

        return new AdminTransactionHistoryResponse(dtos);
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

    private TransactionHistoryResponse mapToTransactionHistoryReponse(Account  account) {
        List<Transaction> transactions = transactionRepository.findBySourceAccount(account.getId());

        List<TransactionDTO> transactionDTOs = transactions.stream()
                .map(transaction -> new TransactionDTO(
                        transaction.getTimestamp(),
                        transaction.getTransactionId(),
                        transaction.getType(),
                        transaction.getSourceAccount(),
                        transaction.getTransactionAmount(),
                        transaction.getSourceBalanceBefore(),
                        transaction.getSourceBalanceAfter()
                        ))
                .toList();

        return new TransactionHistoryResponse(transactionDTOs);
    }

    private ViewBalanceResponse mapToViewBalanceResponse(Account account) {
        ViewBalanceResponse balanceDTO = new ViewBalanceResponse();

        balanceDTO.setBalance(account.getBalance());

        return balanceDTO;
    }

}