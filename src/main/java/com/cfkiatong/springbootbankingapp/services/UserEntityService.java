package com.cfkiatong.springbootbankingapp.services;

import com.cfkiatong.springbootbankingapp.dto.request.CreateUserRequest;
import com.cfkiatong.springbootbankingapp.dto.Mapper;
import com.cfkiatong.springbootbankingapp.dto.request.UpdateUserRequest;
import com.cfkiatong.springbootbankingapp.dto.response.UserResponse;
import com.cfkiatong.springbootbankingapp.entity.UserEntity;
import com.cfkiatong.springbootbankingapp.exception.business.AccountNotFoundException;
import com.cfkiatong.springbootbankingapp.repository.UserEntityRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserEntityService {

    private final UserEntityRepository userEntityRepository;
    private final Mapper mapper;

    public UserEntityService(UserEntityRepository userEntityRepository, Mapper mapper) {
        this.userEntityRepository = userEntityRepository;
        this.mapper = mapper;
    }

    private UserEntity findUserEntity(UUID userId) {
        return userEntityRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(userId.toString()));
    }

    public UserResponse createUser(CreateUserRequest createUserRequest) {

        UserEntity user = new UserEntity(
                createUserRequest.getFirstName(),
                createUserRequest.getLastName(),
                createUserRequest.getEmail(),
                createUserRequest.getUsername(),
                new BCryptPasswordEncoder().encode(createUserRequest.getPassword()),
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
    public UserResponse updateUser(UUID userId, UpdateUserRequest updateUserRequest) {
        UserEntity userEntity = findUserEntity(userId);

        if (!userId.toString().equals(userEntity.getUserId().toString())) {
            throw new AccountNotFoundException(userId.toString());
        }

        if (updateUserRequest.getNewUsername() != null) {
            userEntity.setUsername(updateUserRequest.getNewUsername());
        }

        if (updateUserRequest.getNewPassword() != null) {
            userEntity.setPassword(new BCryptPasswordEncoder().encode(updateUserRequest.getNewPassword()));
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

    public void deleteUser(UUID userId) {
        UserEntity userEntity = findUserEntity(userId);

        if (!userId.toString().equals(userEntity.getUserId().toString())) {
            throw new AccountNotFoundException(userId.toString());
        }

        userEntityRepository.delete(userEntity);
    }

}