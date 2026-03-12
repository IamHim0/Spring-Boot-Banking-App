package com.cfkiatong.springbootbankingapp.service;

import com.cfkiatong.springbootbankingapp.dto.Mapper;
import com.cfkiatong.springbootbankingapp.dto.Role;
import com.cfkiatong.springbootbankingapp.dto.request.CreateUserRequest;
import com.cfkiatong.springbootbankingapp.dto.response.UserResponse;
import com.cfkiatong.springbootbankingapp.entity.UserEntity;
import com.cfkiatong.springbootbankingapp.repository.UserEntityRepository;
import com.cfkiatong.springbootbankingapp.services.UserEntityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserEntityServiceTest {

    @Mock
    private UserEntityRepository userEntityRepository;

    private PasswordEncoder passwordEncoder;
    private Mapper mapper;
    private UserEntityService userEntityService;

    private UserEntity userEntity;
    private UUID userId;
    private String username;

    @BeforeEach
    void setup() {
        passwordEncoder = new BCryptPasswordEncoder();
        mapper = new Mapper();

        userEntityService = new UserEntityService(
                userEntityRepository,
                passwordEncoder,
                mapper
        );

        userId = UUID.randomUUID();
        username = "userEntity";
        userEntity = new UserEntity();
        userEntity.setUserId(userId);
        userEntity.setUsername(username);
    }

    @Test
    void createUser_validRequest_returnUserResponse() {
        CreateUserRequest createUserRequest = new CreateUserRequest(
                "firstName",
                "lastName",
                "email",
                "userName",
                "password",
                Set.of(Role.USER, Role.ADMIN));

        UserResponse expectedResponse = new UserResponse(
                "userName",
                "email",
                "firstName",
                "lastName",
                Collections.emptyList());

        UserResponse result = userEntityService.createUser(createUserRequest);


        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);

        verify(userEntityRepository).save(captor.capture());

        UserEntity savedUser = captor.getValue();

        assertEquals(createUserRequest.getFirstName(), savedUser.getFirstName());
        assertEquals(createUserRequest.getLastName(), savedUser.getLastName());
        assertEquals(createUserRequest.getUsername(), savedUser.getUsername());
        assertEquals(createUserRequest.getEmail(), savedUser.getEmail());
        assertNull(savedUser.getAccounts());
        assertEquals(expectedResponse, result);
    }

}
