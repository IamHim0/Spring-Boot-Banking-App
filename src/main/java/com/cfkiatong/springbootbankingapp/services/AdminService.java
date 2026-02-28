package com.cfkiatong.springbootbankingapp.services;

import com.cfkiatong.springbootbankingapp.dto.Mapper;
import com.cfkiatong.springbootbankingapp.dto.Role;
import com.cfkiatong.springbootbankingapp.dto.request.UpdateRolesRequest;
import com.cfkiatong.springbootbankingapp.dto.request.UpdateUserRequest;
import com.cfkiatong.springbootbankingapp.dto.response.AccountResponse;
import com.cfkiatong.springbootbankingapp.dto.response.TransactionResponse;
import com.cfkiatong.springbootbankingapp.dto.response.UserResponse;
import com.cfkiatong.springbootbankingapp.dto.response.UserRolesResponse;
import com.cfkiatong.springbootbankingapp.entity.UserEntity;
import com.cfkiatong.springbootbankingapp.exception.business.UpdateUserRoleException;
import com.cfkiatong.springbootbankingapp.exception.business.UserNotFoundException;
import com.cfkiatong.springbootbankingapp.repository.AccountRepository;
import com.cfkiatong.springbootbankingapp.repository.TransactionRepository;
import com.cfkiatong.springbootbankingapp.repository.UserEntityRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class AdminService {

    private final UserEntityRepository userEntityRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final UserEntityService userEntityService;
    private final Mapper mapper;

    public AdminService(
            UserEntityRepository userEntityRepository,
            AccountRepository accountRepository,
            TransactionRepository transactionRepository,
            UserEntityService userEntityService,
            Mapper mapper) {
        this.userEntityRepository = userEntityRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.userEntityService = userEntityService;
        this.mapper = mapper;
    }

    private UserEntity findUserEntity(String username) {
        return userEntityRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
    }

    public List<UserResponse> getAllUsers() {
        return userEntityRepository.findAll().stream().map(mapper::mapToUserResponse).toList();

    }

    public List<AccountResponse> getAllAccounts() {
        return accountRepository.findAll().stream().map(mapper::mapToAccountResponse).toList();
    }

    public List<TransactionResponse> getAllTransactions() {
        return transactionRepository.findAll().stream().map(mapper::mapToTransactionDTO).toList();
    }

    public UserResponse getUser(String username) {
        return mapper.mapToUserResponse(userEntityRepository.findByUsername(username).orElseThrow());
    }

    public AccountResponse getAccount(UUID accountId) {
        return mapper.mapToAccountResponse(accountRepository.findById(accountId).orElseThrow());
    }

    public TransactionResponse getTransaction(UUID transactionId) {
        return mapper.mapToTransactionDTO(transactionRepository.findById(transactionId).orElseThrow());
    }

    public UserResponse updateUser(String username, UpdateUserRequest updateUserRequest) {
        return userEntityService.updateUser(username, updateUserRequest);
    }

    public void deleteUser(String username) {
        UserEntity userEntity = findUserEntity(username);
        userEntityRepository.delete(userEntity);
    }

    @Transactional
    public UserRolesResponse updateUserRoles(String username, UpdateRolesRequest updateRolesRequest) {
        UserEntity userEntity = findUserEntity(username);

        switch (updateRolesRequest.action().toLowerCase()) {
            case "add" -> {
                if (userEntity.getRoles().containsAll(updateRolesRequest.roles())) {
                    throw new UpdateUserRoleException("User already has the specified role(s). No changes made.");
                }

                userEntity.getRoles().addAll(updateRolesRequest.roles());
            }
            case "delete" -> {
                if (!userEntity.getRoles().containsAll(updateRolesRequest.roles())) {
                    throw new UpdateUserRoleException("User does not have the specified role(s). No changes made.");
                }

                userEntity.getRoles().removeAll(updateRolesRequest.roles());
            }
            default -> throw new UpdateUserRoleException("Invalid action. Must be 'add' or 'delete'.");
        }

        return mapper.mapToUserRolesResponse(userEntity);
    }

}