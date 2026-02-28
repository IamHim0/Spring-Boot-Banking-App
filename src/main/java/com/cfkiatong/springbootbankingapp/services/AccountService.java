package com.cfkiatong.springbootbankingapp.services;

import com.cfkiatong.springbootbankingapp.dto.response.AccountResponse;
import com.cfkiatong.springbootbankingapp.dto.request.ChangeAccountOwnerRequest;
import com.cfkiatong.springbootbankingapp.dto.Mapper;
import com.cfkiatong.springbootbankingapp.entity.Account;
import com.cfkiatong.springbootbankingapp.entity.UserEntity;
import com.cfkiatong.springbootbankingapp.exception.ForbiddenException;
import com.cfkiatong.springbootbankingapp.exception.business.AccountNotFoundException;
import com.cfkiatong.springbootbankingapp.exception.business.InvalidAccountStateException;
import com.cfkiatong.springbootbankingapp.exception.business.UserNotFoundException;
import com.cfkiatong.springbootbankingapp.repository.AccountRepository;
import com.cfkiatong.springbootbankingapp.repository.UserEntityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserEntityRepository userEntityRepository;
    private final Mapper mapper;

    public AccountService(AccountRepository accountRepository, UserEntityRepository userEntityRepository, Mapper mapper) {
        this.accountRepository = accountRepository;
        this.userEntityRepository = userEntityRepository;
        this.mapper = mapper;
    }

    private Account findAccount(UUID id) {
        return accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException(id));
    }

    public AccountResponse createAccount(UUID ownerId) {
        UserEntity accountOwner = userEntityRepository.findById(ownerId).orElseThrow();

        Account account = new Account(accountOwner);

        accountRepository.save(account);

        return mapper.mapToAccountResponse(account);
    }

    public AccountResponse getAccount(UUID accountOwnerId, UUID accountId) {
        Account account = findAccount(accountId);

        if (!account.getAccountOwner().getUserId().equals(accountOwnerId)) {
            throw new ForbiddenException();
        }

        return mapper.mapToAccountResponse(account);
    }

    @Transactional
    public AccountResponse changeAccountOwner(UUID accountOwnerId, UUID accountId, ChangeAccountOwnerRequest changeAccountOwnerRequest) {
        Account account = findAccount(accountId);
        UUID currentOwnerId = account.getAccountOwner().getUserId();

        if (!currentOwnerId.equals(accountOwnerId)) {
            throw new ForbiddenException();
        }

        UserEntity newOwner = userEntityRepository.
                findByUsername(
                        changeAccountOwnerRequest.getNewAccountOwner())
                .orElseThrow(() -> new UserNotFoundException(changeAccountOwnerRequest.getNewAccountOwner()));

        UUID newOwnerId = account.getAccountOwner().getUserId();

        if (currentOwnerId.equals(newOwnerId)) {
            throw new InvalidAccountStateException("New owner must be different from current owner");
        }

        account.setAccountOwner(newOwner);

        return mapper.mapToAccountResponse(account);
    }

    public void deleteAccount(UUID ownerId, UUID accountId) {
        if (!findAccount(accountId).getAccountOwner().getUserId().equals(ownerId)) {
            throw new ForbiddenException();
        }

        accountRepository.deleteById(accountId);
    }

}