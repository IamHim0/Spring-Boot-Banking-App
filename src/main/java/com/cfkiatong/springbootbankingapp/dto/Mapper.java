package com.cfkiatong.springbootbankingapp.dto;

import com.cfkiatong.springbootbankingapp.entity.Account;
import com.cfkiatong.springbootbankingapp.entity.Transaction;
import com.cfkiatong.springbootbankingapp.entity.UserEntity;
import com.cfkiatong.springbootbankingapp.repository.TransactionRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class Mapper {

    public ViewUserResponse mapToViewUserResponse(UserEntity userEntity) {
        return new ViewUserResponse(
                userEntity.getUsername(),
                userEntity.getPassword(),
                userEntity.getEmail(),
                userEntity.getFirstName(),
                userEntity.getLastName(),
                userEntity.getAccounts()
                        .stream()
                        .map(Account::getId).toList()
        );
    }

    public ViewAccountResponse mapToViewAccountResponse(Account account) {
        return new ViewAccountResponse(
                account.getAccountType(),
                account.getOwner().getUsername(),
                account.getBalance(),
                account.getId()
        );
    }

    public ViewBalanceResponse mapToViewBalanceResponse(Account account) {
        return new ViewBalanceResponse(account.getBalance());
    }

    public TransactionHistoryResponse mapToTransactionHistoryResponse(Account  account, TransactionRepository transactionRepository) {
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

}
