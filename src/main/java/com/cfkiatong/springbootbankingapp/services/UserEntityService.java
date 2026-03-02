package com.cfkiatong.springbootbankingapp.services;

import com.cfkiatong.springbootbankingapp.dto.UserStatus;
import com.cfkiatong.springbootbankingapp.dto.request.CreateUserRequest;
import com.cfkiatong.springbootbankingapp.dto.Mapper;
import com.cfkiatong.springbootbankingapp.dto.request.UpdateUserRequest;
import com.cfkiatong.springbootbankingapp.dto.response.UserResponse;
import com.cfkiatong.springbootbankingapp.entity.UserEntity;
import com.cfkiatong.springbootbankingapp.exception.business.UpdateUserStatusException;
import com.cfkiatong.springbootbankingapp.exception.business.UserNotFoundException;
import com.cfkiatong.springbootbankingapp.repository.UserEntityRepository;
import org.apache.catalina.User;
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
        return userEntityRepository.findById(userId).orElseThrow();
    }

    private UserEntity findUserEntity(String username) {
        return userEntityRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
    }

    public UserResponse createUser(CreateUserRequest createUserRequest) {

        UserEntity user = new UserEntity(
                createUserRequest.getFirstName(),
                createUserRequest.getLastName(),
                createUserRequest.getEmail(),
                createUserRequest.getUsername(),
                passwordEncoder.encode(createUserRequest.getPassword()),
                createUserRequest.getRoles(),
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

    @Transactional
    public UserResponse activateUser(String username) {
        UserEntity userEntity = findUserEntity(username);

        if (userEntity.getUserStatus() == UserStatus.ACTIVE) {
            throw new UpdateUserStatusException("User is already active, no changes made.");
        }

        userEntity.activateUser();

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

}