package com.cfkiatong.springbootbankingapp.controller;

import com.cfkiatong.springbootbankingapp.dto.response.AccountResponse;
import com.cfkiatong.springbootbankingapp.services.AccountService;
import com.cfkiatong.springbootbankingapp.services.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.math.BigDecimal;
import java.net.URI;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.RequestEntity.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.client.ExpectedCount.times;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = AccountController.class
//        excludeAutoConfiguration = {
//                SecurityAutoConfiguration.class,
//                SecurityFilterAutoConfiguration.class
//        }
)
@Import({
        SecurityAutoConfiguration.class,
        com.fasterxml.jackson.databind.ObjectMapper.class
})
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;

    private final UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private final String owner = "owner";
    private final UUID accountId = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private final BigDecimal balance = BigDecimal.valueOf(10000);
    private AccountResponse accountResponse;

    @BeforeEach
    void setUp() {
        accountResponse = new AccountResponse(
                accountId,
                owner,
                balance
        );
    }

    @Test
    @DisplayName("POST /accounts — unauthenticated user — returns 401")
    @WithMockUser(username = "11111111-1111-1111-1111-111111111111")
    void createAccount_validUser_createsAccount() throws Exception {
        AccountResponse accountResponse = new AccountResponse(
                accountId,
                owner,
                balance
        );)

        when(accountService.createAccount(userId)).thenReturn(accountResponse);
    }

}
