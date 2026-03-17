package com.cfkiatong.springbootbankingapp.services;

import com.cfkiatong.springbootbankingapp.dto.request.AuthenticationRequest;
import com.cfkiatong.springbootbankingapp.dto.response.AuthenticationResponse;
import com.cfkiatong.springbootbankingapp.security.UserPrincipal;
import com.cfkiatong.springbootbankingapp.security.jwt.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {


    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final LoginAttemptService loginAttemptService;

    public AuthenticationService(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            LoginAttemptService loginAttemptService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.loginAttemptService = loginAttemptService;
    }

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        try {
            Authentication authentication =
                    authenticationManager.
                            authenticate(new UsernamePasswordAuthenticationToken(
                                    authenticationRequest.username(),
                                    authenticationRequest.password()));

            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

            if (principal.getFailedLoginAttempts() > 0) {
                loginAttemptService.resetFailedAttempts(principal.getUsername());
            }

            return mapToAuthenticationResponse(jwtService.generateToken(principal));
        } catch (BadCredentialsException e) {
            loginAttemptService.recordFailedAttempt(authenticationRequest.username());
            throw e;
        }
    }

    private AuthenticationResponse mapToAuthenticationResponse(String token) {
        AuthenticationResponse authenticationResponse = new AuthenticationResponse(token);

        return authenticationResponse;
    }

}