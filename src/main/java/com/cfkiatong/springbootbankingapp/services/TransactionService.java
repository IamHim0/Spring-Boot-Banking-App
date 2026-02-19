package com.cfkiatong.springbootbankingapp.services;

import com.cfkiatong.springbootbankingapp.entity.Account;
import com.cfkiatong.springbootbankingapp.dto.*;
import com.cfkiatong.springbootbankingapp.entity.Transaction;
import com.cfkiatong.springbootbankingapp.exception.business.AccountNotFoundException;
import com.cfkiatong.springbootbankingapp.exception.business.InsufficientBalanceException;
import com.cfkiatong.springbootbankingapp.repository.AccountRepository;
import com.cfkiatong.springbootbankingapp.repository.TransactionRepository;
import com.cfkiatong.springbootbankingapp.repository.UserEntityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Consumer;

@Service
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final UserEntityRepository userEntityRepository;

    public TransactionService(AccountRepository accountRepository, TransactionRepository transactionRepository, UserEntityRepository userEntityRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.userEntityRepository = userEntityRepository;
    }

    private Account findAccount(String username) {
        return accountRepository.findByUsername(username).orElseThrow(() -> new AccountNotFoundException(username));
    }

    private Account findAccount(UUID id) {
        return accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException(id));
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

    private ViewBalanceResponse mapToViewBalanceResponse(Account account) {
        ViewBalanceResponse balanceDTO = new ViewBalanceResponse();

        balanceDTO.setBalance(account.getBalance());

        return balanceDTO;
    }

}