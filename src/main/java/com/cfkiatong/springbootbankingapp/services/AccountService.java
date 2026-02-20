package com.cfkiatong.springbootbankingapp.services;

import com.cfkiatong.springbootbankingapp.dto.CreateAccountRequest;
import com.cfkiatong.springbootbankingapp.dto.UpdateAccountRequest;
import com.cfkiatong.springbootbankingapp.dto.ViewAccountResponse;
import com.cfkiatong.springbootbankingapp.entity.Account;
import com.cfkiatong.springbootbankingapp.exception.business.AccountNotFoundException;
import com.cfkiatong.springbootbankingapp.exception.business.UsernameUnavailableException;
import com.cfkiatong.springbootbankingapp.repository.AccountRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    private Account findAccount(UUID id) {
        return accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException(id));
    }

    //Write methods (save, delete, deleteById, etc.) are @Transactional by default
    public ViewAccountResponse addAccount(CreateAccountRequest createAccountRequest) {
        if (accountRepository.findByUsername(createAccountRequest.getUsername()).isPresent()) {
            throw new UsernameUnavailableException(createAccountRequest.getUsername());
        }

        Account account = new Account(
                createAccountRequest.getFirstName(),
                createAccountRequest.getLastName(),
                createAccountRequest.getUsername(),
                new BCryptPasswordEncoder().encode(createAccountRequest.getPassword()),
                createAccountRequest.getInitialDeposit());
        accountRepository.save(account);

        return mapToViewAccountResponse(account);
    }

    public ViewAccountResponse getAccount(UUID id) {
        return mapToViewAccountResponse(findAccount(id));
    }

    public void deleteAccount(UUID id) {
        if (findAccount(id) != null) {
            accountRepository.deleteById(id);
        }
    }

    @Transactional
    public ViewAccountResponse updateAccount(UUID id, UpdateAccountRequest updateAccountRequest) {
        Account account = findAccount(id);

        if (updateAccountRequest.getNewFirstName() != null) {
            account.setFirstName(updateAccountRequest.getNewFirstName());
        }
        if (updateAccountRequest.getNewLastName() != null) {
            account.setLastName(updateAccountRequest.getNewLastName());
        }
        if (updateAccountRequest.getNewUsername() != null) {
            account.setUsername(updateAccountRequest.getNewUsername());
        }
        if (updateAccountRequest.getNewPassword() != null) {
            String hashedNewPassword = new BCryptPasswordEncoder().encode(updateAccountRequest.getNewPassword());

            account.setPassword(hashedNewPassword);
        }

        return mapToViewAccountResponse(account);
    }

    //DTO MAPPING
    private ViewAccountResponse mapToViewAccountResponse(Account account) {
        ViewAccountResponse accDTO = new ViewAccountResponse();

        accDTO.setId(account.getId());
        accDTO.setFirstName(account.getFirstName());
        accDTO.setLastName(account.getLastName());
        accDTO.setUsername(account.getUsername());
        accDTO.setBalance(account.getBalance());

        return accDTO;
    }

}
