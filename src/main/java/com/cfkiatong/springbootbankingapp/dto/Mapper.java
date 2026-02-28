package com.cfkiatong.springbootbankingapp.dto;

import com.cfkiatong.springbootbankingapp.dto.response.*;
import com.cfkiatong.springbootbankingapp.entity.Account;
import com.cfkiatong.springbootbankingapp.entity.Transaction;
import com.cfkiatong.springbootbankingapp.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Mapper {

    public UserResponse mapToUserResponse(UserEntity userEntity) {

        return new UserResponse(
                userEntity.getUsername(),
                userEntity.getEmail(),
                userEntity.getFirstName(),
                userEntity.getLastName(),
                userEntity.getAccounts() == null ?
                        List.of()
                        :
                        userEntity.getAccounts().
                                stream().map(Account::getId).toList()
        );
    }

    public AccountResponse mapToAccountResponse(Account account) {
        return new AccountResponse(account.getId(), account.getAccountOwner().getUsername(), account.getBalance());
    }

    public BalanceResponse mapToBalanceResponse(Account account) {
        return new BalanceResponse(account.getBalance());
    }

    public TransactionResponse mapToTransactionDTO(Transaction transaction) {
        return new TransactionResponse(
                transaction.getTimestamp(),
                transaction.getTransactionId(),
                transaction.getType(),
                transaction.getSourceAccount(),
                transaction.getTargetAccount(),
                transaction.getTransactionAmount(),
                transaction.getSourceBalanceBefore(),
                transaction.getSourceBalanceAfter()
        );
    }

    public UserRolesResponse mapToUserRolesResponse(UserEntity userEntity) {
        return new UserRolesResponse(
                userEntity.getUsername(),
                userEntity.getRoles()
        );
    }

}
