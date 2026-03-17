package com.cfkiatong.springbootbankingapp.service;

import com.cfkiatong.springbootbankingapp.dto.Role;
import com.cfkiatong.springbootbankingapp.entity.UserEntity;
import com.cfkiatong.springbootbankingapp.exception.business.UserNotFoundException;
import com.cfkiatong.springbootbankingapp.repository.UserEntityRepository;
import com.cfkiatong.springbootbankingapp.services.LoginAttemptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoginAttemptServiceTest {

    @Mock
    private UserEntityRepository userEntityRepository;

    private LoginAttemptService loginAttemptService;

    private UserEntity userEntity;
    private final UUID userId = UUID.randomUUID();


    @BeforeEach
    void setup() {
        loginAttemptService = new LoginAttemptService(userEntityRepository);

        userEntity = new UserEntity(
                "firstName",
                "lastName",
                "email",
                "username",
                "password",
                Set.of(Role.USER, Role.ADMIN),
                null
        );

        userEntity.setUserId(userId);
    }

    @Test
    void recordFailedAttempt_maxAttemptLessThan3_incrementUserFailedLoginAttempts() {
        when(userEntityRepository.findByUsername(userEntity.getUsername())).thenReturn(Optional.of(userEntity));

        loginAttemptService.recordFailedAttempt(userEntity.getUsername());

        assertEquals(1, userEntity.getFailedLoginAttempts());
    }

    @Test
    void recordFailedAttempt_maxAttemptEquals3_incrementUserFailedLoginAttempts() {
        userEntity.setFailedLoginAttempts(2);

        when(userEntityRepository.findByUsername(userEntity.getUsername())).thenReturn(Optional.of(userEntity));

        loginAttemptService.recordFailedAttempt(userEntity.getUsername());

        assertNotNull(userEntity.getUnlocksAt());
        assertEquals(3, userEntity.getFailedLoginAttempts());
    }

    @Test
    void resetFailedAttempts_validUser_setUserFailedLoginAttemptsTo0() {
        userEntity.setFailedLoginAttempts(3);

        when(userEntityRepository.findByUsername(userEntity.getUsername())).thenReturn(Optional.of(userEntity));

        loginAttemptService.resetFailedAttempts(userEntity.getUsername());

        assertEquals(0, userEntity.getFailedLoginAttempts());
    }

    //EXCEPTION TESTING
    @Test
    void findUser_invalidUser_throwsUserNotFoundException() {
        assertThrows(UserNotFoundException.class, () -> loginAttemptService.recordFailedAttempt("nonexistentUser"));
    }
}