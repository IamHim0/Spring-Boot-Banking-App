package com.cfkiatong.springbootbankingapp.dto;

import com.cfkiatong.springbootbankingapp.dto.response.GetAccountResponse;
import com.cfkiatong.springbootbankingapp.dto.response.GetBalanceResponse;
import com.cfkiatong.springbootbankingapp.dto.response.UserResponse;
import com.cfkiatong.springbootbankingapp.entity.Account;
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
                userEntity.getRoles(),
                userEntity.getAccounts() == null ?
                        List.of()
                        :
                        userEntity.getAccounts().
                                stream().map(Account::getId).toList()
        );
    }

    public GetAccountResponse mapToViewAccountResponse(Account account) {
        GetAccountResponse accDTO = new GetAccountResponse();

        accDTO.setId(account.getId());
        accDTO.setAccountOwner(account.getAccountOwner().getUsername());
        accDTO.setBalance(account.getBalance());

        return accDTO;
    }

    public GetBalanceResponse mapToViewBalanceResponse(Account account) {
        return new GetBalanceResponse(account.getBalance());
    }

}
