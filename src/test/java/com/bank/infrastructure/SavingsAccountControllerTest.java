package com.bank.infrastructure;

import com.bank.infrastructure.rest.SavingsAccountResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SavingsAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createSavingsAccountReturns201WithDepositCap() throws Exception {
        mockMvc.perform(post("/savings-accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("depositCap", 22950.00))))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.accountNumber").isNotEmpty())
            .andExpect(jsonPath("$.balance").value(0))
            .andExpect(jsonPath("$.depositCap").value(22950.0));
    }

    @Test
    void depositWithinCapReturnsUpdatedBalance() throws Exception {
        String accountNumber = createSavingsAccount(new BigDecimal("22950.00"));

        mockMvc.perform(post("/savings-accounts/{number}/deposit", accountNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("amount", 1000.00))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.balance").value(1000.0));
    }

    @Test
    void depositExceedingCapReturns422() throws Exception {
        String accountNumber = createSavingsAccount(new BigDecimal("1000.00"));

        mockMvc.perform(post("/savings-accounts/{number}/deposit", accountNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("amount", 1500.00))))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    void withdrawalReturnsUpdatedBalance() throws Exception {
        String accountNumber = createSavingsAccount(new BigDecimal("22950.00"));
        deposit(accountNumber, new BigDecimal("500.00"));

        mockMvc.perform(post("/savings-accounts/{number}/withdrawal", accountNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("amount", 200.00))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.balance").value(300.0));
    }

    @Test
    void overdraftNotAllowedOnSavingsAccountReturns422() throws Exception {
        String accountNumber = createSavingsAccount(new BigDecimal("22950.00"));

        mockMvc.perform(post("/savings-accounts/{number}/withdrawal", accountNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("amount", 100.00))))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.error").isNotEmpty());
    }

    private String createSavingsAccount(BigDecimal depositCap) throws Exception {
        MvcResult result = mockMvc.perform(post("/savings-accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("depositCap", depositCap))))
            .andExpect(status().isCreated())
            .andReturn();
        SavingsAccountResponse response = objectMapper.readValue(
            result.getResponse().getContentAsString(), SavingsAccountResponse.class);
        return response.getAccountNumber();
    }

    private void deposit(String accountNumber, BigDecimal amount) throws Exception {
        mockMvc.perform(post("/savings-accounts/{number}/deposit", accountNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("amount", amount))))
            .andExpect(status().isOk());
    }
}
