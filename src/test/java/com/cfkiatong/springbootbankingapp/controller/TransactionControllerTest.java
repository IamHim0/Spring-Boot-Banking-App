package com.cfkiatong.springbootbankingapp.controller;

import com.cfkiatong.springbootbankingapp.config.SecurityConfiguration;
import com.cfkiatong.springbootbankingapp.dto.TransactionType;
import com.cfkiatong.springbootbankingapp.dto.request.TransactionRequest;
import com.cfkiatong.springbootbankingapp.dto.response.BalanceResponse;
import com.cfkiatong.springbootbankingapp.dto.response.TransactionResponse;
import com.cfkiatong.springbootbankingapp.security.CustomAuthenticationEntryPoint;
import com.cfkiatong.springbootbankingapp.security.jwt.JwtFilter;
import com.cfkiatong.springbootbankingapp.security.jwt.JwtService;
import com.cfkiatong.springbootbankingapp.services.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = TransactionController.class
//        , excludeAutoConfiguration = {
//                SecurityAutoConfiguration.class,
//                SecurityFilterAutoConfiguration.class
//        }
)
@AutoConfigureMockMvc(addFilters = false)
@Import({
        SecurityConfiguration.class,
//        JwtFilter.class,
        com.fasterxml.jackson.databind.ObjectMapper.class
})
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TransactionService transactionService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private JwtService jwtService; // Required because JwtFilter is imported

    @MockitoBean
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    private final String stringUserId = "11111111-1111-1111-1111-111111111111";
    private final UUID userId = UUID.fromString(stringUserId);
    private final UUID accountId = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private final UUID targetAccountId = UUID.fromString("33333333-3333-3333-3333-333333333333");

    @Test
    @WithMockUser(username = "11111111-1111-1111-1111-111111111111")
    void getBalance_ValidUser_ReturnsBalance() throws Exception {
        when(transactionService.getBalance(userId, accountId))
                .thenReturn(new BalanceResponse(new BigDecimal("1500.00")));

        mockMvc.perform(get("/api/v1/users/me/accounts/{accountId}/transactions/balance", accountId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.balance").value(1500));
    }

    @Test
    @WithMockUser(username = "11111111-1111-1111-1111-111111111111")
    void makeDepositTransaction_ValidTransaction_ReturnBalance() throws Exception {
        TransactionRequest transactionRequest = new TransactionRequest(
                TransactionType.DEPOSIT,
                new BigDecimal("100.00"),
                null
        );

        when(transactionService.makeTransaction(userId, accountId, transactionRequest))
                .thenReturn(new BalanceResponse(new BigDecimal("1600.00")));

        mockMvc.perform(post("/api/v1/users/me/accounts/{accountId}/transactions", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.balance").value(1600.0));
    }

    @Test
    @WithMockUser(username = "11111111-1111-1111-1111-111111111111")
    void makeWithdrawalTransaction_ValidTransaction_ReturnBalance() throws Exception {
        TransactionRequest transactionRequest = new TransactionRequest(
                TransactionType.WITHDRAWAL,
                new BigDecimal("100.00"),
                null
        );

        when(transactionService.makeTransaction(userId, accountId, transactionRequest))
                .thenReturn(new BalanceResponse(new BigDecimal("1400.00")));

        mockMvc.perform(post("/api/v1/users/me/accounts/{accountId}/transactions", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.balance").value(1400));
    }

    @Test
    @WithMockUser(username = "11111111-1111-1111-1111-111111111111")
    void makeTransferTransaction_ValidTransaction_ReturnBalance() throws Exception {
        TransactionRequest transactionRequest = new TransactionRequest(
                TransactionType.TRANSFER,
                new BigDecimal("100.00"),
                targetAccountId
        );

        when(transactionService.makeTransaction(userId, accountId, transactionRequest))
                .thenReturn(new BalanceResponse(new BigDecimal("1400.00")));

        mockMvc.perform(post("/api/v1/users/me/accounts/{accountId}/transactions", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.balance").value(1400.0));
    }

    @Test
    @WithMockUser(username = "11111111-1111-1111-1111-111111111111")
    void getUserTransactions_ValidUser_ReturnsTransactionList() throws Exception {
        TransactionResponse transactionResponse = new TransactionResponse(
                LocalDateTime.now(),
                UUID.randomUUID(),
                TransactionType.DEPOSIT,
                accountId,
                null,
                new BigDecimal("100.00"),
                new BigDecimal("0.00"),
                new BigDecimal("100.00")
        );

        List<TransactionResponse> mockTransactions = List.of(transactionResponse);

        when(transactionService.getUserTransactions(userId))
                .thenReturn(mockTransactions);

        mockMvc.perform(get("/api/v1/users/me/accounts/{accountId}/transactions/userhistory", accountId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(username = "11111111-1111-1111-1111-111111111111")
    void getAccountTransactions_ValidUser_ReturnsTransactionList() throws Exception {
        TransactionResponse transactionResponse = new TransactionResponse(
                LocalDateTime.now(),
                UUID.randomUUID(),
                TransactionType.DEPOSIT,
                accountId,
                null,
                new BigDecimal("100.00"),
                new BigDecimal("0.00"),
                new BigDecimal("100.00")
        );

        List<TransactionResponse> mockTransactions = List.of(transactionResponse);

        when(transactionService.getAccountTransactions(userId, accountId)).thenReturn(mockTransactions);

        mockMvc.perform(get("/api/v1/users/me/accounts/{accountId}/transactions/accounthistory", accountId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

}