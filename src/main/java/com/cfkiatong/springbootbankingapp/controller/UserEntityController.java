package com.cfkiatong.springbootbankingapp.controller;

import com.cfkiatong.springbootbankingapp.dto.CreateUserRequest;
import com.cfkiatong.springbootbankingapp.dto.ViewUserResponse;
import com.cfkiatong.springbootbankingapp.services.UserEntityService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserEntityController {

    private final UserEntityService userEntityService;

    public UserEntityController(UserEntityService userEntityService) {
        this.userEntityService = userEntityService;
    }

    @PostMapping
    public ResponseEntity<ViewUserResponse> createUser(@RequestBody CreateUserRequest createUserRequest) {
        ViewUserResponse userResponse = userEntityService.createUser(createUserRequest);

        return ResponseEntity.created(URI.create("api/v1/users/me")).body(userResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<ViewUserResponse> getUser(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userEntityService.getUser(userDetails));
    }

}