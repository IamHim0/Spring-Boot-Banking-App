package com.cfkiatong.springbootbankingapp.services;

import com.cfkiatong.springbootbankingapp.dto.GetAccountResponse;
import com.cfkiatong.springbootbankingapp.dto.ChangeAccountOwnerRequest;
import com.cfkiatong.springbootbankingapp.entity.Account;
import com.cfkiatong.springbootbankingapp.entity.UserEntity;
import com.cfkiatong.springbootbankingapp.exception.UnauthorizedException;
import com.cfkiatong.springbootbankingapp.exception.business.AccountNotFoundException;
import com.cfkiatong.springbootbankingapp.repository.AccountRepository;
import com.cfkiatong.springbootbankingapp.repository.UserEntityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserEntityRepository userEntityRepository;

    public AccountService(AccountRepository accountRepository, UserEntityRepository userEntityRepository) {
        this.accountRepository = accountRepository;
        this.userEntityRepository = userEntityRepository;
    }

    private Account findAccount(UUID id) {
        return accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException(id));
    }

    //Write methods (save, delete, deleteById, etc.) are @Transactional by default
    public GetAccountResponse createAccount(UUID ownerId) {
        UserEntity accountOwner = userEntityRepository.findById(ownerId).orElseThrow(() -> new AccountNotFoundException(ownerId));

        Account account = new Account(accountOwner);

        accountRepository.save(account);

        return mapToViewAccountResponse(account);
    }

    public GetAccountResponse getAccount(UUID accountOwnerId, UUID accountId) {
        Account account = findAccount(accountId);

        if (!account.getAccountOwner().getUserId().equals(accountOwnerId)) {
            throw new UnauthorizedException();
        }

        return mapToViewAccountResponse(account);
    }

    @Transactional
    public GetAccountResponse changeAccountOwner(UUID accountOwnerId, UUID accountId, ChangeAccountOwnerRequest changeAccountOwnerRequest) {
        Account account = findAccount(accountId);

        if (!account.getAccountOwner().getUserId().equals(accountOwnerId)) {
            throw new UnauthorizedException();
        }

        account.setAccountOwner(userEntityRepository.
                findByUsername(
                        changeAccountOwnerRequest.getNewAccountOwner())
                .orElseThrow(() -> new AccountNotFoundException(changeAccountOwnerRequest.getNewAccountOwner())));

        return mapToViewAccountResponse(account);
    }

    public void deleteAccount(UUID id) {
        if (findAccount(id) != null) {
            accountRepository.deleteById(id);
        }
    }

    //DTO MAPPING
    private GetAccountResponse mapToViewAccountResponse(Account account) {
        GetAccountResponse accDTO = new GetAccountResponse();

        accDTO.setId(account.getId());
        accDTO.setAccountOwner(account.getAccountOwner().getUsername());
        accDTO.setBalance(account.getBalance());

        return accDTO;
    }

}