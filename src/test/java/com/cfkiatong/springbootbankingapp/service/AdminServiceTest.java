package com.cfkiatong.springbootbankingapp.service;

import com.cfkiatong.springbootbankingapp.dto.Mapper;
import com.cfkiatong.springbootbankingapp.dto.Role;
import com.cfkiatong.springbootbankingapp.dto.TransactionType;
import com.cfkiatong.springbootbankingapp.dto.UserStatus;
import com.cfkiatong.springbootbankingapp.dto.request.UpdateRolesRequest;
import com.cfkiatong.springbootbankingapp.dto.request.UpdateUserStatusRequest;
import com.cfkiatong.springbootbankingapp.dto.response.AccountResponse;
import com.cfkiatong.springbootbankingapp.dto.response.TransactionResponse;
import com.cfkiatong.springbootbankingapp.dto.response.UserResponse;
import com.cfkiatong.springbootbankingapp.dto.response.UserRolesResponse;
import com.cfkiatong.springbootbankingapp.entity.Account;
import com.cfkiatong.springbootbankingapp.entity.Transaction;
import com.cfkiatong.springbootbankingapp.entity.UserEntity;
import com.cfkiatong.springbootbankingapp.exception.business.UpdateUserRoleException;
import com.cfkiatong.springbootbankingapp.exception.business.UpdateUserStatusException;
import com.cfkiatong.springbootbankingapp.exception.business.UserNotFoundException;
import com.cfkiatong.springbootbankingapp.repository.AccountRepository;
import com.cfkiatong.springbootbankingapp.repository.TransactionRepository;
import com.cfkiatong.springbootbankingapp.repository.UserEntityRepository;
import com.cfkiatong.springbootbankingapp.services.AdminService;
import com.cfkiatong.springbootbankingapp.services.UserEntityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

    @Mock
    private UserEntityRepository userEntityRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private UserEntityService userEntityService;

    private AdminService adminService;
    private Mapper mapper;

    private UserEntity user1;
    private UserEntity user2;
    private Account account1;
    private Account account2;
    private Transaction transaction1;
    private Transaction transaction2;

    private final UUID user1Id = UUID.randomUUID();
    private final UUID user2Id = UUID.randomUUID();
    private final UUID account1Id = UUID.randomUUID();
    private final UUID account2Id = UUID.randomUUID();
    private final UUID transaction1Id = UUID.randomUUID();
    private final UUID transaction2Id = UUID.randomUUID();
    private final LocalDateTime transactionDate = LocalDateTime.now();

    @BeforeEach
    void setup() {
        Mapper mapper = new Mapper();
        adminService = new AdminService(
                userEntityRepository,
                accountRepository,
                transactionRepository,
                userEntityService,
                mapper);

        user1 = new UserEntity(
                "firstName1",
                "lastName1",
                "email1",
                "username1",
                "password1",
                new HashSet<>(Set.of(Role.USER, Role.ADMIN)),
                Collections.emptyList());

        user1.setUserId(user1Id);

        user2 = new UserEntity(
                "firstName2",
                "lastName2",
                "email2",
                "username2",
                "password2",
                new HashSet<>(Set.of(Role.USER)),
                Collections.emptyList());

        user2.setUserId(user2Id);

        account1 = new Account(user1);
        account1.setId(account1Id);

        account2 = new Account(user2);
        account2.setId(account2Id);

        transaction1 = new Transaction(
                transactionDate,
                TransactionType.DEPOSIT,
                account1.getId(),
                null,
                new BigDecimal("10000"),
                new BigDecimal("0"),
                new BigDecimal("10000"),
                null,
                null);

        transaction1.setTransactionId(transaction1Id);

        transaction2 = new Transaction(
                transactionDate,
                TransactionType.TRANSFER,
                account2.getId(),
                account1.getId(),
                new BigDecimal("10000"),
                new BigDecimal("10000"),
                new BigDecimal("0"),
                new BigDecimal("0"),
                new BigDecimal("10000"));

        transaction2.setTransactionId(transaction2Id);
    }

    @Test
    void getAllUsers_returnListOfUserResponse() {
        UserResponse userResponse1 = new UserResponse(
                "username1",
                "email1",
                "firstName1",
                "lastName1",
                Collections.emptyList());

        UserResponse userResponse2 = new UserResponse(
                "username2",
                "email2",
                "firstName2",
                "lastName2",
                Collections.emptyList());

        when(userEntityRepository.findAll()).thenReturn(List.of(user1, user2));

        List<UserResponse> result = adminService.getAllUsers();

        assertEquals(List.of(userResponse1, userResponse2), result);
    }

    @Test
    void getAllAccounts_returnListOfAccountResponse1() {
        AccountResponse accountResponse1 = new AccountResponse(
                account1.getId(),
                account1.getAccountOwner().getUsername(),
                new BigDecimal("0"));

        AccountResponse accountResponse2 = new AccountResponse(
                account2.getId(),
                account2.getAccountOwner().getUsername(),
                new BigDecimal("0"));

        when(accountRepository.findAll()).thenReturn(List.of(account1, account2));

        List<AccountResponse> result = adminService.getAllAccounts();

        assertEquals(List.of(accountResponse1, accountResponse2), result);
    }

    @Test
    void getAllTransactions_returnListOfTransactionResponse() {
        TransactionResponse transactionResponse1 = new TransactionResponse(
                transactionDate,
                transaction1Id,
                TransactionType.DEPOSIT,
                account1.getId(),
                null,
                new BigDecimal("10000"),
                new BigDecimal("0"),
                new BigDecimal("10000")
        );

        TransactionResponse transactionResponse2 = new TransactionResponse(
                transactionDate,
                transaction2Id,
                TransactionType.TRANSFER,
                account2.getId(),
                account1.getId(),
                new BigDecimal("10000"),
                new BigDecimal("10000"),
                new BigDecimal("0"));

        when(transactionRepository.findAll()).thenReturn(List.of(transaction1, transaction2));

        List<TransactionResponse> result = adminService.getAllTransactions();

        assertEquals(List.of(transactionResponse1, transactionResponse2), result);
    }

    @Test
    void getUser_validUsernameId_returnUserResponse() {
        UserResponse userResponse1 = new UserResponse(
                "username1",
                "email1",
                "firstName1",
                "lastName1",
                Collections.emptyList());

        when(userEntityRepository.findByUsername("username1")).thenReturn(Optional.of(user1));

        UserResponse result = adminService.getUser("username1");

        assertEquals(userResponse1, result);
    }

    @Test
    void getAccount_validAccountId_returnAccountResponse() {
        AccountResponse accountResponse1 = new AccountResponse(account1Id, user1.getUsername(), new BigDecimal("0"));

        when(accountRepository.findById(account1Id)).thenReturn(Optional.of(account1));

        AccountResponse result = adminService.getAccount(account1Id);

        assertEquals(accountResponse1, result);
    }

    @Test
    void getTransaction_validTransactionId_returnTransactionResponse() {
        TransactionResponse transactionResponse1 = new TransactionResponse(
                transactionDate,
                transaction1Id,
                TransactionType.DEPOSIT,
                account1.getId(),
                null,
                new BigDecimal("10000"),
                new BigDecimal("0"),
                new BigDecimal("10000")
        );

        when(transactionRepository.findById(transaction1Id)).thenReturn(Optional.of(transaction1));

        TransactionResponse result = adminService.getTransaction(transaction1Id);

        assertEquals(transactionResponse1, result);
    }

    @Test
    void addUserRoles_validRequest_addRolesToUser() {
        UpdateRolesRequest updateRolesRequest = new UpdateRolesRequest("ADD", Set.of(Role.ADMIN));
        UserRolesResponse userRolesResponse = new UserRolesResponse(user2.getUsername(), Set.of(Role.USER, Role.ADMIN));

        when(userEntityRepository.findByUsername(user2.getUsername())).thenReturn(Optional.of(user2));

        UserRolesResponse result = adminService.updateUserRoles(user2.getUsername(), updateRolesRequest);

        assertTrue(user2.getRoles().contains(Role.ADMIN));
        assertEquals(userRolesResponse, result);
    }

    @Test
    void deleteUserRoles_validRequest_addRolesToUser() {
        UpdateRolesRequest updateRolesRequest = new UpdateRolesRequest("REMOVE", Set.of(Role.ADMIN));
        UserRolesResponse userRolesResponse = new UserRolesResponse(user1.getUsername(), Set.of(Role.USER));

        when(userEntityRepository.findByUsername(user1.getUsername())).thenReturn(Optional.of(user1));

        UserRolesResponse result = adminService.updateUserRoles(user1.getUsername(), updateRolesRequest);

        assertFalse(user1.getRoles().contains(Role.ADMIN));
        assertEquals(userRolesResponse, result);
    }

    //EXCEPTION TESTING
    @Test
    void getUser_nonexistentUser_throwUserNotFoundException() {
        when(userEntityRepository.findByUsername("nonexistentUser")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> adminService.getUser("nonexistentUser"));
    }

    @Test
    void updateUserStatus_deleteUserAttempt_throwUpdateUserStatusException() {
        UpdateUserStatusRequest updateUserStatusRequest = new UpdateUserStatusRequest(UserStatus.DELETED);

        assertThrows(UpdateUserStatusException.class, () -> adminService.updateUserStatus(user1.getUsername(), updateUserStatusRequest));
    }

    @Test
    void updateUserStatus_lockUserAttempt_throwUpdateUserStatusException() {
        UpdateUserStatusRequest updateUserStatusRequest = new UpdateUserStatusRequest(UserStatus.LOCKED);

        assertThrows(UpdateUserStatusException.class, () -> adminService.updateUserStatus(user1.getUsername(), updateUserStatusRequest));
    }

    @Test
    void addUserRoles_userHasRole_throwUpdateUserRolesException() {
        when(userEntityRepository.findByUsername(user1.getUsername())).thenReturn(Optional.of(user1));

        UpdateRolesRequest updateUserRolesRequest = new UpdateRolesRequest("ADD", Set.of(Role.ADMIN));

        assertThrows(UpdateUserRoleException.class, () -> adminService.updateUserRoles(user1.getUsername(), updateUserRolesRequest));
    }

    @Test
    void deleteUserRoles_userDoesntHaveRole_throwUpdateUserRolesException() {
        when(userEntityRepository.findByUsername(user2.getUsername())).thenReturn(Optional.of(user2));

        UpdateRolesRequest updateUserRolesRequest = new UpdateRolesRequest("REMOVE", Set.of(Role.ADMIN));

        assertThrows(UpdateUserRoleException.class, () -> adminService.updateUserRoles(user2.getUsername(), updateUserRolesRequest));
    }

    @Test
    void updateUserRoles_invalidRequest_throwUpdateUserRolesException() {
        when(userEntityRepository.findByUsername(user2.getUsername())).thenReturn(Optional.of(user2));

        UpdateRolesRequest updateUserRolesRequest = new UpdateRolesRequest("invalidAction", Set.of(Role.ADMIN));

        assertThrows(UpdateUserRoleException.class, () -> adminService.updateUserRoles(user2.getUsername(), updateUserRolesRequest));
    }

}