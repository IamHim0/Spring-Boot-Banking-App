package com.cfkiatong.springbootbankingapp.services;

import com.cfkiatong.springbootbankingapp.dto.UserStatus;
import com.cfkiatong.springbootbankingapp.dto.request.CreateUserRequest;
import com.cfkiatong.springbootbankingapp.dto.Mapper;
import com.cfkiatong.springbootbankingapp.dto.request.UpdateUserRequest;
import com.cfkiatong.springbootbankingapp.dto.response.UserResponse;
import com.cfkiatong.springbootbankingapp.entity.UserEntity;
import com.cfkiatong.springbootbankingapp.exception.business.UpdateUserStatusException;
import com.cfkiatong.springbootbankingapp.exception.business.UserNotFoundException;
import com.cfkiatong.springbootbankingapp.exception.business.UsernameUnavailableException;
import com.cfkiatong.springbootbankingapp.repository.UserEntityRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserEntityService {

    private final UserEntityRepository userEntityRepository;
    private final PasswordEncoder passwordEncoder;
    private final Mapper mapper;

    public UserEntityService(
            UserEntityRepository userEntityRepository,
            PasswordEncoder passwordEncoder,
            Mapper mapper) {
        this.userEntityRepository = userEntityRepository;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
    }

    private UserEntity findUserEntity(UUID userId) {
        return userEntityRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }

    private UserEntity findUserEntity(String username) {
        return userEntityRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
    }

    public UserResponse createUser(CreateUserRequest createUserRequest) {

        if (userEntityRepository.existsByUsername(createUserRequest.username())) {
            throw new UsernameUnavailableException(createUserRequest.username());
        }

        UserEntity user = new UserEntity(
                createUserRequest.firstName(),
                createUserRequest.lastName(),
                createUserRequest.email(),
                createUserRequest.username(),
                passwordEncoder.encode(createUserRequest.password()),
                createUserRequest.roles(),
                null
        );

        userEntityRepository.save(user);

        return mapper.mapToUserResponse(user);
    }

    public UserResponse getUser(UUID userId) {
        UserEntity userEntity = findUserEntity(userId);

        return mapper.mapToUserResponse(userEntity);
    }

    @Transactional
    public UserResponse updateUser(String username, UpdateUserRequest updateUserRequest) {
        UserEntity userEntity = findUserEntity(username);

        return applyUserUpdates(userEntity, updateUserRequest);
    }

    @Transactional
    public UserResponse updateUser(UUID userId, UpdateUserRequest updateUserRequest) {
        UserEntity userEntity = findUserEntity(userId);

        return applyUserUpdates(userEntity, updateUserRequest);
    }

    private UserResponse applyUserUpdates(UserEntity userEntity, UpdateUserRequest updateUserRequest) {
        if (updateUserRequest.newUsername() != null) {
            userEntity.setUsername(updateUserRequest.newUsername());
        }

        if (updateUserRequest.newPassword() != null) {
            userEntity.setPassword(passwordEncoder.encode(updateUserRequest.newPassword()));
        }

        if (updateUserRequest.newEmail() != null) {
            userEntity.setEmail(updateUserRequest.newEmail());
        }

        if (updateUserRequest.newFirstname() != null) {
            userEntity.setFirstName(updateUserRequest.newFirstname());
        }

        if (updateUserRequest.newLastname() != null) {
            userEntity.setLastName(updateUserRequest.newLastname());
        }

        return mapper.mapToUserResponse(userEntity);

    }

    @Transactional
    public UserResponse disableUser(String username) {
        UserEntity userEntity = findUserEntity(username);

        if (userEntity.getUserStatus() == UserStatus.DISABLED) {
            throw new UpdateUserStatusException("User is already disabled, no changes made.");
        }

        userEntity.disableUser();

        return mapper.mapToUserResponse(userEntity);
    }

    @Transactional
    public void lockUser(String username) {
        UserEntity userEntity = findUserEntity(username);

        if (userEntity.getUserStatus() == UserStatus.LOCKED) {
            throw new UpdateUserStatusException("User is already locked, no changes made.");
        }

        userEntity.lockUser();
    }

    @Transactional
    public void deleteUser(UUID userId) {
        applyDeleteUser(findUserEntity(userId));
    }

    @Transactional
    public void deleteUser(String username) {
        applyDeleteUser(findUserEntity(username));
    }

    private void applyDeleteUser(UserEntity userEntity) {
        if (userEntity.getUserStatus() == UserStatus.DELETED) {
            throw new UpdateUserStatusException("User is already deleted, no changes made.");
        }

        userEntity.deleteUser();
    }

    @Transactional
    public UserResponse activateUser(String username) {
        UserEntity userEntity = findUserEntity(username);

        if (userEntity.getUserStatus() == UserStatus.ACTIVE) {
            throw new UpdateUserStatusException("User is already active, no changes made.");
        }

        userEntity.activateUser();

        return mapper.mapToUserResponse(userEntity);
    }

}