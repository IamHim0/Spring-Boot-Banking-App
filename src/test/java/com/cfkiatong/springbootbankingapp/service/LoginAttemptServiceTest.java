package com.cfkiatong.springbootbankingapp.service;

import com.cfkiatong.springbootbankingapp.dto.Role;
import com.cfkiatong.springbootbankingapp.dto.UserStatus;
import com.cfkiatong.springbootbankingapp.entity.UserEntity;
import com.cfkiatong.springbootbankingapp.exception.business.UserNotFoundException;
import com.cfkiatong.springbootbankingapp.repository.UserEntityRepository;
import com.cfkiatong.springbootbankingapp.services.LoginAttemptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoginAttemptServiceTest {

    @Mock
    private UserEntityRepository userRepository;

    private LoginAttemptService loginAttemptService;

    private UserEntity userEntity;
    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setup() {
        loginAttemptService = new LoginAttemptService(userRepository);

        userEntity = new UserEntity(
                "firstName",
                "lastName",
                "email",
                "username",
                "password",
                Set.of(Role.ADMIN, Role.USER),
                null
        );

        userEntity.setUserId(userId);
    }

    @Test
    void recordFailedAttempt_notMaxFailedLoginAttempts_incrementUserFailedLoginAttempts() {
        when(userRepository.findByUsername(userEntity.getUsername())).thenReturn(Optional.of(userEntity));

        loginAttemptService.recordFailedAttempt(userEntity.getUsername());

        assertEquals(1, userEntity.getFailedLoginAttempts());
    }

    @Test
    void recordFailedAttempt_maxFailedLoginAttempts_lockUser() {
        userEntity.setFailedLoginAttempts(2);
        when(userRepository.findByUsername(userEntity.getUsername())).thenReturn(Optional.of(userEntity));

        loginAttemptService.recordFailedAttempt(userEntity.getUsername());

        assertSame(UserStatus.LOCKED, userEntity.getUserStatus());
        assertNotNull(userEntity.getUnlocksAt());
        assertEquals(3, userEntity.getFailedLoginAttempts());
    }

    @Test
    void resetFailedAttempts_validLoginAttempt_resetUserFailedLoginAttempts() {
        userEntity.setFailedLoginAttempts(3);
        userEntity.lockUser();
        userEntity.setUnlocksAt(LocalDateTime.now());

        when(userRepository.findByUsername(userEntity.getUsername())).thenReturn(Optional.of(userEntity));

        loginAttemptService.resetFailedAttempts(userEntity.getUsername());

        assertEquals(0, userEntity.getFailedLoginAttempts());
        assertSame(UserStatus.ACTIVE, userEntity.getUserStatus());
        assertNull(userEntity.getUnlocksAt());
    }

    //EXCEPTION TESTING
    @Test
    void findUser_nonexistentUser_throwUserNotFoundException() {
        when(userRepository.findByUsername("nonexistentUser")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> loginAttemptService.recordFailedAttempt("nonexistentUser"));
    }

}