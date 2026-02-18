package com.cfkiatong.springbootbankingapp.services;

import com.cfkiatong.springbootbankingapp.dto.CreateUserRequest;
import com.cfkiatong.springbootbankingapp.dto.Mapper;
import com.cfkiatong.springbootbankingapp.dto.ViewUserResponse;
import com.cfkiatong.springbootbankingapp.entity.UserEntity;
import com.cfkiatong.springbootbankingapp.repository.UserEntityRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@Service
public class UserEntityService {

    private final UserEntityRepository userEntityRepository;
    private final Mapper mapper;

    public UserEntityService(UserEntityRepository userEntityRepository, Mapper mapper) {
        this.userEntityRepository = userEntityRepository;
        this.mapper = mapper;
    }

    public ViewUserResponse createUser(CreateUserRequest createUserRequest) {

        String hashedPassword = new BCryptPasswordEncoder().encode(createUserRequest.getPassword());

        UserEntity userEntity = new UserEntity(
                createUserRequest.getFirstName(),
                createUserRequest.getLastName(),
                createUserRequest.getEmail(),
                createUserRequest.getUsername(),
                hashedPassword,
                createUserRequest.getRoles(),
                null);

        userEntityRepository.save(userEntity);

        return mapper.mapToViewUserResponse(userEntity);
    }

    public ViewUserResponse getUser(UserDetails userDetails) {
        return mapper.mapToViewUserResponse(
                userEntityRepository.findById(UUID.fromString(userDetails.getUsername()))
                        .orElseThrow());
    }

}
