package com.cfkiatong.springbootbankingapp.service;

import com.cfkiatong.springbootbankingapp.dto.Mapper;
import com.cfkiatong.springbootbankingapp.dto.Role;
import com.cfkiatong.springbootbankingapp.dto.UserStatus;
import com.cfkiatong.springbootbankingapp.dto.request.CreateUserRequest;
import com.cfkiatong.springbootbankingapp.dto.request.UpdateUserRequest;
import com.cfkiatong.springbootbankingapp.dto.response.UserResponse;
import com.cfkiatong.springbootbankingapp.entity.UserEntity;
import com.cfkiatong.springbootbankingapp.exception.business.UpdateUserStatusException;
import com.cfkiatong.springbootbankingapp.exception.business.UserNotFoundException;
import com.cfkiatong.springbootbankingapp.exception.business.UsernameUnavailableException;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserEntityServiceTest {

    @Mock
    private UserEntityRepository userEntityRepository;

    private PasswordEncoder passwordEncoder;
    private UserEntityService userEntityService;

    private UserEntity userEntity;
    private UserResponse defaultExpectedResponse;

    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setup() {
        passwordEncoder = new BCryptPasswordEncoder();
        Mapper mapper = new Mapper();

        userEntityService = new UserEntityService(
                userEntityRepository,
                passwordEncoder,
                mapper
        );

        userEntity = new UserEntity(
                "firstName",
                "lastName",
                "email",
                "username",
                "password",
                new HashSet<>(Set.of(Role.ADMIN, Role.USER)),
                null
        );
        userEntity.setUserId(userId);

        defaultExpectedResponse = new UserResponse(
                "username",
                "email",
                "firstName",
                "lastName",
                Collections.emptyList()
        );
    }

    //UserEntityService Tests
    @Test
    void createUser_validRequest_returnUserResponse() {
        CreateUserRequest createUserRequest = returnCreateUserRequest();

        UserResponse result = userEntityService.createUser(createUserRequest);

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);

        verify(userEntityRepository).save(captor.capture());

        UserEntity savedUser = captor.getValue();

        assertEquals(createUserRequest.firstName(), savedUser.getFirstName());
        assertEquals(createUserRequest.lastName(), savedUser.getLastName());
        assertEquals(createUserRequest.username(), savedUser.getUsername());
        assertEquals(createUserRequest.email(), savedUser.getEmail());
        assertNull(savedUser.getAccounts());
        assertEquals(defaultExpectedResponse, result);
    }

    @Test
    void getUser_validRequest_returnUserResponse() {
        when(userEntityRepository.findById(userEntity.getUserId())).thenReturn(Optional.of(userEntity));

        UserResponse result = userEntityService.getUser(userEntity.getUserId());

        assertEquals(defaultExpectedResponse, result);
    }

    @Test
    void applyUpdateUser_validUser_updateUser() {
        UserResponse expectedResponse = new UserResponse(
                "newUsername",
                "newEmail",
                "newFirstname",
                "newLastname",
                Collections.emptyList()
        );

        when(userEntityRepository.findById(userEntity.getUserId())).thenReturn(Optional.of(userEntity));

        UserResponse result = userEntityService.updateUser(userEntity.getUserId(), returnUpdateUserRequest());

        assertEquals("newUsername", userEntity.getUsername());
        assertTrue(passwordEncoder.matches("newPassword", userEntity.getPassword()));
        assertEquals("newEmail", userEntity.getEmail());
        assertEquals("newFirstname", userEntity.getFirstName());
        assertEquals("newLastname", userEntity.getLastName());
        assertEquals(expectedResponse, result);
    }

    @Test
    void disableUser_nonDisabledUserStatus_disableUser() {
        when(userEntityRepository.findByUsername(userEntity.getUsername())).thenReturn(Optional.of(userEntity));

        UserResponse result = userEntityService.disableUser(userEntity.getUsername());

        assertEquals(UserStatus.DISABLED, userEntity.getUserStatus());
        assertEquals(defaultExpectedResponse, result);
    }

    @Test
    void lockUser_nonLockedUserStatus_lockUser() {
        when(userEntityRepository.findByUsername(userEntity.getUsername())).thenReturn(Optional.of(userEntity));

        userEntityService.lockUser(userEntity.getUsername());

        assertEquals(UserStatus.LOCKED, userEntity.getUserStatus());
    }

    @Test
    void applyDeleteUser_nonDeletedStatus_UserStatusEqualsDeleted() {
        when(userEntityRepository.findById(userEntity.getUserId())).thenReturn(Optional.of(userEntity));

        userEntityService.deleteUser(userEntity.getUserId());

        assertEquals(UserStatus.DELETED, userEntity.getUserStatus());
    }

    @Test
    void activateUser_nonActiveUserStatus_activateUser() {

        when(userEntityRepository.findByUsername(userEntity.getUsername())).thenReturn(Optional.of(userEntity));

        userEntityService.disableUser(userEntity.getUsername());
        UserResponse result = userEntityService.activateUser(userEntity.getUsername());

        assertEquals(UserStatus.ACTIVE, userEntity.getUserStatus());
        assertEquals(defaultExpectedResponse, result);
    }

    //EXCEPTION TESTS
    @Test
    void findUserEntity_nonexistentUserId_throwUserNotFoundException() {
        assertThrows(UserNotFoundException.class, () -> userEntityService.getUser(UUID.randomUUID()));
    }

    @Test
    void findUserEntity_nonexistentUsername_throwUserNotfoundException() {
        assertThrows(UserNotFoundException.class, () -> userEntityService.updateUser("nonexistentUsername", returnUpdateUserRequest()));
    }

    @Test
    void createUser_usernameUnavailable_throwUsernameUnavailableException() {
        CreateUserRequest createUserRequest = returnCreateUserRequest();

        when(userEntityRepository.existsByUsername(createUserRequest.username())).thenReturn(true);

        assertThrows(UsernameUnavailableException.class, () -> userEntityService.createUser(createUserRequest));
    }

    @Test
    void disableUser_userAlreadyDisabled_throwUserStatusException() {
        when(userEntityRepository.findByUsername(userEntity.getUsername())).thenReturn(Optional.of(userEntity));

        userEntityService.disableUser(userEntity.getUsername());

        assertThrows(UpdateUserStatusException.class, () -> userEntityService.disableUser(userEntity.getUsername()));
    }

    @Test
    void lockUser_userAlreadyLocked_throwUserStatusException() {
        when(userEntityRepository.findByUsername(userEntity.getUsername())).thenReturn(Optional.of(userEntity));

        userEntityService.lockUser(userEntity.getUsername());

        assertThrows(UpdateUserStatusException.class, () -> userEntityService.lockUser(userEntity.getUsername()));
    }

    @Test
    void deleteUser_userAlreadyDeleted_throwUserStatusException() {
        when(userEntityRepository.findByUsername(userEntity.getUsername())).thenReturn(Optional.of(userEntity));

        userEntityService.deleteUser(userEntity.getUsername());

        assertThrows(UpdateUserStatusException.class, () -> userEntityService.deleteUser(userEntity.getUsername()));
    }

    @Test
    void activateUser_userAlreadyActive_throwUserStatusException() {
        when(userEntityRepository.findByUsername(userEntity.getUsername())).thenReturn(Optional.of(userEntity));

        assertThrows(UpdateUserStatusException.class, () -> userEntityService.activateUser(userEntity.getUsername()));
    }


    UpdateUserRequest returnUpdateUserRequest() {
        return new UpdateUserRequest(
                "newUsername",
                "newPassword",
                "newEmail",
                "newFirstname",
                "newLastname"
        );
    }

    CreateUserRequest returnCreateUserRequest() {
        return new CreateUserRequest(
                "firstName",
                "lastName",
                "email",
                "username",
                "password",
                Set.of(Role.USER, Role.ADMIN));
    }

}