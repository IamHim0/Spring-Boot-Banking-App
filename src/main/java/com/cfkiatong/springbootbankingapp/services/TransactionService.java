package com.cfkiatong.springbootbankingapp.services;

import com.cfkiatong.springbootbankingapp.entity.Account;
import com.cfkiatong.springbootbankingapp.dto.*;
import com.cfkiatong.springbootbankingapp.entity.Transaction;
import com.cfkiatong.springbootbankingapp.exception.business.AccountNotFoundException;
import com.cfkiatong.springbootbankingapp.exception.business.InsufficientBalanceException;
import com.cfkiatong.springbootbankingapp.exception.business.UsernameUnavailableException;
import com.cfkiatong.springbootbankingapp.repository.AccountRepository;
import com.cfkiatong.springbootbankingapp.repository.TransactionRepository;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final Mapper mapper;

    public TransactionService(AccountRepository accountRepository, TransactionRepository transactionRepository, Mapper mapper) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.mapper = mapper;
    }

    private Account findAccount(UUID id) {
        return accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException(id));
    }

    public TransactionHistoryResponse getTransactions(UserDetails userDetails, UUID accountId){
        Account account = findAccount(accountId);

        if(!account.getOwner().getId().toString().equals(userDetails.getUsername())){
            throw new AccountNotFoundException(userDetails.getUsername());
        }

        return mapper.mapToTransactionHistoryResponse(account, transactionRepository);
    }

    public ViewBalanceResponse viewBalance(UserDetails userDetails, UUID id) {
        Account account = findAccount(id);

        if(!account.getOwner().getId().toString().equals(userDetails.getUsername())){
            throw new AccountNotFoundException(userDetails.getUsername());
        }

        return mapper.mapToViewBalanceResponse(account);
    }

    @Transactional
    public ViewBalanceResponse makeTransaction(UserDetails userDetails, UUID id, TransactionRequest transactionRequest) {
        TransactionType type = transactionRequest.getType();

        Account account = findAccount(id);

        if(!account.getOwner().getId().toString().equals(userDetails.getUsername())){
            throw new AccountNotFoundException(userDetails.getUsername());
        }

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

                Account targetAccount = findAccount(transactionRequest.getTargetAccountId());
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

        return mapper.mapToViewBalanceResponse(account);
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

}