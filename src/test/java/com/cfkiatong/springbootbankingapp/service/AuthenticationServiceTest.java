package com.cfkiatong.springbootbankingapp.service;

import com.cfkiatong.springbootbankingapp.dto.Role;
import com.cfkiatong.springbootbankingapp.dto.UserStatus;
import com.cfkiatong.springbootbankingapp.dto.request.AuthenticationRequest;
import com.cfkiatong.springbootbankingapp.dto.response.AuthenticationResponse;
import com.cfkiatong.springbootbankingapp.entity.UserEntity;
import com.cfkiatong.springbootbankingapp.repository.UserEntityRepository;
import com.cfkiatong.springbootbankingapp.security.UserPrincipal;
import com.cfkiatong.springbootbankingapp.security.jwt.JwtService;
import com.cfkiatong.springbootbankingapp.services.AuthenticationService;
import com.cfkiatong.springbootbankingapp.services.LoginAttemptService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationServiceTest.class);
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;

    @Mock
    private UserEntityRepository userEntityRepository;

    private UserEntity userEntity;
    private final UUID userId = UUID.randomUUID();
    private final String username = "username";
    private final String password = "password";
    private final String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI5YzE0NGEzYS1iODJjLTQxYmEtOWY5NC0xMTdkMjEzMDMwY" +
            "mIiLCJyb2xlcyI6WyJBRE1JTiIsIlVTRVIiXSwiaWF0IjoxNzczNzMwMDI4LCJleHAiOjE3NzM3MzcyMjh9" +
            ".sl_yhxt2u8b4N-Yt2eBpsEEX75ydfisJIPi6fYB2Nmo";

    private UserPrincipal userPrincipal;

    private AuthenticationService authenticationService;

    @BeforeEach
    void setup() {
        LoginAttemptService loginAttemptService = new LoginAttemptService(userEntityRepository);

        authenticationService = new AuthenticationService(
                authenticationManager,
                jwtService,
                loginAttemptService
        );

        userEntity = new UserEntity(
                "firstName",
                "lastName",
                "email",
                username,
                password,
                new HashSet<>(Set.of(Role.ADMIN, Role.USER)),
                null
        );

        userEntity.setUserId(userId);
        userPrincipal = new UserPrincipal(userEntity);
    }

    @Test
    void authenticate_validCredentialsNoFailedAttempts_returnAuthenticationResponse() {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(username, password);
        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.username(),
                        authenticationRequest.password())))
                .thenReturn(authentication);

        when(authentication.getPrincipal()).thenReturn(userPrincipal);

        when(jwtService.generateToken(userPrincipal)).thenReturn(token);

        AuthenticationResponse authenticationResponse = authenticationService.authenticate(authenticationRequest);

        assertEquals(token, authenticationResponse.jwtToken());

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        verify(jwtService).generateToken(userPrincipal);
    }

    @Test
    void authenticate_validCredentialsFailedAttemptsNotMaxed_resetFailedAttemptsAndReturnAuthenticationResponse() {
        userEntity.setFailedLoginAttempts(2);
        userPrincipal = new UserPrincipal(userEntity);

        AuthenticationRequest authenticationRequest = new AuthenticationRequest(username, password);
        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.username(),
                        authenticationRequest.password())))
                .thenReturn(authentication);

        when(authentication.getPrincipal()).thenReturn(userPrincipal);

        when(jwtService.generateToken(userPrincipal)).thenReturn(token);

        when(userEntityRepository.findByUsername(username)).thenReturn(Optional.of(userEntity));

        AuthenticationResponse authenticationResponse = authenticationService.authenticate(authenticationRequest);

        assertEquals(token, authenticationResponse.jwtToken());

        assertEquals(0, userEntity.getFailedLoginAttempts());

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        verify(jwtService).generateToken(userPrincipal);
    }

    //EXCEPTION TESTING
    @Test
    void authenticate_invalidCredentialsAttemptsNotMaxed_incrementFailedAttemptsThrowBadCredentialsException() {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(username, "wrongPassword");

        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.username(),
                        authenticationRequest.password()))).thenThrow(new BadCredentialsException("Invalid credentials"));

        when(userEntityRepository.findByUsername(username)).thenReturn(Optional.of(userEntity));

        assertThrows(BadCredentialsException.class, () -> {
            authenticationService.authenticate(authenticationRequest);
        });

        assertEquals(1, userEntity.getFailedLoginAttempts());

        verify(jwtService, never()).generateToken(userPrincipal);
    }

    @Test
    void authenticate_invalidCredentialsAttemptMaxed_lockUserThrowBadCredentialsException() {
        userEntity.setFailedLoginAttempts(2);
        userPrincipal = new UserPrincipal(userEntity);

        AuthenticationRequest authenticationRequest = new AuthenticationRequest(username, "wrongPassword");

        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.username(),
                        authenticationRequest.password()))).thenThrow(new BadCredentialsException("Invalid credentials"));

        when(userEntityRepository.findByUsername(username)).thenReturn(Optional.of(userEntity));

        assertThrows(BadCredentialsException.class, () -> {
            authenticationService.authenticate(authenticationRequest);
        });

        assertEquals(3, userEntity.getFailedLoginAttempts());
        assertEquals(UserStatus.LOCKED, userEntity.getUserStatus());
        verify(jwtService, never()).generateToken(userPrincipal);
    }

}