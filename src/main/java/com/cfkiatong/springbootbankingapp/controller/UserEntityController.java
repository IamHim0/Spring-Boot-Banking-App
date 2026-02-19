package com.cfkiatong.springbootbankingapp.controller;

import com.cfkiatong.springbootbankingapp.dto.CreateUserRequest;
import com.cfkiatong.springbootbankingapp.entity.UserEntity;
import com.cfkiatong.springbootbankingapp.services.UserEntityService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/users")
public class UserEntityController {

    private final UserEntityService userEntityService;

    public UserEntityController(UserEntityService userEntityService) {
        this.userEntityService = userEntityService;
    }

    @PostMapping
    public UserEntity createUser(@RequestBody CreateUserRequest createUserRequest) {
        return userEntityService.createUser(createUserRequest);
    }

}
