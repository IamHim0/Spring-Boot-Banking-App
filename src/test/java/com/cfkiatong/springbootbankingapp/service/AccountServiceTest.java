package com.cfkiatong.springbootbankingapp.service;

import com.cfkiatong.springbootbankingapp.dto.Mapper;
import com.cfkiatong.springbootbankingapp.dto.request.ChangeAccountOwnerRequest;
import com.cfkiatong.springbootbankingapp.dto.response.AccountResponse;
import com.cfkiatong.springbootbankingapp.entity.Account;
import com.cfkiatong.springbootbankingapp.entity.UserEntity;
import com.cfkiatong.springbootbankingapp.repository.AccountRepository;
import com.cfkiatong.springbootbankingapp.repository.UserEntityRepository;
import com.cfkiatong.springbootbankingapp.services.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private UserEntityRepository userEntityRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private Mapper mapper;

    @InjectMocks
    private AccountService accountService;

    private UUID ownerId;
    private String ownerUsername;
    private UUID accountId;
    private UserEntity userEntity;
    private Account account;

    @BeforeEach
    void setup() {
        //ARRANGE
        ownerId = UUID.randomUUID();
        ownerUsername = "owner";
        userEntity = new UserEntity();
        userEntity.setUserId(ownerId);
        userEntity.setUsername(ownerUsername);

        account = new Account();
        accountId = UUID.randomUUID();
        account.setId(accountId);
        account.setAccountOwner(userEntity);
        account.setBalance(new BigDecimal("10000"));
    }

    @Test
    void shouldReturnAccountResponse() {
        AccountResponse expectedResponse = new AccountResponse(accountId, ownerUsername, new BigDecimal("10000"));
        when(mapper.mapToAccountResponse(account)).thenReturn(expectedResponse);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        AccountResponse result = accountService.getAccount(ownerId, accountId);

        assertEquals(expectedResponse, result);
    }

    @Test
    void shouldReturnAccountResponse_whenAccountCreated() {
        
    }

    @Test
    void shouldChangeAccountOwner() {
        UserEntity newOwner = new UserEntity();
        newOwner.setUsername("newOwner");

        ChangeAccountOwnerRequest request = new ChangeAccountOwnerRequest();
        request.setNewAccountOwner("newOwner");

        AccountResponse expectedResponse = new AccountResponse(accountId, "newOwner", new BigDecimal("10000"));
        when(mapper.mapToAccountResponse(account)).thenReturn(expectedResponse);
        when(userEntityRepository.findByUsername("newOwner")).thenReturn(Optional.of(newOwner));
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        AccountResponse result = accountService.changeAccountOwner(ownerId, accountId, request);

        assertEquals(expectedResponse, result);
    }

    @Test
    void shouldDeleteAccountFromRepository() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        accountService.deleteAccount(ownerId, accountId);

        verify(accountRepository).deleteById(accountId);
    }

}
