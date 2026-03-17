package com.cfkiatong.springbootbankingapp.service;

import com.cfkiatong.springbootbankingapp.dto.Role;
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
import org.springframework.security.core.Authentication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AuthenticationServiceTests {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @Mock
    private LoginAttemptService loginAttemptService;
    @Mock
    private UserEntityRepository userEntityRepository;

    private UserEntity userEntity;
    private final UUID userId = UUID.randomUUID();
    private final String username = "username";
    private final String password = "password";
    private final String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI5YzE0NGEzYS1iODJjLTQxYmEtOWY5NC0xMTdkMjEzMDMwYmIiLCJyb2xlcyI6WyJBRE1JTiIsIlVTRVIiXSwiaWF0IjoxNzczNzMwMDI4LCJleHAiOjE3NzM3MzcyMjh9.sl_yhxt2u8b4N-Yt2eBpsEEX75ydfisJIPi6fYB2Nmo";

    private UserPrincipal userPrincipal;

    @ExtendWith(MockitoExtension.class)
    private AuthenticationService authenticationService;

    @BeforeEach
    void setup() {
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
    void authenticate_validCredentials_returnAuthenticationResponse() {
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
    void authenticate_validCredentials_returnsToken_andResetsAttempts() {
        AuthenticationRequest request = new AuthenticationRequest(username, password);
        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)))
                .thenReturn(authentication);

        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(jwtService.generateToken(userPrincipal)).thenReturn(token);
        when(userPrincipal.getFailedLoginAttempts()).thenReturn(1);
        when(userPrincipal.getUsername()).thenReturn(username);

        AuthenticationResponse response = authenticationService.authenticate(request);

        // output
        assertEquals(token, response.jwtToken());

        // interactions
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        verify(jwtService).generateToken(userPrincipal);
        verify(loginAttemptService).resetFailedAttempts(username);
    }

//    @Test
//    void authenticate_validCredentialsAndFailedAttemptsGreaterThan0_resetFailedAttemptsAndReturnAuthenticationResponse() {
//        AuthenticationRequest authenticationRequest = new AuthenticationRequest(username, password);
//        Authentication authentication = mock(Authentication.class);
//
//        userEntity.setFailedLoginAttempts(2);
//
//        when(authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        authenticationRequest.username(),
//                        authenticationRequest.password())))
//                .thenReturn(authentication);
//
//        when(authentication.getPrincipal()).thenReturn(userPrincipal);
//        when(jwtService.generateToken(userPrincipal)).thenReturn(token);
//        doNothing().when(loginAttemptService).resetFailedAttempts(username);
//
//        AuthenticationResponse authenticationResponse = authenticationService.authenticate(authenticationRequest);
//
//        assertEquals(token, authenticationResponse.jwtToken());
//
//        verify(authenticationManager).authenticate(
//                new UsernamePasswordAuthenticationToken(username, password)
//        );
//
//        verify(jwtService).generateToken(userPrincipal);
//        verify(loginAttemptService).resetFailedAttempts(username);
//    }

}
