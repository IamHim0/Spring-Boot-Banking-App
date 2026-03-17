package com.cfkiatong.springbootbankingapp.services;

import com.cfkiatong.springbootbankingapp.entity.UserEntity;
import com.cfkiatong.springbootbankingapp.exception.business.UserNotFoundException;
import com.cfkiatong.springbootbankingapp.repository.UserEntityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class LoginAttemptService {

    private final UserEntityRepository userEntityRepository;
    private final int maxAttempts = 3;

    public LoginAttemptService(UserEntityRepository userEntityRepository) {
        this.userEntityRepository = userEntityRepository;
    }

    private UserEntity findUser(String username) {
        return userEntityRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
    }

    @Transactional
    public void recordFailedAttempt(String username) {
        UserEntity userEntity = findUser(username);

        userEntity.setFailedLoginAttempts(userEntity.getFailedLoginAttempts() + 1);

        if (userEntity.getFailedLoginAttempts() % maxAttempts == 0) {
            userEntity.lockUser();
            userEntity.setUnlocksAt(LocalDateTime.now().plusHours(2));
        }
    }

    @Transactional
    public void resetFailedAttempts(String username) {
        UserEntity userEntity = findUser(username);

        userEntity.setFailedLoginAttempts(0);
        userEntity.setUnlocksAt(null);
        userEntity.activateUser();
    }

}