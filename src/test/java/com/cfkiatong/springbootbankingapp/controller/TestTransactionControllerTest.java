package com.cfkiatong.springbootbankingapp.controller;

import com.cfkiatong.springbootbankingapp.config.SecurityConfiguration;
import com.cfkiatong.springbootbankingapp.config.TestSecurityConfig;
import com.cfkiatong.springbootbankingapp.dto.response.BalanceResponse;
import com.cfkiatong.springbootbankingapp.security.CustomAuthenticationEntryPoint;
import com.cfkiatong.springbootbankingapp.security.jwt.JwtFilter;
import com.cfkiatong.springbootbankingapp.security.jwt.JwtService;
import com.cfkiatong.springbootbankingapp.services.TransactionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@WebMvcTest(controllers = TransactionController.class)
//@Import({
//        SecurityConfiguration.class,
//        JwtFilter.class,
//        JwtService.class
//})
//class TestTransactionControllerTest {
//
//    private static final String USER_ID = "11111111-1111-1111-1111-111111111111";
//    private static final String ACCOUNT_ID = "22222222-2222-2222-2222-222222222222";
//    private static final String BASE_URL = "api/v1/users/me/accounts/{accountId}/transactions";
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockitoBean
//    private TransactionService transactionService;
//
//    @MockitoBean
//    private UserDetailsService userDetailsService;
//
//    @MockitoBean
//    private com.cfkiatong.springbootbankingapp.security.CustomAuthenticationEntryPoint customAuthenticationEntryPoint; // ← ADD THIS
//
//
//    @Test
//    @DisplayName("Get balance")
//    @WithMockUser(username = USER_ID)
//    void returnsBalance_whenAccountBelongsToUser() throws Exception {
//        BalanceResponse expectedResponse = new BalanceResponse(new BigDecimal("1500.00"));
//
//        when(transactionService.getBalance(
//                UUID.fromString(USER_ID),
//                UUID.fromString(ACCOUNT_ID)
//        )).thenReturn(expectedResponse);
//
//        mockMvc.perform(
//                        get(BASE_URL, ACCOUNT_ID)
//                                .accept(MediaType.APPLICATION_JSON)
//                )
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.balance").value("1500.00"));
//    }
//}

@WebMvcTest(controllers = TransactionController.class)
@Import({
        SecurityConfiguration.class,
        JwtFilter.class
})
class TestTransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private JwtService jwtService; // Required because JwtFilter is imported

    @MockitoBean
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Test
    @WithMockUser(username = "11111111-1111-1111-1111-111111111111")
    void returnsBalance_whenAccountBelongsToUser() throws Exception {
        // Mocking behavior
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID accountId = UUID.fromString("22222222-2222-2222-2222-222222222222");

        when(transactionService.getBalance(userId, accountId))
                .thenReturn(new BalanceResponse(new BigDecimal("1500.00")));

        mockMvc.perform(get("/api/v1/users/me/accounts/{accountId}/transactions/balance", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value("1500.00"));
    }
}