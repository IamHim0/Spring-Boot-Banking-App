package com.cfkiatong.springbootbankingapp.controller;

import com.cfkiatong.springbootbankingapp.dto.request.CreateUserRequest;
import com.cfkiatong.springbootbankingapp.dto.request.UpdateUserRequest;
import com.cfkiatong.springbootbankingapp.dto.response.UserResponse;
import com.cfkiatong.springbootbankingapp.exception.business.NoFieldUpdatedException;
import com.cfkiatong.springbootbankingapp.services.UserEntityService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/users")
public class UserEntityController {

    private final UserEntityService userEntityService;

    public UserEntityController(UserEntityService userEntityService) {
        this.userEntityService = userEntityService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody CreateUserRequest createUserRequest) {
        return ResponseEntity.ok(userEntityService.createUser(createUserRequest));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getUser(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userEntityService.getUser(UUID.fromString(userDetails.getUsername())));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserResponse> updateUser(@AuthenticationPrincipal UserDetails userDetails, @RequestBody UpdateUserRequest updateUserRequest) {
        if (!updateUserRequest.oneFieldPresent()) {
            throw new NoFieldUpdatedException();
        }

        return ResponseEntity.ok(userEntityService.updateUser(UUID.fromString(userDetails.getUsername()), updateUserRequest));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal UserDetails userDetails) {
        userEntityService.deleteUser(UUID.fromString(userDetails.getUsername()));

        return ResponseEntity.notFound().build();
    }

}