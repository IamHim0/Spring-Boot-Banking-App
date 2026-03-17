//package com.cfkiatong.springbootbankingapp.service;
//
//import com.cfkiatong.springbootbankingapp.dto.request.AuthenticationRequest;
//import com.cfkiatong.springbootbankingapp.dto.response.AuthenticationResponse;
//import com.cfkiatong.springbootbankingapp.security.UserPrincipal;
//import com.cfkiatong.springbootbankingapp.security.jwt.JwtService;
//import com.cfkiatong.springbootbankingapp.services.AuthenticationService;
//import com.cfkiatong.springbootbankingapp.services.LoginAttemptService;
//import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.authentication.AuthenticationManager;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class AuthenticationServiceTest {
//
//    @Mock
//    private AuthenticationManager authenticationManager;
//
//    @Mock
//    private JwtService jwtService;
//
//    @Mock
//    private LoginAttemptService loginAttemptService;
//
//    private AuthenticationService authenticationService;
//
//    private final String username = "username";
//    private final String password = "password";
//    private final String token = "mock-jwt";
//
//    @BeforeEach
//    void setup() {
//        authenticationService = new AuthenticationService(
//                authenticationManager,
//                jwtService,
//                loginAttemptService
//        );
//    }
//
//    @Test
//    void authenticate_success_resetsAttempts_andReturnsToken() {
//        // arrange
//        AuthenticationRequest request = new AuthenticationRequest(username, password);
//
//        UserPrincipal principal = mock(UserPrincipal.class);
//        Authentication authentication = mock(Authentication.class);
//
//        when(authenticationManager.authenticate(any()))
//                .thenReturn(authentication);
//
//        when(authentication.getPrincipal()).thenReturn(principal);
//        when(jwtService.generateToken(principal)).thenReturn(token);
//        when(principal.getFailedLoginAttempts()).thenReturn(2);
//        when(principal.getUsername()).thenReturn(username);
//
//        // act
//        AuthenticationResponse response =
//                authenticationService.authenticate(request);
//
//        // assert
//        assertEquals(token, response.jwtToken());
//
//        verify(authenticationManager).authenticate(any());
//        verify(jwtService).generateToken(principal);
//        verify(loginAttemptService).resetFailedAttempts(username);
//    }
//
//    @Test
//    void authenticate_success_noReset_whenNoFailedAttempts() {
//        // arrange
//        AuthenticationRequest request = new AuthenticationRequest(username, password);
//
//        UserPrincipal principal = mock(UserPrincipal.class);
//        Authentication authentication = mock(Authentication.class);
//
//        when(authenticationManager.authenticate(any()))
//                .thenReturn(authentication);
//
//        when(authentication.getPrincipal()).thenReturn(principal);
//        when(jwtService.generateToken(principal)).thenReturn(token);
//        when(principal.getFailedLoginAttempts()).thenReturn(0);
//
//        // act
//        authenticationService.authenticate(request);
//
//        // assert
//        verify(loginAttemptService, never()).resetFailedAttempts(any());
//    }
//
//    @Test
//    void authenticate_badCredentials_recordsFailure() {
//        // arrange
//        AuthenticationRequest request = new AuthenticationRequest(username, password);
//
//        when(authenticationManager.authenticate(any()))
//                .thenThrow(new BadCredentialsException("bad"));
//
//        // act + assert
//        assertThrows(BadCredentialsException.class, () ->
//                authenticationService.authenticate(request)
//        );
//
//        verify(loginAttemptService).recordFailedAttempt(username);
//        verify(jwtService, never()).generateToken(any());
//    }
//}