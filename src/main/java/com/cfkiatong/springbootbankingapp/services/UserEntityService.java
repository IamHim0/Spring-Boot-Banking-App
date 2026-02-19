package com.cfkiatong.springbootbankingapp.services;

import com.cfkiatong.springbootbankingapp.dto.CreateUserRequest;
import com.cfkiatong.springbootbankingapp.dto.Mapper;
import com.cfkiatong.springbootbankingapp.dto.UserResponse;
import com.cfkiatong.springbootbankingapp.entity.UserEntity;
import com.cfkiatong.springbootbankingapp.exception.business.AccountNotFoundException;
import com.cfkiatong.springbootbankingapp.repository.UserEntityRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.catalina.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserEntityService {

    private final UserEntityRepository userEntityRepository;
    private final Mapper mapper;

    public UserEntityService(UserEntityRepository userEntityRepository, Mapper mapper) {
        this.userEntityRepository = userEntityRepository;
        this.mapper = mapper;
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
        UserEntity userEntity = userEntityRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(userId.toString()));

        if (!userId.toString().equals(userEntity.getUserId().toString())) {
            throw new AccountNotFoundException(userId.toString());
        }

        return mapper.mapToUserResponse(userEntity);
    }
}