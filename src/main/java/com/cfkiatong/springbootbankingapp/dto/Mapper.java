package com.cfkiatong.springbootbankingapp.dto;

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

}
