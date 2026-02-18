package com.cfkiatong.springbootbankingapp.services;

import com.cfkiatong.springbootbankingapp.dto.*;
import com.cfkiatong.springbootbankingapp.entity.Account;
import com.cfkiatong.springbootbankingapp.exception.UnauthorizedException;
import com.cfkiatong.springbootbankingapp.exception.business.AccountNotFoundException;
import com.cfkiatong.springbootbankingapp.repository.AccountRepository;
import com.cfkiatong.springbootbankingapp.repository.UserEntityRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserEntityRepository userEntityRepository;
    private final Mapper  mapper;

    public AccountService(AccountRepository accountRepository, UserEntityRepository userEntityRepository, Mapper mapper) {
        this.accountRepository = accountRepository;
        this.userEntityRepository = userEntityRepository;
        this.mapper = mapper;
    }

    private Account findAccount(UUID id) {
        return accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException(id));
    }

    public ViewAccountResponse createAccount(CreateAccountRequest createAccountRequest) {

        Account account = new Account(
                createAccountRequest.getInitialDeposit(),
                createAccountRequest.getAccountType(),
                userEntityRepository.findById(createAccountRequest.getOwnerId()).orElseThrow(() -> new AccountNotFoundException("User")
                )
        );

        accountRepository.save(account);
        return mapper.mapToViewAccountResponse(account);
    }

    public ViewAccountResponse getAccount(UserDetails userDetails,  UUID accountId) {
        Account account = findAccount(accountId);

        if(!account.getOwner().getId().toString().equals(userDetails.getUsername())) {
            throw new UnauthorizedException();
        }

        return mapper.mapToViewAccountResponse(account);
    }

    @Transactional
    public ViewAccountResponse changeAccountType(UserDetails userDetails, UUID accountId, ChangeAccountTypeRequest changeAccountTypeRequest) {
        Account account = findAccount(accountId);

        if(!account.getOwner().getId().toString().equals(userDetails.getUsername())) {
            throw new UnauthorizedException();
        }

        account.setAccountType(changeAccountTypeRequest.getAccountType());

        return mapper.mapToViewAccountResponse(account);
    }

    public void deleteAccount(UserDetails userDetails, UUID accountId) {

        if(!findAccount(accountId).getOwner().getId().toString().equals(userDetails.getUsername())) {
            throw new UnauthorizedException();
        }

        accountRepository.deleteById(accountId);
    }

}
