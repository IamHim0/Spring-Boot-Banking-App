package com.cfkiatong.springbootbankingapp.services;

import com.cfkiatong.springbootbankingapp.dto.CreateUserRequest;
import com.cfkiatong.springbootbankingapp.entity.UserEntity;
import com.cfkiatong.springbootbankingapp.repository.UserEntityRepository;
import org.springframework.stereotype.Service;

@Service
public class UserEntityService {

    private final UserEntityRepository userEntityRepository;

    public UserEntityService(UserEntityRepository userEntityRepository) {
        this.userEntityRepository = userEntityRepository;
    }

    public UserEntity createUser(CreateUserRequest createUserRequest) {
        UserEntity user = new UserEntity(
                createUserRequest.getFirstName(),
                createUserRequest.getLastName(),
                createUserRequest.getEmail(),
                createUserRequest.getUsername(),
                createUserRequest.getPassword(),
                createUserRequest.getRoles(),
                null
        );

        userEntityRepository.save(user);

        return user;
    }

}
