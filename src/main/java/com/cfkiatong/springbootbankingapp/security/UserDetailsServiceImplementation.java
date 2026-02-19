package com.cfkiatong.springbootbankingapp.security;

import com.cfkiatong.springbootbankingapp.entity.Account;
import com.cfkiatong.springbootbankingapp.entity.UserEntity;
import com.cfkiatong.springbootbankingapp.exception.business.AccountNotFoundException;
import com.cfkiatong.springbootbankingapp.repository.AccountRepository;
import com.cfkiatong.springbootbankingapp.repository.UserEntityRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImplementation implements UserDetailsService {

    private final UserEntityRepository userEntityRepository;

    public UserDetailsServiceImplementation(UserEntityRepository userEntityRepository) {
        this.userEntityRepository = userEntityRepository;
    }

    @Override
    public UserPrincipal loadUserByUsername(String username) throws AccountNotFoundException {
        UserEntity userEntity = userEntityRepository.findByUsername(username).orElseThrow(() -> new AccountNotFoundException(username));
        return new UserPrincipal(userEntity);
    }

}