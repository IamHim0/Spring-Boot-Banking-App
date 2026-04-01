package com.cfkiatong.springbootbankingapp.controller;

import com.cfkiatong.springbootbankingapp.config.SecurityConfiguration;
import com.cfkiatong.springbootbankingapp.security.CustomAuthenticationEntryPoint;
import com.cfkiatong.springbootbankingapp.security.jwt.JwtFilter;
import com.cfkiatong.springbootbankingapp.security.jwt.JwtService;
import com.cfkiatong.springbootbankingapp.services.TransactionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

@WebMvcTest(controllers = TransactionController.class)
@Import({
        SecurityConfiguration.class,
        JwtFilter.class,
        JwtService.class
})
public class TestTransactionController2 {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtService jwtService;

    @MockitoBean
    private TransactionService transactionService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    private final UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private final UUID accountId = UUID.fromString("22222222-2222-2222-2222-222222222222");

    @Test
    @DisplayName("Get balance")
    void returnsBalance_whenAccountBelongsToUser() throws Exception {
        String token = jwtService.generateToken(userId.toString(), List.of("USER"));

        mockMvc.perform(get("/api/v1/users/me/accounts/{accountId}/transactions/balance", accountId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
