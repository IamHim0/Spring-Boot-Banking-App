package com.cfkiatong.springbootbankingapp.security;

import com.cfkiatong.springbootbankingapp.dto.AuthenticationRequest;
import com.cfkiatong.springbootbankingapp.dto.AuthenticationResponse;
import com.cfkiatong.springbootbankingapp.entity.Account;
import com.cfkiatong.springbootbankingapp.exception.business.AccountNotFoundException;
import com.cfkiatong.springbootbankingapp.repository.AccountRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthenticationService {


    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthenticationService(
            AuthenticationManager authenticationManager,
            JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {

        Authentication authentication =
                authenticationManager.
                        authenticate(new UsernamePasswordAuthenticationToken(
                                authenticationRequest.getUsername(),
                                authenticationRequest.getPassword()));

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        if (authentication.isAuthenticated()) {
            return mapToAuthenticationResponse(jwtService.generateToken(principal.getId().toString()));
        } else {
            return null;
        }
    }

    public AuthenticationResponse mapToAuthenticationResponse(String token) {
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();

        authenticationResponse.setJwtToken(token);

        return authenticationResponse;
    }

}
