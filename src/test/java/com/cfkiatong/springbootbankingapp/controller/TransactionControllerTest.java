package com.cfkiatong.springbootbankingapp.controller;

import com.cfkiatong.springbootbankingapp.dto.response.BalanceResponse;
import com.cfkiatong.springbootbankingapp.services.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private TransactionService transactionService;

    private final UUID accountId = UUID.randomUUID();

    @Test
    @WithMockUser(username = "48f90b11-9656-4cfb-ace4-107a2da90970")
        // ✅ Cleaner than .with(user(...))
    void getBalance_validRequest_returnsBalance() throws Exception {
        BalanceResponse response = new BalanceResponse(new BigDecimal("1000.00"));

        // ✅ Use any() because the controller reconstructs the UUID internally
        when(transactionService.getBalance(any(UUID.class), any(UUID.class)))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/users/me/accounts/{accountId}/transactions", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(1000.00)); // ✅ Correct jsonPath import

        verify(transactionService).getBalance(any(UUID.class), any(UUID.class));
    }
}