package com.cfkiatong.springbootbankingapp.services;

import com.cfkiatong.springbootbankingapp.dto.request.TransactionRequest;
import com.cfkiatong.springbootbankingapp.dto.response.GetBalanceResponse;
import com.cfkiatong.springbootbankingapp.dto.response.TransactionDTO;
import com.cfkiatong.springbootbankingapp.entity.Account;
import com.cfkiatong.springbootbankingapp.dto.*;
import com.cfkiatong.springbootbankingapp.entity.Transaction;
import com.cfkiatong.springbootbankingapp.entity.UserEntity;
import com.cfkiatong.springbootbankingapp.exception.ForbiddenException;
import com.cfkiatong.springbootbankingapp.exception.business.AccountNotFoundException;
import com.cfkiatong.springbootbankingapp.exception.business.InsufficientBalanceException;
import com.cfkiatong.springbootbankingapp.repository.AccountRepository;
import com.cfkiatong.springbootbankingapp.repository.TransactionRepository;
import com.cfkiatong.springbootbankingapp.repository.UserEntityRepository;
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

    private final UserEntityRepository userEntityRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final Mapper mapper;

    public TransactionService(UserEntityRepository userEntityRepository,
                              AccountRepository accountRepository,
                              TransactionRepository transactionRepository,
                              Mapper mapper) {
        this.userEntityRepository = userEntityRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.mapper = mapper;
    }

    private Account findAccount(UUID id) {
        return accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException(id));
    }

    public GetBalanceResponse getBalance(UUID ownerID, UUID accountId) {
        Account account = findAccount(accountId);

        if (!account.getAccountOwner().getUserId().equals(ownerID)) {
            throw new ForbiddenException();
        }

        return mapper.mapToViewBalanceResponse(account);
    }

    @Transactional
    public GetBalanceResponse makeTransaction(UUID ownerId, UUID accountId, TransactionRequest transactionRequest) {
        TransactionType type = transactionRequest.getType();

        Account account = findAccount(accountId);
        UUID targetAccId = null;

        if (!account.getAccountOwner().getUserId().equals(ownerId)) {
            throw new ForbiddenException();
        }

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

    public List<TransactionDTO> getUserTransactions(UUID ownerId) {
        UserEntity user = userEntityRepository.findById(ownerId).orElseThrow();

        List<Account> accounts = user.getAccounts();

        List<UUID> accountIds = accounts.stream().map(Account::getId).toList();

        List<Transaction> transactions = new ArrayList<>();

        for (UUID accountId : accountIds) {
            transactions.addAll(getAccountTransactions(accountId));
        }

        return transactions.stream().distinct().map(TransactionDTO::new).toList();
    }

    private List<Transaction> getAccountTransactions(UUID accountId) {
        List<Transaction> transactions = transactionRepository.findBySourceAccount(accountId);
        transactions.addAll(transactionRepository.findByTargetAccount(accountId));

        return transactions;
    }

    public List<TransactionDTO> getAccountTransactions(UUID ownerId, UUID accountId) {
        Account account = findAccount(accountId);

        if (!account.getAccountOwner().getUserId().equals(ownerId)) {
            throw new ForbiddenException();
        }

        List<Transaction> transactions = transactionRepository.findBySourceAccount(accountId);
        transactions.addAll(transactionRepository.findByTargetAccount(accountId));

        return transactions.stream().map(TransactionDTO::new).toList();
    }

}