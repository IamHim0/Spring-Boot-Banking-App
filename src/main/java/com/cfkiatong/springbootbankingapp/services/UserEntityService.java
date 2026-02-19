package com.cfkiatong.springbootbankingapp.services;

import com.cfkiatong.springbootbankingapp.dto.CreateUserRequest;
import com.cfkiatong.springbootbankingapp.dto.Mapper;
import com.cfkiatong.springbootbankingapp.dto.UserResponse;
import com.cfkiatong.springbootbankingapp.entity.UserEntity;
import com.cfkiatong.springbootbankingapp.repository.UserEntityRepository;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;

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
                createUserRequest.getPassword(),
                createUserRequest.getRoles(),
                null
        );

        userEntityRepository.save(user);

//        return user;
        return mapper.mapToUserResponse(user);
    }

}
