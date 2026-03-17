// COMPLETE CONSOLIDATED JAVA CODE - SPRING BOOT BANKING APPLICATION
// This file contains ALL Java classes from the Spring Boot Banking App project
// Generated on: 2025-06-17
// Total Java files: 32

// ================================================================================
// MAIN APPLICATION CLASS
// ================================================================================
package com.cfkiatong.springbootbankingapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringbootBankingappApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootBankingappApplication.class, args);
    }
}

// ================================================================================
// CONFIGURATION
// ================================================================================
package com.cfkiatong.springbootbankingapp.config;

import com.cfkiatong.springbootbankingapp.security.CustomAuthenticationEntryPoint;
import com.cfkiatong.springbootbankingapp.security.jwt.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    private final UserDetailsService userDetailsService;
    private final JwtFilter jwtFilter;
    private final CustomAuthenticationEntryPoint customAuthEntryPoint;

    public SecurityConfiguration(UserDetailsService userDetailsService, JwtFilter jwtFilter, CustomAuthenticationEntryPoint customAuthEntryPoint) {
        this.userDetailsService = userDetailsService;
        this.jwtFilter = jwtFilter;
        this.customAuthEntryPoint = customAuthEntryPoint;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //Disable CSRF token
        http.csrf(csrf -> csrf.disable());

        //Enable authentication
        http.authorizeHttpRequests(
                requests ->
                        requests
                                .requestMatchers("/api/v1/users", "/api/v1/auth/login")
                                .permitAll() //Waves authentication for login & register endpoints
                                .anyRequest().authenticated());

        //Postman log-in
        http.httpBasic(Customizer.withDefaults());

        //Custom AuthenticationEntryPoint
        http.exceptionHandling(exception ->
                exception.authenticationEntryPoint(customAuthEntryPoint));

        http.sessionManagement(
                        session
                                -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        //Web form log-in
//        http.formLogin(Customizer.withDefaults());

        return http.build();
    }
}

// ================================================================================
// CONTROLLERS
// ================================================================================
package com.cfkiatong.springbootbankingapp.controller;

import com.cfkiatong.springbootbankingapp.dto.response.AccountResponse;
import com.cfkiatong.springbootbankingapp.dto.request.ChangeAccountOwnerRequest;
import com.cfkiatong.springbootbankingapp.services.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/users/me/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@AuthenticationPrincipal UserDetails userDetails) {
        AccountResponse accountResponse = accountService.createAccount(UUID.fromString(userDetails.getUsername()));

        return ResponseEntity.created(URI.create("/api/v1/users/me/accounts/" + accountResponse.getId())).body(accountResponse);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponse> getAccount(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID accountId) {
        AccountResponse accountResponse = accountService.getAccount(UUID.fromString(userDetails.getUsername()), accountId);

        return ResponseEntity.ok(accountResponse);
    }

    @PatchMapping("/{accountId}")
    public ResponseEntity<AccountResponse> changeAccountOwner(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID accountId, @RequestBody ChangeAccountOwnerRequest changeAccountOwnerRequest) {
        return ResponseEntity.ok(accountService.changeAccountOwner(UUID.fromString(userDetails.getUsername()), accountId, changeAccountOwnerRequest));
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> deleteAccount(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID accountId) {
        accountService.deleteAccount(UUID.fromString(userDetails.getUsername()), accountId);

        return ResponseEntity.noContent().build();
    }
}

package com.cfkiatong.springbootbankingapp.controller;

import com.cfkiatong.springbootbankingapp.dto.request.UpdateRolesRequest;
import com.cfkiatong.springbootbankingapp.dto.request.UpdateUserRequest;
import com.cfkiatong.springbootbankingapp.dto.request.UpdateUserStatusRequest;
import com.cfkiatong.springbootbankingapp.dto.response.AccountResponse;
import com.cfkiatong.springbootbankingapp.dto.response.TransactionResponse;
import com.cfkiatong.springbootbankingapp.dto.response.UserResponse;
import com.cfkiatong.springbootbankingapp.dto.response.UserRolesResponse;
import com.cfkiatong.springbootbankingapp.services.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("api/v1/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        return ResponseEntity.ok(adminService.getAllAccounts());
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionResponse>> getAllTransactions() {
        return ResponseEntity.ok(adminService.getAllTransactions());
    }

    @GetMapping("/users/{username}")
    public ResponseEntity<UserResponse> getUser(@PathVariable String username) {
        return ResponseEntity.ok(adminService.getUser(username));
    }

    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable UUID accountId) {
        return ResponseEntity.ok(adminService.getAccount(accountId));
    }

    @GetMapping("/transactions/{transactionId}")
    public ResponseEntity<TransactionResponse> getTransaction(@PathVariable UUID transactionId) {
        return ResponseEntity.ok(adminService.getTransaction(transactionId));
    }

    @PatchMapping("/users/{username}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable String username, @RequestBody UpdateUserRequest updateUserRequest) {
        return ResponseEntity.ok(adminService.updateUser(username, updateUserRequest));
    }

    @PatchMapping("/users/{username}/status")
    public ResponseEntity<UserResponse> updateUserStatus(@PathVariable String username, @RequestBody UpdateUserStatusRequest updateUserStatusRequest) {
        return ResponseEntity.ok(adminService.updateUserStatus(username, updateUserStatusRequest));
    }

    @DeleteMapping("/users/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable String username) {
        adminService.deleteUser(username);

        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/users/{username}/roles")
    public ResponseEntity<UserRolesResponse> updateUserRoles(@PathVariable String username, @RequestBody UpdateRolesRequest updateRolesRequest) {
        return ResponseEntity.ok(adminService.updateUserRoles(username, updateRolesRequest));
    }
}

package com.cfkiatong.springbootbankingapp.controller;

import com.cfkiatong.springbootbankingapp.dto.request.AuthenticationRequest;
import com.cfkiatong.springbootbankingapp.dto.response.AuthenticationResponse;
import com.cfkiatong.springbootbankingapp.services.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest authenticationRequest) {
        return ResponseEntity.ok(authenticationService.authenticate(authenticationRequest));
    }
}

package com.cfkiatong.springbootbankingapp.controller;

import com.cfkiatong.springbootbankingapp.dto.*;
import com.cfkiatong.springbootbankingapp.dto.request.TransactionRequest;
import com.cfkiatong.springbootbankingapp.dto.response.BalanceResponse;
import com.cfkiatong.springbootbankingapp.dto.response.TransactionResponse;
import com.cfkiatong.springbootbankingapp.exception.business.NoTargetAccountException;
import com.cfkiatong.springbootbankingapp.services.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/users/me/accounts/{accountId}/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<BalanceResponse> getBalance(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID accountId) {
        BalanceResponse balanceResponse = transactionService.getBalance(UUID.fromString(userDetails.getUsername()), accountId);

        return ResponseEntity.ok(balanceResponse);
    }

    @PostMapping
    public ResponseEntity<BalanceResponse> makeTransaction(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID accountId, @Valid @RequestBody TransactionRequest transactionRequest) {
        if (transactionRequest.getType() == TransactionType.TRANSFER && transactionRequest.getTargetAccountId() == null) {
            throw new NoTargetAccountException();
        }

        BalanceResponse transactionResponse = transactionService.makeTransaction(UUID.fromString(userDetails.getUsername()), accountId, transactionRequest);

        return ResponseEntity.ok(transactionResponse);
    }

    @GetMapping("/userhistory")
    public ResponseEntity<List<TransactionResponse>> getUserTransactions(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(transactionService.getUserTransactions(UUID.fromString(userDetails.getUsername())));
    }

    @GetMapping("/accounthistory")
    public ResponseEntity<List<TransactionResponse>> getAccountTransactions(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID accountId) {
        return ResponseEntity.ok(transactionService.getAccountTransactions(UUID.fromString(userDetails.getUsername()), accountId));
    }
}

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

// ================================================================================
// DTO - DATA TRANSFER OBJECTS
// ================================================================================
package com.cfkiatong.springbootbankingapp.dto;

public enum Role {
    ADMIN,
    USER;
}

package com.cfkiatong.springbootbankingapp.dto;

public enum TransactionType {
    WITHDRAWAL,
    DEPOSIT,
    TRANSFER
}

package com.cfkiatong.springbootbankingapp.dto;

public enum UserStatus {
    ACTIVE,
    DISABLED,
    LOCKED,
    DELETED
}

package com.cfkiatong.springbootbankingapp.dto;

public enum AccountStatus {
    ACTIVE,
    SUSPENDED,
    FROZEN,
    CLOSED
}

package com.cfkiatong.springbootbankingapp.dto;

import com.cfkiatong.springbootbankingapp.dto.response.*;
import com.cfkiatong.springbootbankingapp.entity.Account;
import com.cfkiatong.springbootbankingapp.entity.Transaction;
import com.cfkiatong.springbootbankingapp.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Mapper {

    public UserResponse mapToUserResponse(UserEntity userEntity) {

        return new UserResponse(
                userEntity.getUsername(),
                userEntity.getEmail(),
                userEntity.getFirstName(),
                userEntity.getLastName(),
                userEntity.getAccounts() == null ?
                        List.of()
                        :
                        userEntity.getAccounts().
                                stream().map(Account::getId).toList()
        );
    }

    public AccountResponse mapToAccountResponse(Account account) {
        return new AccountResponse(account.getId(), account.getAccountOwner().getUsername(), account.getBalance());
    }

    public BalanceResponse mapToBalanceResponse(Account account) {
        return new BalanceResponse(account.getBalance());
    }

    public TransactionResponse mapToTransactionDTO(Transaction transaction) {
        return new TransactionResponse(
                transaction.getTimestamp(),
                transaction.getTransactionId(),
                transaction.getType(),
                transaction.getSourceAccount(),
                transaction.getTargetAccount(),
                transaction.getTransactionAmount(),
                transaction.getSourceBalanceBefore(),
                transaction.getSourceBalanceAfter()
        );
    }

    public UserRolesResponse mapToUserRolesResponse(UserEntity userEntity) {
        return new UserRolesResponse(
                userEntity.getUsername(),
                userEntity.getRoles()
        );
    }
}

// ================================================================================
// DTO - REQUEST CLASSES
// ================================================================================
package com.cfkiatong.springbootbankingapp.dto.request;

import jakarta.validation.constraints.NotBlank;

public class AuthenticationRequest {

    @NotBlank(message = "username field cannot be empty")
    private String username;
    @NotBlank(message = "password field cannot be empty")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

package com.cfkiatong.springbootbankingapp.dto.request;

public class ChangeAccountOwnerRequest {

    private String newAccountOwner;

    public String getNewAccountOwner() {
        return newAccountOwner;
    }

    public void setNewAccountOwner(String newAccountOwner) {
        this.newAccountOwner = newAccountOwner;
    }
}

package com.cfkiatong.springbootbankingapp.dto.request;

import com.cfkiatong.springbootbankingapp.dto.Role;

import java.util.Set;

public class CreateUserRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String password;
    private Set<Role> roles;

    public CreateUserRequest(String firstName, String lastName, String email, String username, String password, Set<Role> roles) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Set<Role> getRoles() {
        return roles;
    }
}

package com.cfkiatong.springbootbankingapp.dto.request;

import com.cfkiatong.springbootbankingapp.dto.TransactionType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public class TransactionRequest {

    @NotNull(message = "transaction type cannot be empty")
    private TransactionType type;

    @NotNull(message = "transaction amount cannot be empty")
    @DecimalMin(value = "100", message = "transaction amount must be at least ₱ 100")
    @DecimalMax(value = "10000", message = "cannot make transactions worth over ₱ 10,000")
    private BigDecimal amount;

    private UUID targetAccountId;

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public UUID getTargetAccountId() {
        return targetAccountId;
    }

    public void setTargetAccountId(UUID targetAccountId) {
        this.targetAccountId = targetAccountId;
    }
}

package com.cfkiatong.springbootbankingapp.dto.request;

import com.cfkiatong.springbootbankingapp.dto.Role;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record UpdateRolesRequest(@NotNull(message = "Must add or delete a role.") String action,
                                 @NotNull(message = "Roles field cannot be empty.") Set<Role> roles) {
}

package com.cfkiatong.springbootbankingapp.dto.request;

public class UpdateUserRequest {

    private String newUsername;
    private String newPassword;
    private String newEmail;
    private String newFirstName;
    private String newLastName;

    public boolean oneFieldPresent() {
        return newUsername != null || newPassword != null || newEmail != null || newFirstName != null || newLastName != null;
    }

    public String getNewUsername() {
        return newUsername;
    }

    public void setNewUsername(String newUsername) {
        this.newUsername = newUsername;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewEmail() {
        return newEmail;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }

    public String getNewFirstName() {
        return newFirstName;
    }

    public void setNewFirstName(String newFirstName) {
        this.newFirstName = newFirstName;
    }

    public String getNewLastName() {
        return newLastName;
    }

    public void setNewLastName(String newLastName) {
        this.newLastName = newLastName;
    }
}

package com.cfkiatong.springbootbankingapp.dto.request;

import com.cfkiatong.springbootbankingapp.dto.UserStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateUserStatusRequest(
        @NotNull(message = "NewUserStatus field cannot be empty.") UserStatus newUserStatus) {
}

// ================================================================================
// DTO - RESPONSE CLASSES
// ================================================================================
package com.cfkiatong.springbootbankingapp.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public class AccountResponse {

    private final UUID accountId;
    private final String accountOwner;
    private final BigDecimal balance;

    public AccountResponse(UUID accountId, String accountOwner, BigDecimal balance) {
        this.accountId = accountId;
        this.accountOwner = accountOwner;
        this.balance = balance;
    }

    public UUID getId() {
        return accountId;
    }

    public String getAccountOwner() {
        return accountOwner;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}

package com.cfkiatong.springbootbankingapp.dto.response;

public enum AccountStatus {
    ACTIVE,
    SUSPENDED,
    FROZEN,
    CLOSED
}

package com.cfkiatong.springbootbankingapp.dto.response;

public class AuthenticationResponse {

    private String jwtToken;

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }
}

package com.cfkiatong.springbootbankingapp.dto.response;

import java.math.BigDecimal;

public record BalanceResponse(BigDecimal balance) {
}

package com.cfkiatong.springbootbankingapp.dto.response;

import com.cfkiatong.springbootbankingapp.dto.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResponse(LocalDateTime timestamp, UUID transactionId, TransactionType type, UUID sourceAccount,
                                  UUID targetAccount, BigDecimal transactionAmount, BigDecimal sourceBalanceBefore,
                                  BigDecimal sourceBalanceAfter) {

}

package com.cfkiatong.springbootbankingapp.dto.response;

import com.cfkiatong.springbootbankingapp.dto.Role;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public record UserResponse(
        String username,
        String email,
        String firstName,
        String lastName,
        List<UUID> accountIds) {
}

package com.cfkiatong.springbootbankingapp.dto.response;

import com.cfkiatong.springbootbankingapp.dto.Role;

import java.util.Set;

public record UserRolesResponse(String username, Set<Role> roles) {
}

package com.cfkiatong.springbootbankingapp.dto.response;

import java.util.List;

public class UserTransactionHistoryResponse {

    List<TransactionResponse> transactions;

    public UserTransactionHistoryResponse(List<TransactionResponse> transactions) {
        this.transactions = transactions;
    }

    public List<TransactionResponse> getTransactions() {
        return transactions;
    }
}

// ================================================================================
// ENTITIES
// ================================================================================
package com.cfkiatong.springbootbankingapp.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private BigDecimal balance;
    @ManyToOne
    @JoinColumn(name = "account_owner_user_id")
    private UserEntity accountOwner;

    protected Account() {

    }

    public Account(UserEntity accountOwner) {
        this.accountOwner = accountOwner;
        this.balance = new BigDecimal("0");
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public UserEntity getAccountOwner() {
        return accountOwner;
    }

    public void setAccountOwner(UserEntity accountOwner) {
        this.accountOwner = accountOwner;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Account account)) return false;
        return Objects.equals(id, account.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

package com.cfkiatong.springbootbankingapp.entity;

import com.cfkiatong.springbootbankingapp.dto.TransactionType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class Transaction {

    private LocalDateTime timestamp;
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID transactionId;
    @Enumerated(EnumType.STRING)
    private TransactionType type;
    private UUID sourceAccount;
    private UUID targetAccount;
    private BigDecimal transactionAmount;
    private BigDecimal sourceBalanceBefore;
    private BigDecimal sourceBalanceAfter;
    private BigDecimal targetBalanceBefore;
    private BigDecimal targetBalanceAfter;

    public Transaction() {

    }

    public Transaction(LocalDateTime timestamp,
                       TransactionType type,
                       UUID sourceAccount,
                       UUID targetAccount,
                       BigDecimal transactionAmount,
                       BigDecimal sourceBalanceBefore,
                       BigDecimal sourceBalanceAfter,
                       BigDecimal targetBalanceBefore,
                       BigDecimal targetBalanceAfter) {

        this.timestamp = timestamp;
        this.type = type;
        this.sourceAccount = sourceAccount;
        this.targetAccount = targetAccount;
        this.transactionAmount = transactionAmount;
        this.sourceBalanceBefore = sourceBalanceBefore;
        this.sourceBalanceAfter = sourceBalanceAfter;
        this.targetBalanceBefore = targetBalanceBefore;
        this.targetBalanceAfter = targetBalanceAfter;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public UUID getSourceAccount() {
        return sourceAccount;
    }

    public void setSourceAccount(UUID sourceAccount) {
        this.sourceAccount = sourceAccount;
    }

    public UUID getTargetAccount() {
        return targetAccount;
    }

    public void setTargetAccount(UUID targetAccount) {
        this.targetAccount = targetAccount;
    }

    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public BigDecimal getSourceBalanceBefore() {
        return sourceBalanceBefore;
    }

    public void setSourceBalanceBefore(BigDecimal sourceBalanceBefore) {
        this.sourceBalanceBefore = sourceBalanceBefore;
    }

    public BigDecimal getSourceBalanceAfter() {
        return sourceBalanceAfter;
    }

    public void setSourceBalanceAfter(BigDecimal sourceBalanceAfter) {
        this.sourceBalanceAfter = sourceBalanceAfter;
    }

    public BigDecimal getTargetBalanceBefore() {
        return targetBalanceBefore;
    }

    public void setTargetBalanceBefore(BigDecimal targetBalanceBefore) {
        this.targetBalanceBefore = targetBalanceBefore;
    }

    public BigDecimal getTargetBalanceAfter() {
        return targetBalanceAfter;
    }

    public void setTargetBalanceAfter(BigDecimal targetBalanceAfter) {
        this.targetBalanceAfter = targetBalanceAfter;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Transaction that)) return false;
        return Objects.equals(transactionId, that.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(transactionId);
    }
}

package com.cfkiatong.springbootbankingapp.entity;

import com.cfkiatong.springbootbankingapp.dto.UserStatus;
import com.cfkiatong.springbootbankingapp.dto.Role;
import com.cfkiatong.springbootbankingapp.exception.business.UserInactiveException;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "Users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;
    @Column(nullable = false)
    private UserStatus userStatus;
    @OneToMany(mappedBy = "accountOwner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Account> accounts;
    @Column(nullable = false)
    private int failedLoginAttempts = 0;
    @Column
    private LocalDateTime unlocksAt = null;

    public UserEntity() {

    }

    public UserEntity(String firstName, String lastName, String email, String username, String password, Set<Role> roles, List<Account> accounts) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.roles = roles;
        this.userStatus = UserStatus.ACTIVE;
        this.accounts = accounts;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID id) {
        this.userId = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }

    public void assertActive() {
        if (this.userStatus != UserStatus.ACTIVE) {
            throw new UserInactiveException(this.username);
        }
    }

    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(int failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public LocalDateTime getUnlocksAt() {
        return unlocksAt;
    }

    public void setUnlocksAt(LocalDateTime unlocksAt) {
        this.unlocksAt = unlocksAt;
    }

    public void activateUser() {
        this.userStatus = UserStatus.ACTIVE;
    }

    public void disableUser() {
        this.userStatus = UserStatus.DISABLED;
    }

    public void lockUser() {
        this.userStatus = UserStatus.LOCKED;
    }

    public void deleteUser() {
        this.userStatus = UserStatus.DELETED;
    }
}

// ================================================================================
// EXCEPTIONS
// ================================================================================
package com.cfkiatong.springbootbankingapp.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException() {
        super("You are not authorized to perform this transaction");
    }

    public HttpStatus getStatus() {
        return HttpStatus.FORBIDDEN;
    }
}

package com.cfkiatong.springbootbankingapp.exception.business;

import org.springframework.http.HttpStatus;

public abstract class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public abstract HttpStatus getStatus();
}

package com.cfkiatong.springbootbankingapp.exception.business;

import org.springframework.http.HttpStatus;

public class AccountNotFoundException extends BusinessException {
    public AccountNotFoundException() {
        super("Account not found");
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }
}

package com.cfkiatong.springbootbankingapp.exception.business;

import org.springframework.http.HttpStatus;

public class InsufficientBalanceException extends BusinessException {
    public InsufficientBalanceException() {
        super("Insufficient balance for this transaction");
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}

package com.cfkiatong.springbootbankingapp.exception.business;

import org.springframework.http.HttpStatus;

public class InvalidAccountStateException extends BusinessException {
    public InvalidAccountStateException() {
        super("Account is not in a valid state for this operation");
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}

package com.cfkiatong.springbootbankingapp.exception.business;

import org.springframework.http.HttpStatus;

public class NoFieldUpdatedException extends BusinessException {
    public NoFieldUpdatedException() {
        super("At least one field must be provided for update");
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}

package com.cfkiatong.springbootbankingapp.exception.business;

import org.springframework.http.HttpStatus;

public class NoTargetAccountException extends BusinessException {
    public NoTargetAccountException() {
        super("Target account is required for transfer transactions");
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}

package com.cfkiatong.springbootbankingapp.exception.business;

import org.springframework.http.HttpStatus;

public class UpdateUserRoleException extends BusinessException {
    public UpdateUserRoleException() {
        super("Failed to update user role");
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}

package com.cfkiatong.springbootbankingapp.exception.business;

import org.springframework.http.HttpStatus;

public class UpdateUserStatusException extends BusinessException {
    public UpdateUserStatusException() {
        super("Failed to update user status");
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}

package com.cfkiatong.springbootbankingapp.exception.business;

import org.springframework.http.HttpStatus;

public class UserInactiveException extends BusinessException {
    public UserInactiveException(String username) {
        super("User " + username + " is not active");
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.FORBIDDEN;
    }
}

package com.cfkiatong.springbootbankingapp.exception.business;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException() {
        super("User not found");
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }
}

package com.cfkiatong.springbootbankingapp.exception.business;

import org.springframework.http.HttpStatus;

public class UsernameUnavailableException extends BusinessException {
    public UsernameUnavailableException() {
        super("Username is already taken");
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.CONFLICT;
    }
}

package com.cfkiatong.springbootbankingapp.exception.errorbody;

import java.time.LocalDateTime;
import java.util.List;

public class ApiError {

    private final LocalDateTime timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final String path;
    private final List<FieldValidationError> errors;

    public ApiError(LocalDateTime timestamp, int status, String error, String message, String path, List<FieldValidationError> errors) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.errors = errors;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public List<FieldValidationError> getErrors() {
        return errors;
    }
}

package com.cfkiatong.springbootbankingapp.exception.errorbody;

public class FieldValidationError {

    private String field;
    private String message;
    private Object rejectedValue;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getRejectedValue() {
        return rejectedValue;
    }

    public void setRejectedValue(Object rejectedValue) {
        this.rejectedValue = rejectedValue;
    }
}

package com.cfkiatong.springbootbankingapp.exception;

import com.cfkiatong.springbootbankingapp.exception.business.BusinessException;
import com.cfkiatong.springbootbankingapp.exception.errorbody.ApiError;
import com.cfkiatong.springbootbankingapp.exception.errorbody.FieldValidationError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    private ApiError buildApiError(HttpStatus status, String message, String path, List<FieldValidationError> errors) {
        return new ApiError(LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleHttpMessageNotReadableException(HttpMessageNotReadableException httpMessageNotReadableException, HttpServletRequest request) {
        return ResponseEntity.badRequest().body(buildApiError(HttpStatus.BAD_REQUEST,
                httpMessageNotReadableException.getMessage(),
                request.getRequestURI(),
                null));
    }

    //Handles validation errors from @Valid or @Validated
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> newHandleValidationExceptions(MethodArgumentNotValidException validationExceptions, HttpServletRequest request) {
        List<FieldValidationError> validationErrors = new ArrayList<>();

        validationExceptions.getBindingResult().getAllErrors().forEach((error) -> {

            FieldValidationError validationError = new FieldValidationError();

            validationError.setField(((FieldError) error).getField());
            validationError.setMessage(error.getDefaultMessage());
            validationError.setRejectedValue(((FieldError) error).getRejectedValue());
            validationErrors.add(validationError);
        });

        return ResponseEntity.badRequest().body(buildApiError(HttpStatus.BAD_REQUEST,
                validationExceptions.getMessage(),
                request.getRequestURI(),
                validationErrors));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusinessException(BusinessException businessException, HttpServletRequest request) {
        Class<?> businessExceptionClass = businessException.getClass();

        HttpStatus status = businessException.getStatus();

        return ResponseEntity.status(status).body(buildApiError(status,
                businessException.getMessage(),
                request.getRequestURI(),
                null));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiError> handleUnauthorizedException(ForbiddenException forbiddenException, HttpServletRequest request) {
        Class<?> unauthorizedExceptionClass = forbiddenException.getClass();

        HttpStatus status = forbiddenException.getStatus();

        return ResponseEntity.status(status).body(buildApiError(status,
                forbiddenException.getMessage(),
                request.getRequestURI(),
                null));
    }

    //SPRINGBOOT EXCEPTIONS HANDLER
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentialsException(BadCredentialsException badCredentialsException, HttpServletRequest request) {
        Class<?> badCredentialsExceptionClass = badCredentialsException.getClass();

        HttpStatus status = HttpStatus.UNAUTHORIZED;

        String message = "Incorrect username or password.";

        return ResponseEntity.status(status).body(buildApiError(status,
                message,
                request.getRequestURI(),
                null));
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ApiError> handleLockedException(LockedException lockedException, HttpServletRequest request) {
        Class<?> lockedExceptionClass = lockedException.getClass();

        HttpStatus status = HttpStatus.UNAUTHORIZED;

        String message = "User account is locked, please contact an administrator or try again later.";

        return ResponseEntity.status(status).body(buildApiError(status,
                message,
                request.getRequestURI(),
                null));
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiError> handleDisabledException(DisabledException disabledException, HttpServletRequest request) {
        Class<?> disabledExceptionClass = disabledException.getClass();

        HttpStatus status = HttpStatus.FORBIDDEN;

        String message = "User account is disabled, please contact an administrator.";

        return ResponseEntity.status(status).body(buildApiError(status,
                message,
                request.getRequestURI(),
                null));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(AccessDeniedException accessDeniedException, HttpServletRequest request) {
        Class<?> accessDeniedExceptionClass = accessDeniedException.getClass();

        HttpStatus status = HttpStatus.FORBIDDEN;

        String message = "You are not authorized to perform this transaction";

        return ResponseEntity.status(status).body(buildApiError(status,
                message,
                request.getRequestURI(),
                null));
    }
}

// ================================================================================
// REPOSITORIES
// ================================================================================
package com.cfkiatong.springbootbankingapp.repository;

import com.cfkiatong.springbootbankingapp.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {

}

package com.cfkiatong.springbootbankingapp.repository;

import com.cfkiatong.springbootbankingapp.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findBySourceAccount(UUID uuid);

    List<Transaction> findByTargetAccount(UUID uuid);
}

package com.cfkiatong.springbootbankingapp.repository;

import com.cfkiatong.springbootbankingapp.entity.Account;
import com.cfkiatong.springbootbankingapp.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserEntityRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByUsername(String username);

    boolean existsByUsername(String username);
}

// ================================================================================
// SECURITY
// ================================================================================
package com.cfkiatong.springbootbankingapp.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        String message;
        if (authException instanceof DisabledException) {
            message = "User account is disabled.";
        } else if (authException instanceof LockedException) {
            message = "User account is locked.";
        } else {
            message = "Authentication failed.";
        }
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }

}

package com.cfkiatong.springbootbankingapp.security;

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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userEntityRepository.findByUsername(username)
                .map(UserPrincipal::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}

package com.cfkiatong.springbootbankingapp.security;

import com.cfkiatong.springbootbankingapp.dto.Role;
import com.cfkiatong.springbootbankingapp.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserPrincipal implements UserDetails {

    private final UUID userId;
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(UserEntity userEntity) {
        this.userId = userEntity.getUserId();
        this.username = userEntity.getUsername();
        this.password = userEntity.getPassword();
        this.authorities = getAuthorities(userEntity.getRoles());
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Set<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public UUID getUserId() {
        return userId;
    }
}

package com.cfkiatong.springbootbankingapp.security.jwt;

import com.cfkiatong.springbootbankingapp.security.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    public String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            Long expiration
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

package com.cfkiatong.springbootbankingapp.security.jwt;

import com.cfkiatong.springbootbankingapp.security.UserPrincipal;
import com.cfkiatong.springbootbankingapp.security.jwt.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        userEmail = jwtService.extractUsername(jwt);

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}

// ================================================================================
// SERVICES
// ================================================================================
package com.cfkiatong.springbootbankingapp.services;

import com.cfkiatong.springbootbankingapp.dto.request.ChangeAccountOwnerRequest;
import com.cfkiatong.springbootbankingapp.dto.response.AccountResponse;
import com.cfkiatong.springbootbankingapp.entity.Account;
import com.cfkiatong.springbootbankingapp.entity.UserEntity;
import com.cfkiatong.springbootbankingapp.exception.business.AccountNotFoundException;
import com.cfkiatong.springbootbankingapp.exception.business.UserNotFoundException;
import com.cfkiatong.springbootbankingapp.repository.AccountRepository;
import com.cfkiatong.springbootbankingapp.repository.UserEntityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserEntityRepository userEntityRepository;

    public AccountService(AccountRepository accountRepository, UserEntityRepository userEntityRepository) {
        this.accountRepository = accountRepository;
        this.userEntityRepository = userEntityRepository;
    }

    @Transactional
    public AccountResponse createAccount(UUID userId) {
        UserEntity userEntity = userEntityRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Account account = new Account(userEntity);
        accountRepository.save(account);

        return new AccountResponse(account.getId(), account.getAccountOwner().getUsername(), account.getBalance());
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccount(UUID userId, UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(AccountNotFoundException::new);

        if (!account.getAccountOwner().getUserId().equals(userId)) {
            throw new AccountNotFoundException();
        }

        return new AccountResponse(account.getId(), account.getAccountOwner().getUsername(), account.getBalance());
    }

    @Transactional
    public AccountResponse changeAccountOwner(UUID userId, UUID accountId, ChangeAccountOwnerRequest changeAccountOwnerRequest) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(AccountNotFoundException::new);

        if (!account.getAccountOwner().getUserId().equals(userId)) {
            throw new AccountNotFoundException();
        }

        UserEntity newOwner = userEntityRepository.findByUsername(changeAccountOwnerRequest.getNewAccountOwner())
                .orElseThrow(UserNotFoundException::new);

        account.setAccountOwner(newOwner);
        accountRepository.save(account);

        return new AccountResponse(account.getId(), account.getAccountOwner().getUsername(), account.getBalance());
    }

    @Transactional
    public void deleteAccount(UUID userId, UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(AccountNotFoundException::new);

        if (!account.getAccountOwner().getUserId().equals(userId)) {
            throw new AccountNotFoundException();
        }

        accountRepository.delete(account);
    }
}

package com.cfkiatong.springbootbankingapp.services;

import com.cfkiatong.springbootbankingapp.dto.*;
import com.cfkiatong.springbootbankingapp.dto.request.UpdateRolesRequest;
import com.cfkiatong.springbootbankingapp.dto.request.UpdateUserRequest;
import com.cfkiatong.springbootbankingapp.dto.request.UpdateUserStatusRequest;
import com.cfkiatong.springbootbankingapp.dto.response.AccountResponse;
import com.cfkiatong.springbootbankingapp.dto.response.TransactionResponse;
import com.cfkiatong.springbootbankingapp.dto.response.UserResponse;
import com.cfkiatong.springbootbankingapp.dto.response.UserRolesResponse;
import com.cfkiatong.springbootbankingapp.entity.Account;
import com.cfkiatong.springbootbankingapp.entity.UserEntity;
import com.cfkiatong.springbootbankingapp.exception.business.*;
import com.cfkiatong.springbootbankingapp.repository.AccountRepository;
import com.cfkiatong.springbootbankingapp.repository.TransactionRepository;
import com.cfkiatong.springbootbankingapp.repository.UserEntityRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AdminService {

    private final UserEntityRepository userEntityRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(UserEntityRepository userEntityRepository, AccountRepository accountRepository, TransactionRepository transactionRepository, PasswordEncoder passwordEncoder) {
        this.userEntityRepository = userEntityRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userEntityRepository.findAll().stream()
                .map(user -> new UserResponse(
                        user.getUsername(),
                        user.getEmail(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getAccounts() != null ? user.getAccounts().stream().map(Account::getId).toList() : List.of()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(account -> new AccountResponse(account.getId(), account.getAccountOwner().getUsername(), account.getBalance()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(transaction -> new TransactionResponse(
                        transaction.getTimestamp(),
                        transaction.getTransactionId(),
                        transaction.getType(),
                        transaction.getSourceAccount(),
                        transaction.getTargetAccount(),
                        transaction.getTransactionAmount(),
                        transaction.getSourceBalanceBefore(),
                        transaction.getSourceBalanceAfter()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(String username) {
        UserEntity userEntity = userEntityRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        return new UserResponse(
                userEntity.getUsername(),
                userEntity.getEmail(),
                userEntity.getFirstName(),
                userEntity.getLastName(),
                userEntity.getAccounts() != null ? userEntity.getAccounts().stream().map(Account::getId).toList() : List.of()
        );
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccount(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(AccountNotFoundException::new);

        return new AccountResponse(account.getId(), account.getAccountOwner().getUsername(), account.getBalance());
    }

    @Transactional(readOnly = true)
    public TransactionResponse getTransaction(UUID transactionId) {
        var transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new BusinessException("Transaction not found") {
                    @Override
                    public org.springframework.http.HttpStatus getStatus() {
                        return org.springframework.http.HttpStatus.NOT_FOUND;
                    }
                });

        return new TransactionResponse(
                transaction.getTimestamp(),
                transaction.getTransactionId(),
                transaction.getType(),
                transaction.getSourceAccount(),
                transaction.getTargetAccount(),
                transaction.getTransactionAmount(),
                transaction.getSourceBalanceBefore(),
                transaction.getSourceBalanceAfter()
        );
    }

    @Transactional
    public UserResponse updateUser(String username, UpdateUserRequest updateUserRequest) {
        UserEntity userEntity = userEntityRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        if (updateUserRequest.getNewUsername() != null) {
            if (userEntityRepository.existsByUsername(updateUserRequest.getNewUsername())) {
                throw new UsernameUnavailableException();
            }
            userEntity.setUsername(updateUserRequest.getNewUsername());
        }

        if (updateUserRequest.getNewPassword() != null) {
            userEntity.setPassword(passwordEncoder.encode(updateUserRequest.getNewPassword()));
        }

        if (updateUserRequest.getNewEmail() != null) {
            userEntity.setEmail(updateUserRequest.getNewEmail());
        }

        if (updateUserRequest.getNewFirstName() != null) {
            userEntity.setFirstName(updateUserRequest.getNewFirstName());
        }

        if (updateUserRequest.getNewLastName() != null) {
            userEntity.setLastName(updateUserRequest.getNewLastName());
        }

        userEntityRepository.save(userEntity);

        return new UserResponse(
                userEntity.getUsername(),
                userEntity.getEmail(),
                userEntity.getFirstName(),
                userEntity.getLastName(),
                userEntity.getAccounts() != null ? userEntity.getAccounts().stream().map(Account::getId).toList() : List.of()
        );
    }

    @Transactional
    public UserResponse updateUserStatus(String username, UpdateUserStatusRequest updateUserStatusRequest) {
        UserEntity userEntity = userEntityRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        switch (updateUserStatusRequest.newUserStatus()) {
            case ACTIVE -> userEntity.activateUser();
            case DISABLED -> userEntity.disableUser();
            case LOCKED -> userEntity.lockUser();
            case DELETED -> userEntity.deleteUser();
        }

        userEntityRepository.save(userEntity);

        return new UserResponse(
                userEntity.getUsername(),
                userEntity.getEmail(),
                userEntity.getFirstName(),
                userEntity.getLastName(),
                userEntity.getAccounts() != null ? userEntity.getAccounts().stream().map(Account::getId).toList() : List.of()
        );
    }

    @Transactional
    public void deleteUser(String username) {
        UserEntity userEntity = userEntityRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        userEntityRepository.delete(userEntity);
    }

    @Transactional
    public UserRolesResponse updateUserRoles(String username, UpdateRolesRequest updateRolesRequest) {
        UserEntity userEntity = userEntityRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        switch (updateRolesRequest.action().toLowerCase()) {
            case "add" -> {
                userEntity.getRoles().addAll(updateRolesRequest.roles());
            }
            case "remove" -> {
                userEntity.getRoles().removeAll(updateRolesRequest.roles());
            }
            default -> throw new UpdateUserRoleException();
        }

        userEntityRepository.save(userEntity);

        return new UserRolesResponse(userEntity.getUsername(), userEntity.getRoles());
    }
}

package com.cfkiatong.springbootbankingapp.services;

import com.cfkiatong.springbootbankingapp.dto.request.AuthenticationRequest;
import com.cfkiatong.springbootbankingapp.dto.response.AuthenticationResponse;
import com.cfkiatong.springbootbankingapp.security.UserPrincipal;
import com.cfkiatong.springbootbankingapp.security.jwt.JwtService;
import com.cfkiatong.springbootbankingapp.services.LoginAttemptService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final LoginAttemptService loginAttemptService;

    public AuthenticationService(AuthenticationManager authenticationManager, JwtService jwtService, LoginAttemptService loginAttemptService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.loginAttemptService = loginAttemptService;
    }

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getUsername(),
                            authenticationRequest.getPassword()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwtToken = jwtService.generateToken(userDetails);

            loginAttemptService.loginSucceeded(authenticationRequest.getUsername());

            AuthenticationResponse response = new AuthenticationResponse();
            response.setJwtToken(jwtToken);
            return response;

        } catch (AuthenticationException e) {
            loginAttemptService.loginFailed(authenticationRequest.getUsername());
            throw new BadCredentialsException("Invalid credentials");
        }
    }
}

package com.cfkiatong.springbootbankingapp.services;

import com.cfkiatong.springbootbankingapp.repository.UserEntityRepository;
import com.cfkiatong.springbootbankingapp.entity.UserEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LoginAttemptService {

    private final UserEntityRepository userEntityRepository;
    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_TIME_DURATION = 24 * 60 * 60 * 1000; // 24 hours

    public LoginAttemptService(UserEntityRepository userEntityRepository) {
        this.userEntityRepository = userEntityRepository;
    }

    public void loginFailed(String username) {
        UserEntity user = userEntityRepository.findByUsername(username).orElse(null);
        if (user != null) {
            int attempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(attempts);

            if (attempts >= MAX_ATTEMPTS) {
                user.lockUser();
                user.setUnlocksAt(LocalDateTime.now().plusSeconds(LOCK_TIME_DURATION / 1000));
            }

            userEntityRepository.save(user);
        }
    }

    public void loginSucceeded(String username) {
        UserEntity user = userEntityRepository.findByUsername(username).orElse(null);
        if (user != null) {
            user.setFailedLoginAttempts(0);
            user.setUnlocksAt(null);
            if (user.getUserStatus() == UserStatus.LOCKED && user.getUnlocksAt() != null && user.getUnlocksAt().isBefore(LocalDateTime.now())) {
                user.activateUser();
            }
            userEntityRepository.save(user);
        }
    }
}

package com.cfkiatong.springbootbankingapp.services;

import com.cfkiatong.springbootbankingapp.dto.*;
import com.cfkiatong.springbootbankingapp.dto.request.TransactionRequest;
import com.cfkiatong.springbootbankingapp.dto.response.BalanceResponse;
import com.cfkiatong.springbootbankingapp.dto.response.TransactionResponse;
import com.cfkiatong.springbootbankingapp.entity.Account;
import com.cfkiatong.springbootbankingapp.entity.Transaction;
import com.cfkiatong.springbootbankingapp.exception.business.*;
import com.cfkiatong.springbootbankingapp.repository.AccountRepository;
import com.cfkiatong.springbootbankingapp.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional(readOnly = true)
    public BalanceResponse getBalance(UUID userId, UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(AccountNotFoundException::new);

        if (!account.getAccountOwner().getUserId().equals(userId)) {
            throw new AccountNotFoundException();
        }

        return new BalanceResponse(account.getBalance());
    }

    @Transactional
    public BalanceResponse makeTransaction(UUID userId, UUID accountId, TransactionRequest transactionRequest) {
        Account sourceAccount = accountRepository.findById(accountId)
                .orElseThrow(AccountNotFoundException::new);

        if (!sourceAccount.getAccountOwner().getUserId().equals(userId)) {
            throw new AccountNotFoundException();
        }

        BigDecimal sourceBalanceBefore = sourceAccount.getBalance();
        BigDecimal transactionAmount = transactionRequest.getAmount();

        switch (transactionRequest.getType()) {
            case WITHDRAWAL -> {
                if (sourceAccount.getBalance().compareTo(transactionAmount) < 0) {
                    throw new InsufficientBalanceException();
                }
                sourceAccount.setBalance(sourceAccount.getBalance().subtract(transactionAmount));
            }
            case DEPOSIT -> {
                sourceAccount.setBalance(sourceAccount.getBalance().add(transactionAmount));
            }
            case TRANSFER -> {
                if (sourceAccount.getBalance().compareTo(transactionAmount) < 0) {
                    throw new InsufficientBalanceException();
                }

                Account targetAccount = accountRepository.findById(transactionRequest.getTargetAccountId())
                        .orElseThrow(AccountNotFoundException::new);

                sourceAccount.setBalance(sourceAccount.getBalance().subtract(transactionAmount));
                targetAccount.setBalance(targetAccount.getBalance().add(transactionAmount));

                accountRepository.save(targetAccount);
            }
        }

        accountRepository.save(sourceAccount);

        Transaction transaction = new Transaction(
                LocalDateTime.now(),
                transactionRequest.getType(),
                accountId,
                transactionRequest.getTargetAccountId(),
                transactionAmount,
                sourceBalanceBefore,
                sourceAccount.getBalance(),
                null,
                null
        );

        transactionRepository.save(transaction);

        return new BalanceResponse(sourceAccount.getBalance());
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getUserTransactions(UUID userId) {
        List<Transaction> transactions = transactionRepository.findBySourceAccount(userId);
        return transactions.stream()
                .map(transaction -> new TransactionResponse(
                        transaction.getTimestamp(),
                        transaction.getTransactionId(),
                        transaction.getType(),
                        transaction.getSourceAccount(),
                        transaction.getTargetAccount(),
                        transaction.getTransactionAmount(),
                        transaction.getSourceBalanceBefore(),
                        transaction.getSourceBalanceAfter()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getAccountTransactions(UUID userId, UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(AccountNotFoundException::new);

        if (!account.getAccountOwner().getUserId().equals(userId)) {
            throw new AccountNotFoundException();
        }

        List<Transaction> transactions = transactionRepository.findBySourceAccount(accountId);
        return transactions.stream()
                .map(transaction -> new TransactionResponse(
                        transaction.getTimestamp(),
                        transaction.getTransactionId(),
                        transaction.getType(),
                        transaction.getSourceAccount(),
                        transaction.getTargetAccount(),
                        transaction.getTransactionAmount(),
                        transaction.getSourceBalanceBefore(),
                        transaction.getSourceBalanceAfter()
                ))
                .toList();
    }
}

package com.cfkiatong.springbootbankingapp.services;

import com.cfkiatong.springbootbankingapp.dto.Role;
import com.cfkiatong.springbootbankingapp.dto.request.CreateUserRequest;
import com.cfkiatong.springbootbankingapp.dto.request.UpdateUserRequest;
import com.cfkiatong.springbootbankingapp.dto.response.UserResponse;
import com.cfkiatong.springbootbankingapp.dto.UserStatus;
import com.cfkiatong.springbootbankingapp.entity.UserEntity;
import com.cfkiatong.springbootbankingapp.exception.business.*;
import com.cfkiatong.springbootbankingapp.repository.UserEntityRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserEntityService {

    private final UserEntityRepository userEntityRepository;
    private final PasswordEncoder passwordEncoder;

    public UserEntityService(UserEntityRepository userEntityRepository, PasswordEncoder passwordEncoder) {
        this.userEntityRepository = userEntityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest createUserRequest) {
        if (userEntityRepository.existsByUsername(createUserRequest.getUsername())) {
            throw new UsernameUnavailableException();
        }

        UserEntity userEntity = new UserEntity(
                createUserRequest.getFirstName(),
                createUserRequest.getLastName(),
                createUserRequest.getEmail(),
                createUserRequest.getUsername(),
                passwordEncoder.encode(createUserRequest.getPassword()),
                createUserRequest.getRoles(),
                List.of()
        );

        userEntityRepository.save(userEntity);

        return new UserResponse(
                userEntity.getUsername(),
                userEntity.getEmail(),
                userEntity.getFirstName(),
                userEntity.getLastName(),
                userEntity.getAccounts() != null ? userEntity.getAccounts().stream().map(account -> account.getId()).toList() : List.of()
        );
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(UUID userId) {
        UserEntity userEntity = userEntityRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        return new UserResponse(
                userEntity.getUsername(),
                userEntity.getEmail(),
                userEntity.getFirstName(),
                userEntity.getLastName(),
                userEntity.getAccounts() != null ? userEntity.getAccounts().stream().map(account -> account.getId()).toList() : List.of()
        );
    }

    @Transactional
    public UserResponse updateUser(UUID userId, UpdateUserRequest updateUserRequest) {
        UserEntity userEntity = userEntityRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        if (updateUserRequest.getNewUsername() != null) {
            if (userEntityRepository.existsByUsername(updateUserRequest.getNewUsername())) {
                throw new UsernameUnavailableException();
            }
            userEntity.setUsername(updateUserRequest.getNewUsername());
        }

        if (updateUserRequest.getNewPassword() != null) {
            userEntity.setPassword(passwordEncoder.encode(updateUserRequest.getNewPassword()));
        }

        if (updateUserRequest.getNewEmail() != null) {
            userEntity.setEmail(updateUserRequest.getNewEmail());
        }

        if (updateUserRequest.getNewFirstName() != null) {
            userEntity.setFirstName(updateUserRequest.getNewFirstName());
        }

        if (updateUserRequest.getNewLastName() != null) {
            userEntity.setLastName(updateUserRequest.getNewLastName());
        }

        userEntityRepository.save(userEntity);

        return new UserResponse(
                userEntity.getUsername(),
                userEntity.getEmail(),
                userEntity.getFirstName(),
                userEntity.getLastName(),
                userEntity.getAccounts() != null ? userEntity.getAccounts().stream().map(account -> account.getId()).toList() : List.of()
        );
    }

    @Transactional
    public void deleteUser(UUID userId) {
        UserEntity userEntity = userEntityRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        userEntityRepository.delete(userEntity);
    }
}

// ================================================================================
// END OF CONSOLIDATED FILE
// ================================================================================
// Total files consolidated: 32
// File includes: Main application, Configuration, Controllers (5), DTOs (19), 
// Entities (3), Exceptions (15), Repositories (3), Security (5), Services (6)
// Generated on: 2025-06-17
