package com.cfkiatong.springbootbankingapp.service;

import com.cfkiatong.springbootbankingapp.dto.Mapper;
import com.cfkiatong.springbootbankingapp.dto.TransactionType;
import com.cfkiatong.springbootbankingapp.dto.request.TransactionRequest;
import com.cfkiatong.springbootbankingapp.dto.response.BalanceResponse;
import com.cfkiatong.springbootbankingapp.entity.Account;
import com.cfkiatong.springbootbankingapp.entity.Transaction;
import com.cfkiatong.springbootbankingapp.entity.UserEntity;
import com.cfkiatong.springbootbankingapp.exception.ForbiddenException;
import com.cfkiatong.springbootbankingapp.exception.business.AccountNotFoundException;
import com.cfkiatong.springbootbankingapp.exception.business.InsufficientBalanceException;
import com.cfkiatong.springbootbankingapp.repository.AccountRepository;
import com.cfkiatong.springbootbankingapp.repository.TransactionRepository;
import com.cfkiatong.springbootbankingapp.services.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private Mapper mapper;

    @InjectMocks
    private TransactionService transactionService;

    private UUID ownerId;
    private UUID accountId;
    private UserEntity userEntity;
    private Account account;

    @BeforeEach
    void setup() {
        //ARRANGE
        ownerId = UUID.randomUUID();
        userEntity = new UserEntity();
        userEntity.setUserId(ownerId);

        account = new Account();
        accountId = UUID.randomUUID();
        account.setId(accountId);
        account.setAccountOwner(userEntity);
        account.setBalance(new BigDecimal("10000"));
    }

    @Test
    void shouldReturnBalanceResponse() {
        BalanceResponse expectedResponse = new BalanceResponse(new BigDecimal("10000"));
        when(mapper.mapToBalanceResponse(account)).thenReturn(expectedResponse);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        //ACT
        BalanceResponse result = transactionService.getBalance(ownerId, account.getId());

        //ASSERT
        assertEquals(new BigDecimal("10000"), result.balance());
    }

    @Test
    void shouldIncreaseBalanceAndSaveTransaction() {
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setType(TransactionType.DEPOSIT);
        transactionRequest.setAmount(new BigDecimal("5000"));

        BalanceResponse expectedResponse = new BalanceResponse(new BigDecimal("15000"));
        when(mapper.mapToBalanceResponse(account)).thenReturn(expectedResponse);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        //ACT
        BalanceResponse result = transactionService.makeTransaction(ownerId, accountId, transactionRequest);

        //ASSERT
        assertEquals(new BigDecimal("15000"), result.balance());

        //VERIFY
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void shouldDecreaseBalanceAndSaveTransaction() {
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setType(TransactionType.WITHDRAWAL);
        transactionRequest.setAmount(new BigDecimal("5000"));

        BalanceResponse expectedResponse = new BalanceResponse(new BigDecimal("5000"));
        when(mapper.mapToBalanceResponse(account)).thenReturn(expectedResponse);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        //ACT
        BalanceResponse result = transactionService.makeTransaction(ownerId, accountId, transactionRequest);

        //ASSERT
        assertEquals(new BigDecimal("5000"), result.balance());

        //VERIFY
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void shouldDecreaseAccountBalanceIncreaseTargetAccountBalanceAndSaveTransaction() {
        UUID targetAccountId = UUID.randomUUID();
        Account targetAccount = new Account();
        targetAccount.setId(targetAccountId);
        targetAccount.setBalance(new BigDecimal("0"));

        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setType(TransactionType.TRANSFER);
        transactionRequest.setAmount(new BigDecimal("5000"));
        transactionRequest.setTargetAccountId(targetAccountId);

        BalanceResponse expectedResponse = new BalanceResponse(new BigDecimal("5000"));
        when(mapper.mapToBalanceResponse(account)).thenReturn(expectedResponse);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.findById(targetAccountId)).thenReturn(Optional.of(targetAccount));

        //ACT
        BalanceResponse result = transactionService.makeTransaction(ownerId, accountId, transactionRequest);

        //ASSERT
        assertEquals(new BigDecimal("5000"), result.balance());

        //VERIFY
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void shouldThrowInsufficientBalanceException_whenBalanceInsufficient() {
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setType(TransactionType.WITHDRAWAL);
        transactionRequest.setAmount(new BigDecimal("15000"));

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        assertThrows(InsufficientBalanceException.class, () ->
                transactionService.makeTransaction(ownerId, accountId, transactionRequest)
        );

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void shouldThrowForbiddenException_whenUserDoesNotOwnAccount() {
        UUID wrongOwnerId = UUID.randomUUID();

        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setType(TransactionType.WITHDRAWAL);
        transactionRequest.setAmount(new BigDecimal("5000"));

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        assertThrows(ForbiddenException.class, () -> transactionService.makeTransaction(wrongOwnerId, accountId, transactionRequest));

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void shouldThrowAccountNotFoundException_whenAccountDoesNotExist() {
        UUID nonExistentAccountId = UUID.randomUUID();

        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setType(TransactionType.WITHDRAWAL);
        transactionRequest.setAmount(new BigDecimal("5000"));

        when(accountRepository.findById(nonExistentAccountId)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> transactionService.makeTransaction(ownerId, nonExistentAccountId, transactionRequest));

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void shouldThrowAccountNotFoundException_whenTargetAccountDoesNotExist() {
        UUID nonExistentTargetAccountId = UUID.randomUUID();

        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setType(TransactionType.TRANSFER);
        transactionRequest.setAmount(new BigDecimal("5000"));
        transactionRequest.setTargetAccountId(nonExistentTargetAccountId);


        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.findById(nonExistentTargetAccountId)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> transactionService.makeTransaction(ownerId, accountId, transactionRequest));

        //VERIFY
        verify(transactionRepository, never()).save(any());
    }

}