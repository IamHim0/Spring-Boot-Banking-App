package com.cfkiatong.springbootbankingapp.services;

import com.cfkiatong.springbootbankingapp.dto.Mapper;
import com.cfkiatong.springbootbankingapp.dto.request.UpdateUserRequest;
import com.cfkiatong.springbootbankingapp.dto.response.UserResponse;
import com.cfkiatong.springbootbankingapp.entity.Account;
import com.cfkiatong.springbootbankingapp.entity.Transaction;
import com.cfkiatong.springbootbankingapp.entity.UserEntity;
import com.cfkiatong.springbootbankingapp.exception.business.AccountNotFoundException;
import com.cfkiatong.springbootbankingapp.exception.business.UserNotFoundException;
import com.cfkiatong.springbootbankingapp.repository.AccountRepository;
import com.cfkiatong.springbootbankingapp.repository.TransactionRepository;
import com.cfkiatong.springbootbankingapp.repository.UserEntityRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AdminService {

    private final UserEntityRepository userEntityRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;
    private final Mapper mapper;

    public AdminService(
            UserEntityRepository userEntityRepository,
            AccountRepository accountRepository,
            TransactionRepository transactionRepository,
            PasswordEncoder passwordEncoder,
            Mapper mapper) {
        this.userEntityRepository = userEntityRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
    }

    private UserEntity findUserEntity(String username) {
        return userEntityRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
    }

    public List<UserEntity> getAllUsers() {
        return userEntityRepository.findAll();

    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public UserEntity getUser(String username) {
        return userEntityRepository.findByUsername(username).orElseThrow();
    }

    public Account getAccount(UUID accountId) {
        return accountRepository.findById(accountId).orElseThrow();
    }

    public Transaction getTransaction(UUID transactionId) {
        return transactionRepository.findById(transactionId).orElseThrow();
    }

    public UserResponse updateUser(String username, UpdateUserRequest updateUserRequest) {
        UserEntity userEntity = findUserEntity(username);


        if (updateUserRequest.getNewUsername() != null) {
            userEntity.setUsername(updateUserRequest.getNewUsername());
        }

        if (updateUserRequest.getNewPassword() != null) {
            userEntity.setPassword(passwordEncoder.encode(updateUserRequest.getNewPassword()));
        }

        if (updateUserRequest.getNewEmail() != null) {
            userEntity.setEmail(updateUserRequest.getNewEmail());
        }

        if (updateUserRequest.getNewFirstName() != null) {
            userEntity.setFirstName(updateUserRequest.getNewFirstName());
        }

        if (updateUserRequest.getNewLastName() != null) {
            userEntity.setLastName(updateUserRequest.getNewLastName());
        }

        return mapper.mapToUserResponse(userEntity);

    }

}